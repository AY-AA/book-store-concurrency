package testers;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    MessageBusImpl messageBus;
    MicroService microService;

    @Before
    public void setUp() throws Exception
    {
        messageBus = MessageBusImpl.getInstance();
        microService = new ExampleBroadcastListenerService("exampleMicroService1", new String[]{"3"});
    }

    @Test
    public void subscribeEvent() {
    }

    @Test
    public void subscribeBroadcast() {
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