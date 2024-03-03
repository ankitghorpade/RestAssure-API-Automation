package com.secpod.testCases.postureAnomaly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetConfiguration {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    PostConfiguration postConfiguration = new PostConfiguration();
    GetAllConfiguration getAllConfiguration = new GetAllConfiguration();

    public GetConfiguration() throws FileNotFoundException {
    };

    @BeforeClass
    public void setUp() throws FileNotFoundException, InterruptedException {
        getAllConfiguration.getAllConfiguration(config.getAllConfiguration(),
                config.statusCode200(), "GET ALL CONFIGURATION", "\"query\"");
        data.setLocalPort(getAllConfiguration.getData().getLocalPort());
        postConfiguration.postConfiguration(config.postConfiguration().replace("INJECT_PORT", data.getLocalPort()),
                config.statusCode200(), "Adding PostConfiguration", "\"Successful\"");
    }

        @Test(dataProvider = "theTestData")
        public void getConfigurationStatus(String body, String statuscodes, String description, String expectedResult) {

            System.out.println("\n###############" + description + "###############\n");

            String APIUrl = config.pa_url()+"getConfiguration";
            System.out.println(APIUrl);

            System.out.println(body);

            RequestSpecification requestspec = new RequestSpecBuilder()
                    .setContentType("application/json")
                    .addHeader("Authorization", config.SAML())
                    .setBody(body)
                    .build();

            response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
            //response.then().time(Matchers.lessThanOrEqualTo(5000L));
            System.out.println(response.asString());
            JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
            String statusCode = String.valueOf(response.getStatusCode());

            response.then().assertThat().body(matchesJsonSchema(new File(config.getConfigurationSchemaJson())));
            Assert.assertEquals(statusCode, config.statusCode200());
            Assert.assertEquals(config.contentType(), response.getContentType());
            Assert.assertEquals(jsonObject1.getAsJsonArray("configure_items").get(0).getAsJsonObject()
                    .get("id").toString().trim(), expectedResult);
            Assert.assertEquals(jsonObject1.getAsJsonArray("configure_items").get(0).getAsJsonObject()
                    .get("configure").getAsString(), data.getLocalPort());

        }
        public  GetSet getData() {
            return data;
        }

    @AfterClass
    public void tearDown() throws FileNotFoundException, InterruptedException {
    // Calling PostConfiguration Api with empty body to remove all configuration.
        postConfiguration.postConfiguration(config.postConfiguration().replace("INJECT_PORT", ""),
                config.statusCode200(), "PostConfiguration to remove the configuration", "\"Successful\"");

    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelGetConfiguration() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.postureAnomalyTestCasesExcelPath());
        Object[][] arr = new Object[tcs.length][4];
        for (int i = 0; i < tcs.length; i++) {
            arr[i][0] = tcs[i][0];
            arr[i][1] = tcs[i][1];
            arr[i][2] = tcs[i][3];
            arr[i][3] = tcs[i][4];
        }
            return arr;
        }

}
