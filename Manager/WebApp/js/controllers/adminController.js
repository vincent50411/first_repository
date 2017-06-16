cetsim.controller("paperManageCtrl", ["$scope", "userService", "paperService", "commonService", "$log", "$timeout", "$rootScope",
    function ($scope, userService, paperService, commonService, $log, $timeout, $rootScope) {

        $scope.queryConditions = {
            type : "-1",
            status : "-1",
            pageIndex : 1,
            pageSize : 10,
            name : null,
            orderColumnName : null,
            orderCode : null    // desc, asc
        };
        $scope.maxPage = 1;
        $scope.papers = [];
        // $scope.paperTypeOptions = [{
        //     key : "全部",
        //     val : "-1"
        // }].concat(commonService.getPaperTypeOptions());
        $scope._selectPaperType = "";
        $scope._paperType = commonService.config.paperType;

        $scope.paperStatusOptions = [{
            key: "全部",
            val : "-1"
        }].concat(commonService.getPaperStatusOptions());
        $scope._selectStatus = "";

        $scope.getPaperTypeName = function (type) {
            return paperService.getPaperTypeName(type);
        };
        $scope.refresh = function (page) {
            if (page !== undefined) {
                $scope.queryConditions.pageIndex = page;
            }
            // 不能传type， 之前type是int类型区分四六级，现在字段保留依然是int类型，如果传递string type会参数映射报错
            // $scope.queryConditions["type"] = $scope._selectPaperType.val;
            // if ($scope._selectPaperType.val != "-1") {
            //     $scope.queryConditions["paperTypeCode"] = $scope._selectPaperType.val;
            // } else {
            //     delete $scope.queryConditions["paperTypeCode"];
            // }
            $scope.queryConditions["paperTypeCode"] = $scope._selectPaperType || null;
            $scope.queryConditions["status"] = $scope._selectStatus || null;

            var conditions = $.extend({}, $scope.queryConditions);
            if (conditions.type == "-1") delete conditions.type;
            if (conditions.status == "-1") delete conditions.status;


            paperService.getPaperList(conditions, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {

                    var list = res.data.data;
                    // list.forEach(function (item) {
                    //     commonService.getRankOfScoreByCetType(item.average_score, item.type, function (level) {
                    //         item["_average_score2"] = level;
                    //         $log.log("score : \t" + item.average_score + "\t type : \t" + item.type + "\t level:" + level);
                    //     });
                    //     commonService.getRankOfScoreByCetType(item.max_score, item.type, function (level) {
                    //         item["_max_score2"] = level;
                    //         $log.log("score : \t" + item.max_score + "\t type : \t" + item.type + "\t level:" + level);
                    //     })
                    // });
                    $scope.papers = list;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.queryConditions.pageSize);

                } else {
                    commonService.showTip("获取试卷列表失败");
                }
            }, true)
        };
        $scope.refresh();



        $scope.goPage = function (page) {
            if (page <= 0) {
                page = 1;
            }
            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            $scope.queryConditions.pageIndex = page;
            $scope.refresh();
        };

        $scope.refreshToPage1 = function () {
            $scope.refresh(1);
        };

        $scope.toggleStatusOfPaperType = function (index) {
            if ($scope.papers[index].status === commonService.config.paperStatus.disabled.code) {
                $scope.papers[index].status = commonService.config.paperStatus.enabled.code;
            } else {
                $scope.papers[index].status = commonService.config.paperStatus.disabled.code;
            }
            $scope.updatePaperStatus($scope.papers[index]);
        };

        $scope.togglePlanUsage = function (index) {
            if ($scope.papers[index].allowPlanUseage === commonService.config.paperPlanUsage.allow) {
                $scope.papers[index].allowPlanUseage = commonService.config.paperPlanUsage.disAllow;
            } else {
                $scope.papers[index].allowPlanUseage = commonService.config.paperPlanUsage.allow;
            }
            $scope.updatePaperPlanUseage($scope.papers[index]);
        };

        $scope.toggleFreeUsage = function (index) {
            if ($scope.papers[index].allowFreeUseage === commonService.config.paperFreeUsage.allow) {
                $scope.papers[index].allowFreeUseage = commonService.config.paperFreeUsage.disAllow;
            } else {
                $scope.papers[index].allowFreeUseage = commonService.config.paperFreeUsage.allow;
            }
            $scope.updatePaperFreeUseage($scope.papers[index]);
        };

        $scope.updatePaperStatus = function (paper) {
            paperService.updatePaperStatus(paper.code, paper.status, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.refresh();
                    commonService.layerClose("dialog");
                    if (paper.status) {
                        commonService.layerMsg("启用 " + paper.name +" 试卷状态成功");
                    } else {
                        commonService.layerMsg("禁用 " + paper.name +" 试卷状态成功");
                    }
                } else {
                    commonService.showTip("修改状态失败");
                }
            })
        };

        $scope.updatePaperPlanUseage = function (paper) {
            paperService.updatePaperPlanUseage(paper.code, paper.allowPlanUseage, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.refresh();

                    commonService.layerClose("dialog");

                    commonService.layerMsg(res.msg);

                    /*if (paper.allow_plan_useage) {
                        commonService.layerMsg("启用 " + paper.name +" 测试权限成功");
                    } else {
                        commonService.layerMsg("禁用 " + paper.name +" 测试权限成功");
                    }*/
                }
                else if(commonService.config.ajaxCode.error)
                {
                    commonService.layerMsg(res.data.msg);
                }
                else {
                    commonService.showTip("修改状态失败");
                }
            })
        };

        $scope.updatePaperFreeUseage = function (paper) {
            paperService.updatePaperFreeUseage(paper.code, paper.allowFreeUseage, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.refresh();

                    commonService.layerMsg(res.msg);
                } else {
                    commonService.showTip("修改状态失败");
                }
            })
        };
        
        $scope.deleteThisPaper = function (id, paperName) {
            commonService.layerConfirm("确认删除<code class='cetsim-stamp'>" + paperName + "</code>吗?", function () {
                paperService.deletePaper(id, function (err, res) {
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        $scope.refresh();
                        commonService.layerSuccess("提示", "删除试卷成功");
                    } else {
                        commonService.layerError("提示", "删除试卷失败");
                    }
                })
            });
        };

        $scope.showimportPaperPanel = function () {
            $rootScope.$broadcast("event.showimportPaperPanel");
        };

        var _unbindRefreshPaperListTable = $rootScope.$on("event.refreshPaperListTable", function () {
            $scope.refresh();
        });
        $scope.$on("$destroy", function () {
            _unbindRefreshPaperListTable();
        });

        $timeout(function () {
            $('.eui-select').cssSelect();
        })
}]);

