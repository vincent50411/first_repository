cetsim.controller('teacherTaskViewCtrl', ["$scope", "$timeout", "commonService", "teacherTaskService", "userService", "$log", "$stateParams", "commonService", "teacherService",
	function($scope, $timeout, $commonService, $taskService, userService, $log, $stateParams, commonService, teacherService) {
		$scope.title = "任务查看";
        $scope.taskName = "";
        $scope.paperName = "";
	    //配置
		$scope.p_pagesize = 30;
		//变量  
		$scope.p_current = 1;
		$scope.p_all_page = 1;
		$scope.pages = [];
		$scope.items = [];
		$scope._curTaskId = $stateParams.planid;	// task id 名称不准确，这里的plan id 对应task id
		$scope._curPlanId = $stateParams.planid;
		$scope._curPaperType = $stateParams.paperType;
		$scope._host = commonService.config.host;
		$scope.description = "";

		//查询条件
		$scope.queryConditions = {
			name : null
		};

        var curUser = userService.getUserModelFromLocalInfo();

		$scope.load_page = function(index) {
			if (index > $scope.p_all_page) {
				index = $scope.p_all_page;
			}
			if (index <= 0) {
                index = 1;
            }
			$scope.p_current = index;

			//增加一个名称模糊查询条件
			$taskService.getTaskDetail($stateParams.planid, $scope.queryConditions.name, $scope.p_current, $scope.p_pagesize, function(err, res) {
				if (!err && res.code === commonService.config.ajaxCode.success) {
					var paperTypeName = commonService.getPaperTypeNameByCode($scope._curPaperType);

					$log.log("Current PaperType : " + paperTypeName);
					// res.data.data.forEach(function (item, i) {
					// 	commonService.getRankOfScoreByCetType(item.max_score, paperTypeName, function (rank) {
					// 		res.data.data[i]["_rank"] = rank;
					// 		$log.log("score : " + item.max_score + " =>" + "level :" + rank);
					// 	});
					// });

					$scope.items = res.data.data;
					$scope.p_all_page = Math.ceil(res.data.total / $scope.p_pagesize);
				} else {
					commonService.layerError("加载列表失败")
				}
			});
		};

		$scope.load_page(1);

		/**
		 * 饼状图
		 */
		$taskService.scoredistribution($stateParams.planid, function (err, res) {
			$log.log("scoredistribution res:");
			$log.log(res);
			if (!err && res.code === commonService.config.ajaxCode.success) {
				$.each(res.data, function (i, item) {
					res.data[i].name = item.k;
					res.data[i].y = item.v;
					res.data[i].yInfo = item.v + "人";
				});
				commonService.highChartPie("task-view-pie-container", res.data, null);
			} else {
				$log.warn('scoredistribution error');
				commonService.showTip("获取成绩分布信息失败");
			}
		});

		/**
		 * 根据成绩等级获取相应的css 样式class
		 * default cls == ""
		 */
		$scope.getCssClsByRank = function (rank) {
			try {
				var _rank = rank.toUpperCase(),
					cls = "";
				switch (_rank) {
					case "A":
						cls = "color-green";
						break;
					case "B":
						cls = "color-yellow";
						break;
					case "C":
						cls = "color-orange";
						break;
					case "D":
						cls = "color-red";
						break;
				}
				return cls;
			} catch(ex) {
				$log.warn(ex.message);
				return "";
			}
		};

		$scope._taskInfo = {};

		$taskService.getTaskInfo($stateParams.planid, $stateParams.taskid, function (err, res) {
			if (!err && res.code === commonService.config.ajaxCode.success) {
				var filter = ["AVGSCORE", "MAXSCORE", "MINSCORE"];
				res.data.forEach(function (item) {

					if (filter.indexOf(item.k) !== - 1) {
						$scope._taskInfo[item.k] = item.v;
					} else {
						$scope._taskInfo[item.k] = item.v;
					}
					$scope._taskInfo["_" + item.k] = item.v;
				});
				$scope.$emit("event.getDescription");
			} else {
				// TODO
			}
		});

		var _unbindGetDesc = $scope.$on("event.getDescription", function () {
			commonService.getScoreLevelOf2CetTypes(true).then(function (response) {
				try {
					if (response.data.code === commonService.config.ajaxCode.success) {
						var cetTypeName = commonService.getPaperTypeNameByCode($scope._curPaperType).toUpperCase();
						var avgScore = parseFloat($scope._taskInfo["_AVGSCORE"]);
						response.data.data["SCORE_DESC"].forEach(function (item) {
							if (item.max > avgScore && item.min <= avgScore) {
								$scope.description = item.description;
							}
						})
					}
				} catch (ex) {
					$log.warn("加载评语失败, 失败原因:");
					$log.warn(ex);
					$scope.description = "";
				}
			})
		});
		$scope.$on("$destroy", function () {
			_unbindGetDesc();
		})

	}
])
	.controller('teacherTaskCtrl', ["$scope", "$timeout", "commonService", "teacherTaskService",
	function($scope, $timeout, $commonService, $taskService) {
		//配置
		$scope.total = 0;
		$scope.p_pagesize = 5;
		//变量  
		$scope.p_current = 1;
		$scope.p_all_page = 0;
		$scope.pages = [];

		$scope.load_page = function(index) {
			$scope.p_current = index;
			$taskService.getTaskList($commonService.Cookies('user_id'), $scope.p_current, $scope.p_pagesize, function(tasks) {
				$scope.tasks = tasks.data;
				$scope.total = tasks.total;
				$scope.pages = [];
				for (var i = 1; i <= tasks.pages; i++) {
					$scope.pages.push(i);
				};
				$scope.p_all_page = tasks.pages;
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
]).controller("classListCtrl", ["$scope", "classService", "userService", "commonService", "teacherService", "$log", "$rootScope",
	function ($scope, classService, userService, commonService, teacherService, $log, $rootScope) {
		$scope.classList = [];
		var curUser = userService.getUserModelFromLocalInfo();
		$scope.classList = [];
		$scope.curClassListPage = 1;
		$scope.classListMaxPage = 1;
		$scope.classListPageSize = 10;

		$scope.goClassListPage = function (page) {
			if (page <= 0) {
				page = 1;
			}
			if (page > $scope.classListMaxPage) {
				page = $scope.classListMaxPage;
			}
			$scope.curClassListPage = page;
			$scope.refreshClassList();
		};


		$scope.refreshClassList = function () {
			$log.log("-------------------------------");
			$log.log("班级列表查询:");
			$log.log("当前登陆老师ID:\t\t" + curUser.id);
			$log.log("page:\t\t\t\t" + $scope.curClassListPage);
			$log.log("size:\t\t\t\t" + $scope.classListPageSize);
			$log.log("-------------------------------");

			classService.getClassList(curUser.account, $scope.curClassListPage, $scope.classListPageSize, function (err, res) {
				if (!err && res.code === commonService.config.ajaxCode.success) {
					$scope.classList = res.data.data;
                    $scope.classListMaxPage = Math.ceil(res.data.total / $scope.classListPageSize);
				} else {
					// TODO teacher classList load error
				}
			});
		};
		$scope.refreshClassList();

		var _unbind = $rootScope.$on("event.refreshClassListTable", function () {
			$scope.refreshClassList();
		});
		$scope.$on("$destroy", function () {
			$log.log("event.refreshClassListTable is unbinded!");
			_unbind();
		});
		
		$scope.showManageClassPanel = function (id) {
			$rootScope.$broadcast("event.showManageClassPanel", id);
		}


}]).controller("ceshiListCtrl", ["$scope", "userService", "taskService", "commonService", "$log", "$rootScope",
	function ($scope, userService, taskService, commonService, $log, $rootScope) {

		$scope.tasks = [];
		$scope.curPage = 1;
		$scope.tasksMaxPage = 1;
		$scope.pageSize = 10;
		$scope._host = commonService.config.host;
		$scope.planProcessCode = commonService.planProcessCode;
		$scope.order = {
			column : null,
			code : null
		};
		$scope.conditionStatus = "";
		$scope.paperType = "";
		$scope.conditionBeginTime = "";
		$scope.conditionEndTime = "";
		$scope.conditionName = "";
		$scope.conditionStartTime = "";
		$scope.conditionEndTime = "";

		var curUser = userService.getUserModelFromLocalInfo();
		$scope._queryTasks = function (id, pageIndex, pageSize) {
			if ($scope.conditionStartTime && $scope.conditionEndTime) {
				if (moment($scope.conditionStartTime).isAfter($scope.conditionEndTime)) {
					commonService.layerMsg("开始时间不可以晚于结束时间");
					return;
				}
			}
			var conditionStartTime,
				conditionEndTime;

			if ($scope.conditionStartTime) {
				conditionStartTime = $.trim($scope.conditionStartTime + " 00:00:00");
			}
			if ($scope.conditionEndTime) {
				conditionEndTime = $.trim($scope.conditionEndTime) + " 23:59:59";
			}

			$scope.curPage = pageIndex;
			taskService.getTaskList(id, pageIndex, pageSize, function (err, res) {
				if (!err && res.code === commonService.config.ajaxCode.success) {
					var list = res.data.data;
					list.forEach(function (item, i) {
					    if ($.isNumeric(item.paper_type)) {
                            list[i]["paper_type"] = parseInt(item.paper_type);
                        }
						if (item.paper_type === commonService.config.paperType.cet4) {
							list[i]["_paperTypeName"] = "CET4";
						} else if (item.paper_type === commonService.config.paperType.cet6){
							list[i]["_paperTypeName"] = "CET6";
						} else {
							try {
								list[i]["_paperTypeName"] = item.paper_type.toUpperCase();
							} catch (ex){
								list[i]["_paperTypeName"] = "";
							}
						}
						if (item["finish_count"]  == 0) {
							list[i]["average_score"] = null;
							list[i]["max_score"] = null;
						}
					});

					list.forEach(function (item) {
						commonService.getRankOfScoreByCetType(item.max_score, item.paper_type, function (level) {
							item["_max_scoreLevel"] = level;
						});
						commonService.getRankOfScoreByCetType(item.average_score, item.paper_type, function (level) {
							item["_average_scoreLevel"] = level;
						});
					});

					commonService.getServerSyncTime(function (err, serverTime) {
						list.forEach(function (item) {
							if (moment(item.start_time).isAfter(serverTime)) {
								item["_process_code"] = commonService.planProcessCode.NOTBEGIN;
							} else if (moment(item.start_time).isBefore(serverTime) && moment(item.end_time).isAfter(serverTime)) {
								item["_process_code"] = commonService.planProcessCode.PROCESSING;
							} else if (moment(item.end_time).isBefore(serverTime)){
								item["_process_code"] = commonService.planProcessCode.FINISHED;
							}
						})
					});

					$scope.tasks = list;
					$scope.tasksMaxPage = Math.ceil(res.data.total / pageSize);
					$log.log("Plan list 如下 ：");
					$log.log(list);
				} else {
					$log.warn("load teacher tasks error");
					commonService.showTip("加载测试列表信息失败");
				}
			}, {
				orderColumnName : $scope.order.column,
				orderCode : $scope.order.code,
				status : ($scope.conditionStatus === "" ? null : $scope.conditionStatus),
				paperType : $scope.paperType || null,
				beginTime : conditionStartTime || null,
				endTime : conditionEndTime || null,
				name : $scope.conditionName || null
			});
		};
		$scope._queryTasks(curUser.account, $scope.curPage, 10);


		$scope.refresh = function (page) {
			$scope.curPage = page;
			$scope._queryTasks(curUser.account, $scope.curPage, 10);
		};

		$scope.refreshToPage1 = function () {
			$scope.refresh(1);
		};

		$scope.goPage = function (page) {

			if (page > $scope.tasksMaxPage) {
				page = $scope.tasksMaxPage;
			}
			if (page <= 0) {
				page = 1;
			}
			$scope.curPage = page;
			$scope._queryTasks(curUser.account, $scope.curPage, $scope.pageSize);
		};

		$scope.editPlan = function (plan) {
			$rootScope.$broadcast("event.editExistedPlan", plan);
		};

		$scope.deletePlan = function (plan) {
			commonService.getServerTime(function (serverTime) {
				if (moment(plan.start_time).isBefore(serverTime)) {
					commonService.showTip("该计划任务已开始，不可以删除。（请刷新页面查看计划任务最新状态）")
					return;
				}

				commonService.layerConfirm("确定删除该计划任务吗?", function () {
					taskService.deletePlan(plan, function (err, res) {
						if (!err && res.code === commonService.config.ajaxCode.success) {
							commonService.layerMsg("删除成功");
							$scope._queryTasks(curUser.account, $scope.curPage, 10);
						} else {
							commonService.layerMsg("删除失败, 请待会重试");
						}
					})
				})
			});
		};

		var _unbindRefreshCeshiListEvent = $rootScope.$on("event.refreshCeshiList", function () {
			$scope._queryTasks(curUser.account, $scope.curPage, 10);
		});
		$scope.$on("$destroy", function () {
			$log.log("destroy event.refreshCeshiList");
			_unbindRefreshCeshiListEvent();
		})
}]);


cetsim.controller("publishCeshiCtrl", ["$scope", "userService", "taskService", "commonService", "$log", "teacherService", "classService", "teacherTaskService", "$timeout", "$location", "$rootScope",
	function ($scope, userService, taskService, commonService, $log, teacherService, classService, teacherTaskService, $timeout, $location, $rootScope) {

		var curUser = userService.getUserModelFromLocalInfo();
		$scope.editedTask = {};
		$scope.selectPaper = {};
		var _msgWhenBeginTimeEarlierThanServerTime = "请将开始时间往后推迟, 否则考生无法开始考试。(当前服务端时间已经过了您选择的开始时间)";


		/**
		 *    取消选择的试卷 (因为试卷单选, 所以将所有radio置为 false状态)
		 */
		$scope.deleteSelectedPaper = function () {
			$scope.selectPaper = {};
			$rootScope.$broadcast("event.cancelSelectedPaper");

			$log.log("删除试卷后的任务对象如下:");
			$log.log($scope.editedTask);
		};
		/**
		 * 发布测试面板数据初始化
		 * @param obj
		 * @private
		 */
		var _init = function (obj) {
			$log.log("发布测试面板数据初始化");
			$scope.editedTask = {
				_plan: {
					publisher: curUser.account,
					name: "",
					startTime: "",
					endTime: "",
					requirement: ""
				},
				_papers: [],
				_rooms: [] // 教师选择要发布任务的班级列表
			};
			$scope.deleteSelectedPaper();
		};
		_init();

		$scope._submitInfo = "";

        //没有学生的班级列表, 作为提示信息
        $scope.notStuClassList = [];

		var _unbind = $rootScope.$on("event.paperIsSelected", function (ev, paper) {
			$scope.selectPaper = paper;
		});

		var _unbindOnCloseEditCeshiPanel = $rootScope.$on("event.hidepop.publishceshipanel", function () {
			$scope.$apply(function () {
				_init();
			})
		});
		
		$scope.$on("$destroy", function () {
			_unbind();
			_unbindOnCloseEditCeshiPanel();
		});




		$scope.publishTask = function ()
		{
			if ($scope.editedTask._plan.name === "") {
				commonService.showTip("请填写名称");
				return;
			}
			
			if ($scope.editedTask._plan.startTime === "" || $scope.editedTask._plan.endTime === "") {
				commonService.showTip("请填写开始或结束时间");
				return;
			}

			if (moment($scope.editedTask._plan.startTime).isAfter($scope.editedTask._plan.endTime)) {
				commonService.showTip("开始时间不可以晚于结束时间");
				return;
			}

			commonService.getServerTime(function (serverTime) {

				// if (moment($scope.editedTask._plan.startTime).isBefore(serverTime)) {
				// 	commonService.showTip(_msgWhenBeginTimeEarlierThanServerTime);
				// 	return;
				// }

				if (!$scope.selectPaper.code) {
					commonService.showTip("请选择试卷");
					return;
				}
				$scope.editedTask._papers = [$scope.selectPaper.code];

				if ($scope.editedTask._rooms.length <= 0) {
					commonService.showTip("请选择班级");
					return;
				}


				$scope.editedTask._plan.publisher = curUser.account;
				classService.getListOfNoStudents($scope.editedTask._rooms, function (err, emptyList) {
					if (err) {
						commonService.showTip("发布任务失败, 请联系管理员。错误信息：" + err.message);
					} else {
						__dealAfterEmptyRoomListReturn(emptyList);
					}
				});
				
			});



		};

		function __dealAfterEmptyRoomListReturn(list) {
			if (list.length === 0) {
				$log.log("所选班级都存在学生, 准备发布任务, 任务对象如下:");
				$log.log($scope.editedTask);

				teacherTaskService.publishTask($scope.editedTask._plan, $scope.editedTask._papers, $scope.editedTask._rooms, function (res, bool) {
					if (bool) {
						$log.log("publish task success");
						layer.closeAll();
						commonService.layerSuccess("提示", "发布任务成功");
						_init();
						$rootScope.$broadcast("event.refreshCeshiList");

					} else {
						$log.warn("publish task error");
						commonService.layerError("发布测试失败");
					}
				}, true)
			} else {
				var idChangeToNameList = [];
				list.forEach(function (item) {
					$scope.classOwned.forEach(function (room) {
						if (room.id === item) {
							idChangeToNameList.push(room.name);
						}
					})
				});
				commonService.showTip(idChangeToNameList.join("，") + "班级内没有学生，请先添加学生或更换班级再发布测试任务哦~");
			}
		}

		$scope.whenClickCancelOnPublishTaskPanel = function () {
			_init();
		};

		/**
		 * 教师所拥有的班级列表
		 * @type {Array}
		 */
		$scope.classOwned = [];
		classService.getAllClassList(curUser.account, function (err, res) {
			if (!err && res.code === commonService.config.ajaxCode.success) {
				$scope.classOwned = res.data;
			} else {
				$log.warn("load teacher owned classes error");
				commonService.showTip("加载教师班级列表失败");
			}
		});

		/**
		 * 切换班级选择
		 * @param classId
		 * @param className 班级名称
		 * @deprecated
		 */
		$scope.toggleClassSelection = function (classId, className)
		{
			var idx = $scope.editedTask._rooms.indexOf(classId);

			if (idx > -1) {
				$scope.editedTask._rooms.splice(idx, 1);
			}
			else {
				$scope.editedTask._rooms.push(classId);
			}
			$log.log($scope.editedTask._rooms);
		};

		var _unbindCeshiTimePicked = $scope.$on("event.publish.ceshi.time.picked", function () {
			if ($scope.editedTask._plan.startTime && $scope.editedTask._plan.endTime) {
				if (moment($scope.editedTask._plan.startTime).isAfter($scope.editedTask._plan.endTime)) {
					commonService.layerMsg("开始时间不可以晚于结束时间");
				}
			}
		});
		var _unbindeditExistedPlan = $scope.$on("event.editExistedPlan", function (ev, plan) {
			_init();
			taskService.getPlanClassList(plan, function (err, res) {
				if (!err && res.code === commonService.config.ajaxCode.success) {
					var classList = res.data;
					var _rooms = [];
					classList.forEach(function (item) {
						_rooms.push(item.code);
					});

					$scope.editedTask._plan = {
						code : plan.plan_code,
						name : plan.plan_name,
						requirement : plan.requirement,
						startTime :  moment(plan.start_time).format("YYYY-MM-DD HH:mm:ss"),
						endTime :  moment(plan.end_time).format("YYYY-MM-DD HH:mm:ss")
					};
					$scope.editedTask._rooms = _rooms;
					$scope.selectPaper = {
						code : plan.paper_code,
						name : plan.paper_name
					};
					commonService.layerOpen({
						title: "发布测试",
						area: "500px",
						content: $("#publish-ceshi-panel"),
						end : function () {
							$scope.$apply(function () {
								_init();
							})
						}
					});
				} else {
					commonService.layerError("加载信息时出错,无法编辑计划任务");
				}
			});

		});
		$scope.$on("$destroy", function () {
			_unbindCeshiTimePicked();
			_unbindeditExistedPlan();
		});

		$scope.bind97DatePick = function() {
			$("#publishCeshiBeginTime").on("click", function (ev) {
				WdatePicker({
					el : "publishCeshiBeginTime",
					dateFmt:'yyyy-MM-dd HH:mm:ss',
					onpicked : function (ev) {
						$scope.$apply(function () {
							$scope.editedTask._plan.startTime = ev.srcEl.value;
							$scope.$emit("event.publish.ceshi.time.picked");
						})
					}
				});
				// $("#publishCeshiBeginTime").off("click");
			});
			$("#publishCeshiEndTime").on("click", function () {
				WdatePicker({
					el : "publishCeshiEndTime",
					dateFmt:'yyyy-MM-dd HH:mm:ss',
					onpicked : function (ev) {
						$scope.$apply(function () {
							$scope.editedTask._plan.endTime = ev.srcEl.value;
							$scope.$emit("event.publish.ceshi.time.picked");
						})
					}
				});
				// $("#publishCeshiEndTime").off("click");
			});
		};
		$scope.bind97DatePick();
		
}]);

cetsim.controller("selectPaperCtrl", ["$scope", "userService", "taskService", "commonService", "$log", "teacherService", "classService", "teacherTaskService", "$timeout", "$location", "$rootScope",
	function ($scope, userService, taskService, commonService, $log, teacherService, classService, teacherTaskService, $timeout, $location, $rootScope) {

		$scope.allPapers = [];
		$scope.curPaperPage = 1;
		$scope.maxPaperPage = 1;
		$scope.paperType = "-1";
		$scope.paperListLimit = 10;

		$scope.isInit = false;
		$scope.paperName = "";

		// $scope._selectedPapersNamesArr = [];
		// $scope._selectedPapersNames = "";

		$scope._selectPaper = {};

		/**
		 * 加载试卷
		 */
		$scope._queryPapersList = function (page) {
			if (page !== undefined) {
				$scope.curPaperPage = page;
			}
			teacherTaskService.getPaperList($scope.paperType, $scope.curPaperPage, $scope.paperListLimit, function (res, bool) {
				if (bool) {
					var list = res.data;
					// list.forEach(function (item, i) {
					// 	list[i]["_checked"] = false;
                    //
					// 	commonService.getRankOfScoreByCetType(item.max_score, item.type, function (level) {
					// 		item["_max_scoreLevel"] = level;
					// 	});
					// 	commonService.getRankOfScoreByCetType(item.average_score, item.type, function (level) {
					// 		item["_average_scoreLevel"] = level;
					// 	});
					// });
					$scope.allPapers = list;
					$scope.maxPaperPage = Math.ceil(res.total / $scope.paperListLimit);

					$log.log("-----------------------------------");
					$log.log("试卷加载成功, 试卷列表如下");
					$log.log(list);
					$log.log("-----------------------------------");
				} else {
					commonService.layerError("加载试卷失败");
				}
			}, true, $scope.paperName || null);
		};

		$scope.goPage = function (page) {

			if (page > $scope.maxPaperPage) {
				page = $scope.maxPaperPage;
			}
			if (page < 1) {
				page = 1;
			}
			$scope.curPaperPage = page;
			$scope._queryPapersList();
		};


		// // 试卷多选使用
		// $scope.togglePaperSelect = function (paperId, name) {
		// 	_toggleArrItem($scope.editedTask._papers, paperId);
		// 	_toggleArrItem($scope._selectedPapersNamesArr, name);
		// 	$scope._selectedPapersNames = $scope._selectedPapersNamesArr.join(",");
		// 	$log.log("Selected papers id and names:");
		// 	$log.log($scope.editedTask._papers);
		// 	$log.log($scope._selectedPapersNamesArr);
        //
		// };

		// 试卷单选 采用这个方法
		$scope.selectThisPapaer = function (paper) {
			$scope._selectPaper = paper;

			$rootScope.$broadcast("event.paperIsSelected", paper);
			$log.log("---Selected papers id and names:---");
			$log.log("paper id : " + paper.id);
			$log.log("paper name : " + paper.name);
			$log.log("------------------------------------")
		};



		// function _toggleArrItem(arr, item) {
		// 	var idx = arr.indexOf(item);
		// 	if (idx > -1) {
		// 		arr.splice(idx, 1);
		// 	}
		// 	else {
		// 		arr.push(item);
		// 	}
		// }
        //
		// $scope.isThisPaperChecked = function (id) {
		// 	var bool = false;
		// 	$.each($scope.editedTask._papers, function (i, item) {
		// 		if (item.id === parseInt(id)) {
		// 			bool = true;
		// 		}
		// 	});
		// 	return bool;
		// };


		var _unbind = $rootScope.$on("event.showPop.selectPaperPanel", function () {
			if (!$scope.isInit) {
				$scope.isInit = true;
				$scope._selectPaper = {};
				$scope._queryPapersList();
			} else {
				$scope._queryPapersList();
			}
		});
		var _unbind2 = $rootScope.$on("event.cancelSelectedPaper", function () {
			$scope._selectPaper = {};
		});
		$scope.$on("$destroy", function () {
			_unbind();
			_unbind2();
		})
}]);

cetsim.controller("addOneStudentForClassCtrl", ["$scope",  "$stateParams", "classService", "$log", "commonService", "$state", "$rootScope", "$timeout", "userService",
	function ($scope, $stateParams, classService, $log, commonService, $state, $rootScope, $timeout, userService) {
		$scope.title = "添加学生";

		var _init = function () {
			$scope.account = {
				account : "",
				name : ""	// 不传服务端报错
			};
		};
		$scope.account = {};
		_init();

		var _unbind = $rootScope.$on("event.showAddOneStudentForClassPanel", function (ev, id) {
			$log.log("Now is addStudentPanel showing for classId : " + id);
			$scope.classId = id;
			$scope.isHaveThisAccount = null;
		});

		var _unbindEndPop = $rootScope.$on("event.cancelpop.addOneStudentForClassPanel", function (ev, id) {
			_init();
		});

		$scope.$on("$destroy", function () {
			_unbind();
			_unbindEndPop();
		});

		$scope.submit = function () {

			var validation = [{
				obj: $scope.account.account,
				name : "账号",
				filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:10', 'validationCetsimAccount']
			}];

			if (!commonService.formValidate(validation)) {
				return;
			}


			classService.addStudent($stateParams.classId, $scope.account, function (err, res) {
				if (!err) {
					if (res.code === commonService.config.ajaxCode.success) {
						$log.log("add one student success");
						// commonService.showTip("添加学生成功");
						var url;
						$state.get().forEach(function (item) {
							if (item["name"] === "teacher.addOneStudentForClass") {
								url = item.url;
								url = "#" + url.replace(":classId", $stateParams.classId);
							}
						});
						layer.closeAll();
						commonService.layerSuccess("提示", "添加学生成功");
						_init();
						$rootScope.$broadcast("event.refreshStudentsListOfClass");
					} else {
						$log.log("add one student error");
						commonService.showTip("添加学生失败,失败信息:" + res.msg);
					}
				} else {
					$log.warn("add one stutend error");
					$log.log(res);
					commonService.showTip("添加学生失败");
				}
			}, true)
		};


		$scope.clickCancelWHenAddStudentForClass = function () {
			_init();
		}

}]);

/**
 * 教师批量导入学生
 */
cetsim.controller("addManyStudentsForClassCtrl", ["$scope", "commonService", "$stateParams", "$log", "$rootScope", "Upload", "$timeout",
    function ($scope, commonService, $stateParams, $log, $rootScope, Upload, $timeout) {
        $scope.title = "批量添加学生";
        $scope.uploadAction = "";
        $scope.getManyStudentsImportUrl = function () {
            return commonService.config.host + commonService.config.studentsExcelImportUrl;
        };
        $scope.uploadAction =  $scope.getManyStudentsImportUrl();
        $scope.classId = $stateParams.classId;
		$scope._submitInfo = "";
		$scope.file = "";
		$scope._host = commonService.config.host;
		
		$scope.submit = function () {
			$scope._submitInfo = "";
			commonService.formDataUpload(commonService.getUrlWithShadowParam($scope.getManyStudentsImportUrl()), {
				classCode : $scope.classId,
				file : $scope.file
			}, function (err, res) {
				$log.log("批量导入学生服务端的返回信息:");
				$log.log(res);
				if (!err) {
					_dealRes(res);
				} else {
					commonService.layerError("批量导入学生失败");
				}
			});
		};

		var _dealRes = function (res) {
			res.code = parseInt(res.code);
			switch (res.code) {
				case commonService.config.ajaxCode.success:
					// commonService.layerClose($("#addManyStudentsForClassPanel"));
					commonService.viewOut($("#addManyStudentsForClassPanel"));
					$rootScope.$broadcast("event.refreshStudentsListOfClass");

					if (/importStudents/.test(location.href)) {
						commonService.layerSuccess("提示", "批量导入学生成功", true, "返回班级学生列表");
					} else {
						commonService.layerSuccess("批量导入学生成功");
					}
					break;
				case commonService.config.ajaxCode.successButInfo:
					// commonService.layerClose($("#addManyStudentsForClassPanel"));
					commonService.viewOut($("#addManyStudentsForClassPanel"));
					$rootScope.$broadcast("event.refreshStudentsListOfClass");

					if (/importStudents/.test(location.href)) {
						commonService.layerSuccess("提示", res.msg, true, "返回班级学生列表");
					} else {
						commonService.layerSuccess(res.msg);
					}
					break;
				default:
					commonService.layerError("批量导入学生失败, 失败原因:" + res.msg);
					break;
			}
		};

		$scope._clearFileComponent = function () {
			$scope.__file = null;
			$scope.__errFiles = null;
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
					url: commonService.getUrlWithShadowParam($scope.getManyStudentsImportUrl()),
					data: {
						file: file,
						classCode : $scope.classId
					}
				});

				file.upload.then(function (res) {
					$scope._clearFileComponent();
					$timeout(function () {
						_dealRes(res.data);
					});
				}, function (response) {
					commonService.layerError("批量导入学生失败, 失败原因:" + response.data.msg);
				}, function (evt) {
					file.progress = Math.min(100, parseInt(100.0 *
						evt.loaded / evt.total));
				});
			} else {
				commonService.layerMsg("请选择文件");
			}
		};

		$scope.onClickCancel = function () {
			$scope._clearFileComponent();
		}
}]);


// cetsim.controller("taskMonitorCtrl", ["$scope", "$stateParams", "teacherTaskService", "$stateParams", "$log", "commonService", "$interval",
// 	function ($scope, $stateParams, teacherTaskService, $stateParams, $log, commonService, $interval) {
// 		$scope.title = "作业监控";
// 		$scope._defaultAllStatus = [0,1,2];
// 		$scope.status = $scope._defaultAllStatus;
// 		$scope.condition = "";
// 		$scope.curPage = 1;
// 		$scope.maxPage = 1;
// 		$scope.pageSize = 10;
// 		$scope.refreshInterval = 5; // 5秒刷新一下页面数据
//
// 		$scope.isHaveData = false;
// 		$scope.totalJoinedStudents = "获取中..";
// 		$scope.finishedStudentsNum = "获取中..";
// 		$scope._unlogin = "获取中";
// 		$scope._examing = "获取中";
// 		$scope._success = "获取中";
// 		$scope._taskInfo = {};
// 		$scope.records = [];
//
//
// 		$scope.toggleStatus = function (status) {
// 			if ($scope.status.indexOf(status) === -1) {
// 				$scope.status.push(status);
// 			} else {
// 				$scope.status.splice($scope.status.indexOf(status), 1);
// 			}
// 		};
//
// 		$scope.toggleQueryStatusOf = function (status) {
// 			$scope.toggleStatus(status);
// 			$scope.refresh(1);
// 		};
//
// 		$scope.refresh = function (page) {
// 			// if page is set, means set curPage = page, then refresh the data of page.
// 			if (page !== undefined) {
// 				$scope.curPage = page;
// 			}
// 			var _status = JSON.stringify($scope.status);
// 			$log.log("----------------------------------");
// 			$log.log("查询条件如下");
// 			$log.log("taskid : " + $stateParams.taskid);
// 			$log.log("status : " + _status);
// 			$log.log("curPage : " + $scope.curPage);
// 			$log.log("pageSize : " + $scope.pageSize);
// 			$log.log("----------------------------------");
// 			teacherTaskService.getTaskMonitorInfo2($stateParams.taskid, $scope.status, $scope.condition, $scope.curPage, $scope.pageSize, function (err, res) {
//
// 				if (!err && res.code === commonService.config.ajaxCode.success) {
// 					var _list = res.data.data;
// 					_list.forEach(function (item, i) {
// 						try {
// 							_list[i]["_examStateName"] = commonService.getExamStateByCode(item.examState);
// 							_list[i]["_flowStateName"] = commonService.getFlowStateByCode(item.flowState);
// 						} catch(ex) {
// 							if (item) {
// 								$log.log(ex.message);
// 							}
// 						}
// 					});
// 					$scope.records = _list;
// 					$scope.maxPage = Math.ceil(res.data.total / $scope.pageSize);
// 					$scope.isHaveData = ($scope.records.length  > 0);
// 				} else {
// 					commonService.closeAllPanel();
// 					$scope.tipPanelIndex = commonService.showTip("获取监控信息失败");
// 				}
// 			});
// 			teacherTaskService.getTaskInfo($stateParams.taskid, function (err, res, modifiedData) {
// 				if (!err && res.code === commonService.config.ajaxCode.success) {
// 					$scope.totalJoinedStudents = modifiedData.total;
// 					$scope.finishedStudentsNum = modifiedData.success;
// 					$scope._unlogin = modifiedData.unlogin;
// 					$scope._examing = modifiedData.examing;
// 					$scope._success = modifiedData.success;
//
// 					$scope._taskInfo = modifiedData;
// 				} else {
// 					commonService.showTip("获取参加总人数与完成人数信息失败");
// 				}
// 			})
// 		};
// 		$scope.refresh();
//
// 		$scope.goPage = function (page) {
//
// 			if (page > $scope.maxPage) {
// 				page = $scope.maxPage;
// 			}
// 			if (page <= 0) {
// 				page = 1;
// 			}
// 			$scope.curPage = page;
// 			$scope.refresh();
// 		};
//
// 		var _interval = $interval(function () {
// 			$scope.refresh();
// 		}, $scope.refreshInterval * 1000);
//
// 		$scope.$on('$destroy',function(){
// 			if(_interval)
// 				$interval.cancel(_interval);
// 		});
// 		// $stateParams.planid;
// 		// $stateParams.taskid;
// }]);

cetsim.controller("createClassCtrl", ["$scope", "userService", "$log", "teacherService", "commonService", "$rootScope",
	function ($scope, userService, $log, teacherService, commonService, $rootScope) {

		var curUser = userService.getUserModelFromLocalInfo();

		$scope.createdClass = {
			name : "",
			creater : curUser.account,
			teacherName : curUser.name
		};

		$scope.submitClass = function () {

            _func();

		};

		var _func = commonService._.debounce(function _func() {

			var validation = [{
				obj: $scope.createdClass.name,
				name : "班级名称",
				filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:20', 'validationNoSpecialChars']
			}];

			if (!commonService.formValidate(validation)) {
				return;
			}

            teacherService.createClass($scope.createdClass, function (err, res) {
                if (!err) {
                	if(res.code === commonService.config.ajaxCode.success) {
						layer.closeAll();
						$log.log("create class success");
						commonService.layerSuccess("提示", "创建班级成功");
						$scope.createdClass.name = "";
						$rootScope.$broadcast("event.refreshClassListTable");
					} else {
						$log.warn("create class error, error message : " + res.msg);
						commonService.layerError("提示", "创建班级失败, 失败原因：" + res.msg);
					}
                } else {
                    $log.warn("create class error");
                    commonService.layerError("提示", "创建班级失败");
                }
            })
        }, 300);

		$scope.removeClassName = function () {
			$scope.createdClass.name = "";
		};

		var _unbindClearClassNameInfo = $scope.$on("event.cancelpop.createClassPanel", function () {
			$scope.$apply(function () {
				$scope.createdClass.name = "";
			})
		});
		$scope.$on("$destroy", function () {
			_unbindClearClassNameInfo();
		})
}]);

cetsim.controller("teacherClassManageCtrl", ["$scope", "userService", "$log", "teacherService", "commonService", "$stateParams", "classService", "$rootScope", "$timeout",
	function ($scope, userService, $log, teacherService, commonService, $stateParams, classService, $rootScope, $timeout) {
		/**
		 * 根据班级id加载学生
		 * @param id
		 */
		$scope.loadStudentsOfClass = function () {
			$log.log("--------------------------------------");
			$log.log("班级加载学生条件:");
			$log.log("班级id:" + $scope.curQueryClassId);
			$log.log("page:" + $scope.curStudentsListPage);
			$log.log("pageSize:" + $scope.studentsListPageSize);
			$log.log("--------------------------------------");

			classService.loadStudentsOfClass($scope.curQueryClassId, $scope.curStudentsListPage, $scope.studentsListPageSize, function (err, res) {
				if (!err && res.code === commonService.config.ajaxCode.success) {
					$scope.currentShowUsers = res.data.data;
					$scope.studentsListMaxPage = Math.ceil(res.data.total / $scope.studentsListPageSize);
					if ($scope.currentShowUsers.length  === 0 && $scope.curStudentsListPage === 1 && !$scope.userFilter) {
						$scope.isExistedStudents = false;
					} else {
						$scope.isExistedStudents = true;
					}
				} else {
					$log.warn("load students by class id error");
					$scope.isExistedStudents = false;
				}
			}, true, $scope.userFilter)
		};

		$scope.goStudentsListPage = function (page) {
			if (page > $scope.studentsListMaxPage) {
				page = $scope.studentsListMaxPage;
			}
			if (page < 1) {
				page = 1;
			}
			$scope.curStudentsListPage = page;
			$scope.loadStudentsOfClass();
		};


		/**
		 * @deprecated
		 * @return {string}
		 */
		$scope.getStudentsImportTemplateDownloadUrl = function () {
			return commonService.config.host + commonService.config.studentsImportTemplateDownloadUrl;
		};

		/**
		 * 班级管理 注意数据数据初始化
		 * @param id
		 */
		$scope.showManageClasssPanel = function (id) {
			$scope.isShowManageClassPanel = true;
			$scope.curQueryClassId = id;
			$scope.curStudentsListPage = 1;
			$scope.loadStudentsOfClass();
		};


		/**
		 * 移除学生
		 * @param userid
		 * @param classid
		 */
		$scope.removeStudentFromClass = function (studentId, classId) {
		    commonService.layerConfirm("确认将该学生从本班级中移除吗？移除后该学生不能够进行本班级的测试了喔~", function () {
                teacherService.removeStudentFromClass(studentId, classId, function (err, res) {
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        commonService.layerSuccess("移除学生成功");
                        $scope.loadStudentsOfClass();
                    } else {
                        commonService.layerError("移除学生失败");
                    }
                })
            });
		};

		$scope._className = "";
		$scope.getClassName = function (teacherId, classId) {
			classService.getClassNameById(teacherId, classId, function (data) {
				if (data) {
					$scope._className = data;
				}
			})
		};

		$scope.curStudentsListPage = 1;
		$scope.studentsListMaxPage = 1;
		$scope.studentsListPageSize = 10;
		$scope.curQueryClassId = null;
		$scope.userFilter = "";

		$scope.isExistedStudents = undefined;
		$scope.currentShowUsers = [];
		var classId = $stateParams.classId;
		$scope.__classId = $stateParams.classId;
		var curUser = userService.getUserModelFromLocalInfo();

		$scope.showManageClasssPanel(classId);
		$scope.getClassName(curUser.account, classId);


		var _unbindRefreshStudentsListOfClass = $rootScope.$on("event.refreshStudentsListOfClass", function () {
			$scope.loadStudentsOfClass();
		});
		$scope.$on('$destroy', function () {
			_unbindRefreshStudentsListOfClass();
		});

        $scope.showEditStudentPanel = function (id) {
            $rootScope.$broadcast("event.showEdiStudentOfClassPanel", id);
        };

		$scope.showAddOneStudentForClassPanel = function () {
			$rootScope.$broadcast("event.showAddOneStudentForClassPanel", classId);
		};

        $timeout(function () {
            $('.eui-select').cssSelect();
        });
		// var unbind = $rootScope.$on("event.showManageClassPanel", function (ev, id) {
        //
		// });
		// $scope.$on('$destroy', unbind);
}]);

