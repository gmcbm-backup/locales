package co.aikar.locales;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class LocaleManager<T> {

    private final Function<T, Locale> localeMapper;
    private final Map<Locale, LanguageTable> tables = new HashMap<>();
    @Getter
    private Locale defaultLocale;

    LocaleManager(Function<T, Locale> localeMapper, Locale defaultLocale) {
        this.localeMapper = localeMapper;
        this.defaultLocale = defaultLocale;
    }

    /**
     * @param localeMapper Mapper to map a context to Locale
     * @param <T>          Context Class Type
     */
    public static <T> LocaleManager<T> create(Function<T, Locale> localeMapper) {
        return new LocaleManager<>(localeMapper, Locale.ENGLISH);
    }

    /**
     * @param localeMapper  Mapper to map a context to Locale
     * @param defaultLocale Default Locale
     * @param <T>           Context Class Type
     */
    public static <T> LocaleManager<T> create(Function<T, Locale> localeMapper,
                                              Locale defaultLocale) {
        return new LocaleManager<>(localeMapper, defaultLocale);
    }

    public Locale setDefaultLocale(Locale defaultLocale) {
        Locale previous = this.defaultLocale;
        this.defaultLocale = defaultLocale;
        return previous;
    }

    /**
     * If a list of locales is supplied, loads the matching message bundle for each locale.
     * If none are supplied, just the default locale is loaded.
     */
    public boolean addMessageBundle(String bundleName, Locale... locales) {
        return this.addMessageBundle(Thread.currentThread().getContextClassLoader(), bundleName, locales);
    }

    public boolean addMessageBundle(ClassLoader classLoader, String bundleName,
                                    Locale... locales) {
        if (locales.length == 0) {
            locales = new Locale[]{defaultLocale};
        }

        boolean found = false;
        for (Locale locale : locales) {
            if (getTable(locale).addMessageBundle(classLoader, bundleName)) {
                found = true;
            }
        }
        return found;
    }

    public void addMessages(Locale locale, Map<MessageKey, String> messages) {
        getTable(locale).addMessages(messages);
    }

    public String addMessage(Locale locale, MessageKey key, String message) {
        return getTable(locale).addMessage(key, message);
    }

    public @Nullable
    String getMessage(T context, MessageKey key) {
        Locale locale = localeMapper.apply(context);

        String message = getTable(locale).getMessage(key);
        if (message == null && !locale.getCountry().isEmpty()) {
            message = getTable(new Locale(locale.getLanguage())).getMessage(key);
        }

        if (message == null && !Objects.equals(locale, defaultLocale)) {
            message = getTable(defaultLocale).getMessage(key);
        }

        return message;
    }

    public LanguageTable getTable(Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

    public boolean addResourceBundle(ResourceBundle bundle, Locale locale) {
        return this.getTable(locale).addResourceBundle(bundle);
    }
}
