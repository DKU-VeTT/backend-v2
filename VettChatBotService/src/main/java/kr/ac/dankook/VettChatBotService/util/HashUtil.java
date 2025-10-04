package kr.ac.dankook.VettChatBotService.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class HashUtil {

    private static final String DEFAULT_DELIMITER = "|";

    public static String sha256HexOfParts(String ... parts){
        return sha256HexOfPartsWithDelimiter(DEFAULT_DELIMITER,parts);
    }

    public static String sha256HexOfPartsWithDelimiter(String delimiter, String... parts) {
        String normalized = Arrays.stream(parts)
                .map(HashUtil::normalize)
                .collect(Collectors.joining(delimiter));
        return DigestUtils.sha256Hex(normalized.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalize(String s) {
        if (s == null) return "null";
        String trimmed = s.trim();
        return trimmed.isEmpty() ? "" : trimmed;
    }
}
