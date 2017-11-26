package com.cqcupt.imis.zj.utils;

/**
 * Created by zhoujun on 2017/11/20.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinyu.meng on 2017/10/14.
 */
public class Excel2JSON {

    //常亮，用作第一种模板类型，如下图
    private static final int HEADER_VALUE_TYPE_Z = 1;
    //第二种模板类型，如下图
    private static final int HEADER_VALUE_TYPE_S = 2;
    static Excel2JSON excel2JSON = new Excel2JSON();

    public static void main(String[] args) {
        File dir = new File("J:\\爆炒江湖\\爆炒江湖.xlsx");
        Excel2JSON excelHelper = getExcel2JSONHelper();
        //dir文件，0代表是第一行为保存到数据库或者实体类的表头，一般为英文的字符串，2代表是第二种模板，
        JSONObject jsonObject = excelHelper.readExcle(dir, 0, 2);
        JSONArray arrayScence = jsonObject.getJSONArray("scence");
        JSONArray arrayCook = jsonObject.getJSONArray("cook");
        JSONArray arrayMenu = jsonObject.getJSONArray("menu");
        ArrayList<Scence> scenceArrayList=new ArrayList<>();
        ArrayList<Cook> cookArrayList=new ArrayList<>();
        ArrayList<Menu> menuArrayList=new ArrayList<>();
        Map<String,Menu> menuMap=new HashMap<>();
        arrayScence.remove(0);
        arrayScence.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Scence scence = JSONObject.parseObject(json.toString(), Scence.class);
            scenceArrayList.add(scence);

        });
        arrayCook.remove(0);
        arrayCook.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Cook cook = JSONObject.parseObject(json.toString(), Cook.class);
            cookArrayList.add(cook);
        });
        arrayMenu.remove(0);
        arrayMenu.stream().forEach(e -> {
            JSONObject json = JSONObject.parseObject(e.toString());
            Menu menu = JSONObject.parseObject(json.toString(), Menu.class);

            menuMap.put(menu.getName(),menu);
        });
     String[] menus=new String[]{"牛肉煎包","葱油拌面","香菇青菜"};
        for(String menu:menus){
            if(menuMap.containsKey(menu)){
                menuArrayList.add(menuMap.get(menu));
            }
            else{
                continue;
            }
        }
        chooseCook();

    }
   public static void chooseCook(){}
    public static Excel2JSON getExcel2JSONHelper() {
        return excel2JSON;
    }


    private boolean fileNameFileter(File file) {
        boolean endsWith = false;
        if (file != null) {
            String fileName = file.getName();
            endsWith = fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
        }
        return endsWith;
    }


    private Row getHeaderRow(Sheet sheet, int index) {
        Row headerRow = null;
        if (sheet != null) {
            headerRow = sheet.getRow(index);
        }
        return headerRow;
    }


    private Object getCellValue(Row row, int cellIndex, FormulaEvaluator formula) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                //String类型
                case Cell.CELL_TYPE_STRING:
                    return cell.getRichStringCellValue().getString();

                //number类型
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().getTime();
                    } else {
                        return cell.getNumericCellValue();
                    }
                    //boolean类型
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
                //公式
                case Cell.CELL_TYPE_FORMULA:
                    return formula.evaluate(cell).getNumberValue();
                default:
                    return null;
            }
        }
        return null;
    }


    private String getHeaderCellValue(Row headerRow, int cellIndex, int type) {
        Cell cell = headerRow.getCell(cellIndex);
        String headerValue = null;
        if (cell != null) {
            //第一种模板类型
            if (type == HEADER_VALUE_TYPE_Z) {
                headerValue = cell.getRichStringCellValue().getString();
                int l_bracket = headerValue.indexOf("（");
                int r_bracket = headerValue.indexOf("）");
                if (l_bracket == -1) {
                    l_bracket = headerValue.indexOf("(");
                }
                if (r_bracket == -1) {
                    r_bracket = headerValue.indexOf(")");
                }
                headerValue = headerValue.substring(l_bracket + 1, r_bracket);
            } else if (type == HEADER_VALUE_TYPE_S) {
                //第二种模板类型
                headerValue = cell.getRichStringCellValue().getString();
            }
        }
        return headerValue;
    }


    public JSONObject readExcle(File file, int headerIndex, int headType) {
        JSONObject result = new JSONObject();

        if (!fileNameFileter(file)) {
            return null;
        } else {
            try {
                //加载excel表格读取file
                WorkbookFactory wbFactory = new WorkbookFactory();
                Workbook wb = wbFactory.create(file);
                _readExcel(headerIndex, headType, result, wb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    public JSONObject readExcel(InputStream inputStream, int headerIndex, int headType) {
        JSONObject result = new JSONObject();
        if (null == inputStream) {
            return null;
        } else {
            try {
                //加载excel表格读取InputStream
                WorkbookFactory wbFactory = new WorkbookFactory();

                Workbook wb = wbFactory.create(inputStream);

                _readExcel(headerIndex, headType, result, wb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private void _readExcel(int headerIndex, int headType, JSONObject result, Workbook wb) {
        int sheetCount = wb.getNumberOfSheets();

        for (int i = 0; i < sheetCount; i++) {
            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
            //读取第一个sheet页
            Sheet sheet = wb.getSheetAt(i);
            //读取表头行
            Row headerRow = getHeaderRow(sheet, headerIndex);
            //读取数据
            FormulaEvaluator formula = wb.getCreationHelper().createFormulaEvaluator();
            for (int r = headerIndex + 1; r <= sheet.getLastRowNum(); r++) {
                Row dataRow = sheet.getRow(r);
                Map<String, Object> map = new HashMap<String, Object>();
                for (int h = 0; h < dataRow.getLastCellNum(); h++) {
                    //表头为key
                    String key = getHeaderCellValue(headerRow, h, headType);
                    //数据为value
                    Object value = getCellValue(dataRow, h, formula);
                    if (!key.equals("") && !key.equals("null") && key != null) {
                        map.put(key, value);
                    }
                }
                lists.add(map);
            }
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(lists));
            result.put(sheet.getSheetName(), jsonArray);
        }
    }

}