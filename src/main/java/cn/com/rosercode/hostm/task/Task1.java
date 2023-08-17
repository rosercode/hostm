package cn.com.rosercode.hostm.task;


import cn.com.rosercode.hostm.component.IMailSender;
import cn.com.rosercode.hostm.mapper.DeviceStatusLogMapper;
import cn.com.rosercode.hostm.mapper.DevicesMapper;
import cn.com.rosercode.hostm.model.Device;
import cn.com.rosercode.hostm.model.DeviceStatusLog;
import cn.com.rosercode.hostm.model.EmailLog;
import cn.com.rosercode.hostm.service.EmailLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
 * @date 2023/8/17 10:05
 */

@Component
@Slf4j
public class Task1 {

    @Resource
    private SpringTemplateEngine templateEngine;

    @Resource
    private IMailSender iMailSender;

    @Resource
    private DeviceStatusLogMapper deviceStatusLogMapper;

    @Resource
    private DevicesMapper devicesMapper;


    @Value("${hm.manager.mail}")
    private String managerMail;


    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private EmailLogService emailLogService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void sayWord() {
        for (Device device : devicesMapper.selectList(null)) {
            DeviceStatusLog deviceStatusLog = new DeviceStatusLog();
            // 设备是否可达
            boolean isDeviceReachable = isReachable(device.getIpAddress());
            // 设备是否新上线（先前状态是不在线）
            boolean isOffline = isDeviceReachable && device.getStatus() == 1;
            // 设备新下线（先前状态是在线）
            boolean isOnline = !isDeviceReachable && device.getStatus() == 0;
            // 判断是否需要记录日志
            if (isOffline || isOnline) {
                // 1、记录日志和设置新的状态
                deviceStatusLog.setDeviceId(device.getId());
                deviceStatusLog.setStatus(isDeviceReachable ? 0 : 1);
                deviceStatusLog.setTimestamp(new Timestamp(System.currentTimeMillis()));
                deviceStatusLogMapper.insert(deviceStatusLog);
                device.setStatus(device.getStatus() == 1 ? 0 : 1);
                devicesMapper.updateById(device);

                // 2、发送邮件给管理员
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                // 利用 Thymeleaf 模板构建 html 文本
                Context ctx = new Context();
                // 给模板的参数的上下文
                ctx.setVariable("status", device.getStatus());
                ctx.setVariable("startupTime", now.format(formatter));
                ctx.setVariable("deviceName", device.getDeviceName());
                ctx.setVariable("deviceLocation", device.getIpAddress());
                ctx.setVariable("deviceType", device.getDeviceType());

                // 执行模板引擎，执行模板引擎需要传入模板名、上下文对象
                String emailText = templateEngine.process("device_notification", ctx);
                String subject = "设备状态变化通知";
                boolean isSuccess = iMailSender.sendHtmlMail(managerMail, subject, emailText);
                EmailLog emailLog = new EmailLog(from, managerMail, subject, emailText, new Timestamp(System.currentTimeMillis()), isSuccess ? 1 : 0);
                emailLogService.save(emailLog);

                if (isSuccess) {
                    log.info("Email send Successfully.");
                    break;
                }
                log.error("Email send Failed.");
            }
        }
        log.info("");
    }
}