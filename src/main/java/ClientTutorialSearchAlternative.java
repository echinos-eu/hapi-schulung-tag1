import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;

public class ClientTutorialSearchAlternative {

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

    Bundle execute = client.search()
        .byUrl(
            "Patient?_count=5&_total=accurate&identifier=http%3A%2F%2Ffhir.de%2Fsid%2Fgkv%2Fkvid-10%7CX453211327")
        .returnBundle(Bundle.class)
        .usingStyle(SearchStyleEnum.POST)
        .execute();
    System.out.println(iParser.encodeResourceToString(execute));
  }
}



