package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CarAcquireEvent;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	public ResourceService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });
		subscribeEvent(CarAcquireEvent.class, acqEv ->{
			Future<DeliveryVehicle> future = ResourcesHolder.getInstance().acquireVehicle();
			if (future != null){
				DeliveryVehicle deliveryVehicle = future.get();
				complete(acqEv,deliveryVehicle);
			}
		});

		subscribeEvent(ReleaseVehicle.class, relEv->{
			ResourcesHolder.getInstance().releaseVehicle(relEv.get_deliveryVehicle());
		});
	}
}
