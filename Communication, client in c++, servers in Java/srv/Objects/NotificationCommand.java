package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.CommandEncoderDecoder;

public class NotificationCommand extends ReturnCommand {
    private int type; //PM-0 or Public-1
    private String postingUserName;
    private String content;

    public NotificationCommand(int opCode) {
        super(opCode);
    }

    public byte[] encode(CommandEncoderDecoder c) {
        return c.encode(this);
    }
    public int getType() {
        return type;
    }

    public String getPostingUserName() {
        return postingUserName;
    }

    public String getContent() {
        return content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPostingUserName(String postingUserName) {
        this.postingUserName = postingUserName;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
