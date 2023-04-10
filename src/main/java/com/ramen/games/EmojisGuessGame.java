package com.ramen.games;

import java.util.HashMap;
import java.util.Map;

public class EmojisGuessGame {
    public static final Map<String, String> charactersMap;

    static {
        charactersMap = Map.ofEntries(Map.entry(":zap::eyeglasses::magic_wand: ?", "Harry Potter"), Map.entry("", ""));
    }
}
