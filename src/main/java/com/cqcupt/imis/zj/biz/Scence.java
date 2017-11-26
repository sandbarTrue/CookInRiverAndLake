package com.cqcupt.imis.zj.biz;

import lombok.Data;

import java.util.List;

/**
 * Created by zhoujun on 2017/11/20.
 */
@Data
public class Scence {
    private String name;
    private Integer meet;
    private Integer rice;
    private Integer fish;
    private Integer vege;
    private Integer level;
    private Integer choosed;
    private List<String> candidateCookList;
    private List<String> cookList;
}
