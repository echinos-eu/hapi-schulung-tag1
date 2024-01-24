import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorial {

  public static void main(String[] args) {
    FhirContext ctx = FhirContext.forR4Cached();
    IParser iParser = ctx.newJsonParser().setPrettyPrint(true);
    String serverBase = "https://fhir.echinos.eu/fhir";
    IGenericClient client = ctx.newRestfulGenericClient(serverBase);
    Patient pat = getIsikPatient(ctx);
    System.out.println(iParser.encodeResourceToString(pat));
    //sendResourceToServer(client, pat);
    List<Patient> patients = searchPatient(client);
    updatePatient(client, patients.get(0));
    //deletePatient(client, patients.get(0), iParser);
    Condition condition = addCondition(client, "259");
    sendResourceToServer(client, condition);
    Encounter encounter = addEncounter(client, "259");
    sendResourceToServer(client, encounter);
  }

  private static Encounter addEncounter(IGenericClient client, String patientId) {
    Encounter encounter = new Encounter();
    Identifier identifier = encounter.addIdentifier();
    identifier.getType().addCoding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("VN");
    identifier.setSystem("http://gefyra.de/sid/Aufnahmenummer").setValue("65423198451");
    encounter.addType().addCoding().setSystem("http://fhir.de/CodeSystem/Kontaktebene")
        .setCode("abteilungskontakt");
    encounter.setStatus(EncounterStatus.PLANNED);
    encounter.getClass_().setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
        .setCode("IMP").setDisplay("stationärer Aufenthalt");
    encounter.setSubject(new Reference("Patient/"+ patientId));
    return encounter;
  }

  private static Condition addCondition(IGenericClient client, String patientId) {
    Condition condition = new Condition();
    condition.setSubject(new Reference("Patient/" + patientId));
    condition.getCode().addCoding().setCode("R05").setDisplay("Husten")
        .setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm");
    condition.setRecordedDate(new Date());
    return condition;

  }

  private static void deletePatient(IGenericClient client, Patient patient, IParser iParser) {
    MethodOutcome outcome = client.delete()
        .resource(patient).execute();
    IBaseOperationOutcome operationOutcome = outcome.getOperationOutcome();
    System.out.println(iParser.encodeResourceToString(operationOutcome));
  }

  private static void updatePatient(IGenericClient client, Patient patient) {
    patient.addName().addGiven(UUID.randomUUID().toString());
    MethodOutcome outcome = client.update()
        .resource(patient).execute();
    System.out.println(outcome.getId());
  }

  private static List<Patient> searchPatient(IGenericClient client) {
//    Bundle bundle = client.search().forResource(Patient.class)
//        .where(Patient.IDENTIFIER.exactly()
//            .systemAndIdentifier("http://gefyra.de/sid/Patientennummer", "01234569"))
//        .returnBundle(Bundle.class)
//        .execute();
    Bundle bundle = client.search()
        .byUrl("Patient?identifier=http://gefyra.de/sid/Patientennummer|01234569")
        .returnBundle(Bundle.class)
        // ohne cache suchen (default 5 Minuten)
        .cacheControl(new CacheControlDirective().setNoCache(true))
        .execute();

    System.out.println("Total: " + bundle.getTotal());
    List<Patient> list = bundle.getEntry().stream().map(c -> c.getResource()).map(r -> (Patient) r)
        .toList();
    return list;
  }

  private static void sendResourceToServer(IGenericClient client, Resource res) {
    MethodOutcome outcome = client.create().resource(res).execute();
    System.out.println(outcome.getId());
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
    identifier.setSystem("http://gefyra.de/sid/Patientennummer").setValue("01234569");
    pat.setActive(true);
    pat.setGender(AdministrativeGender.MALE);
    Address address = pat.addAddress();
    address.setType(AddressType.BOTH);
    StringType line = address.addLineElement();
    line.setValue("Musterstraße 12");
    Extension strasse = line.addExtension();
    strasse.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName")
        .setValue(new StringType("Musterstraße"));
    Extension hausnummer = line.addExtension();
    hausnummer.setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber")
        .setValue(new StringType("12"));
    return pat;
  }

}
