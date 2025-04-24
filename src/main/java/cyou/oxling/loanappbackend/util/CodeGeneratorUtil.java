package cyou.oxling.loanappbackend.util;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 验证码生成工具类
 */
@Component
public class CodeGeneratorUtil {

    /**
     * 生成6位数字验证码
     * @return 6位数字验证码
     */
    public String generateSmsCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 生成4位字母数字混合验证码
     * @return 4位字母数字混合验证码
     */
    public String generateCaptchaCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }
} 