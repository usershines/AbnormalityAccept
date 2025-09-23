package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.dto.request.AiChatRequest;
import com.abnormality.abnormalityaccept.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@CrossOrigin
@RequestMapping("aichat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public Result<String> chat(String message) {
        return Result.ok(aiChatService.chat(message));
    }

    @PostMapping("/chatUser")
    public Result<String> chatUser(@RequestBody AiChatRequest request) {
        return Result.ok(aiChatService.chat(request.getMessage(),request.getUserId()));
    }

    @PostMapping("/chatStream")
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody AiChatRequest request) {
        return aiChatService.chatStream(request.getMessage(),request.getUserId(), request.isContentOnly());
    }

}