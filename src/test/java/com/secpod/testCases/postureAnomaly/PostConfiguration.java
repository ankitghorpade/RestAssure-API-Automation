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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class PostConfiguration {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    GetAllConfiguration getAllConfiguration = new GetAllConfiguration();
    GetStatus getStatus = new GetStatus();

    public PostConfiguration() throws FileNotFoundException {
    };

    @BeforeClass
    public void setUp() throws FileNotFoundException {
        getAllConfiguration.getAllConfiguration(config.getAllConfiguration(),
                config.statusCode200(), "GET ALL CONFIGURATION", "\"query\"");
        data.setLocalPort(getAllConfiguration.getData().getLocalPort());
    }

    @Test(dataProvider = "theTestData")
    public void postConfiguration(String body, String statuscodes, String description, String expectedResult)
            throws FileNotFoundException, InterruptedException {

        if ((body.contains("INJECT_ACCNAME")) || (body.contains("INJECT_PORT"))){
            System.out.println("configuring port:: "+ data.getLocalPort());
            body = body.replace("INJECT_PORT",data.getLocalPort());

        }

        // check if the PA scan is ongoing and wait until the scan completes before Configuring.
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        getStatus.getStatus(config.getStatus().replace("INJECT_ACCNAME", jsonObject.get("account_name")
                        .getAsString()), config.statusCode200(), "get PA scan status before configuring",
                "account update finished".replaceAll("\"", ""));

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.pa_url()+"postConfiguration";

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
        JsonObject jsonObject2 = JsonParser.parseString(response.asString()).getAsJsonObject();
        response.then().assertThat().body(matchesJsonSchema(new File(config.postConfigurationSchemaJson())));
        String StatusCode = String.valueOf(response.statusCode());
        Assert.assertEquals(StatusCode, config.statusCode200());
        Assert.assertEquals(response.contentType(), config.contentType());
        Assert.assertEquals(jsonObject2.get("message").toString(), expectedResult);

    }

    public  GetSet getData() {
        return data;
    }

    @DataProvider(name = "theTestData")
    public Object[][]  excelPostConfiguration() throws IOException {
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
