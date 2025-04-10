package cyou.oxling.loanappbackend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 用户拓展信息表
 * 
 * 字段说明：
 * id - 主键ID
 * userId - 用户ID，关联user_info表
 * name - 真实姓名
 * idCardNo - 身份证号，敏感信息，需考虑加密或脱敏
 * bankCardNo - 银行卡号，默认用来自动还款，需考虑加密或脱敏
 * birthday - 生日，可根据身份证号提取，也可单独存储
 * gender - 性别 男 1 ; 女 2
 * address - 联系地址
 * avatarUrl - 头像地址
 * createTime - 创建时间
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private Long userId;
    private String name;
    private String idCardNo;
    private String bankCardNo;
    private Date birthday;
    private int gender;
    private String address;
    private String avatarUrl;
    private Date createTime;
    private Date updateTime;
} 