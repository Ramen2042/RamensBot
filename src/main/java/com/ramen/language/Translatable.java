package com.ramen.language;

import java.util.HashMap;

public class Translatable {
    private final HashMap<Language, String> translations = new HashMap<>();

    public Translatable() {
    }

    public String toString(Language lang) {
        return translations.get(lang);
    }

    @Override
    public String toString() {
        return String.format("%s : %s | %s : %s", Language.Francais, toString(Language.Francais), Language.English, toString(Language.English));
    }

    public Translatable add(Language lang, String translation) {
        translations.put(lang, translation);
        return this;
    }

    public static Translatable of(String francais, String english) {
        return new Translatable().add(Language.Francais, francais).add(Language.English, english);
    }
}
