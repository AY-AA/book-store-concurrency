package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

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

    public APIService(Customer customer, HashMap<Integer,Vector<String>> booksTicks) {
        super("APIService : " + customer.getCreditNumber());
        _customer = customer;
        _booksTicks = booksTicks;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });


        subscribeBroadcast(TickBroadcast.class, ev -> {
            int currTick = ev.getCurrenTick();
            if (!_booksTicks.containsKey(currTick))
                return;
            Vector<String> books = _booksTicks.get(currTick);
            for (String currBook : books)
            {
                Future<OrderReceipt> order = sendEvent(new BookOrderEvent(_customer,currBook));
                if (order == null) {
                    System.out.println("No Micro-Service has registered to handle book order event events");
                }
                OrderReceipt receipt = order.get();
                _customer.takeReceipt(receipt);
            }
        });
    }

    public Customer get_customer()
    {
        return _customer;
    }
}
