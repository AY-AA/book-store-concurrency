package testers;

import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.Result;

import static javafx.scene.input.KeyCode.T;
import static org.junit.Assert.*;

public class MessageBusImplTest {

    MessageBusImpl messageBus;
    MicroService microService1;
    MicroService microService2;

    @Before
    public void setUp() throws Exception
    {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new ExampleBroadcastListenerService("exampleMicroService1", new String[]{"3"});
        microService2 = new ExampleBroadcastListenerService("exampleMicroService2", new String[]{"1"});
    }

    @Test
    public void subscribeEvent() throws ClassNotFoundException {
        String msName = microService1.getName();
        ExampleEvent event = new ExampleEvent(msName);
        Class<? extends Event<String>> x = event.getClass();
        messageBus.subscribeEvent(x,microService2);
    }

    @Test
    public void subscribeBroadcast()
    {
    }

    @Test
    public void complete()
    {

    }

    @Test
    public void sendBroadcast()
    {
    }

    @Test
    public void sendEvent()
    {
    }

    @Test
    public void register()
    {
    }

    @Test
    public void unregister()
    {
    }

    @Test
    public void awaitMessage()
    {
    }
}