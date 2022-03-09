import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    private Future f;

    @BeforeEach
    void setUp() {
        f = new Future<String>();
    }

    @Test
    void get() {
        String str = "a";
        f.resolve(str);
        assertEquals(f.get(),str);
    }

    @Test
    void resolve() {
        assertFalse(f.isDone());
        String str = "a";
        f.resolve(str);
        assertEquals(f.get(),str);
        assertTrue(f.isDone());
    }

    @Test
    void isDone() {
        assertFalse(f.isDone());
        String str = "a";
        f.resolve(str);
        assertTrue(f.isDone());
    }

    @Test
    void get2() {
        long Timeout = 1500;
        TimeUnit unit = TimeUnit.MILLISECONDS;

        assertNull(f.get(Timeout,unit));

        Thread k = new Thread(){
            public void run() {
                assertEquals("test",f.get(Timeout,unit));
            }
        };
        k.start();
        f.resolve("test");

        Future fu=new Future<String>();
        Thread d = new Thread(){
            public void run() {
                assertNull(fu.get(Timeout,unit));
            }
        };
        d.run();
    }
}