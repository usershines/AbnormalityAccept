package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.dto.response.AiChatStreamResponse;
import com.abnormality.abnormalityaccept.dto.response.kg.KgData;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.AiChatService;
import com.abnormality.abnormalityaccept.util.N4jClient;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class AiChatServiceImplLocal implements AiChatService {


    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Value("${rag-flow.model}")
    private String model;

    @Value("${dashscope.api-key}")
    private String dashScopeApiKey;
    @Value("${dashscope.model}")
    private String dashScopeModel;
    @Value("${dashscope.endpoint}")
    private String dashScopeEndpoint;

    @Value("${rag-flow.api-key}")
    private String apiKey;

    @Value("${rag-flow.endpoint}")
    private String endpoint;

    @Value("${neo4j.uri}")
    private String neo4jUri;
    @Value("${neo4j.user}")
    private String neo4jUser;
    @Value("${neo4j.password}")
    private String neo4jPassword;

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
    public Flux<ServerSentEvent<String>> chatStream(String message, String userId, boolean contentOnly,boolean enableGraphRag) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        if(enableGraphRag){
            executor.submit(() -> {
                try {
                    callStreamGraphRag(message, sink);
                } catch (Exception e) {
                    sink.tryEmitError(e);
                } finally {
                    sink.tryEmitComplete();
                }
            });
        }else{
            executor.submit(() -> {
                try {
                    callStreamRagFlow(message, sink,contentOnly);
                } catch (Exception e) {
                    sink.tryEmitError(e);
                } finally {
                    sink.tryEmitComplete();
                }
            });
        }
        // 在单独的线程中执行流式调用


        return sink.asFlux();
    }

    private void callStreamRagFlow(String message, Sinks.Many<ServerSentEvent<String>> sink, boolean contentOnly){
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
                String text = chunk.choices().get(0).delta().content().isPresent() ? chunk.choices().get(0).delta().content().get() : "";
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

    public void callStreamGraphRag(String message, Sinks.Many<ServerSentEvent<String>> sink){
        OpenAIClient aliClient = OpenAIOkHttpClient.builder()
                .apiKey(dashScopeApiKey)
                .baseUrl(dashScopeEndpoint)
                .build();
        String extractSystemPrompt="你是一个精通LLM的助手，青葱用户输入中尽可能多地抽取出实体和名词，并输出。只输出抽取的的结果，实体之间使用英文逗号隔开,不要输出其余额外内容";

        String e1xtractResult=callAliModel(extractSystemPrompt, message);
        List<String> entityArr = List.of(e1xtractResult.split(","));
        KgData kgData = new KgData();
        N4jClient n4jClient = new N4jClient(neo4jUri, neo4jUser, neo4jPassword);
        String relationStr = n4jClient.getAllRelations(entityArr,kgData);
        ServerSentEvent<String> event = ServerSentEvent.<String>builder()
                .data(JSONUtil.toJsonStr(AiChatStreamResponse.newGraph(kgData)))
                .build();
        sink.tryEmitNext(event);
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(dashScopeModel)
                .addSystemMessage("你是一个RAG助手，请根据以下信息，回答用户的问题："+relationStr)
                .addUserMessage(message)
                .build();
        StreamResponse<ChatCompletionChunk> streamResponse=null;
        try  {
            streamResponse = aliClient.chat().completions().createStreaming(params);
            streamResponse.stream().forEach(chunk -> {
                System.out.println(chunk);
                String text = chunk.choices().get(0).delta().content().isPresent() ? chunk.choices().get(0).delta().content().get() : "";
                log.info("Received text: {}", text);
                if (text != null && !text.isEmpty()) {
                    // 发送文本内容到前端
                    ServerSentEvent<String> nevent = ServerSentEvent.<String>builder()
                            .data(JSONUtil.toJsonStr(AiChatStreamResponse.newText(text)))
                            .build();
                    sink.tryEmitNext(nevent);
                }
            });
            System.out.println("No more chunks!");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public String callAliModel(String systemMsg,String userMsg){
        Generation gen = new Generation();
        Message systemM = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemMsg)
                .build();
        Message userM = Message.builder()
                .role(Role.USER.getValue())
                .content(userMsg)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(this.dashScopeApiKey)
                .model(dashScopeModel)
                .messages(Arrays.asList(systemM, userM))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        try{
            GenerationResult result = gen.call(param);
            if (result != null&&result.getOutput().getChoices().get(0).getMessage().getContent()!= null) {
                System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
                return result.getOutput().getChoices().get(0).getMessage().getContent();
            }else{
                throw new ServiceException("调用模型失败");
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }

    }
}
