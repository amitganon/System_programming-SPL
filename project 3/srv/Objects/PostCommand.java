package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

import java.util.List;
import java.util.Vector;

public class PostCommand extends ReceivedCommand {
    private String content;
    private String sendingDate;

    public PostCommand(int opCode) {
        super(opCode);
    }
    public void decodeNextByte(byte nextByte, CommandEncoderDecoder c) {
        c.decodeNextByte(nextByte,this);
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(String sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Vector<String> getMentionedUsers(){
        Vector<String> result = new Vector<>();
        String [] split = content.split("@");
        for (int i = 1; i < split.length; i++) {
            String str = split[i];
            result.add(str.substring(0,str.indexOf(' ')));
        }
        return result;
    }
}
