package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

public abstract class ReturnCommand extends Command {
    private int destUserID = -1;

    public ReturnCommand(int opCode) {
        super(opCode);
    }

    abstract public byte[] encode(CommandEncoderDecoder c);

    public int getDestUserID() {
        return destUserID;
    }

    public void setDestUserID(int destUserID) {
        this.destUserID = destUserID;
    }
}