/**
 * 试题管理
 */
cetsim.controller("paperItemManage", ["$scope", "paperItemService", "commonService", "$rootScope", "$timeout",
    function ($scope, paperItemService, commonService, $rootScope, $timeout) {

        $scope.maxPage = 1;
        $scope.curPage  = 1;
        $scope.pageSize = 10;
        $scope.paperItemName = null;
        $scope.items = [];
        $scope.order = {
            column : null,
            code : null
        };
        $scope._selectPaperItemType = "";
        $scope.paperItemStatusOptions = [{
            key : null,
            name : "全部"
        }, {
            key : 1,
            name : "启用"
        }, {
            key : 0,
            name : "禁用"
        }];
        $scope._selectStatus = $scope.paperItemStatusOptions[0];


        $scope.refresh = function (page) {
            if (page){
                $scope.curPage = page;
            }
            paperItemService.getPaperItems({
                pageIndex : $scope.curPage,
                pageSize : $scope.pageSize,
                paperItemTypeCode : $scope._selectPaperItemType || null,
                status : $scope._selectStatus.key,
                name : $scope.paperItemName,
                orderColumnName : $scope.order.column,
                orderCode : $scope.order.code
            }, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    var list = res.data.data;
                    if (list) {
                        list.forEach(function (item) {
                            item._paperItemTypeName = commonService.paperItemType[item.paperItemTypeCode];
                            item.useCount = item.userCount;
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

        $scope.refreshToPage1 = function () {
            $scope.refresh(1);
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

        var _unbindRefreshPaperListTable = $rootScope.$on("event.refreshPaperListTable", function () {
            $scope.refresh();
        });
        $scope.$on("$destroy", function () {
            _unbindRefreshPaperListTable();
        });

        $scope.toggleStatusOfPaperType = function (item) {
            var statusToSet;
            if (item.status == 1) {
                statusToSet = 0;
            } else {
                statusToSet = 1;
            }
            paperItemService.toggleStatusOfPaperType(item.code, statusToSet, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    if (statusToSet) {
                        commonService.layerMsg("启用权限成功");
                    } else {
                        commonService.layerMsg("禁用权限成功");
                    }

                    $scope.refresh();
                } else {
                    commonService.layerMsg("修改权限状态失败");
                }
            })
        };





        $timeout(function () {
            $(".eui-select").cssSelect();
        })
}]);

cetsim.controller("teacherManageCtrl", ["$scope",  "$timeout", "userService", "commonService", "teacherService", "$log", "$rootScope",
    function ($scope, $timeout, userService, commonService, teacherService, $log, $rootScope) {
        /**
         * 教师列表查询条件
         * @type {{page: number, limit: number, status: (any), name: string}}
         */
        $scope.queryConditions = {
            pageIndex : 1,
            pageSize : 10,
            status : userService.userStatus.all,
            name : ""
        };
        $scope.userGender = userService.userGender;
        $scope.userStatus = userService.userStatus;
        $scope.maxPage = 1;
        /**
         * 教师列表
         * @type {Array}
         */
        $scope.teachers = [];
        $scope.defaultPassword = commonService.config.resetDefaultPassword;

        /**
         * 切换状态
         * @param index
         */
        $scope.toggleStatus  = function (index) {
            if ($scope.teachers[index].status === userService.userStatus.disabled) {
                $scope.enableAccount($scope.teachers[index].account, index);
            } else {
                $scope.disableAccount($scope.teachers[index].account, index);
            }
        };

        $scope.saveStatus = function (user) {
            if (user.status === userService.userStatus.disabled) {
                $scope.disableAccount(user.account);
            } else {
                $scope.enableAccount(user.account);
            }
        };

        /**
         * 禁用账号
         * @param id 账号主键id
         */
        $scope.disableAccount = function (account, index) {
            userService.setUserStatus(account, userService.userStatus.disabled, function (err, res) {
                $scope.refreshTable();
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    commonService.showTip("禁用成功");
                } else {
                    commonService.showTip("禁用失败");
                    $log.log(res);
                }
            });
        };

        /**
         * 启用账号
         * @param id
         * @param index
         */
        $scope.enableAccount = function (account, index) {
            userService.setUserStatus(account, userService.userStatus.enabled, function (err, res) {
                $scope.refreshTable();
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    commonService.showTip("启用成功");
                } else {
                    $log.log(res);
                    $log.log("启用账号失败");
                    commonService.showTip("启用失败");
                }
            });
        };
        /**
         * 执行条件查询
         */
        $scope.executeQueryConditions = function () {
            $log.log("教师list查询条件");
            $log.log($scope.queryConditions);
            teacherService.query($scope.queryConditions, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $.each(res.data.data, function (i, item) {
                        if (item.status === userService.userStatus.enabled) {
                            res.data.data[i]["_isEnable"] = true;
                        } else {
                            res.data.data[i]["_isEnable"] = false;
                        }
                        res.data.data[i]["_lastLogin"] = item.lastLogin;
                        try {
                            if (item.lastLogin) {
                                res.data.data[i]["_lastLogin"] = (new Date(item.lastLogin)).Format("yyyy-MM-dd hh:mm:ss");
                            }
                        } catch(ex) {}
                    });
                    $scope.teachers = res.data.data;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.queryConditions.pageSize);
                    $log.log("查询教师list成功");
                } else {
                    $log.warn("查询教师list失败");
                    commonService.showTip("查询教师列表失败");
                }
            }, true);
        };
        $scope.executeQueryConditions();

        /**
         * 刷新表单数据
         */
        $scope.refreshTable = function (page) {
            // 如果page不是undefined则切换到指定的页面
            if (page !== undefined) {
                $scope.queryConditions.pageIndex = page;
            }
            $scope.executeQueryConditions();
        };

        /**
         * 重置密码
         * @param id
         */
        $scope.resetPassword = function (account, user) {
            // "确定重置<code>" + user.name + "</code>的密码么 ? 重置后密码为" + commonService.config.resetDefaultPassword
            commonService.layerConfirm("重置后的密码为" + commonService.config.resetDefaultPassword, function () {
                userService.resetPassword(account, function (err, res) {
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        $log.log("修改密码成功");
                        commonService.layerSuccess("重置密码成功")
                    } else {
                        $log.warn("修改密码失败");
                        commonService.layerError("重置密码失败");
                    }
                }, true);
            });
        };


        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if(page <= 0) {
                page = 1;
            }
            $scope.queryConditions.pageIndex = page;
            $scope.executeQueryConditions();
        };

        /**
         * 获取教师导入excel模板下载地址
         */
        $scope.getTeachersImportTemplateDownloadUrl = function () {
            return commonService.config.host + commonService.config.teachersImportTemplateDownloadUrl;
        };


        /**
         * 删除教师
         * @param id
         */
        $scope.deleteTeacher = function (id) {
            commonService.layerConfirm("确定删除教师吗?", function () {
                userService.deleteUser(id, function (err, res) {
                    if (!err) {
                        if (res.code === commonService.config.ajaxCode.success) {
                            $log.log("删除教师成功");
                            $scope.refreshTable();
                            commonService.layerSuccess("删除教师成功");
                        } else {
                            $log.warn("删除教师失败");
                            commonService.layerError("删除教师失败, 失败信息：" + res.msg);
                        }
                    } else {
                        $log.warn("删除教师失败");
                        commonService.layerError("删除教师失败")
                    }
                })
            });
        };

        var _unbindRefreshEvent = $rootScope.$on("event.refreshTeachersList", function () {
            $scope.refreshTable();
        });
        $scope.$on("$destroy", function () {
            _unbindRefreshEvent();
        });

        $timeout(function () {
            $('.eui-select').cssSelect();
        });
}]);

