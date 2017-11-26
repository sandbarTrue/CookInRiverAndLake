package com.cqcupt.imis.zj.biz;

/**
 * Created by zhoujun on 2017/11/20.
 */
public enum ScenceEnum {
    PASTURE(1,"牧场"),WORKSHOP(2,"作坊"),VEGETABLESHED(3,"菜棚"),VEGETABLEFIELD(4,"菜地"),
    HENHOUSE(5,"鸡舍"),PIGSTY(6,"猪圈");
        private String desc;
        private Integer code;
    ScenceEnum(Integer code,String desc){
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
        public ScenceEnum getByDesc(String desc){
            ScenceEnum[] scenceEna= ScenceEnum.values();
            for(ScenceEnum scenceEnum:scenceEna){
                if(scenceEnum.getDesc().equals(desc)){
                    return  scenceEnum;
                }
            }
            return null;
        }
    }

