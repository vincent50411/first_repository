/**
 * 自主考试控制器
 * Created by Administrator on 2017/6/1.
 */
cetsim.controller("zjksCtrl", ["$scope", "studentService", "studentTrainTaskService", "userService", "commonService", "$log", "$timeout",
    function ($scope, studentService, studentTrainTaskService, userService, commonService, $log, $timeout) {
        //获取当前登录用户信息（图像、姓名）
        $scope.curUser = userService.getUserModelFromLocalInfo();

        //A角色的考试记录code
        $scope.candidateARecordCode;

        $scope.paperTypeCode;

        //学生账号
        $scope.studentAccount = $scope.curUser.account;

        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 10;
        $scope.items = [];
        $scope.conditions = {};
        $scope._selectPaperType = {
            key: "全部",
            val: null
        };
        $scope._selectUseState = {
            key: "全部",
            val: null
        };

        $scope.order = {
            column: null,
            code: null
        };

        $scope.refresh = function (page) {
            if (page) {
                $scope.curPage = page;
            }
            studentService.queryTrainTaskPaperList($.extend({
                pageIndex : $scope.curPage,
                pageSize : $scope.pageSize,
                paperTypeCode : $scope._selectPaperType.val,
                useState :  $scope._selectUseState.val,
                orderColumnName : $scope.order.column,
                orderCode : $scope.order.code
            }, $scope.conditions), function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.items = res.data.data;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                } else {
                    commonService.layerError("加载失败");
                }
            })
        };
        $scope.refresh();

        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if (page <= 0) {
                page = 1;
            }

            $scope.curPage = page;
            $scope.refresh();
        };

        $timeout(function () {
            $(".eui-select").cssSelect();
        });


        /**
         * 创建自主考试房间
         * @param paperCode
         */
        $scope.createTrainTaskRoom = function(paperCode){
            //var jsonObject = {userAccount: commonService.Cookies('user_account'), paperCode:paperCode, message: message};

            wirteUserBPhotoAndNameText();
            function wirteUserBPhotoAndNameText(img, name) {
                if (name) {
                    $("#train-user-b-photo").attr("src", img);
                    $("#candidateB").text(name);
                    $("#candidate-B-area").removeClass("no-user-B");
                } else {
                    $("#train-user-b-photo").attr("src", "static/images/avatar-wait.png");
                    $("#candidateB").text("等待中...");
                    $("#candidate-B-area").addClass("no-user-B");
                }
            }
            studentTrainTaskService.createTrainTaskRoomService(commonService.Cookies('user_account'), paperCode, false, function(data, result)
            {
                //创建的房间信息
                var trainTaskRoom;

                if(result && data)
                {
                   //创建房间成功
                    trainTaskRoom = data;

                    //A创建房间成功后，发送一个事件给服务器
                    studentTrainTaskService.clientSocket.emit('CANDIDATE_A_CREATE_TASK_ROOM_REQUEST_EVENT', trainTaskRoom);

                    var scrollWidth  = document.body.scrollWidth ;
                    var scrollHeight = document.body.scrollHeight;

                    var centerWidth = (scrollWidth/2 - 450/2) + "px";
                    var centerHeight = (scrollHeight/2 - 280/2) + "px";

                    // var gourpView_A = layer.open({
                    //     type: 1,
                    //     title: false,
                    //     area: ['480px', '340px'],
                    //     offset: [centerHeight, centerWidth],
                    //     closeBtn: false,
                    //     shadeClose: false,
                    //     content: '<div id="layerContainer"><div id="loadingDiv" class="breadcrumbs" style=" text-align: center; vertical-align: middle;  font-size: 18px; font-weight: 100; color: green;"><div style="border-color: gold; border-style: solid;">房间：<span id="roomCode"></span></div><div style="border-color: gold; border-style: solid;"><span id="candidateA"></span></div><div style="border-color: gold; border-style: solid;"><span id="candidateB"></span></div><div><button id="startExamBtn">开始考试</button><button id="removeRoomBtn">退出房间</button></div></div></div>'
                    // });

                    $log.log(trainTaskRoom);

                    var gourpView_A = commonService.layerOpen({
                        title: "等待同组加入",
                        area: "540px",
                        closeBtn: false,
                        shadeClose: false,
                        content: $("#zuduikaoshi-waiting-panel")
                    });

                    $("#startExamBtn").show();

                    $("#train-paper-name").text(trainTaskRoom.paperName);
                    $("#train-paper-type-code").text(trainTaskRoom.paperTypeCode);
                    $("#train-user-a-photo").attr("src", userService.getUserPhotoAbsolutePathByFileName(trainTaskRoom.candidateAPhoto));
                    $("#roomCode").text(trainTaskRoom.roomCode);
                    $("#candidateA").text(trainTaskRoom.candidateAName);

                    //开始考试
                    $("#startExamBtn").click(function()
                    {
                        var roomCode = trainTaskRoom.roomCode;
                        var candidateACode = trainTaskRoom.candidateACode;
                        var candidateBCode = trainTaskRoom.candidateBCode;
                        var paperCode = trainTaskRoom.paperCode;

                        //房间开始考试
                        studentTrainTaskService.roomStartExamService(roomCode, candidateACode, candidateBCode, paperCode, false, function(data, result)
                        {
                            if(result && data)
                            {


                            }
                            else
                            {
                                if (data.message) {
                                    commonService.layerError(data.message);
                                } else {
                                    commonService.layerError(data);
                                }
                            }
                        });
                    });

                    //退出房间
                    $("#removeRoomBtn").click(function(){
                        //弹出确认退出分组提示框
                        commonService.layerConfirm("是否确定退出分组?", function () {
                            //确认退出分组
                            studentTrainTaskService.candidateAQuitRoomService(trainTaskRoom.roomCode, false, function(data, result)
                            {
                                if(result && data)
                                {
                                    commonService.layerSuccess(data);

                                    layer.close(gourpView_A);
                                }
                                else
                                {
                                    commonService.layerError(data);
                                }
                            });
                        }, function () {
                            //取消退出分组
                        });
                    });

                    //***************** 监听房主A退出房间的事件(自己更换机器重新登录) ***************************
                    studentTrainTaskService.clientSocket.on("CANDIDATE_A_QUIT_TASK_ROOM_EVENT", function(data)
                    {
                        //撤销的房间代码
                        var quiteRoomCode = data.roomCode;

                        //自己加入到房间如何和撤销的房间代码一致，则将B退出该房间
                        if(trainTaskRoom && trainTaskRoom.roomCode == quiteRoomCode)
                        {
                            layer.close(gourpView_A);

                            if('startExam' === data.messageType)
                            {
                                //commonService.layerSuccess(data.message);
                            }
                            else
                            {
                                commonService.layerSuccess("房主已经撤销房间~");
                            }
                        }
                    });

                    //***************** 监听B加入房间的通知事件 ***************************
                    studentTrainTaskService.clientSocket.on("CANDIDATE_B_JION_TASK_ROOM_EVENT", function(data){

                        var joinRoomCode = data.roomCode;

                        if(trainTaskRoom && trainTaskRoom.roomCode == joinRoomCode)
                        {
                            trainTaskRoom.candidateBCode = data.candidateBCode;

                            wirteUserBPhotoAndNameText(userService.getUserPhotoAbsolutePathByFileName(data.candidateBPhoto), data.candidateBName);

                            layer.title("组队成功", gourpView_A);
                        }
                    });

                    //***************** 监听B可能退出房间的通知事件 ***************************
                    studentTrainTaskService.clientSocket.on("CANDIDATE_B_QUIT_TASK_ROOM_EVENT", function(data)
                    {
                        //撤销的房间代码
                        var quiteRoomCode = data.roomCode;

                        //自己加入到房间如何和撤销的房间代码一致，则将B退出该房间
                        if(trainTaskRoom && trainTaskRoom.roomCode == quiteRoomCode)
                        {
                            wirteUserBPhotoAndNameText();

                            layer.title("等待同组加入", gourpView_A);
                        }
                    });

                    //***************** 监听服务器关闭的通知事件 ***************************
                    studentTrainTaskService.clientSocket.on('disconnect', function()
                    {
                        //修改界面状态，提示用户服务器关闭
                    });

                    //***************** 监听房间开始考试的通知事件 ***************************
                    studentTrainTaskService.clientSocket.on("START_TASK_EXAM_EVENT", function(data)
                    {
                        var startExamRoomCode = data.roomCode;

                        if(trainTaskRoom && trainTaskRoom.roomCode == startExamRoomCode)
                        {
                            //A的考试记录代码
                            $scope.candidateARecordCode = data.candidateARecord;

                            $scope.paperTypeCode = data.paperTypeCode == null ? "CET4" : data.paperTypeCode;

                            //commonService.layerError(data);
                            var url = 'cetsim://{"toLogin":"' + $scope.candidateARecordCode + '", "url":"' + globalConfig.host  + '", "type":"' + $scope.paperTypeCode + '", "mode":"train"}';

                            //启动考试机，通过检测
                            //IE浏览器
                            if("IE" == commonService.getBrowType())
                            {
                                //ie 插件检测客户端有没有注册协议，如果注册了，则说明已经安装软件，直接用自定义协议启动客户端
                                if(commonService.checkInstallClient())
                                {
                                    location.href = url;
                                }
                                else
                                {
                                    //判断IE的位数，64位IE浏览器不支持，提示用户更换32位IE浏览器
                                    if(commonService.checkIEIs64Version())
                                    {
                                        commonService.showTip("64位IE浏览器不支持启动考试机客户端，请更换32位IE或者其他浏览器");
                                        return;
                                    }
                                }

                                //如果插件没有检测到客户端安装特定协议，则显示提示框提示用户下载
                            }
                            else
                            {
                                //自定义协议启动客户端程序，参数说明toLogin：考试记录ID，url：服务器地址
                                //非IE内核通过iframe启动自定义协议, 非IE浏览器和插件无关，只关注考试机程序是否在注册表正确写入cetsim协议
                                $("#trainTaskStartClientFrame_A").attr("src", url);
                            }

                            var scrollWidth  = document.body.scrollWidth ;
                            var scrollHeight = document.body.scrollHeight;

                            var centerWidth = (scrollWidth/2 - 450/2) + "px";
                            var centerHeight = (scrollHeight/2 - 280/2) + "px";

                            //页面层-自定义, 手动计算居中
                            var startClientExamLayerIndex = layer.open({
                                type: 1,
                                title: false,
                                area: ['480px', '340px'],
                                offset: [centerHeight, centerWidth],
                                closeBtn: false,
                                skin: 'layui-layer-nobg', //没有背景色
                                shade : 0.5,
                                shadeClose: false,
                                content: '<div id="layerContainer_A"><div id="loadingDiv_A" style=" text-align: center; vertical-align: middle;  font-size: 160px; font-weight: 700; color: green;">10</div></div>'
                            });

                            //启动考试机以后，启动定时器，每隔一秒中访问一次服务器，检查考试机是否已经启动，如果已经启动则停止定时器；如果10s后还未启动，新打开界面提示下载考试客户端
                            var checkIndex = 10;

                            var CheckClientStatus = function()
                            {
                                studentTrainTaskService.isTrainTaskStartService($scope.candidateARecordCode, function(resultData)
                                {
                                    if(resultData == null)
                                    {
                                        layer.close(startClientExamLayerIndex);
                                        return;
                                    }

                                    checkIndex = checkIndex - 1;

                                    //返回true，考试开始；false未启动考试机
                                    if(resultData.data)
                                    {
                                        //成功启动考试机并开始考试, 关闭倒计时等候窗口，新弹出一个刷新界面确认窗口
                                        layer.close(startClientExamLayerIndex);

                                        //新弹出的窗口是让考生在成功结束考试并关闭考试机后，刷新界面
                                        var layer_confirm_html = $("#flushTrainInfoListLayerHtml_A").html();
                                        var layer_confirm_html_index = layer.open({
                                            type: 1,
                                            title: false,
                                            area: ['480px', '340px'],
                                            offset: [centerHeight, centerWidth],
                                            closeBtn: false,
                                            skin: 'layui-layer-nobg', //没有背景色
                                            shade : 0.5,
                                            shadeClose: false,
                                            content: layer_confirm_html
                                        });

                                        $(".common-btn").click(function ()
                                        {
                                            //点击确定按钮关闭弹出框，刷新当前界面数据
                                            layer.close(layer_confirm_html_index);
                                        });
                                    }
                                    else
                                    {
                                        if(checkIndex < 10)
                                        {
                                            $("#loadingDiv_A").text("0" + checkIndex);
                                        }
                                        else
                                        {
                                            $("#loadingDiv_A").text(checkIndex);
                                        }

                                        if(checkIndex === 0)
                                        {
                                            //计数完成后，替换弹出框内容
                                            var htmlStr = $("#downloadExamClientInfoHtml_A").html();

                                            //副本html中避免出现重复的ID，导致jquery无法获取正常的对象
                                            $("#downloadExamClientInfoHtml_A .common-btn .common-btn-primary").attr("id", "confirmBtn");
                                            $("#downloadExamClientInfoHtml_A .common-btn").attr("id", "closeLayerBtn");

                                            //替换显示内容
                                            $("#layerContainer_A").html(htmlStr);

                                            $(".common-btn").click(function ()
                                            {
                                                //关闭弹出框
                                                layer.close(startClientExamLayerIndex);
                                            });
                                        }
                                        else
                                        {
                                            setTimeout(CheckClientStatus,1000);
                                        }
                                    }
                                });
                            };

                            CheckClientStatus();
                        }
                    });
                }
                else
                {
                    commonService.layerError(data);
                }
            });
        };



        /**
         * 角色A退出房间
         * @param roomCode
         * @param candidateACode
         */
        $scope.removeTrainTaskRoom = function(roomCode, candidateACode){


        };
    }]);