cetsim.controller("studentManageCtrl", ["$scope",  "$timeout", "userService", "commonService", "studentService", "$log", "$rootScope",
    function ($scope, $timeout, userService, commonService, studentService, $log, $rootScope) {
        $scope.institutes = commonService.institutes;
        $scope.userGender = userService.userGender;
        $scope.userStatus = userService.userStatus;
        $scope._host = commonService.config.host;
        $scope.defaultPassword = commonService.config.resetDefaultPassword;

        $scope._selectedGrade = "";

        // commonService.getGradesList(function (gradesList) {
        //     var arr;
        //     try {
        //         arr = gradesList.filter(function (item) {
        //             return $.trim(item) !== "" && item !== null && item !== undefined;
        //         });
        //         arr = arr.map(function (item) {
        //             return parseInt(item);
        //         });
        //         arr.sort(function (a, b) {
        //             return b - a;
        //         });
        //     } catch (ex) {
        //         $log.log("processing grades list error, using gradeslist from config.js");
        //         arr = commonService.config.gradesList;
        //     }
        //     arr.forEach(function (item) {
        //         $scope.allGrades.push({
        //             key: item,
        //             val: item
        //         })
        //     });
        // });

        $scope.allInstidutes = [{
            key: "全部",
            val : ""
        }];
        $scope._selectedInstitute = $scope.allInstidutes[0];
        commonService.config.institutesList.forEach(function (item) {
            $scope.allInstidutes.push({
                key : item,
                val : item
            })
        });

        $scope.queryConditions = {
            grade : "", // '' means all grades
            institution : "",
            name : "",
            pageIndex : 1,
            pageSize : 10
        };

        $scope.students = [];
        $scope.maxPage = 1;
        $scope.curPage = 1;
        $scope.pageSize = 10;
        /**
         * 根据条件查询学生列表
         * @param err
         * @param res
         */
        $scope.queryStudents = function () {
            var conditions = $.extend({}, $scope.queryConditions);
            conditions.grade = $scope._selectedGrade || null;
            conditions.institution  = $scope._selectedInstitute.val;

            if (commonService.isIE7()) {
                conditions.grade = $("#_selectedGradeForIE7").val();
            }
            if (commonService.isIE7()) {
                conditions.institution  = $("#_selectedInstituteForIE7").val();
            }
            if (conditions.grade === "") delete  conditions.grade;
            if (conditions.name === "") delete  conditions.name;
            if (conditions.institution === "") delete conditions.institution;

            studentService.query(conditions, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $.each(res.data.data, function (i, item) {
                        res.data.data[i]["_checked"] = $scope.isAllBoxChecked;
                    });
                    $scope.students = res.data.data;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                } else {
                    commonService.layerError("查询学生列表失败");
                }
            }, true);
        };
        $scope.queryStudents();

        $scope.conditionQuery = function () {
            $scope.queryConditions.pageIndex = 1;
            $scope.queryStudents();
        };
        /**
         * 重置学生密码
         * @param id
         */
        $scope.resetPassword = function (id, user) {
            // "确定重置<code>" + user.name + "</code>的密码么 ? 重置后密码为" + commonService.config.resetDefaultPassword
            commonService.layerConfirm("重置后的密码为" + commonService.config.resetDefaultPassword, function () {
                userService.resetPassword(id, function (err, res) {
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        $log.log("学生密码重置成功");
                        commonService.layerSuccess("重置密码成功");
                    } else {
                        $log.log("学生密码重置失败, 失败原因:");
                        $log.log(err);
                        commonService.layerError("重置密码失败");
                    }
                }, true)
            });
        };

        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if (page <= 0) {
                page = 1;
            }
            $scope.curPage = page;
            $scope.queryConditions.pageIndex = page;
            $scope.queryStudents();
        };

        $scope.isAllBoxChecked = false;
        $scope.toggleAllCheckboxes = function () {
            if ($scope.isAllBoxChecked) {
                $.each($scope.students, function (i) {
                    $scope.students[i]["_checked"] = true;
                })
            } else {
                $.each($scope.students, function (i) {
                    $scope.students[i]["_checked"] = false;
                })
            }
        };

        /**
         * 某个学生被选择的状态发生改变时
         */
        $scope.onStatusOfUserOnChange = function () {
            var isAllChecked = true;
            $.each($scope.students, function (i, item) {
                if (!item._checked) {
                    isAllChecked = false;
                    return false;
                }
            });
            $scope.isAllBoxChecked = isAllChecked;
        };

        /**
         * 禁用账号
         */
        $scope.disableAccount = function (id, index) {
            userService.setUserStatus(id, userService.userStatus.disabled, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    commonService.showTip("禁用账号成功");
                } else {
                    commonService.showTip("禁用账号失败");
                }
                $scope.queryStudents();
            });
        };

        /**
         * 启用账号
         */
        $scope.enableAccount = function (id) {
            userService.setUserStatus(id, userService.userStatus.enabled, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    commonService.showTip("启用账号成功");
                } else {
                    commonService.showTip("启用账号失败");
                }
                $scope.queryStudents();
            });
        };

        /**
         * 切换账号的状态(status)
         * @param id    被修改状态的用户id
         * @param index 数组索引
         */
        $scope.toggleStatusOfAccount = function (id, index) {
            if ($scope.students[index].status === $scope.userStatus.disabled) {
                $scope.enableAccount(id, index);
            } else {
                $scope.disableAccount(id, index);
            }
        };


        $scope.saveStatus = function (user) {
            if (user.status === userService.userStatus.disabled) {
                $scope.disableAccount(user.id);
            } else {
                $scope.enableAccount(user.id);
            }
        };

        /**
         * 导出帅选条件下的所有学生的测试报告详情
         */
        $scope.exportTestInfo = function()
        {
            //查询条件封装
            var conditions = $.extend({}, $scope.queryConditions);
            var arr = [];
            $scope.students.forEach(function (item) {
                if (item._checked) {
                    arr.push(item.account);
                }
            });
            if(arr.length > 0) {
                conditions["studentIdList"] = JSON.stringify(arr).replace(/(\[|])/g, "").replace(/\"/g,"");
            }
            //年级
            conditions.grade = $scope._selectedGrade.val;
            //学院
            conditions.institution  = $scope._selectedInstitute.val;
            if (conditions.grade === "") delete  conditions.grade;
            if (conditions.name === "") delete  conditions.name;
            if (conditions.institution === "") delete conditions.institution;

            //客户端下载文件, GET方法传递中文参数时，需要确保tomcat编码格式为UTF-8
            var a = document.createElement("a");
            document.body.appendChild(a);
            a.href = commonService.config.host + "/admin/exportExportDetail.action?" + $.param(conditions);
            a.click();
            a.remove();

        };

        /**
         * admin 查看 学生测试次数详情
         * @param id
         */
        $scope.showExamRecordPanel = function (account, exam_count) {
            if(_.isNumber(exam_count) && exam_count === 0) {
                return;
            }
            $rootScope.$broadcast("event.showExamRecordPanel", account);
        };


        $timeout(function () {
            $('.eui-select').cssSelect();
        });

}]);

