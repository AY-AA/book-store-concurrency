package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


public class BookOrderEvent implements Event<OrderReceipt> {

    private OrderReceipt orderReceipt;

    public BookOrderEvent(OrderReceipt or){
        this.orderReceipt = or;
    }



}

