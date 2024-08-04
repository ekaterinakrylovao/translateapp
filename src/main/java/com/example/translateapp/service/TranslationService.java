package com.example.translateapp.service;

import com.example.translateapp.entity.Translation;
import com.example.translateapp.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;
    private final TranslationRepository translationRepository;

    @Value("${yandex.translate.api.url}")
    private String translateApiUrl;

    @Value("${yandex.translate.api.key}")
    private String apiKey;

    public TranslationService(RestTemplate restTemplate, TranslationRepository translationRepository) {
        // Устанавливаем поддержку UTF-8 для всех запросов и ответов
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        this.translationRepository = translationRepository;
    }

    public String translateText(String text, String sourceLang, String targetLang) {
        String url = translateApiUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        String requestBody = String.format("{\"texts\": [\"%s\"], \"sourceLanguageCode\": \"%s\", \"targetLanguageCode\": \"%s\"}",
                text, sourceLang, targetLang);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            // Парсинг JSON-ответа для извлечения текста перевода
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode translationsNode = rootNode.path("translations");

            if (translationsNode.isArray() && translationsNode.size() > 0) {
                return translationsNode.get(0).path("text").asText();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Обработка исключений
        }

        return "[error]"; // Возвращаем ошибку, если не удалось извлечь текст
    }

    @Transactional
    public void saveTranslation(String ipAddress, String originalText, String translatedText) {
        Translation translation = new Translation();
        translation.setIpAddress(ipAddress);
        translation.setOriginalText(originalText);
        translation.setTranslatedText(translatedText);
        translationRepository.save(translation);
    }
}
