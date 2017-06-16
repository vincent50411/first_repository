cetsim.service("userService", ["$http", "commonService", "$log", "$location", function ($http, commonService, $log, $location) {
    var _self = this;
    this.signinUrl = "/api/user/login.action";
    /**
     * @deprecated
     * @type {string}
     * @private
     */
    this._userPhotoDir = "img/user/";
    this.signinPageImgs = commonService.config.homeRollImgs;
    this.userModel = {
        id : 0,
        name : "",
        role : -1,  //(0:管理员, 1:教师, 2:学生)
        status : 1, //用户状态(0:非激活,1:已激活)
        gender : -1, //0:男,1:女
        photo : "", // 头像文件名
        photoRelativePath : "", // 头像相对路径
        lastLogin : ""
    };
    this.userRole = {
        visitor : -1,
        admin : 0,
        teacher : 1,
        student : 2
    };
    this.userGender = {
        boy : 0,
        girl : 1
    };
    this.userStatus = {
        disabled : 0,
        enabled : 1,
        all : -1
    };
    /**
     * 是否已登录
     * @returns {boolean}
     */
    this.isSignin = function () {
        try {
            var isSignin = JSON.parse(commonService.Cookies("user.issignin"));
            return isSignin;
        } catch (ex) {
            return false;
        }
    };


    

    /**
     * 登录
     * @param params
     * @param fn
     */
    this.signin = function (params, fn) {
        var url = commonService.config.host + this.signinUrl;
        url = commonService.getUrlWithShadowParam(url);

        $http({
            method : "POST",
            url : url,
            data : $.param({
                account : params.user,
                password : params.pass
            }),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            if (response.data.code === 1) {
                response.data.data.photo = response.data.data.reserved1;
                // 登录成功
                if (params.remember) {
                    _self.saveSignInfo(params.user, params.pass, params.remember);
                } else {
                    //false不保存状态
                    _self.removeSignInfo(params.remember == "" ? false : true);
                }

                var userModel = $.extend(_self.userModel, response.data.data);
                _self._saveUserModel(userModel);

                fn(null, true);
                $log.debug("登陆成功");
            } else {
                // 登录失败
                fn(new Error(response.data.msg), false);
                $log.debug("登陆失败:" + response.data.msg);
            }
        }, function (response) {
            fn(new Error("登录失败"), false);
            $log.debug("登陆失败");
            $log.debug(response);
        })
    };


    this.signup = function (params, fn) {
        var url = commonService.config.host + "/api/user/studentRegister.action";
        url = commonService.getUrlWithShadowParam(url);

        commonService.removeKeyOfNullValue(params);


        $http({
            method : "POST",
            url : url,
            data : $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {

            fn(null, response.data);

        }, function (response) {
            fn(new Error("注册失败"), false);
            $log.debug("注册失败");
            $log.debug(response);
        })
    };

    /**
     * 退出登录
     * update by lws 退出时，需要判断是否保持登录状态标记，如果保持状态则不能清空登录信息
     * jiehe2 2017年5月24日16:07:06 更新 如果isForceRemoveInfo为true, 表示强制擦除登录信息
     */
    this.logout = function (isForceRemoveInfo) {
        // 清除服务端session
        $.post(commonService.config.host + "/api/user/logout.action", {});


        //false不保存登录状态, 从缓存中获取状态值
        var isRemember;
        try {
            isRemember = JSON.parse(commonService.Cookies("signin.remember"));
        } catch (ex) {
            isRemember = false;
        }
        if (isForceRemoveInfo) {
            isRemember = false;
        }

        this.removeSignInfo(isRemember ? true : false);

        this.goHomePage();
    };
    /**
     * 退出登录
     */
    this.logoutOnlyHttpPost = function () {
        // 清除服务端session
        $.post(commonService.config.host + "/api/user/logout.action", {});
    };

    /**
     * 将用户信息保存在cookie中
     * @param userModel
     * @private
     */
    this._saveUserModel = function (userModel) {
        commonService.cookies("user.model", JSON.stringify(userModel));
        commonService.cookies("user_id", userModel.id);

        //保存account唯一主键，所有user_id都需要替换为account
        commonService.cookies("user_account", userModel.account);
    };

    /**
     * 从cookie中读取用户信息 (go to signin page when not signin)
     * @returns {userModel|null}
     */
    this.getUserModelFromLocalInfo = function () {
        try {
            return $.parseJSON(commonService.cookies("user.model"));
        } catch (ex) {
            $log.warn("从cookie中获取用户信息失败,cookie可能过期:" + ex.message);
            $location.path("/signin").search({
                _clearLayer : 1
            });
            throw new Error("用户未登录");
            // return null;
        }
    };

    this.getUserModelFromLocalInfoNoJump = function () {
        try {
            return $.parseJSON(commonService.cookies("user.model"));
        } catch (ex) {
            $log.warn("从cookie中获取用户信息失败,cookie可能过期:" + ex.message);
            throw new Error("用户未登录");
        }
    };


    // 保存登陆信息到cookie中
    this.saveSignInfo = function (account, password, isRemember) {
        commonService.Cookies("signin.account", account);
        commonService.Cookies("signin.password", password);
        commonService.Cookies("signin.remember", isRemember);
        commonService.Cookies("user.issignin", true, {expires : commonService.config.signinCookieExpires});
    };

    /**
     * 删除登陆信息
     * @param isRemember 需要携带是否保存登录状态的标志位。
     */
    this.removeSignInfo = function (isRemember) {
        //不保存状态，则删除登录信息
        if(!isRemember){
            commonService.removeCookie("signin.account");
            commonService.removeCookie("signin.password");
            commonService.removeCookie("signin.remember");
        }

        commonService.removeCookie("user.issignin");
        commonService.removeCookie("user.model");
        commonService.removeCookie("user_id");
    };

    /**
     * 从cookie中读取用户登录信息
     * @returns {{user: string, pass: string, remember: boolean}}
     */
    this.readStatusFromCookie = function () {
        var params  = {
            user : "",
            pass : ""
        };
        if (commonService.Cookies("signin.account") && commonService.Cookies("signin.password")) {
            params.user = commonService.Cookies("signin.account");
            params.pass = commonService.Cookies("signin.password");
            params.remember = JSON.parse(commonService.Cookies("signin.remember"));
        }

        //commonService.showTip(commonService.Cookies("signin.account"));
        return params;
    };

    /**
     * 从cookie中读取用户角色
     * @returns {number}
     */
    this.getRole = function () {
        try {
            var userModel = this.getUserModelFromLocalInfo();
            return userModel.role;
        } catch(ex) {
            return this.userRole.visitor;
        }
    };
    /**
     * 是否是学生
     * @returns {boolean}
     */
    this.isStudent = function () {
        return this.getRole() === this.userRole.student;
    };

    /**
     * 是否是老师
     * @returns {boolean}
     */
    this.isTeacher = function () {
        return this.getRole() === this.userRole.teacher;
    };

    /**
     * 是否是管理员
     * @returns {boolean}
     */
    this.isAdmin = function () {
        return this.getRole() === this.userRole.admin;
    };

    /**
     * 根据用户的角色不同跳转到相应的界面
     */
    this.goHomePage = function () {
        var role = this.getRole();
        if (role === this.userRole.student) {
            $location.path("/student/mytask");
        } else if (role === this.userRole.teacher) {
            $location.path("/teacher/ceshiList");
        } else if (role === this.userRole.admin) {
            $location.path("/admin/resourceManage/paperManage");
        } else {
            $location.path("/signin");
        }
    };

    /**
     * 重置密码
     * @param id    用户账号id
     * @param fn
     */
    this.resetPassword = function (account, fn, isShowShadow) {
        var url = commonService.config.host + "/admin/resetPassword.action?account=" + account;

        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method : "GET",
            url : url,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        })
    }

    /**
     * 禁用账号
     * @param id
     * @param fn
     */
    this.setUserStatus = function (account, status, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/admin/setUserStatus.action",
            data: $.param({
                account : account,
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
    };

    /**
     * 根据account从服务端加载用户信息
     * @param account
     * @param fn
     */
    this.getUserInfoFromServer = function (user, fn, isShowShadow) {
        if (_.isString(user)) {
            throw new Error("user cannnot be String");
        }
        var url = commonService.config.host + "/api/user/selectUserByAccount.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param({
                account : user.account,
                role : user.role
            }),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            try {
                response.data.data.photo = response.data.data.reserved1;
            } catch(ex) {
                // 因为  response.data.data可能为null 查不到对应用户的信息
            }
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * 根据account从服务端加载用户信息
     * @param account
     * @param fn
     */
    this.getUserInfoFromServer2 = function (fn, isShowShadow) {
        var user = _self.getUserModelFromLocalInfo();

        var url = commonService.config.host + "/api/user/selectUserByAccount.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param({
                account : user.account,
                role : user.role
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
     * 根据account从服务端加载用户信息
     * @param account
     * @param fn
     * @param isShowShadow
     */
    this.getUserInfoFromServerByAccount = function (account, fn, isShowShadow) {
        var url = commonService.config.host + "/api/teacher/selectStudentByAccount.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param({
                studentAccount: account
            }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response);
        });
    };

    /**
     * 更新用户个人信息
     * @param params 用户信息-键值对
     * @param fn
     * @deprecated
     */
    this.updateUserInfo = function (params, fn, isShowShadowWhenQuery) {
        this.updateUserInfoForPersonalInfoEdit(params, fn, isShowShadowWhenQuery);
    };

    /**
     * 更新用户个人信息 (for个人信息修改页面, 会删除一些字段如photo, password, role等)
     * @param params 用户信息-键值对
     * @param fn
     */
    this.updateUserInfoForPersonalInfoEdit = function (params, fn, isShowShadowWhenQuery) {
        var path = "";
        switch (parseInt(params.role)) {
            case _self.userRole.admin:
                path = "/api/user/updateAdminByAccount.action";
                break;
            case _self.userRole.teacher:
                path = "/api/user/updateTeacherByAccount.action";
                break;
            case _self.userRole.student:
                path = "/api/user/updateStudentByAccount.action";
                // 学生的这些字段不传会报错
                var fieldCannotBenull = ["naturalClass", "institution", "major", "grade", "school"];
                fieldCannotBenull.forEach(function (item) {
                    if (params[item] == null || params[item] == undefined) {
                        params[item] = "";
                    }
                });
                break;
            default:
                commonService.layerError("无法获取个人信息, 请重新登陆");
                throw new Error("无法获取个人信息, 请重新登陆");
        }

        var url = commonService.config.host + path;
        if (isShowShadowWhenQuery) {
            url = commonService.getUrlWithShadowParam(url)
        }
        try {
            delete params.photo;
            delete params.password;
            delete params.role;
        } catch (ex) {}

        $http({
            method: "POST",
            url: url,
            data: $.param(params),
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response)
        {
            //更新用户信息成功后，将更新信息返回控制层，更新本地界面显示状态
            fn(null, response.data, params);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * 修改密码
     * @param id 被修改密码的用户的主键id
     * @param oldPass 旧密码
     * @param newPass 新密码
     * @param fn
     */
    this.changePassword = function (userAccount, oldPass, newPass, fn, isShowShadow) {
        var url = commonService.config.host + "/api/user/updatePassword.action";
        if (isShowShadow) {
            url = commonService.getUrlWithShadowParam(url);
        }
        $http({
            method: "POST",
            url: url,
            data: $.param({
                userAccount : userAccount,
                oldPass : oldPass,
                newPass : newPass
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
     * 修改密码
     */
    this.changePasswordByEmail = function (conditions, fn) {
        var url = commonService.config.host + "/api/user/updatePasswordByEmail.action";

        url = commonService.getUrlWithShadowParam(url);

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
     * 根据文件名称获取用户头像图片的绝对地址
     * @param fileName
     * @returns {string}
     */
    this.getUserPhotoAbsolutePathByFileName = function (fileName) {
        if (/(^http|^\/\/)/.test(fileName)) {
            return fileName;
        } else {
            return commonService.config.host + "/api/user/photo.action?path=" + fileName;
        }
    };


    /**
     * 更新本地的用户信息
     * @param params
     */
    this.updateLocalUserInfo = function (params) {
        var _user = this.getUserModelFromLocalInfo();
        this._saveUserModel($.extend(_user, params));
    };


    /**
     * 根据id删除用户
     * @param id
     * @param fn
     */
    this.deleteUser = function (id, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/user/deleteAccount.action",
            data: $.param({
                userId : id
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

    this.checkRequiredUserInfo = function (account, fn) {
        $http({
            method: "POST",
            url: commonService.config.host + "/api/user/checkUserInfo.action",
            data: $.param({
                account: account
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

    this.goPersonalInfoEditPage = function () {
        var role = this.getRole();
        if (role === this.userRole.student) {
            $location.path("/student/personalInfoEdit");
        } else if (role === this.userRole.teacher) {
            $location.path("/teacher/personalInfoEdit");
        } else if (role === this.userRole.admin) {
            $location.path("/admin/personalInfoEdit");
        }
    };


    this.getAdminInfo = function (fn) {
        $http({
            method: "GET",
            url: commonService.config.host + "/admin/getAdminInfo.action",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            cache : false
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * 通过邮箱地址获取验证码
     * @param account (无需传递account参数)
     * @param emailAddr
     * @param fn
     */
    this.getEmailCodeService = function(account, emailAddr, fn, isNeedAccount)
    {
        $log.log("send email to " + emailAddr);

        var url;
        if (isNeedAccount) {
            url  = commonService.config.host + "/api/user/getValidateCodeByEmail.action?" + $.param({
                    account: account,
                    email : emailAddr
                });
        } else {
            url  = commonService.config.host + "/api/user/getValidateCodeForUpdateEmail.action?" + $.param({
                    email : emailAddr
                });
        }
        $http({
            method: "GET",
            url: url,
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            cache : false
        }).then(function (response) {
            fn && fn(null, response.data);
        }, function (response) {
            fn && fn(new Error(response.statusText), response.data);
        });
    };

    /**
     * 服务端验证输入的验证码是否合法
     * @param account
     * @param emailCode
     * @param fn
     */
    this.validateEmailCodeService = function(account, emailCode, fn)
    {
        $http({
            method: "GET",
            url: commonService.config.host + "/api/user/validateEmail.action?" + $.param({
                account: account,
                emailCode : emailCode
            }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            cache : false
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), response.data);
        });
    };
    
    this.isEnableEmailValidate = function (fn) {
        $http({
            method: "GET",
            url: commonService.getUrlWithShadowParam(commonService.config.host + "/api/user/getEmailValidateState.action"),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            cache : false
        }).then(function (response) {
            if (response.data.data == 1) {
                fn(true);
            } else {
                fn(false);
            }
        }, function (response) {
            fn(true);
        });
    }

}]);