package com.practice.GhibliGen.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GhibliController {

    @Value("${stability.api.key}")
    private String apiKey;

    @Value("${stability.api.url}")
    private String apiUrl;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateArt(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        String outputFormat = "png"; // or "png", "jpeg"

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(List.of(MediaType.valueOf("image/*")));
        headers.setBearerAuth(apiKey);

        // Request Body (multipart/form-data)
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("prompt", prompt);
        formData.add("output_format", outputFormat);

        // Combine headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        // Make the API call
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            // Return image bytes directly to frontend
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG) // You can use IMAGE_WEBP or others
                    .body(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
