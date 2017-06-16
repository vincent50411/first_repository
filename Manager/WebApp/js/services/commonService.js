cetsim.service('commonService', ['$http', "$log", "$q", "$location", "$rootScope", "$filter", function ($http, $log, $q, $location, $rootScope, $filter) {
    var _self = this;
    this.Cookies = $.cookie;
    this.cookies = $.cookie;
    this.removeCookie = $.removeCookie;

    this.config = $.extend({
        connectionErrorMsg : "服务器繁忙,请待会重试",
        ajaxCode : {
            success : 1,
            successButInfo : 104, // 服务端返回成功,但是成功也有相应的消息需要前端显示出来
            error : 0
        },
        signinCookieExpires : 0.5, //半天
        /**
         * @deprecated
         */
        studentsImportTemplateDownloadUrl : "/api/teacher/downloadStuTemplate.action",
        studentsExcelImportUrl : "/api/teacher/addStudents.action",
        teachersImportUrl : "/admin/addTeachers.action",    // 教师Excel批量导入上传的服务端接口地址
        /**
         * @deprecated
         */
        teachersImportTemplateDownloadUrl : "/admin/downloadTeacherTemplate.action"  // 教师导入excel模板下载地址
    }, globalConfig);

    this.defaultSchool = globalConfig.school;

    this.institutes = globalConfig.institutes;

    this._userGenderOptions = [
        {
            id: "0",
            name: "男"
        }, {
            id: "1",
            name : "女"
        }, {
            id : "-1",
            name : "全部"
        }
    ];
    this._userGenderOptions2 = [
        {
            id: "0",
            name: "男"
        }, {
            id: "1",
            name : "女"
        }
    ];

    /**
     * 计划开始结束时间对应的状态
     * @type {{NOTBEGIN: number, PROCESSING: number, FINISHED: number}}
     */
    this.planProcessCode = {
        NOTBEGIN: 0,    // 未开始
        PROCESSING: 1,  // 进行中
        FINISHED: 2,     // 已完成
        EXPIRED : 3     // 已过期
    };

    /**
     * 试题类型
     * @deprecated
     */
    this.paperItemType = {

        "04_02" : "Read Aloud",
        "04_03" : "Question & Answer",
        "04_03_01" : "Question & Answer Question 1",
        "04_03_02" : "Question & Answer Question 2",
        "04_04" : "Individual Presentation",
        "04_05" : "Pair Work"

    };
    /**
     * 试题类型
     */
    this.paperItemTypeV2 = {
        "04_02": {
            name: "Read Aloud",
            zhName : "朗读",
            cssClass: "icon-other-typeread"
        },
        "04_03": {
            name: "Question & Answer",
            zhName : "问答",
            cssClass: "icon-other-typeqa"
        },
        "04_03_01": {
            name: "Question & Answer Question 1",
            zhName : "问答1",
            cssClass: "icon-other-typeqa"
        },
        "04_03_02": {
            name: "Question & Answer Question 2",
            zhName : "问答2",
            cssClass: "icon-other-typeqa"
        },
        "04_04": {
            name: "Individual Presentation",
            zhName : "个人表述",
            cssClass: "icon-other-typepersonal"
        },
        "04_05": {
            name: "Pair Work",
            zhName : "小组讨论",
            cssClass: "icon-other-typegroup"
        }
    };

    this.getPaperItemTypeV2Options = function () {
        var list = [];
        for (var key in _self.paperItemTypeV2) {
            list.push({
                key: key,
                val : key,
                name: _self.paperItemTypeV2[key].zhName,
                enName : _self.paperItemTypeV2[key].name
            })
        }
        return list;
    };

    /**
     * underscore
     * from underscore.js
     */
    this._ = _;

    //缓存CET不同等级下成绩区间配置信息
    var cetPaperScopeConfig;

    /**
     * 获取试卷类型数组
     * 供前端angularjs <select>组件的ngOptions属性使用
     * @returns {Array}
     */
    this.getPaperTypeOptions = function () {
        var list = [];
        for (var key in this.config.paperType) {
            list.push({
                key : key,
                val : this.config.paperType[key]
            })
        }
        return list;
    };

    /**
     * 获取试卷状态数组
     * 供前端angularjs <select>组件的ngOptions属性使用
     * @returns {Array}
     */
    this.getPaperStatusOptions = function () {
        var list = [];
        for (var key in this.config.paperStatus) {
            list.push({
                key : this.config.paperStatus[key].name,
                val : this.config.paperStatus[key].code
            })
        }
        return list;
    };

    /**
     * 文件异步上传
     * @param url
     * @param params
     * @param fn
     */
    this.formDataUpload = function (url, params, fn) {
        var payload = new FormData();

        for (var key in params) {
            payload.append(key, params[key]);
        }
        $http({
            url: url,
            method: 'POST',
            data: payload,
            headers: { 'Content-Type': undefined},
            //prevents serializing payload.  don't do it.
            transformRequest: angular.identity
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), null);
        });
    };


    /**
     * 提示消息给用户
     */
    this.showTip = function (message) {
        if (message == null) return;
        return layer.alert(message);
    };


    /**
     *
     * @param container html 容器的id属性
     * @param data  数据来源 格式: [{name:"", y:""},{name:"", y:""}]  name表示名称, y表示占据多少份
     * @param title 标题
     */
    this.highChartPie = function (container, data, title) {
        Highcharts.chart(container, {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: title
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>:<br> {point.yInfo}', //- {point.percentage:.1f} %
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            },
            series: [{
                name: '比例:',
                colorByPoint: true,
                data: data
            }]
        });
    };

    this.highChartLineGraph = function (container, data) {
        Highcharts.chart(container, {

            title: {
                text: data.title
            },
            //
            // subtitle: {
            //     text: 'Source: thesolarfoundation.com'
            // },

            plotOptions: {
                series: {
                    cursor: 'pointer',
                    point: {
                        events: {
                            click: function (ev) {
                                var i = ev.point.index;
                                var item = data.points[i];
                                $rootScope.$apply(function () {
                                    $location.path("/student/zzks/zzks_ckbg/" + item.recordCode + "/cet4")
                                });
                                $log.log('Category: ' + this.category + ', value: ' + this.y);
                            }
                        }
                    }
                }
            },

            tooltip: {
                valueSuffix: '',
                formatter: function() {
                    var i = this.point.index;
                    var item = data.points[i];
                    var scoreValueFatter = $filter("scoreValueFormater");
                     try{
                         return item.paperName + "<br>成绩：" + scoreValueFatter(item.score);
                     } catch (ex) {
                         return item.paperName + "<br>成绩：" + item.score;
                     }
                }
            },

            yAxis: {
                min: 0,
                max: 20,
                tickInterval: 1,
                title: {
                    text: ''
                }
                // categories : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
            },
            xAxis: {
                title: {
                    text: ''
                },
                type: 'datetime',
                dateTimeLabelFormats: {
                    day: '%d %b %Y'    //ex- 01 Jan 2016
                },
                categories :  data.xList
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle'
            },

            series: [{
                name: '成绩折线图',
                data: data.valueList
            }
                // , {
                //     name: 'Manufacturing',
                //     data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
                // }, {
                //     name: 'Sales & Distribution',
                //     data: [11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387]
                // }, {
                //     name: 'Project Development',
                //     data: [null, null, 7988, 12169, 15112, 22452, 34400, 34227]
                // }, {
                //     name: 'Other',
                //     data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
                // }
            ]

        });
    };


    /**
     * 检查客户机是否安装了指定插件，通过验证指定插件来判断是否安装了考试机
     * 插件和考试机绑定安装
     * @returns {boolean}
     */
    this.checkInstallClient = function()
    {
        var  result = false;

        try
        {
            //通过创建activeX对象来判断客户端是否安装了考试机
            //new   ActiveXObject("iFLytekClientActiveX.ComBridgeCETSim");

            new  ActiveXObject("iFLytekClientActiveX.CETSim");
            result =  true;
        }
        catch(e)
        {
            //如果客户端没有安装考试机，则插件创建异常
        }

        return result;
    };

    /**
     * 检查客户机使用的IE浏览器位数，由于插件是32位的不能运行在64位IE浏览器上
     * @returns {boolean}
     */
    this.checkIEIs64Version = function()
    {
        var is64VersionIE = false;
        var ieVersion = window.navigator.platform;

        if(ieVersion.indexOf('64') >= 0)
        {
            is64VersionIE = true;
        }

        return is64VersionIE;
    };




    //模态化窗口弹出服务
    // this.openModalWinService = function($scope, templateUrl, controller, widthValue, heightValue){
    //     //模态化窗口对象依赖名称固定modalInstance
    //     var modalInstance = $modal.open({
    //         templateUrl : templateUrl,
    //         controller : controller, // specify controller for modal
    //         backdrop:"static",
    //         size : {
    //             width: widthValue,
    //             height: heightValue
    //         },
    //         resolve : {
    //             Item : function() {
    //                 return $scope.Item;
    //             }
    //         }
    //     });

    //     // modal return result
    //     modalInstance.result.then(function(selectedItem) {
    //         $scope.selected = selectedItem;
    //     }, function() {
    //         $log.info('Modal dismissed at: ' + new Date());
    //     });

    //     return modalInstance;
    // };

    /**
     * 音频播放器
     * @param mp3FileList mp3音频文件列表
     * @param jplayer_video_dom  播放器dom对象
     * @param jp_container_video_dom 播放器容器dom对象
     * @param index layer弹出层下标，自动以关闭窗口需要
     */
    this.playerSoundService = function(mp3FileList, jplayer_video_dom, jp_container_video_dom, index)
    {
        var mp3PlayIndex = 0;

        var palyMp3 = function(){
            new jPlayerPlaylist({
                jPlayer: jplayer_video_dom,
                cssSelectorAncestor: jp_container_video_dom
            }, mp3FileList,{
                ready: function () {
                    //自动播放
                    $(".jp-play").trigger("click");
                },
                ended: function()
                {
                    //播放完一个视频片段
                    mp3PlayIndex += 1;

                    //音频播放完毕后，自动关闭播放器(存在多个片段)
                    if(mp3PlayIndex == mp3FileList.length)
                    {
                        layer.close(index);
                    }
                },
                swfPath: "lib/jplayer/jquery.jplayer.swf",
                supplied: "mp3",
                wmode: "window",
                useStateClassSkin: true,
                autoBlur: false,
                smoothPlayBar: true,
                keyEnabled: true
            });
        }

        //初始化
        palyMp3();
    };

    /**
     * 视频播放器
     * @param videoFileList  视频文件列表
     * @param playerSize     播放器大小
     * @param jplayer_video_dom 播放器DOM对象字符串
     * @param jp_container_video_dom 播放器容器dom对象字符串
     * @param index layer弹出层下标
     */
    this.playerVideoService = function(videoFileList, playerSize, jplayer_video_dom, jp_container_video_dom, index)
    {
        //总视频列表
        var videoFileList = videoFileList;

        //记录视频片段播放完成对下标
        var videoPlayIndex = 0;

        //播放列表形式
        var videoPlayerList = function () {
            new jPlayerPlaylist({
                jPlayer: jplayer_video_dom,
                cssSelectorAncestor: jp_container_video_dom
            }, videoFileList, {
                ready: function () {
                    //自动播放
                    $(".jp-play").trigger("click");
                },
                swfPath: "lib/jplayer/jquery.jplayer.swf",
                supplied: "m4v",
                size: playerSize,
                useStateClassSkin: true,
                autoBlur: false,
                smoothPlayBar: true,
                keyEnabled: true,
                ended: function()
                {
                    //播放完一个视频片段
                    videoPlayIndex += 1;

                    //视频播放完毕后，自动关闭播放器(存在多个视频片段)
                    if(videoPlayIndex == videoFileList.length)
                    {
                        layer.close(index);
                    }
                },
                error: function (event) {
                    $(".jp-next").trigger("click");
                }
            });

        }

        //初始化播放器
        videoPlayerList();
    };

    /**
     * 获取taskId成绩等级区间
     * @param studentTaskId
     * @param fn(Array|null)
     */
    this.getTaskScorelevel = function (task_id, fn) {
        $http({
            url: this.config.host + "/api/common/taskscorelevel.action?" + $.param({
                taskId : task_id
            }),
            method: 'GET',
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            },
            cache : true
        }).then(function (response) {
            if (response.data.code === _self.config.ajaxCode.success) {
                fn(response.data.data);
            } else {
                fn(null);
            }
        }, function (response) {
            fn(new Error(response.statusText), null);
        });
    };

    /**
     * return cet4 or cet6 or null
     */
    this.getPaperTypeNameByCode = function (code) {
        var _code = parseInt(code);
        if (_code === this.config.paperType.cet4) {
            return "cet4";
        } else if (_code === this.config.paperType.cet6) {
            return "cet6";
        } else {
            return code;
        }
    };

    /**
     * 根据试卷类型(cet4 or cet6)获取成绩的等级
     * @param score
     * @param cetType value is "cet4" or "cet6",  or can be code too!!
     * @param fn(levelString|null)
     */
    this.getRankOfScoreByCetType = function (score, cetType, fn) {
        if (!$.isNumeric(score)) {
            fn(null);
            return;
        }
        if ($.isNumeric(cetType)) {
            cetType = this.getPaperTypeNameByCode(cetType);
        } else if (!_self._.isString(cetType) || $.trim(cetType) === "") {
            fn(null);
            return;
        }

        this.getScoreLevelOf2CetTypes().then(function (response) {
            if (response.data.code === _self.config.ajaxCode.success) {

                cetType = cetType.toUpperCase();
                var arr = response.data.data[cetType];
                var rank = "";
                arr.forEach(function (item) {
                    if (score >= item.min && score < item.max) {
                        rank = item.name;
                    }
                });
                fn(rank);
            } else {
                fn(null);
            }
        }, function () {
            fn(null);
        });
    };

    /**
     * 根据等级获取相应的css class
     * @param rank {String} rank must be A|B|C|D
     * @return {string}
     */
    this.getColorClassByRank = function (rank) {
        var result = "";
        if (rank === "A") {
            result = "color-green";
        } else if (rank === "B") {
            result = "color-yellow";
        } else if (rank === "C") {
            result = "color-orange";
        } else if (rank === "D") {
            result = "color-red";
        }

        return result;
    };
    
    /**
     * 获取成绩等级区间
     * 返回cet4 和 cet6俩种不同标准的成绩等级区间
     */
    this.getScoreLevelOf2CetTypes = function (isTeacher) {
        var url;
        if (isTeacher) {
            url = _self.config.host + "/api/common/testPlanScoreDesc.action";
        } else {
            url = _self.config.host + "/api/common/scorelevel.action";
        }
        return $http({
            url: _self.getUrlWithShadowParam(url),
            method: 'GET',
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            },
            cache : true
        });
    };
    
    /**
     * 从服务端获取全局的分数等级配置
     */
    this.getScorelevel = function () {
        $http({
            url: _self.getUrlWithShadowParam(this.config.host + "/api/common/scorelevel.action"),
            method: 'GET',
            headers: {
                "Content-Type" : "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            if (response != null && response.data.code === _self.config.ajaxCode.success)
            {
                cetPaperScopeConfig = response.data.data;
            }
        }, function (response) {

        });
    };



    /**
     * 全局的分数等级配置
     * @param score
     * @param paperType 试卷类型 0：CET4; 1:CET6
     * @param fn
     */
    this.getGlobRankOfScore = function(score, paperType, fn){
        var arr;
        if(paperType == "0" && cetPaperScopeConfig != undefined)
        {
            arr = cetPaperScopeConfig.CET4;
        }
        else if(paperType == "1" && cetPaperScopeConfig != undefined)
        {
            arr = cetPaperScopeConfig.CET6;
        }

        if (!arr)
        {
            fn("");
            return;
        }
        arr.forEach(function(element) {
            if(score < element.max && score >= element.min) {
                fn(element.name);
            }
        });
    };

    /**
     * 获取成绩排名和评语
     * @param score
     * @param paperType
     * @param fn
     */
    this.getGlobRankAndDescriptionOfScore = function(score, paperType, fn){
        var arr;
        if(paperType.toUpperCase() == "CET4" && cetPaperScopeConfig != undefined)
        {
            arr = cetPaperScopeConfig.CET4;
        }
        else if(paperType.toUpperCase() == "CET6" && cetPaperScopeConfig != undefined)
        {
            arr = cetPaperScopeConfig.CET6;
        }

        if (!arr)
        {
            fn("", "");
            return;
        }
        arr.forEach(function(element) {
            if(score < element.max && score >= element.min) {
                fn(element.name, element.description);
            }
        });
    };


    /**
     * 获取分数的等级
     * @param taskId
     * @param score
     * @deprecated
     */
    this.getRankOfScore = function (taskId, score, fn) {
        this.getTaskScorelevel(taskId, function (arr) {
            if (!arr) {
                fn("");
                return;
            }
            var map = {};
            var theMax = -1,
                theName = "";
            arr.forEach(function (item) {
                for (var i = item.min; i < item.max; i++) {
                    map[i] = item.name;
                }
                if (theMax < item.max) {
                    theMax = item.max;
                    theName = item.name;
                }
            });
            map[theMax] = theName;
            fn(map[Math.ceil(score)]);
        })
    };


    /**
     * change {one : {code : 1, name : "xx"}, two : {code : 2, name : "xx2"}}
     * To   {1 : "xx", 2 : "xx2"}
     * Then map["1"] get "xx" fast
     * @param map
     * @returns {{}}
     * @private
     */
    var _tool1 = function (map) {
        var newMap = {};
        for (var key in map) {
            newMap[map[key].code] = map[key].name;
        }
        return newMap;
    };
    /**
     * 根据examState状态码获取对应的中文名称
     * @param code
     * @returns {*}
     */
    this.getExamStateByCode = function (code) {
        var map = _tool1(this.config.examState);
        return map[code];
    };

    /**
     * 根据flowState状态码获取对应的中文名称
     * @param code
     * @returns {*}
     */
    this.getFlowStateByCode = function (code) {
        var map = _tool1(this.config.flowState);
        return map[code];
    };

    var _maxIndex  = 1000;
    this.getMaxZindex = function () {
        _maxIndex = ++_maxIndex > layer.zIndex ? _maxIndex : layer.zIndex;
        return _maxIndex;
    };

    /**
     * 显示弹窗, 该方法把一个dom元素放在页面正中间且最上方(z-index最大)
     * 注意showpanel 的弹窗 z-index 值都比layer的弹窗z-index小
     * @deprecated 请使用layer 弹出弹框 commonService.layerOpen
     * @param dom
     */
    this.showPanel = function (dom) {
        dom = $(dom);
        dom.css({
            position: "fixed",
            top : "50%",
            left : "50%",
            // marginTop : - dom.height() / 2 + "px",
            // marginLeft : - dom.width() / 2 + "px",
            transform: "translate(-50%, -50%)",
            border : "1px solid #eee",
            boxShadow: "#ccc 0px 1px 20px",
            backgroundColor:"white",
            zIndex : this.getMaxZindex()
        });
        dom.addClass("clearfix");
        dom.fadeIn();
    };


    /**
     * 使用layer弹出 弹出层(建议的用法)
     * 目前使用该方法 content必须是 jQuery dom对象
     * @param config
     * {
     *      type: 1,
     *      closeBtn: 1,
     *      anim: 2,
     *      area: "300px",
     *      title: false,
     *      shadeClose: true,
     *      content: ""
     *  }
     *  @return layer index
     */
    this.layerOpen = function (config) {
        var _config = $.extend({
            type: 1,
            closeBtn: 1,
            anim: 2,
            area: "300px",
            title: false,
            shadeClose: true,
            content: ""
        }, config);

        try {   // _config.content can be jQuery Dom, html String, dom
            if (_.isElement(_config.content) || _config.content instanceof jQuery) {
                _config.content = $(_config.content);
                if (!_config.content.is(":hidden") && _config.content.attr("layer-index") !== undefined) {
                    $log.log("layerOpen : content is already opened, do not open");
                    return;
                }
            }
        } catch(ex) {
            $log.log(ex.message);
        }
        var index = layer.open(_config);
        if (_config.content instanceof jQuery) {
            _config.content.attr("layer-index", index);
        }
        return index;
    };

    /**
     * the same as layerOpen
     * @type {any}
     */
    this.showLayer = this.layerOpen;

    /**
     * 关闭弹窗
     * @param jDom jQuery dom对象 | layer index | type String (close all of specific type)
     */
    this.layerClose = function (jDom) {
        try {
            if (jDom instanceof jQuery) {
                var index = parseInt(jDom.attr("layer-index"));
                layer.close(index);
            } else if ($.isNumeric(jDom)) {
                layer.close(parseInt(jDom));
            } else if (_.isString(jDom)) {
                layer.closeAll(jDom);
            }
        } catch(ex) {
            $log.warn("layerClose exception, info :");
            $log.warn(ex.message);
        }
    };

    var _initLayerSuccessAndErrorDom = function (dom, info, isSuccess) {
        dom.find(".cet-icon").remove();
        if (isSuccess) {
            dom.find(".pop-prompt").prepend('<i class="icon cet-icon icon-other-success"></i>');
        } else {
            dom.find(".pop-prompt").prepend('<i class="icon cet-icon icon-other-error"></i>')
        }
        dom.find(".information").html(info);

        dom.find(".btn-confirm").show();
        dom.find(".btn-return").hide().html("确定");
    };

    /**
     * 全局的成功提示
     * @param title
     * @param info
     * @param [isGoback] 可选, 表示返回的地址
     * @param [goBackTitle] 可选
     */
    this.layerSuccess = function (title, info, isGoback, goBackTitle) {
        if (info === undefined) {
            info = title;
            title = "提示";
        }
        var dom = $("#global-info-panel");
        _initLayerSuccessAndErrorDom(dom, info, true);

        if (isGoback === true && goBackTitle !== undefined) {
            dom.find(".btn-confirm").hide();
            dom.find(".btn-return").show().html(goBackTitle).show();
        }
        this.layerOpen({
            content : dom,
            area : "440px",
            title : title
        })
    };

    /**
     * 全局的失败提示
     * @param title
     * @param info
     */
    this.layerError = function (title, info) {
        if (info === undefined) {
            info = title;
            title = "提示";
        }
        var dom = $("#global-info-panel");
        _initLayerSuccessAndErrorDom(dom, info, false);
        this.layerOpen({
            content: dom,
            area: "440px",
            title: title
        })
    };

    /**
     * 询问框 (无须手动关闭confirm弹框)
     * @param question  询问问题
     * @param [onConfirm]
     * @param [onCancel]
     * @return {*}
     */
    this.layerConfirm = function (question, onConfirm, onCancel) {
        var index = layer.confirm(question, {
            btn: ['确定','取消'],
            skin : "cetsim-confirm",
            title : "提示"
        }, function(){
            _self.layerClose(index);
            onConfirm && onConfirm();
        }, function(){
            _self.layerClose(index);
            onCancel && onCancel();
        });
        return index;
    };

    /**
     * layer 提示消息 (3s后自动关闭)
     * @param msg
     */
    this.layerMsg = function (msg) {
        layer.msg(msg);
    };


    /**
     * @deprecated
     * @param jDom
     */
    this.showInCenterOnScreen = function (jDom) {
        var width = jDom.width(),
            height = jDom.height(),
            winWidth = $(window).width(),
            winHeight = $(window).height();

        height = height === 0 ? 260 : height;


        jDom.css({
            position: "fixed",
            zIndex : _self.getMaxZindex(),
            left : "50%",
            top : "50%",
            marginTop : -(height) / 2 + "px",
            marginLeft : - (width) / 2 + "px",
            opacity : 1
        });
    };

    this.popupPanel = function(titleValue, areaValue, contentValue, maxminValue, scrollbarValue, shadeCloseValue)
    {
        //统一弹出框的高度和宽度
        if(areaValue == null)
        {
            areaValue = ['970px', '570px'];
        }

        var index = layer.open({
            type: 1,
            title: false,
            closeBtn: true,
            area: areaValue,
            shadeClose: shadeCloseValue, //点击遮罩关闭
            scrollbar: true,//禁止浏览器滚动
            maxmin: false,
            skin: 'layui-layer-nobg', //没有背景色
            content: contentValue
        });

        return index;
    };

    /**
     * 加载层
     * @param timeValue 单位毫秒
     * @returns {*}
     */
    this.loadingWin = function(timeValue)
    {
        var loadingIndex = layer.load(0, {
            shade: [0.1,'#fff'],
            time: timeValue
        });

        return loadingIndex;
    };


    this.closePanel = function (index) {
        layer.close(index);
    };

    this.closeAllPanel = function () {
        layer.closeAll();
    };

    this.getBrowType = function()
    {
        //取得浏览器的userAgent字符串
        var userAgent = navigator.userAgent;
        var isOpera = userAgent.indexOf("Opera") > -1;
        //判断是否Opera浏览器
        if (isOpera)
        {
            return "Opera"
        };

        //判断是否Firefox浏览器
        if (userAgent.indexOf("Firefox") > -1)
        {
            return "FF";
        }

        //判断是否Chrome浏览器
        if (userAgent.indexOf("Chrome") > -1){
            return "Chrome";
        }

        //判断是否Safari浏览器
        if (userAgent.indexOf("Safari") > -1) {
            return "Safari";
        }

        //判断是否IE浏览器
        if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
            return "IE";
        };

        return "IE";
    };

    //检测IE浏览器版本号
    this.getBrowserVersion = function(){
        var userAgent = navigator.userAgent.toLowerCase();
        if(userAgent.match(/msie ([\d.]+)/)!=null)
        {
            //ie6--ie9
            uaMatch = userAgent.match(/msie ([\d.]+)/);
            return 'IE'+uaMatch[1];
        }
        else if(userAgent.match(/(trident)\/([\w.]+)/))
        {
            uaMatch = userAgent.match(/trident\/([\w.]+)/);
            switch (uaMatch[1])
            {
                case "4.0":
                    return "IE8" ;
                    break;
                case "5.0":
                    return "IE9" ;
                    break;
                case "6.0":
                    return "IE10";
                    break;
                case "7.0":
                    return "IE11";
                    break;
                default:
                    return "undefined" ;
            }
        }
        return "undefined";
    };

    /**
     * 显示全屏的等待遮罩
     */
    this.showWaitingShadow = function () {
        // var dom = $("#ajax-waiting-modal");
        // dom.css({zIndex : this.getMaxZindex()});
        // dom.show();
        return layer.load(0, {
            shade: [0.1,'#fff']
        });
    };

    /**
     * 隐藏全屏的等待遮罩
     */
    this.hideWaitingShadow = function (index) {
        if (_.isNumber(index)) {
            layer.close(index);
        } else {
            layer.closeAll("loading");
        }
    };


    /**
     * URI 操作
     * from URI.js
     * @type {any}
     */
    this.URI = URI;

    /**
     * 为url加上随机的时间戳
     * @param url
     * @return {string|*}
     */
    this.addRandomTimestampForUrl = function (url) {
        return this.URI(url).addQuery({
            t: Math.random()
        }).toString();
    };


    /**
     * 将url转换成 请求中加载全局shadow的 url (相应的拦截器是loadingService)
     * @param url
     * @return {string|*}
     */
    this.getUrlWithShadowParam = function (url) {
        return new this.URI(url).addSearch("_showGlobalShadowWhenQuery", true).toString()
    };

    /**
     * 获取学校的所有专业
     * @return {Array}
     */
    this.getAllMajorList = function () {
        var majorListMap = this.config.majorListMap;
        var arr = [];
        for (var key in majorListMap) {
            arr = arr.concat(majorListMap[key]);
        }
        return arr;
    };

    /**
     * 获取某一学院下的所有专业
     * @param institute
     * @return Array
     */
    this.getMajorListOf = function (institute) {
        return this.config.majorListMap[institute];
    };

    /**
     * 获取所有学院信息（2017.6.8后从服务端加载该配置信息）
     */
    this.getInstitutesList = function (fn) {
        // TODO
        fn(null, _self.config.institutesList);
    };

    this.layerImportProgress = function (progress) {
        var jDom = $("#importProgressPanel");
        var textDom = jDom.find(".progress-val"),
            progressDom = jDom.find(".progressinner");

        if (jDom.is(":hidden")) {
            this.layerOpen({
                title : "导入进度",
                area : "320px",
                content : jDom
            });
            textDom.html("");
            progressDom.css({
                width : 0
            })
        }

        textDom.html(progress);
        progressDom.css({
            width: progress
        });
    };

    /**
     * 获取服务端同步时间，格式为yyyy/MM/dd HH:mm
     * @param fn
     */
    this.getServerSyncTime = function(fn)
    {
        $http({
            url: this.config.host + "/api/common/servertime.action",
            method: 'POST',
            headers: { "Content-Type" : "application/x-www-form-urlencoded"}
        }).then(function (response)
        {
            fn(null, response.data.data);
        }, function (response) {
            fn(new Error(response.statusText), null);
        });
    };

    var _serverTimeObj = {
        serverTimeWhenGET: null,
        localTimeWHenGet: null,
        isOK : false
    };
    /**
     * 获取服务端时间
     * @return {Date}
     */
    this.getServerTime = function (fn) {
        // 如果未初始化
        if (_serverTimeObj.serverTimeWhenGET === null) {
            $http({
                url: this.config.host + "/api/common/servertime.action",
                method: "GET",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                cached : true
            }).then(function (response) {
                // data is timestamp such as 1492049841246
                try {
                    _serverTimeObj.serverTimeWhenGET = parseInt(response.data.data);
                    _serverTimeObj.localTimeWHenGet = +new Date();
                    _serverTimeObj.isOK = true;
                } catch (ex) {
                    _serverTimeObj.isOK = false;
                }
                fn && fn(new Date(_serverTimeObj.serverTimeWhenGET));
            }, function (response) {
                _serverTimeObj.isOK = false;
                $log.warn("时间加载失败");
                fn && fn(+ new Date());
            });
        } else {
            if (_serverTimeObj.isOK) {
                $log.log("|-----------------------------------------------|");
                $log.log("初始化获取服务端的时间是:");
                $log.log(new Date(_serverTimeObj.serverTimeWhenGET));
                $log.log("再次获取服务端时间, 客户端已经过了" + (+new Date() - _serverTimeObj.localTimeWHenGet )+ "(ms), 得到服务端时间是 : " + (new Date(_serverTimeObj.serverTimeWhenGET + (+ new Date() - _serverTimeObj.localTimeWHenGet))));
                $log.log("|-----------------------------------------------|");
                fn && fn(new Date(_serverTimeObj.serverTimeWhenGET + (+ new Date() - _serverTimeObj.localTimeWHenGet)));
            } else {
                $log.log("服务端时间加载失败, 调用getServerTime函数返回的是客户端时间");
                fn && fn(new Date());
            }
        }
    };
    // 初始化服务端时间
    this.getServerTime();


    /**
     * 获取年级枚举数组
     * @param fn
     */
    this.getGradesList = function (fn) {
        $http({
            url: this.config.host + "/admin/gradelist.action",
            method: 'GET',
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            cache: true
        }).then(function (response) {
            if (response.data.code === 1) {
                fn(response.data.data);

                $log.log("年级列表数据初始化成功, 数据如下:");
                $log.log(response.data.data);
            } else {
                onError(fn);
            }
        }, function (response) {
            onError(fn);
        });

        function onError(fn) {
            fn(_self.config.gradesList);
            $log.log("grade list 数据加载失败, 使用config.js中配置的年级列表");
        }
    };

    /**
     * 是否是正确格式的手机号码 (strict为false时,空字符串同样通过校验)
     * @param val
     * @return {boolean}
     */
    this.isPhoneNumber = function (val, strict) {
        if (strict === false) {
            return /^\d{11}$/.test(val) || val === "" || val === null || val === undefined;
        } else {
            return /^\d{11}$/.test(val);
        }
    };

    /**
     * 是否是密码(只要求是不包含空格的至少六位字符组成)
     * @param val
     * @return {boolean}
     */
    this.isPassword = function (val) {
        if (!val) {
            return false;
        }
        return /^\S{6,}$/.test(val);
    };

    this.isEmail = function (email, strict) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (strict === false) {
            return re.test(email) || email === "" || email === null || email === undefined;
        } else {
            return re.test(email);
        }
    };

    this.isNumber = function (val, strict) {
        if (strict === false) {
            return $.isNumeric(val) || val === "" || val === null || val === undefined;
        } else {
            return $.isNumeric(val);
        }
    };

    this.viewIn = function (jDom) {
        jDom.removeClass("out-of-view");
    };

    this.viewOut = function (jDom) {
        jDom.addClass("out-of-view");
    };

    this.isIE7 = function () {
        try {
            return navigator.appVersion.indexOf("MSIE 7.") != -1;
        } catch (ex) {
            return false;
        }
    }

    /**
     * 成绩算数运算
     * @param scoreValue
     * @returns {number}
     */
    this.scoreMathService = function(scoreValue)
    {
        var resultValue = 0;
        //有考试记录，成绩不为0，则计算考试成绩区间值
        if(scoreValue < 1)
        {
            //0-1分之间向上取整数
            resultValue = Math.ceil(scoreValue);
        }
        else if(scoreValue > 19 && scoreValue < 20)
        {
            //19-20分之间向下取整数
            resultValue = Math.floor(scoreValue);
        }
        else
        {
            //其他分数段的小数四舍五入
            resultValue = Math.round(scoreValue);
        }

        return resultValue;
    };

    this.removeKeyOfNullValue = function (params) {
        for (var key in params) {
            if (params[key] === null) {
                delete params[key];
            }
        }
    }


    this.startClient = function (frameDoc, url) {
        if ("IE" == this.getBrowType()) {
            //ie 插件检测客户端有没有注册协议，如果注册了，则说明已经安装软件，直接用自定义协议启动客户端
            if (this.checkInstallClient()) {
                location.href = url;
            }
            else {
                //判断IE的位数，64位IE浏览器不支持，提示用户更换32位IE浏览器
                if (this.checkIEIs64Version()) {
                    this.showTip("64位IE浏览器不支持启动考试机客户端，请更换32位IE或者其他浏览器");
                    return;
                }
            }

            //如果插件没有检测到客户端安装特定协议，则显示提示框提示用户下载
        }
        else {
            //自定义协议启动客户端程序，参数说明toLogin：考试记录ID，url：服务器地址
            //非IE内核通过iframe启动自定义协议, 非IE浏览器和插件无关，只关注考试机程序是否在注册表正确写入cetsim协议
            $(frameDoc).attr("src", url);
        }
    };

    this.isHaveSpace = function (val) {
        return /\s/.test(val);
    };


    this.formValidate = function (validation) {
        var filter,
            filterText,
            filterTextArr,
            filterTextArrSlice,
            filterRes;
        for (var i = 0; i < validation.length; i++) {
            for (var j = 0; j < validation[i].filters.length; j++) {
                filterText = validation[i].filters[j];
                filterTextArr = filterText.split(":");

                filter = $filter(filterTextArr[0]);
                filterTextArrSlice = filterTextArr.slice(1);
                filterTextArrSlice.unshift(validation[i].name);
                filterTextArrSlice.unshift(validation[i].obj);
                filterRes = filter.apply(null, filterTextArrSlice);
                if (!filterRes.bool) {
                    _self.showTip(filterRes.msg);
                    return false;
                }
            }
        }
        return true;
    };

    this.isHaveSpecialCharacters = function (input) {
        var reg = /[!@#\$%\^&\*\(\)\_\+\.\,~"]+/g;
        return reg.test(input);
    };

    this.getPasswordStength = function (password) {
        if (/^\d+$/.test(password) || /^[a-z]+$/.test(password) || /^[A-Z]+$/.test(password) || password.length < 6) {
            return 1;
        } else if (/\d/.test(password) && /[a-z]/.test(password) && /[A-Z]/.test(password)) {
            return 3;
        } else {
            return 2;
        }
    };


    this.getSystemSettingsInfo = function (fn) {
        $http({
            method: "GET",
            url: _self.getUrlWithShadowParam(_self.config.host + "/api/common/queryAllSysConfigInfoList.action"),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), null);
        });
    };
    
    this.updateSystemSettingsInfo = function (key, val, fn) {
        $http({
            method: "POST",
            url: _self.getUrlWithShadowParam(_self.config.host + "/api/common/updateSysConfigByKey.action"),
            data : $.param({
                configKey : key,
                configValue : val
            }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        }).then(function (response) {
            fn(null, response.data);
        }, function (response) {
            fn(new Error(response.statusText), null);
        });
    };


    /**
     * 根据当前时间和结束时间，返回倒计时天、小时、分钟和秒
     * @param endDate
     * @param currentDate
     */
    this.leftTimer = function(endDate, currentDate)
    {
        //计算剩余的毫秒数
        var leftTime;
        if (moment(endDate).isAfter(currentDate)) {
            leftTime = endDate - currentDate;
        } else {
            leftTime = currentDate - endDate;
        }

        //计算剩余的天数
        var days = parseInt(leftTime / 1000 / 60 / 60 / 24 , 10);

        //计算剩余的小时
        var hours = parseInt(leftTime / 1000 / 60 / 60 % 24 , 10);

        //计算剩余的分钟
        var minutes = parseInt(leftTime / 1000 / 60 % 60, 10);

        //计算剩余的秒数
        var seconds = parseInt(leftTime / 1000 % 60, 10);

        var days = this.checkTime(days);
        var hours = this.checkTime(hours);
        var minutes = this.checkTime(minutes);
        var seconds = this.checkTime(seconds);

        return "" + days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
    };

    /**
     * 将0-9的数字前面加上0，例1变为01
     * @param i
     * @returns {*}
     */
    this.checkTime = function (i)
    {
        if(i<10)
        {
            i = "0" + i;
        }
        return i;
    };

    this.uuid = function () {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    };

    this.countDown = function (_from, _to, fn) {
        var from = _from,
            to = _to;
        var id = "countDown-" + _self.uuid(),
            div = $("<div>");
        var timer,
            mFrom = moment(from),
            mTo = moment(to);

        div.attr('id', id);
        div.css({
            padding: "20px 40px"
        });
        div.html(fn(_self.leftTimer(from, to), mFrom, mTo));


        _self.layerOpen({
            area: "400px",
            title: "提示",
            content: div.prop("outerHTML"),
            end: function () {
                clearInterval(timer)
            }
        });

        timer = setInterval(function () {
            mFrom.add(1, "s");

            var str = fn(_self.leftTimer(new Date(mFrom.toString()), to), mFrom, mTo);
            $("#" + id).html(str);

            if (mFrom.isAfter(mTo)) {
                clearInterval(timer);
            }

            $log.log(str);
        }, 1000)
    };

    this.isIE7 = function () {
        return $("#cetsim-browser-detect-ie7").length > 0;
    };
    this.isIE8 = function () {
        return $("#cetsim-browser-detect-ie8").length > 0;
    };
    this.isIE9 = function () {
        return $("#cetsim-browser-detect-ie9").length > 0;
    };
    this.isLessIE9 = function () {
        return $("#cetsim-browser-detect-less-than-ie9").length > 0;
    };

}]);
