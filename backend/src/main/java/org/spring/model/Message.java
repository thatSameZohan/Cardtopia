package org.spring.model;

public class Message {
    private String room;
    private String sender;
    private String text;

    public Message() {
    }

    public Message(String room, String sender, String text) {
        this.room = room;
        this.sender = sender;
        this.text = text;
    }

    public String getRoom() { return room; }
    public String getSender() { return sender; }
    public String getText() { return text; }

    public void setRoom(String room) { this.room = room; }
    public void setSender(String sender) { this.sender = sender; }
    public void setText(String text) { this.text = text; }
}