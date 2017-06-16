/**
 * Created by Administrator on 2017/5/13.
 */

cetsim.service("studentTrainTaskService", ["$http", "commonService", function ($http, commonService)
{
    var _self = this;
    this.clientSocket;

    /**
     * 初始化网络通信
     */
    this.initSocketIO = function()
    {
        var serverPath = commonService.config.host;

        //截取配置的服务器地址和端口
        var serverIP = serverPath.substring(0, serverPath.lastIndexOf(":"));

        //端口号取tomcat端口减1
        var serverPort = parseInt(serverPath.substring(serverPath.lastIndexOf(":") + 1, serverPath.length)) - 1;

        var server = serverIP + ":" + serverPort;


        this.clientSocket =  io.connect(server);
    };

    this.initSocketIO();



    this.createTrainTaskRoomService = function(studentAccount, paperCode, isShowShadowWhenQuery, fn){
        var url = commonService.config.host + '/api/student/createTrainTaskRoom.action?' + $.param({
                studentAccount : studentAccount,
                paperCode : paperCode
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("创建房间异常"), false);
        })
    };

    /**
     * 加入房间
     * @param studentAccount 自己的账号
     * @param roomCode 房间账号
     */
    this.joinTrainTaskRoomService = function(studentAccount, candidateACode, roomCode, isShowShadowWhenQuery, fn)
    {
        var url = commonService.config.host + '/api/student/joinTrainTaskRoom.action?' + $.param({
                studentAccount : studentAccount,
                candidateACode: candidateACode,
                roomCode : roomCode
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("加入房间异常"), false);
        })
    };

    /**
     * 房主A启动开始考试
     * @param roomCode
     * @param isShowShadowWhenQuery
     * @param fn
     */
    this.roomStartExamService = function(roomCode, candidateACode, candidateBCode, paperCode, isShowShadowWhenQuery, fn)
    {
        var url = commonService.config.host + '/api/student/startTrainTaskExam.action?' + $.param({
                roomCode : roomCode,
                candidateACode:candidateACode,
                candidateBCode:candidateBCode,
                paperCode:paperCode
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("服务器异常"), false);
        })
    };

    this.isTrainTaskStartService = function(recordId, callBackFun){
        $http({
            method : "POST",
            url : commonService.config.host + '/exam/isEcpStart.action?' + $.param({
                recordId : recordId
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(response.data);
        }, function (response) {
            commonService.showTip("请求服务端数据失败");

            callBackFun(null);
        });

    };

    /**
     * 房主A退出房间
     * @param roomCode
     * @param isShowShadowWhenQuery
     * @param fn
     */
    this.candidateAQuitRoomService = function(roomCode, isShowShadowWhenQuery, fn)
    {
        var url = commonService.config.host + '/api/student/candidateAQuitRoom.action?' + $.param({
                roomCode : roomCode
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("服务器异常"), false);
        })
    };

    this.candidateBQuitRoomService = function(roomCode, isShowShadowWhenQuery, fn)
    {
        var url = commonService.config.host + '/api/student/candidateBQuitRoom.action?' + $.param({
                roomCode : roomCode
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("服务器异常"), false);
        })
    };

    /**
     * 自主考试成绩报告
     * @param trainRecordCode
     * @param fn
     */
    this.getExamResultService = function(trainRecordCode, fn){
        $http({
            method : "GET",
            url : commonService.config.host + '/api/student/queryTrainTaskExamResult.action?' + $.param({
                trainRecordCode : trainRecordCode
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(response.data.data, true);
        }, function (response) {
            fn(new Error(response.data.msg), false);
        })
    };



}]);






