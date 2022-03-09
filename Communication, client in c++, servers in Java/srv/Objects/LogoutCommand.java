package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

public class LogoutCommand extends ReceivedCommand {

    public LogoutCommand(int opCode) {
        super(opCode);
    }
    public void decodeNextByte(byte nextByte, CommandEncoderDecoder c) {
        c.decodeNextByte(nextByte,this);
    }
}
