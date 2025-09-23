package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.service.AiChatService;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    // 创建一个单线程的执行器用于处理流式响应
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Value("${dashscope.model}")
    private String model;

    @Value("${dashscope.api-key}")
    private String apiKey;
    @Override
    public String chat(String message) {
        return "";
    }

    @Override
    public String chat(String message, String userId) {
        return "";
    }

    @Override
    public Flux<ServerSentEvent<String>> chatStream(String message, String userId,boolean contentOnly) {
        // 使用 Sinks.Many 创建一个可发射多个元素的发布者
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

    private void callStreamWithMessage(String message, Sinks.Many<ServerSentEvent<String>> sink, boolean contentOnly)
            throws NoApiKeyException, ApiException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个善解人意的助手")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(message)
                .build();

        GenerationParam param = GenerationParam.builder()
                .apiKey(this.apiKey)
                .model(model)
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true) // 启用增量输出
                .build();

        // 调用流式API
        Flowable<GenerationResult> flowable = gen.streamCall(param);

        // 处理流式响应
        flowable.blockingForEach(result -> {
            // 从结果中提取文本内容
            String text = extractTextFromResult(result,contentOnly);
            log.info("Received text: {}", text);
            if (text != null && !text.isEmpty()) {
                // 发送文本内容到前端
                ServerSentEvent<String> event = ServerSentEvent.<String>builder()
                        .data(text)
                        .build();
                sink.tryEmitNext(event);
            }
        });
    }

    private String extractTextFromResult(GenerationResult result,boolean isContentOnly) {
        if (!isContentOnly){
            return JSONUtil.toJsonStr(result);
        }
        try {
            // 从结果中提取文本内容
            if (result != null && result.getOutput() != null &&
                    result.getOutput().getChoices() != null &&
                    !result.getOutput().getChoices().isEmpty()) {
                return result.getOutput().getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            // 处理可能的异常
            System.err.println("Error extracting text from result: " + e.getMessage());
        }
        return "";
    }

    public GenerationResult callWithMessage(String  message) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(message)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(this.apiKey)
                .model(model)
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }


    private GenerationParam buildGenerationParam(List<Message> msg,boolean isStream) {
        return GenerationParam.builder()
                .apiKey(this.apiKey)
                .model(model)
                .messages(msg)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(isStream)
                .build();
    }
}
