import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Calendar;
import java.util.Date;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorialUpdate {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://fhir.de/sid/gkv/kvid-10";
  private static String sidNumber = "X453211327";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    client = ctx.newRestfulGenericClient("https://fhir.echinos.eu/fhir");
    iParser = ctx.newJsonParser();
    iParser.setPrettyPrint(true);

    Patient patient = client.read()
        .resource(Patient.class)
        .withId("1258").execute();

    patient.setActive(!patient.getActive());
    System.out.println(patient.getActive());

    MethodOutcome updateOutcome = client.update()
        .resource(patient)
        .execute();

    System.out.println(updateOutcome.getId());

  }
}
