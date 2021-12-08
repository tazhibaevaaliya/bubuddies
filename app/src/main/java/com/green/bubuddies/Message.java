package com.green.bubuddies;

// Class for storing all the messages going between two users

public class Message {
    String user;
    String msg;
    Long timestamp;

    public Message() {
    }

    public Message(String user, String msg, Long timestamp) {
        this.user = user;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
