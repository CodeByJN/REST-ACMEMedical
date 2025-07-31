/********************************************************************************************************
 * File:  Physician.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the physician database table.
 */
@SuppressWarnings("unused")

//DONE PH01 - Add the missing annotations.
//DONE PH02 - Do we need a mapped super class? If so, which one? 
@Entity
@Table(name = "physician")
@AttributeOverride(name = "id", column = @Column(name = "physician_id"))
public class Physician extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

    public Physician() {
    	super();
    }

	// DONE PH03 - Add annotations.
    @Column(name = "first_name", nullable = false)
	private String firstName;

	// DONE PH04 - Add annotations.
    @Column(name = "last_name", nullable = false)
	private String lastName;

	// DONE PH05 - Add annotations for 1:M relation.  What should be the cascade and fetch types?
    @OneToMany(cascade=CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "physician")
	private Set<MedicalCertificate> medicalCertificates = new HashSet<>();

	// DONE PH06 - Add annotations for 1:M relation.  What should be the cascade and fetch types?
	@OneToMany(cascade=CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "physician")
	private Set<Prescription> prescriptions = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// DONE PH07 - Is an annotation needed here? NO
    public Set<MedicalCertificate> getMedicalCertificates() {
		return medicalCertificates;
	}

	public void setMedicalCertificates(Set<MedicalCertificate> medicalCertificates) {
		this.medicalCertificates = medicalCertificates;
	}

	// DONE PH08 - Is an annotation needed here? NO
    public Set<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(Set<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
