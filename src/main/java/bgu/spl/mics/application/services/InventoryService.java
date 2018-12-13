package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory _inventory;

	public InventoryService(String name) {
		super(name);
		_inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

		subscribeEvent(CheckAvailabilityEvent.class, check_ev ->{
			System.out.println(getName() + " CHECKS if " + check_ev.get_bookToOrder() + " is available");
			int price =_inventory.checkAvailabiltyAndGetPrice(check_ev.get_bookToOrder());
			complete(check_ev,price);
		});
		subscribeEvent(TakeBookEvent.class, take_ev->{
			System.out.println(getName() + " TAKES " + take_ev.get_bookToOrder());
			OrderResult res =_inventory.take(take_ev.get_bookToOrder());
			complete(take_ev,res);
		});
	}

}
