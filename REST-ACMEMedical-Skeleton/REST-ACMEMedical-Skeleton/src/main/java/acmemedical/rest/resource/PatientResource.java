/********************************************************************************************************
 * File:  PatientResource.java Course Materials CST 8277
 *
 * @author Your Name
 * @date   2025-07-31
 */

package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.PATIENT_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Patient;
import acmemedical.entity.SecurityUser;

@Path(PATIENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    // Only ADMIN_ROLE can view all patients
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPatients() {
        LOG.debug("retrieving all patients ...");
        List<Patient> patients = service.getAllPatients();
        return Response.ok(patients).build();
    }

    // ADMIN_ROLE or USER_ROLE can view a specific patient
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPatientById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("retrieving patient {}", id);
        Patient patient = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            patient = service.getPatientById(id);
            return (patient == null) ? Response.status(Status.NOT_FOUND).build()
                                     : Response.ok(patient).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            patient = sUser.getPatient();
            if (patient != null && patient.getId() == id) {
                return Response.ok(patient).build();
            } else {
                throw new ForbiddenException("User trying to access a patient they do not own.");
            }
        }

        return Response.status(Status.BAD_REQUEST).build();
    }

    // Only ADMIN_ROLE can add new patients
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response createPatient(Patient newPatient) {
        LOG.debug("creating new patient");
        Patient created = service.persistPatient(newPatient);
        return Response.ok(created).build();
    }

    // Only ADMIN_ROLE can update a patient
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updatePatient(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Patient updatedPatient) {
        LOG.debug("updating patient {}", id);
        Patient updated = service.updatePatient(id, updatedPatient);
        return (updated == null) ? Response.status(Status.NOT_FOUND).build()
                                 : Response.ok(updated).build();
    }

    // Only ADMIN_ROLE can delete a patient
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePatient(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("deleting patient {}", id);
        service.deletePatientById(id);
        return Response.noContent().build();
    }
}
