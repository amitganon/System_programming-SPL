package bgu.spl.net.srv;

public abstract class Command {
    private int opCode;

    public Command(int opCode) {
        this.opCode = opCode;
    }

    public int getOpCode() {
        return opCode;
    }
}
