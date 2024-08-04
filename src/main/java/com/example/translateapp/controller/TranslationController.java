package com.example.translateapp.controller;

import com.example.translateapp.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/translate")
public class TranslationController {

    private final TranslationService translationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping
    public ResponseEntity<String> translate(
            @RequestParam String text,
            @RequestParam String sourceLang,
            @RequestParam String targetLang,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String[] words = text.split(" ");
        List<Future<String>> futures = new ArrayList<>();

        for (String word : words) {
            futures.add(executorService.submit(() -> translationService.translateText(word, sourceLang, targetLang)));
        }

        StringBuilder result = new StringBuilder();
        for (Future<String> future : futures) {
            try {
                result.append(future.get()).append(" ");
            } catch (InterruptedException | ExecutionException e) {
                // Обработка ошибок
                result.append("[error]").append(" ");
            }
        }

        String translatedText = result.toString().trim();
        translationService.saveTranslation(ipAddress, text, translatedText);

        return ResponseEntity.ok(translatedText);
    }
}