cetsim.controller("examRecordOfOneStudentCtrl", ["$scope", "$rootScope", "$log", "userService", "commonService", "studentService",
    function ($scope, $rootScope, $log, userService, commonService, studentService) {
        $scope.list = [];
        $scope.curPage = 1;
        $scope.maxPage = 1;
        $scope.pageSize = 10;
        $scope.user = {};

        $scope.getAllExamRecordsOf = function (account) {
            $log.log("-------------------------------------");
            $log.log("examRecordOfOneStudentCtrl:");
            $log.log("account :" + account);
            $log.log("page :" + $scope.curPage);
            $log.log("-------------------------------------");

            studentService.getAllExamRecordList(account, $scope.curPage, $scope.pageSize, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    var list = res.data.data;
                    // list.forEach(function (item) {
                    //     commonService.getRankOfScoreByCetType(item.totalScore, item.type, function (level) {
                    //         item["_totalScore2"] = level;
                    //     });
                    // });
                    $scope.list = list;
                    $scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
                    $scope.showExamRecordPanel();

                } else {
                    //TODO
                }
            }, true);

            //同步服务端配置的成绩区间配置信息, 否则过滤器中无法根据得分过滤出成绩排名
            commonService.getScorelevel();

        };

        $scope.getUserInfo = function (userModel) {
            userService.getUserInfoFromServer(userModel, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.user = res.data;
                } else {
                    // TODO
                }
            });
        };

        $scope.showExamRecordPanel = function () {
            // commonService.showPanel(angular.element("#studentExamRecordPanel"));
            commonService.layerOpen({
                area : "500px",
                title : "测试详情",
                content : angular.element("#studentExamRecordPanel")
            })
        };

        $scope.goPage = function (page) {

            if (page > $scope.maxPage) {
                page = $scope.maxPage;
            }
            if (page < 1) {
                page = 1;
            }
            $scope.curPage = page;
            $scope.getAllExamRecordsOf($scope.user.account);
        };

        var _unbind = $rootScope.$on("event.showExamRecordPanel", function (ev, account) {

            $scope.list = [];
            $scope.curPage = 1;
            $scope.maxPage = 1;
            $scope.pageSize = 10;
            $scope.user = {};

            $scope.getUserInfo({
                account : account,
                role : userService.userRole.student
            });
            $scope.getAllExamRecordsOf(account);
        });
        $scope.$on("$destroy", function () {
            $log.log("examRecordOfOneStudentCtrl panel destroy");
            _unbind();
        })


}]);

