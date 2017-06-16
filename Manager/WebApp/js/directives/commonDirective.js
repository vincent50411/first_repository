/**
 * angularjs 文件异步上传时候用到    file-model="name"
 */
cetsim.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);


cetsim.directive("repeatDone", function () {
    return function(scope, element, attrs) {
        if (scope.$last) { // all are rendered
            scope.$emit(attrs.repeatDone);
        }
    }
});

/**
 * 绑定 键盘回车键事件
 */
cetsim.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13) {
                scope.$apply(function(){
                    scope.$eval(attrs.ngEnter, {'event': event});
                });

                event.preventDefault();
            }
        });
    };
});

/**
 * 网页回退一个状态
 */
cetsim.directive("goBack", function ($window) {
    return function (scope, element, attrs) {
        element.on("click", function () {
            $window.history.back();
        })
    }
});

/**
 * 页面跳转指令
 * 使用该指令,控制器中跳转页面的方法必须是 $scope.goPage
 */
cetsim.directive("goPage", function ($timeout) {
    return function (scope, element, attrs) {
        $timeout(function () {
            try {
                if (isNaN(parseInt(attrs.goPage)) || parseInt(attrs.goPage) <= 0) {
                    //attrs.goPage is not number such as ...
                    return;
                }
            } catch (ex) {
                return;
            }
            element.on("click", function (e) {
                var page = parseInt($(e.target).attr("go-page"));
                scope.$eval("goPage(" + page + ")");
            })
        })
    }
});

/**
 * 页面跳转指令
 * 使用该指令,可以指定控制器中跳转页面的方法
 */
cetsim.directive("goPage2", function ($timeout) {
    return function (scope, element, attrs) {
        element.on("click", function (e) {
            if ($.isNumeric($(this).text())) {
                scope.$eval(attrs.goPage2);
            }
        })
    }
});

/**
 * checkbox toggle for angularjs
 * usage example :
 * <input type="checkbox" toggle-checkbox
                         data-toggle="toggle"
                         data-model="status"
                         data-yes="1"
                         data-no="0"
                         data-change="updatePaperFreeUseage(paper)">
 *
 * status means $scope.status
 * data-yes = "1" means 如果 $scope.status === 1,则为选中状态; 否则如果为0(data-no的属性值), 则为非选中状态
 * 该指令会在修改选中状态后, 1.修改data-model对应的变量值 2.执行data-change指定的函数
 */
cetsim.directive("toggleCheckbox", function ($timeout) {
    return function (scope, element, attrs) {
        $timeout(function () {
            var model = element.data("model"),
                status = parseInt(scope.$eval(model)),
                yesVal = parseInt(element.data("yes")),
                noVal = parseInt(element.data("no"));

            if (status == yesVal) {
                $(element).bootstrapToggle("on");
            } else {
                $(element).bootstrapToggle("off");
            }
            element.on("change", function () {
                if ($(element).prop('checked')) {
                    scope.$eval(model + "=" + yesVal);
                } else {
                    scope.$eval(model + "=" + noVal);
                }
                if (attrs.change) {
                    scope.$eval(attrs.change);
                }
            })
        })
    }
});

/**
 * hide-pop = "#container"    表示点击当前元素隐藏 #container这个dom元素
 */
cetsim.directive("hidePop", function (commonService) {
    return function (scope, element, attrs) {
        element.on("click", function () {
            var dom =  $(attrs.hidePop);
            var layerIndex = dom.attr("layer-index");
            if (layerIndex != undefined) {
                commonService.layerClose(parseInt(layerIndex));
            } else {
                dom.hide();
            }
        })
    }
});

/**
 * 参考 hidePop
 */
cetsim.directive("showPop", function (commonService, $rootScope) {
    return function (scope, element, attrs) {
        element.on("click", function () {
            // commonService.showPanel(angular.element(attrs.showPop));
            var title = false,
                width = 500;
            if (attrs.popTitle) title = attrs.popTitle;
            if (attrs.popWidth) width = parseInt(attrs.popWidth);
            // var index = layer.open();
            // $(attrs.showPop).attr("layer-index", index);
            commonService.layerOpen({
                type: 1,
                closeBtn: 1,
                anim: 2,
                area : width + "px",
                title : title,
                shadeClose: true,
                content: $(attrs.showPop),
                cancel : function () {
                    $rootScope.$broadcast("event.hidepop." + attrs.showPop.replace(/[^\w]/g, "")); // 过时的
                    $rootScope.$broadcast("event.cancelpop." + attrs.showPop.replace(/[^\w]/g, ""));// 推荐
                },
                end : function () {
                    $rootScope.$broadcast("event.endpop." + attrs.showPop.replace(/[^\w]/g, ""));
                }
            });


            $rootScope.$broadcast("event.showPop." + attrs.showPop.replace(/[^\w]/g, ""));
        })
    }
});

