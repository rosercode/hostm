package cn.com.rosercode.hostm.task;


import cn.com.rosercode.hostm.component.IMailSender;
import cn.com.rosercode.hostm.model.Device;
import cn.com.rosercode.hostm.model.DeviceStatusLog;
import cn.com.rosercode.hostm.model.EmailLog;
import cn.com.rosercode.hostm.service.DeviceStatusLogService;
import cn.com.rosercode.hostm.service.DevicesService;
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
    private DeviceStatusLogService deviceStatusLogService;

    @Resource
    private DevicesService devicesService;


    @Value("${hm.manager.mail}")
    private String managerMail;


    @Value("${spring.mail.username}")
    private String from;

    @Value("${hm.manager.repeat}")
    private Integer repeat;

    @Resource
    private EmailLogService emailLogService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void sayWord() {
        for (Device device : devicesService.list(null)) {
            handleDeviceStatusChange(device);
        }
        log.info("");
    }

    private void handleDeviceStatusChange(Device device) {
        // whether device is reachable
        boolean isDeviceReachable = isReachable(device.getIpAddress());
        // judge the host's status whether change
        boolean isOffline = isDeviceReachable && device.getStatus() == 1;
        boolean isOnline = !isDeviceReachable && device.getStatus() == 0;
        // judge whether record log and send mail to manger
        if (isOffline || isOnline) {
            if (!repeatCheck(device.getIpAddress(), isDeviceReachable, this.repeat)) {
                return;
            }
            // 1. record log.
            Boolean isSuccess = updateDeviceStatusAndLog(device, isDeviceReachable);
            // 2. send mail to manager.
            sendStatusChangeEmail(device);
        }
    }

    private Boolean updateDeviceStatusAndLog(Device device, boolean isDeviceReachable) {
        DeviceStatusLog deviceStatusLog = new DeviceStatusLog();
        deviceStatusLog.setDeviceId(device.getId());
        deviceStatusLog.setStatus(isDeviceReachable ? 0 : 1);
        deviceStatusLog.setTimestamp(new Timestamp(System.currentTimeMillis()));
        deviceStatusLogService.save(deviceStatusLog);

        device.setStatus(isDeviceReachable ? 0 : 1);
        return devicesService.updateById(device);
    }

    private void sendStatusChangeEmail(Device device) {

        Context ctx = new Context();
        ctx.setVariable("status", device.getStatus());
        ctx.setVariable("startupTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ctx.setVariable("deviceName", device.getDeviceName());
        ctx.setVariable("deviceLocation", device.getIpAddress());
        ctx.setVariable("deviceType", device.getDeviceType());

        String emailText = templateEngine.process("device_notification", ctx);
        String subject = "设备状态变化通知";
        boolean isSuccess = iMailSender.sendHtmlMail(managerMail, subject, emailText);
        EmailLog emailLog = new EmailLog(from, managerMail, subject, emailText, new Timestamp(System.currentTimeMillis()), isSuccess ? 1 : 0);
        emailLogService.save(emailLog);

        if (isSuccess) {
            log.info("Email send Successfully.");
        } else {
            log.error("Email send Failed.");
        }
    }

    /**
     * when the status is changed, repeat check n times.
     *
     * @param ipAddress ip address
     * @param current   whether current device is reachable.
     * @param count     check count
     * @return
     */
    public static boolean repeatCheck(String ipAddress, Boolean current, Integer count) {
        for (int i = 0; i < count; i++) {
            if (current != isReachable(ipAddress)) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("repeat {}", i + 1);
                return false;
            }
        }
        return true;
    }

}