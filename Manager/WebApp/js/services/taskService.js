cetsim.service("taskService", ["$http", "commonService", function ($http, commonService) {
    /**
     * 根据老师id获取任务列表
     * @param id    教师ID
     * @param pageIndex
     * @param pageSize
     * @param fn
     */
    this.getTaskList = function (account, pageIndex, pageSize, fn, orderObj) {
        var params;
        if (orderObj) {
            params = $.extend({
                account: account,
                pageIndex: pageIndex,
                pageSize: pageSize
            }, orderObj);

        } else {
            params = {
                account: account,
                pageIndex: pageIndex,
                pageSize: pageSize
            };
        }

        commonService.removeKeyOfNullValue(params);

        var url = commonService.config.host + "/api/teacher/tasklist.action?" + $.param(params);
        url = commonService.getUrlWithShadowParam(url);
        $http({
            method: "GET",
            url: url,
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
     * 根据plan的code获取已发布的测试的对应的班级列表
     * @param plan
     * @param fn
     */
    this.getPlanClassList = function (plan, fn) {
        var url = commonService.config.host + "/api/teacher/getPlanClassList.action";
        url = commonService.getUrlWithShadowParam(url);

        $http({
            method: "POST",
            url: url,
            data: $.param({
                plan_code: plan.plan_code
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

    /**
     * 删除计划
     * @param plan
     * @param fn
     */
    this.deletePlan = function (plan, fn) {
        var url = commonService.config.host + "/api/teacher/deletePlan.action";
        url = commonService.getUrlWithShadowParam(url);

        $http({
            method: "POST",
            url: url,
            data: $.param({
                plan_code: plan.plan_code
            }),
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