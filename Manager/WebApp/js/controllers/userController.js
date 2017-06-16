cetsim.controller('signinCtrl', ["$scope", "$timeout", "commonService", "userService", "$log", "$rootScope", "$http", "$location",
    function ($scope, $timeout, commonService, user, $log, $rootScope, $http, $location) {
        if (user.isSignin()) {
            user.goHomePage();
        }

        $scope.title = commonService.config.name;
        // 左侧滚动图片
        $scope.imgs = user.signinPageImgs;
        // 登录信息
        $scope.signinParams = {
            user : "",
            pass : "",
            remember : false
        };
        $scope.signinParams = $.extend($scope.signinParams, user.readStatusFromCookie());

        $timeout(function () {
            if ($scope.signinParams.user) {
                $("#cetsim-signin-input-user").trigger("change");
            }
            if ($scope.signinParams.pass) {
                $("#cetsim-signin-input-password").trigger("change");
            }
        });

        $scope.errorMessage = "";

        $scope.signinFormSubmit = function () {
            if ($scope.signinParams.user === "") {
                $scope.showSignTip("请输入账号");
                return;
            }

            if ($scope.signinParams.pass === "") {
                $scope.showSignTip("请输入密码");
                return;
            }

            $log.log("sign in model follow:");
            $log.log($scope.signinParams);

            user.signin($scope.signinParams, function (err, isSuccess) {
                layer.closeAll();
                if (isSuccess) {
                    user.goHomePage();

                    user.checkRequiredUserInfo($scope.signinParams.user, function (err, res) {
                        if (!err) {
                            if (res.code === commonService.config.ajaxCode.error) {
                                var index = layer.confirm("为了您的账号安全，请完善个人信息", {
                                    btn: ['去完善','取消']
                                },function () {
                                    layer.close(index);

                                    $rootScope.$apply(function () {
                                        var role = user.getRole();
                                        $location.path("/userHome/personalInfoEdit");
                                    });

                                }, function () {
                                    layer.close(index);
                                })
                            }
                        }
                    });
                } else {
                    $scope.showSignTip(err.message);
                }
            })
        };

        var _removeErrorMessage = commonService._.debounce(function () {
            $scope.$apply(function () {
                $scope.errorMessage = "";
            })
        }, 3000);
        $scope.showSignTip = function (message) {
            $scope.errorMessage = message;
            _removeErrorMessage();
        };

        $(document.body).addClass("login-page");
        $scope.$on("$destroy", function () {
            $(document.body).removeClass("login-page");
        });

        if ($location.search()["_clearLayer"]) {
            $timeout(function () {
                layer.closeAll();
            });
            setTimeout(function () {
                layer.closeAll();
            }, 1000);

            setTimeout(function () {
                layer.closeAll();
            }, 2000);
        }

        $timeout(function () {

            $('#body .imgsRoll').slick({
                autoplay: true,
                autoplaySpeed: 3000,
                arrows: true,
                lazyLoad: true,
                prevArrow: "<span><</span>",
                nextArrow: "<span class='slick-next'>></span>"
            });

        });


}]);

/**
 * 个人信息修改
 */