cetsim.controller("editStudentCtrl", ["$scope", "userService", "commonService", "$rootScope", "$timeout", "$log",
    function ($scope, userService, commonService, $rootScope, $timeout, $log) {

        $scope.user = {
			institution : "",
			grade : "",
			major : ""
		};
        $scope._userGender = userService.userGender;


        $scope.update = function () {

			var validation = [{
				obj: $scope.user.name,
				name : "账号",
				filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:10', 'validationNoSpecialChars']
			},{
				obj: $scope.user.telephone,
				name : "联系方式",
				filters: ['validationTelephoneNotStrict']
			}];

			if (!commonService.formValidate(validation)) {
				return;
			}

			$scope.user.role = userService.userRole.student;
            userService.updateUserInfo($scope.user, function (err, res) {
                if (!err) {
                    if (res.code === commonService.config.ajaxCode.success) {
                        commonService.layerClose($("#editStudentOfClassPanel"));
                        commonService.layerSuccess("提示", "更新学生信息成功");
                        $rootScope.$broadcast("event.refreshStudentsListOfClass");
                    } else {
                        commonService.layerError("更新学生信息失败,失败原因:" + res.msg);
                    }
                } else {
                    commonService.layerError("更新学生信息失败")
                }
            }, true);
        };

        var _unbind = $rootScope.$on("event.showEdiStudentOfClassPanel", function (ev, id) {
            commonService.layerOpen({
                title : "编辑学生信息",
                content : $("#editStudentOfClassPanel"),
                area : "480px"
            });
            $scope.user = {};
            $scope.isAjaxing = true;

            userService.getUserInfoFromServer({
            	account : id,
				role : userService.userRole.student
			}, function (err, res) {
				$scope.isAjaxing = false;

                if (!err && res.code === commonService.config.ajaxCode.success) {
                    $scope.user = res.data;
					$timeout(function () {
						$('.eui-select').cssSelect();
					})
                } else {
                    commonService.showTip("学生信息加载失败");
                }

            }, true);

        });
        $scope.$on("$destroy", function () {
            _unbind();
        });

        $timeout(function () {
            $('.eui-select').cssSelect();
        });

}]);