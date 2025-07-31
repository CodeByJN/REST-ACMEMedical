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
import acmemedical.entity.PrescriptionPK;

@Path("/prescriptions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {

    @EJB
    ACMEMedicalService service;

    @GET
    @RolesAllowed(ADMIN_ROLE)
    public Response getAll() {
        List<Prescription> prescriptions = service.getAll(Prescription.class, "Prescription.findAll");
        return Response.ok(prescriptions).build();
    }

    @GET
    @Path("/{physicianId}/{patientId}")
    @RolesAllowed(ADMIN_ROLE)
    public Response get(@PathParam("physicianId") int physicianId,
                        @PathParam("patientId") int patientId) {
        PrescriptionPK id = new PrescriptionPK(physicianId, patientId);
        Prescription found = service.getPrescriptionById(id);
        return (found == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(found).build();
    }

    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response create(Prescription p, @Context UriInfo uri) {
        Prescription created = service.persistPrescription(p);
        URI loc = uri.getAbsolutePathBuilder().build();
        return Response.created(loc).entity(created).build();
    }

    @PUT
    @Path("/{physicianId}/{patientId}")
    @RolesAllowed(ADMIN_ROLE)
    public Response update(@PathParam("physicianId") int physicianId,
                           @PathParam("patientId") int patientId,
                           Prescription updated) {
        PrescriptionPK id = new PrescriptionPK(physicianId, patientId);
        updated.setId(id);
        Prescription modified = service.updatePrescription(id, updated);
        return (modified == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(modified).build();
    }

    @DELETE
    @Path("/{physicianId}/{patientId}")
    @RolesAllowed(ADMIN_ROLE)
    public Response delete(@PathParam("physicianId") int physicianId,
                           @PathParam("patientId") int patientId) {
        PrescriptionPK id = new PrescriptionPK(physicianId, patientId);
        boolean success = service.deletePrescription(id);
        return success
            ? Response.noContent().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}
