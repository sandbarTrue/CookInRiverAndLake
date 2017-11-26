package com.cqcupt.imis.zj.utils;

/**
 * Created by zhoujun on 2017/11/20.
 */
public enum AblityEnum {
    MEET(1,"肉"),VEGETABLE(2,"菜"),FISH(3,"鱼"),FLOUR(4,"面");
    private String desc;
    private Integer code;
     AblityEnum(Integer code,String desc){
        this.desc=desc;
        this.code=code;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public AblityEnum getByDesc(String desc){
        AblityEnum[] ablityEna=AblityEnum.values();
        for(AblityEnum ablityEnum:ablityEna){
            if(ablityEnum.getDesc().equals(desc)){
                return  ablityEnum;
            }
        }
        return null;
    }
}
