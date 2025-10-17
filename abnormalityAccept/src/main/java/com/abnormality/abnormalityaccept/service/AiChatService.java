package com.abnormality.abnormalityaccept.service;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

public interface AiChatService {
    String chat(String message);
    String chat(String message, String userId);
    Flux<ServerSentEvent<String>> chatStream(String message, String userId,boolean contentOnly,boolean enableGraphRag);
}
