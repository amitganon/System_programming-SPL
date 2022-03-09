package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

public class FollowCommand extends ReceivedCommand {
    private String followName;
    private boolean isUnFollow;
    public FollowCommand(int opCode) {
        super(opCode);
    }

    public void decodeNextByte(byte nextByte, CommandEncoderDecoder c) {
        c.decodeNextByte(nextByte,this);
    }

    public boolean isUnFollow() {
        return isUnFollow;
    }

    public void setUnFollow(byte unFollow) {
        isUnFollow = unFollow==1;
    }

    public String getFollowName() {
        return followName;
    }

    public void setFollowName(String followName) {
        this.followName = followName;
    }
}
