package com.cityfix.citifix.infrastructure.adapter.outbound.ai;

import com.cityfix.citifix.domain.port.out.AiAssistantPort;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Component
public class OpenAiAdapter implements AiAssistantPort {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;

    public OpenAiAdapter(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    /**
     * Analiza la descripción de un problema urbano.
     */
    @Override
    public String analyzeIssueDescription(String message) {
        try {
            // Construir la petición de chat
            ChatRequest request = ChatRequest.builder()
                    .addUserMessage(message)
                    .memory(chatMemory) // usar memoria en memoria
                    .build();

            // Llamar al modelo
            ChatResponse response = chatModel.chat(request);

            // Devolver el contenido del primer mensaje de respuesta
            return response.getMessages().get(0).getContent();

        } catch (Exception e) {
            return "Error analyzing issue description: " + e.getMessage();
        }
    }


    public String analyzeImage(String imageUrl, String promptText) {
        try {
            String fullPrompt = promptText + "\nImagen: " + imageUrl;

            ChatRequest request = ChatRequest.builder()
                    .addUserMessage(fullPrompt)
                    .memory(chatMemory)
                    .build();

            ChatResponse response = chatModel.chat(request);

            return response.getMessages().get(0).getContent();

        } catch (Exception e) {
            return "Error analyzing image: " + e.getMessage();
        }
    }
}