/**
 * 单独添加教师
 */
cetsim.controller("addOneTeacherCtrl", ["$scope", "commonService", "userService", "$log", "teacherService", "$rootScope", "$timeout",
    function ($scope, commonService, user, $log, teacherService, $rootScope, $timeout) {
        $scope.teacherModel = {};
        var _copy = $.extend({}, $scope.teacherModel);

        function __initModel() {
            $scope.teacherModel = {
                account: "",
                name: "",
                status: parseInt(user.userStatus.enabled),
                gender: parseInt(user.userGender.boy),
                address: "",
                email: "",
                telephone: ""
            };

            $timeout(function () {
                $(".eui-select").cssSelect();
            })
        }
        __initModel();

        $scope.onClickCancenl = function () {
            __initModel();
        };

        $scope.submitTeacher = function () {
            _submitTeacher();
        };

        var _submitTeacher = commonService._.debounce(function () {

            /************************validation begin**********************************/
            var validation = [{
                obj: $scope.teacherModel.account,
                name : "账号",
                filters: ['validationNotEmpty', 'validationNoSpace', "validationLengthNotMoreThan:20", 'validationCetsimAccount']
            },{
                obj: $scope.teacherModel.name,
                name : "姓名",
                filters: ['validationNotEmpty', 'validationNoSpace', 'validationNoSpecialChars']
            },{
                obj: $scope.teacherModel.email,
                name : "邮箱",
                filters: ['validationNotEmpty', 'validationEmailNotStrict']
            },{
                obj: $scope.teacherModel.telephone,
                name : "联系方式",
                filters: ['validationNotEmpty', 'validationTelephoneNotStrict']
            }];

            if (!commonService.formValidate(validation)) {
                return;
            }
            /************************validation end**********************************/


            $log.log("-----------------");
            $log.log("添加用户的数据如下:");
            $log.log($scope.teacherModel);
            $log.log("-----------------");

            teacherService.addOneTeacher($scope.teacherModel, function (err, res) {
                if (!err) {
                    if (res.code === commonService.config.ajaxCode.success) {

                        $log.log("add teacher success");
                        layer.closeAll();
                        commonService.layerSuccess("提示", "添加老师成功");
                        $rootScope.$broadcast("event.refreshTeachersList");
                        __initModel();
                    } else {
                        $log.warn("add teacher error");
                        commonService.layerError("添加老师失败, 失败原因:" + res.msg);
                    }
                } else {
                    $log.warn("add teacher error");
                    commonService.layerError("添加老师失败");
                }
            }, true);
        }, 300);

        var _unbindClearCacheFormInfo = $scope.$on("event.cancelpop.addOneTeacherCtrlPanel", function () {
            $scope.$apply(function () {
                __initModel();
            })
        });
        $scope.$on("$destroy", function () {
            _unbindClearCacheFormInfo();
        })

}]);
/**
 * 批量添加教师
 */
