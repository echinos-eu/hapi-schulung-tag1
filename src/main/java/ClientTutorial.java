import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
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
    Patient pat = getIsikPatient(ctx);
    System.out.println(iParser.encodeResourceToString(pat));
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
    identifier.setSystem("http://gefyra.de/sid/Patientennummer").setValue("0815");
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
