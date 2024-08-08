package cn.iocoder.yudao.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 *
 * @author 芋道源码
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${yudao.info.base-package}
@AutoConfigurationPackage(basePackages = {"cn.iocoder.yudao"})
@SpringBootApplication(scanBasePackages = {"${yudao.info.base-package}.server", "${yudao.info.base-package}.module", "ai.timefold.solver"})
public class YudaoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YudaoServerApplication.class, args);
    }

}
