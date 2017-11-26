package com.cqcupt.imis.zj.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqcupt.imis.zj.utils.Excel2JSON;

import java.io.File;
import java.util.*;

/**
 * Created by zhoujun on 2017/11/26.
 */
public class Main {
     public static void main(String[] args ) throws Exception {
         File dir = new File("爆炒江湖.xlsx");
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
                                  cooks,ArrayList<Menu> menus) throws Exception {
         String result="result:";
        Map<String,List<String>> map=getCookForMenu(cooks,menus);
        for (Map.Entry<String,List<String>> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                for(String name:entry.getValue()) {
                    Cook cook = getCookByName(name, cooks);
                    if (cook == null) {
                        throw new Exception("bussiness exception");
                    }
                    if (cook.getCanBeChoose() > cook.getChoosed()) {
                        cook.setChoosed(cook.getChoosed() + 1);
                        result = result + entry.getKey() + ": " + name + " ";
                        break;
                    }
                }
        }
        Map<String,List<String>> map1=getCookForScence(cooks,scences);
        Map<String,List<String>> map2=new HashMap<>();
        for(int i=0;isAllBeChoosed(cooks) && i<cooks.size()/scences.size();i++){
            for (Map.Entry<String,List<String>> entry : map1.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                Scence scence=getScenceByName(entry.getKey(),scences);
                if(scence==null){
                    throw new Exception("bussiness exception");
                }
                result=result + entry.getKey() + ": ";
                for(String name:entry.getValue()) {
                    if(scence.getChoosed()==scence.getLevel()){
                        break;
                    }
                    Cook cook = getCookByName(name, cooks);
                    if (cook == null) {
                        throw new Exception("bussiness exception");
                    }
                    if (cook.getChoosed()==0) {
                        scence.setChoosed(scence.getChoosed()+1);
                        cook.setChoosed(cook.getChoosed() + 1);
                        result =result+ name + " ";
                    }
                }
            }
        }
        return result;
    }
    static boolean isAllBeChoosed(ArrayList<Cook> cooks){
         for(Cook cook:cooks){
             if(cook.getChoosed()==0){
                 return true;
             }
         }
         return false;
    }
    static Scence getScenceByName(String name,ArrayList<Scence> scences){
        for(Scence scence:scences){
            if(scence.getName().equals(name)){
                return scence;
            }
        }
        return null;
    }
    static Menu getMenuByName(String name,ArrayList<Menu> menus){
        for(Menu menu:menus){
            if(menu.getName().equals(name)){
                return menu;
            }
        }
        return null;
    }
    static Cook getCookByName(String name,ArrayList<Cook> cooks){
         for(Cook cook:cooks){
             if(cook.getName().equals(name)){
                 return cook;
             }
         }
         return null;
    };
   static Map<String,List<String>> getCookForMenu(ArrayList<Cook> cooks,ArrayList<Menu> menus) throws Exception {
       Map<String,List<String>> map=new HashMap<>();
         for(Menu menu:menus){
             ArrayList<CookMenu> cookMenus=new ArrayList<>();
            for(Cook cook:cooks){

                //获取候选厨师
                if(cook.getBoil()<menu.getBoil()){
                    continue;
                }
                if(cook.getSlice()<menu.getSlice()){
                    continue;
                }
                if(cook.getSteamed()<menu.getSteamed()){
                    continue;
                }
                if(cook.getToast()<menu.getToast()){
                    continue;
                }
                if(cook.getFry()<menu.getFry()){
                    continue;
                }
                if(cook.getZha()<menu.getZha()){
                    continue;
                }
                int count=6;
                int sum=cook.getBoil()+cook.getZha()+cook.getFry()+cook.getToast()+cook.getSlice()+cook.getSteamed();
                if(menu.getZha()==0){
                     count--;
                    sum=sum-cook.getZha();
                }
                if(menu.getSteamed()==0){
                    count--;
                    sum=sum-cook.getSteamed();
                }
                if(menu.getBoil()==0){
                    count--;
                    sum=sum-cook.getBoil();
                }
                if(menu.getFry()==0){
                    count--;
                    sum=sum-cook.getFry();
                }
                if(menu.getToast()==0){
                    count--;
                    sum=sum-cook.getToast();
                }
                if(menu.getSlice()==0){
                    count--;
                    sum=sum-cook.getSlice();
            }

                int weight=sum/count;
                CookMenu cookMenu=new CookMenu();
                cookMenu.setName(cook.getName());
                cookMenu.setWeight(weight);
                cookMenus.add(cookMenu);
            }
             //排序候选厨师
             map.put(menu.getName(),sortCookForMenu(cookMenus));
        }
       if(map==null || map.size()<=0){
           throw new Exception("no one can make any cake");
       }
        return map;
   }
    static Map<String,List<String>>  getCookForScence(ArrayList<Cook> cooks,ArrayList<Scence> scences) throws Exception {
        Map<String,List<String>> map=new HashMap<>();
       for(Scence scence:scences){
           ArrayList<CookMenu> cookMenus=new ArrayList<>();
           for(Cook cook:cooks){
               if(cook.getChoosed()>1){
                   continue;
               }
               if(cook.getVege()<scence.getVege()){
                   continue;
               }
               if(cook.getFish()<scence.getFish()){
                   continue;
               }
               if(cook.getRice()<scence.getRice()){
                   continue;
               }
               if(cook.getMeet()<scence.getMeet()){
                   continue;
               }
               int sum=cook.getVege()+cook.getMeet()+cook.getRice()+cook.getFish();
               CookMenu cookMenu=new CookMenu();
               cookMenu.setName(cook.getName());
               cookMenu.setWeight(sum);
               cookMenus.add(cookMenu);
           }
           map.put(scence.getName(),sortCookForMenu(cookMenus));
       }
        if(map==null || map.size()<=0){
            throw new Exception("no one can make any collection");
        }
       return map;
    }
    static ArrayList<String>  sortCookForMenu(ArrayList<CookMenu> cookMenus){
         //按照权重排列
         if(cookMenus==null || cookMenus.size()<=0){
             return new ArrayList<String>();
         }

        Collections.sort(cookMenus,new Comparator<CookMenu>(){
            @Override
            public int compare(CookMenu o1, CookMenu o2) {
                // TODO Auto-generated method stub

                return    o1.getWeight().compareTo(o2.getWeight());
            }}
        );
        Collections.reverse(cookMenus);
        ArrayList<String> result=new ArrayList<>();
        for(CookMenu cookMenu:cookMenus){
            result.add(cookMenu.getName());
        }
    return result;
   }
    }

