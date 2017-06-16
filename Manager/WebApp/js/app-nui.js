
var cetsim= angular.module("cetsim", [ 'ui.router', 'ngFileUpload','ie7-support']);

/**
 * 全局的ajax请求过程显示遮罩(遮罩挡住整个web页面)
 * 如何使用 : 在需要显示遮罩的请求(例如导入学生,导入教师等请求时间长的操作中), 在请求的url加上 _showGlobalShadowWhenQuery 字段并把值设置成 true
 * 例子: new commonService.URI(yourUrlHere).setSearch("_showGlobalShadowWhenQuery", true).toString()
 */
cetsim.value('loadingService', {
    dom : null,
    _lastShowTimeStamp : (+ new Date()),
    layerIndexList : [],
    request: function(config) {
        if (this._isHaveSpecialStringInSearch(config)) {
            this._show(config);
        }
    },
    requestError : function (rejection) {
        if (this._isHaveSpecialStringInSearch(rejection.config)) {
            this._hide(rejection.config);
        }
    },
    response: function(response) {
        if (this._isHaveSpecialStringInSearch(response.config)) {
            this._hide(response.config);
        }
    },
    responseError : function (rejection) {
        if (this._isHaveSpecialStringInSearch(rejection.config)) {
            this._hide(rejection.config);
        }
    },
    _show : function (config) {
        this._lastShowTimeStamp = (new Date()).valueOf();

        var commonService = angular.element(document.body).injector().get('commonService');
        var index = commonService.showWaitingShadow();
        try {
            config.layerIndex = index;
            this.layerIndexList.push(index);
        } catch (ex){}
    },
    _hide : function (config) {
        var that = this;
        try {
            var interval = (new Date()).valueOf() - this._lastShowTimeStamp;
            interval = (interval >= 500) ? 0 : (500 - interval);

            setTimeout(function () {
                var commonService = angular.element(document.body).injector().get('commonService');
                try {
                    that.layerIndexList.splice(_.indexOf(that.layerIndexList, config.layerIndex), 1);
                    if (that.layerIndexList.length <= 0) {
                        commonService.hideWaitingShadow();
                    }
                } catch(ex){
                    commonService.hideWaitingShadow();
                }
            }, interval);
        } catch (ex) {
            commonService.hideWaitingShadow();
        }
    },
    _isHaveSpecialStringInSearch : function (config) {
        try {
            var url = new URI(config.url),
                _showGlobalShadowWhenQuery = url.search(true)["_showGlobalShadowWhenQuery"];

            return _showGlobalShadowWhenQuery ? true : false;
        } catch (ex) {
            return false;
        }
    }
});

cetsim.factory('loadingInterceptor', function($q, loadingService, $location, $timeout, $rootScope) {
    return {
        request: function(config) {
            loadingService.request(config);

            if (config.method.toUpperCase() === "GET" && /\.action/.test(config.url) && !config.cache) {
                // 解决在IE低版本浏览器会有缓存的问题
                config.url = new URI(config.url).addSearch({"__uniqueStamp" : Math.random()}).toString()
            }
            return config;
        },
        requestError: function(rejection) {
            loadingService.requestError(rejection);
            return $q.reject(rejection);
        },
        response: function(response) {
            loadingService.response(response);
            if (!/#\/signin/.test(location.href) && !/#\/signup/.test(location.href)) {    //如果当前页不是登录或注册页,避免循环嵌套
                if (response && response["data"] && response.data["code"] == 2) {
                    var userService = angular.element(document.body).injector().get('userService');
                    userService.removeSignInfo(true);   // 如果不擦除登录信息会出现服务端超时但是前端的登陆信息依然存在的情况， 然后跳转到登陆页又会跳转到指定页面然后登陆超时又跳转到登录页， 形成跳转嵌套
                    layer.msg('登录超时, 请重新登录!', {
                        time: 3000 // 5秒后自动关闭
                    });
                    $timeout(function () {
                        layer.closeAll();
                        $location.path("/signin");
                        layer.msg('登录超时, 请重新登录!', {
                            time: 3000 // 5秒后自动关闭
                        });
                    }, 500);
                    throw new Error("登录超时");
                }
            }
            $rootScope.$broadcast("event.httpResponse");
            return response;
        },
        responseError : function (rejection) {
            loadingService.responseError(rejection);
            $rootScope.$broadcast("event.httpResponse");
            return $q.reject(rejection);
        }
    }
});



