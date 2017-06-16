/**
 * Created by Administrator on 2017/5/15.
 */
cetsim.service("studentSpecialTrainService", ["$http", "commonService", function($http, commonService){

    /**
     * 查询管理员已经发布的可用的专项训练试题列表
     * @param conditions
     * @param fn
     */
    this.queryQuestionList = function (conditions, fn) {
        var url = commonService.config.host + "/api/student/queryQuestionList.action";

        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: commonService.getUrlWithShadowParam(url),
            data : $.param(conditions),
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
     * 开始专项训练考试
     * @param studentAccount
     * @param paperItemCode
     */
    this.specialStartExamService = function(studentAccount, paperItemCode, callBackFun)
    {
        $http({
            method : "POST",
            url : commonService.config.host + '/api/student/startSpecialTrainExam.action?' + $.param({
                studentAccount:studentAccount,
                paperItemCode : paperItemCode
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(response.data);
        }, function (response) {
            commonService.showTip("请求服务端数据失败");

            callBackFun(null);
        });
    };

    this.isSpecialTrainTaskStartService = function(recordId, callBackFun)
    {
        $http({
            method : "POST",
            url : commonService.config.host + '/exam/isEcpStart.action?' + $.param({
                recordId : recordId
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(response.data);
        }, function (response) {
            commonService.showTip("请求服务端数据失败");

            callBackFun(null);
        });
    };

    /**
     * 由于专项训练提供当考试机启动后，直接跳转到查看报告界面，需要判断考试记录是否正确结束，如果没有，则不执行后续查询操作
     * @param studentAccount
     * @param specialRecordCode
     */
    this.querySpecialRecordStateService = function(studentAccount, specialRecordCode, callBackFun)
    {
        $http({
            method : "POST",
            url : commonService.config.host + '/api/student/querySpecialRecordState.action?' + $.param({
                studentAccount : studentAccount,
                specialRecordCode : specialRecordCode
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(response.data);
        }, function (response) {
            commonService.showTip("请求服务端数据失败");

            callBackFun(null);
        });

    };

    /**
     * 查看专项训练报告成绩信息
     * @param studentAccount
     * @param specialRecordCode
     */
    this.querySpecialExamReportService = function(studentAccount, specialRecordCode, callBackFun)
    {
        $http({
            method : "POST",
            url : commonService.config.host + '/api/student/querySpecialRecordReport.action?' + $.param({
                studentAccount : studentAccount,
                specialRecordCode : specialRecordCode
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(response.data);
        }, function (response) {
            commonService.showTip("请求服务端数据失败");

            callBackFun(null);
        });

    };

    /**
     * 查询自己的答题信息，包括自己的录音文件地址、学习引擎返回的评价地址
     * @param conditions
     * @param callBackFun
     */
    this.querySelfSpecialAnswerInfo = function (conditions, callBackFun) {
        var url = commonService.config.host + '/api/student/querySelfSpecialAnswerInfo.action';
        url = commonService.getUrlWithShadowParam(url);
        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "POST",
            url: url,
            data: $.param(conditions),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(null, response.data);
        }, function (response) {
            callBackFun(new Error(response.statusText), response.data);
        });
    };
    
    this.taskExamInfo = function (conditions, callBackFun) {
        var url = commonService.config.host + '/api/student/taskExamInfo.action?' + $.param(conditions);
        commonService.removeKeyOfNullValue(conditions);

        $http({
            method: "GET",
            url: url,
            data: $.param(conditions),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            callBackFun(null, response.data);
        }, function (response) {
            callBackFun(new Error(response.statusText), response.data);
        });
    }

}]);