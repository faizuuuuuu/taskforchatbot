package com.example.llamachatbot;

public class Message {
    public static final int USER = 0;
    public static final int BOT = 1;

    private String text;
    private int sender;

    public Message(String text, int sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public int getSender() {
        return sender;
    }
}
