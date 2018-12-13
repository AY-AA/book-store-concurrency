package bgu.spl.mics.application.services;

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
    private int _currTick,_orderTick,_issuedTick;

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
            System.out.println(getName() + " is SELLING , tick number = " + _currTick);
            _orderTick = _currTick;
            Future<Integer> isAvailable = sendEvent(new CheckAvailabilityEvent(ev.get_bookToOrderTitle()));
            if (isAvailable != null) {
                Integer price = isAvailable.get(); //waits until resolved
                if (price == -1) {
                    complete(ev,null);
                    System.out.println(ev.get_bookToOrderTitle() + " IS NOT FOUND!");
                    return;
                }
                if (price <= ev.get_customer().getAvailableCreditAmount()) {
                    Future<OrderResult> isTaken = sendEvent(new TakeBookEvent(ev.get_bookToOrderTitle()));
                    if (isTaken != null) {
                        OrderResult taken = isTaken.get();
                        OrderReceipt orderReceipt = null;
                        if (taken == OrderResult.SUCCESSFULLY_TAKEN) {
                           _moneyRegister.chargeCreditCard(ev.get_customer(),price);
                           String address = ev.get_customer().getAddress();
                           int distance = ev.get_customer().getDistance();
                           sendEvent(new DeliveryEvent(address,distance));
                            _issuedTick = _currTick;
                            orderReceipt = createReceipt(ev.get_customer(),ev.get_bookToOrderTitle(),ev.get_bookToOrderPrice(),_issuedTick,_orderTick);
                           _moneyRegister.file(orderReceipt);
                        }
                        complete(ev,orderReceipt);
                    }
                }
                else {
                    System.out.println("------ NO MONEY ! ------");
                    complete(ev,null);
                }
            }
        });
    }

    private OrderReceipt createReceipt(Customer c, String bookTitle, int bookPrice,int issuedTick,int orderProcessTick){
        String seller = this.getName();
        int customerId = c.getId();
        String _bookTitle = bookTitle;
        int _bookPrice = bookPrice;
        int tIssuedTick = issuedTick;
        int tOrderTick = orderProcessTick;
        int processTick = orderProcessTick;
        OrderReceipt orderReceipt = new OrderReceipt(0,seller,customerId,_bookTitle,_bookPrice,tIssuedTick,tOrderTick,processTick);
        return orderReceipt;
    }
}


















