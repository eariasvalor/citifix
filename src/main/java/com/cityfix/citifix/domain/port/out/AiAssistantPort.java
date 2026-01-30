package com.cityfix.citifix.domain.port.out;

public interface AiAssistantPort {
    String analyzeIssueDescription(String description);
    String analyzeImage(String imageUrl, String promptText);}

