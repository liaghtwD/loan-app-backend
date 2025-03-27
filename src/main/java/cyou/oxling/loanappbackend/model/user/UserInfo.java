package cyou.oxling.loanappbackend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 用户基础信息表
 * 
 * 字段说明：
 * id - 主键ID
 * phone - 用户手机号，唯一索引，用于短信验证码登录
 * password - 密码（哈希后），若只使用短信登录可为空
 * email - 邮箱，可为空
 * status - 用户状态：0=未激活；1=正常；2=冻结；3=黑名单等
 * lastLoginTime - 最后登录时间
 * deleted - 软删除标志：0=未删除；1=已删除
 * createTime - 创建时间
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String phone;
    private String password;
    private String email;
    private Integer status;
    private Date lastLoginTime;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}
