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

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class GetWhitelist {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    public GetWhitelist() throws FileNotFoundException {
    };


    @Test(dataProvider = "theTestData")
    public void getWhitelist(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.pa_url()+"getWhitelist";
        System.out.println(APIUrl);
        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", config.SAML())
                .setBody(body)
                .build();

        JsonObject jsonObject1 = JsonParser.parseString(body).getAsJsonObject();
        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        //response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());
        JsonObject jsonObject2 = JsonParser.parseString(response.asString()).getAsJsonObject();

        response.then().assertThat().body(matchesJsonSchema(new File(config.getWhitelistSchemaJson())));

        Assert.assertEquals(config.contentType(), response.getContentType());
        Assert.assertEquals(String.valueOf(response.getStatusCode()), config.statusCode200());
        Assert.assertEquals(jsonObject2.getAsJsonArray("whitelist_items").get(0).getAsJsonObject()
                .get("whitelist").getAsString(), expectedResult.replaceAll("\"", ""));

    }
    public  GetSet getData() {
        return data;
    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelGetWhitelist() throws IOException {
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
