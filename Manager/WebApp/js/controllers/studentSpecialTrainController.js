/**
 * 学生专项训练控制器
 * Created by Administrator on 2017/5/15.
 */
cetsim.controller("zxxlListCtrl", ["$scope", "studentSpecialTrainService", "commonService", "$log", "$timeout",
    function ($scope, studentSpecialTrainService, commonService, $log, $timeout) {
        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 10;
        $scope.items = [];

        $scope._selectPaperItemType = "";

        $scope.usageStatus = [{
            key: null,
            name: "全部"
        }, {
            key: 1,
            name: "已练习"
        }, {
            key: 0,
            name: "未练习"
        }];
        $scope.selectUsageStatus = $scope.usageStatus[0];



        $scope.refresh = function () {
            studentSpecialTrainService.queryQuestionList({
                pageIndex : $scope.curPage,
                pageSize : $scope.pageSize,
                paperItemTypeCode : $scope._selectPaperItemType || null,
                useState : $scope.selectUsageStatus.key
            }, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    var list = res.data.data;
                    if (list) {
                        list.forEach(function (item) {
                            item._paperItemTypeName = commonService.paperItemType[item.paperItemTypeCode];
                            item._paperItemTypeObj = commonService.paperItemTypeV2[item.paperItemTypeCode];
                            item.useCount= item.userCount;  // 服务端字段拼写错误,修改服务端代码改动太多,所以修改前端
                        })
                    }

                    $scope.items = list;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                } else {
                    commonService.layerError("加载试题列表失败");
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
        });

        //专项训练考试记录代码
        $scope.specialRecordCode;

        //专项训练朗读题题型代码
        $scope.paperItemTypeCode = "04_02";

        /**
         * 开始专项训练
         * @param paperStaus 试卷的状态 1可用，0不可用
         * @param paperItemCode   试题代码
         */
        $scope.startSpecialTrainExam = function(paperStaus, paperItemCode)
        {
            //判断试卷的权限
            if(paperStaus == 0)
            {
                commonService.layerError("该专项试题权限已经被管理员关闭，不能开始练习了~");
                return;
            }

            //开始练习，服务端还需要对试卷的权限进行检查，避免由于界面没有刷新的情况
            studentSpecialTrainService.specialStartExamService(commonService.Cookies('user_account'), paperItemCode, function(data)
            {
                if(data.code == commonService.config.ajaxCode.success)
                {
                    var recordObj = data;

                    //专项训练考试记录
                    var specialRecordCode = recordObj.data.code;

                    $scope.specialRecordCode = specialRecordCode;

                    var url = 'cetsim://{"toLogin":"' + specialRecordCode + '", "url":"' + globalConfig.host  + '", "type":"' + paperItemCode + '", "mode":"special"}';

                    //启动考试机，通过检测不同浏览器分别采用插件（IE）和自定义协议
                    commonService.startClient("#specialTrainTaskStartClientFrame", url);

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
                        content: '<div id="layerContainer_special"><div id="loadingDiv_special" style=" text-align: center; vertical-align: middle;  font-size: 160px; font-weight: 700; color: green;">10</div></div>'
                    });

                    //启动考试机以后，启动定时器，每隔一秒中访问一次服务器，检查考试机是否已经启动，如果已经启动则停止定时器；如果10s后还未启动，新打开界面提示下载考试客户端
                    var checkIndex = 10;

                    var CheckClientStatus = function()
                    {
                        studentSpecialTrainService.isSpecialTrainTaskStartService(specialRecordCode, function(resultData)
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
                                var layer_confirm_html = $("#flushSpecialTrainInfoListLayerHtml").html();
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
                                    $("#loadingDiv_special").text("0" + checkIndex);
                                }
                                else
                                {
                                    $("#loadingDiv_special").text(checkIndex);
                                }

                                if(checkIndex === 0)
                                {
                                    //计数完成后，替换弹出框内容
                                    var htmlStr = $("#downloadspecialTrainTaskClientInfoHtml").html();

                                    //副本html中避免出现重复的ID，导致jquery无法获取正常的对象
                                    $("#downloadspecialTrainTaskClientInfoHtml .common-btn .common-btn-primary").attr("id", "confirmBtn");
                                    $("#downloadspecialTrainTaskClientInfoHtml .common-btn").attr("id", "closeLayerBtn");

                                    //替换显示内容
                                    $("#layerContainer_special").html(htmlStr);

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
                else
                {
                    commonService.layerError(data.msg);
                }
            });
        };

    }]);


/**
 * 专项训练查看报告Ctrl
 */
cetsim.controller("zxxlCkbgCtrl", ["$scope", "$stateParams", "studentSpecialTrainService", "commonService", "$log", "userService",
    function ($scope, $stateParams, studentSpecialTrainService, commonService, $log, userService)
    {
        var specialRecordCode = $stateParams.code;
        var paperItemTypeCode = $stateParams.paperItemTypeCode;

        $scope.data = {};
        $scope.data2 = {};

        var user = userService.getUserModelFromLocalInfo();

        /**
         * 查看专项训练报告成绩信息, 需要判断考试状态
         * @param studentAccount
         * @param specialRecordCode
         */
        $scope.querySpecialExamReport = function(studentAccount, specialRecordCode)
        {
            //由于专项训练提供当考试机启动后，直接跳转到查看报告界面，需要判断考试记录是否正确结束，如果没有，则不执行后续查询操作
            studentSpecialTrainService.querySpecialRecordStateService(studentAccount, specialRecordCode, function (data)
            {
                if (data && data.code == commonService.config.ajaxCode.error)
                {
                    commonService.layerError(data.msg);
                    return;
                }
                else
                    {
                    //1、查询专项训练试题包信息及考试记录信息
                    studentSpecialTrainService.querySpecialExamReportService(studentAccount, specialRecordCode, function (data)
                    {
                        if(data && data.code == 1)
                        {
                            $scope.answerPath = commonService.config.host + data.data.answerPath;

                            $scope.score = data.data.score;

                            $scope.paperItemName = data.data.paperItemName;

                            $scope.paperItemTypeName = data.data.paperItemTypeName;

                            $scope.startTime = data.data.startTimeStr;

                            $scope.data = data.data;

                        }
                        else if (data && data.code == 0)
                        {
                            commonService.layerError(data.msg);
                        }
                    });

                    //2、学习引擎结果
                    $scope.querySelfSpecialAnswerInfo = function () {
                        studentSpecialTrainService.querySelfSpecialAnswerInfo({
                            studentAccount : user.account,
                            specialRecordCode : specialRecordCode,
                            paperItemTypeCode : paperItemTypeCode,
                            examType : "special"
                        }, function (err, res) {
                            $log.log(res);
                            if (!err && res.code === commonService.config.ajaxCode.success) {
                                $scope.data2 = res.data;
                            }
                            else
                            {
                                commonService.layerError(res.msg);

                                /*var a = document.createElement("a");
                                document.body.appendChild(a);
                                a.href = "javascript:;";
                                a.click();
                                a.remove();*/
                            }
                        });
                    };

                    $scope.querySelfSpecialAnswerInfo();
                }
            });
        };

        $scope.querySpecialExamReport(user.account, specialRecordCode);

        studentSpecialTrainService.taskExamInfo({
            examRecordCode : specialRecordCode,
            examType : "special"
        }, function (err, res) {
            if (err) {
                // TODO 置灰播放按钮颜色
            }
        })

    }]);



