package com.abnormality.abnormalityaccept.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求体缓存过滤器，用于在后续处理（如 AOP 或异常处理器）中多次读取请求体内容。
 * <p>
 *     <strong>
 *         AOP鉴权需要多次读取请求体，但因为请求体由InputStream给出，从头到尾只能读取一次，所以用此类包装，以便多次读取
 *     </strong>
 * </p>
 * <p>该过滤器通过包装原始的 HttpServletRequest，将请求体缓存起来，
 * 避免因流被消费后无法再次读取的问题。</p>
 */
@Component
public class RequestBodyCachingFilter extends GenericFilterBean {


    /**
     * 过滤器的核心方法，负责包装请求并缓存请求体。
     *
     * <p>首先将传入的 ServletRequest 强制转换为 HttpServletRequest，
     * 然后使用 ContentCachingRequestWrapper 对其进行包装，从而缓存请求体内容。
     * 接着继续执行过滤器链，将包装后的请求传递给后续处理。</p>
     *
     * @param servletRequest  当前的 Servlet 请求对象
     * @param servletResponse 当前的 Servlet 响应对象
     * @param chain           当前的过滤器链
     * @throws IOException      如果发生 I/O 错误
     * @throws ServletException 如果处理请求时发生 ServletException
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        // 将请求对象转换为 HttpServletRequest
        HttpServletRequest currentRequest = (HttpServletRequest) servletRequest;

        // 使用 ContentCachingRequestWrapper 包装当前请求，实现请求体的缓存
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(currentRequest);

        // 继续执行过滤器链，传递包装后的请求和原始响应对象
        chain.doFilter(wrappedRequest, servletResponse);
    }
}
