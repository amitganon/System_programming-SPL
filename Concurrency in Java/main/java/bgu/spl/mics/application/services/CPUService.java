package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private final CPU cpu;

    public CPUService(CPU cpu) {
        super("CPU - " + (cpu.getId()) + " Service");
        this.cpu=cpu;
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)-> {
            cpu.tickAndCompute();
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast)->{terminate();});
    }
}
