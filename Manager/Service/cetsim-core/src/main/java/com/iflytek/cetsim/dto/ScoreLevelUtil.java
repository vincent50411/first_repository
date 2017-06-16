package com.iflytek.cetsim.dto;

import com.alibaba.fastjson.JSON;
import com.iflytek.cetsim.common.enums.PaperTypeEnum;
import com.iflytek.cetsim.common.utils.PropertiesLoader;

import java.util.ArrayList;
import java.util.List;

public class ScoreLevelUtil {

    public static List<ScoreLevel> getLevelsForTeacher()
    {
        try {
            String prop = new PropertiesLoader("cetsim.properties").getProperty("teacher.cet.scorelevel");

            List<ScoreLevel> levels = JSON.parseArray(prop, ScoreLevel.class);
            return levels;
        }catch(Exception ex)
        {
            return new ArrayList<ScoreLevel>();
        }
    }

    public static List<ScoreLevel> getLevels(String paperTypeCode)
    {
        try {
            if(paperTypeCode == PaperTypeEnum.CET4.getTypeCode()) {
                String prop = new PropertiesLoader("cetsim.properties").getProperty("cet4.scorelevel");

                List<ScoreLevel> levels = JSON.parseArray(prop, ScoreLevel.class);
                return levels;
            }
            else
            {
                String prop = new PropertiesLoader("cetsim.properties").getProperty("cet6.scorelevel");
                List<ScoreLevel> levels = JSON.parseArray(prop, ScoreLevel.class);
                return levels;
            }
        }catch(Exception ex)
        {
            return new ArrayList<ScoreLevel>();
        }
    }

    public static String getLevel(String paperTypeCode, double score)
    {
        String prop = "";
        if(paperTypeCode == PaperTypeEnum.CET4.getTypeCode()) {
            prop = new PropertiesLoader("cetsim.properties").getProperty("cet4.scorelevel");
        }
        else
        {
            prop = new PropertiesLoader("cetsim.properties").getProperty("cet6.scorelevel");
        }
        List<ScoreLevel> levels = JSON.parseArray(prop, ScoreLevel.class);
        for (ScoreLevel level : levels)
        {
            if(level.getMin() <= score && level.getMax() > score)
            {
                return  level.getName();
            }
        }
        return "";
    }
}

