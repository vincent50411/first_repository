/**
 * []|range:5 => [1,2,3,4,5]
 * for ng-repeat loop
 */
cetsim.filter('range', function() {
    return function(input, total) {
        total = parseInt(total);

        for (var i = 1; i <= total; i++) {
            input.push(i);
        }

        return input;
    };
});


/**
 * midRange 请搭配 goPage 指令共同使用, 因为当 页码为 "..."时, goPage指令可以正常处理
 */
cetsim.filter('midRange', function() {
    return function(input, total, curIndex) {
        var list = [];
        total = parseInt(total);
        curIndex = parseInt(curIndex);
        var num = 11; // 请给一个奇数 例如 : [5个, 中间1个, 5个]
        var i = 1,
            max = total;
        i = (curIndex - (num - 1) / 2) > 1 ? (curIndex - (num - 1) / 2) : i;
        max = (i + num - 1) > total ? total : (i + num - 1);

        if (i > 1) {
            list.push("..");
        }
        for (; i <= max; i++) {
            list.push(i);
        }
        if (max < total) {
            list.push("...");
        }
        return list;
    };
});


cetsim.filter("cetTypeName", function (commonService) {
    return function (input) {
        try {
            var code = parseInt(input);
            if (!isNaN(code)) {
                var map = commonService.config.paperType;
                if (code === map.cet4) {
                    return "cet4";
                } else if (code === map.cet6) {
                    return "cet6";
                } else {
                    return input;
                }
            }
        } catch(ex) {
            return input;
        }
    }
});


/**
 * 列表显示时,如果字段木有值则显示"--"
 */
cetsim.filter("EmptyText", function () {
    return function (input, defaultEmptyText) {
        var text = defaultEmptyText === undefined ? "-" : defaultEmptyText;
        if (input === null || input === undefined || input === "") {
            return text;
        } else {
            return input;
        }
    }
});

/**
 * 转换用户头像的图片地址 文件名=>绝对地址
 */
cetsim.filter("userPhotoPathResolve", function (userService, $log, $timeout) {
    return function (input) {
        try {
            return userService.getUserPhotoAbsolutePathByFileName(input);
        } catch (ex) {
            $log.warn("userPhoto filter error");
            return input;
        }
    }
});

/**
 * 任务报告界面默认值设置
 */
cetsim.filter("defaultTaskLabelSet", function(){
    return function(input, type){
        if(input == null || input == "")
        {
            if(type == "PLAN_NAME") {
                //计划名称
                return "没有计划名称";
            }
            else if(type == "TASK_NAME")
            {
                //任务名称
                return "没有任务名称";
            }
            else if(type == "EXAM_TIME")
            {
                //考试时间, 不显示
                return " ";
            }
            else if(type == "SUBMIT")
            {
                //提交人数
                return "-";
            }
            else if(type == "TOTAL")
            {
                //总人数
                return "-";
            }
            else if(type == "PM_VALUE")
            {
                //总人数
                return "-";
            }
            else if(type == "RANK")
            {
                //排名百分比
                return "-";
            }
        }

        return input;
    };
});

/**
 * 过滤cet类型
 */
cetsim.filter("paperTypeFormater", function()
{
    return function(input)
    {
        if(input == "0")
        {
            return "CET4";
        }
        else if(input == "1")
        {
            return "CET6";
        }
        else
        {
            return "-";
        }
    };
});

cetsim.filter("paperItemTypeCodeNameFormatter", ["commonService", function (commonService) {
    return function (input) {
        var map = commonService.paperItemTypeV2;

        try {
            return map[input].name || map[input].zhName;
        } catch  (ex) {
            return input;
        }
    }
}]);

cetsim.filter("scoreValueFormater", ["commonService", "$log", function($commonService, $log){
    return function(input, examCount){
        var scopeValue = 0;

        //如果一次考试都没有参加，由于数据库默认值为0，考生没有成绩-
        if(examCount == 0 || input === null || input === undefined)
        {
            scopeValue = "-";
        }
        else
        {
            //如果有参加考试记录，但是成绩为0，则直接显示D
            if(input == "0")
            {
                scopeValue = 0;
            }
            else
            {
                scopeValue = $commonService.scoreMathService(input);
            }
        }

        // $log.log("scoreValueFormater:\t" + input + "&&\t" + examCount + "\t=>\t" + scopeValue);
        return scopeValue;
    };
}]);

