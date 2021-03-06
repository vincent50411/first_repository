var globalConfig = {
    host : "http://10.5.115.16:8180",
    name : "大学英语四六级口语智能考试系统",
    resetDefaultPassword : "cetset",
    school : "",
    /**
     * 学院
     * 过时的(准备删除)
     * @deprecated
     */
    institutes : [
        {
            id : "1",
            name : "全部"
        },{
            id : "2",
            name : "土木学院"
        },{
            id : "3",
            name : "计算机学院"
        }
    ],
    /**
     * 学院列表
     */
    institutesList : ["计算机学院", "机械工程学院","土木与水利工程学院","外国语学院","资源与环境工程学院","微电子学院","工培中心","技师学院","电气与自动化工程学院","化学与化工学院","管理学院","食品科学与工程学院","汽车与交通工程学院","体育部","材料科学与工程学院","马克思主义学院","仪器科学与光电工程学院","数学学院","软件学院","继续教育学院","计算机与信息学院","经济学院","建筑与艺术学院","电子科学与应用物理学院","生物与医学工程学院"],
    /**
     * 专业列表
     * @deprecated
     */
    majorList : [
        "计算机",
        "软件工程",
        "土木工程",
        "医学"
    ],

    /**
     * 专业对应学院
     */
    majorListMap : {"计算机学院" : ["软件工程"], "机械工程学院":["机械工程学院marjor1","机械工程学院marjor2","机械工程学院marjor3","机械工程学院marjor4"],"土木与水利工程学院":["土木与水利工程学院marjor1","土木与水利工程学院marjor2","土木与水利工程学院marjor3","土木与水利工程学院marjor4"],"外国语学院":["外国语学院marjor1","外国语学院marjor2","外国语学院marjor3","外国语学院marjor4"],"资源与环境工程学院":["资源与环境工程学院marjor1","资源与环境工程学院marjor2","资源与环境工程学院marjor3","资源与环境工程学院marjor4"],"微电子学院":["微电子学院marjor1","微电子学院marjor2","微电子学院marjor3","微电子学院marjor4"],"工培中心":["工培中心marjor1","工培中心marjor2","工培中心marjor3","工培中心marjor4"],"技师学院":["技师学院marjor1","技师学院marjor2","技师学院marjor3","技师学院marjor4"],"电气与自动化工程学院":["电气与自动化工程学院marjor1","电气与自动化工程学院marjor2","电气与自动化工程学院marjor3","电气与自动化工程学院marjor4"],"化学与化工学院":["化学与化工学院marjor1","化学与化工学院marjor2","化学与化工学院marjor3","化学与化工学院marjor4"],"管理学院":["管理学院marjor1","管理学院marjor2","管理学院marjor3","管理学院marjor4"],"食品科学与工程学院":["食品科学与工程学院marjor1","食品科学与工程学院marjor2","食品科学与工程学院marjor3","食品科学与工程学院marjor4"],"汽车与交通工程学院":["汽车与交通工程学院marjor1","汽车与交通工程学院marjor2","汽车与交通工程学院marjor3","汽车与交通工程学院marjor4"],"体育部":["体育部marjor1","体育部marjor2","体育部marjor3","体育部marjor4"],"材料科学与工程学院":["材料科学与工程学院marjor1","材料科学与工程学院marjor2","材料科学与工程学院marjor3","材料科学与工程学院marjor4"],"马克思主义学院":["马克思主义学院marjor1","马克思主义学院marjor2","马克思主义学院marjor3","马克思主义学院marjor4"],"仪器科学与光电工程学院":["仪器科学与光电工程学院marjor1","仪器科学与光电工程学院marjor2","仪器科学与光电工程学院marjor3","仪器科学与光电工程学院marjor4"],"数学学院":["数学学院marjor1","数学学院marjor2","数学学院marjor3","数学学院marjor4"],"软件学院":["软件学院marjor1","软件学院marjor2","软件学院marjor3","软件学院marjor4"],"继续教育学院":["继续教育学院marjor1","继续教育学院marjor2","继续教育学院marjor3","继续教育学院marjor4"],"计算机与信息学院":["计算机与信息学院marjor1","计算机与信息学院marjor2","计算机与信息学院marjor3","计算机与信息学院marjor4"],"经济学院":["经济学院marjor1","经济学院marjor2","经济学院marjor3","经济学院marjor4"],"建筑与艺术学院":["建筑与艺术学院marjor1","建筑与艺术学院marjor2","建筑与艺术学院marjor3","建筑与艺术学院marjor4"],"电子科学与应用物理学院":["电子科学与应用物理学院marjor1","电子科学与应用物理学院marjor2","电子科学与应用物理学院marjor3","电子科学与应用物理学院marjor4"],"生物与医学工程学院":["生物与医学工程学院marjor1","生物与医学工程学院marjor2","生物与医学工程学院marjor3","生物与医学工程学院marjor4"]},

    // 所有年级列表
    gradesList : [
        "2020",
        "2019",
        "2018",
        "2017",
        "2016",
        "2015",
        "2014",
        "2013",
        "2012",
        "2011",
        "2010",
        "2009",
        "2008"
    ],
    paperType : {
        "cet4" : "CET4",
        "cet6" : "CET6"
    },
    /**
     * 试卷状态
     */
    paperStatus : {
        "disabled" : {
            code : 0,
            name : "禁用"
        },
        "enabled" : {
            code : 1,
            name : "启用"
        }
    },
    /**
     * 试卷作业权限
     */
    paperPlanUsage : {
        allow : 1,
        disAllow : 0
    },
    /**
     * 试卷练习权限
     */
    paperFreeUsage : {
        allow : 1,
        disAllow : 0
    },
    /**
     * 考试状态
     */
    examState : {
        NO_PARTNER : {
            code : 0,
            name : "未分组"
        },
        WAIT_LOGIN : {
            code : 1,
            name : "考试准备"
        },
        IN_EXAM : {
            code : 2,
            name : "考试中"
        },
        FINISH : {
            code : 3,
            name : "考试完成"
        },
        FAILURE : {
            code : 4,
            name : "考试失败"
        }
    },
    /**
     * 考试流程状态
     */
    flowState : {
        unLogin : {
            code : 0,
            name : "未登录"
        },
        hasLogin : {
            code : 10,
            name : "已登录"
        },
        grouping : {
            code : 20,
            name : "组网中"
        },
        groupSuccess : {
            code : 21,
            name : "组网成功"
        },
        groupFail : {
            code : 22,
            name : "组网失败"
        },
        testing : {
            code : 30,
            name : "试音中"
        },
        testSuccess : {
            code : 31,
            name : "试音成功"
        },
        testFail : {
            code : 32,
            name : "试音失败"
        },
        examing : {
            code : 40,
            name : "答题中"
        },
        examSuccess : {
            code :41,
            name : "答题成功"
        },
        examFail : {
            code : 42,
            name : "答题失败"
        }
    },
    /**
     * 首页登陆页滚动图片
     */
    "homeRollImgs" : [
        {
            url : "img/signin/1.jpg",
            title : ""
        },{
            url : "img/signin/2.jpg",
            title : ""
        },{
            url : "img/signin/3.jpg",
            title : ""
        }
    ]
};