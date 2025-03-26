import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Calendar;
import java.util.Date;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorialSearch {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://fhir.de/sid/gkv/kvid-10";
  private static String sidNumber = "X453211327";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    client = ctx.newRestfulGenericClient("https://fhir.echinos.eu/fhir");
    iParser = ctx.newJsonParser();
    iParser.setPrettyPrint(true);

    Bundle searchResponseBundle = client.search()
        .forResource(Patient.class)
        .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(sidSystem, sidNumber))
        .returnBundle(Bundle.class)
        .count(5)
        .totalMode(SearchTotalModeEnum.ACCURATE)
        .execute();

    System.out.println(iParser.encodeResourceToString(searchResponseBundle));
    System.out.println("Gefundene Ressourcen: " + searchResponseBundle.getTotal());

    Patient patient = (Patient) searchResponseBundle.getEntryFirstRep().getResource();
    System.out.println(patient.getNameFirstRep().getFamily());

    if (searchResponseBundle.getLink(Bundle.LINK_NEXT) != null) {

      // load next page
      Bundle nextPage = client.loadPage().next(searchResponseBundle).execute();
      System.out.println(iParser.encodeResourceToString(nextPage));
    }
  }
}


