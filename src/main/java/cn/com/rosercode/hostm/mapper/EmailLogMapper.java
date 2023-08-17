package cn.com.rosercode.hostm.mapper;

/**
 * @author rosercode
 * @date 2023/8/17 13:04
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.rosercode.hostm.model.EmailLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailLogMapper extends BaseMapper<EmailLog> {

}