cetsim.controller("personalInfoEditCtrl", ["$scope", "commonService", "userService", "$log", "$rootScope", "$timeout", "Upload", "studentService",
    function ($scope, commonService, userService, $log, $rootScope, $timeout, Upload, studentService) {
        var _curUser = userService.getUserModelFromLocalInfo();
        $scope.userModel = {
            id : _curUser.id,
            account : "",
            name  : "",
            gender : "",
            telephone : ""
        };
        $scope.isEmailFieldChanged = false;
        $scope._userEmailCache = "";

        $scope.userPhoto = "";
        // 只有学生账号才有下面myClassList的信息,否则为空数组
        $scope.myClassList = [];
        $scope.file = "";
        $scope.isStudent = userService.isStudent();
        $scope.isEnableEmailValidate = false;

        userService.isEnableEmailValidate(function (bool) {
            $scope.isEnableEmailValidate = bool;
        });

        $scope.uploadFiles = function (file, errFiles) {
            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                file.upload = Upload.upload({
                    url: commonService.getUrlWithShadowParam(commonService.config.host + "/api/user/uploadphoto.action"),
                    data: {
                        file: file,
                        account : userService.getUserModelFromLocalInfo().account
                    }
                });

                file.upload.then(function (res) {
                    $timeout(function () {
                        _dealResponseOfUploadSuccess(res.data);
                    });
                }, function (response) {
                    var msg = "修改头像失败,请重试";
                    commonService.layerError("提示", msg);
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                        evt.loaded / evt.total));
                });
            }
        };

        var _dealResponseOfUploadSuccess = function (res) {
            if (res.code === commonService.config.ajaxCode.success) {
                $scope.userPhoto = commonService.addRandomTimestampForUrl(userService.getUserPhotoAbsolutePathByFileName(res.data));

                userService.updateLocalUserInfo({
                    photo: res.data
                });
                $rootScope.$broadcast("event.userPhotoChanged");
                commonService.layerMsg("修改头像成功");
            } else {
                var errorInfo = "修改头像失败,请重试";
                if (res.msg) {
                    errorInfo += "。失败信息：" + res.msg;
                }
                commonService.layerError("提示", errorInfo);
            }
        };

        userService.getUserInfoFromServer2(function (err, res) {
            if (!err) {
                if (res.code === commonService.config.ajaxCode.success) {
                    $scope.userModel = res.data;
                    $scope.userModel.gender = $scope.userModel.gender + "";
                    $scope.userPhoto = commonService.addRandomTimestampForUrl(userService.getUserPhotoAbsolutePathByFileName(res.data["reserved1"]));
                    $scope._userEmailCache = res.data["email"];

                    $log.log("load user model from server (gender has been changed from Number to String[for IE8 hack]): ");
                    $log.log($scope.userModel);
                } else {
                    $log.warn("加载个人信息失败,userid=" + _curUser.id + ", info=" + res.msg);
                    commonService.showTip("加载个人信息失败, 失败原因：" + res.msg);
                }
            } else {
                $log.warn("加载个人信息失败,userid=" + _curUser.id + ", info=" + err.message);
                commonService.showTip("加载个人信息失败");
            }
        }, true);

        if (userService.isStudent()) {
            studentService.getClassListOfStudent(_curUser, function (err, res) {
                if (!err) {
                    if (res.code === commonService.config.ajaxCode.success) {
                        if (res.data && res.data.length > 0) {
                            $scope.myClassList = res.data;


                            $timeout(function () {
                                _classListRoll();
                            });
                        }
                    } else {
                        //commonService.showTip("加载班级列表失败, 失败原因：" + res.msg);
                    }
                } else {
                    $log.warn("加载班级列表失败,userid=" + _curUser.id + ", info=" + err.message);
                    //commonService.showTip("加载班级列表失败");
                }
            })
        }

        $scope.submit = function () {
            _updateUserInfo();
        };

        var _updateUserInfo = commonService._.debounce(function () {

            var params = $.extend({}, $scope.userModel);
            params.telephone = $.trim(params.telephone);

            var validation = [{
                obj: params.name,
                name : "姓名",
                filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:10', 'validationNoSpecialChars']
            },{
                obj: params.naturalClass,
                name : "班级",
                filters: ['validationLengthNotMoreThan:20', 'validationNoSpecialChars']
            },{
                obj: params.telephone,
                name:"手机号",
                filters: ['validationNotEmpty', 'validationTelephoneNotStrict']
            },{
                obj: params.email,
                name: "邮箱",
                filters: ['validationNotEmpty', 'validationEmailNotStrict']
            }];

            if ($scope.isEnableEmailValidate && $scope.isEmailFieldChanged) {
                validation.push({
                    obj: params.emailCode,
                    name : "验证码",
                    filters: ['validationNotEmpty']
                });
            }
            if (!commonService.formValidate(validation)) {
                return;
            }

            if ($scope.isEnableEmailValidate && $scope.isEmailFieldChanged) {
                userService.validateEmailCodeService(null, $scope.userModel.emailCode, function (err, res) {
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        sendInfoToServer();
                    } else {
                        commonService.showTip("验证码不正确");
                    }
                })
            } else {
                sendInfoToServer();
            }

            function sendInfoToServer() {
                userService.updateUserInfoForPersonalInfoEdit(params, function (err, res, resultParam) {
                    if (!err && res.code === commonService.config.ajaxCode.success)
                    {
                        //用户信息修改成功后，需要更新界面显示信息
                        userService.updateLocalUserInfo(resultParam);

                        //需要触发自定义事件，更新界面显示信息 by lws 20170329
                        $scope.$emit("updateUserInfoEvent");

                        commonService.layerSuccess("提示", "修改个人信息成功", true, "返回");
                    } else {
                        // commonService.showTip("修改个人信息失败");
                        if (res && res.msg) {
                            commonService.layerError(res.msg);
                        } else {
                            commonService.layerError("提示", "修改个人信息失败");
                        }
                    }
                }, true)
            }

        }, 300);

        $scope.changePhoto = function () {
            if (!$scope.file) {
                $log.log("没有选择上传的图片, 这可能是取消选择文件触发的(并不是bug)");
                return;
            }
            var curUser = userService.getUserModelFromLocalInfo();

            commonService.formDataUpload(commonService.getUrlWithShadowParam(commonService.config.host + "/api/user/uploadphoto.action"), {
                file : $scope.file,
                account : curUser.account
            }, function (err, res) {
                if (!err && res.code === commonService.config.ajaxCode.success) {
                    _dealResponseOfUploadSuccess(res);
                } else {
                    commonService.layerError("提示", "修改头像失败,请重试");
                }
            })
        };
        
        $scope.showchangePasswordPanel = function () {
            commonService.showPanel($("#changePasswordPanel"));
        };

        $scope.togglePhotoUploadArea = function () {
            $("#userPhotoUploadForm").find("input[type='file']").click();
            // $("#userPhotoUploadForm").slideToggle();
        };

        $("#userPhotoUploadForm").find("input[type='file']").on("change", function () {
            $log.log("uploading photo now!");
            $timeout(function () {
                $scope.changePhoto();
            }, 100);
        });


        $scope.$watch("userModel.email", function () {
            if ($scope.userModel.email != $scope._userEmailCache) {
                $scope.isEmailFieldChanged = true;
            } else {
                $scope.isEmailFieldChanged = false;
            }
        });

        var _unbind = $rootScope.$on("event.userPhotoChanged", function () {
            $("#userPhotoUploadForm").slideUp();
        });
        $scope.$on("$destroy", function () {
            _unbind();
            $("#userPhotoUploadForm").find("input[type='file']").off("change");
        });

        var _classListRoll = function () {
            $("#cetsim-slick-roll").slick({
                autoplay: false,
                arrows: true,
                slidesToShow : 4,
                prevArrow: "<span class='slick-arrow slick-prev'><</span>",
                nextArrow: "<span class='slick-arrow slick-next'>></span>"
            });
        };


}]);


