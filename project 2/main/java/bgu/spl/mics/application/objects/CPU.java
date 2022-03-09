package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int id;
    private final int cores;
    private Pair<DataBatch,Integer> databatchPair;
    private final Cluster cluster;
    private boolean ready;
    private final AtomicInteger processedTime;
    private int endProcessedTime;

    public CPU(int numOfCores, int id){
        cores=numOfCores;
        this.cluster=Cluster.getInstance();
        this.id = id;
        ready=true;
        processedTime=new AtomicInteger(0);
    }

    /**
     * this function receiving unProcessed data and store it.
     * also calculate the time it will take to process the data.
     * <p>
     * @param databatchPair a pair that contains databatch from the cluster and the gpu name the data came from;
     * @pre this.ready = true;
     * @pre this.databatch=null;
     * @post this.databatch != null;
     * @post this.endProcessedTime=@pre(processedTime)+(32/@pre(cores))*databatch.getProcessedTime();
     * @post ths.ready = false;
     */
    public synchronized void ReceiveUnProcessedData(Pair<DataBatch,Integer> databatchPair){
        if(ready) {
            ready = false;
            this.databatchPair = databatchPair;
            endProcessedTime = processedTime.intValue() + (32 / cores) * databatchPair.getFirst().getProcessTime();
        }
    }


    /**
     * represent a tick for the cpu.
     * if in this tick the data finished processed than send it back to cluster, and make cpu ready for next databatch
     * <p>
     * @post this.processedTime == @prethis.processedTime+1
     * @post this.processedTime <= @pre(endProcessedTime) | @post(endProcessedTime) == 0
     * @post this.databatch == @pre(databatch) | @post(databatch) == null
     */
    public void tickAndCompute(){
        int val;
        do{
            val=processedTime.intValue();
        }while(!processedTime.compareAndSet(val,val+1));
        if(!ready) {
            cluster.getStatistics().AddCpu_TimeUsed();
        }
        if(!ready & processedTime.intValue()==endProcessedTime){
            endProcessedTime=0;
            ready=true;
            cluster.ReceiveDataFromCpu(databatchPair, this.id);
        }
    }

    /**
     * @return true or false if the cpu is ready to receive new databatch
     */
    public boolean isReady(){
        return ready;
    }

    /**
     * @return the databatch
     */
    public DataBatch getDataBatch(){
        if(databatchPair == null)
            return null;
        return databatchPair.getFirst();
    };

    /**
     * @return the processedTime, the amount of ticks that the cpu received
     */
    public int getProcessedTime(){return processedTime.intValue();};

    /**
     * @return the amount of tick that needed to process the databath
     */
    public int getEndProcessedTime(){return endProcessedTime;}
    public int getId() {
        return id;
    }
}