cetsim.directive("viewIn", function (commonService, $rootScope) {
    return function (scope, element, attrs) {
        element.on("click", function () {

            $(attrs.viewIn).removeClass("out-of-view");

        })
    }
});
cetsim.directive("viewOut", function (commonService, $rootScope) {
    return function (scope, element, attrs) {
        element.on("click", function () {

            $(attrs.viewOut).addClass("out-of-view");

        })
    }
});



/**
 * 根据成绩,cettype获取成绩的等级 (使用该方法而不是过滤器的优点是可以解决从服务端异步获取数据而导致无法正常显示的问题)
 * @example <span score-level-format data-score="{{paper.max_score}}" data-type="{{paper.type}}"></span>
 */
cetsim.directive("scoreLevelFormat", function (commonService, $timeout, $log) {
    return function (scope, element, attrs) {
        $timeout(function () {
            var jDom = $(element);
            var score = jDom.data("score"),
                type = jDom.data('type');

            if (["A", "B", "C", "D"].indexOf($.trim(score).toUpperCase()) !== -1) {
                $log.warn("scorelevelformat  GET ABCD, check it.");
                _deal(score);
            } else {
                commonService.getRankOfScoreByCetType(score, type, function (rank) {
                    _deal(rank);
                })
            }

            function _deal(rank) {
                var result = "--";
                if (rank) {
                    result = rank;
                }

                jDom.html(result);
                jDom.addClass(commonService.getColorClassByRank(rank));
                $log.log("score:" + score + "\ttype:" + type + "\t=>rank:" + result);
            }
        });
    }
});

/**
 * 禁止input组件的拷贝丶剪切丶粘贴操作
 */
cetsim.directive("disableCutCopyPaste", function ($log) {
    return function (scope, element, attrs) {
        element.on("cut copy paste",function(e) {
            $log.log("cut copy paste is diabled on this component");
            e.preventDefault();
        });
    }
});

/**
 * 文本超出指定的字符长度,则隐藏超出的字符并加上 ... ,而且全部文本放在title中鼠标hover显示全部(长度计算规则:string.length)
 */
cetsim.directive("maxCharOverShowTitle", function ($timeout, $log) {
   return function (scope, element, attrs) {
       $timeout(function () {
          try {
              var maxLength = 10,
                  text = element.text();
              if ($.isNumeric(attrs.maxCharOverShowTitle)) {
                  maxLength = parseInt(attrs.maxCharOverShowTitle);
              }
              if (text.length > maxLength) {
                  element.text(text.substr(0, maxLength) + "...");
                  element.attr("title", text);
              }
          } catch (ex) {
              $log.warn("maxCharOverShowTitle Error : " + ex.message);
          }
       })
   }
});

/**
 * 根据等级添加字体样色样式
 */
cetsim.directive("rankColorClass", function (commonService, $timeout) {
    return function (scope, element, attrs) {
       $timeout(function () {
           var rank = scope.$eval(attrs.rankColorClass),
               result;
           result = commonService.getColorClassByRank(rank);

           var clsList = ["color-green", "color-yellow", "color-orange", "color-red"];
           clsList.forEach(function (item) {
               element.removeClass(item);
           });
           element.addClass(result);
       })
    }
});


cetsim.directive('errorImage', function () {
    var fallbackSrc = {
        link: function postLink(scope, iElement, iAttrs) {
            iElement.bind('error', function () {
                angular.element(this).attr("src", iAttrs.errorImage || "static/images/avatar.png");
            });
        }
    };
    return fallbackSrc;
});

cetsim.directive("disableBackspace", function () {
    return function (scope, element, attrs) {
        element.on("keydown", function(e){
            if( e.keyCode === 8 ){ // 8 == backspace
                e.preventDefault();
            }
        });
    }
});

cetsim.directive("requiredStar", function () {
    return {
        restrict : "EA",
        link : function (scope, element, attrs) {
            element.html('<span class="input-field-require-star" title="必填项">*</span>');
        }
    };
});

