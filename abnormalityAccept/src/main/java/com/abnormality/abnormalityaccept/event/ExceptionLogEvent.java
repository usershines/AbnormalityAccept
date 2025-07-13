package com.abnormality.abnormalityaccept.event;

import org.springframework.context.ApplicationEvent;

public class ExceptionLogEvent extends ApplicationEvent {
    public ExceptionLogEvent(Object source) {
        super(source);
    }
}
