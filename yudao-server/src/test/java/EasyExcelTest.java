import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasyExcelTest {
    @Test
    public void dynamicHeadWrite() {
        String fileName = "/Users/yangzy/Desktop/1.xlsx";
        EasyExcel.write(fileName)
                // 这里放入动态头
                .head(head()).sheet("模板")
                // 当然这里数据也可以用 List<List<String>> 去传入
                .doWrite(data());
    }

    private List<DemoData> data() {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<String> head = new ArrayList<>();
            head.add("字符串" + i);
            list.add(head);
        }
        return list;
    }

    @Test
    public void importTest() throws Exception {
        String fileName = "/Users/yangzy/SynologyDrive/城阳高中/2024下学期/2024  下学期 教师分工表.xlsx";
        File file = new File(fileName);

        // 匹配中文正则
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Pattern digitPattern = Pattern.compile("\\d+");
        Pattern gradePattern = Pattern.compile("\\d+班");


        Workbook workbook = WorkbookFactory.create(file);

        // 只获取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);

        String gradeTopName = "";
        String gradeSecondName = "";
        // 每个科目的课时map
        Map<String, Integer> subjectCountMap = new HashMap<>();
        // 每个科目对应的列索引map
        Map<Integer, String> subjectCellIndexMap = new HashMap<>();

        List<TimetableSettingDO> timetableSettingList = new ArrayList<>();

        int rowCount = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            gradeSecondName = "";
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            int cellCount = row.getLastCellNum();
            // 存储值和列索引的map
            Map<String, Integer> cellValueIndexMap = new HashMap<>();
            for (int cellIndex = 0; cellIndex < cellCount; cellIndex++) {
                Cell timeSlotCell = row.getCell(cellIndex);
                String value = timeSlotCell.getStringCellValue();
                if (StrUtil.isNotEmpty(value)) {
                    cellValueIndexMap.put(value, cellIndex);
                }
            }

            // 找到当前行是几班的
            for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                String value = entry.getKey();
                if (gradePattern.matcher(value).find()) {
                    gradeSecondName = value;
                }
            }

            if (cellValueIndexMap.size() == 1) {
                // 说明是年级标题行
                for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                    String gradeTitle = entry.getKey();
                    gradeTopName = gradeTitle.replace("教师分工表", "");
                }
                subjectCountMap = new HashMap<>();
                subjectCellIndexMap = new HashMap<>();
            } else if (cellValueIndexMap.size() > 1) {
                for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                    String value = entry.getKey();
                    Integer index = entry.getValue();
                    if (Character.isDigit(value.charAt(value.length() - 1))) {
                        //如果是以数字结尾, 说明是课时标题行
                        String subjectName = "";
                        Integer count = 0;
                        Matcher subjectMatcher = chinesePattern.matcher(value);
                        Matcher countMatcher = digitPattern.matcher(value);
                        while (subjectMatcher.find()) {
                            subjectName += subjectMatcher.group();
                        }
                        while (countMatcher.find()) {
                            count += Integer.valueOf(countMatcher.group());
                        }
                        if (StrUtil.isEmpty(subjectName) || count == 0) {
                            throw new Exception("错误");
                        }

                        subjectCountMap.put(subjectName, count);
                        subjectCellIndexMap.put(index, subjectName);
                    } else {
                        Integer currentIndex = entry.getValue();
                        if (!subjectCellIndexMap.containsKey(currentIndex)) {
                            continue;
                        }
                        // 这里到了真正的每个人的课时定义处
                        String teacherName = value;

                        // 获取当前列对应的科目
                        String subjectName = subjectCellIndexMap.get(currentIndex);
                        Integer count = subjectCountMap.get(subjectName);
                        TimetableSettingDO timetableSetting = new TimetableSettingDO();
                        System.out.println((gradeTopName + gradeSecondName) + "---" + teacherName + "---" + subjectName + "---" + count);
                    }
                }
            }



        }
    }
}
