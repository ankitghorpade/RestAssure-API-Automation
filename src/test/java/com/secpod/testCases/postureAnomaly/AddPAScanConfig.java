package com.secpod.testCases.postureAnomaly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.accountManagement.GetAccount;
import com.secpod.testCases.organizationManagement.DeleteOrg;
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

public class AddPAScanConfig {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    GetAccount getAccount = new GetAccount();


    public AddPAScanConfig() throws FileNotFoundException {
    };

    @BeforeClass
    public void setUp() throws FileNotFoundException {
        getAccount.setUp();
        getAccount.getAccount(config.getACC(), config.statusCode200(), "GET ACCOUNT", config.statusSuccess());
        data.setAccID(getAccount.getData().getAccID());
        data.setResponseOrgName(getAccount.getData().getResponseOrgName());
        data.setResponseAccName(getAccount.getData().getResponseAccName());
        data.setAccID(getAccount.getData().getAccID());
    }

    @Test(dataProvider = "theTestData")
    public void addPAScanConfig(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");
        String APIUrl = config.configs_url();
        System.out.println(APIUrl);



        if ((body.contains("INJECT_ACCNAME")) || (body.contains("INJECT_ORGNAME"))) {
            body = body.replace("INJECT_ACCNAME", data.getResponseAccName())
                    .replace("INJECT_ORGNAME", data.getResponseOrgName());
        }
        System.out.println(body);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();


        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println(response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(expectedResult))) {
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            // Assert the account ID from the response
            Assert.assertEquals(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject()
                    .get("key").toString().replaceAll("\"", ""),data.getAccID());
        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
        }

    }


    public  GetSet getData() {
        return data;
    }


    @AfterClass
    public void tearDown() throws FileNotFoundException {
        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getResponseOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());
    }



    @DataProvider(name = "theTestData")
    public Object[][]  excelAddPAScanConfig() throws IOException {
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
