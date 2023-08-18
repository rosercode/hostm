package cn.com.rosercode.hostm;

import cn.com.rosercode.hostm.component.IMailSender;
import cn.com.rosercode.hostm.mapper.DevicesMapper;
import cn.com.rosercode.hostm.model.Device;
import cn.com.rosercode.hostm.model.EmailLog;
import cn.com.rosercode.hostm.service.EmailLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.com.rosercode.hostm.task.Task1.repeatCheck;
import static cn.com.rosercode.hostm.utils.HostReachabilityChecker.isReachable;

/**
 * @author rosercode
 * @date 2023/8/17 11:47
 */

@Component
@Slf4j
public class HostmRunner implements ApplicationRunner {

    @Resource
    private SpringTemplateEngine templateEngine;

    @Resource
    private IMailSender iMailSender;


    @Value("${spring.application.name}")
    private String projectName;


    @Value("${hm.manager.mail}")
    private String managerMail;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${hm.manager.repeat}")
    private Integer repeat;

    @Resource
    private DevicesMapper devicesMapper;

    @Resource
    private EmailLogService emailLogService;


    @Override
    public void run(ApplicationArguments args) {
        log.info("Start Alter");
        sendStartupEmail();
        // init device status
        initDeviceStatus();
        log.info("All check finally.");
    }


    private void sendStartupEmail() {
        log.info("Server start finally, And send e-mail to manger");

        Context ctx = new Context();
        ctx.setVariable("name", projectName);
        ctx.setVariable("startupTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ctx.setVariable("javaVersion", System.getProperty("java.version"));
        ctx.setVariable("javaVendor", System.getProperty("java.vendor"));

        String emailText = templateEngine.process("startup", ctx);
        String subject = "项目启动通知";
        boolean isSuccess = iMailSender.sendHtmlMail(managerMail, subject, emailText);
        logEmailResult(isSuccess);
        saveEmailLog(emailText, subject, isSuccess);
    }

    private void logEmailResult(boolean isSuccess) {
        if (isSuccess) {
            log.info("Email send successfully.");
        } else {
            log.error("Email send failed.");
        }
    }

    private void saveEmailLog(String emailText, String subject, boolean isSuccess) {
        EmailLog emailLog = new EmailLog(from, managerMail, subject, emailText, new Timestamp(System.currentTimeMillis()), isSuccess ? 1 : 0);
        emailLogService.save(emailLog);
    }

    private void initDeviceStatus() {
        log.info("Check all device's status whether common with the database record");
        log.info("If not, update database's record.");
        for (Device device : devicesMapper.selectList(null)) {
            updateDeviceIfNeeded(device);
        }
    }

    private void updateDeviceIfNeeded(Device device) {
        if (StringUtils.isNotBlank(device.getIpAddress())) {
            boolean isDeviceReachable = isReachable(device.getIpAddress());
            boolean isOffline = isDeviceReachable && device.getStatus() == 1;
            boolean isOnline = !isDeviceReachable && device.getStatus() == 0;
            if (isOffline || isOnline) {
                if (!repeatCheck(device.getIpAddress(), isDeviceReachable, this.repeat)){
                    return;
                }
                device.setStatus(device.getStatus() == 1 ? 0 : 1);
                log.info("Update device(id:{}) status", device.getId());
                devicesMapper.updateById(device);
            }
        }
    }
}
