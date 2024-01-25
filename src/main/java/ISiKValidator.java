import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import java.io.IOException;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;

public class ISiKValidator extends FhirValidator {

  /**
   * Constructor (this should not be called directly, but rather {@link FhirContext#newValidator()}
   * should be called to obtain an instance of {@link FhirValidator})
   *
   * @param theFhirContext
   */
  public ISiKValidator(FhirContext theFhirContext) throws IOException {
    super(theFhirContext);
    NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(theFhirContext);
    npmPackageSupport.loadPackageFromClasspath("classpath:package/de.basisprofil.r4-1.4.0.tgz");
    npmPackageSupport.loadPackageFromClasspath(
        "classpath:package/de.gematik.isik-basismodul-3.0.3.tgz");
    ValidationSupportChain validationSupportChain = new ValidationSupportChain(
        npmPackageSupport,
        new DefaultProfileValidationSupport(theFhirContext),
        new CommonCodeSystemsTerminologyService(theFhirContext),
        new InMemoryTerminologyServerValidationSupport(theFhirContext),
        new SnapshotGeneratingValidationSupport(theFhirContext)
    );
    CachingValidationSupport cachingValidationSupport = new CachingValidationSupport(
        validationSupportChain);
    FhirInstanceValidator validator = new FhirInstanceValidator(cachingValidationSupport);
    registerValidatorModule(validator);
    validator.setNoExtensibleWarnings(true);
    validator.setErrorForUnknownProfiles(false);
  }
}
