cetsim
    .controller('studentCtrl', ['$scope','$location','commonService','userService',
        function($scope, $location,$commonService,$userService) {
            // var loginuser = JSON.parse($commonService.Cookies('user.model'));    // 如果这句代码执行错误,页面无法重定位到登陆页面,所以使用下句代码获取user model
            var loginuser = $userService.getUserModelFromLocalInfo();
            if(loginuser)
            {
                $scope.name = loginuser.name;
            }

            $scope.signout = function () {
                $userService.logout();
            };

            $location.path("/student/mytask");

            //获取当前登录用户信息（图像、姓名）
            $scope.curUser = $userService.getUserModelFromLocalInfo();

            //需要监听自定义事件，更新界面显示信息 by lws 20170329
            $scope.$on("updateUserInfoEvent", function()
            {
                var _localUser = $userService.getUserModelFromLocalInfo();
                _localUser.photo = $commonService.addRandomTimestampForUrl($userService.getUserPhotoAbsolutePathByFileName(_localUser.photo));
                $scope.curUser = _localUser;
            });

            $scope.$on("event.userPhotoChanged", function () {
                var _localUser = $userService.getUserModelFromLocalInfo();
                _localUser.photo = $commonService.addRandomTimestampForUrl($userService.getUserPhotoAbsolutePathByFileName(_localUser.photo));
                $scope.curUser = _localUser;
            })

        }
    ])
    .controller('studentTaskCtrl', ["$scope", "$timeout", "$interval", "$window", "commonService", "studentTaskService",'userService',
        function($scope, $timeout, $interval, $window, $commonService, $studentTaskService, $userService) {
            //配置
            $scope.total = 0;
            $scope.p_pagesize = 10;
            //变量
            $scope.p_current = 1;
            $scope.p_all_page = 0;
            $scope.maxPaperPage = 1;
            $scope.pages = [];
            $scope.order = {
                column: null,
                code: null
            };

            //初始化界面时，先加载成绩区间配置，避免多次重复加载
            $commonService.getScorelevel();

            //默认查询全部,status int 查询条件，3查询全部，1已完成，0进行中, -1已过期
            $scope.queryStatus = 3;

            var currentServerTime = null;

                /**
             * 查询界面数据时，需要同步服务器时间
             * @param index
             * @param status
             */
            $scope.load_page = function(index, status)
            {
                //查询服务器端时间
                $commonService.getServerSyncTime(function(err, value)
                {
                    currentServerTime = new Date(value);
                });

                //更新查询状态
                $scope.queryStatus = status;

                $scope.p_current = index;
                //status状态字段最新需求为：考试状态(0:未考试,1:考试成功,2:考试失败)
                $studentTaskService.getTaskList($commonService.Cookies('user_account'), $scope.p_current, $scope.p_pagesize, status, function(tasks) {
                    $scope.tasks = tasks.data;
                    $scope.total = tasks.total;
                    $scope.pages = [];
                    for (var i = 1; i <= tasks.pages; i++) {
                        $scope.pages.push(i);
                    };
                    $scope.p_all_page = tasks.pages;

                    $scope.maxPaperPage = Math.ceil(tasks.total / $scope.p_pagesize);
                }, true, {
                    orderColumnName : $scope.order.column,
                    orderCode : $scope.order.code
                });
            }
            $scope.p_index = function() {
                $scope.load_page(1, $scope.queryStatus);
            }
            $scope.p_last = function() {
                $scope.load_page($scope.p_all_page, $scope.queryStatus);
            }

            $scope.goPage = function (page) {

                if (page > $scope.maxPaperPage) {
                    page = $scope.maxPaperPage;
                }
                if (page < 1) {
                    page = 1;
                }

                $scope.load_page(page, $scope.queryStatus);

            };

            $scope.load_page(1, $scope.queryStatus);

            /**
             * 综合判断显示操作类型，去完成、查看报告、已过期
             * @param examCount
             * @param maxScore
             * @param taskEndTime
             */
            $scope.showOperaterType = function (examCount, maxScore, taskEndTime)
            {
                var taskEndTime = new Date(taskEndTime);

                //先判断考试时间是否已经过期, 如果已经参加过考试，则可以查看报告
                if(currentServerTime > taskEndTime && examCount == 0)
                {
                    return "OVER";
                }
                else
                {
                    //一次都没参加考试
                    if(examCount == 0)
                    {
                        //去完成
                        return "DO";
                    }
                    else
                    {
                        //查看报告
                        return "QUERY";
                    }
                }
            };

            $scope.examRecordCode;
            $scope.paperTypeCode;

            //学生账号
            $scope.studentAccount = $userService.getUserModelFromLocalInfo().account;

            /**
             * 去完成考试, 需要判断哪些情况不能启动考试
             * 1、考试时间未到
             * 2、试卷权限被关闭
             */
            $scope.beginExamTaskHandler = function(paperAllowPlanUseage, taskCode, taskStartTime, paperType)
            {
                //判断试卷权限，如果权限关闭，不允许开始考试
                if(paperAllowPlanUseage == 0)
                {
                    $commonService.layerError("试卷权限已经被管理员关闭，不能正常开始考试了~");
                    return;
                }

                //需要判断任务启动时间和当前服务器时间，如果任务开始时间未到，则不允许考生启动考试
                //查询服务器端时间
                $commonService.getServerSyncTime(function(err, value)
                {
                    if(err || value == null)
                    {
                        $commonService.showTip("网络有点小问题，请稍后重新开始考试~");
                        return;
                    }

                    var currentServerTime = new Date(value);
                    var taskEndTime = new Date(taskStartTime);

                    //考试时间没有到
                    if(moment(currentServerTime).isBefore(taskEndTime))
                    {
                        var leftTimeStr = $commonService.leftTimer(taskEndTime, currentServerTime);

                        // $commonService.showTip("开始时间未到，不能启动考试机, 还剩" + leftTimeStr);
                        $commonService.countDown(currentServerTime, taskEndTime, function (leftTimeStr, mFrom, mTo) {
                            if (mFrom.isAfter(mTo)) {
                                return "可以开始考试了";
                            }
                            return "开始时间未到，不能启动考试机, 还剩" + leftTimeStr;
                        })
                    }
                    else
                    {
                        //启动考试机
                        $studentTaskService.beginExamService(taskCode, false, function(res)
                        {
                            //只有请求服务端成功后，才可以启动客户端考试机
                            if (res.code === $commonService.config.ajaxCode.success)
                            {
                                //服务端新增的考试记录
                                var recordObj = res.data;

                                $scope.examRecordCode = recordObj.code;
                                $scope.paperTypeCode = paperType.toUpperCase();

                                //考试状态(0:未分组，1:未考试,2:正在考试,3:考试完成,4:考试错误)
                                var examState = recordObj.examState;

                                if(examState == "3")
                                {
                                    $commonService.showTip("考试已完成，不能重复考试");
                                }
                                else
                                {
                                    //var loginuser = JSON.parse($commonService.Cookies('user.model')); type: CET4 cet6; mode:sim train special

                                    var paperTypeCode = paperType.toUpperCase();

                                    var url = 'cetsim://{"toLogin":"' + recordObj.code + '", "url":"' + globalConfig.host  + '", "type":"' + paperTypeCode + '", "mode":"sim"}';

                                    //IE浏览器
                                    if("IE" == $commonService.getBrowType())
                                    {
                                        //ie 插件检测客户端有没有注册协议，如果注册了，则说明已经安装软件，直接用自定义协议启动客户端
                                        if($commonService.checkInstallClient())
                                        {
                                            location.href = url;
                                        }
                                        else
                                        {
                                            //判断IE的位数，64位IE浏览器不支持，提示用户更换32位IE浏览器
                                            if($commonService.checkIEIs64Version())
                                            {
                                                $commonService.showTip("64位IE浏览器不支持启动考试机客户端，请更换32位IE或者其他浏览器");
                                                return;
                                            }
                                        }

                                        //如果插件没有检测到客户端安装特定协议，则显示提示框提示用户下载
                                    }
                                    else
                                    {
                                        //自定义协议启动客户端程序，参数说明toLogin：考试记录ID，url：服务器地址
                                        //非IE内核通过iframe启动自定义协议, 非IE浏览器和插件无关，只关注考试机程序是否在注册表正确写入cetsim协议
                                        $("#startClientFrame").attr("src", url);
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
                                        content: '<div id="layerContainer"><div id="loadingDiv" style=" text-align: center; vertical-align: middle;  font-size: 160px; font-weight: 700; color: green;">10</div></div>'
                                    });

                                    //启动考试机以后，启动定时器，每隔一秒中访问一次服务器，检查考试机是否已经启动，如果已经启动则停止定时器；如果10s后还未启动，新打开界面提示下载考试客户端
                                    var checkIndex = 10;

                                    var CheckClientStatus = function(){
                                       $studentTaskService.isEcpStartService(recordObj.code, function(resultData)
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
                                                var layer_confirm_html = $("#flushTaskInfoListLayerHtml").html();
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

                                                    //直接跳转到查看报告详情界面
                                                    //$scope.load_page(1, $scope.queryStatus);
                                                });
                                            }else
                                            {
                                                if(checkIndex < 10)
                                                {
                                                    $("#loadingDiv").text("0" + checkIndex);
                                                }
                                                else
                                                {
                                                    $("#loadingDiv").text(checkIndex);
                                                }

                                                if(checkIndex === 0)
                                                {
                                                    //计数完成后，替换弹出框内容
                                                    var htmlStr = $("#downloadExamClientInfoHtml").html();

                                                    //副本html中避免出现重复的ID，导致jquery无法获取正常的对象
                                                    $("#downloadExamClientInfoHtml .common-btn .common-btn-primary").attr("id", "confirmBtn");
                                                    $("#downloadExamClientInfoHtml .common-btn").attr("id", "closeLayerBtn");

                                                    //替换显示内容
                                                    $("#layerContainer").html(htmlStr);

                                                    $(".common-btn").click(function ()
                                                    {
                                                        //关闭弹出框
                                                        layer.close(startClientExamLayerIndex);

                                                        //刷新界面状态，做为补充
                                                        $scope.load_page(1, $scope.queryStatus);
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
                            }
                            else
                            {
                                $commonService.showTip("网络有点小问题，请稍后重新开始考试~");
                            }
                        });
                    }
                });
            };
        }
    ])
    .controller('studentFreeSimCtrl', ["$scope", "$timeout", "commonService",
        function($scope, $timeout, $commonService) {


        }
    ])
    .controller('studentClassCtrl', ["$scope", "$timeout", "commonService", "studentClassService",
        function($scope, $timeout, $commonService, $studentClassService) {
            //配置
            $scope.total = 0;
            $scope.p_pagesize = 5;
            //变量
            $scope.p_current = 1;
            $scope.p_all_page = 0;
            $scope.pages = [];

            $scope.load_page = function(index) {
                $scope.p_current = index;
                $studentClassService.getClassList($commonService.Cookies('user_id'), $scope.p_current, $scope.p_pagesize, function(json) {
                    $scope.rooms = json.data;
                    $scope.total = json.total;
                    $scope.pages = [];
                    for (var i = 1; i <= json.pages; i++) {
                        $scope.pages.push(i);
                    };
                    $scope.p_all_page = json.pages;
                });

            }
            $scope.p_index = function() {
                $scope.load_page(1);
            }
            $scope.p_last = function() {
                $scope.load_page($scope.p_all_page);
            }

            $scope.load_page(1);
        }
    ])
    .controller("studentViewTaskCtrl", ["$scope", "studentTaskReportService", "commonService", "$stateParams", "$location", "userService", "studentSpecialTrainService",
        function($scope, $studentTaskReportService, $commonService, $stateParams, $location, userService, studentSpecialTrainService)
    {
        //学生账号
        var studentAccount = $stateParams.studentAccount;

        //学生任务ID
        var recordCode = $stateParams.recordCode;

        //CET试卷类型 0：cet4； 1：cet6
        var paperTypeValue = $stateParams.paperType;
        var user = userService.getUserModelFromLocalInfo();
        $scope.data2 = {};


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
            $studentTaskReportService.getTaskExamInfo(recordCode, "sim", function(response, isSuccess)
            {
                if(!isSuccess)
                {
                    $scope.taskExamInfo = null;
                    //$commonService.showTip("服务端获取试卷包信息异常");
                }
                else {
                    if(response.data.code == $commonService.config.ajaxCode.success)
                    {
                        $scope.taskExamInfo = response.data.data;

                        if($scope.taskExamInfo == null)
                        {
                            //$commonService.showTip("服务器端没有读取到试卷包信息");
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

            //2 获取测试答题包信息
            $studentTaskReportService.getTaskAnswerInfo(studentAccount, recordCode, "sim", function(response, isSuccess)
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
                        $studentTaskReportService.operatePartnerSounder(studentAccount, recordCode, "sim", true, function(result, isSuccess)
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
            $studentTaskReportService.getExamResultService(recordCode, function(result, isSuccess)
            {
                if(isSuccess)
                {
                    $.each(result, function (index, value) {

                        var key = value.k;

                        var itemValue = value.v;

                        //得分区间需要用过滤器格式化处理
                        if(key == "MAXSCORE")
                        {
                            //从全局成绩配置中计算排名A\B\C\D
                            $commonService.getGlobRankAndDescriptionOfScore(itemValue, paperTypeValue, function(pmValue, description)
                            {
                                //没有成绩或者成绩为0的，必须要去完成考试
                                $scope.examResultInfoData.PM_VALUE = pmValue;

                                //最新需求，将界面所有排名等级修改为只显示成绩,不同区间去整数不一样
                                $scope.examResultInfoData.PM_SCORE = $commonService.scoreMathService(itemValue);

                                //评语信息
                                $scope.examResultInfoData.description = description;
                            });
                        }
                        else if(key == "RANK")
                        {
                            //总人数
                            var totalValue = $scope.examResultInfoData.TOTAL;

                            //个人排名
                            var rankValue = itemValue;

                            //计算排名百分比
                            var roundValue = rankValue / totalValue * 100;

                            if(roundValue < 1)
                            {
                                //分值小于1的，向上取整数
                                itemValue = Math.ceil(roundValue);
                            }
                            else
                            {
                                itemValue = Math.round(roundValue);
                            }

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

        //只提供学习引擎返回错误的提示信息
        $scope.querySelfSpecialAnswerInfo = function () {
            studentSpecialTrainService.querySelfSpecialAnswerInfo({
                studentAccount : user.account,
                specialRecordCode : recordCode,
                paperItemTypeCode : "04_02",
                examType : "sim"
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

    .controller("soundMediaModalPlayerCtrl", ["$scope", "commonService", function($scope, $commonService)
    {
        $commonService.playerSoundService($scope, "#jquery_jplayer_mp3", "#jp_container_mp3");
    }]);



cetsim.controller("zxxlRecordCtrl", ["$scope", "studentService", "commonService", "$log", "userService", "$timeout",
    function ($scope, studentService, commonService, $log, userService, $timeout) {
        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 10;
        $scope.items = [];
        $scope.paperName = "";
        $scope._selectPaperItemTypeCode ="";
        $scope.order = {
            code: null,
            column: null
        };

        var user = userService.getUserModelFromLocalInfo();

        $scope.refresh = function () {
            studentService.querySpecialRecordList({
                studentAccount : user.account,
                pageIndex : $scope.curPage,
                pageSize : $scope.pageSize,
                name :  $scope.paperName || null,
                paperItemTypeCode : $scope._selectPaperItemTypeCode || null,
                orderColumnName : $scope.order.column,
                orderCode : $scope.order.code
            }, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {

                    try {

                        res.data.data.forEach(function (item) {
                            item.useCount = item.userCount;
                        });
                    } catch (ex) {}
                    $scope.items = res.data.data;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                } else {
                    commonService.layerError("加载试题列表失败");
                }
            })
        };
        $scope.refresh();

        $scope.refreshToPage1 = function () {
            $scope.goPage(1);
        };

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
        })
    }]);


cetsim.controller("zdksGuijiCtrl", ["$scope", "studentService", "commonService", "$log", "$timeout", "userService", "userService", "$filter",
    function ($scope, studentService, commonService, $log, $timeout, userService, userService, $filter) {
        $scope.data = {};
        var user = userService.getUserModelFromLocalInfo();
        $scope.conditions = {
            studentAccount : user.account,
            beginTime : null,
            endTime : null
        };
        $scope.timeLimitType = 1; //1个月
        $scope.title = "";

        $scope.chooseTimeLimit = function (limitMonth) {
            $scope.timeLimitType = limitMonth;
            $scope.conditions.endTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
            $scope.conditions.beginTime = moment(new Date()).subtract(limitMonth, "month").format("YYYY-MM-DD HH:mm:ss");

            $scope.getGuijiData();
        };

        $scope.getGuijiData = function () {
            studentService.queryTrainTaskScoreTrack($scope.conditions, function (err, res) {

                var data = {
                    title : null,
                    valueList : [],
                    xList : []
                };
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    res.data.pointList.forEach(function (item) {
                        data.valueList.push(item.score);
                        data.xList.push(item.time);
                    });

                    try {
                        var scoreValueFatter = $filter("scoreValueFormater");
                        data.valueList.forEach(function (item, i) {
                            data.valueList[i] = scoreValueFatter(item);
                        });
                    } catch (ex){}

                    data = $.extend(data, res.data);
                    data.points = res.data.pointList;

                    if ( data.points &&  data.points.length >= 3) {
                        $scope.isShowNoDataImage = false;
                        $scope.title = "成绩轨迹";
                        commonService.highChartLineGraph("score-guiji-container", data);
                    } else {
                        $scope.isShowNoDataImage = true;
                    }

                } else {
                    commonService.layerError("加载图表数据失败");
                }
            })
        };
        $scope.getGuijiData();
}]);




