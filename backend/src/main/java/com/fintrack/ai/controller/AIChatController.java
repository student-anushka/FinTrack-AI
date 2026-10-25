
package com.fintrack.ai.controller;

import com.fintrack.ai.dto.AIChatRequest;
import com.fintrack.ai.dto.AIChatResponse;
import com.fintrack.ai.service.AIChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AIChatController {

    private final AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AIChatResponse> chat(
            @Valid @RequestBody AIChatRequest request) {

        AIChatResponse response = aiChatService.askQuestion(request.question());

        return ResponseEntity.ok(response);
    }
}