cetsim.directive("taskProcessStatus", function (commonService) {
    return {
        restrict : "EA",
        link : function (scope, element, attrs) {
            var item = {
                start_time: scope.$eval(attrs.startTime),
                end_time: scope.$eval(attrs.endTime),
                use_count : scope.$eval(attrs.useCount),
                isforstu : scope.$eval(attrs.isforstu)
            };
            commonService.getServerTime(function (serverTime) {
                if (item.use_count && item.use_count > 0) {
                    item["_process_code"] = commonService.planProcessCode.FINISHED;
                } else {
                    if (moment(item.start_time).isAfter(serverTime)) {
                        item["_process_code"] = commonService.planProcessCode.NOTBEGIN;
                    } else if (moment(item.start_time).isBefore(serverTime) && moment(item.end_time).isAfter(serverTime)) {
                        item["_process_code"] = commonService.planProcessCode.PROCESSING;
                    } else if (moment(item.end_time).isBefore(serverTime)){
                        if (item.use_count == "0") {
                            item["_process_code"] = commonService.planProcessCode.EXPIRED;
                        } else {
                            item["_process_code"] = commonService.planProcessCode.FINISHED;
                        }
                    }
                }



                if (item['_process_code'] === commonService.planProcessCode.NOTBEGIN) {
                    element.html("<span style='color:gray'><span class='iconfont' style='color:gray'>&#xe7b0;</span>未开始</span>");
                } else if (item['_process_code'] === commonService.planProcessCode.PROCESSING) {
                    element.html("<span style='color:#fea100'><span class='iconfont' style='color:#fea100'>&#xe7b0;</span>进行中</span>");
                } else if (item['_process_code'] === commonService.planProcessCode.FINISHED) {
                    if (item.isforstu) {
                        element.html("<span style='color:#5bc37a'><span class='iconfont' style='color:#5bc37a'>&#xe7b0;</span>已完成</span>");
                    } else {
                        element.html("<span style='color:#5bc37a'><span class='iconfont' style='color:#5bc37a'>&#xe7b0;</span>已结束</span>");
                    }
                } else if (item['_process_code'] === commonService.planProcessCode.EXPIRED) {
                    element.html("<span style='color:#ff696b'><span class='iconfont' style='color:#ff696b'>&#xe7b0;</span>已过期</span>");
                }
            })
        }
    }
});

cetsim.directive("trainTaskState", function () {
    return {
        restrict : "EA",
        link : function (scope, ele, attrs) {
            var useCount = scope.$eval(attrs.useCount);
            if (useCount == 0) {
                ele.html("<span><img src='img/red-point.png' alt=''> 未练习</span>")
            } else {
                ele.html("<span><img src='img/green-point.png' alt=''> 已练习</span>")
            }
        }
    }
});

cetsim.directive("getCodeByEmailButton", function (commonService, userService) {
    return {
        restrict : "EA",
        link : function (scope, ele, attrs) {

            ele.html('<button class="common-btn ml10">获取验证码</button>');
            ele.find("button").on("click", function () {
                if ($(this).hasClass("disabled")) return;


                var email = scope.$eval(attrs.email);
                var isNeedAccount = scope.$eval(attrs.isneedaccount) === false ? false : true;

                var account,
                    interval;
                if (attrs.account) {
                    account = scope.$eval(attrs.account);

                    if ($.trim(account) === "") {
                        commonService.layerMsg("账号不能为空");
                        return;
                    }
                } else {
                    try {
                        var user = userService.getUserModelFromLocalInfoNoJump();
                        account = user.account;
                    } catch (ex) {
                        account = null;
                    }
                }

                var validation = [{
                    obj: email,
                    name : "邮箱",
                    filters: ['validationNotEmpty', 'validationEmailNotStrict']
                }];
                if (!commonService.formValidate(validation)) {
                    return;
                }
                

                ele.find("button").addClass("disabled");


                userService.getEmailCodeService(account, email, function (err, res) {
                    if (err || res.code != commonService.config.ajaxCode.success) {
                        var info = "发送验证码失败";
                        if (res.msg) {
                            info += ", 失败原因：" + res.msg;
                        }
                        commonService.layerMsg(info);
                        removeWaiting(interval);
                    }
                }, isNeedAccount);

                commonService.layerMsg("邮件已发送，请查收");
                interval = beginWaiting();
            });
            
            function beginWaiting() {
                var seconds = 60;
                do1();
                var interval = setInterval(do1, 1000);
                function do1() {
                    ele.find("button").html(seconds + "秒后重新发送");
                    seconds--;
                    if (seconds <= 0) {
                        removeWaiting(interval);
                    }
                }
                return interval;
            }

            function removeWaiting(interval) {
                clearInterval(interval);
                ele.find("button").removeClass("disabled").html("获取验证码");
            }
        }
    }
});

cetsim.directive("verifyCodeTip", function (userService, commonService, $log, $rootScope) {
    return {
        restrict: "EA",
        link: function (scope, ele, attrs) {
            var id = attrs.inputid;
            var tipid = attrs.tipid;
            if (tipid) {
                scope.$on("event.cleartip." + tipid, function () {
                    ele.html("");
                });
            }


            $("#" + id).on("blur", function () {
                var val = $(this).val(),
                    validExpression = attrs.status;
                if ($.trim(val) === "") {
                    return;
                }

                // var user = userService.getUserModelFromLocalInfo();
                userService.validateEmailCodeService(null, val, function (err, res) {
                    $log.log("verify code : " + val + "=> response code: " + res.code);
                    if (!err && res.code === commonService.config.ajaxCode.success) {
                        ele.html('<i class="icon icon-other-passed" title="验证码正确"></i>');
                        scope.$eval(validExpression + "=true");
                    } else {
                        ele.html('<i class="icon icon-other-warningred" title="验证码不正确"></i>');
                        scope.$eval(validExpression + "=false");
                    }
                })
            })
        }
    }
});


