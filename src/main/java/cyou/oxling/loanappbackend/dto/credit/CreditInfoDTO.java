package cyou.oxling.loanappbackend.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 信用信息DTO
 */
@Data
public class CreditInfoDTO {
    private Integer creditScore;
    private BigDecimal creditLimit;
    private BigDecimal usedCredit;
} 