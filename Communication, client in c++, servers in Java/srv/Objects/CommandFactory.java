package bgu.spl.net.srv.Objects;

import bgu.spl.net.srv.Command;

public class CommandFactory {
    public static ReceivedCommand makeReceivedCommand(int OpCode){
        switch (OpCode){
            case 1:
                return new RegisterCommand(1);
            case 2:
                return new LoginCommand(2);
            case 3:
                return new LogoutCommand(3);
            case 4:
                return new FollowCommand(4);
            case 5:
                return new PostCommand(5);
            case 6:
                return new PrivateMessageCommand(6);
            case 7:
                return new LogStatCommand(7);
            case 8:
                return new StatsCommand(8);
            case 12:
                return new BlockCommand(12);
            default:
                return null;
        }
    }

    public static ReturnCommand makeReturnCommand(int OpCode){
        switch (OpCode) {
            case 9:
                return new NotificationCommand(9);
            case 10:
                return new AckCommand(10);
            case 11:
                return new ErrorCommand(11);
            default:
                return null;
        }
    }
}
