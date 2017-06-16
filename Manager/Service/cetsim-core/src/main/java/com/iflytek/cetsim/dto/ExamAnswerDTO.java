package com.iflytek.cetsim.dto;

/**
 * 考试答题结果，主要包含答题录音和队友录音信息
 * Created by Administrator on 2017/4/1.
 */
public class ExamAnswerDTO
{
    //自己帐号
    private String account = null;

    //队友帐号
    private String partnerAccount = null;

    //自我介绍录音文件地址
    private String mePath = null;

    //队友自我介绍录音文件地址
    private String partnerMePath = null;

    //第一题自录音文件地址
    private String readPath = null;

    //第一题朗读题引擎返回码（评测引擎）
    private int readEngineCode;

    private String readTipMesage;

    //第二题第一个问题
    private String answerOnePath = null;

    //第二题第一个问题引擎返回码（评测引擎）
    private int answerOneEngineCode;

    private String answerOneTipMesage;

    private String partnerAnswerOnePath = null;

    //第二题第二个问题
    private String answerTwoPath = null;

    //第二题第二个问题引擎返回码（评测引擎）
    private int answerTwoEngineCode;

    private String answerTwoTipMesage;

    private String partnerAnswerTwoPath = null;

    //第三题
    private String presPath = null;

    //第三题引擎返回码（评测引擎）
    private int presEngineCode;

    private String presTipMesage;

    private String partnerPresPath = null;

    //第四题
    private String pairPath = null;

    //第四题引擎返回码（评测引擎）
    private int pairEngineCode;

    private String pairTipMesage;

    //第四题 语音合成后的录音文件地址
    private String togetherPairPath = null;


    public String getReadTipMesage() {
        return readTipMesage;
    }

    public void setReadTipMesage(String readTipMesage) {
        this.readTipMesage = readTipMesage;
    }

    public String getAnswerOneTipMesage() {
        return answerOneTipMesage;
    }

    public void setAnswerOneTipMesage(String answerOneTipMesage) {
        this.answerOneTipMesage = answerOneTipMesage;
    }

    public String getAnswerTwoTipMesage() {
        return answerTwoTipMesage;
    }

    public void setAnswerTwoTipMesage(String answerTwoTipMesage) {
        this.answerTwoTipMesage = answerTwoTipMesage;
    }

    public String getPresTipMesage() {
        return presTipMesage;
    }

    public void setPresTipMesage(String presTipMesage) {
        this.presTipMesage = presTipMesage;
    }

    public String getPairTipMesage() {
        return pairTipMesage;
    }

    public void setPairTipMesage(String pairTipMesage) {
        this.pairTipMesage = pairTipMesage;
    }

    public int getReadEngineCode() {
        return readEngineCode;
    }

    public void setReadEngineCode(int readEngineCode) {
        this.readEngineCode = readEngineCode;
    }

    public int getAnswerOneEngineCode() {
        return answerOneEngineCode;
    }

    public void setAnswerOneEngineCode(int answerOneEngineCode) {
        this.answerOneEngineCode = answerOneEngineCode;
    }

    public int getAnswerTwoEngineCode() {
        return answerTwoEngineCode;
    }

    public void setAnswerTwoEngineCode(int answerTwoEngineCode) {
        this.answerTwoEngineCode = answerTwoEngineCode;
    }

    public int getPresEngineCode() {
        return presEngineCode;
    }

    public void setPresEngineCode(int presEngineCode) {
        this.presEngineCode = presEngineCode;
    }

    public int getPairEngineCode() {
        return pairEngineCode;
    }

    public void setPairEngineCode(int pairEngineCode) {
        this.pairEngineCode = pairEngineCode;
    }

    public String getAccount() {
        return account;
    }

    public String getPartnerAccount() {
        return partnerAccount;
    }

    public String getMePath() {
        return mePath;
    }

    public String getPartnerMePath() {
        return partnerMePath;
    }

    public String getReadPath() {
        return readPath;
    }

    public String getAnswerOnePath() {
        return answerOnePath;
    }

    public String getAnswerTwoPath() {
        return answerTwoPath;
    }

    public String getPresPath() {
        return presPath;
    }

    public String getPairPath() {
        return pairPath;
    }

    public String getTogetherPairPath() {
        return togetherPairPath;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPartnerAccount(String partnerAccount) {
        this.partnerAccount = partnerAccount;
    }

    public void setMePath(String mePath) {
        this.mePath = mePath;
    }

    public void setPartnerMePath(String partnerMePath) {
        this.partnerMePath = partnerMePath;
    }

    public void setReadPath(String readPath) {
        this.readPath = readPath;
    }

    public void setAnswerOnePath(String answerOnePath) {
        this.answerOnePath = answerOnePath;
    }

    public void setAnswerTwoPath(String answerTwoPath) {
        this.answerTwoPath = answerTwoPath;
    }

    public void setPresPath(String presPath) {
        this.presPath = presPath;
    }

    public void setPairPath(String pairPath) {
        this.pairPath = pairPath;
    }

    public void setTogetherPairPath(String togetherPairPath) {
        this.togetherPairPath = togetherPairPath;
    }

    public String getPartnerAnswerOnePath() {
        return partnerAnswerOnePath;
    }

    public void setPartnerAnswerOnePath(String partnerAnswerOnePath) {
        this.partnerAnswerOnePath = partnerAnswerOnePath;
    }

    public String getPartnerAnswerTwoPath() {
        return partnerAnswerTwoPath;
    }

    public void setPartnerAnswerTwoPath(String partnerAnswerTwoPath) {
        this.partnerAnswerTwoPath = partnerAnswerTwoPath;
    }

    public String getPartnerPresPath() {
        return partnerPresPath;
    }

    public void setPartnerPresPath(String partnerPresPath) {
        this.partnerPresPath = partnerPresPath;
    }
}
