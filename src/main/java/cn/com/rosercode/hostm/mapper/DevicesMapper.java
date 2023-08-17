package cn.com.rosercode.hostm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.rosercode.hostm.model.Device;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rosercode
 * @date 2023/8/17 13:03
 */

@Mapper
public interface DevicesMapper extends BaseMapper<Device> {

}