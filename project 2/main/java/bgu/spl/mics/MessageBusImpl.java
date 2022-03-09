package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private final HashMap<Class<? extends Message>, Queue<MicroService>> event_subscribe; // string = type event
	private final ReadWriteLock eventLock;
	private final HashMap<Class<? extends Message>, LinkedList<MicroService>> Broadcast_subscribe;
	private final ReadWriteLock broadcastLock;

	private final HashMap<String, Queue<Message>> microService_queues; // string = microservice name

	private MessageBusImpl() {
		event_subscribe = new HashMap<>();
		Broadcast_subscribe= new HashMap<>();
		microService_queues = new HashMap<>();
		eventLock = new ReentrantReadWriteLock();
		broadcastLock = new ReentrantReadWriteLock();
		Initialize();
	}

	private void Initialize(){
		this.Broadcast_subscribe.put(TickBroadcast.class, new LinkedList<>());
		this.Broadcast_subscribe.put(TerminateBroadcast.class, new LinkedList<>());
		this.Broadcast_subscribe.put(PublishConferenceBroadcast.class, new LinkedList<>());

		this.event_subscribe.put(TrainModelEvent.class, new LinkedList<>());
		this.event_subscribe.put(TestModelEvent.class, new LinkedList<>());
		this.event_subscribe.put(PublishResultsEvent.class, new LinkedList<>());

	}

	private static class MessageBusImplHolder{
		private static MessageBusImpl messageBusImplInstance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImpl.MessageBusImplHolder.messageBusImplInstance;
	}

	/**
	 * @pre  this.isSubscribed(m,type) == false;
	 * @post this.isSubscribed(m,type) == true;
	 */
	@Override
	public <T> void  subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventLock.writeLock().lock();
		event_subscribe.get(type).add(m);
		eventLock.writeLock().unlock();
	}

	/**
	 * @pre  this.isSubscribed(m,type) == false;
	 * @post this.isSubscribed(m,type) == true;
	 */
	@Override
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastLock.writeLock().lock();
		Broadcast_subscribe.get(type).add(m);
		broadcastLock.writeLock().unlock();
	}

	/**
	 * @post e.future.isDone = true;
	 * @post result = e.future.get();
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
	}

	/**
	 * @pre none
	 * @post Broadcast_subscribe.get(b).size = 0
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		broadcastLock.readLock().lock();
		LinkedList<MicroService> list = Broadcast_subscribe.get(b.getClass());
		for (MicroService m : list) {
			synchronized (microService_queues.get(m.getName())) {
				microService_queues.get(m.getName()).add(b);
				microService_queues.get(m.getName()).notifyAll();
			}
		}
		broadcastLock.readLock().unlock();
	}

	/**
	 * @pre none
	 * @post event_subscribe.get(e).size() = @pre event_subscribe.get(e).size()
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		eventLock.readLock().lock();
		Queue<MicroService> queue = event_subscribe.get(e.getClass());
		eventLock.readLock().unlock();
		synchronized (event_subscribe.get(e.getClass())) {
			if (!queue.isEmpty()) {
				MicroService m = queue.poll();
				queue.add(m);
				synchronized (microService_queues.get(m.getName())) {
					microService_queues.get(m.getName()).add(e);
					microService_queues.get(m.getName()).notifyAll();
				}
			}
		}
		return e.getFuture();
	}

	/**
	 * @pre  this.isRegistered(m) == false;
	 * @post this.isRegistered(m) == true;
	 */
	@Override
	public void register(MicroService m) {
		synchronized (microService_queues) {
			microService_queues.put(m.getName(), new LinkedList<>());
		}
	}

	/**
	 * @post this.isRegistered(m) == false;
	 */
	@Override
	public void unregister(MicroService m) {
		eventLock.writeLock().lock();
		for (Queue<MicroService> queue:event_subscribe.values()) {
			for(int i=0; i<queue.size();i++){
				MicroService temp = queue.poll();
				if(temp.getName()!=m.getName())
					queue.add(temp);
			}
		}
		eventLock.writeLock().unlock();
		broadcastLock.writeLock().lock();
		for (LinkedList<MicroService> list:Broadcast_subscribe.values()) {
			list.removeIf(temp -> temp.getName() == m.getName());
		}
		broadcastLock.writeLock().unlock();
		synchronized (microService_queues) {
			microService_queues.remove(m.getName());
		}
	}

	/**
	 * @pre  this.isRegistered(m) == true;
	 * @post microService_queues[m].size() = @pre microService_queues[m].size()-1
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (microService_queues) {
			if (!isRegistered(m))
				throw new IllegalArgumentException();
		}
		synchronized (microService_queues.get(m.getName())) {
			while (microService_queues.get(m.getName()).isEmpty())
				microService_queues.get(m.getName()).wait();

			return microService_queues.get(m.getName()).poll();
		}
	}

	/**
	 * the function check if s the microService is registered
	 * <p>
	 * @return true if already registered or false
	 * @param s the MicroService
	 */
	public boolean isRegistered(MicroService s){
		synchronized (microService_queues) {
			return microService_queues.containsKey((s.getName()));
		}
	}

	public synchronized boolean isSubscribedBroadcast(MicroService s, Broadcast b){
		return Broadcast_subscribe.get(b.getClass()).contains(s);
	}

	public synchronized boolean isSubscribedEvent(MicroService s, Event e){
		return event_subscribe.get(e.getClass()).contains(s);
	}

	public HashMap<String, Queue<Message>> getMicroService_queues() {
		return microService_queues;
	}
}