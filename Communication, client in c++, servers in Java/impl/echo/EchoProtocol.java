package bgu.spl.net.impl.echo;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionsImp;

import java.time.LocalDateTime;

public class EchoProtocol implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;
    private Connections<String> connections;
    private int conId;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connections = connections;
        conId = connectionId;
    }

    @Override
    public void process(String msg) {
        shouldTerminate = "bye".equals(msg);
        System.out.println("[" + LocalDateTime.now() + "]: " + msg);
        String echo = createEcho(msg);
        connections.send(conId, echo);
        //return createEcho(msg);
    }

    private String createEcho(String message) {
        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
        return (message + " .. " + echoPart + " .. " + echoPart + " ..");
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
