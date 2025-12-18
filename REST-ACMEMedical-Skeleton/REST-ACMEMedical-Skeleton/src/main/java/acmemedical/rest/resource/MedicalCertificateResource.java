package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.*;

import java.net.URI;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalCertificate;

@Path("/certificates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalCertificateResource {

    @EJB
    protected ACMEMedicalService service;

    // ADMIN_ROLE can view all certificates
    @GET
    @RolesAllowed(ADMIN_ROLE)
    public Response getAllCertificates() {
        List<MedicalCertificate> certs = service.getAll(MedicalCertificate.class, "MedicalCertificate.findAll");
        return Response.ok(certs).build();
    }

    // ADMIN_ROLE or USER_ROLE can get one certificate
    @GET
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getCertificateById(@PathParam("id") int id) {
        MedicalCertificate mc = service.getById(MedicalCertificate.class, "MedicalCertificate.findById", id);
        return (mc == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(mc).build();
    }

    //ADMIN_ROLE can create new certificate
    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response createCertificate(MedicalCertificate mc, @Context UriInfo uri) {
        MedicalCertificate created = service.persistMedicalCertificate(mc);
        URI location = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }

    //ADMIN_ROLE can update
    @PUT
    @Path("/{id}")
    @RolesAllowed(ADMIN_ROLE)
    public Response updateCertificate(@PathParam("id") int id, MedicalCertificate updated) {
        MedicalCertificate changed = service.updateMedicalCertificate(id, updated);
        return (changed == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(changed).build();
    }

    //ADMIN_ROLE can delete
    @DELETE
    @Path("/{id}")
    @RolesAllowed(ADMIN_ROLE)
    public Response deleteCertificate(@PathParam("id") int id) {
        boolean deleted = service.deleteMedicalCertificate(id);
        return deleted
            ? Response.noContent().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}
