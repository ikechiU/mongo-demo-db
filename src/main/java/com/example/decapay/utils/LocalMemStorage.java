package com.example.decapay.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMemStorage {

    private final MemcachedClient memcachedClient;

    public void save(String key,String value, int expiryInSeconds) {
        try {
            memcachedClient.set(key, expiryInSeconds, value);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
        }
    }

    public void saveToken(String key, String value) {
        try {
            log.info("key is {}", key);
            if (keyExist(key) != null) {
                log.info("key is cleared");
                clear(key);
            }
            memcachedClient.set(key, 0, value);
            log.info("key is set with value {}", value);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
        }
    }

    public String getValueByKey(String key) {
        try {
            return memcachedClient.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean keyExist(String key) {
        try {
            return memcachedClient.get(key) != null;
        } catch (Exception e) {
            return null;
        }
    }

    public void clear(String key) {
        try {
            memcachedClient.delete(key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.warn("Key is not available {}", key);
            e.printStackTrace();
        }
    }
}
