package com.ramen.texttojava;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Script implements Runnable {
    private File scriptFile;
    private final String name;
    private List<String> scriptLines;
    private HashMap<String, Object> variables;
    private final ArrayList<Instruction> instructions = new ArrayList<>();

    public Script(File scriptFile, HashMap<String, Object> variables) {
        this.scriptFile = scriptFile;
        this.variables = variables;

        {
            String[] name = scriptFile.getName().split("\\.");
            StringBuilder string = new StringBuilder();
            Arrays.stream(Arrays.copyOfRange(name, 0, name.length - 1)).forEach(string::append);
            this.name = string.toString();
        }

        try {
            scriptLines = Files.readAllLines(scriptFile.toPath());
            int lineIndex = 0;
            for (String scriptLine : scriptLines) {
                lineIndex++;
                StringBuilder wordStringBuilder = new StringBuilder();
                Instruction instruction = new Instruction(lineIndex);
                int charIndex = 0;
                int wordIndex = 0;
                int blocksNumber = 0;

                for (char scriptLineChar : scriptLine.toCharArray()) {
                    charIndex++;
                    if (scriptLineChar == ' ' || charIndex == scriptLine.length()) {
                        try {
                            if (scriptLine.charAt(charIndex - 2) != ' ') wordIndex++;
                        } catch (IndexOutOfBoundsException ignored) {
                        }

                        if (instruction.getInstructionKeyword() == Instruction.Keyword.AFFICHER) {
                            //instruction.setStringValue((instruction.getStringValue().charAt(instruction.getStringValue().length() - 1) == '\"' ? instruction.getStringValue() : instruction.getStringValue() + " ") + wordStringBuilder);
                            try {
                                instruction.setStringValue((
                                        instruction.getStringValue().charAt(instruction.getStringValue().length() - 1) == '\"'
                                                && instruction.getStringValue().length() == 1
                                                ? "" : instruction.getStringValue() + " ") + wordStringBuilder);
                            } catch (StringIndexOutOfBoundsException e) {
                                instruction.setStringValue(" " + wordStringBuilder);
                            }
                        } else if (instruction.getInstructionKeyword() == Instruction.Keyword.METTREA) {

                        } else switch (wordStringBuilder.toString().toLowerCase()) {
                            case "afficher" -> {
                                getBlock(instruction, lineIndex, blocksNumber).setInstructionKeyword(Instruction.Keyword.AFFICHER);
                                getBlock(instruction, lineIndex, blocksNumber).setStringValue("\"");
                            }
                            case "mettre" -> instruction.setInstructionKeyword(Instruction.Keyword.METTREA);
                            case "repeter" -> {
                                instruction.setInstructionKeyword(Instruction.Keyword.BOUCLE);
                                blocksNumber++;
                            }
                            case "indefiniment" -> {
                                if (instruction.getInstructionKeyword() == Instruction.Keyword.BOUCLE)
                                    getBlock(instruction, lineIndex, blocksNumber - 1).setInstructionKeyword(Instruction.Keyword.BOUCLEINFINIE);
                                else throw new IllegalArgumentException("Syntaxe incorrecte, %d%s ligne, %d%s mot".formatted(lineIndex, lineIndex == 1 ? "ère" : "ème", wordIndex, wordIndex == 1 ? "er" : "ème"));
                            }
                            case "attendre" -> instruction.setInstructionKeyword(Instruction.Keyword.ATTENDRE);
                            default -> throw new IllegalArgumentException("Mot clé inconnu, %d%s ligne, %d%s mot".formatted(lineIndex, lineIndex == 1 ? "ère" : "ème", wordIndex, wordIndex == 1 ? "er" : "ème"));
                        }

                        wordStringBuilder = new StringBuilder();

                        if (charIndex == scriptLine.length()) {
                            instruction.setStringValue(instruction.getStringValue() + scriptLineChar);

                            instructions.add(instruction);
                            break;
                        }
                    } else if (instruction.getInstructionKeyword() == Instruction.Keyword.AFFICHER && scriptLineChar == '\"') {
                        if (instruction.getStringValue().charAt(instruction.getStringValue().length() - 1) == '\"') ;
                    } else wordStringBuilder.append(scriptLineChar);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        instructions.forEach(instruction -> instruction.start(variables));
    }

    public static Instruction getBlock(Instruction instruction, int lineIndex, int blocksNumber) {
        if (blocksNumber == 0) return instruction;

        for (int i = 0; i < blocksNumber - 1; i++) {
            try {
                instruction = instruction.getInStruction();
            } catch (NullPointerException e) {
                instruction.setInStruction(new Instruction(lineIndex));
            }
        }

        return instruction;
    }

    @Override
    public String toString() {
        return "Script{" +
                "scriptFile=" + scriptFile +
                ",\n name='" + name + '\'' +
                ",\n scriptLines=" + scriptLines +
                ",\n variables=" + variables +
                ",\n instructions=" + instructions +
                '}';
    }
}
