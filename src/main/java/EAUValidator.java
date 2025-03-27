import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;

public class EAUValidator extends FhirValidator {

  public EAUValidator(FhirContext ctx) {
    super(ctx);

  }

}
