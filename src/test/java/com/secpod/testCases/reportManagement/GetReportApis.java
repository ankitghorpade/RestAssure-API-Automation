package com.secpod.testCases.reportManagement;

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

public class GetReportApis {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    JsonObject getProfileNamesData;

    public GetReportApis() throws FileNotFoundException {
    }

    @Test(dataProvider = "theTestData")
    void getReportApis(String body, String statuscodes, String urlParam, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);
        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        System.out.println(body);
        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
//        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        if (response.asString().contains(config.statusFail().toUpperCase())) {
            getProfileNamesData = ((jsonObject1.getAsJsonObject().get("response").getAsJsonObject()));

            data.setResponseGetFixConfig(String.valueOf(
                    jsonObject1.getAsJsonObject()
                            .get("response").getAsJsonObject()
                            .get("results").getAsJsonObject()
                            .get("result").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("status").toString()));

            data.setRequestReasonMessage(String.valueOf(
                    jsonObject1.getAsJsonObject()
                            .get("response").getAsJsonObject()
                            .get("results").getAsJsonObject()
                            .get("result").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("reason").toString()));
            Assert.assertEquals(data.getRequestReasonMessage(), expectedResult);
            Assert.assertEquals(data.getResponseGetFixConfig(), config.statusFail().toUpperCase());
        } else {


            response.then().assertThat().body(matchesJsonSchema(new File(config.getReportApisSchemaJson())));
            Assert.assertEquals(response.getContentType(), config.contentType());

            String[] toolsNames = expectedResult.replaceAll("\"", "").split(",");
            System.out.println(Arrays.toString(toolsNames));
            for (String toolsName : toolsNames) {
                String toolName = String.valueOf(jsonObject1.getAsJsonArray("reportsapis"));
                System.out.println(toolsName.trim());
                Assert.assertTrue(toolName.contains(toolsName.trim()));
            }
        }
    }


    public GetSet getData() {
        return data;
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelGetReportApis() throws IOException {
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
