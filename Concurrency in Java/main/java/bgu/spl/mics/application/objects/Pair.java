package bgu.spl.mics.application.objects;

public class Pair <T,K>{
    private T first;
    private K second;
    public Pair(T first, K second){
        this.first = first;
        this.second=second;
    }
    public T getFirst(){
        return first;
    }
    public K getSecond(){
        return second;
    }
}