/**
 * 房间大厅，注册实时通信，监听大厅房间变化
 */
cetsim.controller("zdksCtrl", ["$scope", "studentService", "studentTrainTaskService", "userService", "commonService", "$log", "$timeout",
    function ($scope, studentService, studentTrainTaskService, userService, commonService, $log, $timeout) {
        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 100;
        $scope.items = [];

        $scope._selectPaperType = "";

        $scope.paperName = "";

        //B角色的考试记录code
        $scope.candidateBRecordCode;

        $scope.paperTypeCode;

        var user = userService.getUserModelFromLocalInfo();
        //学生账号
        $scope.studentAccount = user.account;

        $scope.refresh = function () {

            studentService.queryTrainTaskRoomList({
                studentAccount : user.account,
                pageIndex : $scope.curPage,
                pageSize : $scope.pageSize,
                paperTypeCode : $scope._selectPaperType || null,
                paperName : $scope.paperName ? $scope.paperName : null
            }, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    if (res.data && res.data.data) {
                        $scope.items = res.data.data;
                        $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                    } else {
                        $scope.items = res.data;
                        $scope.maxPage = 1;
                    }
                } else {
                    commonService.layerError("加载失败");
                }
            })
        };
        $scope.refresh();

        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if (page <= 0) {
                page = 1;
            }

            $scope.curPage = page;
            $scope.refresh();
        };

        $scope.refreshToPage1 = function () {
            $scope.goPage(1);
        };

        $timeout(function () {
            $(".eui-select").cssSelect();
        });

        $scope.initClientSocket = function()
        {
            //***************** 监听房间组队成功的事件，修改房间大厅的房间状态 ***************************
            studentTrainTaskService.clientSocket.on("TRAIN_TASK_ROOM_GROUPED_EVENT", function(data)
            {
                //已经组队成功的房间代码
                var groupedRoomCode = data.roomCode;

                $($scope.items).each(function(index, item)
                {
                    //循环所有的房间，如果发现有房间已经组队成功，则需要修改房间显示样式提示用户
                    if(item.roomCode == groupedRoomCode)
                    {
                        //******************* 修改房间状态:房间已满，显示B角色的信息，加入按钮隐藏 ******************
                        $("#" + groupedRoomCode + "_candidateBName").text(data.candidateBName);
                        $scope.$apply(function () {
                            $scope.items[index]['disabled'] = true;
                        });
                        //******************* 修改房间状态:房间已满，显示B角色的信息，加入按钮隐藏 ******************
                    }
                });
            });

            //***************** 监听房间有A角色退出房间的事件，修改房间大厅的房间状态: A退出房间，房间不可用 ***************************
            studentTrainTaskService.clientSocket.on("CANDIDATE_A_QUIT_TASK_ROOM_EVENT", function(data)
            {
                //已经组队成功的房间代码
                var groupedRoomCode = data.roomCode;

                $($scope.items).each(function(index, item)
                {
                    //循环所有的房间，如果发现有房间已经组队成功，则需要修改房间显示样式提示用户
                    if(item.roomCode == groupedRoomCode)
                    {
                        //******************* 修改房间状态： A退出房间，房间不可用 ******************
                        $scope.$apply(function () {
                            $scope.items[index]['disabled'] = true;
                            $scope.items[index]['deleted'] = true;
                        });
                        //******************* 修改房间状态： A退出房间，房间不可用 ******************
                    }
                });
            });

            //***************** 监听房间有B角色退出房间的事件，修改房间大厅的房间状态：B退出房间，房间仍然可用 ***************************
            studentTrainTaskService.clientSocket.on("CANDIDATE_B_QUIT_TASK_ROOM_EVENT", function(data)
            {
                //已经组队成功的房间代码
                var groupedRoomCode = data.roomCode;

                $($scope.items).each(function(index, item)
                {
                    //循环所有的房间，如果发现有房间已经组队成功，则需要修改房间显示样式提示用户
                    if(item.roomCode == groupedRoomCode)
                    {
                        //******************* 修改房间状态:B退出房间，房间可用, 房间已满状态变更 ******************
                        $scope.$apply(function () {
                            $scope.items[index]['disabled'] = false;
                        });
                        $("#" + groupedRoomCode + "_candidateBName").html('<a href="javascript:;" class="btn-join">+</a>');
                        //******************* 修改房间状态:B退出房间，房间可用, 房间已满状态变更 ******************
                    }
                });
            });

            //***************** 监听服务器关闭的通知事件 ***************************
            studentTrainTaskService.clientSocket.on('disconnect', function()
            {
                //修改界面状态，提示用户服务器关闭

            });
        };

        $scope.initClientSocket();

        /**
         * 加入房间
         * @param roomCode 房间号
         * @param candidateACode 房主的账号
         */
        $scope.joinTrainTaskRoom = function(roomCode, candidateACode)
        {
            var trainTaskRoom;
            var gourpView_B;

                //加入房间
            studentTrainTaskService.joinTrainTaskRoomService(commonService.Cookies('user_account'), candidateACode, roomCode, false, function(data, result)
            {
                if(result && data)
                {
                    //加入的房间信息
                    trainTaskRoom = data;

                    //B加入房间成功后，发送一个事件给服务器
                    studentTrainTaskService.clientSocket.emit('CANDIDATE_B_JION_TASK_ROOM_REQUEST_EVENT', trainTaskRoom);

                    gourpView_B = commonService.layerOpen({
                        title: "组队成功",
                        area: "540px",
                        closeBtn: false,
                        shadeClose: false,
                        content: $("#zuduikaoshi-waiting-panel")
                    });

                    $log.log(trainTaskRoom);

                    $("#startExamBtn").hide();

                    $("#train-paper-name").text(trainTaskRoom.paperName);
                    $("#train-paper-type-code").text(trainTaskRoom.paperTypeCode);
                    $("#roomCode").text(trainTaskRoom.roomCode);


                    $("#candidateB").text(trainTaskRoom.candidateBName);
                    $("#train-user-b-photo").attr("src", userService.getUserPhotoAbsolutePathByFileName(trainTaskRoom.candidateBPhoto));


                    $("#candidateA").text(trainTaskRoom.candidateAName);
                    $("#train-user-a-photo").attr("src", userService.getUserPhotoAbsolutePathByFileName(trainTaskRoom.candidateAPhoto));


                    //【退出房间】, 需要将全局保存到房间信息重置 trainTaskRoom = null
                    $("#removeRoomBtn").click(function(){
                        //弹出确认退出分组的提示信息
                        commonService.layerConfirm("是否确定退出分组?", function(){
                            //确认退出分组
                            studentTrainTaskService.candidateBQuitRoomService(trainTaskRoom.roomCode, false, function(data, result)
                            {
                                if(result && data)
                                {
                                    commonService.layerSuccess(data);

                                    layer.close(gourpView_B);
                                }
                                else
                                {
                                    commonService.layerError(data);
                                }

                                trainTaskRoom = null;
                            });
                        }, function(){
                            //取消退出分组
                        });

                    });
                }
                else
                {
                    commonService.layerError(data);
                }
            });


            //***************** 监听房主A退出房间的事件 ***************************
            studentTrainTaskService.clientSocket.on("CANDIDATE_A_QUIT_TASK_ROOM_EVENT", function(data)
            {
                //撤销的房间代码
                var quiteRoomCode = data.roomCode;

                //自己加入到房间如何和撤销的房间代码一致，则将B退出该房间
                if(trainTaskRoom && trainTaskRoom.roomCode == quiteRoomCode)
                {
                    layer.close(gourpView_B);

                    //重新弹出提示信息
                    if('startExam' === data.messageType)
                    {
                        //不需要提示房间已经撤销的提示信息
                        //commonService.layerSuccess(data.message);
                    }
                    else
                    {
                        commonService.layerSuccess("房主解散了您刚加入的房间，请重新加入其它房间");
                    }
                }
            });

            //***************** 监听B(自己)可能退出房间的通知事件（更换其他机器重新登录） ***************************
            studentTrainTaskService.clientSocket.on("CANDIDATE_B_QUIT_TASK_ROOM_EVENT", function(data)
            {
                //撤销的房间代码
                var quiteRoomCode = data.roomCode;

                //自己加入到房间如何和撤销的房间代码一致，则将B退出该房间
                if(trainTaskRoom && trainTaskRoom.roomCode == quiteRoomCode)
                {
                    $("#candidateB").text("账号在其他地方登录，需要退出该房间");
                }
            });

            //***************** 监听房间开始考试的通知事件 ***************************
            studentTrainTaskService.clientSocket.on("START_TASK_EXAM_EVENT", function(data)
            {
                //撤销的房间代码
                var startExamRoomCode = data.roomCode;

                //自己加入到房间如何和撤销的房间代码一致，则将B退出该房间
                if(trainTaskRoom && trainTaskRoom.roomCode == startExamRoomCode)
                {
                    //B的考试记录代码
                    $scope.candidateBRecordCode = data.candidateBRecord;
                    $scope.paperTypeCode = data.paperTypeCode == null ? "CET4" : data.paperTypeCode;

                    //commonService.layerError(data);
                    var url = 'cetsim://{"toLogin":"' + $scope.candidateBRecordCode + '", "url":"' + globalConfig.host  + '", "type":"' + $scope.paperTypeCode + '", "mode":"train"}';

                    //启动考试机，
                    //IE浏览器
                    if("IE" == commonService.getBrowType())
                    {
                        //ie 插件检测客户端有没有注册协议，如果注册了，则说明已经安装软件，直接用自定义协议启动客户端
                        if(commonService.checkInstallClient())
                        {
                            location.href = url;
                        }
                        else
                        {
                            //判断IE的位数，64位IE浏览器不支持，提示用户更换32位IE浏览器
                            if(commonService.checkIEIs64Version())
                            {
                                commonService.showTip("64位IE浏览器不支持启动考试机客户端，请更换32位IE或者其他浏览器");
                                return;
                            }
                        }

                        //如果插件没有检测到客户端安装特定协议，则显示提示框提示用户下载
                    }
                    else
                    {
                        //自定义协议启动客户端程序，参数说明toLogin：考试记录ID，url：服务器地址
                        //非IE内核通过iframe启动自定义协议, 非IE浏览器和插件无关，只关注考试机程序是否在注册表正确写入cetsim协议
                        $("#trainTaskStartClientFrame_B").attr("src", url);
                    }

                    var scrollWidth  = document.body.scrollWidth ;
                    var scrollHeight = document.body.scrollHeight;

                    var centerWidth = (scrollWidth/2 - 450/2) + "px";
                    var centerHeight = (scrollHeight/2 - 280/2) + "px";

                    //页面层-自定义, 手动计算居中
                    var startClientExamLayerIndex = layer.open({
                        type: 1,
                        title: false,
                        area: ['480px', '340px'],
                        offset: [centerHeight, centerWidth],
                        closeBtn: false,
                        skin: 'layui-layer-nobg', //没有背景色
                        shade : 0.5,
                        shadeClose: false,
                        content: '<div id="layerContainer_B"><div id="loadingDiv_B" style=" text-align: center; vertical-align: middle;  font-size: 160px; font-weight: 700; color: green;">10</div></div>'
                    });

                    //启动考试机以后，启动定时器，每隔一秒中访问一次服务器，检查考试机是否已经启动，如果已经启动则停止定时器；如果10s后还未启动，新打开界面提示下载考试客户端
                    var checkIndex = 10;

                    var CheckClientStatus = function()
                    {
                        studentTrainTaskService.isTrainTaskStartService($scope.candidateBRecordCode, function(resultData)
                        {
                            if(resultData == null)
                            {
                                layer.close(startClientExamLayerIndex);
                                return;
                            }

                            checkIndex = checkIndex - 1;

                            //返回true，考试开始；false未启动考试机
                            if(resultData.data)
                            {
                                //成功启动考试机并开始考试, 关闭倒计时等候窗口，新弹出一个刷新界面确认窗口
                                layer.close(startClientExamLayerIndex);

                                //新弹出的窗口是让考生在成功结束考试并关闭考试机后，刷新界面
                                var layer_confirm_html = $("#flushTrainInfoListLayerHtml_B").html();
                                var layer_confirm_html_index = layer.open({
                                    type: 1,
                                    title: false,
                                    area: ['480px', '340px'],
                                    offset: [centerHeight, centerWidth],
                                    closeBtn: false,
                                    skin: 'layui-layer-nobg', //没有背景色
                                    shade : 0.5,
                                    shadeClose: false,
                                    content: layer_confirm_html
                                });

                                $(".common-btn").click(function ()
                                {
                                    //点击确定按钮关闭弹出框，刷新当前界面数据
                                    layer.close(layer_confirm_html_index);
                                });
                            }
                            else
                            {
                                if(checkIndex < 10)
                                {
                                    $("#loadingDiv_B").text("0" + checkIndex);
                                }
                                else
                                {
                                    $("#loadingDiv_B").text(checkIndex);
                                }

                                if(checkIndex === 0)
                                {
                                    //计数完成后，替换弹出框内容
                                    var htmlStr = $("#downloadExamClientInfoHtml_B").html();

                                    //副本html中避免出现重复的ID，导致jquery无法获取正常的对象
                                    $("#downloadExamClientInfoHtml_B .common-btn .common-btn-primary").attr("id", "confirmBtn");
                                    $("#downloadExamClientInfoHtml_B .common-btn").attr("id", "closeLayerBtn");

                                    //替换显示内容
                                    $("#layerContainer_B").html(htmlStr);

                                    $(".common-btn").click(function ()
                                    {
                                        //关闭弹出框
                                        layer.close(startClientExamLayerIndex);
                                    });
                                }
                                else
                                {
                                    setTimeout(CheckClientStatus,1000);
                                }
                            }
                        });
                    };

                    CheckClientStatus();
                }
            });

        };

    }]);


