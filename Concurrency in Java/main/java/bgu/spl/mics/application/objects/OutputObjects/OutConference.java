package bgu.spl.mics.application.objects.OutputObjects;

public class OutConference {
    private String name;
    private int date;
    private OutModel[] trainedModels;

    public OutConference(String name, int date, OutModel[] trainedModels) {
        this.name = name;
        this.date = date;
        this.trainedModels = trainedModels;
    }
}
