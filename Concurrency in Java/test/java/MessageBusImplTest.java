import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.messages.ExampleBroadcast;
import bgu.spl.mics.application.services.messages.ExampleEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    MessageBusImpl mBus;
    TrainModelEvent e;
    MicroService m;
    TickBroadcast b;
    @BeforeEach
    void setUp() {
        mBus = MessageBusImpl.getInstance();
    }

    @Test
    void subscribeEvent() {
        TrainModelEvent event = new TrainModelEvent(0,new Model("testModel",new Data("Text",2000),1));
        MicroService m = new GPUService(new GPU("GTX1080",Cluster.getInstance(),0));
        mBus.register(m);
        assertFalse(mBus.isSubscribedEvent(m,event));
        mBus.subscribeEvent(event.getClass(),m);
        assertTrue(mBus.isSubscribedEvent(m,event));
    }

    @Test
    void subscribeBroadcast() {
        TickBroadcast tickB = new TickBroadcast();
        MicroService k = new GPUService(new GPU("GTX1080",Cluster.getInstance(),1));
        mBus.register(k);
        assertFalse(mBus.isSubscribedBroadcast(k,tickB));
        mBus.subscribeBroadcast(tickB.getClass(),k);
        assertTrue(mBus.isSubscribedBroadcast(k,tickB));
    }

    @Test
    void complete() {
        Model model = new Model("testModel",new Data("Text",2000),1);
        TrainModelEvent e = new TrainModelEvent(0,new Model("testModel",new Data("Text",2000),1));
        mBus.complete(e,model);
        assertTrue(e.getFuture().isDone());
        assertEquals(e.getFuture().get(),model);
    }

//    @Test
//    void sendBroadcast() {
//        TerminateBroadcast ter = new TerminateBroadcast();
//        MicroService mic = new GPUService(new GPU("GTX1080",Cluster.getInstance(),6));
//        mBus.register(mic);
//        mBus.subscribeBroadcast(ter.getClass(),mic);
//        mBus.sendBroadcast(ter);
//        assertEquals( mBus.getMicroService_queues().get(mic).size(),1);
//    }
//
//    @Test
//    void sendEvent() {
//        mBus.register(m);
//        mBus.subscribeEvent(e.getClass(),m);
//        mBus.sendEvent(e);
//        assertEquals(mBus.getMicroService_queues().get(m).size(),1);
//    }

    @Test
    void register() {
        MicroService mic = new GPUService(new GPU("GTX1080",Cluster.getInstance(),6));
        assertFalse(mBus.isRegistered(mic));
        mBus.register(mic);
        assertTrue(mBus.isRegistered(mic));
    }

    @Test
    void unregister() {
        MicroService mic2 = new GPUService(new GPU("GTX1080",Cluster.getInstance(),6));
        mBus.register(mic2);
        assertTrue(mBus.isRegistered(mic2));
        mBus.unregister(mic2);
        assertFalse(mBus.isRegistered(mic2));
    }

//    @Test
//    void awaitMessage() {
//        try{
//            mBus.awaitMessage(m);
//            assertTrue(false);
//        }
//        catch (Exception e){
//            assertTrue(e.equals(new InterruptedException()));
//        }
//
//        mBus.register(m);
//        Thread t = new Thread(){
//            public void run(){
//                try{
//                    mBus.awaitMessage(m);
//                }
//                catch (Exception e){
//                    assertTrue(false);
//                }
//            }
//        };
//        t.run();
//
//        try {
//            Thread.sleep(1000);
//            assertEquals(t.getState(),Thread.State.WAITING);
//            mBus.subscribeEvent(e.getClass(),m);
//            mBus.sendEvent(e);
//            Thread.sleep(10);
//            assertNotEquals(t.getState(),Thread.State.WAITING);
//        }
//        catch (Exception e){
//        }
//    }
}