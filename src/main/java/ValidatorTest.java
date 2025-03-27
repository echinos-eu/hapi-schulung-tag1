import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

public class ValidatorTest {

  private static String sidSystem = "http://fhir.de/sid/gkv/kvid-10";
  private static String sidNumber = "X453211327";

  public static void main(String[] args) throws IOException {
    Patient patient = new Patient();
    patient.getMeta()
        .addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Patient|1.2");
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
    FhirContext ctx = FhirContext.forR4Cached();
    IParser iParser = ctx.newJsonParser().setPrettyPrint(true);
    EAUValidator validator = new EAUValidator(ctx);
    ValidationOptions options = new ValidationOptions();
    options.addProfile("https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Patient|1.2.0");

    ValidationResult validationResult = validator.validateWithResult(patient, options);
    System.out.println("Validierung war erfolgreich? " + validationResult.isSuccessful());

    String outcomeString = iParser.encodeResourceToString(validationResult.toOperationOutcome());
    System.out.println(outcomeString);
  }
}
