package com.ramen.texttojava;

import java.util.HashMap;

public class Instruction {
    private Keyword instructionKeyword = Keyword.NULL;
    private String stringValue = "";
    private Object value1;
    private Object value2;
    private final int scriptLineIndex;
    private Instruction inStruction;

    public Instruction(int scriptLineIndex) {
        this.scriptLineIndex = scriptLineIndex;
    }

    public Object start(HashMap<String, Object> variables) {
        switch (instructionKeyword) {
            case NULL -> {
                return null;
            }

            case AFFICHER -> {
                StringBuilder result = new StringBuilder(stringValue);
                System.out.println(result);
                return null;
            }
            case AFFICHERCHAINE -> {
                System.out.println(stringValue);
                return null;
            }
            case AFFICHERVAR -> {
                System.out.println(variables.get(stringValue));
                return null;
            }
            case METTREA -> {
                variables.put(stringValue, value1);
            }
            case BOUCLE, BOUCLEINFINIE -> {
                while(true) {
                    inStruction.start(variables);
                }
            }
            case BOUCLEFOR -> {
            }
            case BOUCLEWHILE -> {
            }
            case ATTENDRE -> {
            }
            default -> throw new IllegalArgumentException("Syntaxe erronée de l'instruction à la ligne " + scriptLineIndex);
        }
        return null;
    }

    public Keyword getInstructionKeyword() {
        return instructionKeyword;
    }

    public void setInstructionKeyword(Keyword instructionKeyword) {
        this.instructionKeyword = instructionKeyword;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setValue1(Object value1) {
        this.value1 = value1;
    }

    public Object getValue1() {
        return value1;
    }

    public Instruction getInStruction() {
        return inStruction;
    }

    public void setInStruction(Instruction inStruction) {
        this.inStruction = inStruction;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public enum Keyword {
        AFFICHER(Type.CONSUMER), AFFICHERCHAINE(Type.CONSUMER), AFFICHERVAR(Type.CONSUMER),
        ATTENDRE(Type.CONSUMER), BOUCLE(Type.BOUCLE), BOUCLEFOR(Type.BOUCLE),
        BOUCLEINFINIE(Type.BOUCLE), BOUCLEWHILE(Type.BOUCLE), METTREA(Type.CONSUMER),
        RETURN(Type.OPERATION), NULL(Type.CONSUMER);

        private final Type type;

        Keyword(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            BOUCLE, CONDITION, CONSUMER, OPERATION
        }
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "instructionKeyword=" + instructionKeyword +
                ",\n stringValue='" + stringValue + '\'' +
                ",\n value1=" + value1 +
                ",\n value2=" + value2 +
                ",\n scriptLineIndex=" + scriptLineIndex +
                ",\n inStruction=" + inStruction +
                '}';
    }
}
