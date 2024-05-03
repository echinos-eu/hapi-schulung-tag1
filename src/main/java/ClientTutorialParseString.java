import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Date;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class ClientTutorialParseString {

  private static FhirContext ctx;
  private static IParser iParser;
  private static IGenericClient client;
  private static String sidSystem = "http://gefyra.de/fhir/sid/Patientennummer";
  private static String sidNumber = "01246546fff879232323233";

  public static void main(String[] args) {
    ctx = FhirContext.forR4Cached();
    iParser = ctx.newJsonParser().setPrettyPrint(true);

    String resource = """
        {
                        "resourceType": "Patient",
                        "id": "a34a60b9-f980-4f52-8120-53bb82f9e951",
                        "identifier": [
                            {
                                "use": "usual",
                                "type": {
                                    "coding": [
                                        {
                                            "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
                                            "code": "MR"
                                        }
                                    ]
                                },
                                "system": "urn:oid:1.2.36.146.595.217.0.1",
                                "value": "12345",
                                "period": {
                                    "start": "2001-05-06"
                                },
                                "assigner": {
                                    "display": "Acme Healthcare"
                                }
                            }
                        ],
                        "active": true,
                        "name": [
                            {
                                "use": "official",
                                "family": "Werner",
                                "given": [
                                    "Peter",
                                    "James2"
                                ]
                            },
                            {
                                "use": "usual",
                                "given": [
                                    "Jim"
                                ]
                            },
                            {
                                "use": "maiden",
                                "family": "Windsor",
                                "given": [
                                    "Peter",
                                    "James"
                                ],
                                "period": {
                                    "end": "2002"
                                }
                            }
                        ],
                        "telecom": [
                            {
                                "use": "home"
                            },
                            {
                                "system": "phone",
                                "value": "(03) 5555 6473",
                                "use": "work",
                                "rank": 1
                            },
                            {
                                "system": "phone",
                                "value": "(03) 3410 5613",
                                "use": "mobile",
                                "rank": 2
                            },
                            {
                                "system": "phone",
                                "value": "(03) 5555 8834",
                                "use": "old",
                                "period": {
                                    "end": "2014"
                                }
                            }
                        ],
                        "gender": "male",
                        "birthDate": "1974-12-25",
                        "_birthDate": {
                            "extension": [
                                {
                                    "url": "http://hl7.org/fhir/StructureDefinition/patient-birthTime",
                                    "valueDateTime": "1974-12-25T14:35:45-05:00"
                                }
                            ]
                        },
                        "deceasedBoolean": false,
                        "address": [
                            {
                                "use": "home",
                                "type": "both",
                                "text": "534 Erewhon St PeasantVille, Rainbow, Vic  3999",
                                "line": [
                                    "534 Erewhon St"
                                ],
                                "city": "PleasantVille",
                                "district": "Rainbow",
                                "state": "Vic",
                                "postalCode": "3999",
                                "period": {
                                    "start": "1974-12-25"
                                }
                            }
                        ],
                        "contact": [
                            {
                                "relationship": [
                                    {
                                        "coding": [
                                            {
                                                "system": "http://terminology.hl7.org/CodeSystem/v2-0131",
                                                "code": "N"
                                            }
                                        ]
                                    }
                                ],
                                "name": {
                                    "family": "du Marché",
                                    "_family": {
                                        "extension": [
                                            {
                                                "url": "http://hl7.org/fhir/StructureDefinition/humanname-own-prefix",
                                                "valueString": "VV"
                                            }
                                        ]
                                    },
                                    "given": [
                                        "Bénédicte"
                                    ]
                                },
                                "telecom": [
                                    {
                                        "system": "phone",
                                        "value": "+33 (237) 998327"
                                    }
                                ],
                                "address": {
                                    "use": "home",
                                    "type": "both",
                                    "line": [
                                        "534 Erewhon St"
                                    ],
                                    "city": "PleasantVille",
                                    "district": "Rainbow",
                                    "state": "Vic",
                                    "postalCode": "3999",
                                    "period": {
                                        "start": "1974-12-25"
                                    }
                                },
                                "gender": "female",
                                "period": {
                                    "start": "2012"
                                }
                            }
                        ]
                    }
        """;
    Patient patient = iParser.parseResource(Patient.class, resource);
    System.out.println(patient.getNameFirstRep().getFamily());

  }
}
