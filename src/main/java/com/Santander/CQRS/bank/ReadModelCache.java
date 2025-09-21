package com.Santander.CQRS.bank;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReadModelCache {
    private final ConcurrentHashMap<Long, String> mem = new ConcurrentHashMap<>();

    public void put(Long accountId, String json) {
        mem.put(accountId, json);
    }

    public String get(Long accountId) {
        return mem.get(accountId);
    }
}
