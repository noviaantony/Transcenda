package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryDeduplicationService implements DeduplicationService {

    private final Map<String, Boolean> processedMessageIds = new ConcurrentHashMap<>();

    @Override
    public boolean isMessageProcessed(String messageId) {
        Boolean previouslyProcessed = processedMessageIds.putIfAbsent(messageId, true);
        return previouslyProcessed != null;
    }
}
