package com.green.bubuddies;

public class Contact {
    String id;
    String name;
    String msg;
    String pic;

    public Contact() {
    }

    public Contact(String id, String name, String msg, String pic) {
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
