package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	private int TickCount;
	private final long speed;
	private final int duration;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		this.TickCount = 1;
		act();
	}

	private void act() {
		TickBroadcast tickBroadcast = new TickBroadcast();
		while (TickCount < duration) {
			sendBroadcast(tickBroadcast);
			TickCount++;
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
			}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}
}
