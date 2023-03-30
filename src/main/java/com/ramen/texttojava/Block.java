package com.ramen.texttojava;

import java.util.ArrayList;
import java.util.HashMap;

public class Block extends Instruction {
    private final ArrayList<Instruction> instructions;

    public Block(int scriptLineIndex) {
        super(scriptLineIndex);

        instructions = new ArrayList<>();
    }

    @Override
    public Object start(HashMap<String, Object> variables) {
        for (Instruction instruction : instructions) {
            Object result = instruction.start(variables);
            if (result instanceof ReturnedValue<?>) {
                return ((ReturnedValue<?>) result).getValue();
            }
        }

        return null;
    }
}
