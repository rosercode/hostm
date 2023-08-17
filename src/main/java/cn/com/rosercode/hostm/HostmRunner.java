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

    @Resource
    private DevicesMapper devicesMapper;

    @Resource
    private EmailLogService emailLogService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start Alter");
        // 项目启动

        // 1、发送邮件给管理员
        log.info("Server start finally, And send e-mail to manger");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 利用 Thymeleaf 模板构建 html 文本
        Context ctx = new Context();
        // 给模板的参数的上下文
        ctx.setVariable("name", projectName);
        ctx.setVariable("startupTime", now.format(formatter));
        ctx.setVariable("javaVersion", System.getProperty("java.version"));
        ctx.setVariable("javaVendor", System.getProperty("java.vendor"));

        // 执行模板引擎，执行模板引擎需要传入模板名、上下文对象
        String emailText = templateEngine.process("startup", ctx);
        String subject = "项目启动通知";
        boolean isSuccess = iMailSender.sendHtmlMail(managerMail, subject, emailText);
        if (isSuccess){
            log.info("email send successfully.");
        }else {
            log.error("email send failed.");
        }
        EmailLog emailLog = new EmailLog(from, managerMail, subject, emailText, new Timestamp(System.currentTimeMillis()), isSuccess ? 1 : 0);
        emailLogService.save(emailLog);

        // 2. 设置所有设备的默认状态
        log.info("check all device's status whether common with the database record");
        log.info("if not, update database's record.");
        for (Device device : devicesMapper.selectList(null)){
            if (StringUtils.isNotBlank(device.getIpAddress())){
                boolean isDeviceReachable = isReachable(device.getIpAddress());
                // 设备是否新上线（先前状态是不在线）
                boolean isOffline = isDeviceReachable && device.getStatus() == 1;
                // 设备新下线（先前状态是在线）
                boolean isOnline = !isDeviceReachable && device.getStatus() == 0;
                if (isOffline || isOnline) {
                    device.setStatus(device.getStatus() == 1 ? 0 : 1);
                    log.info("update device(id:{}) status", device.getId());
                    devicesMapper.updateById(device);
                }
            }
        }
        log.info("All check finally.");
    }
}
