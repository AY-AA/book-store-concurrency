package bgu.spl.mics;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


    private static class MessageBusImplHolder {
        private static MessageBusImpl _messageBus = new MessageBusImpl();
    }

    // this hash map represents each micro service and its queue
    // whenever a micro service's message vector is null, it means it has been unregistered
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> _messagesQueues;

    // this hash map represents events as keys
    // and the value of each event is the array list which holds all micro services subscribed to a certain event
    private final ConcurrentHashMap<Class,Vector<MicroService> > _eventSubscriptions;

    // this hash map represents broadcasts as keys
    // and the value of each broadcast is the array list which holds all micro services subscribed to a certain broadcast
    private final ConcurrentHashMap<Class,Vector<MicroService> >_broadcastSubscriptions;

    // this hash map represents messages as keys
    // and the value of each message is the future object represents the result might become out of the event
    private ConcurrentHashMap<Message,Future> _messagesAndFutures;

    // this hash map represents messages as keys
    // and the value of each message is the last index of micro service that got the message
    private HashMap<Class,Integer> _roundRobinNum;

    public static MessageBusImpl getInstance()
    {
        return MessageBusImplHolder._messageBus;
    }

    private MessageBusImpl()
    {
        _messagesQueues = new ConcurrentHashMap<>();
        _eventSubscriptions = new ConcurrentHashMap<>();
        _broadcastSubscriptions = new ConcurrentHashMap<>();
        _messagesAndFutures = new ConcurrentHashMap<>();
        _roundRobinNum = new HashMap<>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        synchronized (_eventSubscriptions) {
            // if micro service is not registered, return
            if (!_messagesQueues.containsKey(m) || _messagesQueues.get(m) == null)
                return;
            // if event does not exist, add it
            if (!_eventSubscriptions.containsKey(type)) {
                _eventSubscriptions.put(type, new Vector<>());
                _roundRobinNum.put(type, 0);
            }
        }
        // subscribe m to the event
        _eventSubscriptions.get(type).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        synchronized(_broadcastSubscriptions) {
            // if micro service is not registered, return
            if (!_messagesQueues.containsKey(m) || _messagesQueues.get(m) == null)
                return;
            // if broadcast does not exist, add it
            if (!_broadcastSubscriptions.containsKey(type)) {
                _roundRobinNum.put(type, 0);
                _broadcastSubscriptions.put(type, new Vector<>());
            }
        }
        // subscribe m to the broadcast
        _broadcastSubscriptions.get(type).add(m);

    }

    @Override
    public <T> void complete(Event<T> e, T result)
    {
        Future future = _messagesAndFutures.get(e);
        if (future != null)
            future.resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b)
    {
        // at first we need to find all micro services subscribed to b
        Vector<MicroService> microServices = _broadcastSubscriptions.get(b.getClass());
        synchronized (microServices) {
            // for each micro service subscribed to b we insert the message b into its list
            for (MicroService m : microServices) {
                LinkedBlockingQueue<Message> currMsgVec = _messagesQueues.get(m);
                if (currMsgVec == null)     // null means the micro service is not registered
                    continue;
                try {
                    currMsgVec.put(b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            microServices.notifyAll();
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e)
    {
        MicroService m = roundRobinAlgo(e);
        if (m == null)  // in case there is no micro service matching this event
            return null;

        // event addition
        LinkedBlockingQueue<Message> mQueue = _messagesQueues.get(m);
        if (mQueue == null)
            return null;
        synchronized (mQueue) {
            Future<T> future = new Future<>();
            _messagesAndFutures.put(e, future);
            try {
                mQueue.put(e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            mQueue.notifyAll();
            return future;
        }
    }


    /**
     * Finds the micro service that is going to get the {@code e} event and return it
     * @param e
     * @param <T>
     * @return
     */
    private <T> MicroService roundRobinAlgo(Event<T> e) {
        Vector<MicroService> subscribedToE = _eventSubscriptions.get(e.getClass());

        // if no one is registered to this event or micro service has been unregistered
        if (subscribedToE == null || subscribedToE.isEmpty())
            return null;

        // the last index of the micro service that was sent
        int currMicroService = _roundRobinNum.get(e.getClass());
        int numOfMicroServices = subscribedToE.size();

        // we check size again because a micro service could be unregistered
        currMicroService = currMicroService % numOfMicroServices;
        MicroService m = subscribedToE.get(currMicroService);

        // modulo again, if number is bigger than size
        currMicroService = (currMicroService + 1) % numOfMicroServices;
        _roundRobinNum.put(e.getClass(),currMicroService);
        return m;
    }

    @Override
    public void register(MicroService m)
    {
        _messagesQueues.put(m,new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m)
    {
        if (!_messagesQueues.containsKey(m))
            return;
        // remove m from _eventSubscriptions && _broadcastSubscriptions
        for (Vector<MicroService> currVector : _eventSubscriptions.values())
        {
            synchronized (currVector) {
                currVector.remove(m);
                currVector.notifyAll();
            }
        }
        for (Vector<MicroService> currVector : _broadcastSubscriptions.values())
        {
            synchronized (currVector) {
                currVector.remove(m);
                currVector.notifyAll();
            }
        }
        _messagesQueues.remove(m);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        Message msg = null;
        LinkedBlockingQueue<Message> mQueue = null;
        try {
            mQueue = _messagesQueues.get(m);
        } catch (IllegalStateException e) {
            return null;
        }
        if (mQueue == null)
            return null;
        try {
            msg = _messagesQueues.get(m).take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return msg;
    }
}