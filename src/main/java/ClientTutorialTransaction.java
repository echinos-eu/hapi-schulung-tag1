import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorialTransaction {

  public static void main(String[] args) {
    FhirContext ctx = FhirContext.forR4Cached();
    IParser iParser = ctx.newJsonParser().setPrettyPrint(true);
    String serverBase = "https://fhir.echinos.eu/fhir";
    IGenericClient client = ctx.newRestfulGenericClient(serverBase);
    Patient pat = getIsikPatient(ctx);
    System.out.println(iParser.encodeResourceToString(pat));
    MethodOutcome outcome = validateResource(client, pat);
    //System.out.println(iParser.encodeResourceToString(outcome.getOperationOutcome()));
    pat.setId(IdType.newRandomUuid());

    Encounter encounter = addEncounter(client, pat);
    encounter.setId(IdType.newRandomUuid());
    Condition condition = addCondition(client, pat, encounter);
    condition.setId(IdType.newRandomUuid());
    List<Resource> resources = new ArrayList<>();
    resources.add(pat);
    resources.add(encounter);
    resources.add(condition);
    //Bundle bundle = sendTransactionBundleToServer(client, resources);
    //System.out.println(iParser.encodeResourceToString(bundle));
  }

  private static MethodOutcome validateResource(IGenericClient client, Patient pat) {
    MethodOutcome outcome = client.validate().resource(pat).execute();
    return outcome;
  }

  private static Bundle sendTransactionBundleToServer(IGenericClient client,
      List<Resource> resources) {
    Bundle bundle = new Bundle();
    bundle.setType(BundleType.TRANSACTION);
    resources.forEach(r -> {
      BundleEntryComponent bundleEntryComponent = bundle.addEntry();
      bundleEntryComponent.setResource(r);
      bundleEntryComponent.setFullUrl(r.getId());
      BundleEntryRequestComponent request = bundleEntryComponent.getRequest();
      request.setMethod(HTTPVerb.POST);
      request.setUrl(r.getResourceType().name());
    });
    Bundle responseBundle = client.transaction().withBundle(bundle).execute();
    return responseBundle;
  }

  private static Encounter addEncounter(IGenericClient client, Patient patient) {
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
    encounter.setSubject(new Reference(patient.getId()));
    return encounter;
  }

  private static Condition addCondition(IGenericClient client, Patient patient,
      Encounter encounter) {
    Condition condition = new Condition();
    condition.setSubject(new Reference(patient.getId()));
    condition.getCode().addCoding().setCode("R05").setDisplay("Husten")
        .setSystem("http://fhir.de/CodeSystem/bfarm/icd-10-gm");
    condition.setRecordedDate(new Date());
    condition.setEncounter(new Reference(encounter.getId()));
    return condition;
  }

  private static void sendResourceToServer(IGenericClient client, Resource res) {
    MethodOutcome outcome = client.create().resource(res).execute();
    System.out.println(outcome.getId());
  }


  private static Patient getIsikPatient(FhirContext ctx) {
    Patient pat = new Patient();
    pat.getMeta().addProfile("https://gematik.de/fhir/isik/v3/Basismodul/StructureDefinition/ISiKPatient");
    HumanName name = pat.addName();
    name.addGiven("Patrick").addGiven("Fritz");
    name.setFamily("Werner");
    name.setUse(NameUse.OFFICIAL);
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
    address.setCity("Stadt");
    address.setPostalCode("postalCode");
    address.setCountry("DE");
    return pat;
  }

}
