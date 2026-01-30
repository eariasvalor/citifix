package com.cityfix.citifix.infrastructure.config.ai;

import org.mockito.Mockito;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AiTestConfig {

    @Bean
    @Primary
    public ChatModel chatModel() {
        return Mockito.mock(ChatModel.class);
    }
}