cetsim.directive("iconStar", ["commonService", "$log", "$filter", function (commonService, $log, $filter) {
    return {
        restrict : "EA",
        link : function (scope, ele, attrs) {
            try {
                attrs.$observe("score", function () {
                    var score = parseFloat(attrs.score);
                    var scoreValue0To5Formatter = $filter("scoreValue0To5Formatter");
                    try {
                        score = scoreValue0To5Formatter(score);
                    } catch (ex) {}

                    var area = [{
                        min : 0,
                        max : 1.999,
                        starNum : 1
                    },{
                        min : 1.999,
                        max : 2.999,
                        starNum : 2
                    },{
                        min : 2.999,
                        max : 3.999,
                        starNum : 3
                    },{
                        min : 3.999,
                        max : 4.999,
                        starNum : 4
                    },{
                        min : 5,
                        max : 5.1,
                        starNum : 5
                    }];

                    var starNum = 1;
                    area.forEach(function (item) {
                        if (score >= item.min && score < item.max) {
                            starNum = item.starNum;
                        }
                    });

                    var domArr = [];
                    for (var i = 0; i < starNum; i++) {
                        domArr.push('<i class="icon icon-other-star"></i>');
                    }

                    ele.html(domArr);
                })
            } catch (ex) {
                $log.warn("iconStar error " + ex.message);
            }
        }
    }
}]);

cetsim.directive("playSound", ["commonService", "$log", function (commonService, $log) {
    return function (scope, ele, attrs) {
        ele.on("click", function () {
            var src = scope.$eval(attrs.playSound);
            $log.log("play sound " + src);
            if (!src) {
                commonService.showTip("语音文件不存在");
                return;
            }

            var mediaURLValue = [];
            mediaURLValue.push({mp3: commonService.config.host + src});

            //视频播放器窗口html内容
            var mpePlayerHtmlStr = $("#answerMP3PlayerHtml").html();
            //由于html（）产生的副本，id重复命名，导致播放器界面无法正常加载，需要替换ID
            mpePlayerHtmlStr = mpePlayerHtmlStr.replace("jp_container_mp3_r", "jp_container_mp3").replace("jquery_jplayer_mp3_r", "jquery_jplayer_mp3");
            //弹出视频播放器窗口
            var index = commonService.popupPanel("录音播放", ['398px', '132px'], mpePlayerHtmlStr, true, false, false);
            commonService.playerSoundService(mediaURLValue, "#jquery_jplayer_mp3", "#jp_container_mp3", index);
        })
    }
}]);

cetsim.directive("combineToLine", ["commonService", "$log", function (commonService, $log) {
    return function (scope, ele, attrs) {
        attrs.$observe("combineToLine", function () {
            var list = scope.$eval(attrs.combineToLine);
            var line = scope.$eval(attrs.line);
            var score = scope.$eval(attrs.score);

            if (!_.isArray(list) || list.length <= 0) {
                return;
            }
            if ($.isNumeric(score) && score < 2) {  // score can be 0
                try {
                    line = line.substr(0, 1).toUpperCase() + line.substr(1);
                    line += ".";
                } catch (ex){}
                ele.html("<span class='color-error'>" + line  + "</span>");
                return;
            }
            var domArr = [];
            var span = $("<span>");
            var line2List = line.split(/\s+/);
            var list2Map = {};
            list.forEach(function (item) {
                list2Map[item.wordContent] = item;
            });

            line2List.forEach(function (item, i) {
                try {
                    var cleanItem = item.replace(",", "");
                    var scoreMap = list2Map[cleanItem];

                    try {
                        if (i == 0) {
                            cleanItem = cleanItem.substr(0, 1).toUpperCase() + cleanItem.substr(1);
                        }
                    } catch (Ex){}

                    if (scoreMap && scoreMap.totalScore < 2) {
                        domArr.push('<span class="color-bad">' + cleanItem + '</span>');
                    } else {
                        domArr.push(cleanItem);
                    }
                    if (item.indexOf(",") !== -1) {
                        domArr.push(",");
                    }
                } catch (ex) {
                    domArr.push(item);
                }
                if (i != line2List.length - 1) {
                    domArr.push("&nbsp;");
                }
            });

            domArr.push(".");
            ele.html(domArr);
        })
    }
}]);


