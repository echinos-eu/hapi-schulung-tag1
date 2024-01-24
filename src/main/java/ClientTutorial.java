import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
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
        .execute();
    System.out.println(bundle.getTotal());
    List<Patient> list = bundle.getEntry().stream().map(c -> c.getResource()).map(r -> (Patient) r)
        .toList();
    return list;
  }

  private static void sendResourceToServer(IGenericClient client, Patient pat) {
    MethodOutcome outcome = client.create().resource(pat).execute();
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