cetsim.controller("changePasswordCtrl", ["$scope", "userService", "commonService", function ($scope, userService, commonService) {
    $scope.oldPass = "";
    $scope.newPass = "";
    $scope.confirmPass = "";


    $scope.submit = function () {
        if ($.trim($scope.oldPass) === "") {
            commonService.layerMsg("旧密码不能为空");
            return;
        }

        if (/\s/.test($scope.oldPass)) {
            commonService.layerMsg("旧密码不能包含空格");
            return;
        }

        if ($.trim($scope.newPass) === "") {
            commonService.layerMsg("新密码不能为空");
            return;
        }

        if ($scope.confirmPass !== $scope.newPass) {
            commonService.layerMsg("新密码两次输入不一致");
            return;
        }

        if (!commonService.isPassword($scope.newPass)) {
            commonService.layerMsg("新密码长度不能小于6位, 且不能包含空白字符");
            return;
        }


        var curUser = userService.getUserModelFromLocalInfo();
        userService.changePassword(curUser.account, $scope.oldPass, $scope.newPass, function (err, res) {
            if (!err) {

                if (res.code === commonService.config.ajaxCode.success) {
                    userService.logoutOnlyHttpPost();
                    userService.removeSignInfo();
                    layer.closeAll();
                    commonService.layerOpen({
                        content: $("#user-change-password-info-panel"),
                        area: "480px",
                        title: "提示"
                    })
                } else {
                    commonService.layerError("提示", "修改密码失败, 提示：" + res.msg);
                }
            } else {
                commonService.layerError("提示", "修改密码失败");
            }
        }, true);
    };

}]);

