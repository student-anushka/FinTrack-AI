
package com.fintrack.ai.service;

import com.fintrack.ai.dto.AIChatResponse;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Service;

@Service
public class AIChatService {

    private final OpenAIClient openAIClient;

    public AIChatService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public AIChatResponse askQuestion(String question) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4O)
                .input(
                        "You are FinTrack AI, a helpful personal finance "
                                + "assistant. Explain financial concepts clearly, "
                                + "give practical budgeting and saving guidance, "
                                + "and never claim to know a user's financial data "
                                + "unless it has been provided. Do not guarantee "
                                + "investment returns. This is general information, "
                                + "not professional financial advice.\n\n"
                                + "User question: " + question)
                .build();

        Response response = openAIClient.responses().create(params);

        String answer = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .reduce("", (first, second) -> first + second);

        if (answer.isBlank()) {
            throw new IllegalStateException(
                    "The AI service returned an empty response.");
        }

        return new AIChatResponse(answer);
    }
}
