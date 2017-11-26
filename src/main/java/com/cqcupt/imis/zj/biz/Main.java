package com.cqcupt.imis.zj.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqcupt.imis.zj.utils.Excel2JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoujun on 2017/11/26.
 */
public class Main {
     public static void main(String args ) {
         File dir = new File("J:\\爆炒江湖\\爆炒江湖.xlsx");
         ArrayList<Scence> scences=new ArrayList<>();
         ArrayList<Cook> cooks=new ArrayList<>();
         ArrayList<Menu> menus=new ArrayList<>();
         //初始化数据
         init(dir,scences,cooks,menus);
         //分配任务
         String result= allocatingTask(scences,cooks,menus);
         System.out.println(result);

     }

     public static void init(File dir,ArrayList<Scence> scences,ArrayList<Cook> cooks,ArrayList<Menu> menus){
        Excel2JSON excelHelper = Excel2JSON.getExcel2JSONHelper();
        //dir文件，0代表是第一行为保存到数据库或者实体类的表头，一般为英文的字符串，2代表是第二种模板，
        JSONObject jsonObject = excelHelper.readExcle(dir, 0, 2);
        JSONArray arrayScence = jsonObject.getJSONArray("scence");
        JSONArray arrayCook = jsonObject.getJSONArray("cook");
        JSONArray arrayMenu = jsonObject.getJSONArray("menu");
        Map<String,Menu> menuMap=new HashMap<>();
        arrayScence.remove(0);
        arrayScence.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Scence scence = JSONObject.parseObject(json.toString(), Scence.class);
            scences.add(scence);

        });
        arrayCook.remove(0);
        arrayCook.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Cook cook = JSONObject.parseObject(json.toString(), Cook.class);
            cooks.add(cook);
        });
        arrayMenu.remove(0);
        arrayMenu.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Menu menu = JSONObject.parseObject(json.toString(), Menu.class);
            menuMap.put(menu.getName(),menu);
        });
        String[] menuArray=new String[]{"牛肉煎包","葱油拌面","香菇青菜"};
        for(String menu:menuArray){
            if(menuMap.containsKey(menu)){
                menus.add(menuMap.get(menu));
            }
            else{
                continue;
            }
        }
    }
    static  String allocatingTask(ArrayList<Scence> scences,ArrayList<Cook>
                                  cooks,ArrayList<Menu> menus){

        return null;
    }

    }

