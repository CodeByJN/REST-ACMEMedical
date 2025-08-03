package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class PhysicianResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/physician";
    static Client client;

    static String adminUser = "admin", adminPass = "admin";
    static String normalUser = "cst8277", normalPass = "8277";

    static Invocation.Builder authRequest(String url, String user, String pass) {
        return client.target(url)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Basic " +
                Base64.getEncoder().encodeToString((user + ":" + pass).getBytes()));
    }

    @BeforeAll static void setup() { client = ClientBuilder.newClient(); }
    @AfterAll static void teardown() { client.close(); }

    @Test
    void adminCanGetAllPhysicians() {
        Response resp = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, resp.getStatus());
    }

    @Test
    void userCannotGetAllPhysicians() {
        Response resp = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanAddPhysician() {
        String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"specialty\":\"General\"}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertEquals(200, resp.getStatus());
    }

    @Test
    void adminCanGetPhysicianById() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).get();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void userCannotAddPhysician() {
        String json = "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"specialty\":\"General\"}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void userCannotGetOtherPhysicianById() {
        Response resp = authRequest(BASE_URI + "/9999", normalUser, normalPass).get();
        assertTrue(resp.getStatus() == 403 || resp.getStatus() == 404);
    }

    @Test
    void adminCanUpdateMedicineForPhysicianPatient() {
        String json = "{\"name\":\"Ibuprofen\",\"dosage\":\"200mg\"}";
        Response resp = authRequest(BASE_URI + "/1/patient/1/medicine", adminUser, adminPass)
            .put(Entity.json(json));
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }
}
