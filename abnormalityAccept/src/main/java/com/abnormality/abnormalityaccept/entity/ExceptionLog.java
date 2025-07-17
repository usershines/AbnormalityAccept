package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 异常日志类，用于记录系统运行过程中发生的异常信息。
 * <p>
 * 该类主要用于存储和管理异常相关的详细信息，包括异常消息、请求URL、HTTP方法、用户代理、IP地址、用户名、请求参数以及堆栈跟踪信息。
 * </p>
 *
 *
 */
@Data
public class ExceptionLog {
    /**
     * 唯一标识符，用于唯一标识每条异常日志记录。
     */
    private String id;

    /**
     * 异常消息，表示异常发生时抛出的错误信息。
     */
    private String message;

    /**
     * 请求的URL，表示发生异常时的请求路径。
     */
    private String url;

    /**
     * HTTP方法，表示发生异常时的请求方法（如GET、POST等）。
     */
    private String method;

    private String machineId;

    /**
     * 用户代理，表示客户端浏览器或设备的User-Agent信息。
     */
    private String userAgent;

    /**
     * 客户端IP地址，表示发起请求的客户端IP。
     */
    private String ip;

    /**
     * 用户名，表示触发异常的操作用户。
     */
    private String username;

    /**
     * 请求参数，表示发生异常时的请求参数。
     */
    private String params;

    /**
     * 异常堆栈信息，表示异常的完整堆栈跟踪信息。
     */
    private String exceptionInfo;

    /**
     * 创建时间，表示异常日志记录的创建时间。
     * <p>该字段在插入数据库时自动填充。</p>
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 根据异常对象创建异常日志记录。
     *
     * @param e 异常对象，包含异常信息和堆栈跟踪。
     * @return 返回一个初始化好的ExceptionLog对象。
     */
    public static ExceptionLog fromException(Exception e) {
        ExceptionLog log = new ExceptionLog();
        log.setMessage(e.getMessage());
        log.setExceptionInfo(Arrays.toString(e.getStackTrace()));
        String machineId="";
        try{
            machineId= InetAddress.getLocalHost().getHostAddress();
        }catch (Exception ex){
            machineId="unknown";
        }
        log.setMachineId(machineId);
        return log;
    }
}