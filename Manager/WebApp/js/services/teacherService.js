cetsim.service("teacherTaskService", ["$http", "commonService", "userService", function ($http, commonService, userService) {

    var task = {};
    var papers = [];
    var rooms = [];
    this.publishTask = function(task,papers,rooms,fn, isShowShadow){
        task.start_time = new Date(task.start_time);
        task.end_time = new Date(task.end_time);

        var url = commonService.config.host + '/api/teacher/publish.action';
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "POST",
            url : url,
            data:$.param({
                "_plan"  :JSON.stringify(task),
                "_rooms" :JSON.stringify(rooms),
                "_papers":JSON.stringify(papers)
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {            
            if (response.data.code === 1) {                
                fn(response.data.data, true);
                task = null;
                papers = [];
                rooms = [];
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("发布任务失败"), false);
        });
    }

    this.getPublishTask = function(){
        if(task == null){
            task = {
                teacher_id:commonService.Cookies('user_id')
            }
        }
        return task;
    }

    this.getPublishPaper = function(){
        return papers;
    }

    this.getPublishRoom = function(){
        return rooms;
    }

	this.getTaskList = function(studentId,pageIndex,pageSize,fn){		
		 $http({
            method : "GET",
            url : commonService.config.host + '/api/teacher/tasklist.action?'+
            $.param({
                teacherId : commonService.Cookies('user_id'),
                pageIndex : pageIndex || 1,
                pageSize: pageSize || 10
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {                
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("获取任务列表失败"), false);
        })
	}

    this.getRoomList = function(fn){
        $http({
            method : "GET",
            url : commonService.config.host + '/api/teacher/roomlist.action?'+
            $.param({
                teacherId : commonService.Cookies('user_id')
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {                
                fn(response.data.data, true);
            } else {
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("获取失败"), false);
        })
    }

    this.getPaperList = function(type, pageIndex, pageSize,fn, isShowShadow, paperName){
        // 为了兼容旧的代码, 可能传递 数字类型的 type, 现在 type 要求传递 cet4 或者 cet6
        if (type == "-1") {
            type = null;
        } else if (type == "0") {
            type = "CET4";
        } else if (type == "1") {
            type = "CET6";
        }

        var params = {
            paperTypeCode: type,
            pageIndex: pageIndex || 1,
            pageSize: pageSize || 10
        };
        if (paperName) {
            params.paperName = paperName;
        }
        commonService.removeKeyOfNullValue(params);

        var url = commonService.config.host + '/api/teacher/paperlist.action?' + $.param(params);

        if (isShowShadow) {
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
            fn(new Error("获取失败"), false);
        })
    };

    /**
     * 获取学生详情列表
     * @param planCode 测试计划code
     * @param name  模糊查询名称
     * @param pageIndex
     * @param pageSize
     * @param fn
     */
    this.getTaskDetail = function (planCode, name, pageIndex, pageSize, fn) {
        var url = commonService.config.host + '/api/teacher/taskdetail.action?' +
            $.param({
                planCode: planCode,
                name:name,
                pageIndex: pageIndex,
                pageSize: pageSize
            });
        url = commonService.getUrlWithShadowParam(url);

        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error("获取失败"), response);
        })
    };


    this.scoredistribution = function (plantCode, fn) {
        var url = commonService.config.host + '/api/teacher/scoredistribution.action?' +
            $.param({
                plantCode: plantCode
            });
        url = commonService.getUrlWithShadowParam(url);
        
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error("获取失败"), response);
        })
    };

    /**
     * 任务监控信息获取
     * @param taskId
     * @param pageIndex
     * @param pageSize
     * @param fn
     * @deprecated
     */
    this.getTaskMonitorInfo = function (taskId, pageIndex, pageSize, fn) {
        $http({
            method : "GET",
            url : commonService.config.host + '/api/teacher/studentmonitor.action?'+
            $.param({
                taskId : taskId,
                pageIndex : pageIndex,
                pageSize : pageSize
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error("获取失败"), response);
        })
    };

    /**
     * 任务监控信息获取
     * @param taskId
     * @param status
     * @param condition
     * @param pageIndex
     * @param pageSize
     * @param fn
     */
    this.getTaskMonitorInfo2 = function (taskId, status, condition, pageIndex, pageSize, fn) {
        var __config;
	// 这个地方这么写是因为 如果 status 为空数组， 那么status[]="" 并不会传到后台，然后后台接口参数不匹配会报错
        if (status.length === 0) {
            __config = {
                taskId : taskId,
                "status[]" : "",
                condition : condition,
                pageIndex : pageIndex,
                pageSize : pageSize
            };
        } else {
            __config = {
                taskId : taskId,
                status : status,
                condition : condition,
                pageIndex : pageIndex,
                pageSize : pageSize
            };
        }
        $http({
            method : "GET",
            url : commonService.config.host + '/api/teacher/studentmonitor.action?'+
            $.param(__config),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error("获取失败"), response);
        })
    };


    /**
     * 获取任务信息,注意与taskDetail方法不一样
     * @param taskId
     * @param fn
     */
    this.getTaskInfo = function (planCode, taskCode, fn) {
        var url = commonService.config.host + '/api/teacher/taskinfo.action?' +
            $.param({
                planCode: planCode,
                taskCode : taskCode
            });
        url = commonService.getUrlWithShadowParam(url);

        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            var modifiedData = {};
            response.data.data.forEach(function (item) {
                modifiedData[item.k.toLowerCase()] = item.v;
            });
            fn(null, response.data, modifiedData);
        }, function (response) {
            fn(new Error("获取失败"), response);
        })
    };
}]);