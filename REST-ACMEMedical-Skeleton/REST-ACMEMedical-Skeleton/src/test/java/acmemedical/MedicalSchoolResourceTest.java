package acmemedical;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

public class MedicalSchoolResourceTest {

    static final String BASE_URI = "http://localhost:8080/rest-acmemedical/api/v1/medicalschool";
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
    void anyoneCanGetAllMedicalSchools() {
        Response respAdmin = authRequest(BASE_URI, adminUser, adminPass).get();
        assertEquals(200, respAdmin.getStatus());
        Response respUser = authRequest(BASE_URI, normalUser, normalPass).get();
        assertEquals(200, respUser.getStatus());
    }

    @Test
    void adminOrUserCanGetMedicalSchoolById() {
        Response respAdmin = authRequest(BASE_URI + "/1", adminUser, adminPass).get();
        assertTrue(respAdmin.getStatus() == 200 || respAdmin.getStatus() == 404);
        Response respUser = authRequest(BASE_URI + "/1", normalUser, normalPass).get();
        assertTrue(respUser.getStatus() == 200 || respUser.getStatus() == 404);
    }

    @Test
    void adminCanCreateMedicalSchool() {
        String json = "{\"schoolName\":\"Algonquin Medical\",\"address\":\"Ottawa\"}";
        Response resp = authRequest(BASE_URI, adminUser, adminPass).post(Entity.json(json));
        assertEquals(200, resp.getStatus());
    }

    @Test
    void userCannotCreateMedicalSchool() {
        String json = "{\"schoolName\":\"FakeUserMed\",\"address\":\"Nowhere\"}";
        Response resp = authRequest(BASE_URI, normalUser, normalPass).post(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanUpdateMedicalSchool() {
        String json = "{\"schoolName\":\"Algonquin Medical Updated\",\"address\":\"Ottawa\"}";
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).put(Entity.json(json));
        assertTrue(resp.getStatus() == 200 || resp.getStatus() == 404);
    }

    @Test
    void userCannotUpdateMedicalSchool() {
        String json = "{\"schoolName\":\"FakeUpdate\",\"address\":\"Nope\"}";
        Response resp = authRequest(BASE_URI + "/1", normalUser, normalPass).put(Entity.json(json));
        assertEquals(403, resp.getStatus());
    }

    @Test
    void adminCanDeleteMedicalSchool() {
        Response resp = authRequest(BASE_URI + "/1", adminUser, adminPass).delete();
        assertEquals(200, resp.getStatus()); 
    }
}
