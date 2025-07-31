package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.*;

import java.net.URI;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;

@Path("/medicines")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicineResource {

    @EJB
    ACMEMedicalService service;

    //Any user can view all medicines
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMedicines() {
        List<Medicine> medicines = service.getAll(Medicine.class, "Medicine.findAll");
        return Response.ok(medicines).build();
    }

    //Any user can view a specific medicine
    @GET
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicineById(@PathParam("id") int id) {
        Medicine medicine = service.getById(Medicine.class, "Medicine.findById", id);
        return (medicine == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(medicine).build();
    }

    //Only ADMIN can create
    @POST
    @RolesAllowed(ADMIN_ROLE)
    public Response createMedicine(Medicine medicine, @Context UriInfo uri) {
        Medicine created = service.persistMedicine(medicine);
        URI location = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }

    //Only ADMIN can update
    @PUT
    @Path("/{id}")
    @RolesAllowed(ADMIN_ROLE)
    public Response updateMedicine(@PathParam("id") int id, Medicine updated) {
        Medicine changed = service.updateMedicine(id, updated);
        return (changed == null)
            ? Response.status(Response.Status.NOT_FOUND).build()
            : Response.ok(changed).build();
    }

    //Only ADMIN can delete
    @DELETE
    @Path("/{id}")
    @RolesAllowed(ADMIN_ROLE)
    public Response deleteMedicine(@PathParam("id") int id) {
        boolean success = service.deleteMedicine(id);
        return success
            ? Response.noContent().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}
