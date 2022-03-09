package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Pair;
import bgu.spl.mics.application.services.GPUService;

import java.util.PriorityQueue;
import java.util.Queue;

public class PublishConferenceBroadcast implements Broadcast {

    private Pair<String,Integer>[] models;

    public PublishConferenceBroadcast(Queue<Pair<String,Integer>> models) {
        this.models = new Pair[models.size()];
        int count =0;
        for (Pair<String,Integer> pair:models){
            this.models[count] = new Pair<>(pair.getFirst(),pair.getSecond());
            count++;
        }
    }

    public Pair<String, Integer>[] getModels() {
        return models;
    }

}