cetsim.directive("clickScrollTo", function ($log) {
    return function (scope, ele, attrs) {
        ele.on("click", function () {
            var idList = ["cetsim-Self-introductions", "cetsim-task1", "cetsim-task2", "cetsim-task3", "cetsim-task4"],
                index = idList.indexOf(attrs.clickScrollTo);

            var id = "#" + attrs.clickScrollTo,
                jDom = $(id),
                winHeight = $(window).height(),
                offset = jDom.offset();

            $("html, body").animate({scrollTop : (offset.top - 250 + 30)})
        })
    }
});

cetsim.directive("goTop", function ($log) {
    return function (scope, ele, attrs) {
        ele.on("click", function () {
            $("html, body").animate({scrollTop : 0})
        })
    }
});

cetsim.directive("goTopBlock", function ($log) {
    return {
        restrict: "A",
        template: '<a class="sideToolbar-up" style="visibility: visible;" title="" href="javascript:void(0)"></a>',
        link : function (scope, ele, attrs) {
            ele.hide();
            ele.css({
                position: "fixed",
                bottom: 100,
                right: ($(window).width() - 1000) / 2 - 80 + "px"
            });

            var eleCssRight = _.throttle(function () {
                ele.css({
                    right: ($(window).width() - 1000) / 2 - 80 + "px"
                });
            }, 500);

            var eleCssShow = _.throttle(function () {
                if ($(window).scrollTop() > 100) {
                    ele.show();
                } else {
                    ele.hide();
                }
            }, 500);

            $(window).on('resize', eleCssRight);
            $(window).on("scroll", eleCssShow);
            ele.on("click", function () {
                $("html, body").animate({scrollTop : 0})
            })
        }
    };
});


/**
 * 表格按字段排序
 */
cetsim.directive("cetsimSortColumn", function ($log, $timeout) {
    return {
        restrict: "AE",
        scope : {
            column : '=',
            order : '=',
            text : '=',
            columnName : '=',
            refreshMethod : '&refreshMethod'
        },
        link: function (scope, ele, attrs) {
            scope.sortByThisColumn = function () {


                if (scope.column != scope.columnName) {
                    scope.order = "desc";
                    scope.column = scope.columnName;
                } else {
                    if (scope.order == null) {
                        scope.order = "desc";
                        scope.column = scope.columnName;
                    } else if (scope.order == 'desc') {
                        scope.order = "asc";
                        scope.column = scope.columnName;
                    } else {
                        scope.order = null;
                        scope.column = null;
                    }
                }
                $timeout(function () {
                    scope.refreshMethod();
                });

            }
        },
        templateUrl : "tpl2/includes/table-column-sort-piece.html"
    }
});

cetsim.directive("mouseWheelEventNotPropagationAndPreventDefault", function ($log) {
    return function (scope, ele, attrs) {
        ele.on("mousewheel", function (ev) {
            ev.stopPropagation();
            ev.preventDefault();
        })
    }
});

cetsim.directive("passwordStrength", function (commonService, $log) {
    return {
        restrict : "EA",
        link : function (scope, ele, attrs) {
            var id = attrs.id,
                dom = $("#" + id);
            dom.on("keyup", function () {
                try {
                    var val = $(this).val(),
                        strength = commonService.getPasswordStength(val);

                    ele.html("");
                    if(val !== "") {
                        switch (strength) {
                            case 1:
                                ele.html('<span class="pass-level ml10 pw-weak"><i class="level1"></i><i class="level2"></i><i class="level3"></i><em>弱</em></span>');
                                break;
                            case 2:
                                ele.html('<span class="pass-level ml10 pw-medium"><i class="level1"></i><i class="level2"></i><i class="level3"></i><em>中</em></span>');
                                break;
                            case 3:
                                ele.html('<span class="pass-level ml10 pw-strong"><i class="level1"></i><i class="level2"></i><i class="level3"></i><em>强</em></span>');
                                break;
                        }
                    }
                } catch (ex) {
                    $log.error(ex);
                }
            })
        }
    }
});

cetsim.directive("userImg", function (userService) {
    return {
        restrict : "EA",
        link : function (scope, ele, attrs) {
            attrs.$observe("src", function () {
                var src = attrs.src,
                    imgSrc;
                if (!src) {
                    // 没有头像， 显示默认头像
                    imgSrc = userService.getUserPhotoAbsolutePathByFileName("null");
                } else {
                    imgSrc = userService.getUserPhotoAbsolutePathByFileName(src);
                }

                imgSrc = new URI(imgSrc).setSearch("_imgTimeStamp", Math.random()).toString();
                ele.html('<img src="' + imgSrc + '" class="userface">');
            })
        }
    }
});