cetsim.controller("addManyTeachersCtrl", ["$scope", "commonService", "$rootScope", "$timeout", "Upload", "$log",
    function ($scope, commonService, $rootScope, $timeout, Upload, $log) {
        /**
         * 获取教师导入上传的服务端接口地址
         */
        $scope.getTeachersImportActionUrl = function () {
            return commonService.config.host + commonService.config.teachersImportUrl;
        };
        $scope._host = commonService.config.host;

        $scope.file = "";
        $scope.submit = function () {
            commonService.formDataUpload(new commonService.URI($scope.getTeachersImportActionUrl())
                .addSearch("_showGlobalShadowWhenQuery", true).toString(), {
                file : $scope.file
            }, function (err, res) {
                if (!err) {
                    _dealRes(res);
                } else {
                    commonService.showTip("批量导入失败");
                }
            })
        };

        var _dealRes = function (res) {
            switch (res.code) {
                case  commonService.config.ajaxCode.success:
                    // commonService.layerClose($("#addManyTeachersPanel"));
                    commonService.viewOut($("#addManyTeachersPanel"));
                    if (/importTeachers/.test(location.href)) {
                        commonService.layerSuccess("提示", "批量导入成功", true, "返回教师列表");
                    } else {
                        commonService.layerSuccess("提示", "批量导入成功");
                    }
                    $rootScope.$broadcast("event.refreshTeachersList");
                    break;
                case commonService.config.ajaxCode.successButInfo:
                    // commonService.layerClose($("#addManyTeachersPanel"));
                    commonService.viewOut($("#addManyTeachersPanel"));
                    if (/importTeachers/.test(location.href)) {
                        commonService.layerSuccess("提示", res.msg, true, "返回教师列表");
                    } else {
                        commonService.layerSuccess("提示", res.msg);
                    }
                    $rootScope.$broadcast("event.refreshTeachersList");
                    break;
                default :
                    commonService.layerError("提示", "批量导入失败, 失败信息：" + res.msg);
                    break;
            }
        };


        $scope._clearFileComponent = function () {
            $scope.__file = null;
            $scope.__errFiles = null;
        };

        $scope.onClickCancel = function () {
            $scope._clearFileComponent();
        };

        $scope.__file = null;
        $scope.__errFiles = null;
        $scope._uploadFiles = function (file, errFiles) {
            $scope.__file = file;
            $scope.__errFiles = errFiles;
        };


        $scope.uploadFiles = function (file, errFiles) {

            if (file === undefined) {
                file = $scope.__file;
            }
            if (errFiles === undefined) {
                errFiles = $scope.__errFiles;
            }

            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                file.upload = Upload.upload({
                    url: commonService.getUrlWithShadowParam($scope.getTeachersImportActionUrl()),
                    data: {
                        file: file
                    }
                });

                file.upload.then(function (res) {
                    commonService.layerClose($("#importProgressPanel"));
                    $scope._clearFileComponent();
                    $timeout(function () {
                        _dealRes(res.data);
                    });
                }, function (response) {
                    commonService.layerError("提示", "批量导入失败");
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                        evt.loaded / evt.total));
                    $log.log(file.progress);
                    // commonService.layerImportProgress(Math.min(100, parseInt(100.0 *
                    //         evt.loaded / evt.total)) + "%");
                });
            } else {
                commonService.layerMsg("请选择文件");
            }
        };
}]);


