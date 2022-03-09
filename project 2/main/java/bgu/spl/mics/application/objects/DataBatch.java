package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private int processTime;
    private final int start_index;
    Data data;

    public DataBatch(int index,Data data) {
        this.start_index = index;
        this.data = data;
        if(data.getTypeString() == "Images") this.processTime = 4;
        if(data.getTypeString() == "Text") this.processTime = 2;
        if(data.getTypeString() == "Tabular") this.processTime = 1;
    }

    public int getProcessTime(){
        return processTime;
    }
}
