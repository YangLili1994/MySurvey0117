package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class Option {

    //选项类型
    public static enum OptionType{TEXT,IMAGE}

    private int quesId;
    private int optionId;
    private String optionContent;
    private OptionType optionType;

    public Option(int quesId, int optionId, OptionType optionType,String optionContent) {
        this.quesId = quesId;
        this.optionId = optionId;
        this.optionContent = optionContent;
        this.optionType = optionType;
    }

    public int getQuesId() {
        return quesId;
    }

    public void setQuesId(int quesId) {
        this.quesId = quesId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getOptionId() {
        return optionId;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
    }
}
