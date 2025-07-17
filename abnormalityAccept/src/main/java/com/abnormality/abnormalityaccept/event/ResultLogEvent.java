package com.abnormality.abnormalityaccept.event;

import org.springframework.context.ApplicationEvent;

public class ResultLogEvent extends ApplicationEvent {
    public ResultLogEvent(Object source) {
        super(source);
    }
}
