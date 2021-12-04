package com.green.bubuddies;

import java.util.Comparator;

public class Contact {
    String id;
    String name;
    String msg;
    String pic;
    Long msg_time;

    public Contact() {
    }

    public Contact(String id, String name, String msg, String pic, Long msg_time) {
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.pic = pic;
        this.msg_time = msg_time;
    }

    public static Comparator<Contact> contactsComparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact contact, Contact t1) {
            try{
            return (int)(t1.getMsg_time()-contact.getMsg_time());}
            catch (Exception e){
                return 0;
            }
        }
    };

    public Long getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(Long msg_time) {
        this.msg_time = msg_time;
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
