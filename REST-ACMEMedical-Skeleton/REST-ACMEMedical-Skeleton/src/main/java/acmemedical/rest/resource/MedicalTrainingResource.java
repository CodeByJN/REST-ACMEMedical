package acmemedical.rest.resource;

import java.net.URI;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;

import static acmemedical.utility.MyConstants.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class MedicalTrainingResource {

    @EJB
    ACMEMedicalService service;

    @GET
    @PermitAll
    public Response getAllMedicalTrainings() {
        List<MedicalTraining> list = service.getAll(MedicalTraining.class, MedicalTraining.FIND_ALL);
        return Response.ok(list).build();
    }

    @GET
    @PermitAll
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMedicalTrainingById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        MedicalTraining mt = service.getMedicalTrainingById(id);
        return mt == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(mt).build();
    }

    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response addMedicalTraining(MedicalTraining mt, @Context UriInfo uriInfo) {
        MedicalTraining created = service.persistMedicalTraining(mt);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(uri).entity(created).build();
    }

    @PUT
    @RolesAllowed(ADMIN_ROLE)
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, MedicalTraining incoming) {
        MedicalTraining updated = service.updateMedicalTraining(id, incoming);
        return updated == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(updated).build();
    }

    @DELETE
    @RolesAllowed(ADMIN_ROLE)
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        MedicalTraining deleted = service.deleteMedicalTraining(id); // You need this in the service
        return deleted == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(deleted).build();
    }
}
