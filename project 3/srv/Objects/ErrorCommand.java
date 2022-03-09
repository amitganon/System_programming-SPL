package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.CommandEncoderDecoder;

public class ErrorCommand extends ReturnCommand {

    private int msgOpCode;

    public ErrorCommand(int opCode) {
        super(opCode);
    }

    @Override
    public byte[] encode(CommandEncoderDecoder c) {
        return c.encode(this);
    }

    public int getMsgOpCode() {
        return msgOpCode;
    }

    public void setMsgOpCode(int msgOpCode) {
        this.msgOpCode = msgOpCode;
    }
}
