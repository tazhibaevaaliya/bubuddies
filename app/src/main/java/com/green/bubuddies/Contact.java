package com.green.bubuddies;

import java.util.Comparator;
// Class for storing users' information for rendering the contacts page
public class Contact {
    String id;
    String name;
    String msg;
    String pic;
    int unread_count;
    Long msg_time;

    public Contact() {
    }

    public Contact(String id, String name, String msg, String pic, Long msg_time, int unread_count) {
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.pic = pic;
        this.msg_time = msg_time;
        this.unread_count = unread_count;
    }
    // Implemented for sort the user's depending on their latest msg's timestamp
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

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

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
