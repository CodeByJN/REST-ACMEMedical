package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class MedicalCertificateResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/certificates";
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
    void adminCanGetAllCertificates() {
        Response resp = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, resp.getStatus());
    }

    @Test
    void userCannotGetAllCertificates() {
        Response resp = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminOrUserCanGetCertificateById() {
        Response respAdmin = authRequest(BASE_URI + "/1", adminUser, adminPass).get();
        assertTrue(respAdmin.getStatus() == 200 || respAdmin.getStatus() == 404);
        Response respUser = authRequest(BASE_URI + "/1", normalUser, normalPass).get();
        assertTrue(respUser.getStatus() == 200 || respUser.getStatus() == 404);
    }

    @Test
    void adminCanCreateCertificate() {
        String json = "{\"name\":\"Fit for work\",\"details\":\"Cleared for duty\"}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertTrue(resp.getStatus() == 201 || resp.getStatus() == 200);
    }

    @Test
    void userCannotCreateCertificate() {
        String json = "{\"name\":\"Fake Cert\",\"details\":\"No perms\"}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanUpdateCertificate() {
        String json = "{\"name\":\"Fit for school\",\"details\":\"OK\"}";
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).put(Entity.json(json));
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void adminCanDeleteCertificate() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).delete();
        assertTrue(resp.getStatus() == 204 || resp.getStatus() == 404);
    }
}
