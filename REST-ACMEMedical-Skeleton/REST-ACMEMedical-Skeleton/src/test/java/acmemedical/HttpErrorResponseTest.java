package acmemedical;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import acmemedical.rest.resource.HttpErrorResponse;

public class HttpErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        int code = 409;
        String reason = "Entity already exists";

        HttpErrorResponse err = new HttpErrorResponse(code, reason);

        assertEquals(code, err.getStatusCode());
        assertEquals(reason, err.getReasonPhrase());
    }

    @Test
    void testNegativeCodeAndNullReason() {
        HttpErrorResponse err = new HttpErrorResponse(-1, null);
        assertEquals(-1, err.getStatusCode());
        assertNull(err.getReasonPhrase());
    }
}
