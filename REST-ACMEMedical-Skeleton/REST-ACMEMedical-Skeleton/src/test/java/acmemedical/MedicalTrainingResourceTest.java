package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class MedicalTrainingResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/medicaltraining";
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
    void anyoneCanGetAllMedicalTrainings() {
        Response resp = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, resp.getStatus());
        resp = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(200, resp.getStatus());
    }

    @Test
    void anyoneCanGetMedicalTrainingById() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).get();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
        resp = authRequest(BASE_URI + "/1", normalUser, normalPass).get();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void adminCanCreateMedicalTraining() {
        String json = "{\"trainingName\":\"Residency\",\"description\":\"Some desc\"}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertTrue(resp.getStatus() == 201 || resp.getStatus() == 200);
    }

    @Test
    void userCannotCreateMedicalTraining() {
        String json = "{\"trainingName\":\"NotAllowed\",\"description\":\"Test\"}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanUpdateMedicalTraining() {
        String json = "{\"trainingName\":\"ResidencyUpdated\",\"description\":\"Updated desc\"}";
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).put(Entity.json(json));
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void userCannotUpdateMedicalTraining() {
        String json = "{\"trainingName\":\"FakeUpdate\",\"description\":\"Should fail\"}";
        Response resp = authRequest(BASE_URI + "/1", normalUser, normalPass).put(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanDeleteMedicalTraining() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).delete();
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }
}