cetsim.controller("zdksRecordCtrl", ["$scope", "studentService", "commonService", "$log", "$timeout", "userService",
    function ($scope, studentService, commonService, $log, $timeout, userService) {
        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 10;
        $scope.items = [];

        $scope._selectPaperType = "";
        $scope.order = {
            column : null,
            code : null
        };

        var user = userService.getUserModelFromLocalInfo();

        //初始化界面时，先加载成绩区间配置，避免多次重复加载
        commonService.getScorelevel();

        $scope.refresh = function () {
            studentService.queryTrainTaskRecordList({
                pageSize : $scope.pageSize,
                pageIndex : $scope.curPage,
                studentAccount : user.account,
                paperTypeCode : $scope._selectPaperType || null,  // change empty string to null, then remove key of null value in next step
                paperName : $scope.paperName || null,
                orderColumnName : $scope.order.column,
                orderCode : $scope.order.code
            }, function (err, res) {
                $log.log(res);
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.items = res.data.data;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                } else {
                    commonService.layerError("加载考试记录数据失败");
                }
            })
        };
        $scope.refresh();

        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if (page <= 0) {
                page = 1;
            }

            $scope.curPage = page;
            $scope.refresh();
        };

        $scope.refreshToPage1 = function () {
            $scope.refresh(1);
        };

        $timeout(function () {
            $(".eui-select").cssSelect();
        })
    }]);

