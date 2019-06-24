package com.ttms.Config;

import org.springframework.boot.CommandLineRunner;

//SpringBoot容器启动后执行
///@Component
public class ExecuteOnStartUp implements CommandLineRunner {
    public void run(String... args) throws Exception {
        System.out.println("执行commandLineRunner方法");
    }
}