cetsim.config(function($stateProvider, $locationProvider, $urlRouterProvider, $logProvider, $httpProvider) {    
    $httpProvider.interceptors.push('loadingInterceptor');

    $logProvider.debugEnabled(true);
    $urlRouterProvider.when("", "/home");

    var tplDir = "tpl2";

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: tplDir + '/home.html',
            controller : "homeCtrl"
        })

        .state('signin', {
            url: '/signin',
            templateUrl: tplDir + '/signin.html',
            controller: 'signinCtrl'
        })

        .state('signup', {
            url: '/signup',
            templateUrl: tplDir + '/signup.html'
        })

        .state('userHome', {
            url: '/userHome',
            templateUrl: tplDir + '/route.html'
        })
        .state('userHome.personalInfoEdit', {
            url: '/personalInfoEdit',
            templateUrl: tplDir + '/includes/personalInfoEdit.html'
        })
        .state('userHome.changePassword', {
            url: '/changePassword',
            templateUrl: tplDir + '/includes/changePassword.html'
        })
        .state('userHome.changePassword.byOldPassword', {
            url: '/byOldPassword',
            templateUrl: tplDir + '/includes/changePasswordByOldPassword.html'
        })
        .state('userHome.changePassword.byEmail', {
            url: '/byEmail',
            templateUrl: tplDir + '/includes/changePasswordByEmail.html'
        })


        // -----------admin part-----------
        .state('admin', {
            url: '/admin',
            templateUrl: tplDir + '/route.html'
        })
        .state('download', {
            url: '/download',
            templateUrl: tplDir + '/download.html'
        })

        .state('admin.changePassword', {
            url: '/changePassword',
            templateUrl: tplDir + '/includes/changePassword.html'
        })

        .state('admin.importPaperForIE', {
            url: '/importPaperForIE',
            templateUrl: tplDir + '/admin/importPaperForIE.html'
        })
        .state('admin.importPaperItemForIE', {
            url: '/importPaperItemForIE',
            templateUrl: tplDir + '/admin/importPaperItemForIE.html'
        })

        .state('admin.importTeachers', {
            url: '/importTeachers',
            templateUrl: tplDir + '/admin/importTeachersForIE.html'
        })

        .state('admin.personalInfoEdit', {
            url: '/personalInfoEdit',
            templateUrl: tplDir + '/includes/personalInfoEdit.html'
        })

        .state('admin.resourceManage', {
            url: '/resourceManage',
            templateUrl: tplDir + '/admin/resourceManage.html'
        })

        .state('admin.resourceManage.paperManage', {
            url: '/paperManage',
            templateUrl: tplDir + '/admin/paperManage.html'
        })

        .state('admin.resourceManage.paperItemManage', {
            url: '/paperItemManage',
            templateUrl: tplDir + '/admin/paperItemManage.html'
        })

        .state('admin.teacherManage', {
            url: '/teacherManage',
            templateUrl: tplDir + '/admin/teacherManage.html'
        })

        .state('admin.studentManage', {
            url: '/studentManage',
            templateUrl: tplDir + '/admin/studentManage.html'
        })

        .state('admin.addOneStudent', {
            url: '/addOneStudent',
            templateUrl: tplDir + '/admin/addOneStudent.html',
            controller: 'addOneStudentCtrl'
        })

        .state('admin.attributesManage', {
            url: '/attributesManage',
            templateUrl: tplDir + '/admin/attributesManage.html'
        })
        .state('admin.attributesManage.institute', {
            url: '/institute',
            templateUrl: tplDir + '/admin/attributesManage.institute.html'
        })
        .state('admin.attributesManage.major', {
            url: '/major',
            templateUrl: tplDir + '/admin/attributesManage.major.html'
        })
        .state('admin.attributesManage.grade', {
            url: '/grade',
            templateUrl: tplDir + '/admin/attributesManage.grade.html'
        })
        // -----------admin part-----------

        // -----------teacher part BEGIN-----------
        .state('teacher', {
            url: '/teacher',
            templateUrl: tplDir + '/route.html'
        })

        .state('teacher.personalInfoEdit', {
            url: '/personalInfoEdit',
            templateUrl: tplDir + '/includes/personalInfoEdit.html'
        })

        .state('teacher.createClass', {
            url: '/createClass',
            templateUrl: tplDir + '/teacher/createClass.html',
            controller: 'createClassCtrl'
        })

        .state('teacher.teacherClassManage', {
            url: '/teacherClassManage/:classId',
            templateUrl: tplDir + '/teacher/teacherClassManage.html'
        })

        .state('teacher.importStudents', {
            url: '/importStudents/:classId',
            templateUrl: tplDir + '/teacher/importStudentsForIE.html'
        })

        .state('teacher.classList', {
            url: '/classList',
            templateUrl: tplDir + '/teacher/classList.html'
        })

        .state('teacher.taskview',{
            url:'/taskview/:planid/:taskid/:paperType',
            templateUrl: tplDir + '/teacher/taskview.html'
        })

        .state('teacher.taskMonitor',{
            url:'/taskMonitor/:planid/:taskid',
            templateUrl: tplDir + '/teacher/taskMonitor.html',
            controller: 'taskMonitorCtrl'
        })

        .state('teacher.ceshiList', {
            url: '/ceshiList',
            templateUrl: tplDir + '/teacher/ceshiList.html'
        })

        .state('teacher.ckbg', {
            url: '/ckbg/:studentAccount/:recordCode/:paperType',
            templateUrl: tplDir + '/teacher/ckbg.html'
        })

        // -----------teacher part END-----------

        .state('student', {
            url: '/student',
            templateUrl: tplDir + '/route.html'
        })
        .state('student.mytask', {
            url: '/mytask',
            templateUrl: tplDir + '/student/mytask.html',
            controller: 'studentTaskCtrl'
        })
        //增加查看报告详情路由配置
        .state('student.viewtask', {
            url: '/viewtask/:studentAccount/:recordCode/:paperType',
            templateUrl: tplDir + '/student/viewtask.html',
            controller: 'studentViewTaskCtrl'
        })
        .state('student.freesim', {
            url: '/freesim',
            templateUrl: tplDir + '/student/freesim.html',
            controller: 'studentFreeSimCtrl'
        })
        .state('student.myclass', {
            url: '/myclass',
            templateUrl: tplDir + '/student/myclass.html',
            controller: 'studentClassCtrl'
        })
        //修改个人设置信息
        .state('student.personalInfoEdit', {
            url: '/personalInfoEdit',
            templateUrl: tplDir + '/includes/personalInfoEdit.html'
        })
        // 专项训练
        .state('student.zxxl', {
            url: '/zxxl',
            templateUrl: tplDir + '/student/zxxl.html'
        })
        // 专项训练 列表
        .state('student.zxxl.list', {
            url: '/list',
            templateUrl: tplDir + '/student/zxxl_list.html'
        })
        // 专项训练 记录
        .state('student.zxxl.record', {
            url: '/record',
            templateUrl: tplDir + '/student/zxxl_record.html'
        })
        // 专项训练 记录 查看报告
        .state('student.zxxl.ckbg', {
            url: '/zxxl_ckbg/:code/:paperItemTypeCode',
            templateUrl: tplDir + '/student/zxxl_ckbg.html'
        })
        // 自主考试
        .state('student.zzks', {
            url: '/zzks',
            templateUrl: tplDir + '/student/zzks.html'
        })
        // 自主考试 组队考试
        .state('student.zzks.zdks', {
            url: '/list',
            templateUrl: tplDir + '/student/zzks_zdks.html'
        })
        // 自主考试 考试记录
        .state('student.zzks.record', {
            url: '/record',
            templateUrl: tplDir + '/student/zzks_record.html'
        })
        // 自主考试 组建考试
        .state('student.zjks', {
            url: '/zjks',
            templateUrl: tplDir + '/student/zjks.html'
        })
        // 自主考试 查看报告
        .state('student.zzks.taskReport', {
            url: '/zzks_ckbg/:studentAccount/:recordCode/:paperType',
            templateUrl: tplDir + '/student/zzks_ckbg.html'
        })
});


var do1_throttle  = _.throttle(function ($log) {
    $log.log("calculate footerPosition");
    $(window).trigger("event.footerPosition"); //底部始终固定在底部方法
}, 1000/2);

cetsim.run(function ($rootScope, $log, $timeout) {
    $rootScope.$on("$stateChangeStart", function (e, toState, toParams, fromState, fromParams) {
        do1();


        $rootScope.$on("event.httpResponse", function () {

            do1();
            do2();

        });

        function do1() {
            $timeout(function () {
                do1_throttle($log);
            })
        }

        do2();
        function do2() {
            var timesLimit = 5000, //ms
                timesStep = 500;
            var timer = setInterval(function () {
                do1_throttle($log);

                timesLimit -= timesStep;
                if (timesLimit <= 0) {
                    clearInterval(timer);
                }
            }, timesStep);
        }
    })
});