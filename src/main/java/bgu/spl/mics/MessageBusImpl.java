package bgu.spl.mics;

import java.util.ArrayList;
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
    private HashMap<Message,Vector<MicroService> >_messagesSubscriptions;

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
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
		if (_messagesSubscriptions.containsKey(type))
        {
            // finding the event type
//            Vector<MicroService> typesList = _messagesSubscriptions.get(type);
//            typesList.add(m);
        }
        else
        {
//            Vector<MicroService> typesList = new Vector<>();
//            typesList.add(m);
            // adding the event type
//            _messagesSubscriptions.put(type,typesList);
        }
        // return the T object
//        type.get

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result)
    {
		// TODO Auto-generated method stub

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
            if (currMsgVec != null)
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

		// TODO goes to sleep ?? what happens til the micro service finished tasking
		return new Future<T>();
	}


    /**
     * Finds the micro service that is going to get the {@code e} event and return it
     * @param e
     * @param <T>
     * @return
     */
    private <T> MicroService roundRobinAlgo(Event<T> e) {
        // TODO : implement round-robin fashion
        MicroService m = null;
        return m;
	}

    @Override
	public void register(MicroService m)
    {
	    Vector<Message> messages = new Vector<>();
        _messagesQueues.put(m,messages);
	}

	@Override
	public void unregister(MicroService m)
    {
	    if (_messagesQueues.containsKey(m))
            _messagesQueues.put(m,null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException
    {
        Message msg = null;
        if (_messagesQueues.containsKey(m))
        {
            Vector<Message> mQueue = _messagesQueues.get(m);
            if (mQueue != null && !mQueue.isEmpty())
            {
                msg = mQueue.firstElement();
                mQueue.remove(msg);
            }
        }
		return msg;
	}

	

}
