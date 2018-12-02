package java;

import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class FutureTest {

    Future _future;

    @Before
    public void setUp() throws Exception
    {
        _future = new Future<String>();
    }

    /**
     * checks if future object has been initialized
     */
    @Test
    public void testInitialization()
    {
        assertNotNull(_future);
    }

    /**
     * checks if future object hasn't been resolved yet and its result is still null.
     */
    @Test
    public void get()
    {
        assertNull(_future.get());
    }

    /**
     * checks if future object has been resolved with given object.
     */
    @Test
    public void resolve()
    {
        _future.resolve("Resolved");
        assertEquals("Resolved",_future.get());
    }

    /**
     * checks if future object has not modified the resolved value whenever it already has one.
     */
    @Test
    public void resolveTwice()
    {
        // resolve once
        String result = "Resolved";
        _future.resolve(result);

        // resolve again , Future must deny this one
        _future.resolve("AGAIN!");

        assertEquals(result,_future.get());
    }

    /**
     *  checks if future object has been resolved.
     */
    @Test
    public void isDone()
    {
        Object resultOfFuture = _future.get();
        boolean hasResult = resultOfFuture != null;
        assertEquals(hasResult,_future.isDone());
    }

    /**
     * checks what future object returns according to two scenarios
     * first is when a future object holds a result, it will be returned before the expected time
     * second is when a future object does not hold a result and the waiting time passed, the result must to be null.
     */
    @Test
    public void getWithTimeArgue()
    {

        // 5 seconds will be converted into seconds in get function of Future object
        long timeoutSeconds = 5L;
        TimeUnit unit = TimeUnit.SECONDS;

        // current time
        long startTime = System.currentTimeMillis();

        Object result = _future.get(timeoutSeconds,unit);

        // elapsed time
        long elapsedTime = System.currentTimeMillis()-startTime;

        // the actual time 'get' function does its calculation
        long givenTime = TimeUnit.SECONDS.convert(timeoutSeconds, TimeUnit.SECONDS);

        if (elapsedTime < givenTime)
            assertNotNull(result);
        else
            assertNull(result);
    }
}