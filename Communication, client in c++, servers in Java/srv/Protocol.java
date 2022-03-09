package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Objects.*;

import java.rmi.registry.RegistryHandler;
import java.util.Vector;

public class Protocol implements BidiMessagingProtocol<ReceivedCommand> {
    private Twitter twit;
    private Connections connections;
    private boolean shouldTerminate;
    private int connectionId;

    public Protocol(Twitter twit){
        this.twit=twit;
        shouldTerminate = false;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connections = connections;
        this.connectionId=connectionId;
    }

    @Override
    public void process(ReceivedCommand message) {
        ReceivedCommand com = message;
        com.setSenderId(connectionId);
        Vector<ReturnCommand> processedMessage = new Vector<>();
        switch (com.getOpCode()){
            case 1:
                processedMessage = twit.Register(((RegisterCommand)com));
                break;
            case 2:
                processedMessage = twit.Login(((LoginCommand)com));
                break;
            case 3:
                processedMessage = twit.Logout(((LogoutCommand)com));
                break;
            case 4:
                if(!((FollowCommand)message).isUnFollow())
                    processedMessage = twit.Follow(((FollowCommand)com));
                else
                    processedMessage = twit.UnFollow(((FollowCommand)com));
                break;
            case 5:
                processedMessage = twit.Post(((PostCommand)com));
                break;
            case 6:

                processedMessage = twit.PrivateMessage(((PrivateMessageCommand)com));
                break;
            case 7:
                processedMessage = twit.LogStat(((LogStatCommand)com));
                break;
            case 8:
                processedMessage = twit.Stats(((StatsCommand)com));
                break;
            case 12:
                processedMessage = twit.Block(((BlockCommand)com));
                break;
            default:
                System.out.println("error in protocol process");
        }

        if(!processedMessage.isEmpty()){
            for (ReturnCommand cmd : processedMessage){
                if(cmd.getDestUserID() == -1){
                    connections.send(connectionId, cmd);
                }
                else{
                    connections.send(cmd.getDestUserID(), cmd);
                }
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
