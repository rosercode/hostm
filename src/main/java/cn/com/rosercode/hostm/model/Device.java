package cn.com.rosercode.hostm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rosercode
 * @date 2023/8/17 12:58
 */


@Data
@NoArgsConstructor
@AllArgsConstructor

@TableName("t_devices")
public class Device {
    /**
     * 设备 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备 IP 地址
     */
    private String ipAddress;

    /**
     * 当前设备状态 (0: 在线, 1: 离线)
     */
    private int status;
}