package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;
import bgu.spl.net.srv.CommandEncoderDecoder;

public class LoginCommand extends ReceivedCommand {
    private String password;
    private int Captcha;

    public LoginCommand(int opCode) {
        super(opCode);
    }

    public void decodeNextByte(byte nextByte, CommandEncoderDecoder c) {
        c.decodeNextByte(nextByte,this);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCaptcha() {
        return Captcha;
    }

    public void setCaptcha(int captcha) {
        Captcha = captcha;
    }
}
