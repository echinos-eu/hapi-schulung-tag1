import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import org.hl7.fhir.r4.model.Patient;

public class ValidationTest {

  public static void main(String[] args) throws IOException {
    FhirContext ctx = FhirContext.forR4Cached();
    IParser iParser = ctx.newJsonParser().setPrettyPrint(true);
    Patient isikPatient = ClientTutorial.createIsikPatient();
    ISiKValidator validator = new ISiKValidator(ctx);
    ValidationOptions validationOptions = new ValidationOptions();
    validationOptions.addProfile("https://gematik.de/fhir/isik/StructureDefinition/ISiKPatient");

    ValidationResult validationResult = validator.validateWithResult(isikPatient, validationOptions);
    System.out.println("Validierung erfolgreich: " + validationResult.isSuccessful());
    String outcome = iParser.encodeResourceToString(validationResult.toOperationOutcome());
    System.out.println(outcome);
  }
}
