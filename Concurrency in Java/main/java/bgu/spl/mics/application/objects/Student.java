package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.services.StudentService;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private final int id;
    private String name;
    private final String department;
    private final Degree status;
    private int publications;
    private int papersRead;

    private final Vector <Model> models;
    private final LinkedList<Model> TrainedModels;

    public Student(String name, String department, String degree, int id){
        this.name = name;
        this.department = department;
        this.status = FromStringToType(degree);
        publications = 0;
        papersRead = 0;
        this.models = new Vector<>();
        this.TrainedModels =new <Model> LinkedList ();
        this.id = id;
    }

    public void AddModel(Model m){
        this.models.add(m);
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublications(int publications, String modelName) {
        this.publications = publications;
        for (Model model:TrainedModels) {
            if(model.getName()==modelName)
                model.setPublished(true);
        }
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

    public int getId() {
        return id;
    }

    public Vector<Model> getModels() {
        return models;
    }

    public LinkedList<Model> getTrainedModels() {
        return TrainedModels;
    }

    /**
     * @return the Type by the string type
     * <p>
     * @param type the type
     */
    private Student.Degree FromStringToType(String type){
        switch (type.toLowerCase()) {
            case ("msc"):
                return Student.Degree.MSc;
            case ("phd"):
                return Student.Degree.PhD;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", status=" + status +
                ", publications=" + publications +
                ", papersRead=" + papersRead +
                '}';
    }
}
