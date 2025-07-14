package com.abnormality.abnormalityaccept.exception;

import com.abnormality.abnormalityaccept.enums.Code;

public class ServiceException extends BaseException {
    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(Code code, String msg) {
        super(code, msg);
    }

    public ServiceException(Code code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
