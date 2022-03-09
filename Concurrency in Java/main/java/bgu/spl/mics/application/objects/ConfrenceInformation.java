package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final int date;
    private final Queue<Pair<String,Integer>> successfulModels;

    public ConfrenceInformation(String name, int date) {
        this.successfulModels = new LinkedList<>();
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }


    public int getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ConfrenceInformation{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }

    public Queue<Pair<String, Integer>> getSuccessfulModels() {
        return successfulModels;
    }

    public void addSuccessfulModel(Pair<String, Integer> successfulModel) {
        this.successfulModels.add(successfulModel);
    }
}
