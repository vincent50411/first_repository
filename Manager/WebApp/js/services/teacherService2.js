cetsim.service("teacherService", ["$http", "commonService", "userService", function ($http, commonService, userService) {

    /**
     * 根据条件查询教师列表
     * @param conditions
     * @param fn
     */
    this.query = function (conditions, fn, isShowShadowWhenQuery) {
        var conditionsCopy = $.extend({}, conditions);
        if (conditionsCopy.name === "") {
            delete conditionsCopy["name"];
        }
        var url = commonService.config.host + "/admin/queryTeachers.action";
        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param(conditionsCopy),
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
     * 根据条件获取最大页码
     * @param conditions
     * @param fn
     * @deprecated
     */
    this.getMaxPage = function (conditions, fn) {
        $http({
            method: "POST",
            url: "test/teachers.maxPage.json", // TODO
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




    this.addOneTeacher = function (accoutModel, fn, isShowModel) {
        var url = commonService.config.host + "/admin/addTeacher.action";
        if (isShowModel) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param(accoutModel),
            headers:
            {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    this.createClass = function (params, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/teacher/createCourseClass.action",
            data: $.param(params),
            headers:
            {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };
    /**
     * 移除学生
     * @param params
     * @param fn
     */
    this.removeStudentFromClass = function (studentId, classId, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/teacher/deleteStudentFromCourseClass.action",
            data: $.param({
                account : studentId,
                classCode : classId
            }),
            headers:
            {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    }
}]);