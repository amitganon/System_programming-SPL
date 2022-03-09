package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;

    private final double TestProbability;

    enum Status {PreTrained, Training, Trained,Tested}
    private Status status;

    enum Result {None, Good, Bad}
    private Result result;

    private boolean published;

    public Model(String name, Data data, double testProbability){
        this.name = name;
        this.data = data;
        this.status = Status.PreTrained;
        this.TestProbability = testProbability;
        this.result = Result.None;
        published=false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public String getStatusString() {
        return status.toString();
    }

    public void setStatusString(String status) {
        this.status = FromStringToStatus(status);
    }

    public String getResultString() {
        return result.toString();
    }

    public void setResultString(String result) {
        this.result = FromStringToResult(result);
    }

    public double getTestProbability() {
        return TestProbability;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", status=" + status +
                '}';
    }

    private Model.Result FromStringToResult(String type){
        switch (type) {
            case ("None"):
                return Result.None;
            case ("Good"):
                return Result.Good;
            case ("Bad"):
                return Result.Bad;
            default:
                return null;
        }
    }

    private Model.Status FromStringToStatus(String type){
        switch (type) {
            case ("PreTrained"):
                return Status.PreTrained;
            case ("Training"):
                return Status.Training;
            case ("Trained"):
                return Status.Trained;
            case ("Tested"):
                return Status.Tested;
            default:
                return null;
        }
    }
}
