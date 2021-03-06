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
    public static void main(String[] args) throws Exception {
         File dir = new File("爆炒江湖.xlsx");
         ArrayList<Scence> scences = new ArrayList<>();
         ArrayList<Cook> cooks = new ArrayList<>();
         ArrayList<Menu> menus = new ArrayList<>();
         //初始化数据
         init(dir, scences, cooks, menus);
         //分配任务
         String result = allocatingTask(scences, cooks, menus);
         System.out.println(result);
    }

    public static void init(File dir, ArrayList<Scence> scences, ArrayList<Cook> cooks, ArrayList<Menu> menus) {
        Excel2JSON excelHelper = Excel2JSON.getExcel2JSONHelper();
        //dir文件，0代表是第一行为保存到数据库或者实体类的表头，一般为英文的字符串，2代表是第二种模板，
        JSONObject jsonObject = excelHelper.readExcle(dir, 0, 2);
        JSONArray arrayScence = jsonObject.getJSONArray("scence");
        JSONArray arrayCook = jsonObject.getJSONArray("cook");
        JSONArray arrayMenu = jsonObject.getJSONArray("menu");
        Map<String, Menu> menuMap = new HashMap<>();
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
            menuMap.put(menu.getName(), menu);
        });
        String[] menuArray = new String[]{"番茄肥牛", "糖番茄", "葱油拌面","盐酥鸡"};
        for (String menu : menuArray) {
            if (menuMap.containsKey(menu)) {
                menus.add(menuMap.get(menu));
            } else {
                continue;
            }
        }
    }

    static String allocatingTask(ArrayList<Scence> scences, ArrayList<Cook>
            cooks, ArrayList<Menu> menus) throws Exception {
        String result = "result:";
        getCookForMenu(cooks, menus);
        for (Menu menu : menus) {
            System.out.println("Key = " + menu.getName() + ", Value = " + menu.getCandidateCookList());
            if (menu.getCandidateCookList() == null) {
                throw new Exception("no one can cook this cake  " + menu.getName());
            }
            for (String name : menu.getCandidateCookList()) {
                Cook cook = getCookByName(name, cooks);
                if (cook == null) {
                    throw new Exception("bussiness exception");
                }
                if (cook.getCanBeChoose() > cook.getChoosed()) {
                    cook.setChoosed(cook.getChoosed() + 1);
                    menu.setCook(name);
                    break;
                }
            }
        }
        getCookForScence(cooks, scences);
        for (Scence scence : scences) {
            scence.setCookList(new ArrayList<>());
        }
        for (int i = 0; isAllBeChoosed(cooks) && i < cooks.size() / scences.size() + 1; i++) {
            for (Scence scence : scences) {
                System.out.println("Key = " + scence.getName() + ", Value = " + scence.getCandidateCookList());
                if (scence.getCandidateCookList() == null) {
                    throw new Exception("no one can cook this cake  " + scence.getName());
                }
                for (String name : scence.getCandidateCookList()) {
                    if (scence.getChoosed() == scence.getLevel()) {
                        break;
                    }
                    Cook cook = getCookByName(name, cooks);
                    if (cook == null) {
                        throw new Exception("bussiness exception");
                    }
                    if (cook.getChoosed() == 0) {
                        scence.setChoosed(scence.getChoosed() + 1);
                        cook.setChoosed(cook.getChoosed() + 1);
                        scence.getCookList().add(name);
                        break;
                    }
                }
            }
        }
        for (Menu menu : menus) {
            result = result + menu.getName() + ": " + menu.getCook() + " ";
        }
        for (Scence scence : scences) {
            result = result + scence.getName() + ": ";
            for (String name : scence.getCookList()) {
                result = result + name + " ";
            }
        }
        return result;
    }

    static boolean isAllBeChoosed(ArrayList<Cook> cooks) {
        for (Cook cook : cooks) {
            if (cook.getChoosed() == 0) {
                return true;
            }
        }
        return false;
    }

    static Scence getScenceByName(String name, ArrayList<Scence> scences) {
        for (Scence scence : scences) {
            if (scence.getName().equals(name)) {
                return scence;
            }
        }
        return null;
    }

    static Menu getMenuByName(String name, ArrayList<Menu> menus) {
        for (Menu menu : menus) {
            if (menu.getName().equals(name)) {
                return menu;
            }
        }
        return null;
    }

    static Cook getCookByName(String name, ArrayList<Cook> cooks) {
        for (Cook cook : cooks) {
            if (cook.getName().equals(name)) {
                return cook;
            }
        }
        return null;
    }

    ;

    static void getCookForMenu(ArrayList<Cook> cooks, ArrayList<Menu> menus) throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        for (Menu menu : menus) {
            ArrayList<CookMenu> cookMenus = new ArrayList<>();
            for (Cook cook : cooks) {
                //获取候选厨师
                if (cook.getBoil() < menu.getBoil()) {
                    continue;
                }
                if (cook.getSlice() < menu.getSlice()) {
                    continue;
                }
                if (cook.getSteamed() < menu.getSteamed()) {
                    continue;
                }
                if (cook.getToast() < menu.getToast()) {
                    continue;
                }
                if (cook.getFry() < menu.getFry()) {
                    continue;
                }
                if (cook.getZha() < menu.getZha()) {
                    continue;
                }
                int count = 6;
                int sum = cook.getBoil() + cook.getZha() + cook.getFry() + cook.getToast() + cook.getSlice() + cook.getSteamed();
                if (menu.getZha() == 0) {
                    count--;
                    sum = sum - cook.getZha();
                }
                if (menu.getSteamed() == 0) {
                    count--;
                    sum = sum - cook.getSteamed();
                }
                if (menu.getBoil() == 0) {
                    count--;
                    sum = sum - cook.getBoil();
                }
                if (menu.getFry() == 0) {
                    count--;
                    sum = sum - cook.getFry();
                }
                if (menu.getToast() == 0) {
                    count--;
                    sum = sum - cook.getToast();
                }
                if (menu.getSlice() == 0) {
                    count--;
                    sum = sum - cook.getSlice();
                }

                int weight = sum / count;
                CookMenu cookMenu = new CookMenu();
                cookMenu.setName(cook.getName());
                cookMenu.setWeight(weight);
                cookMenus.add(cookMenu);
            }
            //排序候选厨师
            if (cookMenus == null) {
                throw new Exception("no one can cook this cake  " + menu.getName());
            }
            menu.setCandidateCookList(sortCookForMenu(cookMenus));
        }
    }

    static void getCookForScence(ArrayList<Cook> cooks, ArrayList<Scence> scences) throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        for (Scence scence : scences) {
            ArrayList<CookMenu> cookMenus = new ArrayList<>();
            for (Cook cook : cooks) {
                if (cook.getChoosed() > 1) {
                    continue;
                }
                if (cook.getVege() < scence.getVege()) {
                    continue;
                }
                if (cook.getFish() < scence.getFish()) {
                    continue;
                }
                if (cook.getRice() < scence.getRice()) {
                    continue;
                }
                if (cook.getMeet() < scence.getMeet()) {
                    continue;
                }

                int sum = cook.getVege() + cook.getMeet() + cook.getRice() + cook.getFish();
                if (scence.getMeet() == 0) {
                    sum = sum - cook.getMeet();
                }
                if (scence.getRice() == 0) {
                    sum = sum - cook.getRice();
                }
                if (scence.getVege() == 0) {
                    sum = sum - cook.getVege();
                }
                if (scence.getFish() == 0) {
                    sum = sum - cook.getFish();
                }
                CookMenu cookMenu = new CookMenu();
                cookMenu.setName(cook.getName());
                cookMenu.setWeight(sum);
                cookMenus.add(cookMenu);
            }
            scence.setCandidateCookList(sortCookForMenu(cookMenus));
        }
    }

    static ArrayList<String> sortCookForMenu(ArrayList<CookMenu> cookMenus) {
        //按照权重排列
        if (cookMenus == null || cookMenus.size() <= 0) {
            return new ArrayList<String>();
        }

        Collections.sort(cookMenus, new Comparator<CookMenu>() {
                    @Override
                    public int compare(CookMenu o1, CookMenu o2) {
                        // TODO Auto-generated method stub

                        return o1.getWeight().compareTo(o2.getWeight());
                    }
                }
        );
        Collections.reverse(cookMenus);
        ArrayList<String> result = new ArrayList<>();
        for (CookMenu cookMenu : cookMenus) {
            result.add(cookMenu.getName());
        }
        return result;
    }
}

