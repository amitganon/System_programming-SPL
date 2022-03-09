package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.*;

import static bgu.spl.mics.application.objects.Model.Status.Trained;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 * @inv processingDataBatch.size() <= capacity
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private final Type type;

    private final int id;
    private Model model;
    private final Cluster cluster;

    private final Queue<Pair<DataBatch,Integer>> processingDataBatch;

    private DataBatch[] unProcessedDataBatch;

    private int indexUPDB;
    private int countPDB;

    private int capacity;

    private int countDataBatchToSend;
    private int timeClock;
    private int trainingTime;

    private boolean finishTrainModel;
    private boolean ready;

    public GPU(String type,Cluster cluster, int id){
        this.type=FromStringToType(type);
        this.id = id;
        if(this.type == Type.GTX1080) {
            capacity = 8;
            trainingTime=4;
        }
        if(this.type == Type.RTX2080){
            capacity = 16;
            trainingTime=2;
        }
        if(this.type == Type.RTX3090) {
            capacity = 32;
            trainingTime=1;
        }

        processingDataBatch= new LinkedList<Pair<DataBatch,Integer>>();
        this.cluster = cluster;
        countPDB=0;
        model = null;
        indexUPDB = 0;
        timeClock=0;

        ready=true;
        finishTrainModel=false;
        countDataBatchToSend=capacity;
    }


    /**
     * represent a tick for the cpu.
     * <p>
     * @post this.timeClock == @pre(this.timeClock)+1
     */
    public void tick(){
        timeClock++;
        if(!ready) {
            if(!processingDataBatch.isEmpty())
                cluster.getStatistics().AddGpu_TimeUsed();
            TrainModel();
            SendDataBatch();
            if (unProcessedDataBatch!=null && countPDB == unProcessedDataBatch.length & countPDB != 0) {
                cluster.finishTrainModel(model.getName());
                finishTrainModel = true;
                model.setStatusString("Trained");
                Finish();
            }
        }
    }
    /**
     * this function trains the process data the gpu holds
     * <p>
     * @pre this.ready == false
     * @pre processingDataBatch.isEmpty() != true;
     * @post processingDataBatch.size <= @pre(processingDataBatch.size)
     * @post countPDB >= @pre(countPDB)
     * @post this.model.Status = Training
     * @post finishTrainModel = false
     */
    public void TrainModelEvent(Model m){
        ready=false;
        finishTrainModel=false;
        model=m;
        model.setStatusString("Training");
        DivideDataBatch();
    }

    /**
     * this function tests the model received
     * <p>
     * @pre m.Status = Trained
     * @pre this.ready = true
     * @post this.model = m
     * @post this.model.Status = Tested
     * @post finishTrainModel = false
     */
    public void TestModel(Model m){
        ready = false;
        finishTrainModel=false;
        this.model = m;
        Random rnd = new Random();
        if(rnd.nextDouble() < m.getTestProbability()){
            this.model.setResultString("Good");
        }
        else{
            this.model.setResultString("Bad");
        }
        model.setStatusString("Tested");
        Finish();
    }


    /**
     * this function trains the process data the gpu holds
     * <p>
     * @pre this.ready == false
     * @pre processingDataBatch.isEmpty() != true;
     * @post processingDataBatch.size <= @pre(processingDataBatch.size)
     * @post countPDB >= @pre(countPDB)
     */
    private void TrainModel(){
        synchronized (processingDataBatch) {
            while (!processingDataBatch.isEmpty() && processingDataBatch.peek().getSecond() + trainingTime <= timeClock) {
                processingDataBatch.poll();
                countPDB++;
            }
        }
    }

    /**
     * this function preperes the gpu for a new model
     * <p>
     * @pre this.model != null
     * @pre this.ready = false
     * @pre countPDB =  indexUPDB
     * @pre processingDataBatch.isEmpty() = true;
     * @post this.ready = true
     * @post this.unProcessedDataBatch = null
     * @post this.indexUPDB = 0
     * @post this.countPDB = 0
     * @post this.trainingTime =0 ;
     */
    private void Finish(){
        countPDB=0;
        indexUPDB=0;
        trainingTime=0;
        unProcessedDataBatch=null;
        this.ready=true;
    }

    /**
     * this function divides the data to data batch's.
     * <p>
     * @pre this.model != null
     * @pre this.ready == false
     * @pre unProcessedDataBatch.isEmpty() == true;
     * @post unProcessedDataBatch.isEmpty() == false;
     */
    private void DivideDataBatch(){
        int size =model.getData().getSize()/1000;
        if(model.getData().getSize()%1000!=0)
            size++;
        unProcessedDataBatch = new DataBatch[size];
        for(int i=0; i<size;i++){
            DataBatch data = new DataBatch(i*1000,model.getData());
            unProcessedDataBatch[i]=data;
        }
    }

    /**
     * if there's a databath that is unprocessed only send it to cluster if the gpu have a place to store it when comes back.
     * <p>
     * @pre this.model != null
     * @pre this.ready == false
     * @pre unProcessedDataBatch.size()-1 > indexUPDB
     * @post this.indexUPDB = @pre(indexUPDB)+1
     */
    private void SendDataBatch(){
        while(countDataBatchToSend>0 && indexUPDB<unProcessedDataBatch.length) {
            Pair tempPair = new  <DataBatch,Integer> Pair(unProcessedDataBatch[indexUPDB],id);
            cluster.ReceiveDataFromGpu(tempPair);
            indexUPDB++;
            countDataBatchToSend--;
        }
    }


    /**
     * this function receives a process data and store it
     * <p>
     * @pre this.model != null
     * @pre this.ready == false
     */
    public void ReceiveProcessedData(DataBatch databatch){
        synchronized (processingDataBatch) {
            processingDataBatch.add(new Pair<DataBatch, Integer>(databatch, timeClock));
            countDataBatchToSend++;
        }
    }

    /**
     * @return the Type by the string type
     * <p>
     * @param type the type
     */
    private Type FromStringToType(String type){
        switch (type) {
            case ("RTX3090"):
                return Type.RTX3090;
            case ("RTX2080"):
                return Type.RTX2080;
            case ("GTX1080"):
                return Type.GTX1080;
            default:
                return null;
        }
    }

    public Type getType() {
        return type;
    }

    public synchronized boolean isReady() {
        return ready;
    }

    public Model getModel() {
        return model;
    }

    public int getId() {
        return id;
    }

    public boolean isFinishTrainModel() {
        return finishTrainModel;
    }

    public void setFinishTrainModel(boolean finishTrainModel) {
        this.finishTrainModel = finishTrainModel;
    }

    public int getCapacity() {
        return capacity;
    }

    public Queue<Pair<DataBatch, Integer>> getProcessingDataBatch() {
        return processingDataBatch;
    }

    public DataBatch[] getUnProcessedDataBatch() {
        return unProcessedDataBatch;
    }

    public int getIndexUPDB() {
        return indexUPDB;
    }

    public int getCountPDB() {
        return countPDB;
    }

    public int getTimeClock() {
        return timeClock;
    }

    public int getTrainingTime() {
        return trainingTime;
    }

    public int getCountDataBatchToSend() {
        return countDataBatchToSend;
    }
}
