import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {
    GPU gpu;
    @BeforeEach
    void setUp() {
        gpu= new GPU("RTX2080", Cluster.getInstance(),0);
    }

    @Test
    void constructorTest(){
        assertTrue(gpu.isReady());
        assertEquals(gpu.getId(),0);
        assertEquals(gpu.getModel(),null);
        assertEquals(gpu.getCapacity(),16);
        assertEquals(gpu.getTrainingTime(),2);
    }

    @Test
    void tickGPUisEmpty() {
        int time = gpu.getTimeClock();
        gpu.tick();
        assertEquals(time+1, gpu.getTimeClock());
        assertTrue(gpu.isReady());
    }

    @Test
    void tickGPUisRunning() {
        Model model = new Model("testModel",new Data("Images",3000),1);
        gpu.TrainModelEvent(model);
        assertEquals(gpu.getModel().getStatusString(), "Training");
        assertFalse(gpu.isReady());
        assertFalse(gpu.isFinishTrainModel());
        assertEquals(gpu.getModel(),model);
        gpu.ReceiveProcessedData(new DataBatch(0,new Data("Images",3000)));
        gpu.ReceiveProcessedData(new DataBatch(1000,new Data("Images",3000)));
        gpu.ReceiveProcessedData(new DataBatch(2000,new Data("Images",3000)));
        gpu.tick();

        assertFalse(gpu.isReady());
        assertFalse(gpu.isFinishTrainModel());
        assertEquals(gpu.getModel(),model);
        assertEquals(gpu.getTimeClock(),1);
        gpu.tick();

        assertTrue(gpu.isReady());
        assertTrue(gpu.isFinishTrainModel());
        assertEquals(gpu.getTimeClock(),2);
        assertEquals(gpu.getModel().getStatusString(), "Trained");

        GPU gpu2=new GPU("RTX2080", Cluster.getInstance(),1);
        Data data = new Data("Tabular", 8000);
        DataBatch DB1 = new DataBatch(0,data);
        DataBatch DB2 = new DataBatch(1000,data);
        DataBatch DB3 = new DataBatch(2000,data);
        DataBatch DB4 = new DataBatch(3000,data);
        Model m = new Model("testModel",data, 0.8);
        gpu2.TrainModelEvent(model);
        gpu2.ReceiveProcessedData(DB1);
        gpu2.ReceiveProcessedData(DB2);
        gpu2.ReceiveProcessedData(DB3);
        gpu2.ReceiveProcessedData(DB4);
        gpu2.tick();
        assertEquals(4, gpu2.getProcessingDataBatch().size());
        assertEquals(0, gpu2.getCountPDB());
        gpu2.tick();
        assertEquals(0, gpu2.getProcessingDataBatch().size());
        assertEquals(4, gpu2.getCountPDB());

        gpu2.ReceiveProcessedData(DB1);
        gpu2.ReceiveProcessedData(DB2);
        gpu2.tick();
        assertEquals(2, gpu2.getProcessingDataBatch().size());
        assertEquals(4, gpu2.getCountPDB());
        gpu2.ReceiveProcessedData(DB3);
        gpu2.ReceiveProcessedData(DB4);

        assertEquals(4, gpu2.getProcessingDataBatch().size());
        assertEquals(4, gpu2.getCountPDB());

        gpu2.tick();
        assertEquals(2, gpu2.getProcessingDataBatch().size());
        assertEquals(6, gpu2.getCountPDB());
        gpu2.tick();
        assertEquals(0, gpu2.getProcessingDataBatch().size());
        assertEquals(8, gpu2.getCountPDB());

        assertEquals(0,gpu.getProcessingDataBatch().size());
        assertNull(gpu.getUnProcessedDataBatch());
        assertEquals(0,gpu.getIndexUPDB());
        assertEquals(0,gpu.getCountPDB());
        assertEquals(0,gpu.getTrainingTime());
        assertTrue(gpu.isReady());
    }

    @Test
    void trainModelEvent() {
        Model model = new Model("testModel", new Data("Images", 3000), 1);
        gpu.TrainModelEvent(model);
        assertFalse(gpu.isReady());
        assertFalse(gpu.isFinishTrainModel());
        assertEquals(gpu.getModel(), model);
        assertFalse(gpu.getUnProcessedDataBatch().length == 0);
    }

    @Test
    void testModelEvent() {
        Model model = new Model("testModel", new Data("Images", 3000), 1);
        gpu.TestModel(model);

        assertEquals(gpu.getModel(), model);
        assertEquals(gpu.getModel().getResultString(), "Good");
        assertEquals(gpu.getModel().getStatusString(), "Tested");
    }

    @Test
    void divideDataBatch() {
        assertNull(gpu.getUnProcessedDataBatch());
        Model model = new Model("testModel", new Data("Images", 2000), 1);
        gpu.TrainModelEvent(model);

        assertEquals(gpu.getUnProcessedDataBatch().length,2);

        Data data2 = new Data("Text", 2500);
        model=new Model("testModel",data2, 0.8);
        gpu=new GPU("RTX3090", Cluster.getInstance(),0);
        gpu.TrainModelEvent(model);
        assertEquals(gpu.getUnProcessedDataBatch().length,3);
    }

    @Test
    void receiveProcessedData() {
        Data data = new Data("TEXT",7500);
        DataBatch databatch = new DataBatch(1000,data);
        int ProcessingDataBatch_Size = gpu.getProcessingDataBatch().size();
        int CountDataBatchToSend = gpu.getCountDataBatchToSend();
        gpu.ReceiveProcessedData(databatch);
        assertTrue(ProcessingDataBatch_Size+1== gpu.getProcessingDataBatch().size());
        assertEquals(CountDataBatchToSend+1,gpu.getCountDataBatchToSend());
    }
}