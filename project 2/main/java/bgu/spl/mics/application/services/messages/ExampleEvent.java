package bgu.spl.mics.application.services.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.services.GPUService;

public class ExampleEvent implements Event<String>{

    private String senderName;
    private Future<String> future;

    public String getType(){
        return "String";
    }

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public Future<String> getFuture() {
        return future;
    }

    public String getSenderName() {
        return senderName;
    }
}