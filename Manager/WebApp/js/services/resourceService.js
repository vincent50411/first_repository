cetsim.service("paperService", ["$http", "commonService", "$log", function ($http, commonService, $log) {

    /**
     * 根据试卷type获取类型名称
     * @param type
     * @returns {*}
     */
    this.getPaperTypeName = function (type) {
        try {
            type = parseInt(type);
            switch (type) {
                case commonService.config.paperType.cet4:
                    return "cet4";
                case commonService.config.paperType.cet6:
                    return "cet6";
                default :
                    return "--";
            }
        } catch (ex) {
            $log.warn(ex.message);
            return "--";
        }

    };
    /**
     * 条件查询试卷列表
     * @param conditions
     * @param fn
     */
    this.getPaperList = function (conditions, fn, isShowShadow) {
        var url = commonService.getUrlWithShadowParam(commonService.config.host + "/api/paper/selectPaper.action");
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        commonService.removeKeyOfNullValue(conditions);
        $http({
            method: "POST",
            url: url,
            data : $.param(conditions),
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
     * 更新试卷是否启用状态
     * @param id
     * @param status
     * @param fn
     */
    this.updatePaperStatus = function (id, status, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/paper/setPaperStatus.action",
            data : $.param({
                paperCode : id,
                status : status
            }),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    }

    /**
     * 更新试卷 planUseage 状态
     * @param code
     * @param status
     * @param fn
     */
    this.updatePaperPlanUseage = function (code, status, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/paper/setAllowPlanUsage.action", //TODO
            data : $.param({
                paperCode : code,
                allowPlanUsage : status == 0 ? false : true
            }),
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
     * 更新试卷 freeUseage 状态
     * @param id
     * @param status
     * @param fn
     */
    this.updatePaperFreeUseage = function (paperCode, status, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/paper/setAllowFreeUsage.action",
            data : $.param({
                paperCode : paperCode,
                allowFreeUsage : status
            }),
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
     * @deprecated
     * @param id
     * @param fn
     */
    this.deletePaper = function (id, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/paper/deletePaper.action",
            data : $.param({
                paperId : id
            }),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    }
}]);


cetsim.service("paperItemService", ["$http", "commonService",
    function ($http, commonService) {

        /**
         * 根据条件获取试题列表
         * @param conditions
         * @param fn
         */
        this.getPaperItems = function (conditions, fn) {
            commonService.removeKeyOfNullValue(conditions);
            var url = commonService.config.host + "/api/question/selectQuestionList.action";
            url = commonService.getUrlWithShadowParam(url);
            $http({
                method: "POST",
                url: url,
                data : $.param(conditions),
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
         * 更新字段信息（例如专项训练权限）
         * @param params
         * @param fn
         */
        this.update = function (params, fn) {
            //TODO
        };

        /**
         * 设置试题的状态
         * @param paperItemCode
         * @param status
         * @param fn
         */
        this.setQuestionStatus = function (paperItemCode, status, fn) {
            $http({
                method: "POST",
                url: commonService.config.host + "/api/question/setQuestionStatus.action",
                data: $.param({
                    paperItemCode: paperItemCode,
                    status: status
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
        
        this.toggleStatusOfPaperType = function (paperItemCode, allowFreeUsage, fn) {
            $http({
                method: "POST",
                url: commonService.config.host + "/api/question/setQuestionStatus.action",
                data: $.param({
                    paperItemCode: paperItemCode,
                    status: allowFreeUsage
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