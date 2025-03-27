package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.user.UserCredit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 信用数据访问接口
 */
@Mapper
public interface CreditDao {
    
    /**
     * 根据用户ID查询信用信息
     * @param userId 用户ID
     * @return 信用信息
     */
    UserCredit findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新信用额度
     * @param userId 用户ID
     * @param creditLimit 信用额度
     * @return 影响行数
     */
    int updateCreditLimit(@Param("userId") Long userId, @Param("creditLimit") java.math.BigDecimal creditLimit);
    
    /**
     * 更新已用额度
     * @param userId 用户ID
     * @param usedCredit 已用额度
     * @return 影响行数
     */
    int updateUsedCredit(@Param("userId") Long userId, @Param("usedCredit") java.math.BigDecimal usedCredit);
    
    /**
     * 更新信用分
     * @param userId 用户ID
     * @param creditScore 信用分
     * @return 影响行数
     */
    int updateCreditScore(@Param("userId") Long userId, @Param("creditScore") Integer creditScore);
} 