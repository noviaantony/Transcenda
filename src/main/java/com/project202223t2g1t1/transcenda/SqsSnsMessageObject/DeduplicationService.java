package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;

public interface DeduplicationService {
    boolean isMessageProcessed(String messageId);
}
