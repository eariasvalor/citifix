package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.AiChatInputPort;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.ai.ChatRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.ai.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatInputPort chatUseCase;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> sendMessage(
            @RequestBody @Valid ChatRequest request,
            Principal principal) {

        String aiMessage = chatUseCase.chat(principal.getName(), request.message());
        return ResponseEntity.ok(ChatResponse.of(aiMessage));
    }
}