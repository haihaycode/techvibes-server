package com.haihaycode.techvibesservice.service;
import org.springframework.stereotype.Service;

import java.util.Base64;
@Service
public class EncryptionUtils {

    // Mã hóa ID với chuỗi "haihaycode"
    public static String encodeId(Long id, String key) {
        String data = id + key;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    // Giải mã ID
    public static Long decodeId(String encodedData, String key) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
        String decodedString = new String(decodedBytes);
        String idString = decodedString.replace(key, "");
        return Long.parseLong(idString);
    }
}
