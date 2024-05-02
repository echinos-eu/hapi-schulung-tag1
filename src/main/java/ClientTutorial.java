import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Date;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorial {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://gefyra.de/fhir/sid/Patientennummer";
  private static String sidNumber = "01246546879232323233";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    iParser = ctx.newJsonParser().setPrettyPrint(true);
    String serverUrl = "https://fhir.echinos.eu/fhir";
    client = ctx.newRestfulGenericClient(serverUrl);

    Patient isikPatient = getIsikPatient();
//    String patientString = iParser.encodeResourceToString(isikPatient);
//    System.out.println(patientString);

    // only create patient if not already on server
    MethodOutcome outcome = client.create()
        .resource(isikPatient)
        .conditional()
        .where(Patient.IDENTIFIER.exactly().systemAndValues(sidSystem, sidNumber))
        .execute();
    IIdType patientId = outcome.getId();
    System.out.println("PatientenId: " + patientId.getIdPart());

    //Encounter
    Encounter encounter = getISiKEncounter(patientId);
    MethodOutcome encounterOutcome = client.create().resource(encounter).execute();
    IIdType idEncounter = encounterOutcome.getId();
    System.out.println("EncounterId: " + idEncounter);

    //Condition
    Condition iSiKCondition = getISiKCondition(patientId, idEncounter);
    MethodOutcome conditionOutcome = client.create().resource(iSiKCondition).execute();
    System.out.println("ConditionId: " + conditionOutcome.getId());
  }

  private static Encounter getISiKEncounter(IIdType patientId) {
    Encounter encounter = new Encounter();
    Identifier identifier = encounter.addIdentifier();
    identifier.getType().addCoding()
        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("VN").setDisplay("visit number");
    identifier.setSystem("http://gefyra.de/fhir/sid/Aufnahmenummer")
        .setValue("66548646848");
    encounter.addType().addCoding().setSystem("http://fhir.de/CodeSystem/Kontaktebene")
        .setCode("abteilungskontakt").setDisplay("Abteilungskontakt");
    encounter.setStatus(EncounterStatus.PLANNED);
    encounter.getClass_().setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
        .setCode("IMP").setDisplay("stationärer Aufenthalt");
    encounter.setSubject(new Reference("Patient/" + patientId.getIdPart()));
    return encounter;
  }

  private static Condition getISiKCondition(IIdType patientId, IIdType idEncounter) {
    Condition condition = new Condition();
    condition.setSubject(new Reference(patientId.getResourceType()
        + "/" + patientId.getIdPart()));
    condition.setEncounter(new Reference("Encounter/" + idEncounter.getIdPart()));
    condition.getCode().addCoding().setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("R05").setDisplay("Husten");
    condition.setRecordedDate(new Date());
    condition.addNote().setText("Ich bin eine Notiz");
    return condition;
  }

  static Patient getIsikPatient() {
    Patient patient = new Patient();
    HumanName humanName = patient.addName();
    humanName.addGiven("Patrick").addGiven("Fritz").setFamily("Werner")
        .setUse(NameUse.OFFICIAL);
    patient.setBirthDateElement(new DateType("1982-04-03"));
    Identifier identifier = patient.addIdentifier();
    identifier.getType().addCoding()
        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("MR").setDisplay("Patientennummer");

    identifier.setSystem(sidSystem).setValue(sidNumber);
    patient.setActive(true);
    Enumeration<AdministrativeGender> genderElement = patient.getGenderElement();
    genderElement.setValue(AdministrativeGender.OTHER);
    genderElement.addExtension().setUrl("http://fhir.de/StructureDefinition/gender-amtlich-de")
        .setValue(new Coding("http://fhir.de/CodeSystem/gender-amtlich-de",
            "X", "divers"));

    Address address = patient.addAddress();
    address.setType(AddressType.BOTH);
    StringType line = address.addLineElement();
    line.setValue("Musterstraße 3");
    Extension strasse = line.addExtension();
    strasse.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName")
        .setValue(new StringType("Musterstraße"));
    line.addExtension().setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber")
        .setValue(new StringType("3"));
    address.setCity("Stadt");
    address.setPostalCode("13245");
    return patient;
  }
}
