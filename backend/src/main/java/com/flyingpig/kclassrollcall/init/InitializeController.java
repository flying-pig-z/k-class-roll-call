package com.flyingpig.kclassrollcall.init;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;


@Slf4j(topic = "Initialize")
@RestController
@RequiredArgsConstructor
public class InitializeController {

    private final DataSource dataSource;

    @GetMapping("/initialize/application")
    public void initializeDispatcherServletAndDataSource() {
        log.info("开始初始化DispatcherServlet和数据源");
        try {
            log.info("初始化DispatcherServlet和数据源成功");
            dataSource.getConnection().createStatement().execute("SELECT 1");
        } catch (Exception e) {
            log.error("初始化DispatcherServlet和数据源失败", e);
        }
    }
}