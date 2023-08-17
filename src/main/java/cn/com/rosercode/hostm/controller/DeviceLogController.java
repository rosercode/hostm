package cn.com.rosercode.hostm.controller;

import cn.com.rosercode.hostm.model.Device;
import cn.com.rosercode.hostm.model.DeviceStatusLog;
import cn.com.rosercode.hostm.service.DeviceStatusLogService;
import cn.com.rosercode.hostm.service.DevicesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author rosercode
 * @date 2023/8/17 14:09
 */

@Controller
public class DeviceLogController {

    @Resource
    private DevicesService devicesService;

    @Resource
    private DeviceStatusLogService deviceStatusLogService;


    @GetMapping("/devicesAndLogs")
    public String showDevicesAndLogs(Model model) {
        // 获取设备列表和设备日志列表数据
        List<Device> deviceList = devicesService.list(null);
        List<DeviceStatusLog> deviceStatusLogs = deviceStatusLogService.list(null);

        // 将数据添加到模型中，用于在模板中渲染
        model.addAttribute("devices", deviceList);
        model.addAttribute("logs", deviceStatusLogs);

        // 返回模板的逻辑视图名
        return "devices_and_logs";
    }
}
