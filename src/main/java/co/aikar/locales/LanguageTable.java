package co.aikar.locales;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LanguageTable {

    private final Locale locale;
    private final Map<MessageKey, String> messages = new HashMap<>();

    LanguageTable(@Nonnull Locale locale) {
        this.locale = locale;
    }

    public @Nonnull
    String addMessage(@Nonnull MessageKey key, @Nonnull String message) {
        return Objects.requireNonNull(messages.put(key, message));
    }

    public @Nullable
    String getMessage(@Nonnull MessageKey key) {
        return messages.get(key);
    }

    public void addMessages(@Nonnull Map<MessageKey, String> messages) {
        this.messages.putAll(messages);
    }

    public @Nonnull
    Locale getLocale() {
        return locale;
    }

    public boolean addMessageBundle(@Nonnull String bundleName) {
        return this.addMessageBundle(Thread.currentThread().getContextClassLoader(), bundleName);
    }

    public boolean addMessageBundle(@Nonnull ClassLoader classLoader, @Nonnull String bundleName) {
        try {
            return this.addResourceBundle(ResourceBundle.getBundle(bundleName, this.locale,
                    classLoader, new UTF8Control()));
        } catch (MissingResourceException e) {
            return false;
        }
    }

    public boolean addResourceBundle(@Nonnull ResourceBundle bundle) {
        for (String key : bundle.keySet()) {
            addMessage(MessageKey.of(key), bundle.getString(key));
        }

        return !bundle.keySet().isEmpty();
    }
}