cetsim.controller("addOneStudentCtrl", ["$scope",  "$stateParams", "classService", "$log", "commonService", "studentService",
    function ($scope, $stateParams, classService, $log, commonService, studentService) {
        $scope.title = "添加学生";
        $scope._selectOption = {
            id : "0",
            name : "男"
        };
        $scope._userGenderOptions = commonService._userGenderOptions2;

        $scope.account = {
            account : "",
            name : "",
            grade : "",
            major : "",
            institution : "",
            className : "",
            address : "",
            email : "",
            telephone : "",
            password : "cetsim",
            gender : $scope._selectOption.id
        };
        var _copy = $.extend({}, $scope.account);
        $scope.resetFormOnSubmitSuccess = false;

        $scope._submitInfo = "";
        $scope.submit = function () {
            $scope._submitInfo = "";
            $scope.account.gender = $scope._selectOption.id;

            studentService.addStudent($scope.account, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $log.log("add one student success");
                    commonService.showTip("添加学生成功");
                    if ($scope.resetFormOnSubmitSuccess) {
                        $scope.account = _copy;
                    }
                } else {
                    $log.warn("add one stutend error");
                    $log.log(res);
                    commonService.showTip("添加学生失败");
                }
            })
        }

}]);

/**
 * 管理员批量添加教师(该功能已被删除)
 * @deprecated
 */
cetsim.controller("addManyStudentsCtrl", ["$scope", "commonService", "$stateParams", "$log",
    function ($scope, commonService, $stateParams, $log) {

        $scope._submitInfo = "";
        $scope.file = "";

        $scope.submit = function () {
            $scope._submitInfo = "";
            commonService.formDataUpload(commonService.config.host + "/admin/addStudents.action", {
                file : $scope.file
            }, function (err, res) {
                $log.log(res);
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    commonService.showTip("批量导入成功");
                } else {
                    commonService.showTip("批量导入失败");
                }
            });
        }

}]);


/**
 * 导入试卷,导入试题公用这个 controller
 */
