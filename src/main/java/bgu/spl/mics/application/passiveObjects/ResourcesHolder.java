package bgu.spl.mics.application.passiveObjects;

import Accessories.VehiclesSemaphore;
import bgu.spl.mics.Future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static class ResourcesHolderHolder {
		private static ResourcesHolder _resourceHolder = new ResourcesHolder();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderHolder._resourceHolder;
	}

	// A collection of delivery vehicles
	private List<DeliveryVehicle> _deliveryVehicles;
	private VehiclesSemaphore _vehiclesSem;

	private ResourcesHolder(){

		_deliveryVehicles = new ArrayList<>();
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = null;
		int vehicleIndex = _vehiclesSem.acquire();
		if(vehicleIndex != -1){
			future = new Future<>();
			future.resolve(_deliveryVehicles.get(vehicleIndex));
		}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		int index = _deliveryVehicles.indexOf(vehicle);
		_vehiclesSem.release(index);
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle vehicle : vehicles)
		    _deliveryVehicles.add(vehicle);
	}

}
