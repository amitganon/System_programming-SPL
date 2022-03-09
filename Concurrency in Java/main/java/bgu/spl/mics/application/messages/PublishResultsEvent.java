package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.GPUService;

public class PublishResultsEvent implements Event<Boolean> {

    private int senderID;
    private Future<Boolean> future;
    private String modelName;

    public PublishResultsEvent(int senderID, String modelName) {
        this.senderID = senderID;
        this.modelName = modelName;
        future = new Future<>();
    }

    public String getType(){
        return "Model";
    }

    public Future<Boolean> getFuture() {
        return future;
    }

    public int getSenderId() {
        return senderID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModel(String modelName) {
        this.modelName = modelName;
    }

    public void setFuture(Future<Boolean> future) {
        this.future = future;
    }
}
