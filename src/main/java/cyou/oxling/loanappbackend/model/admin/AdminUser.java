package cyou.oxling.loanappbackend.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 管理员用户信息表
 * 
 * 字段说明：
 * id - 主键ID
 * employeeId - 管理员/风控人员的工号或标识，唯一
 * username - 管理员可用于登录时的别名或展示名
 * password - 存储哈希后密码
 * twoFactorSecret - 可存储2FA秘钥，如Google Authenticator
 * twoFactorEnabled - 是否开启双因素认证：0=未开启，1=已开启
 * role - 角色，例如admin、risk_officer等角色区分
 * status - 状态：0=禁用；1=正常；2=离职等
 * email - 邮箱
 * lastLoginTime - 最后登录时间
 * createTime - 创建时间
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
    private Long id;
    private String employeeId;
    private String username;
    private String password;
    private String twoFactorSecret;
    private Integer twoFactorEnabled;
    private String role;
    private Integer status;
    private String email;
    private Date lastLoginTime;
    private Date createTime;
    private Date updateTime;
} 