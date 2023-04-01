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
        //consoleText.append("FEZGHTHHRYHSRTHSHSTRHSTHSRTHSTHFEZGHTHHRYHSRTHSHSTRHSTHSRTHSTHFEZGHTHHRYHSRTHSHSTRHSTHSRTHSTHFEZGHTHHRYHSRTHSHSTRHSTHSRTHSTHFEZGHTHHRYHSRTHSHSTRHSTHSRTHSTHFEZGHTHHRYHSRTHSHSTRHSTHSRTHSTH\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nGTHRSTHTRHSHSHT");
        consoleText.setLineWrap(true);
        JScrollPane consoleScroll = new JScrollPane(consoleText);

        inputText.setPreferredSize(new Dimension(600, 50));
        inputText.setFont(new Font(inputText.getFont().getName(), inputText.getFont().getStyle(), inputText.getFont().getSize() + 5));
        inputText.setToolTipText("Entre ton message ici");
        JScrollPane inputScroll = new JScrollPane(inputText);
        inputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        inputScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        enterButton.addActionListener(e -> isEntered = !inputText.getText().isBlank());
        enterButton.setMnemonic(KeyEvent.VK_ENTER);
        enterButton.setEnabled(false);

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputScroll);
        inputPanel.add(enterButton);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(consoleScroll, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void println(Object... objectsToPrint) {
        String textToPrint = Arrays.stream(objectsToPrint).map(Object::toString).collect(Collectors.joining());
        consoleText.append(textToPrint + "\n");
    }

    public String nextString() {
        enterButton.setEnabled(true);
        while (!isEntered) Thread.onSpinWait();
        isEntered = false;
        String line = inputText.getText();
        inputText.setText(null);
        enterButton.setEnabled(false);
        return line;
    }

    public static void main(String[] args) {
        new BotFrame();
    }
}
