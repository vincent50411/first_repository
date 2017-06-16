cetsim.service("studentTaskService", ["$http", "commonService", function ($http, commonService) {
    /**
     * 获取任务列表
     * @param studentAccount  学生账号
     * @param pageIndex
     * @param pageSize
     * @param status int 查询条件，3查询全部，1未完成，2已完成
     * @param fn
     */
    this.getTaskList = function(studentAccount, pageIndex, pageSize, status, fn, isShowShadowWhenQuery, orderObj)
    {
        var params;
        if (orderObj) {
            params = $.extend({
                studentAccount: studentAccount,
                pageIndex: pageIndex || 1,
                pageSize: pageSize || 10,
                status: status
            }, orderObj);
        } else {
            params = {
                studentAccount: studentAccount,
                pageIndex: pageIndex || 1,
                pageSize: pageSize || 10,
                status: status
            };
        }
        commonService.removeKeyOfNullValue(params);
        var url = commonService.config.host + '/api/student/tasklist.action?' + $.param(params);

        if (isShowShadowWhenQuery) {
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
                // 登录失败
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("登录失败"), false);
        })
	};

    /**
     * 开始考试
     * @param examTaskCode 模拟考试任务代码
     * @param always 是否可以重考
     * @param callBackFun
     */
    this.beginExamService = function(examTaskCode, always, callBackFun){
        $http({
            method : "GET",
            url : commonService.config.host + '/api/student/beginexam.action?' + $.param({
                studentAccount: commonService.cookies("user_account"),
                examTaskCode : examTaskCode,
                always : always
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if(response != null)
            {
                callBackFun(response.data);
            }
        }, function (response) {
            //callBackFun(new Error("请求服务端数据失败"));
        });
    };

    /**
     * 去服务器端检查考试机是否已经启动，true已经启动，false未启动
     * @param recordId
     * @param callBackFun
     */
    this.isEcpStartService = function(recordId, callBackFun){
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


}]).service("studentClassService", ["$http", "commonService", function ($http, commonService) {  
    this.getClassList = function(studentId,pageIndex,pageSize,fn){       
         $http({
            method : "GET",
            url : commonService.config.host + '/api/student/classlist.action?' + $.param({
                studentId : commonService.Cookies('user_id'),
                pageIndex : pageIndex || 1,
                pageSize: pageSize || 10
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {                
                fn(response.data.data, true);
            } else {
                // 登录失败
                fn(new Error(response.data.msg), false);
            }
        }, function (response) {
            fn(new Error("登录失败"), false);
        })
    }
}]).service("studentTaskReportService", ["$http", "commonService", function ($http, commonService) {

    /**
     * 任务试卷包信息
     * @param examRecordCode
     * @param fun
     */
    this.getTaskExamInfo = function(examRecordCode, examType, fun)
    {
        $http({
            method: "GET",
            url: commonService.config.host + '/api/student/taskExamInfo.action?' + $.param({
                examRecordCode : examRecordCode,
                examType : examType
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function successCallback(response) {
            //返回任务报告明细数据
            fun(response, true);
        }, function errorCallback(response) {
            // 请求失败执行代码
            fun(response, false);
        })
    };

    /**
     * 考生答题包信息
     * @param recordCode
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @param fun
     */
    this.getTaskAnswerInfo = function(studentAccount, recordCode, examType, fun)
    {
        $http({
            method: "GET",
            url: commonService.config.host + '/api/student/taskAnswerInfo.action?' + $.param({
                studentAccount:studentAccount,
                examRecordCode : recordCode,
                examType : examType
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function successCallback(response) {
            // 请求成功执行代码
            fun(response, true);
        }, function errorCallback(response) {
            // 请求失败执行代码
            fun(response, false);
        })
    };

    /**
     * 服务端合成队友和自己的录音文件
     * @param recordCode
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     */
    this.operatePartnerSounder = function(studentAccount, recordCode, examType, isShowShadowWhenQuery, fun)
    {
        var url = commonService.config.host + '/api/student/partnerSounder.action?' + $.param({
                studentAccount: studentAccount,
                examRecordCode : recordCode,
                examType : examType
            });

        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url);
        }

        //由于获取服务端语音文件需要合并处理，需要等待时间
        var loadingIndex = commonService.loadingWin(20 * 1000);

        $http({
            method: "GET",
            url: url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function successCallback(response) {
            // 请求成功执行代码
            fun(response, true);
            layer.close(loadingIndex);
        }, function errorCallback(response) {
            // 请求失败执行代码
            fun(null, false);
            layer.close(loadingIndex);
        })

    };



    //成绩报告上面的信息
    this.getExamResultService = function(examRecordCode, fn){
        $http({
            method : "GET",
            url : commonService.config.host + '/api/student/examresult.action?' + $.param({
                examRecordCode : examRecordCode
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(response.data.data, true);
        }, function (response) {
            fn(new Error(response.data.msg), false);
        })
    };




}]);