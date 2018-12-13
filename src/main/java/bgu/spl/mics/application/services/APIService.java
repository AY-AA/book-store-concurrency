package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

    private Customer _customer;
    private HashMap<Integer, Vector<String>> _booksTicks;
    private final int _lastOrderTick;

    public APIService(Customer customer, HashMap<Integer,Vector<String>> booksTicks) {
        super("APIService : " + customer.getId());
        _customer = customer;
        _booksTicks = booksTicks;
        _lastOrderTick = findLastTick();
    }

    private int findLastTick() {
        int max = -1 ;
        for (Integer currTick : _booksTicks.keySet())
        {
            if (currTick > max)
                max = currTick;
        }
        return max;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });


        subscribeBroadcast(TickBroadcast.class, ev -> {
            Vector<Future<OrderReceipt>> orders = new Vector<>();
            int currTick = ev.getCurrenTick();
            if (!_booksTicks.containsKey(currTick))
                return;
            Vector<String> books = _booksTicks.get(currTick);
            for (String currBook : books)
            {
                System.out.println(getName() + " is BUYING , tick number = " + currTick);
                Future<OrderReceipt> order = sendEvent(new BookOrderEvent(_customer,currBook,0));
                orders.add(order);
                if (order == null) {
                    System.out.println("No Micro-Service has registered to handle book order event events");
                }
            }
            for (Future<OrderReceipt> future : orders) {
                OrderReceipt oR = future.get();
                _customer.takeReceipt(oR);
                if (oR != null)
                    System.out.println(" ================ " + get_customer().getName() + " BOUGHT " + oR.getBookTitle());
                else
                    System.out.println(" ================ " + get_customer().getName() + " DIDNT BUY");
            }
            System.out.println(get_customer().getName() + " FINISHED ordering");
            if (_lastOrderTick == currTick) {
                System.out.println(get_customer().getName() + " ORDERED for the LAST time");
                terminate();
            }
        });
    }

    public Customer get_customer()
    {
        return _customer;
    }
}
