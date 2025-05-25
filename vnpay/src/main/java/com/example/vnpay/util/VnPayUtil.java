package com.example.vnpay.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayUtil {

    public static String buildUrlWithSecureHash(Map<String, String> params, String secretKey) {
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String key : sortedKeys) {
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                hashData.append(key).append('=').append(value);
                query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                        .append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (!key.equals(sortedKeys.get(sortedKeys.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        return query.toString();
    }

    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).toUpperCase();
        } catch (Exception ex) {
            throw new RuntimeException("HMAC SHA512 error: " + ex.getMessage());
        }
    }

    public static String calculateHash(Map<String, String> fields, String secretKey) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        for (String name : fieldNames) {
            String value = fields.get(name);
            if (value != null && !value.isEmpty()) {
                sb.append(name).append('=').append(value).append('&');
            }
        }
        // Xóa ký tự '&' cuối
        sb.setLength(sb.length() - 1);

        return hmacSHA512(secretKey, sb.toString());
    }
}

