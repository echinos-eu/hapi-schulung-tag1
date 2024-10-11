import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;

public class ISiKValidator extends FhirValidator {

  /**
   * Constructor (this should not be called directly, but rather {@link FhirContext#newValidator()}
   * should be called to obtain an instance of {@link FhirValidator})
   *
   * @param theFhirContext
   */
  public ISiKValidator(FhirContext theFhirContext) {
    super(theFhirContext);



  }
}
