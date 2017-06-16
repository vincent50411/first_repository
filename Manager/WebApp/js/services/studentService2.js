cetsim.service("studentService", ["$http", "commonService", function ($http, commonService) {


    /**
     * 根据条件查询学生
     * @param conditions
     * @param fn
     */
    this.query = function (conditions, fn, isShowShadowWhenQuery) {
        var url = commonService.config.host + "/admin/queryStudents.action";
        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        commonService.removeKeyOfNullValue(conditions);
        $http({
            method: "POST",
            url: url,
            data: $.param(conditions),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };


    /**
     * 管理员添加学生接口
     */
    this.addStudent = function (conditions, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/admin/addStudent.action",
            data: $.param(conditions),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * 学生所有考试记录
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     */
    this.getAllExamRecordList = function (studentAccount, pageIndex, pageSize, fn, isShowShadow) {
        var url = commonService.config.host + "/admin/examAllRecordList.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param({
                studentAccount: studentAccount,
                pageIndex: pageIndex,
                pageSize: pageSize
            }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };


    this.getClassListOfStudent = function (user, fn) {
        var url = commonService.config.host + "/api/student/myClassList.action";

        url += "?studentAccount=" + user.account;
        $http({
            method: "GET",
            url: commonService.getUrlWithShadowParam(url),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };




    /**
     * 查询学生的专项训练测试记录
     * @param conditions
     * @param fn
     */
    this.querySpecialRecordList = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/querySpecialRecordList.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data: $.param(conditions),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * @param conditions
     * @param fn
     */
    this.queryTrainTaskRoomList = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/queryTrainTaskRoomList.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data: $.param(conditions),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };


    /**
     * @param conditions
     * @param fn
     */
    this.queryTrainTaskPaperList = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/queryTrainTaskPaperList.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data: $.param(conditions),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };


    /**
     * 查询自主考试记录
     * @param conditions
     * @param fn
     */
    this.queryTrainTaskRecordList = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/queryTrainTaskRecordList.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data: $.param(conditions),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });

    };

    /**
     * 查询学生端成绩轨迹
     * @param conditions
     * @param fn
     */
    this.queryTrainTaskScoreTrack = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/queryTrainTaskScoreTrack.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data: $.param(conditions),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    }

}]);