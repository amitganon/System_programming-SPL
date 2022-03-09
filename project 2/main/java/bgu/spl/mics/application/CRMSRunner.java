package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.OutputObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    private static Vector<StudentService> StudentServices;

    public static void main(String[] args) {

        if(args.length != 1){
            System.out.println("no input file");
        }
        String inputFile = args[0];

        String students="";
        String gpus="";
        String cpus="";
        String conferences="";
        String TickTime = "";
        String Duration = "";

        MessageBusImpl messageBus = MessageBusImpl.getInstance();

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(inputFile));
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey().equals("Students"))  students = entry.getValue().toString();
                else if(entry.getKey().equals("GPUS")) gpus = entry.getValue().toString();
                else if(entry.getKey().equals("CPUS")) cpus = entry.getValue().toString();
                else if(entry.getKey().equals("Conferences")) conferences = entry.getValue().toString();
                else if(entry.getKey().equals("TickTime")) TickTime = entry.getValue().toString();
                else if(entry.getKey().equals("Duration")) Duration = entry.getValue().toString();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        StudentServices = new Vector<>();
        Vector<Student> STUDENTS = JsonStringToStudentList(students);
        Vector<GPU> GPUS = JsonStringToGPUList(gpus);
        Vector<CPU> CPUS = JsonStringToCPUList(cpus);
        Vector<ConfrenceInformation> CONFERENCES = JsonStringToConfList(conferences);
        int TICK_TIME = Integer.parseInt(TickTime.substring(0, TickTime.length()-2));
        int DURATION = Integer.parseInt(Duration.substring(0, Duration.length()-2));

        Cluster cluster = Cluster.getInstance();
        cluster.AddCPUS(CPUS);
        cluster.AddGPUS(GPUS);

        try {
            Thread.sleep(100);
        }
        catch (Exception e){

        }


        for (int i = 0; i < StudentServices.size(); i++) {
            Thread studentThread = new Thread(StudentServices.elementAt(i));
            studentThread.start();
        }

        TimeService timeService = new TimeService(TICK_TIME, DURATION);
        timeService.run();



        OutputJSON outputJSON = CreateOutputObject(STUDENTS, CONFERENCES, cluster);
        String jsonString = ConvertObjectToJSONString(outputJSON);
        //System.out.println(jsonString);
        SaveJSONObject(outputJSON);
    }


    public static Vector<Student> JsonStringToStudentList(String students){
        Vector<Student> Result = new Vector<>();
        int StudentID = 0;
        students = students.substring(1,students.length()-3);
        for (String str : students.split("]}, ")) {
            String[] str_split1 = str.split("models=");
            String[] str_split = str_split1[0].split(", ");
            String name = str_split[0].substring(6);
            String department = str_split[1].substring(11);
            String status = str_split[2].substring(7);
            Student student = new Student(name, department, status, StudentID);
            StudentID++;
            Result.add(student);
            StudentService studentService = new StudentService(student);
            StudentServices.add(studentService);
            String[] models = str_split1[1].substring(1, str_split1[1].length()-1).split("}, ");
            for(String m : models){
                String[] m_split = m.split(", ");
                String modelName = m_split[0].substring(6);
                String DataType = m_split[1].substring(5);
                String DataSize = m_split[2].substring(5);
                int DataSizeInt=0;
                if (DataSize.contains("E")) DataSizeInt = new BigDecimal(DataSize).intValue();
                else DataSizeInt = Integer.parseInt(DataSize.substring(0, DataSize.length()-2));

                Data data = new Data(DataType, DataSizeInt);
                double testProb=0;
                if(student.getStatus() == Student.Degree.MSc){
                    testProb = 0.6;
                }
                else{
                    testProb = 0.8;
                }
                Model model = new Model(modelName, data, testProb);
                student.AddModel(model);
            }
        }
        return Result;
    }

    public static Vector<GPU> JsonStringToGPUList(String gpus){
        Vector<GPU> Result = new Vector<>();
        int GPU_ID = 0;
        gpus = gpus.substring(1,gpus.length()-1);
        for (String str : gpus.split(", ")) {
            GPU gpu = new GPU(str, Cluster.getInstance(), GPU_ID);
            GPU_ID++;
            Result.add(gpu);
            GPUService gpuService = new GPUService(gpu);
            Thread gpuThread = new Thread(gpuService);
            gpuThread.start();
        }
        return Result;
    }

    public static Vector<CPU> JsonStringToCPUList(String cpus){
        Vector<CPU> Result = new Vector<>();
        int CPU_ID = 0;
        cpus = cpus.substring(1,cpus.length()-1);
        for (String str : cpus.split(", ")) {
            CPU cpu = new CPU(Integer.parseInt(str.substring(0,str.length()-2)), CPU_ID);
            CPU_ID++;
            Result.add(cpu);
            CPUService cpuService = new CPUService(cpu);
            Thread cpuThread = new Thread(cpuService);
            cpuThread.start();
        }
        return Result;
    }

    public static Vector<ConfrenceInformation> JsonStringToConfList(String conferences){
        Vector<ConfrenceInformation> Result = new Vector<>();
        conferences = conferences.substring(1,conferences.length()-2);
        for (String str : conferences.split("}, ")) {
            String cName = str.split(", ")[0].substring(6);
            String cDate = str.split(", ")[1].substring(5);
            int cDateInt = Integer.parseInt(cDate.substring(0, cDate.length()-2));
            ConfrenceInformation conference = new ConfrenceInformation(cName, cDateInt);
            Result.add(conference);
            ConferenceService conferenceService = new ConferenceService(conference);
            Thread confThread = new Thread(conferenceService);
            confThread.start();
        }
        return Result;
    }



    public static OutputJSON CreateOutputObject(Vector<Student> students, Vector<ConfrenceInformation> conferences, Cluster cluster){
        OutStudent[] outStudents = new OutStudent[students.size()];
        for(int i = 0; i < students.size(); i++){
            Student s = students.elementAt(i);
            OutModel[] models = new OutModel[s.getTrainedModels().size()];
            int j = 0;
            for(Model m : s.getTrainedModels()){
                Data data = m.getData();
                OutData outdata = new OutData(data.getTypeString(), data.getSize());
                models[j] = new OutModel(m.getName(), outdata, m.getStatusString(), m.getResultString());
                j++;
            }
            outStudents[i] = new OutStudent(s.getName(), s.getDepartment(), s.getStatus().toString(), s.getPublications(), s.getPapersRead(), models);
        }

        OutConference[] outConferences = new OutConference[conferences.size()];
        for(int i = 0; i < conferences.size(); i++){
            ConfrenceInformation c = conferences.elementAt(i);
            OutModel[] models = new OutModel[c.getSuccessfulModels().size()];
            boolean isnull = true;
            int j = 0;
            for(Pair<String, Integer> p : c.getSuccessfulModels()){
                for (Student student : students){
                    if(p.getSecond() == student.getId()){
                        for (Model s_model : student.getTrainedModels()){
                            if(s_model.isPublished() && s_model.getName()==p.getFirst()){
                                Data data = s_model.getData();
                                OutData outdata = new OutData(data.getTypeString(), data.getSize());
                                models[j] = new OutModel(s_model.getName(), outdata, s_model.getStatusString(), s_model.getResultString());
                                isnull = false;
                            }
                        }
                    }
                }
                j++;
            }
            if(isnull){
                models = new OutModel[0];
            }
            outConferences[i] = new OutConference(c.getName(), c.getDate(), models);
        }

        int cpuTimes = cluster.getStatistics().getCpu_TimeUsed().get();
        int gpuTimes = cluster.getStatistics().getGpu_TimeUsed().get();
        int batchTimes = cluster.getStatistics().getNumberOfDataBatchProcessedByCpu().get();

        OutputJSON out = new OutputJSON(outStudents, outConferences,cpuTimes, gpuTimes, batchTimes);
        return out;
    }

    public static String ConvertObjectToJSONString(OutputJSON out){
        String json = "";
        try {
            json = new GsonBuilder().setPrettyPrinting().create().toJson(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static void SaveJSONObject(OutputJSON out){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = Files.newBufferedWriter(Paths.get("output.json"));
            gson.toJson(out, writer);
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
