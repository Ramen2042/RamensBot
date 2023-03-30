package com.ramen.texttojava;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        File scriptsDirectory = new File("scripts");
        HashMap<String, Object> variables = new HashMap<>();
        ArrayList<Script> scripts = Arrays.stream(Objects.requireNonNull(scriptsDirectory.listFiles((dir, name) -> {
            String[] splited = name.split("\\.");

            if (splited[splited.length - 1].equals("jvtxt")) {
                System.out.printf("Script trouvé. Nom = \"%s\", Chemin : \"%s\"\n", name, dir.getAbsolutePath());
                return true;
            } else return false;
        }))).map(file -> new Script(file, variables)).collect(Collectors.toCollection(ArrayList::new));

        System.out.println(scripts);

        System.out.println("\n----------------------------------------------\n");

        scripts.forEach(script -> new Thread(script).start());
    }
}