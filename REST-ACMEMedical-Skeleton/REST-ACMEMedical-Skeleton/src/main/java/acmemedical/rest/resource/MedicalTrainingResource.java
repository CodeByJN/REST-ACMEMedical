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

    //Any user can list all MedicalTraining
    @GET
    @PermitAll
    public Response list() {
        List<MedicalTraining> items = service.getAll(MedicalTraining.class, "MedicalTraining.findAll");
        return Response.ok(items).build();
    }

    //Any user can get one MedicalTraining
    @GET
    @PermitAll
    @Path(RESOURCE_PATH_ID_PATH)
    public Response get(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        MedicalTraining mt = service.getMedicalTrainingById(id);
        return (mt == null) ? Response.status(Response.Status.NOT_FOUND).build()
                            : Response.ok(mt).build();
    }

    //Only ADMIN can create
    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response create(MedicalTraining mt, @Context UriInfo uri) {
        MedicalTraining created = service.persistMedicalTraining(mt);
        URI loc = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(loc).entity(created).build();        // 201 + Location
    }

    //Only ADMIN can update
    @PUT
    @RolesAllowed(ADMIN_ROLE)
    @Path(RESOURCE_PATH_ID_PATH)
    public Response update(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, MedicalTraining incoming) {
        MedicalTraining updated = service.updateMedicalTraining(id, incoming);
        return (updated == null) ? Response.status(Response.Status.NOT_FOUND).build()
                                 : Response.ok(updated).build();
    }

    //Only ADMIN can delete — service doesn’t have a delete so I left it like this
    @DELETE
    @RolesAllowed(ADMIN_ROLE)
    @Path(RESOURCE_PATH_ID_PATH)
    public Response delete(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED)
                       .entity("TODO: implement deleteMedicalTraining(int) in ACMEMedicalService").build();
    }
}
