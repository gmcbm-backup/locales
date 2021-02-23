package co.aikar.locales;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class MessageKey implements MessageKeyProvider {

    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final Map<String, MessageKey> keyMap = new ConcurrentHashMap<>();
    private final int id = counter.getAndIncrement();
    private final String key;

    private MessageKey(@Nonnull String key) {
        this.key = key;
    }

    public static MessageKey of(String key) {
        return keyMap.computeIfAbsent(key.toLowerCase().intern(), MessageKey::new);
    }

    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o);
    }

    public @Nonnull
    String getKey() {
        return key;
    }

    @Override
    public @Nonnull
    MessageKey getMessageKey() {
        return this;
    }
}
