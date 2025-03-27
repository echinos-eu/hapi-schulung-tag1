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
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorialDelete {

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

    Patient patient = createPatient();
    MethodOutcome execute = client.create()
        .resource(patient)
        .execute();
    String id = execute.getId().getIdPart();

    client
        .delete()
        .resourceById(new IdType(patient.getResourceType()+ "/" + id)).execute();

    Patient patient2 = client.read().resource(Patient.class)
        .withId(id).execute();

    Patient patient3 = client.read().resource(Patient.class)
        .withId("dwfferfweferwgerwfgegr").execute();
  }


  private static Patient createPatient() {
    Patient patient = new Patient();
    patient.getMeta()
        .addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Patient|1.1.0");
    CodeableConcept type = patient.addIdentifier().setSystem(sidSystem).setValue(sidNumber)
        .getType();
    type.addCoding().setSystem("http://fhir.de/CodeSystem/identifier-type-de-basis").setCode("GKV");

    HumanName humanName = patient.addName();
    humanName.setUse(NameUse.OFFICIAL);
    humanName.addGiven("Patrick");
    StringType familyElement = humanName.getFamilyElement();
    familyElement.setValue("von Werner");
    familyElement.addExtension()
        .setUrl("http://hl7.org/fhir/StructureDefinition/humanname-own-name")
        .setValue(new StringType("Werner"));
    patient.setBirthDate(new Date(1982, Calendar.APRIL, 3));
    patient.addAddress().setType(AddressType.BOTH)
        .addLine("Musterstraße 1")
        .setCity("Musterstadt")
        .setPostalCode("12345")
        .setCountry("D");
    return patient;
  }
}
