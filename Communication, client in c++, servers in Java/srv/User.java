package bgu.spl.net.srv;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private int ID;
    final private String name;
    final private String password;
    final private String Birthday;
    private AtomicInteger numOfFollowing;
    private AtomicInteger numOfFollowers;
    private AtomicInteger numPostedPost;
    private ConcurrentLinkedQueue<Message> unreadMsg;

    public User(int id, String name, String password, String birthday) {
        this.ID = id;
        this.name = name;
        this.password = password;
        this.Birthday = birthday;
        this.numOfFollowing = new AtomicInteger(0);
        this.numOfFollowers = new AtomicInteger(0);
        this.numPostedPost=new AtomicInteger(0);
        unreadMsg = new ConcurrentLinkedQueue<>();
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return Birthday;
    }

    public int getID() {
        return ID;
    }

    public int getNumOfFollowing() {
        return numOfFollowing.get();
    }

    public void addFollowing() {
        int val;
        do{
            val=this.numOfFollowing.get();
        }while(!numOfFollowing.compareAndSet(val,val+1));
    }
    public void subFollowing() {
        int val;
        do{
            val=this.numOfFollowing.get();
        }while(!numOfFollowing.compareAndSet(val,val-1));
    }

    public int getNumOfFollowers() {
        return numOfFollowers.get();
    }

    public void addFollowers() {
        int val;
        do{
            val=this.numOfFollowers.get();
        }while(!this.numOfFollowers.compareAndSet(val,val+1));
    }
    public void subFollowers() {
        int val;
        do{
            val=this.numOfFollowers.get();
        }while(!this.numOfFollowers.compareAndSet(val,val-1));
    }

    public int getNumPostedPost() {
        return numPostedPost.get();
    }

    public void addPostedPost() {
        int val;
        do{
            val=this.numPostedPost.get();
        }while(!this.numPostedPost.compareAndSet(val,val+1));
    }

    public int getAge(){
        String [] split = Birthday.split("-");
        String reversed = split[2] + "-" + split[1] + "-" + split[0];

        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.parse(reversed);
        Period p = Period.between(birthday, today);
        return p.getYears();
    }

    public String getStats(){
        return "" +  getAge() +" "+ numPostedPost.get() +" "+ numOfFollowers.get() +" "+ numOfFollowing.get();
    }

    public synchronized ConcurrentLinkedQueue<Message> getUnreadMsgAndReset() {
        ConcurrentLinkedQueue<Message> tmp = this.unreadMsg;
        this.unreadMsg = new ConcurrentLinkedQueue<>();
        return tmp;
    }


    public synchronized void addUnreadMsg(Message msg){
        this.unreadMsg.add(msg);
    }
}
