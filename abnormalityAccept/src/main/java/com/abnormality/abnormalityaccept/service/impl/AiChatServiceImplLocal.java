package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.AiChatService;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class AiChatServiceImplLocal implements AiChatService {


    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Value("${rag-flow.model}")
    private String model;

    @Value("${rag-flow.api-key}")
    private String apiKey;

    @Value("${rag-flow.endpoint}")
    private String endpoint;

    @Autowired
    private OpenAIClient client;
    @Override
    public String chat(String message) {
        return "";
    }

    @Override
    public String chat(String message, String userId) {
        return "";
    }

    @Override
    public Flux<ServerSentEvent<String>> chatStream(String message, String userId, boolean contentOnly) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 在单独的线程中执行流式调用
        executor.submit(() -> {
            try {
                callStreamWithMessage(message, sink,contentOnly);
            } catch (Exception e) {
                sink.tryEmitError(e);
            } finally {
                sink.tryEmitComplete();
            }
        });

        return sink.asFlux();
    }

    private void callStreamWithMessage(String message, Sinks.Many<ServerSentEvent<String>> sink, boolean contentOnly){
//        OpenAIClient client = OpenAIOkHttpClient.builder()
//                .apiKey(apiKey)
//                .baseUrl(endpoint)
//                .build();
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .addSystemMessage("You are an RAG assistant.")
                .addUserMessage(message)
                .build();
        StreamResponse<ChatCompletionChunk> streamResponse=null;
        try  {
            streamResponse = client.chat().completions().createStreaming(params);
            streamResponse.stream().forEach(chunk -> {
                System.out.println(chunk);
                String text = chunk.choices().get(0).delta().content().get();
                log.info("Received text: {}", text);
                if (text != null && !text.isEmpty()) {
                    // 发送文本内容到前端
                    ServerSentEvent<String> event = ServerSentEvent.<String>builder()
                            .data(text)
                            .build();
                    sink.tryEmitNext(event);
                }
            });
            System.out.println("No more chunks!");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
