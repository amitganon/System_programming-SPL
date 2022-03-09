package bgu.spl.mics.application.objects.OutputObjects;

public class OutputJSON {
    private OutStudent[] students;
    private OutConference[] conferences;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    public OutputJSON(OutStudent[] students, OutConference[] conferences, int cpuTimeUsed, int gpuTimeUsed, int batchesProcessed) {
        this.students = students;
        this.conferences = conferences;
        this.cpuTimeUsed = cpuTimeUsed;
        this.gpuTimeUsed = gpuTimeUsed;
        this.batchesProcessed = batchesProcessed;
    }
}
