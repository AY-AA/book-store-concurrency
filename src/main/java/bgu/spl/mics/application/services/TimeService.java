package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private final int _duration;
	private final int _speed;
	private Timer _timer;
	private int _currTick;

	public TimeService(int speed, int duration) {
		super("TimeService");
		_speed = speed;
		_duration = duration;

	}

	@Override
	protected void initialize() {
        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
				_currTick ++;
				if (_currTick == 3)
					System.out.println(" tick number 3");
                sendBroadcast(new TickBroadcast(_currTick));
                if (_currTick == _duration) {
                    sendBroadcast(new TerminateBroadcast());
                    _timer.cancel();
                    terminate();
                }
            }
        }, _speed, _speed);
	}

}
