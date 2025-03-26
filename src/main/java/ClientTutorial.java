import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
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

public class ClientTutorial {

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
    System.out.println(iParser.encodeResourceToString(patient));

    Practitioner practitioner = createPractitioner();
    System.out.println(iParser.encodeResourceToString(practitioner));

    Condition condition = createCondition(patient, practitioner);
    System.out.println(iParser.encodeResourceToString(condition));

  }

  private static Condition createCondition(Patient patient, Practitioner practitioner) {
    Condition condition = new Condition();
    condition
        .getMeta()
        .addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_EAU_Condition_ICD|1.1.0");
    condition.getCode()
        .addCoding()
        .setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("J01.1")
        .setVersion("2025")
        .setDisplay("Akute Sinusitis frontalis");
    condition.setSubject(new Reference(patient));
    condition.setAsserter(new Reference(practitioner));
    return condition;
  }

  private static Practitioner createPractitioner() {
    Practitioner practitioner = new Practitioner();
    practitioner
        .getMeta()
        .addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Practitioner|1.1.0");
    practitioner
        .addName()
        .addGiven("Vorname")
        .setFamily("ArztNachname")
        .setUse(NameUse.OFFICIAL);
    //Berufsbezeichnung
    practitioner
        .addQualification()
        .getCode()
        .setText("Thoraxchirurg")
        .addCoding()
        .setSystem("https://fhir.kbv.de/CodeSystem/KBV_CS_FOR_Berufsbezeichnung")
        .setCode("Berufsbezeichnung");
    //Typ
    practitioner
        .addQualification()
        .getCode()
        .addCoding()
        .setSystem("https://fhir.kbv.de/CodeSystem/KBV_CS_FOR_Qualification_Type")
        .setCode("00")
        .setDisplay("Arzt");
    return practitioner;
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
