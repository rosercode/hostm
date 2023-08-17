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
 * @date 2023/8/17 13:01
 */


@Data
@NoArgsConstructor
@AllArgsConstructor

@TableName("t_email_logs")
public class EmailLog {
    /**
     * 记录 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发件人邮箱地址
     */
    private String sender;

    /**
     * 收件人邮箱地址
     */
    private String recipient;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Timestamp sentAt;

    /**
     * 发送状态 (成功/失败等)
     */
    private Integer status;

    public EmailLog(String sender, String recipient, String subject, String content, Timestamp sentAt, Integer status) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.sentAt = sentAt;
        this.status = status;
    }
}