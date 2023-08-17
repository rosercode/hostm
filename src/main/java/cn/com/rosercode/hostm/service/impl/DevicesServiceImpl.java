package cn.com.rosercode.hostm.service.impl;

import cn.com.rosercode.hostm.mapper.DevicesMapper;
import cn.com.rosercode.hostm.model.Device;
import cn.com.rosercode.hostm.service.DevicesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author rosercode
 * @date 2023/8/17 16:12
 */

@Service
public class DevicesServiceImpl  extends ServiceImpl<DevicesMapper, Device> implements DevicesService {


}
