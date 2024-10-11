import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import java.io.IOException;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;

public class ISiKValidator extends FhirValidator {

  /**
   * Constructor (this should not be called directly, but rather {@link FhirContext#newValidator()}
   * should be called to obtain an instance of {@link FhirValidator})
   *
   * @param theFhirContext
   */
  public ISiKValidator(FhirContext theFhirContext) throws IOException {
    super(theFhirContext);

    NpmPackageValidationSupport npmPackageValidationSupport = new NpmPackageValidationSupport(theFhirContext);
    npmPackageValidationSupport.loadPackageFromClasspath("classpath:/packages/de.basisprofil.r4-1.5.0.tgz");
    npmPackageValidationSupport.loadPackageFromClasspath("classpath:/packages/de.gematik.isik-basismodul-4.0.0.tgz");

  }
}
