package co.aikar.locales;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class LocaleManager<T> {

    private final Function<T, Locale> localeMapper;
    private final Map<Locale, LanguageTable> tables = new HashMap<>();
    private Locale defaultLocale;

    LocaleManager(@Nonnull Function<T, Locale> localeMapper, @Nonnull Locale defaultLocale) {
        this.localeMapper = localeMapper;
        this.defaultLocale = defaultLocale;
    }

    /**
     * @param localeMapper Mapper to map a context to Locale
     * @param <T>          Context Class Type
     */
    public static <T> LocaleManager<T> create(@Nonnull Function<T, Locale> localeMapper) {
        return new LocaleManager<>(localeMapper, Locale.ENGLISH);
    }

    /**
     * @param localeMapper  Mapper to map a context to Locale
     * @param defaultLocale Default Locale
     * @param <T>           Context Class Type
     */
    public static <T> LocaleManager<T> create(@Nonnull Function<T, Locale> localeMapper,
                                              @Nonnull Locale defaultLocale) {
        return new LocaleManager<>(localeMapper, defaultLocale);
    }

    public @Nonnull
    Locale getDefaultLocale() {
        return defaultLocale;
    }

    public @Nonnull
    Locale setDefaultLocale(@Nonnull Locale defaultLocale) {
        Locale previous = this.defaultLocale;
        this.defaultLocale = defaultLocale;
        return previous;
    }

    /**
     * If a list of locales is supplied, loads the matching message bundle for each locale.
     * If none are supplied, just the default locale is loaded.
     */
    public boolean addMessageBundle(@Nonnull String bundleName, @Nonnull Locale... locales) {
        return this.addMessageBundle(Thread.currentThread().getContextClassLoader(), bundleName, locales);
    }

    public boolean addMessageBundle(@Nonnull ClassLoader classLoader, @Nonnull String bundleName,
                                    @Nonnull Locale... locales) {
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

    public void addMessages(@Nonnull Locale locale, @Nonnull Map<MessageKey, String> messages) {
        getTable(locale).addMessages(messages);
    }

    public @Nonnull
    String addMessage(@Nonnull Locale locale, @Nonnull MessageKey key, @Nonnull String message) {
        return getTable(locale).addMessage(key, message);
    }

    public @Nullable
    String getMessage(@Nonnull T context, @Nonnull MessageKey key) {
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

    public @Nonnull
    LanguageTable getTable(@Nonnull Locale locale) {
        return tables.computeIfAbsent(locale, LanguageTable::new);
    }

    public boolean addResourceBundle(@Nonnull ResourceBundle bundle, @Nonnull Locale locale) {
        return this.getTable(locale).addResourceBundle(bundle);
    }
}
