package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CarAcquireEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, ev -> {
			terminate();
		});
		subscribeEvent(DeliveryEvent.class, delEv->{
			System.out.println(getName() + " got a delivery request");
			Future<DeliveryVehicle> future = sendEvent(new CarAcquireEvent());
			if(future != null){
				DeliveryVehicle deliveryVehicle = future.get();
				if(deliveryVehicle != null){
					deliveryVehicle.deliver(delEv.get_address(),delEv.get_distance());
					System.out.println(getName() + " has completed the delivery and now releasing the vehicle");
					sendEvent(new ReleaseVehicle(deliveryVehicle));
				}
			}
		});
	}

}
