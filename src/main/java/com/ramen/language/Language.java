package com.ramen.language;

import java.io.Serializable;

public enum Language implements Serializable {
    English("english"), Francais("français");

    private String language;

    Language(String language) {
        this.language = language;

    }

    @Override
    public String toString() {
        return language;
    }

    public static Language ofString(String languageString) {
        for (Language language : values()) {
            if (language.language.equals(languageString)) return language;
        }
        throw new IllegalArgumentException("Le paramètre %s n'est pas inclus dans la liste des languages disponibles.".formatted(languageString));
    }
}
