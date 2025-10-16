package kr.ac.dankook.VettAIRecordService.util;

import kr.ac.dankook.VettAIRecordService.error.ErrorCode;
import kr.ac.dankook.VettAIRecordService.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {

    private static String ALGORITHM;
    private static byte[] SECRET_KEY;

    @Value("${app.secret.entity}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey.getBytes();
    }
    @Value("${app.secret.algorithm.entity}")
    public void setAlgorithm(String algorithm) {
        ALGORITHM = algorithm;
    }

    public static String encrypt(Long value) {
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] valueBytes = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
            byte[] encryptedData = cipher.doFinal(valueBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedData);
        }catch (Exception e){
            log.error(
                    "[encrypt_entity_error, component={}, value={}, error={}]",
                    "EncryptionUtil", value, e.getMessage());
            return String.valueOf(value);
        }
    }

    public static Long decrypt(String encryptedData) {
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedData = Base64.getUrlDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedData);
            return ByteBuffer.wrap(decryptedBytes).getLong();
        }catch (Exception e){
            log.error(
                    "[decrypt_entity_error, component={}, value={}, error={}]",
                    "EncryptionUtil", encryptedData, e.getMessage());
            throw new CustomException(ErrorCode.INVALID_ENCRYPT_PK);
        }
    }
}