cetsim.controller("importPaperCtrl", ["$scope", "commonService", "userService", "$rootScope", "Upload", "$timeout", "$log", "$element",
    function ($scope, commonService, userService, $rootScope, Upload, $timeout, $log, $element) {
        $scope.file = "";
        $scope.isShowPanel = false;
        $scope._hostForIE = commonService.config.host;

        // 获取上传地址, 默认还是试卷上传地址
        function getUploadUrl () {
            try {
                var jDom = $($element),
                    hiddenInput = jDom.find("._uploadUrl");
                if (hiddenInput.length > 0) {
                    return hiddenInput.val();
                } else {
                    return "/api/paper/importPaper.action";
                }
            }catch (ex) {
                return "/api/paper/importPaper.action";
            }
        }
        var _user = userService.getUserModelFromLocalInfo();
            $scope.submit = function () {
                commonService.formDataUpload(new commonService.URI(commonService.config.host + getUploadUrl())
                    .addSearch("_showGlobalShadowWhenQuery", true).toString(), {
                    file: $scope.file,
                    teacherId: _user.id
                }, function (err, res) {
                    if (!err) {
                        _dealRes(res);
                    } else {
                        commonService.layerError("试卷导入失败")
                    }
                });
            };
        $scope.hidePanel = function () {
            $scope.isShowPanel = false;
        };

        var _dealRes = function (res) {
            if (res.code === commonService.config.ajaxCode.success) {
                // commonService.showTip("试卷导入成功");
                // commonService.layerClose($("#importPaperPanel"));
                commonService.viewOut($("#importPaperPanel"));

                if (/importPaperForIE/.test(location.href)) {
                    commonService.layerSuccess("提示", "导入试卷成功", true, "返回试卷列表");
                } else {
                    commonService.layerSuccess("试卷导入成功");
                }
                _clearFileComponent();

                $rootScope.$broadcast("event.refreshPaperListTable");
            } else {
                commonService.layerError("试卷导入失败, 失败原因 : " + res.msg);
            }
        };

        var _unbind = $rootScope.$on("event.showimportPaperPanel", function () {
            $scope.isShowPanel = true;
            commonService.showInCenterOnScreen($("#importPaperPanel"));
        });
        $scope.$on("$destroy", function () {
            _unbind();
        });

        $scope.__file = null;
        $scope.__errFiles = null;
        $scope._uploadFiles = function (file, errFiles) {
            $scope.__file = file;
            $scope.__errFiles = errFiles;
        };

        function _clearFileComponent() {
            $("#importPaperPanel .file-input-component").val("");
            $scope.__file = null;
            $scope.__errFiles = null;
        }
        $scope._clearFileComponent = _clearFileComponent;

        $scope.uploadFiles = function (file, errFiles) {
            if (file === undefined) {
                file = $scope.__file;
            }
            if (errFiles === undefined) {
                errFiles = $scope.__errFiles;
            }

            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                file.upload = Upload.upload({
                    url: commonService.getUrlWithShadowParam(commonService.config.host + getUploadUrl()),
                    data: {
                        file : file,
                        teacherId : _user.id
                    }
                });

                file.upload.then(function (res) {
                    $timeout(function () {
                        _dealRes(res.data);
                        $scope.hidePanel();
                    });
                }, function (res) {
                    $timeout(function () {
                        commonService.layerError("导入失败, 失败原因 : " + res.data.msg);
                        $scope.hidePanel();
                    });
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                        evt.loaded / evt.total));
                    $log.log("upload progress : \t" + file.progress);

                    // if (file.progress < 100) {
                    //     commonService.layerImportProgress(Math.min(100, parseInt(100.0 *
                    //         evt.loaded / evt.total)) + "%");
                    // } else {
                    //     commonService.layerClose($("#importProgressPanel"));
                    // }
                });

            } else {
                commonService.layerMsg("请选择文件");
            }
        };



        $scope.onClickCancel = function () {
            $scope._clearFileComponent();
        };
}]);


cetsim.controller("importPaperCtrlForIE7", ["$scope", "commonService", function ($scope, commonService) {
    $scope.uploadForIE7 = function () {
        alert(1);
        var form = $("#fileUpload");
        form.attr("action", commonService.config.host + "/api/paper/importPaper.action");
        form.submit();
    };
}]);


cetsim.controller("adminSettingCtrl", ["$scope", "commonService", "$timeout", function ($scope, commonService, $timeout) {
    $scope.title = "";
    $scope.items = [];
    var _unbind = $scope.$on("event.showPop.adminSettingsEditPanel", function () {
        commonService.getSystemSettingsInfo(function (err, res) {
            if (!err && res.code === commonService.config.ajaxCode.success) {
                $scope.items = res.data;
            } else {
                $scope.title = "系统设置加载失败，请刷新页面重试";
            }

            $timeout(function () {
                $(".eui-select").cssSelect();
            })
        })
    });
    $scope.$on("$destroy", function () {
        _unbind();
    });
    
    $scope.updateSettings = function () {
        var num = 0;
       $scope.items.forEach(function (item) {
           commonService.updateSystemSettingsInfo(item.configKey, item.configValue, function (err, res) {

               if (err || res.code !== commonService.config.ajaxCode.success) {
                   commonService.showTip(item.displayName + "信息更新失败");
               } else {
                   num ++;
                   if (num === $scope.items.length) {
                       commonService.layerClose($("#adminSettingsEditPanel"));
                       commonService.layerSuccess("系统配置信息修改成功");
                   }
               }
           })
       })
    }
}]);

cetsim.controller("attributeMangeCtrl", ["$scope", "$log", "commonService", function ($scope, $log, commonService) {
    $scope.leftList = [];
    $scope.rightList = [];
    $scope.total = "-";

    commonService.getInstitutesList(function (err, list) {
        if (!err) {
            var cut = Math.ceil(list.length / 2);
            $scope.leftList = list.slice(0, cut);
            $scope.rightList = list.slice(cut);
            $scope.total = list.length;
        } else {
            commonService.layerError("加载学院信息失败");
        }
    });

    $scope.getMajorsOfInstitute = function (institute, fn) {
        // TODO ceshi Code Follow
        var list = [],
            i = 5;
        while(i--) {
            list.push(institute + i);
        }
        fn(list);
    };
    
    $scope.ceshi = function () {
        $log.log($scope.leftList);
        $log.log($scope.rightList);
    }
}]);