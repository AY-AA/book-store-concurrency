package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.accessories.VehiclesSemaphore;
import bgu.spl.mics.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	// A queue of futures waiting to get resolved with a vehicle
	private LinkedBlockingQueue<Future<DeliveryVehicle>> _futures;

	private ResourcesHolder(){
		_futures = new LinkedBlockingQueue<>();
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
		if (_vehiclesSem == null || _deliveryVehicles.size() == 0)	// in case semaphore has not initialized yet or there are no cars
			return null;
		Future<DeliveryVehicle> future = new Future<>();
		Future<Integer> tryAcquireFuture = new Future<>();
		boolean canBeAcquired = _vehiclesSem.tryAcquire(tryAcquireFuture);
		if (canBeAcquired)    // if can acquire, acquire and resolve
			future.resolve(_deliveryVehicles.get(tryAcquireFuture.get()));
		else {
			try {
				_futures.put(future);
			} catch (InterruptedException e) {
				Thread.currentThread().isInterrupted();
			}
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
	    if (vehicle == null)
        {   // invoke all waiting logistics because the program terminates
            for (Future future : _futures)
                future.resolve(null);
            return;
        }
		if (_futures.isEmpty()) {
			int index = _deliveryVehicles.indexOf(vehicle);
			_vehiclesSem.release(index);
		}
		else{
			try {   // resolve the first one in the queue
				Future f = _futures.take();
				f.resolve(vehicle);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		_deliveryVehicles.addAll(Arrays.asList(vehicles));
		_vehiclesSem = new VehiclesSemaphore(_deliveryVehicles.size());
	}

}
