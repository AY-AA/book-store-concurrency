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

    private int _currTick,_orderTick,_issuedTick;

    public SellingService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        // --- TerminateBroadcast subscription
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

        // --- TickBroadcast subscription
        subscribeBroadcast(TickBroadcast.class,tickEV->{
            _currTick = tickEV.getCurrentTick();
        });

        // --- BookOrderEvent subscription
        subscribeEvent(BookOrderEvent.class, ev -> {
            System.out.println(getName() + " is SELLING , tick number = " + _currTick);
            _orderTick = _currTick;                 //order tick update
            // checks if book is available
            Future<Integer> isAvailable = sendEvent(new CheckAvailabilityEvent(ev.get_bookToOrderTitle()));
            if (isAvailable != null) {              // there's a micro service which can handle it
                Integer price = isAvailable.get();  //waits until resolved and then gets price
                if (price == -1) {                  //books is not found
                    complete(ev,null);
                    System.out.println(ev.get_bookToOrderTitle() + " IS NOT FOUND!");
                    return;
                }
                if (price <= ev.get_customer().getAvailableCreditAmount()) {    // customer can buy it - has money
                    // tries to take the book from the inventory using other micro services
                    Future<OrderResult> isTaken = sendEvent(new TakeBookEvent(ev.get_bookToOrderTitle()));
                    if (isTaken != null) {  // there's a micro service which can handle it
                        OrderResult taken = isTaken.get();  //waits until resolved and then gets receipt
                        OrderReceipt orderReceipt = null;
                        if (taken == OrderResult.SUCCESSFULLY_TAKEN) {  //if taken, charge customer
                            MoneyRegister.getInstance().chargeCreditCard(ev.get_customer(),price);
                            String address = ev.get_customer().getAddress();
                            int distance = ev.get_customer().getDistance();
                            sendEvent(new DeliveryEvent(address,distance));
                            _issuedTick = _currTick;
                            orderReceipt = createReceipt(ev.get_customer(),ev.get_bookToOrderTitle(),price,_issuedTick,_orderTick);
                            MoneyRegister.getInstance().file(orderReceipt);
                        }
                        complete(ev,orderReceipt);   //whether it was bought or not, complete invokes the customer from waiting
                    }
                }
                else {      // if customer has no money, invoke customer from waiting
                    System.out.println("------ NO MONEY ! ------");
                    complete(ev,null);
                }
            }
        });
    }

    /**
     * This method creates a receipt for a customer order
     * @param c is the customer bought
     * @param bookTitle is the name of the bought book
     * @param bookPrice is the price of the book
     * @param issuedTick is the time when the receipt was created
     * @param orderProcessTick is the time when the order was made
     * @return an object typed OrderReceipt holds all the information above
     */
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


















