package com.abnormality.abnormalityaccept.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-15
 */
public class CaptchaException extends AuthenticationException {
    public CaptchaException(String msg) {
        super(msg);
    }
}