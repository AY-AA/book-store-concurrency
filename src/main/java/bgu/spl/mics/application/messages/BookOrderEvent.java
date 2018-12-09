package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer _customer;
    private String _bookToOrder;

    public BookOrderEvent(Customer customer, String bookToOrder){
        _bookToOrder = bookToOrder;
        _customer = customer;
    }

    public Customer get_customer() {
        return _customer;
    }

    public String get_bookToOrder() {
        return _bookToOrder;
    }
}

