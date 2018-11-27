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


    // TODO : think of other object which can hold more than one value for each key
//    // this hash map represents each subscription type and the micro services subscribed to it
//    private HashMap<Message,MicroService> _messagesSubscriptions;

	public static MessageBusImpl getInstance()
    {
        if (messageBus == null)
            messageBus = new MessageBusImpl();
        return messageBus;
    }

    private MessageBusImpl()
    {
        _messagesQueues = new HashMap<>();
	};

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
	    Vector<Message> messages = new Vector<>();
        _messagesQueues.put(m,messages);
	}

	@Override
	public void unregister(MicroService m) {
	    if (_messagesQueues.containsKey(m))
            _messagesQueues.put(m,null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
