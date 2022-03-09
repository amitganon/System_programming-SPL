package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.*;

public class ReactorMain {
    public static void main(String[] args) {
        Twitter twitter = new Twitter();

        int port = Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);

        try(Server<Command> server = Server.reactor(numOfThreads,port,()->new Protocol(twitter),()->new CommandEncoderDecoder());){
            server.serve();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
