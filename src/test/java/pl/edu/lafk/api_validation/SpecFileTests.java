package pl.edu.lafk.api_validation;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.schema.validator.v3.SchemaValidator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

@Test
public class SpecFileTests {

    private final String specPath = "src/main/resources/static/api.yaml";

    @Test
    public void validateOpenApiSpec() {
        OpenApi3 openApi = null;
        try {
            openApi = new OpenApi3Parser().parse(new File(specPath), true);
        } catch (ResolutionException e) {
            Assert.fail("Test failed. JSON (ref unreachable) problem? " + e.getMessage());
        } catch (ValidationException e) {
            Assert.fail("Spec validation failure. See context/results.\n" + e.toString());
        }
        Assert.assertNotNull(openApi, "Failed to parse OpenAPI spec! Was the file given correct?");
        }

    @Test(dependsOnMethods = "validateOpenApiSpec")
    public void validateVersioning() throws ResolutionException, ValidationException {
        OpenApi3 openApi = new OpenApi3Parser().parse(new File(specPath), true);
        Assert.assertEquals(openApi.getOpenapi(), "3.0.0", "OpenAPI version differs!");
        Assert.assertEquals(openApi.getInfo().getVersion(), "1.0", "Spec doc version differs!");
    }

    @Test(dependsOnMethods = "validateOpenApiSpec")
    public void validateNumberOfPaths() throws ResolutionException, ValidationException {
        OpenApi3 openApi = new OpenApi3Parser().parse(new File(specPath), true);
        Assert.assertEquals(openApi.getPaths().size(), 2, "I'd expect two paths, but things have changed?");
    }

    @Test(dependsOnMethods = "validateOpenApiSpec")
    public void checkSpecToJSONConversion() throws ResolutionException, ValidationException {
        OpenApi3 openApi = new OpenApi3Parser().parse(new File(specPath), true);
        JsonNode apiNode = null;
        try {
            apiNode = openApi.toNode();
        } catch (EncodeException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(dependsOnMethods = "checkSpecToJSONConversion")
    public void simpleChecks() throws ResolutionException, ValidationException, EncodeException {
        OpenApi3 openApi = new OpenApi3Parser().parse(new File(specPath), true);
        JsonNode apiNode = openApi.toNode();
        apiNode.iterator().forEachRemaining(System.out::println);
        Assert.assertEquals(apiNode.size(),5);
        // Other simple checks?
    }

    @Test(dependsOnMethods = "checkSpecToJSONConversion")
    public void validateSchema() throws ResolutionException, ValidationException, EncodeException {
        OpenApi3 openApi = new OpenApi3Parser().parse(new File(specPath), true);
        JsonNode apiNode = openApi.toNode();
        SchemaValidator validator = new SchemaValidator(null, apiNode);
       // https://www.openapi4j.org/schema-validator.html#usage
        // https://www.openapi4j.org/operation-validator.html
    }
}
