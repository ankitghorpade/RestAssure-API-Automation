package com.secpod.testCases.ActiveDirectory;

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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetADConfig {
    public static Response response;

    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    JsonArray getADStatus;

    public GetADConfig() throws FileNotFoundException {
    }

    @BeforeClass
    public void setUp() throws IOException {
        // Create a AD config
        AddADConfig createAD = new AddADConfig();
        createAD.addADConfig(config.addADCONFIG(), config.statusCode200(), "Create_ADconfig", config.statusSuccess());
        data.setRequestOrgName(createAD.getData().getRequestOrgName());
        data.setRequestDeviceName(createAD.getData().getRequestDeviceName());
    }


    @Test(dataProvider = "theTestData")
    public void getADConfig(String body, String statuscodes, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");
        String APIUrl = config.configs_url();

        if (body.contains("INJECT_ORGNAME")) {
            body = body.replace("INJECT_ORGNAME", data.getRequestOrgName()
                    .replaceAll("\"", ""));
        }
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        data.setRequestOrgName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        data.setRequestAccName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        System.out.println("Request -> " + body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println("Response -> " + response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        getADStatus = ((jsonObject1.getAsJsonObject().get("adconfigurations").getAsJsonArray()));


        if (getADStatus.isEmpty()) {
            System.out.println("empty arrayList::" + response.asString());
        } else if (getADStatus.toString().contains(config.statusFail())) {
            Assert.assertEquals(config.contentType(), response.getContentType());
//            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation11())));
            data.setStatusMessage(jsonObject1.getAsJsonArray("adconfigurations")
                    .get(0).getAsJsonObject().get("status").toString());
            data.setResponseOrgName(jsonObject1.getAsJsonArray("adconfigurations")
                    .get(0).getAsJsonObject().get("key").toString());
            data.setOrgErrorMessage(jsonObject1.getAsJsonArray("adconfigurations")
                    .get(0).getAsJsonObject().get("reason").toString());
            Assert.assertEquals(data.getStatusMessage(), config.statusFail());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);

        } else {
            data.setResponseOrgName(jsonObject1.getAsJsonArray("adconfigurations").get(0).getAsJsonObject()
                    .get("adconfigurationinfo").getAsJsonObject().get("organization").getAsString());
            Assert.assertEquals(config.contentType(), response.getContentType());
            Assert.assertEquals(data.getRequestOrgName(), data.getResponseOrgName());
            response.then().assertThat().body(matchesJsonSchema(new File(config.getADConfigurationSchemaJson())));
        }
    }


    @AfterMethod
    public void tearDown() throws FileNotFoundException {
        if ( (!Objects.equals(config.statusFail(), data.getStatusMessage()))) {
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
    public GetSet getData() {
        return data;
    }


    @DataProvider(name = "theTestData")
    //This method will get the name of the method,calls apiDataGenerator which will get the .json file, replace
    //values of INJECT with excelsheet column names and returns 2d array with JSON and status code expected
    public Object[][] getADconfig() throws IOException {
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