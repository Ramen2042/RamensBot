package com.ramen.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BotFrame extends JFrame {
    JTextArea consoleText = new JTextArea();
    JTextArea inputText = new JTextArea();
    JButton enterButton = new JButton("Entrer");
    volatile boolean isEntered = false;
    public BotFrame() {
        setTitle("Bot Controller");
        setBackground(Color.white);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        consoleText.setEditable(false);
        consoleText.setCaretColor(Color.MAGENTA);

        inputText.setPreferredSize(new Dimension(500, 23));
        inputText.setFont(new Font(inputText.getFont().getName(), inputText.getFont().getStyle(), inputText.getFont().getSize() + 5));
        inputText.setAutoscrolls(true);

        enterButton.setMnemonic(KeyEvent.VK_ENTER);
        enterButton.addActionListener(e -> isEntered = !inputText.getText().isBlank());

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputText);
        inputPanel.add(enterButton);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(consoleText, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void println(Object... objectsToPrint) {
        String textToPrint = Arrays.stream(objectsToPrint).map(Object::toString).collect(Collectors.joining());
        consoleText.append(textToPrint + "\n");
    }

    public String nextString() {
        while (!isEntered) Thread.onSpinWait();
        isEntered = false;
        String line = inputText.getText();
        inputText.setText(null);
        return line;
    }

    public static void main(String[] args) {
        new BotFrame();
    }
}
