package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

import java.util.List;

public class StatsCommand extends ReceivedCommand {

    private List<String> userNameList;

    public StatsCommand(int opCode) {
        super(opCode);
    }

    public void decodeNextByte(byte nextByte, CommandEncoderDecoder c) {
        c.decodeNextByte(nextByte,this);
    }

    public List<String> getUserNameList() {
        return userNameList;
    }

    public void setUserNameList(List<String> userNameList) {
        this.userNameList = userNameList;
    }
}
