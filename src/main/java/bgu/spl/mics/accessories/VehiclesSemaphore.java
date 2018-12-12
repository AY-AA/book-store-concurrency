package bgu.spl.mics.accessories;

public class VehiclesSemaphore  {

    private final int _permits;
    private int _free;
    private boolean[] _aqcuiredVehicles;

    public VehiclesSemaphore(int numOfVehicles){
        _permits = numOfVehicles;
        _free = numOfVehicles;
        _aqcuiredVehicles = new boolean[numOfVehicles];
    }

    public synchronized int acquire(){
        int index = -1;
        while(_free == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i < _aqcuiredVehicles.length; i++){
            if(!_aqcuiredVehicles[i]) {
                _aqcuiredVehicles[i] = !_aqcuiredVehicles[i];
                index = i;
                break;
            }
        }
        _free--;
        return index;
    }

    public synchronized void release(int indexToRelease){
        if(_free <= _permits){
            _free++;
            _aqcuiredVehicles[indexToRelease] = false;
            notify();
        }
    }

    public synchronized boolean tryAcquire(){
        if(_free == 0)
            return false;
        _free--;
        return true;
    }

}
