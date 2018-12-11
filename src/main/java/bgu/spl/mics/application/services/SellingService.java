package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
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
    private int _currTick;

    public SellingService(String name) {
        super(name);
        this._moneyRegister = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

        subscribeBroadcast(TickBroadcast.class,tickEV->{
            _currTick = tickEV.getCurrenTick();
        });
        subscribeEvent(BookOrderEvent.class, ev -> {

            Future<Integer> isAvailable = sendEvent(new CheckAvailabilityEvent(ev.get_bookToOrderTitle()));
            if (isAvailable != null) {
                Integer price = isAvailable.get(); //waits until resolved
                if (price <= ev.get_customer().getAvailableCreditAmount()) {
                    Future<OrderResult> isTaken = sendEvent(new TakeBookEvent(ev.get_bookToOrderTitle()));
                    if (isTaken != null) {
                        OrderResult taken = isTaken.get();
                        if (taken == OrderResult.SUCCESSFULLY_TAKEN) {
                           _moneyRegister.chargeCreditCard(ev.get_customer(),price);
                           String address = ev.get_customer().getAddress();
                           int distance = ev.get_customer().getDistance();
                           sendEvent(new DeliveryEvent(address,distance));
                           OrderReceipt orderReceipt = createReceipt(ev.get_customer(),ev.get_bookToOrderTitle(),ev.get_bookToOrderPrice());
                           complete(ev,orderReceipt);
                        }
                    }
                }
            }
        });
    }

    private OrderReceipt createReceipt(Customer c, String bookTitle, int bookPrice){
        String seller = this.getName();
        int customerId = c.getId();
        String _bookTitle = bookTitle;
        int _bookPrice = bookPrice;
        int issuedTick = 1;
        int orderTick = 1;
        int processTick = 1;
        OrderReceipt orderReceipt = new OrderReceipt(0,seller,customerId,bookTitle,bookPrice,issuedTick,orderTick,processTick);
        return orderReceipt;
    }
}


















