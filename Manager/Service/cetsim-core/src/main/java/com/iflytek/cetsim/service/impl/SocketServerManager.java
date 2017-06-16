package com.iflytek.cetsim.service.impl;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.utils.Dom4jUtils;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.dao.TrainRoomMapper;
import com.iflytek.cetsim.dto.TrainTaskRoom;
import com.iflytek.cetsim.dto.TrainTaskRoomMessage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/11.
 */
@Service
@Transactional
public class SocketServerManager
{
    public static SocketIOServer serverSocket = null;

    @Autowired
    private TrainRoomMapper trainRoomMapper;

    private boolean isClear = false;


    @PostConstruct
    public void initServerSocket()
    {
        //取tomcat默认端口号减1
        Integer serverSocketPort = 8079;

        String userDir = System.getProperty("user.dir");
        LogUtil.info("--> user.dir:" + userDir);

        String tomcatConfigPath = Paths.get(userDir.replace("bin", "conf"), "server.xml").toString();

        LogUtil.info("--> tomcat.conf.dir:" + tomcatConfigPath);

        Document doc = Dom4jUtils.getXMLByFilePath(tomcatConfigPath);
        if (doc != null)
        {
            List<Element> paperEls = Dom4jUtils.getChildElements(doc.getRootElement());

            Element rootElement = doc.getRootElement();

            if(rootElement != null)
            {
                Element serviceElement = rootElement.element("Service");

                if(serviceElement != null)
                {
                    List<Element> connectorElementList = Dom4jUtils.getChildElements(serviceElement);

                    if(connectorElementList != null)
                    {
                        for(Element element : connectorElementList)
                        {
                            String protocol = element.attribute("protocol").getValue();

                            if("HTTP/1.1".equals(protocol))
                            {
                                //获取tomcat的端口号
                                String port = element.attribute("port") == null ? "8080" : element.attribute("port").getValue();

                                serverSocketPort = Integer.valueOf(port) - 1;

                                break;
                            }
                        }
                    }
                }
            }
        }

        Configuration config = new Configuration();
        //config.setHostname(geServerIP);
        config.setPort(serverSocketPort);

        serverSocket = new SocketIOServer(config);

        serverSocket.start();

        monitorClientSocket();

        //清理所有未组队或者组队成功的房间(目前没有恢复机制）
        if(!isClear)
        {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("unGroup", (short)0);
            paramMap.put("grouped", (short)1);

            trainRoomMapper.deleteExitTrainTaskRoomByState(paramMap);

            isClear = true;
        }
    }

