package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QrCodeDecoderUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> decodeStandardGstQr(String qrCodeToken) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            // 1. If your mock server uses "SignedQR_", this cleans it up automatically
            String cleanToken = qrCodeToken.replace("SignedQR_", "").trim();
            
            // 2. Split the token and grab the middle payload segment
            String[] parts = cleanToken.split("\\.");
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            String jsonString = new String(decodedBytes, StandardCharsets.UTF_8);

            // 3. Turn it into a neat Java map of Strings
            Map<String, Object> rawMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                resultMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        } catch (Exception e) {
            throw new RuntimeException("QR Parsing Error: " + e.getMessage());
        }
        return resultMap;
    }
}
