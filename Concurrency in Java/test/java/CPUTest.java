import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    CPU cpu;
    @BeforeEach
    void setUp() {
        cpu = new CPU(8, 0);
    }

    @Test
    void receiveUnProcessedData() {
        assertNull(cpu.getDataBatch());
        Data d = new Data("Text",7800);
        DataBatch data = new DataBatch(0,d);
        assertEquals(0,cpu.getEndProcessedTime());
        cpu.ReceiveUnProcessedData(new <DataBatch ,Integer> Pair(data,0));
        assertEquals(8,cpu.getEndProcessedTime());
        assertEquals(cpu.getDataBatch(), data);
    }

    @Test
    void tickAndCompute() {

        for (int i = 0; i < cpu.getEndProcessedTime(); i++) {
            int time = cpu.getProcessedTime();
            cpu.tickAndCompute();
            assertEquals(time+1, cpu.getProcessedTime());
            assertFalse(cpu.isReady());
        }
        assertTrue(cpu.isReady());
        assertNull(cpu.getDataBatch());
        assertEquals(0, cpu.getEndProcessedTime());
    }

    @Test
    void isReady() {
        if(cpu.getDataBatch()== null){
            assertTrue(cpu.isReady());
        }
        else{
            assertFalse(cpu.isReady());
        }
    }
}