package java;

import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class MessageBusImplTest {

    MessageBusImpl _messageBus;
    MicroService _microServiceEvent;
    MicroService _microServiceBroadcast;

    @Before
    public void setUp() throws Exception
    {
        _messageBus = MessageBusImpl.getInstance();
        _microServiceEvent = new ExampleEventHandlerService("exampleMicroService1", new String[]{"3"});
        _microServiceBroadcast = new ExampleBroadcastListenerService("exampleMicroService2", new String[]{"2"});
    }

    /**
     * Test which checks if the singleton implementation is correct and an instance was created.
     */
    @Test
    public void testGetInstance()
    {
        Assert.assertNotNull(_messageBus);
    }


    /**
     * checks if MessageBusImp subscribes a MicroService for an event message
     */
    @Test
    public void subscribeEvent()
    {
        Event event = registerAndSubscribeEvent (_microServiceEvent);

        // sendEvent forces micro service to work on the event
        _messageBus.sendEvent(event);

        Message afterSubscribtion = null;
        try
        {
            afterSubscribtion = _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e) {}
        // if successfully subscribed, after sending the event, the micro service might find it in its queue
        // since only one event was sent, it surely be it, so we check it isn't null
        assertNotNull(afterSubscribtion);
    }

    /**
     * checks if MessageBusImp subscribes a MicroService for an event message
     */
    @Test
    public void subscribeEvent_twice()
    {
        Event event = registerAndSubscribeEvent (_microServiceEvent);

        // sendEvent forces micro service to work on the event
        _messageBus.sendEvent(event);

        // micro service subscription
        String microServiceName = _microServiceEvent.getName();
        ExampleEvent event2 = new ExampleEvent(microServiceName);
        Class<? extends Event<String>> type = event2.getClass();
        _messageBus.subscribeEvent(type, _microServiceEvent);

        _messageBus.sendEvent(event2);

        Message afterSubscribtion = null;
        try
        {
            afterSubscribtion = _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e) {}
        // if successfully subscribed, after sending the event, the micro service might find it in its queue
        // this check verifies that the returned one is the first was sent
        assertEquals(event,afterSubscribtion);
    }

    /**
     * checks if MessageBusImp subscribes a MicroService for a broadcast message
     */
    @Test
    public void subscribeBroadcast()
    {
        Broadcast broadcast = registerAndSubscribeBroadcast (_microServiceBroadcast);

        // sendEvent forces micro service to work on the event
        _messageBus.sendBroadcast(broadcast);

        Message afterSubscribtion = null;
        //we need to check if the micro service successfully subscribed the broadcast
        //if _microService1 is subscribed to the broadcast, the awaitMessage method should return the same broadcast.
        try // it may not fail since the micro service is registered
        {
            afterSubscribtion = _messageBus.awaitMessage(_microServiceBroadcast);

        }   catch (InterruptedException e) {}
        // if the micro service got the message then it was successfully subscribed
        assertEquals(broadcast,afterSubscribtion);
    }

    /**
     *  the complete message resolves the future object associated with a given event parameter
     *  in this test we check if complete really solved the correct event
     */
    @Test
    public void complete_solvedCheck()
    {
        Event event = registerAndSubscribeEvent (_microServiceEvent);

        // we send this event to be solved and keep the returned value
        Future future = _messageBus.sendEvent(event);

        String result = "solved";
        // assuming another micro service has completed the event
        _messageBus.complete(event,result);

        assertTrue(future.get() instanceof String);

    }

    /**
     *  the complete message resolves the future object associated with a given event parameter
     *  in this test we check if complete really solved event into the correct object
     */
    @Test
    public void complete_valueCheck()
    {
        Event event = registerAndSubscribeEvent (_microServiceEvent);

        // we send this event to be solved and keep the returned value
        Future future = _messageBus.sendEvent(event);

        String result = "solved";
        // assuming another micro service has completed the event
        _messageBus.complete(event,result);

        assertEquals(result,future.get());

    }

    /**
     *  whenever a micro service is subscribed to a broadcast message,
     *  once the same type of message is sent, it will get the message
     */
    @Test
    public void sendBroadcast()
    {
        Broadcast broadcast = registerAndSubscribeBroadcast (_microServiceBroadcast);

        // we send this event to be solved and keep the returned value
        _messageBus.sendBroadcast(broadcast);

        Message afterSubscribe = null;
        // if the broadcast was successfully sent, so the micro service should find it in its queue
        try // it may not fail since the micro service is registered
        {
            afterSubscribe = _messageBus.awaitMessage(_microServiceBroadcast);

        }   catch (InterruptedException e) {}

        assertNotNull(afterSubscribe);
    }

    /**
     *  In this test we check if the following scenario
     *  leads to the correct returned object of SendEvent method
     */
    @Test
    public void sendEvent()
    {
        Event event = registerAndSubscribeEvent (_microServiceEvent);
        // we send this event to be solved and keep the returned value
        Future future = _messageBus.sendEvent(event);

        try{
            _messageBus.awaitMessage(_microServiceEvent);
        } catch (InterruptedException e) {}

        // the returned value, which is future, holds our result, which must be String as defined in ExampleEvent
        assertTrue(future.get() instanceof String);
    }

    /**
     *  In this test we check if the following scenario,
     *  leads to the correct returned object of SendEvent method
     *  in this case we test unregistered micro service, a test which must lead to null result
     */
    @Test
    public void sendEvent_null()
    {
        // we send this event to be solved and keep the returned value
        Future future = _messageBus.sendEvent(new ExampleEvent("no one"));
        assertNull(future);
    }

    /**
     *  this check verifies that a certain micro service was successfully registered
     */
    @Test
    public void register()
    {
        Event event = registerAndSubscribeEvent(_microServiceEvent);

        // an event is sent so the awaitMessage method won't be waiting for an event to appear
        _messageBus.sendEvent(event);

        boolean registered = true;
        try
        {
            _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e)
        {   // if the micro service is not registered an exception will be thrown
            registered = false;
        }
        assertTrue(registered);
    }

    /**
     *  this check verifies that a certain micro service was successfully unregistered
     */
    @Test
    public void unregister()
    {
        _messageBus.unregister(_microServiceEvent);

        boolean notRegistered = false;
        try
        {
            _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e)
        {   // if the micro service is not registered an exception will be thrown
            notRegistered = true;
        }
        assertTrue(notRegistered);
    }

    /**
     *  using this check we will verify that the awaitMessage method does really return expected value
     *  whenever an awaitMessage method is called, it must return the next message
     *  so we assume there's a message in a micro service's queue and it was solved
     *  this scenario must lead the awaitMessage to return the next message
     *  which must not be null
     */
    @Test
    public void awaitMessage_withEvent()
    {
        Event event = registerAndSubscribeEvent(_microServiceEvent);

        _messageBus.sendEvent(event);

        Message afterSubscribe = null;
        try
        {
            afterSubscribe = _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e) {}

        assertEquals(event,afterSubscribe);
    }

    /**
     *  Whenever an awaitMessage is called with unregistered micro service,
     *  it must throw an exception, so in this test we check that an exception is thrown
     */
    @Test
    public void awaitMessage_Unregistered()
    {
        boolean awaitMessageCrashed = false;
        try
        {
            _messageBus.awaitMessage(_microServiceEvent);

        }   catch (InterruptedException e)
        {
            awaitMessageCrashed = true;
        }
        assertTrue(awaitMessageCrashed);
    }

    private Event registerAndSubscribeEvent (MicroService m)
    {
        _messageBus.register(_microServiceEvent);
        // micro service subscription
        String microServiceName = _microServiceEvent.getName();
        ExampleEvent event = new ExampleEvent(microServiceName);
        Class<? extends Event<String>> type = event.getClass();
        _messageBus.subscribeEvent(type, _microServiceEvent);

        return event;
    }

    private Broadcast registerAndSubscribeBroadcast (MicroService m)
    {
        _messageBus.register(_microServiceEvent);
        // micro service subscription
        String microServiceName = _microServiceEvent.getName();
        ExampleBroadcast broadcast = new ExampleBroadcast(microServiceName);
        _messageBus.subscribeBroadcast(broadcast.getClass(), _microServiceEvent);

        return broadcast;
    }
}