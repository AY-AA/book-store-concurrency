package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliveryEvent implements Event {

    String _address;

    public String get_address() {
        return _address;
    }

    public int get_distance() {
        return _distance;
    }

    int _distance;

    public DeliveryEvent(String address, int distance){
        _address = address;
        _distance = distance;
    }

}
