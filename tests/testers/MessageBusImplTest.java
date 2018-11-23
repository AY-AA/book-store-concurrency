package testers;

import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageBusImplTest {

    MessageBusImpl _messageBus;
    MicroService _microService1;    // does 3 tasks
    MicroService _microService2;    // does only one task

    @Before
    public void setUp() throws Exception
    {
        _messageBus = MessageBusImpl.getInstance();
        _microService1 = new ExampleBroadcastListenerService("exampleMicroService1", new String[]{"3"});
        _microService2 = new ExampleBroadcastListenerService("exampleMicroService2", new String[]{"1"});
    }

    /**
     * Test which checks if the singleton implementation is correct and an instance was created.
     */
    @Test
    public void testGetInstance()
    {
        Assert.assertNotNull(_messageBus);
    }

    @Test
    public void subscribeEvent()
    {

        String microServiceName = _microService1.getName();
        ExampleEvent event = new ExampleEvent(microServiceName);
        Class<? extends Event<String>> type = event.getClass();
        _messageBus.subscribeEvent(type,_microService1);








    }

    @Test
    public void subscribeBroadcast() {

//        Broadcast taskCompleted = new ExampleBroadcast("1");
//        _microService1.
//
//
//
//        String microServiceName = _microService1.getName();
//        Broadcast broadcast = new ExampleBroadcast("");
//        Class <? extends Broadcast> type = broadcast.getClass();
//        _messageBus.subscribeBroadcast(type , _microService1);

    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }


}