package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.*;

import java.net.URI;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Prescription;

@Path("/prescriptions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {

    @EJB
    ACMEMedicalService service;

    // Only ADMIN can get all prescriptions
    @GET
    @RolesAllowed(ADMIN_ROLE)
    public Response getAll() {
        List<Prescription> items = service.getAll(Prescription.class, "Prescription.findAll");
        return Response.ok(items).build();
    }

    // Only ADMIN can create prescriptions
    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response create(Prescription p, @Context UriInfo uri) {
        Prescription created = service.persistPrescription(p);
        URI loc = uri.getAbsolutePathBuilder().build();  // can't point to exact ID without PK class
        return Response.created(loc).entity(created).build();
    }

    // Only ADMIN can update prescriptions
    @PUT
    @RolesAllowed(ADMIN_ROLE)
    public Response update(Prescription incoming) {
        Prescription updated = service.updatePrescription(incoming);
        return (updated == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(updated).build();
    }

    // Only ADMIN can delete prescriptions
    @DELETE
    @RolesAllowed(ADMIN_ROLE)
    public Response delete(Prescription toDelete) {
        boolean success = service.deletePrescription(toDelete);
        return success
            ? Response.noContent().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}