cetsim.filter("scoreValue0To5Formatter", function () {
    return function(input, useCount){
        try {
            // !input => 0 undefined null
            if (!input && $.isNumeric(useCount) && useCount == 0) {
                return "-";
            }
            var score = input;
            if (score == 0) {
                score = 0
            } else if (score > 0 && score < 1) {
                score = 1;
            } else if (score >= 1 && score < 4) {
                score = Math.round(score);
            } else {
                score = Math.floor(score);
            }
            if (isNaN(score)) {
                throw new Error("");
            } else {
                return score;
            }
        } catch (ex) {
            return input;
        }
    };
});


/**
 * 过滤成绩区间
 */
cetsim.filter("scorelevelFormater", ["commonService", function(commonService)
{
    return function(input, paperTypeValue, examCount)
    {
        var scopeLeverl = "";

        //如果一次考试都没有参加，由于数据库默认值为0，考生没有成绩-
        if(examCount == 0)
        {
            return "-";
        }
        else
        {
            //如果有参加考试记录，但是成绩为0，则直接显示D
            if(input == "0")
            {
                return "D";
            }
            else
            {
                //有考试记录，成绩不为0，则计算考试成绩区间值
                commonService.getGlobRankOfScore(input, paperTypeValue, function(value){
                    scopeLeverl = value;
                });
            }
        }

        return scopeLeverl;
    };
}]);

/**
 * 根据gender code 获取性别名称  男 或者 女这样的字符串
 */
cetsim.filter("genderResolver", function () {
    return function (input) {
        try {
            input = parseInt(input);
            if (input === 0) {
                return "男";
            } else if (input === 1){
                return "女";
            } else {
                return "-";
            }
        } catch(ex) {
            return "-";
        }
    }
});

/**
 * 学生任务模块中表格排名单元格展示label过滤器
 */
cetsim.filter("showPMLabel", function()
{
    return function(input, total, examCount, plan_name)
    {
        if(examCount == "0" || examCount == 0)
        {
            //如果没有参加考试，则排名不显示
            return "-";
        }


        return input + "/" + total;
    };
});

/**
 * 答卷包值状态 服务端会返回答卷包的名称, 但是前端在有名称的情况下 请显示 "已上传", 否则返回未上传
 */
cetsim.filter("paperPackageStatus", function () {
    return function (input) {
        var result = "";
        try {
            if (input.length > 0) {
                result = "已上传";
            }
        } catch(ex) {}
        if (result === "") {
            result = "未上传";
        }
        return result;
    }
});


cetsim.filter("rankColorClassFilter", function (commonService) {
    return function (input) {
        return commonService.getColorClassByRank(input);
    }
});

var __paperTypeOptions_cache;
cetsim.filter("paperTypeOptions", function (commonService) {
    return function (input) {
        if (!__paperTypeOptions_cache) {
            __paperTypeOptions_cache = [{
                key : "全部",
                val : null
            }].concat(commonService.getPaperTypeOptions());
        }
        return __paperTypeOptions_cache;
    }
});

var __paperItemTypeOptions_cache;
cetsim.filter("paperItemTypeOptions", function (commonService) {
    return function (input) {
        if (!__paperItemTypeOptions_cache) {
            __paperItemTypeOptions_cache = [{
                name : "全部题型",
                val : null
            }].concat(commonService.getPaperItemTypeV2Options());
        }
        return __paperItemTypeOptions_cache;
    }
});



var __useStateOptions_cache;
cetsim.filter("useStateOptions", function () {
    return function (input) {
        if (!__useStateOptions_cache) {
            __useStateOptions_cache = [{
                key: "全部",
                val: null
            }, {
                key: "未练习",
                val: 0
            }, {
                key: "已练习",
                val: 1
            }];
        }
        return __useStateOptions_cache;
    }
});



cetsim.filter("trainTaskState", function(){
    return function(useCount){
        if(useCount > 0)
        {
            return "已练习";
        }
        else {
            return "未练习";
        }
    };
});

cetsim.filter("paperItemTypeFormatter", ["commonService", function(commonService){
    return function(paperItemTypeCode){
        return commonService.paperItemTypeV2[paperItemTypeCode].name;
    };

}]);

