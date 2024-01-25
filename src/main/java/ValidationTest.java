import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import org.hl7.fhir.r4.model.Patient;

public class ValidationTest {

  public static void main(String[] args) throws IOException {
    FhirContext fhirContext = FhirContext.forR4Cached();
    IParser iParser = fhirContext.newJsonParser().setPrettyPrint(true);
    Patient isikPatient = ClientTutorial.getIsikPatient(fhirContext);
    ISiKValidator validator = new ISiKValidator(fhirContext);
    ValidationOptions options = new ValidationOptions();
    options.addProfile("https://gematik.de/fhir/isik/v3/Basismodul/StructureDefinition/ISiKPatient");
    ValidationResult validationResult = validator.validateWithResult(isikPatient, options);
    System.out.println(validationResult.isSuccessful());
    System.out.println(iParser.encodeResourceToString(validationResult.toOperationOutcome()));
  }

}