cetsim.controller("changePasswordByEmailCtrl", ["$scope", "userService", "commonService", function ($scope, userService, commonService) {

    $scope.newPass = "";
    $scope.confirmPass = "";
    $scope.userModel = {};


    $scope.submit = function () {

        var validation = [{
            obj: $scope.userModel.email,
            name : "邮箱",
            filters: ['validationNotEmpty', 'validationEmailNotStrict']
        },{
            obj: $scope.userModel.code,
            name : "验证码",
            filters: ['validationNotEmpty']
        },{
            obj: $scope.newPass,
            name : "新密码",
            filters: ['validationNotEmpty', 'validationPassword']
        }];

        if (!commonService.formValidate(validation)) {
            return;
        }

        if ($scope.confirmPass !== $scope.newPass) {
            commonService.layerMsg("新密码两次输入不一致");
            return;
        }

        var curUser = userService.getUserModelFromLocalInfo();
        userService.changePasswordByEmail({
            userAccount : curUser.account,
            newPass : $scope.newPass,
            emailCode : $scope.userModel.code
        }, function (err, res) {
            if (res.code === commonService.config.ajaxCode.success) {
                userService.logoutOnlyHttpPost();
                userService.removeSignInfo();
                layer.closeAll();
                commonService.layerOpen({
                    content: $("#user-change-password-info-panel"),
                    area: "480px",
                    title: "提示"
                })
            } else {
                commonService.layerError("修改密码失败" + (res.msg ? "，失败原因：" + res.msg : ""));
            }
        })
    };

    /**
     * 获取邮箱验证码
     */
    $scope.getEmialCode = function()
    {
        //邮箱地址
        var emailAddr = $("#emailAddr").val();

        //用户账号
        var account = commonService.Cookies('user_account');

        //获取验证码
        userService.getEmailCodeService(account, emailAddr, function(error, data)
        {
            if(error == null)
            {
                if(data.code == 0)
                {
                    commonService.layerError(data.msg)
                }
                else
                    {
                    commonService.showTip("验证码发送成功")
                }

            }
            else {
                commonService.layerError("验证码发送失败")
            }
        });
    };

    /**
     * 校验验证码
     */
    $scope.validateEmailCode = function()
    {
        //输入的验证码
        var validatorCode = $("#validatorCode").val();

        //用户账号
        var account = commonService.Cookies('user_account');

        //验证码校验
        userService.validateEmailCodeService(account, validatorCode, function(error, data)
        {
            if(error == null)
            {
                if(data.code == 0)
                {
                    commonService.layerError(data.msg)
                }
                else
                {
                    commonService.showTip(data.msg)
                }
            }
            else {
                commonService.layerError("验证码校验失败")
            }
        });
    };



}]);

