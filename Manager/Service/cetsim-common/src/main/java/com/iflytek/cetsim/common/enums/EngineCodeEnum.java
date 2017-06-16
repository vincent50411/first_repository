package com.iflytek.cetsim.common.enums;

import java.util.ArrayList;
import java.util.List;

public enum EngineCodeEnum {

    Normal(0, "正常", ""),
    NonSpeech(0X7011, "纯零语音", "请正常开启麦克风后再开始答题喔~"),
    NonHumanVoice(0X7001, "无语音", "更大声地回答问题，会获得更高的分数哦~"),
    CutRate(0x7012, "截幅", "你回答时的声音太大啦！减小音量效果会更好喔~\n"),
    SnrLow(0x7008, "信噪比低", "更大声地回答问题，会获得更高的分数哦~"),
    NoScene(0x7004, "乱说", "请认真审题，回答问题相关的内容，才会获得更高的分数~\n");

    EngineCodeEnum(int code, String name, String message) {
        this.code = code;
        this.name = name;
        this.message = message;
    }

    private int code;

    private String name;

    private String message;


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public static boolean IsKownCode(int code)
    {
        List<Integer> codes = new ArrayList<>();
        codes.add(Normal.getCode());
        codes.add(NonSpeech.getCode());
        codes.add(NonHumanVoice.getCode());
        codes.add(CutRate.getCode());
        codes.add(SnrLow.getCode());
        codes.add(NoScene.getCode());
        for (int i = 0; i < codes.size(); i++) {
            if(codes.get(i) == code)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据引擎码返回提示信息
     * @param engineCode
     * @return
     */
    public static String getTipMessage(int engineCode)
    {
        if(engineCode == Normal.getCode())
        {
            return Normal.getMessage();
        }
        else if(engineCode == NonSpeech.getCode())
        {
            return NonSpeech.getMessage();
        }
        else if(engineCode == NonHumanVoice.getCode())
        {
            return NonHumanVoice.getMessage();
        }
        else if(engineCode == CutRate.getCode())
        {
            return CutRate.getMessage();
        }
        else if(engineCode == SnrLow.getCode())
        {
            return SnrLow.getMessage();
        }
        else if(engineCode == NoScene.getCode())
        {
            return NoScene.getMessage();
        }
        else {
            return Normal.getMessage();
        }
    }
}
