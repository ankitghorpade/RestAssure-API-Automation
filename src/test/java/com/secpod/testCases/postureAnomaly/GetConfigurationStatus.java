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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetConfigurationStatus {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    public GetConfigurationStatus() throws FileNotFoundException {
    };

    @Test(dataProvider = "theTestData")
    public void getConfigurationStatus(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.pa_url()+"getConfigurationStatus";
        System.out.println("prints " + APIUrl);

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
        response.then().assertThat().body(matchesJsonSchema(new File(config.getConfigurationStatusSchemaJson())));
        String statusCode = String.valueOf(response.getStatusCode());
        Assert.assertEquals(statusCode, config.statusCode200());
        Assert.assertEquals(config.contentType(), response.getContentType());
        String [] listPAIds = expectedResult.replaceAll("\"", "").split(",");
        System.out.println(Arrays.toString(listPAIds));

        for (String listPAId : listPAIds) {
//            System.out.println(listPAIds[i]);
            Assert.assertTrue(jsonObject1.has(listPAId));
        }

    }


    public  GetSet getData() {
        return data;
    }

    @DataProvider(name = "theTestData")
    public Object[][]  excelGetConfigurationStatus() throws IOException {
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
