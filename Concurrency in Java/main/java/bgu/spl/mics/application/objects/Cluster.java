package bgu.spl.mics.application.objects;


import java.util.Collection;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {


	private final HashMap<Integer,CPU> CPUs;
	private final HashMap<Integer,GPU> GPUs;

	private final Queue<Pair<DataBatch,Integer>> dataBATCH_ForCPU;
	private final Queue <CPU> CpuLine;
	private final Statistics statistics;

	private Cluster(){
		statistics= new Statistics();
		CPUs = new HashMap<>();
		GPUs = new HashMap<>();
		dataBATCH_ForCPU = new LinkedList<>();
		CpuLine= new LinkedList<>();
	}
	/**
     * Retrieves the single instance of this class.
     */

	private static class clusterHolder{
		private static Cluster clusterInstance = new Cluster();
	}

	public static Cluster getInstance() {
		return clusterHolder.clusterInstance;
	}

	public void ReceiveDataFromCpu(Pair<DataBatch,Integer> dataBatchPair, int cpuID){
		GPU tempGPU= GPUs.get(dataBatchPair.getSecond());
		statistics.AddNumberOfDataBatchProcessedByCpu();
		tempGPU.ReceiveProcessedData(dataBatchPair.getFirst());
		CPU c = CPUs.get(cpuID);
		synchronized (CpuLine) {
			synchronized (dataBATCH_ForCPU) {
				if (dataBATCH_ForCPU.isEmpty())
					CpuLine.add(c);
				else {
					c.ReceiveUnProcessedData(dataBATCH_ForCPU.poll());
				}
			}
		}
	}

	public synchronized void ReceiveDataFromGpu(Pair<DataBatch,Integer> dataBatchPair){
		synchronized (CpuLine) {
			synchronized (dataBATCH_ForCPU) {
				if (CpuLine.isEmpty())
					dataBATCH_ForCPU.add(dataBatchPair);
				else
					CpuLine.poll().ReceiveUnProcessedData(dataBatchPair);
			}
		}
	}

	public void finishTrainModel(String modelName){
			statistics.AddModelName(modelName);
	}

	public void AddCPUS(Vector<CPU> cpus){
		for (CPU cpu:cpus) {
			CPUs.put(cpu.getId(),cpu);
			CpuLine.add(cpu);
		}
	}
	public void AddGPUS(Vector<GPU> gpus){
		for (GPU gpu: gpus) {
			GPUs.put(gpu.getId(),gpu);
		}
	}

	public Statistics getStatistics() {
		synchronized (statistics) {
			return statistics;
		}
	}
}
