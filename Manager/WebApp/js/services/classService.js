cetsim.service("classService", ["$http", "commonService", function ($http, commonService) {

    /**
     * 根据老师id获取其拥有的班级列表
     * @param id 教师id
     * @param fn
     */
    this.getClassList = function (teacherCode, pageIndex, pageSize, fn) {
        var url = commonService.config.host + "/api/teacher/queryCourseClass.action?" + $.param({
                teacherCode : teacherCode,
                pageIndex : pageIndex,
                pageSize : pageSize
            });
        url = commonService.getUrlWithShadowParam(url);
        $http({
            method: "GET",
            url: url,
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
     * 该接口并不与 getClassList 重复, 该方法查询该教师所拥有的所有班级
     * @param teacherId
     * @param fn
     */
    this.getAllClassList = function (account, fn) {
        $http({
            method: "GET",
            url: commonService.config.host + "/api/teacher/roomlist.action?" + $.param({
                account : account
            }),
            headers : {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };
    
    this.getClassNameById = function (teacherId, classId, fn) {

        this.getAllClassList(teacherId, function (err, res) {
            if (!err && res.code === commonService.config.ajaxCode.success) {
                $.each(res.data, function (i, item) {
                    if (item.code === classId) {
                        fn(item.name);
                        return false;
                    }
                })
            } else {
                fn(null);
            }
        })
    };
    
    this.loadStudentsOfClass = function (classCode, pageIndex, pageSize, fn, isShowShadow, userFilter) {
        var params = {
            classCode: classCode,
            pageIndex: pageIndex,
            pageSize: pageSize

        };
        if (userFilter) {
            params["conditionValue"] = userFilter;    /// TODO
        }
        var url = commonService.config.host + "/api/teacher/queryCourseClassStudents.action?" + $.param(params);
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "GET",
            url: url,
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };


    this.addStudent = function (classCode, account, fn, isShowShadow) {
        var url = commonService.config.host + "/api/teacher/addStudent.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param($.extend({
                classCode: classCode
            }, account)),
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
     * 查询班级中是否有学生
     * @param classId
     */
    this.isHaveStudents = function (classId, fn) {
        this.loadStudentsOfClass(classId, 1, 10, function (err, res) {
            if (!err) {
                if (res.code === commonService.config.ajaxCode.success) {
                    fn(null, res.data.data.length > 0);
                } else {
                    fn(new Error(res.data.msg), null);
                }
            } else {
                fn(err, null);
            }
        });
    };

    /**
     * 筛选班级中没有学生的id
     * @param classIdList empty list if each class of list has students
     */
    this.getListOfNoStudents = function (classCodeList, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/admin/getemptyclass.action",
            data: $.param({
                codeList : classCodeList
            }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(null, response.data.data);
            } else {
                fn(new Error(response.data.msg), null);
            }
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    }

}]);