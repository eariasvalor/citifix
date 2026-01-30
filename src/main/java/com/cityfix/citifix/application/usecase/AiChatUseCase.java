package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.AiChatInputPort;
import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.domain.port.out.AiAssistantPort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiChatUseCase implements AiChatInputPort {

    private final AiAssistantPort aiAssistant;
    private final IssueRepositoryPort issueRepository;
    private final CreateIssueInputPort createIssueUseCase;
    private final UserRepositoryPort userRepository;

    @Override
    public String chat(String userEmail, String message) {
        String fullMessage = String.format(
                "You are the official CityFix assistant. The current user is: %s. " +
                        "You can help report problems or consult existing ones. " +
                        "User message: %s", userEmail, message);

        return aiAssistant.analyzeIssueDescription("User context: " + userEmail + " | Message: " + message);
    }
}