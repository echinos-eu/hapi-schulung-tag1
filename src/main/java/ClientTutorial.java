import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Patient;

public class ClientTutorial {

  public static void main(String[] args) {
    FhirContext ctx = FhirContext.forR4Cached();
    Patient pat = getIsikPatient(ctx);
  }

  private static Patient getIsikPatient(FhirContext ctx) {
  }

}