var generateSelectDom = function (list, scope, ele, attrs) {
    var select = $("<select>");
    select.addClass("eui-select");


    var isScopeModelInList = false;
    list.forEach(function (item) {
        if (item.val === scope.model) {
            isScopeModelInList = true;
        }
    });

    if (!isScopeModelInList) {
        select.append('<option value="' + scope.model + '">' + scope.model + '</option>')
    }

    list.forEach(function (item) {
        var option = $("<option>");
        option.val(item.val);   // null will be empty string if item.val === null
        option.text(item.key);
        select.append(option);
    });

    select.val(scope.model);

    select.on("change", function () {
        var val = $(this).val();
        if (val == "null") val = null;
        scope.$apply(function () {
            scope.model = val;
        });

        if(scope.onchange) {
            scope.onchange()
        }
    });
    select.attr("name", "select-" + (Math.random() + "").substring(2));
    if (scope.width) {
        select.css("width", parseInt(scope.width) + "px");
    } else {
        select.css("width", "210px");
    }
    ele.html(select);
    select.cssSelect();
};

cetsim.directive("selectInstitute", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                $log.log("directive selectInstitute scope model: " + scope.model);
                var allInstidutes = [{
                    key: "未填写",
                    val: ""
                }];
                commonService.config.institutesList.forEach(function (item) {
                    allInstidutes.push({
                        key: item,
                        val: item
                    })
                });

                generateSelectDom(allInstidutes, scope, ele, attrs);
            });
        }
    }
});




cetsim.directive("selectMajor", function (commonService, $timeout, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                $log.log("directive selectMajor scope model: " + scope.model);
                var list = [{
                    key: "未填写",
                    val : ""
                }];
                var majors = commonService.getAllMajorList();

                majors.forEach(function (item) {
                    list.push({
                        key : item,
                        val : item
                    })
                });

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});

cetsim.directive("selectGrade", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width :  "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                var defaultText = attrs.defaulttext;
                if (!defaultText) {
                    defaultText = "未选择";
                }
                var list = [{
                    key: defaultText,
                    val : ""
                }];
                commonService.getServerTime(function (time) {
                    var beginYear = moment(time).year() + 5;

                    for (var i = 0; i < 16; i++) {
                        list.push({
                            key: beginYear - i + "",
                            val: beginYear - i +  ""
                        })
                    }

                    generateSelectDom(list, scope, ele, attrs);
                });
            });
        }
    }
});

cetsim.directive("selectPaperType", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width :  "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                $log.log("directive select paper type scope model: " + scope.model);
                var list = [{
                    key: "全部",
                    val: ""
                }].concat(commonService.getPaperTypeOptions());

                try {
                    list.forEach(function (item) {
                        item.key = item.key.toUpperCase();
                    });
                } catch (ex){}

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});

cetsim.directive("selectPaperQuanxian", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width :  "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                var list = [{
                    key: "全部",
                    val: ""
                },{
                    key: "总权限开",
                    val: "1"
                },{
                    key: "总权限关",
                    val: "0"
                },{
                    key: "仅模拟测试开",
                    val: "2"
                },{
                    key: "仅自主考试开",
                    val: "3"
                }];

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});
cetsim.directive("selectPaperItemType", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            width :  "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                var list = [{
                    name : "全部题型",
                    key : "全部题型",
                    val : ""
                }];
                var list2 = (commonService.getPaperItemTypeV2Options());
                list2.forEach(function (item) {
                    list.push({
                        key: item.enName,
                        val: item.val
                    })
                });

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});


cetsim.directive("selectStatus", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                var list = [{
                    key: "全部",
                    val: ""
                }, {
                    key: "启用",
                    val: "1"
                }, {
                    key: "禁用",
                    val: "0"
                }];

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});
cetsim.directive("selectPlanStatus", function (commonService, $log) {
    return {
        restrict : "EA",
        scope : {
            model :  "=",
            onchange : "=",
            width : "="
        },
        link : function (scope, ele, attrs) {
            scope.$watch("model", function () {
                $log.log("directive selectGrade scope model: " + scope.model);
                var list = [{
                    key: "全部",
                    val: ""
                }, {
                    key: "未开始",
                    val: "0"
                }, {
                    key: "进行中",
                    val: "1"
                }, {
                    key: "已结束",
                    val: "2"
                }];

                generateSelectDom(list, scope, ele, attrs);
            });
        }
    }
});

cetsim.directive("togglePasswordVal", function () {
    return {
        restrict : "AE",
        link : function (scope, ele, attrs) {
            var s = {
                hide : "隐藏",
                show : "显示"
            };
            ele.html(s.show);
            ele.css("cursor", "pointer");
            ele.on("click", function () {
                var id = attrs.id,
                    dom = $("#" + id),
                    domie = $("#" + id + "-for-lowie");
                
                if (ele.html() === s.hide) {
                    if(domie.length > 0) {
                        domie.hide();
                        dom.show();
                    } else {
                        dom.attr("type", "password");
                    }
                    ele.html(s.show);
                } else {
                    if (domie.length > 0) {
                        domie.show();
                        dom.hide();
                    } else {
                        dom.attr("type", "text");
                    }
                    ele.html(s.hide);
                }
            })
        }
    }
});

