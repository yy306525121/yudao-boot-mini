import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}
