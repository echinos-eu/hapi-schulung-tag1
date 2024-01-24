import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

public class ClientTutorial {

  public static void main(String[] args) {
    FhirContext ctx = FhirContext.forR4Cached();
    Patient pat = getIsikPatient(ctx);
  }

  private static Patient getIsikPatient(FhirContext ctx) {
    Patient pat = new Patient();
    HumanName name = pat.addName();
    name.addGiven("Patrick").addGiven("Fritz");
    name.setFamily("Werner");
    pat.setBirthDateElement(new DateType("1982-04-03"));

    Identifier identifier = pat.addIdentifier();
    identifier.getType().addCoding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("MR");
    identifier.setSystem("http://gefyra.de/sid/Patientennummer").setValue("0815");
    return pat;
  }

}
