package com.abnormality.abnormalityaccept.exception;

import com.abnormality.abnormalityaccept.enums.Code;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础异常类，用于封装统一的业务异常信息，包含异常码、消息内容和消息列表。
 *
 * <p>该类继承自 RuntimeException，支持通过枚举 Code 定义错误码，并支持携带多条错误信息。</p>
 */
@Getter
public class BaseException extends RuntimeException {

    /**
     * 错误码，用于标识异常类型，如 SUCCESS、ERROR、NOT_FOUND 等。
     */
    private Code code;

    /**
     * 异常消息，用于描述当前异常的具体信息。
     */
    private String msg;

    /**
     * 异常消息列表，用于存储多个错误信息，支持链式异常信息的叠加。
     */
    private List<String> msgList = new ArrayList<>();

    /**
     * 构造方法，创建一个带有错误码和消息的 BaseException 实例。
     *
     * <p>构造逻辑如下：</p>
     * <ul>
     *   <li>调用父类构造方法设置异常消息。</li>
     *   <li>设置错误码和消息字符串。</li>
     *   <li>将当前消息添加到 msgList 中。</li>
     * </ul>
     *
     * @param code 错误码，用于标识异常类型
     * @param msg  异常消息，用于描述异常信息
     */
    public BaseException(Code code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.msgList.add(msg);
    }

    /**
     * 构造方法，创建一个仅携带消息的 BaseException 实例，默认错误码为 ERROR。
     *
     * <p>构造逻辑如下：</p>
     * <ul>
     *   <li>调用父类构造方法设置异常消息。</li>
     *   <li>设置默认错误码为 Code.ERROR。</li>
     *   <li>将当前消息添加到 msgList 中。</li>
     * </ul>
     *
     * @param msg 异常消息，用于描述异常信息
     */
    public BaseException(String msg) {
        super(msg);
        this.code = Code.ERROR;
        this.msg = msg;
        this.msgList.add(msg);
    }

    /**
     * 构造方法，创建一个带有错误码、消息和异常原因的 BaseException 实例。
     *
     * <p>构造逻辑如下：</p>
     * <ul>
     *   <li>调用父类构造方法设置异常消息和原因。</li>
     *   <li>设置错误码和消息字符串。</li>
     *   <li>如果异常原因为 BaseException 类型，则将其 msgList 合并到当前 msgList。</li>
     *   <li>将当前消息添加到 msgList 中。</li>
     * </ul>
     *
     * @param code 错误码，用于标识异常类型
     * @param msg  异常消息，用于描述当前异常信息
     * @param cause 异常原因，用于链式异常信息传递
     */
    public BaseException(Code code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
        if (cause != null) {
            if (cause instanceof BaseException e) {
                // 如果原因为 BaseException 类型，合并其 msgList
                this.msgList.addAll(e.getMsgList());
            }
            // 添加当前异常消息到列表
            this.msgList.add(msg);
        }
    }
}