cetsim.directive("toggleByStatusOfValidateEmail", function (userService) {
    return function (scope, ele, attrs) {
        userService.isEnableEmailValidate(function (bool) {
            if (!bool) {
                ele.hide();
            } else {
                ele.show();
            }
        })
    }
});

cetsim.directive("emailAutoComplete", function ($log) {
    return {
        restrict : "A",
        scope : {
            model : "="
        },
        templateUrl : "tpl2/includes/emailAutoComplete.html",
        link : function (scope, ele, attrs) {
            var id = attrs.inputid,
                input = $("#" + id);

            scope.hidePanel = function () {
                ele.find(".emailAutoCompletePanel").hide();
            };
            scope.showPanel = function () {
                ele.find(".emailAutoCompletePanel").show();
            };
            var adjustPosition = _.throttle(function () {
                var jDom = ele.find(".emailAutoCompletePanel"),
                    eleOffset = input.offset();
                jDom.css({
                    top: eleOffset.top - $(window).scrollTop() + input.outerHeight(),
                    width: input.outerWidth(),
                    left: eleOffset.left - $(window).scrollLeft()
                });
            }, 300);


            scope.hidePanel();
            scope.emailSuffixList = ["@qq.com", "@163.com", "@gmail.com"];
            scope.hoverIndex = -1;

            input.on("keyup", function (ev) {
                scope.showPanel();
                adjustPosition();

                if (input.val() != "" && $.trim(input.val()) != "") {
                    scope.$apply(function () {
                        scope.val = input.val().split('@')[0];
                    })
                } else {
                    scope.$apply(function () {
                        scope.hidePanel();
                        scope.val = input.val().split('@')[0];
                    })
                }
            });

            $(window).on("resize", function () {
                adjustPosition();
            });
            $(window).on("scroll", function () {
                adjustPosition();
            });

            input.on("click", function (ev) {
                ev.stopPropagation();
            });

            $(document).on("click", function () {
                scope.hidePanel();
            });
            
            // input.on("keyup", function (ev) {
            //
            //     if (ev.keyCode === 38) {
            //         scope.$apply(function () {
            //             if (scope.hoverIndex - 1 < 0) {
            //                 scope.hoverIndex = scope.emailSuffixList.length - 1;
            //             } else {
            //                 scope.hoverIndex = Math.abs((scope.hoverIndex - 1) % scope.emailSuffixList.length);
            //             }
            //         });
            //     }
            //     if (ev.keyCode === 40) {
            //         scope.$apply(function () {
            //             scope.hoverIndex = Math.abs((scope.hoverIndex + 1) % scope.emailSuffixList.length);
            //         });
            //
            //     }
            // });
            //
            // input.on("keyup", function (ev) {
            //     if (ev.keyCode === 13) {
            //         scope.$apply(function () {
            //             scope.selectThis(scope.hoverIndex);
            //         })
            //     }
            // });

            scope.selectThis = function (index) {
                scope.model = scope.val + scope.emailSuffixList[index];
                scope.hidePanel();
            };


        }
    }
});

cetsim.directive("listMaintain", ["commonService", "$log", function (commonService, $log) {
    return {
        restrict : "A",
        templateUrl : "tpl2/includes/listMaintain.html",
        scope : {
            model : '=',
            getchildrenfn : "&getchildrenfn"
        },
        link : function (scope, ele, attrs) {
            scope.items = [];
            scope.isSelectAll = false;
            scope.uuid = commonService.uuid();
            scope.model.forEach(function (item, i) {
                scope.items.push({
                    _id: i,
                    value: item,
                    _checked: false
                })
            });
            var getchildrenfn = scope.getchildrenfn();
            if (getchildrenfn) {
                scope.isShowToggleButton = true;
            } else {
                scope.isShowToggleButton = false;
            }

            scope.edit = function (item) {
                scope.curEditItem = $.extend({}, item);
            };

            scope.editUpdate = function () {
                commonService.layerClose($("#list-maintain-edit-panel-" + scope.uuid));

                scope.items.forEach(function (item) {
                    if (item._id === scope.curEditItem._id) {
                        item.value = scope.curEditItem.value;
                    }
                });

                syncScopeItemsToModel();
            };

            scope.remove = function (deleteItem) {
                commonService.layerConfirm("确定删除" + deleteItem.value + "吗？", function () {
                    scope.$apply(function () {
                        var index = _.findIndex(scope.items, deleteItem);
                        scope.items.splice(index, 1);
                        syncScopeItemsToModel();
                    })
                })
            };

            scope.toggleSelectAll = function () {
                var toState = !scope.isSelectAll; // from true to false, or false to true
                scope.items.forEach(function (item) {
                    item._checked = toState;
                })
            };

            scope.getChildren = function (item) {
                if (item._isOpen) {
                    item._isOpen = false;
                    return;
                }
                item._isOpen = true;
                item.children = [];
                getchildrenfn(item.value, function (arr) {
                    if (_.isArray(arr)) {
                        arr.forEach(function (one, i) {
                            item.children.push({
                                _id: i,
                                value: one,
                                _checked: false
                            });
                        })
                    }
                })
            };
            
            scope.ceshi = function () {
                $log.log(scope.items);
            };

            function syncScopeItemsToModel() {
                var list = [];
                scope.items.forEach(function (item) {
                    list.push(item.value);
                });
                scope.model = list;
            }
        }
    }
}]);

