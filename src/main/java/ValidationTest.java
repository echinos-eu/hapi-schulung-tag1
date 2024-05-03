import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
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
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ValidationTest {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://gefyra.de/fhir/sid/Patientennummer";
  private static String sidNumber = "01246546879232323233";

  public static void main(String[] args) throws IOException {
    ctx = FhirContext.forR4Cached();
    iParser = ctx.newJsonParser().setPrettyPrint(true);
    String serverUrl = "https://fhir.echinos.eu/fhir";
    client = ctx.newRestfulGenericClient(serverUrl);

    Patient isikPatient = getIsikPatient();
    ISiKValidator validator = new ISiKValidator(ctx);
    ValidationOptions validationOptions = new ValidationOptions();
    validationOptions.addProfile("https://gematik.de/fhir/isik/StructureDefinition/ISiKPatient");
    ValidationResult validationResult = validator.validateWithResult(isikPatient,
        validationOptions);
    System.out.println("Validierung erfolgreich? " + validationResult.isSuccessful());
    IBaseOperationOutcome operationOutcome = validationResult.toOperationOutcome();
    System.out.println(iParser.encodeResourceToString(operationOutcome));

    Encounter iSiKEncounter = getISiKEncounter(isikPatient.getIdElement());

    ValidationResult validationResultEncounter = validator.validateWithResult(iSiKEncounter);
    System.out.println(
        "Validierung Encounter erfolgreich? " + validationResultEncounter.isSuccessful());
    operationOutcome = validationResultEncounter.toOperationOutcome();
    System.out.println(iParser.encodeResourceToString(operationOutcome));

    Condition iSiKCondition = getISiKCondition(isikPatient.getIdElement(),
        iSiKEncounter.getIdElement());
    ValidationResult validationResultCondition = validator.validateWithResult(iSiKCondition);
    System.out.println(
        "Validierung Condition erfolgreich? " + validationResultCondition.isSuccessful());
    operationOutcome = validationResultCondition.toOperationOutcome();
    OperationOutcome outcomeR4 = (OperationOutcome) operationOutcome;
    List<OperationOutcomeIssueComponent> filterIssues = outcomeR4.getIssue()
        .stream().filter(i -> !i.getDetails().getCodingFirstRep().getCode()
            .equals("Terminology_PassThrough_TX_Message") && i.getDiagnostics().contains("http://fhir.de/CodeSystem/bfarm/icd-10-gm")).toList();
    outcomeR4.setIssue(filterIssues);

    System.out.println(iParser.encodeResourceToString(outcomeR4));

  }

  private static Encounter getISiKEncounter(IIdType patientId) {
    Encounter encounter = new Encounter();
    encounter.getMeta().addProfile(
        "https://gematik.de/fhir/isik/StructureDefinition/ISiKKontaktGesundheitseinrichtung");
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
//    patient.getMeta().addProfile("https://gematik.de/fhir/isik/StructureDefinition/ISiKPatient");
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
    address.setCountry("DE");
    return patient;
  }
}
