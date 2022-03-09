package bgu.spl.mics.application.objects.OutputObjects;

import bgu.spl.mics.application.objects.Model;

public class OutStudent {
    private String name;
    private String department;
    private String status;
    private int publications;
    private int papersRead;
    private OutModel[] trainedModels;

    public OutStudent(String name, String department, String status, int publications, int papersRead, OutModel[] trainedModels) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = publications;
        this.papersRead = papersRead;
        this.trainedModels = trainedModels;
    }
}
