package cn.com.rosercode.hostm;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@MapperScan("cn.com.rosercode.hostm.mapper")
public class HostmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostmApplication.class, args);
    }

}
