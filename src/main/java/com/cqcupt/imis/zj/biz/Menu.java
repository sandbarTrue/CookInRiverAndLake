package com.cqcupt.imis.zj.biz;

import lombok.Data;

/**
 * Created by zhoujun on 2017/11/20.
 */
@Data
public class Menu {
    private String name;
    private Integer fry;
    private Integer toast;
    private Integer boil;
    private Integer steamed;
    private Integer zha;
    private Integer slice;
    private String  level;
    private Integer amount;
}
