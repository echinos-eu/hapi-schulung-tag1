import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

public class ClientTutorialSearchPagination {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://fhir.de/sid/gkv/kvid-10";
  private static String sidNumber = "X453211327";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    client = ctx.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
    iParser = ctx.newJsonParser();
    iParser.setPrettyPrint(true);

    Bundle searchResponseBundle = client.search()
        .forResource(Patient.class)
        .where(Patient.NAME.matches().value("Patrick"))
        .returnBundle(Bundle.class)
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


