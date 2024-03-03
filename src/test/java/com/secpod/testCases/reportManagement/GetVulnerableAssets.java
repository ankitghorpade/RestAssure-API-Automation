package com.secpod.testCases.reportManagement;

import com.google.gson.JsonArray;
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

public class GetVulnerableAssets {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    JsonArray getProfileNamesData;

    public GetVulnerableAssets() throws FileNotFoundException {
    }

    @Test(dataProvider = "theTestData")
    void getVulnerableAssets(String body, String statuscodes, String urlParam, String description, String expectedResult) throws IOException {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
//        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());
        String responsebody = response.body().asString();

        JsonObject jsonObject = JsonParser.parseString(response.asString()).getAsJsonObject();

        if (jsonObject.has("asset")) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.getAssetsSchemaJson())));
            Assert.assertEquals(response.getContentType(), config.contentType());
        } else if (jsonObject.has("errorCode")) {
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            System.out.println("Bug raised for invalid account name giving 403 and 404");
        } else {
            String expectedStatus = "FAIL";
            String actualStatus = config.statusFail().toUpperCase().replaceAll("\"", "");
            JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
            data.setRequestAddConfig(jsonObject1.getAsJsonObject("response")
                    .getAsJsonObject("results")
                    .getAsJsonArray("result").get(0)
                    .getAsJsonObject().get("reason").toString());
            Assert.assertEquals(data.getRequestAddConfig(), expectedResult);
            Assert.assertEquals(actualStatus, expectedStatus);
        }

    }

    public GetSet getData() {
        return data;
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelGetVulnerableAssets() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.reportManagementTestCasesExcelPath());
        Object[][] arr = new Object[tcs.length][5];
        for (int i = 0; i < tcs.length; i++) {
            arr[i][0] = tcs[i][0];
            arr[i][1] = tcs[i][1];
            arr[i][2] = tcs[i][2];
            arr[i][3] = tcs[i][3];
            arr[i][4] = tcs[i][4];
        }
        return arr;
    }
}
