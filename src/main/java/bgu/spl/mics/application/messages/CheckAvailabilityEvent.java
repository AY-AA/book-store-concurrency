package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.awt.print.Book;

public class CheckAvailabilityEvent implements Event<Integer> {

    private String _bookToOrder;

    public CheckAvailabilityEvent(String bookToOrder) {
        _bookToOrder = bookToOrder;
    }

    public String get_bookToOrder() {
        return _bookToOrder;
    }
}
