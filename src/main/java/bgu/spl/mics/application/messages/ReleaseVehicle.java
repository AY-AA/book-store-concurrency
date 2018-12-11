package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicle implements Event<DeliveryVehicle> {

    private DeliveryVehicle _deliveryVehicle;

    public ReleaseVehicle(DeliveryVehicle deliveryVehicle){
        _deliveryVehicle = deliveryVehicle;
    }

    public DeliveryVehicle get_deliveryVehicle(){
        return _deliveryVehicle;
    }
}
