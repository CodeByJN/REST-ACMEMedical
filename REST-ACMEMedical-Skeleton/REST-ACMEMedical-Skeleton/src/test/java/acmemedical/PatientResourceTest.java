package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class PatientResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/patient";
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
    void adminCanGetAllPatients() {
        Response resp = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, resp.getStatus());
    }

    @Test
    void userCannotGetAllPatients() {
        Response resp = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanCreatePatient() {
        String json = "{\"firstName\":\"Test\",\"lastName\":\"Patient\",\"year\":1999}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertEquals(200, resp.getStatus());
    }

    @Test
    void adminCanGetPatientById() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).get();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void userCannotCreatePatient() {
        String json = "{\"firstName\":\"Nope\",\"lastName\":\"User\",\"year\":1988}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void userCannotGetOtherPatientById() {
        Response resp = authRequest(BASE_URI + "/9999", normalUser, normalPass).get();
        assertTrue(resp.getStatus() == 403 || resp.getStatus() == 404);
    }

    @Test
    void adminCanDeletePatient() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).delete();
        assertTrue(resp.getStatus() == 204 || resp.getStatus() == 404);
    }
}