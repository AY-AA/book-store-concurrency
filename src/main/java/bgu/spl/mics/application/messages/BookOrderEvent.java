package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer _customer;


    private String _bookToOrderTitle;
    private int _bookToOrderPrice;

    public BookOrderEvent(Customer customer, String title, int price){
        _bookToOrderTitle = title;
        _bookToOrderPrice = price;
        _customer = customer;
    }

    public Customer get_customer() {
        return _customer;
    }

    public String get_bookToOrderTitle() {
        return _bookToOrderTitle;
    }

    public int get_bookToOrderPrice() {
        return _bookToOrderPrice;
    }
}

