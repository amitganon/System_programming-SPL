package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private final GPU gpu;
    private final Queue<Pair<TrainModelEvent,Integer>> TrainModelEventQueue;
    private final Queue<Pair<TestModelEvent,Integer>> TestModelEventQueue;
    int clock;
    private Event currentEvent;

    public GPUService(GPU gpu) {
        super("GPU - " + (gpu.getId()) + " Service");
        this.gpu = gpu;
        TrainModelEventQueue = new LinkedList<>();
        TestModelEventQueue = new LinkedList<>();
        clock=0;
        currentEvent=null;
    }

    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class, (TrainModelEvent)->{TrainModelEvent(TrainModelEvent);});
        subscribeEvent(TestModelEvent.class, (TestModelEvent)->{TestModel(TestModelEvent);});
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast)->{tick();});
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast)->{closeProcess();terminate();});
    }

    private void closeProcess(){
        for (Pair<TrainModelEvent ,Integer> pair:TrainModelEventQueue) {
            TrainModelEvent event = pair.getFirst();
            complete(event, null);
        }
        for (Pair<TestModelEvent ,Integer> pair:TestModelEventQueue) {
            TestModelEvent event = pair.getFirst();
            complete(event, null);
        }
        complete(currentEvent,null);
    }

    private void tick(){
        clock++;
        gpu.tick();
        if(gpu.isReady()) {
            if(gpu.isFinishTrainModel()) {
                gpu.setFinishTrainModel(false);
                complete(currentEvent,gpu.getModel());
            }
            nextEvent();
        }
    }

    private void TrainModelEvent(TrainModelEvent event){
        if(!gpu.isReady()) {
            Pair <TrainModelEvent,Integer> pair = new Pair(event,clock);
            TrainModelEventQueue.add(pair);
        }
        else {
            currentEvent=event;
            gpu.TrainModelEvent(event.getModel());
        }
    }

    private void TestModel(TestModelEvent event){
        if(!gpu.isReady()){
            Pair <TestModelEvent,Integer> pair = new Pair(event,clock);
            TestModelEventQueue.add(pair);
        }
        else {
            currentEvent=event;
            gpu.TestModel(event.getModel());
            complete(currentEvent,gpu.getModel().getResultString() == "Good");
            nextEvent();
        }
    }

    public void nextEvent(){
        if(TrainModelEventQueue.isEmpty() && !TestModelEventQueue.isEmpty()) {
            TestModelEvent event =TestModelEventQueue.poll().getFirst();
            currentEvent = event;
            TestModel(event);
        }
        else if(!TrainModelEventQueue.isEmpty() && TestModelEventQueue.isEmpty()) {
            TrainModelEvent event =TrainModelEventQueue.poll().getFirst();
            currentEvent = event;
            TrainModelEvent(event);
        }
        else if(!TrainModelEventQueue.isEmpty() && (TrainModelEventQueue.peek().getSecond() <= TestModelEventQueue.peek().getSecond())) {
            TrainModelEvent event =TrainModelEventQueue.poll().getFirst();
            currentEvent = event;
            TrainModelEvent(event);
        }
        else if(!TestModelEventQueue.isEmpty()) {
            TestModelEvent event =TestModelEventQueue.poll().getFirst();
            currentEvent = event;
            TestModel(event);
        }
    }

    public GPU getGpu() {
        return gpu;
    }
}
