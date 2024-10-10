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
import org.hl7.fhir.r4.model.DateTimeType;
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
  private static String sidNumber = "9849849846984964132197";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    iParser = ctx.newJsonParser();
    iParser.setPrettyPrint(true);
    Patient isikPatient = createIsikPatient();
    String patientString = iParser.encodeResourceToString(isikPatient);
    System.out.println(patientString);
    Encounter isikKontakt = getIsiKEncounter("Patient/123");
    System.out.println(iParser.encodeResourceToString(isikKontakt));
    // add Diagnose
    Condition isiKCondition = getIsiKCondition("Patient/123");
    System.out.println(iParser.encodeResourceToString(isiKCondition));

  }

  static Patient createIsikPatient() {
    Patient patient = new Patient();
    HumanName humanName = patient.addName();
    humanName.setFamily("Werner").addGiven("Patrick").addGiven("Fritz")
        .setUse(NameUse.OFFICIAL);
    patient.setBirthDateElement(new DateType("1982-04-03"));
    //alternativ:
    //patient.setBirthDate(new Date());
    patient.setGender(AdministrativeGender.OTHER);
    Enumeration<AdministrativeGender> genderElement = patient.getGenderElement();
    genderElement.addExtension().setUrl("http://fhir.de/StructureDefinition/gender-amtlich-de")
        .setValue(new Coding("http://fhir.de/CodeSystem/gender-amtlich-de", "X", "unbestimmt"));
    Identifier identifier = patient.addIdentifier();
    identifier.getType().addCoding()
        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("MR")
        .setDisplay("Patientennummer");
    identifier.setSystem(sidSystem).setValue(sidNumber);
    patient.setActive(true);
    //Adresse
    Address address = patient.addAddress();
    address.setType(AddressType.BOTH);
    StringType line = address.addLineElement();
    line.setValue("Musterstraße 130");
    Extension strassenName = line.addExtension();
    strassenName.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName")
        .setValue(new StringType("Musterstraße"));
    Extension hausnummer = line.addExtension();
    hausnummer.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber")
        .setValue(new StringType("130"));
    address.setCity("Mannheim");
    address.setPostalCode("68169");
    return patient;
  }
  static Condition getIsiKCondition(String patientId) {
    Condition condition = new Condition();
    condition.setSubject(new Reference(patientId));
    condition.setRecordedDate(new Date());
    condition.addNote().setText("Ich bin eine Notiz");
    condition.getCode().addCoding().setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
        .setCode("R05").setDisplay("Husten");
    return condition;
  }

  private static Encounter getIsiKEncounter(String patientId) {
    Encounter encounter = new Encounter();
    encounter.setSubject(new Reference(patientId));
    Identifier identifier = encounter.addIdentifier();
    identifier.getType().addCoding()
        .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("VN")
        .setDisplay("visit number");
    identifier.setSystem("http://gefyra.de/fhir/sid/Aufnahmenummer");
    identifier.setValue("515816941");
    encounter.addType().addCoding().setSystem("http://fhir.de/CodeSystem/Kontaktebene")
        .setCode("abteilungskontakt").setDisplay("Abteilungskontakt");
    encounter.setStatus(EncounterStatus.PLANNED);
    return encounter;
  }
}
