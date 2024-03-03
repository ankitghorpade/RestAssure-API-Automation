package com.secpod.testCases.ActiveDirectory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class AddADConfig {
    public static Response response;

    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    public AddADConfig() throws FileNotFoundException {
    }

    @Test(dataProvider = "theTestData")
    public void addADConfig(String body, String statuscodes, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");
        String APIUrl = config.configs_url();

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        data.setRequestOrgName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));
        data.setRequestDeviceName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(1).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        System.out.println("Request -> " + body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println("Response -> " + response.asString());
//        response.then().assertThat().body(matchesJsonSchema(new File(config.deletePAScanConfigSchemaJson())));
//                .time(Matchers.lessThanOrEqualTo(15000L));

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());

        Assert.assertEquals(config.contentType(), response.getContentType());

        if (String.valueOf(response.getStatusCode()).equals(config.statusCode200())) {
            if ((data.getStatusMessage().equals(config.statusSuccess()))) {
                data.setResponseOrgName(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                        .getAsJsonArray("result").get(0).getAsJsonObject().get("key").getAsString());
                response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
                Assert.assertEquals(data.getRequestOrgName(), data.getResponseOrgName());
                Assert.assertEquals(data.getStatusMessage(), expectedResult);
            }
            if ((data.getStatusMessage().equals(config.statusFail()))) {
                response.then().assertThat().body(matchesJsonSchema(new File(config.deletePAScanConfigSchemaJson())));
                data.setExpectedResult(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                        .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
                Assert.assertEquals((data.getStatusMessage()), config.statusFail());
                Assert.assertEquals(data.getExpectedResult(), expectedResult);
            }
        } else {
            System.out.println("API responded with status code -> " + response.getStatusCode());
        }
    }

    public GetSet getData() {
        return data;
    }

    @AfterMethod
    public void tearDown() throws FileNotFoundException {
        if (Objects.equals(config.statusSuccess(), data.getStatusMessage())) {
            DeleteADConfig deleteAD = new DeleteADConfig();
            try {
                String body = config.deleteAD().replace("INJECT_ORGNAME", data.getRequestOrgName());
                System.out.println("body");
                deleteAD.deleteADConfig(body.replace("INJECT_DEVICENAME", data.getRequestDeviceName()),
                        config.statusSuccess(), "DELETE_ADConfig", config.statusSuccess());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @DataProvider(name = "theTestData")
    //This method will get the name of the method,calls apiDataGenerator which will get the .json file, replace
    //values of INJECT with excelsheet column names and returns 2d array with JSON and status code expected
    public Object[][] addADconfig() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.adTestCasesExcelPath());
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
