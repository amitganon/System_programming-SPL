package bgu.spl.mics.application.objects;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private final Stack<String> modelNames;
    private final AtomicInteger gpu_TimeUsed;
    private final AtomicInteger cpu_TimeUsed;
    private final AtomicInteger numberOfDataBatchProcessedByCpu;

    public Statistics(){
        modelNames = new Stack<String>();
        gpu_TimeUsed=new AtomicInteger(0);
        cpu_TimeUsed=new AtomicInteger(0);
        numberOfDataBatchProcessedByCpu=new AtomicInteger(0);
    }
    public void AddNumberOfDataBatchProcessedByCpu(){
        int val;
        do{
            val=numberOfDataBatchProcessedByCpu.get();
        }while(!numberOfDataBatchProcessedByCpu.compareAndSet(val,val+1));
    }

    public void AddGpu_TimeUsed(){
        int val;
        do{
            val=gpu_TimeUsed.get();
        }while(!gpu_TimeUsed.compareAndSet(val,val+1));
    }

    public void AddCpu_TimeUsed(){
        int val;
        do{
            val=cpu_TimeUsed.get();
        }while(!cpu_TimeUsed.compareAndSet(val,val+1));
    }

    public Stack<String> getModelNames() {
        return modelNames;
    }

    public void AddModelName(String modelName) {
        synchronized (modelNames) {
            this.modelNames.add(modelName);
        }
    }

    public AtomicInteger getGpu_TimeUsed() {
        return gpu_TimeUsed;
    }
    public AtomicInteger getCpu_TimeUsed() {
        return cpu_TimeUsed;
    }
    public AtomicInteger getNumberOfDataBatchProcessedByCpu() {
        return numberOfDataBatchProcessedByCpu;
    }

}