/**
 * 兼容低版本IE浏览器 checkbox
 */
cetsim.directive("cetsimCheckbox", function () {
    return {
        restrict : "A",
        templateUrl : "tpl2/includes/cetsim.checkbox.html",
        scope : {
            model : "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            scope.dochange = function () {
                if (scope.onchange) {
                    scope.onchange();
                }
            }
        }
    }
});


cetsim.directive("cetsimTimePicker", ["commonService", "$timeout", function (commonService, $timeout) {
    return {
        restrict : "A",
        template : '<input id="{{moduleId}}" type="text" class="webinput wdate" style="width: 148px;" ng-model="model" readonly disable-backspace="">',
        scope : {
            model : "=",
            onchange : "="
        },
        link : function (scope, ele, attrs) {
            var id = "cetsim-timepicker-" + commonService.uuid();
            scope.moduleId = id;

            $(document).on("click", "#" + id, function (ev) {
                WdatePicker({
                    el : id,
                    dateFmt:'yyyy-MM-dd',
                    onpicked : function (ev) {
                        scope.$apply(function () {
                            scope.model = ev.srcEl.value;
                            $timeout(function () {
                                if (scope.onchange) {
                                    scope.onchange();
                                }
                            });
                        })
                    },
                    oncleared : function () {
                        scope.$apply(function () {
                            scope.model = "";
                            $timeout(function () {
                                if (scope.onchange) {
                                    scope.onchange();
                                }
                            });
                        })
                    }
                });
            });
        }
    }
}]);


cetsim.directive("cetsimPlaceholder", ["commonService", function (commonService) {
    return function (scope, ele, attrs) {
        if (!commonService.isLessIE9()) return;
        var id = attrs.inputid,
            text = attrs.cetsimPlaceholder,
            offsetTop = attrs.offsetTop ? parseInt(attrs.offsetTop) : 0,
            offsetLeft = attrs.offsetLeft ? parseInt(attrs.offsetLeft) : 0;
        var jDom = $("#" + id),
            offset;


        var moveUp = function (ev) {
            if (ev) ev.stopPropagation();
            if (ele.hasClass("up")) return;
            ele.addClass("up");
            // ele.css({
            //     top: parseInt(ele.css("top")) - 20
            // })
            ele.html("");
            jDom.focus();
        };
        var moveDown = function (ev) {
            if (ev) ev.stopPropagation();
            if (jDom.val() !== "") return;
            ele.removeClass("up");
            // offset = jDom.offset();
            // ele.css({
            //     top : offset.top - $(window).scrollTop() + offsetTop,
            // })
            ele.html(text);
        };

        var adjustPosition = _.throttle(function (ev) {
            if (ev) ev.stopPropagation();
            offset = jDom.offset();
            ele.removeClass("up");
            ele.css({
                position: "fixed",
                top: offset.top - $(window).scrollTop() + offsetTop,
                left: offset.left - $(window).scrollLeft() + offsetLeft
            });
            if (jDom.val() != "") {
                moveUp();
            }
        }, 200);

        var onchange = function () {
            if (jDom.val() != "") {
                moveUp();
            } else {
                moveDown();
            }
        };

        jDom.removeAttr("placeholder");
        ele.html(text);
        adjustPosition();


        $(window).on('scroll', adjustPosition);
        $(window).on('resize', adjustPosition);
        jDom.on("focus", moveUp);
        jDom.on("blur", moveDown);
        jDom.on("click", moveUp);
        jDom.on("change", onchange);
        jDom.on("keyup", onchange);
        ele.on("click", moveUp);
        $(document).on("click", moveDown);

        
        ele.on("$destroy", function () {
            $(window).off('scroll', adjustPosition);
            $(window).off('resize', adjustPosition);
            jDom.off("focus", moveUp);
            jDom.off("click", moveUp);
            ele.off("click", moveUp);
            jDom.off("blur", moveDown);
            $(document).off("click", moveDown);
            jDom.off("change", onchange);
            jDom.off("keyup", onchange);
        })
    }
}]);