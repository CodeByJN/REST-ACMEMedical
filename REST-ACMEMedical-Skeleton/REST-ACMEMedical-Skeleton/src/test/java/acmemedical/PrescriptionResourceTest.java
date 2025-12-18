package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class PrescriptionResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/prescriptions";
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
    void adminCanGetAllPrescriptions() {
        Response resp = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, resp.getStatus());
    }

    @Test
    void userCannotGetAllPrescriptions() {
        Response resp = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanCreatePrescription() {
        String json = "{\"physicianId\":1,\"patientId\":2,\"medicine\":\"Ibuprofen\",\"dosage\":\"200mg\"}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertTrue(resp.getStatus() == 201 || resp.getStatus() == 200);
    }

    @Test
    void adminCanGetPrescriptionById() {
        Response resp = authRequest(BASE_URI + "/1/2", adminUser, adminPass).get();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void userCannotCreatePrescription() {
        String json = "{\"physicianId\":1,\"patientId\":3,\"medicine\":\"Paracetamol\",\"dosage\":\"500mg\"}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void userCannotGetOtherPrescriptionById() {
        Response resp = authRequest(BASE_URI + "/9999/9999", normalUser, normalPass).get();
        assertEquals(403, resp.getStatus()); 
    }

    @Test
    void adminCanDeletePrescription() {
        Response resp = authRequest(BASE_URI + "/1/2", adminUser, adminPass).delete();
        assertTrue(resp.getStatus() == 204 || resp.getStatus() == 404);
    }
}
