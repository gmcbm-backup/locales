package co.aikar.locales;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.*;

public class LanguageTable {

    @Getter
    private final Locale locale;
    private final Map<MessageKey, String> messages = new HashMap<>();

    LanguageTable(Locale locale) {
        this.locale = locale;
    }

    public String addMessage(MessageKey key, String message) {
        return messages.put(key, message);
    }

    public @Nullable
    String getMessage(MessageKey key) {
        return messages.get(key);
    }

    public void addMessages(Map<MessageKey, String> messages) {
        this.messages.putAll(messages);
    }

    public boolean addMessageBundle(String bundleName) {
        return this.addMessageBundle(Thread.currentThread().getContextClassLoader(), bundleName);
    }

    public boolean addMessageBundle(ClassLoader classLoader, String bundleName) {
        try {
            return this.addResourceBundle(ResourceBundle.getBundle(bundleName, this.locale,
                    classLoader, new UTF8Control()));
        } catch (MissingResourceException e) {
            return false;
        }
    }

    public boolean addResourceBundle(ResourceBundle bundle) {
        for (String key : bundle.keySet()) {
            addMessage(MessageKey.of(key), bundle.getString(key));
        }

        return !bundle.keySet().isEmpty();
    }
}