/**
 * 表单验证规则:必填项,不能为空
 */
cetsim.filter("validationNotEmpty", function () {
    return function (input, fieldName) {
        var result = {bool : true};
        if ($.trim(input) === "") {
            result = {
                bool: false,
                msg: fieldName + "不能为空"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:手机格式是否正确(非严格:空字符串即没有输入的情况认为输入符合手机电话格式)
 */
cetsim.filter("validationTelephoneNotStrict", function (commonService) {
    return function (input) {
        var result = {bool : true};
        if (!commonService.isPhoneNumber(input, false)) {
            result = {
                bool: false,
                msg: "请输入正确格式的联系方式（11位数字）"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:email格式是否正确(非严格:空字符串即没有输入的情况认为输入符合email格式)
 */
cetsim.filter("validationEmailNotStrict", function (commonService) {
    return function (input) {
        var result = {bool : true};
        if (!commonService.isEmail(input, false)) {
            result = {
                bool: false,
                msg: "邮箱格式不正确"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:是否是数字(非严格:空字符串即没有输入的情况认为输入是数字)
 */
cetsim.filter("validationNumberNotStrict", function (commonService) {
    return function (input, fieldName) {
        var result = {bool : true};
        if (!commonService.isNumber(input, false)) {
            result = {
                bool: false,
                msg: fieldName + "必须是数字格式"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:是否是正确密码格式
 */
cetsim.filter("validationPassword", function (commonService) {
    return function (input) {
        var result = {bool : true};
        if (!commonService.isPassword(input)) {
            result = {
                bool: false,
                msg: "密码格式不正确(至少六位字符)"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:不能包含空格
 */
cetsim.filter("validationNoSpace", function () {
    return function (input, fieldName) {
        var result = {bool : true};
        if (/\s/.test(input)) {
            result = {
                bool: false,
                msg: fieldName + "不能包含空格"
            };
        }
        return result;
    }
});
/**
 * 表单验证规则:长度是否超出
 */
cetsim.filter("validationLengthNotMoreThan", function () {
    return function (input, fieldName, length) {
        var result = {
                bool : true
            };

        if (input === null || input === undefined) {
            return result;
        }
        try {

            var length = parseInt(length);
            if (input.length > length) {
                result = {
                    bool : false,
                    msg : fieldName + "不能超出" + length + "个字符"
                }
            }
        } catch (ex) {
            result = {
                bool : false,
                msg : ex.message
            }
        }
        return result;
    }
});

/**
 * 表单验证规则:是否包含某些英文特殊字符 !@#$%^&*()_+.,~"
 */
cetsim.filter("validationNoSpecialChars", function () {
    return function (input, fieldName) {
        var result = {
            bool: true
        };
        try {
            var reg = /[!@#\$%\^&\*\(\)\_\+\.\,~"]+/g;
            if (reg.test(input)) {
                result = {
                    bool: false,
                    msg: fieldName + "不能包含特殊字符"
                }
            }
        } catch (ex) {
        }
        return result;
    }
});
/**
 * 表单验证规则:只能由字母数字组成
 */
cetsim.filter("validationOnlyCharacterAndNumberNotStrict", function () {
    return function (input, fieldName) {
        var result = {
            bool: true
        };
        try {
            var reg = /[^a-zA-Z0-9]/;
            if (reg.test(input)) {
                result = {
                    bool: false,
                    msg: fieldName + "只能由大小写字母和数字组成"
                }
            }
        } catch (ex) {
        }
        return result;
    }
});
/**
 * 表单验证规则:是否符合账号格式
 */
cetsim.filter("validationCetsimAccount", function () {
    return function (input, fieldName) {
        var result = {
            bool: true
        };
        try {
            var reg = /[^a-zA-Z0-9()_\-]/;
            if (reg.test(input)) {
                result = {
                    bool: false,
                    msg: "账号只能由大小写字母、数字或者-()_组成"
                }
            }
        } catch (ex) {
        }
        return result;
    }
});

/**
 * 超出指定长度, 裁剪字符串并加上省略号(不会添加title属性)
 */
cetsim.filter("maxCharOverFlowHidden", function () {
    return function (input, length) {
        try {
            var result = input;
            if (input.length > length) {
                result = input.substr(0, length) + "...";
            }
            return result;
        } catch (ex) {
            return input;
        }
    }
});