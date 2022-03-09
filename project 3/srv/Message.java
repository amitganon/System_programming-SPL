package bgu.spl.net.srv;

public class Message {
    final private int type; //PM-0 or Public-1
    final private String content;
    final private String senderName;
    final private String sendingDate;

    public Message(int type, String content, String senderName, String sendingDate){
        this.type=type;
        this.content=content;
        this.senderName = senderName;
        this.sendingDate=sendingDate;
    }

    public String getSendingDate() {
        return sendingDate;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }
}
