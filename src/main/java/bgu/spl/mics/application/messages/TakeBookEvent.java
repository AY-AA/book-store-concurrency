package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements Event<OrderResult> {

    private String _bookToOrder;

    public TakeBookEvent(String bookToOrder) {
        _bookToOrder = bookToOrder;
    }

    public String get_bookToOrder() {
        return _bookToOrder;
    }
}
