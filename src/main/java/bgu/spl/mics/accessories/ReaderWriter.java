package bgu.spl.mics.accessories;

public abstract class ReaderWriter<T> {

    protected int _activeReaders;
    protected int _activeWriters;
    protected int _waitingWriters;

    public void read(){
        beforeRead();
        read1();
        afterRead();
    }

    protected synchronized void beforeRead() {
        while(!allowRead()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _activeReaders ++;

    }

    protected abstract void read1();

    protected synchronized void afterRead() {
        _activeReaders -- ;
        notifyAll();
    }

    private boolean allowRead() {
        return _activeWriters == 0 && _waitingWriters == 0;
    }

    public void write() {
        beforeWrite();
        write1();
        afterWrite();
    }

    protected synchronized void beforeWrite() {
        _waitingWriters ++;
        while (!allowWrite()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _waitingWriters --;
        _activeWriters ++;
    }

    protected abstract void write1();

    protected synchronized void afterWrite() {
        _activeWriters --;
        notifyAll();
    }

    private boolean allowWrite() {
        return _activeWriters == 0;
    }





}
