package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.GPUService;

public class TestModelEvent implements Event<Boolean> {

    private int senderID;
    private Future<Boolean> future;
    private Model model;

    public TestModelEvent(int senderID, Model model) {
        this.senderID = senderID;
        this.model = model;
        future = new Future<>();
    }

    public String getType(){
        return "Model";
    }

    public Future<Boolean> getFuture() {
        return future;
    }

    public Model getModel() {
        return model;
    }

    public void setFuture(Future<Boolean> future) {
        this.future = future;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
