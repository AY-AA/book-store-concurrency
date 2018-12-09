package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

    private MoneyRegister _moneyRegister;

    public SellingService(String name) {
        super(name);
        this._moneyRegister = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeEvent(BookOrderEvent.class, ev -> {

            Future<Integer> isAvailable = sendEvent(new CheckAvailabilityEvent(ev.get_bookToOrder()));
            if (isAvailable != null) {
                Integer price = isAvailable.get(); //waits until resolved
                if (price <= ev.get_customer().getAvailableCreditAmount()) {
                    Future<OrderResult> isTaken = sendEvent(new TakeBookEvent(ev.get_bookToOrder()));
                    if (isTaken != null) {
                        OrderResult taken = isTaken.get();
                        if (taken == OrderResult.SUCCESSFULLY_TAKEN) {
                           _moneyRegister.chargeCreditCard(ev.get_customer(),price);
                        }
                    }
                }
            }
        });
    }
}


