cetsim.controller("studentTrainTaskViewTaskCtrl", ["$scope", "studentTaskReportService", "studentTrainTaskService", "commonService", "$stateParams", "$location", "userService", "studentSpecialTrainService",
    function($scope, $studentTaskReportService, $studentTrainTaskService, $commonService, $stateParams, $location, userService, studentSpecialTrainService)
    {
        //学生账号
        var studentAccount = $stateParams.studentAccount;

        //学生任务ID
        var recordCode = $stateParams.recordCode;

        //CET试卷类型 0：cet4； 1：cet6
        var paperTypeValue = $stateParams.paperType;
        $scope.data2 = {};
        var user = userService.getUserModelFromLocalInfo();

        /**
         * 返回首页
         */
        $scope.goMyTaskListView = function()
        {
            $location.path("/student/mytask");
        };

        //试卷包信息
        $scope.taskExamInfo = null;

        //答题包信息
        $scope.taskAnswerInfo = null;

        $scope.serverHost = "";

        //最后一题队友和自己的合并语音文件地址
        $scope.partnerAndSelfMP3Path = "";

        //加载报告界面信息
        $scope.loadTaskReportInfo = function()
        {
            //1 获取测试试卷包信息
            $studentTaskReportService.getTaskExamInfo(recordCode, "train", function(response, isSuccess)
            {
                if(!isSuccess)
                {
                    $scope.taskExamInfo = null;
                    $commonService.showTip("服务端获取试卷包信息异常");
                }
                else {
                    if(response.data.code == $commonService.config.ajaxCode.success)
                    {
                        $scope.taskExamInfo = response.data.data;

                        if($scope.taskExamInfo == null)
                        {
                            $commonService.showTip("服务器端没有读取到试卷包信息");
                        }

                        //图片路径
                        $scope.serverHost = $commonService.config.host;
                    }
                    else
                    {
                        $scope.taskExamInfo = null;
                        $commonService.showTip(response.data.msg);
                    }
                }
            });

            //只提供学习引擎返回错误的提示信息
            $scope.querySelfSpecialAnswerInfo = function () {
                studentSpecialTrainService.querySelfSpecialAnswerInfo({
                    studentAccount : user.account,
                    specialRecordCode : recordCode,
                    paperItemTypeCode : "04_02",
                    examType : "train"
                }, function (err, res) {
                    if (!err && res.code === $commonService.config.ajaxCode.success) {
                        $scope.data2 = res.data;
                    }
                    else
                    {
                        $commonService.layerError(res.msg);
                    }
                });
            };
            $scope.querySelfSpecialAnswerInfo();

            //2 获取测试答题包信息 sim:模拟考试，train:自主考试
            $studentTaskReportService.getTaskAnswerInfo(studentAccount, recordCode, "train",  function(response, isSuccess)
            {
                if(!isSuccess)
                {
                    $scope.taskAnswerInfo = null;
                    //$commonService.showTip("服务端获取答题包信息异常");
                }
                else
                {
                    if(response.data.code == $commonService.config.ajaxCode.success)
                    {
                        $scope.taskAnswerInfo = response.data.data;

                        if($scope.taskAnswerInfo == null)
                        {
                            //$commonService.showTip("服务器端没有读取到答题包信息");
                        }

                        //图片路径
                        $scope.serverHost = $commonService.config.host;

                        //2.1 合成队友录音文件, 不能异步操作，必须等答题包解压完毕后才可以操作
                        $studentTaskReportService.operatePartnerSounder(studentAccount, recordCode, "train", true, function(result, isSuccess)
                        {
                            if(isSuccess)
                            {
                                if(result.data.code == $commonService.config.ajaxCode.success)
                                {
                                    //answer/test_pair.mp3
                                    $scope.partnerAndSelfMP3Path = result.data.data;
                                }
                                else
                                {
                                    $commonService.showTip(result.data.msg);
                                }
                            }
                            else
                            {
                                $commonService.showTip("服务端合成语音失败");
                            }
                        });
                    }
                    else
                    {
                        $scope.taskAnswerInfo = null;
                        //$commonService.showTip(response.data.msg);
                    }
                }
            });

            //试卷报告头部信息
            $scope.examResultInfoData = {};
            $studentTrainTaskService.getExamResultService(recordCode, function(result, isSuccess)
            {
                if(isSuccess)
                {
                    $.each(result, function (index, value) {

                        var key = value.k;

                        var itemValue = value.v;

                        //得分区间需要用过滤器格式化处理
                        if(key == "MAXSCORE")
                        {
                            $commonService.getGlobRankAndDescriptionOfScore(itemValue, paperTypeValue, function(pmValue, description)
                            {
                                //评语信息
                                $scope.examResultInfoData.description = description;
                            });
                        }
                        //动态创建对象的属性和值，属性名称界面写死需要和服务端或者数据库一致
                        $scope.examResultInfoData[key] = itemValue;
                    })
                }
                else
                {
                    $commonService.showTip(result.message);
                }
            });
        };

        //加载任务报告信息
        $scope.loadTaskReportInfo();

        //打开音频播放窗口, 队友录音处理
        $scope.openSoundPlayer = function(itemType, itemIndex)
        {
            if($scope.taskAnswerInfo == null)
            {
                $commonService.showTip("答题包语音文件不存在.");
                return;
            }

            //本人录音服务器文件基础地址
            var answerServerPathObj = $scope.taskAnswerInfo;

            var mediaURLValue = [];

            //第二题有2个回答录音
            if(itemType == "_ans1|_ans2")
            {
                if(answerServerPathObj.answerOnePath == null && answerServerPathObj.answerTwoPath == null)
                {
                    $commonService.showTip("2个问题的录音文件不存在");
                    return;
                }

                var mp3Obj;
                if(itemIndex == 1)
                {
                    mp3Obj = {mp3: $commonService.config.host + answerServerPathObj.answerOnePath};
                }
                else if(itemIndex == 2)
                {
                    mp3Obj = {mp3: $commonService.config.host + answerServerPathObj.answerTwoPath};
                }

                mediaURLValue.push(mp3Obj);
            }
            else if('togetherPairPath' == itemType)
            {
                mp3Obj = {mp3: $commonService.config.host + $scope.partnerAndSelfMP3Path};

                mediaURLValue.push(mp3Obj);
            }
            else
            {
                var mp3Obj = $scope.checkMp3Exit(answerServerPathObj[itemType]);

                if(mp3Obj != null)
                {
                    mediaURLValue.push(mp3Obj);
                }
            }

            if(mediaURLValue.length > 0)
            {
                //视频播放器窗口html内容
                var mpePlayerHtmlStr = $("#answerMP3PlayerHtml").html();

                //由于html（）产生的副本，id重复命名，导致播放器界面无法正常加载，需要替换ID
                mpePlayerHtmlStr = mpePlayerHtmlStr.replace("jp_container_mp3_r", "jp_container_mp3").replace("jquery_jplayer_mp3_r", "jquery_jplayer_mp3");

                //弹出视频播放器窗口
                var index = $commonService.popupPanel("录音播放", ['398px', '132px'], mpePlayerHtmlStr, true, false, false);

                $commonService.playerSoundService(mediaURLValue, "#jquery_jplayer_mp3", "#jp_container_mp3", index);
            }
        };

        /**
         * 判断mp3录音文件是否存在
         * @param mp3Path
         * @returns {*}
         */
        $scope.checkMp3Exit = function(mp3Path)
        {
            if(mp3Path == null)
            {
                $commonService.showTip("录音文件不存在");

                return null;
            }

            var mp3Obj = {mp3: $commonService.config.host + mp3Path};

            return mp3Obj;
        };

        /**
         * 打开视频播放窗口 itemValue:题目内容信息，itemName:
         * @param itemName  题目名称
         * @param itemIndex 第三题问答题，有三个题干视频，按照1、2、3加载视频
         */
        $scope.openVideoPlayer = function(itemName)
        {
            var videoFileList = [];

            //题目类型名称，不同题型，视频的题干不一样，需要固定
            if("item01" == itemName)
            {
                videoFileList = $scope.createVideoObject(videoFileList, itemName, "F1-T0-L1");
            }
            else if("item02" == itemName)
            {
                videoFileList = $scope.createVideoObject(videoFileList, itemName, "F1-T1-L1");
            }
            else if("item03_1" == itemName)
            {
                videoFileList = $scope.createVideoObject(videoFileList, "item03", "F1-T2-L1");
            }
            else if("item03_2" == itemName)
            {
                videoFileList = $scope.createVideoObject(videoFileList, "item03", "F1-T2-L2");
            }
            else if("item03_3" == itemName)
            {
                videoFileList = $scope.createVideoObject(videoFileList, "item03", "F1-T2-L3");
            }
            else if("item04" == itemName)
            {
                //视频数据对象
                videoFileList = $scope.createVideoObject(videoFileList, itemName, "F1-T3-L1");
            }
            else if("item05" == itemName)
            {
                //视频数据对象
                videoFileList = $scope.createVideoObject(videoFileList, itemName, "F1-T4-L1");
            }

            //自动播放视频
            if(videoFileList.length > 0)
            {
                //视频播放器窗口html内容
                var videoPlayerHtmlStr = $("#videoHtml").html();

                //由于html（）产生的副本，id重复命名，导致播放器界面无法正常加载，需要替换ID
                videoPlayerHtmlStr = videoPlayerHtmlStr.replace("jp_container_video_r", "jp_container_video").replace("jquery_jplayer_video_r", "jquery_jplayer_video");

                //弹出视频播放器窗口, 最大化窗口隐藏
                var index = $commonService.popupPanel("视频播放", ['478px', '395px'], videoPlayerHtmlStr, false, false, false);

                var playerSize = {
                    width: "478px",
                    height: "358px",
                    cssClass: "jp-video-360p"
                }

                $commonService.playerVideoService(videoFileList, playerSize, "#jquery_jplayer_video", "#jp_container_video", index);
            }
        };

        //创建视频数据对象
        $scope.createVideoObject = function(videoFileList, itemName, videoName)
        {
            if($scope.taskExamInfo == null)
            {
                $commonService.showTip("试卷包信息不存在.");
                return videoFileList;
            }

            //视频封面照片
            var posterPath;
            //题干视频
            var itemVideoPath;
            if($scope.taskExamInfo != null)
            {
                posterPath = $scope.taskExamInfo[itemName]["F1_teacher"];
                itemVideoPath = $scope.taskExamInfo[itemName][videoName];
            }

            if(itemVideoPath == null || itemVideoPath == "")
            {
                $commonService.showTip("视频资源不存在");
                return videoFileList;
            }

            //截取文件名称
            var fileName = itemVideoPath.substring(itemVideoPath.lastIndexOf("/") + 1, itemVideoPath.lastIndexOf("."));

            var videoFileObj = {
                title: fileName,
                m4v: $commonService.config.host + itemVideoPath,
                poster: $commonService.config.host + posterPath
            };

            videoFileList.push(videoFileObj);

            return videoFileList;
        };
    }])





