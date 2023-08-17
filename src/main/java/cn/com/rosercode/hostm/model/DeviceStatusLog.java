package cn.com.rosercode.hostm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author rosercode
 * @date 2023/8/17 12:58
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

@TableName("t_device_status_logs")
public class DeviceStatusLog {
    /**
     * 记录 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备 ID
     */
    private Long deviceId;

    /**
     * 设备状态 (0: 上线, 1: 下线)
     */
    private int status;

    /**
     * 记录时间戳
     */
    private Timestamp timestamp;

    public DeviceStatusLog(int status, Timestamp timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }
}