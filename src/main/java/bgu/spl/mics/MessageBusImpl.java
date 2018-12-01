package bgu.spl.mics;

import java.util.HashMap;
import java.util.Vector;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl messageBus;

    // this hash map represents each micro service and its queue
	// whenever a micro service's message vector is null, it means it has been unregistered
    private HashMap<MicroService, Vector<Message>> _messagesQueues;

    // this hash map represents messages as keys
    // and the value of each message is the array list which holds all micro services subscribed to a certain message
    private HashMap<Object,Vector<MicroService> >_messagesSubscriptions;

    // this hash map represents messages as keys
    // and the value of each message is the future object represents the result might become out of the event
    private HashMap<Message,Future> _messagesAndFutures;

    // this hash map represents messages as keys
    // and the value of each message is the last index of micro service that got the message
    private HashMap<Object,Integer> _roundRobinNum;

    public static MessageBusImpl getInstance()
    {
        if (messageBus == null)
            messageBus = new MessageBusImpl();
        return messageBus;
    }

    private MessageBusImpl()
    {
        _messagesQueues = new HashMap<>();
        _messagesSubscriptions = new HashMap<>();
        _messagesAndFutures = new HashMap<>();
        _roundRobinNum = new HashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        // if micro service is not registered, return
        if (!_messagesSubscriptions.containsKey(m))
            return;
        // if event does not exist, add it
		if (!_messagesSubscriptions.containsKey(type))
        {
            _messagesSubscriptions.put(type,new Vector<>());
            _roundRobinNum.put(type,0);
        }
        // subscribe m to the event
        _messagesSubscriptions.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        // if broadcast does not exist, add it
        if (!_messagesSubscriptions.containsKey(type))
        {
            _roundRobinNum.put(type,0);
            _messagesSubscriptions.put(type,new Vector<>());
        }
        // subscribe m to the broadcast
        _messagesSubscriptions.get(type).add(m);

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
        Vector<MicroService> microServices = _messagesSubscriptions.get(b);

        // for each micro service subscribed to b we insert the message b into its list
        for (MicroService m : microServices)
        {
            Vector<Message> currMsgVec = _messagesQueues.get(m);
            if (currMsgVec != null)     // null means the micro service is not registered
                currMsgVec.add(b);
        }
    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e)
    {
        MicroService m = roundRobinAlgo(e);
        if (m == null)  // in case there is no micro service matching this event
            return null;

        // event addition
        _messagesQueues.get(m).add(e);

		Future<T> future = new Future<>();
		_messagesAndFutures.put(e,future);

        return future;
    }


    /**
     * Finds the micro service that is going to get the {@code e} event and return it
     * @param e
     * @param <T>
     * @return
     */
    private <T> MicroService roundRobinAlgo(Event<T> e) {
        Vector<MicroService> subscribedToE = _messagesSubscriptions.get(e.getClass());

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
        _messagesQueues.put(m,new Vector<>());
	}

	@Override
	public void unregister(MicroService m)
    {
	    if (!_messagesQueues.containsKey(m))
	        return;
	    // remove m from _messagesSubscriptions
        Vector<Message> messages = _messagesQueues.get(m);
        for (Message msg : messages)
        {
            if (_messagesSubscriptions.containsKey(msg))
            {
                Vector<MicroService> microServices = _messagesSubscriptions.get(msg);
                if (microServices.contains(m))
                    microServices.remove(m);
            }
        }
	    _messagesQueues.put(m,null);

	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException
    {
        Message msg = null;
        Vector<Message> mQueue = null;
        try {
            mQueue = _messagesQueues.get(m);
        }
        catch (IllegalStateException e){
            return null;
        }
//        try{
            while(_messagesQueues.get(m).isEmpty())
            {
//                wait();
            }
            msg = _messagesQueues.get(m).firstElement();
            _messagesQueues.get(m).remove(msg);
//        }
//        catch (InterruptedException e){}
        notifyAll();
        return msg;
	}

	

}