    /**
     * 监听接口
     */
    public void monitorClientSocket()
    {
        serverSocket.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient)
            {
                String clientSessionID = socketIOClient.getSessionId().toString();

                System.out.println("===============connect==================clientSessionID:" + clientSessionID);


            }
        });

        //监听客户端断开连接的事件，通知A\B，退出房间
        serverSocket.addDisconnectListener(new DisconnectListener()
        {
            public void onDisconnect(SocketIOClient socketIOClient)
            {
                String clientSessionID = socketIOClient.getSessionId().toString();

                synchronized (Constants.trainTaskRoomMap)
                {
                    //理论上，一个客户端退出连接，如果已经创建房间或者加入房间，都应该清除缓存记录
                    if(Constants.trainTaskRoomMap.containsKey(clientSessionID))
                    {
                        TrainTaskRoom trainTaskRoom = (TrainTaskRoom)Constants.trainTaskRoomMap.get(clientSessionID);

                        System.out.println("===============Disconnect :房间[" + trainTaskRoom.getRoomCode() + "]已经断开连接，IP：" + socketIOClient.getRemoteAddress());


                        LogUtil.info("--> 房间[" + trainTaskRoom.getRoomCode() + "]已经断开连接，IP：" + socketIOClient.getRemoteAddress());

                        if(trainTaskRoom != null)
                        {
                            LogUtil.info("--> 客户端断网，房间号：" + trainTaskRoom.getRoomCode() + "; A:" + trainTaskRoom.getCandidateACode() + "; B:" + trainTaskRoom.getCandidateBCode());

                            TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
                            trainTaskRoomMessage.setRoomCode(trainTaskRoom.getRoomCode());

                            if(Constants.ROLE_A.equals(trainTaskRoom.getRoomRole()))
                            {
                                //如果是A，则删除房间记录
                                int delResult = trainRoomMapper.deleteExitTrainTaskRoom(trainTaskRoom.getRoomCode());

                                if(delResult > 0)
                                {
                                    LogUtil.info("--> 客户端为A角色，账号为：" + trainTaskRoom.getCandidateACode());
                                    //如果是A角色退出房间，则发送房间撤销事情
                                    sendMessageToClient(Constants.CANDIDATE_A_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);
                                }
                            }
                            else
                            {
                                //如果是B角色退出房间，则发送队友退出房间事件
                                sendMessageToClient(Constants.CANDIDATE_B_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);
                            }

                            //删除缓存的房间记录
                            Constants.trainTaskRoomMap.remove(clientSessionID);
                        }
                    }
                }
            }
        });

        //监听A创建房间成功事情通知，缓存房间信息，如果断网时通知
        serverSocket.addEventListener(Constants.CANDIDATE_A_CREATE_TASK_ROOM_REQUEST_EVENT, TrainTaskRoomMessage.class, (client, data, ackRequest) ->
        {
            String clientSessionID = client.getSessionId().toString();

            synchronized (Constants.trainTaskRoomMap)
            {
                //理论上应该不存在缓存记录的
                if(!Constants.trainTaskRoomMap.containsKey(clientSessionID))
                {
                    TrainTaskRoom trainTaskRoom = new TrainTaskRoom();
                    trainTaskRoom.setCandidateACode(data.getCandidateACode());
                    trainTaskRoom.setRoomCode(data.getRoomCode());
                    trainTaskRoom.setSocketSessionID(clientSessionID);
                    trainTaskRoom.setRoomRole(Constants.ROLE_A);

                    LogUtil.info("--> 新创建房间，房间号：" + data.getCandidateACode() + "; A:" + data.getCandidateACode());

                    Constants.trainTaskRoomMap.put(clientSessionID, trainTaskRoom);
                }
                else
                {
                    LogUtil.info("--> [tip]该客户端已经创建过房间，房间号：" + data.getRoomCode());
                }
            }
        });

        //监听B加入房间成功事情通知，缓存房间信息，如果断网时通知
        serverSocket.addEventListener(Constants.CANDIDATE_B_JION_TASK_ROOM_REQUEST_EVENT, TrainTaskRoomMessage.class, (client, data, ackRequest) ->
        {
            String clientSessionID = client.getSessionId().toString();

            synchronized (Constants.trainTaskRoomMap)
            {
                //理论上同一个客户端不存在房间缓存记录
                if(!Constants.trainTaskRoomMap.containsKey(clientSessionID))
                {
                    TrainTaskRoom trainTaskExitRoom = (TrainTaskRoom)Constants.trainTaskRoomMap.get(clientSessionID);

                    if(trainTaskExitRoom == null)
                    {
                        TrainTaskRoom joinTrainTaskRoom = new TrainTaskRoom();
                        joinTrainTaskRoom.setCandidateACode(data.getCandidateACode());
                        joinTrainTaskRoom.setRoomCode(data.getRoomCode());
                        joinTrainTaskRoom.setSocketSessionID(clientSessionID);
                        joinTrainTaskRoom.setRoomRole(Constants.ROLE_B);

                        Constants.trainTaskRoomMap.put(clientSessionID, joinTrainTaskRoom);

                        LogUtil.info("--> 客户端第一次加入房间，房间号：" + data.getRoomCode());
                    }
                }
                else
                {
                    LogUtil.info("--> [tip]客户端已经加入过房间，房间号：" + data.getRoomCode());
                }
            }
        });


    }

    public void sendMessageToClient(String eventName, TrainTaskRoomMessage trainTaskRoomMessage)
    {
        // broadcast messages to all clients
        serverSocket.getBroadcastOperations().sendEvent(eventName, trainTaskRoomMessage);
    }

    public void deleteTrainTaskRoom(String eventName, TrainTaskRoomMessage trainTaskRoomMessage)
    {
        serverSocket.getBroadcastOperations().sendEvent(eventName, trainTaskRoomMessage);

        serverSocket.addEventListener(eventName, TrainTaskRoomMessage.class, new DataListener<TrainTaskRoomMessage>()
        {
            @Override
            public void onData(SocketIOClient client, TrainTaskRoomMessage data, AckRequest ackRequest)
            {
                System.out.println("=================================" + data.getCandidateACode());
            }
        });
    }





}
