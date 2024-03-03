package com.secpod.testCases.deviceManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class ScanDevice {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    public ScanDevice() throws FileNotFoundException {
    }


    @Test(dataProvider = "theTestData")
    void scanDevice(String body, String statuscodes, String urlParam, String description, String expectedResult) throws IOException {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        if (body.contains("null")) {
            body = body.replace("\"parameterset\":[{\"parameter\":[{\"key\":\"null\",\"value\":\"null\"}]}]", "");
        }
        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());

        if (Objects.equals(data.getStatusMessage(), config.statusFail().toUpperCase())) {
            Assert.assertEquals(data.getStatusMessage(), config.statusFail().toUpperCase());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);
        }else {
            Assert.assertEquals(data.getStatusMessage(), config.statusSuccess().toUpperCase());
        }
    }

    public  GetSet getData() {
        return data;
    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelScanDevice() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.deviceManagementTestCasesExcelPath());
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
