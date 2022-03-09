package bgu.spl.mics.application.services.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.services.GPUService;

public class ExampleBroadcast implements Broadcast {

    private String senderId;

    public String getType(){
        return "string";
    }

    public ExampleBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
