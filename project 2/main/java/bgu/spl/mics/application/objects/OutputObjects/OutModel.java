package bgu.spl.mics.application.objects.OutputObjects;

import bgu.spl.mics.application.objects.Data;

public class OutModel {
    private String name;
    private OutData data;
    private String status;
    private String results;

    public OutModel(String name, OutData data, String status, String results) {
        this.name = name;
        this.data = data;
        this.status = status;
        this.results = results;
    }
}
