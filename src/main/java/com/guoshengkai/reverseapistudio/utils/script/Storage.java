package com.guoshengkai.reverseapistudio.utils.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiatian
 */
public class Storage implements AutoCloseable {

    private final Map<String, StorageEntry> data = new HashMap<>();
    private volatile boolean running = true;
    private final Thread cleanerThread;
    private final String key;

    public Storage(String key) {
        this.key = key;
        System.out.println("Storage started: data-cleaner:" + this.key);
        cleanerThread = Thread.ofVirtual().name("data-cleaner:" + this.key).start(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                    synchronized (this) {
                        long currentTime = System.currentTimeMillis();
                        data.entrySet().removeIf(
                                entry -> {
                                    if (entry.getValue().expireTime > 0 && currentTime > entry.getValue().expireTime) {
                                        System.out.println("Storage entry expired: " + entry.getKey());
                                        return true; // Remove expired entry
                                    }
                                    return false; // Keep valid entry
                                }
                        );
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    public synchronized Object get(String key) {
        StorageEntry entry = data.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.expireTime > 0 && System.currentTimeMillis() > entry.expireTime) {
            data.remove(key);
            return null;
        }
        return entry.value;
    }

    public synchronized void set(String key, Object value, long expire) {
        data.put(key, new StorageEntry(value, expire > 0 ? (System.currentTimeMillis() + expire): expire));
    }

    public synchronized void remove(String key) {
        data.remove(key);
    }

    public synchronized boolean containsKey(String key) {
        StorageEntry entry = data.get(key);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() > entry.expireTime) {
            data.remove(key);
            return false;
        }
        return true;
    }

    public synchronized void clear() {
        data.clear();
    }

    @Override
    public void close() {
        System.out.println("Storage closed:" + cleanerThread.getName());
        running = false;
        clear();
    }

    private static class StorageEntry {
        Object value;
        long expireTime;
        StorageEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
    }
}
