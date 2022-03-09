package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.GPUService;

public class TrainModelEvent implements Event<Model> {

    private int senderID;

    private Future<Model> future;
    private Model model;

    public TrainModelEvent(int senderID, Model model) {
        this.senderID = senderID;
        this.model = model;
        future = new Future<>();
    }

    public String getType(){
        return "Model";
    }

    public Future<Model> getFuture() {
        return future;
    }

    public void setFuture(Future<Model> future) {
        this.future = future;
    }

    public int getSenderID() {
        return senderID;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