cetsim.controller("forgetPasswordCtrl", ["$scope", "userService", "commonService", "$rootScope",
    function ($scope, userService, commonService, $rootScope) {

        $scope.userModel = {};
        $scope.step = 1;
        $scope.isValid = false;

        var _unbind = $scope.$on("event.showPop.cetsimforgetpasswordpanel", function () {
            $rootScope.$broadcast("event.cleartip.forgetpass-code");
            $scope.$apply(function () {
                $scope.userModel = {};
                $scope.step = 1;
                $scope.isValid = false;
            });
        });
        $scope.$on("$destroy", function () {
            _unbind();
        });

        $scope.next = function () {
            $scope.step = 2;
        };
        
        $scope.changePassword = function () {

            var validation = [{
                obj: $scope.userModel.account,
                name: "账号",
                filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:20', 'validationNoSpecialChars']
            },{
                obj: $scope.userModel.code,
                name: "验证码",
                filters: ['validationNotEmpty']
            },{
                obj: $scope.userModel.password,
                name: "新密码",
                filters: ['validationPassword',  'validationLengthNotMoreThan:20']
            }];

            if (!commonService.formValidate(validation)) {
                return;
            }

            if ($scope.userModel.password !== $scope.userModel.password2) {
                commonService.layerMsg("新密码两次输入不一致");
                return;
            }


            userService.changePasswordByEmail({
                userAccount : $scope.userModel.account,
                newPass : $scope.userModel.password,
                emailCode : $scope.userModel.code
            }, function (err, res) {
                if (res.code === commonService.config.ajaxCode.success) {
                    userService.logoutOnlyHttpPost();
                    userService.removeSignInfo();
                    layer.closeAll();
                    commonService.layerOpen({
                        content: $("#user-change-password-info-panel"),
                        area: "480px",
                        title: "提示"
                    })
                } else {
                    commonService.layerError("修改密码失败" + (res.msg ? "，失败原因：" + res.msg : ""));
                }
            })
        }

}]);




cetsim.controller("signupCtrl", ["$scope", "userService", "commonService",
    function ($scope, userService, commonService) {
        $scope.user = {
            institution : "",
            grade : "",
            school : "",
            naturalClass : "",
            major : "",
            gender : userService.userGender.boy
        };

        $scope._userGender = userService.userGender;
        $scope.isRequireEmail = true;

        userService.isEnableEmailValidate(function (bool) {

            $scope.isRequireEmail = bool;

            $scope.signup = function () {

                var validation = [{
                    obj: $scope.user.account,
                    name : "账号",
                    filters: ['validationNotEmpty', 'validationNoSpace', 'validationLengthNotMoreThan:20', 'validationCetsimAccount']
                },{
                    obj: $scope.user.name,
                    name : "姓名",
                    filters: ['validationNotEmpty', 'validationNoSpace',  'validationLengthNotMoreThan:10', 'validationNoSpecialChars']
                },{
                    obj: $scope.user.institution,
                    name : "学院",
                    filters: ['validationLengthNotMoreThan:20', 'validationNoSpecialChars']
                },{
                    obj: $scope.user.grade,
                    name : "年级",
                    filters: ['validationNumberNotStrict']
                },{
                    obj: $scope.user.major,
                    name : "专业",
                    filters: ['validationLengthNotMoreThan:20', 'validationNoSpecialChars']
                },{
                    obj: $scope.user.naturalClass,
                    name : "班级",
                    filters: ['validationLengthNotMoreThan:20', 'validationNoSpecialChars']
                },{
                    obj: $scope.user.telephone,
                    name : "手机号",
                    filters: ['validationNotEmpty', 'validationTelephoneNotStrict']
                }];

                if ($scope.isRequireEmail) {
                    validation.push({
                        obj: $scope.user.email,
                        name: "邮箱",
                        filters: ['validationNotEmpty', 'validationEmailNotStrict',  'validationLengthNotMoreThan:50']
                    });
                    validation.push({
                        obj: $scope.user.emailCode,
                        name: "验证码",
                        filters: ['validationNotEmpty']
                    });
                }
                validation.push({
                    obj: $scope.user.password,
                    name : "密码",
                    filters: ['validationPassword',  'validationLengthNotMoreThan:20']
                });


                if (!commonService.formValidate(validation)) {
                    return;
                }

                if ($scope.user.password !== $scope.user.password2) {
                    commonService.showTip("两次密码输入不一致");
                    return;
                }

                userService.signup($scope.user, function (err, res) {

                    if (!err) {

                        if (res.code === commonService.config.ajaxCode.success) {
                            userService.removeSignInfo(false);
                            commonService.layerOpen({
                                title : "用户提示",
                                area : "480px",
                                content: $("#user-signup-info-panel")
                            });

                        } else {
                            commonService.layerError("提示", res.msg);
                        }
                    } else {
                        commonService.layerError("提示", "注册失败");
                    }
                })
            }
        });
}]);


