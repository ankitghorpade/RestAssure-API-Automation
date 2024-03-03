package com.secpod.testCases.configManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.accountManagement.RemoveAccount;
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

public class DeleteConfig {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    JsonObject deleteConfigData;

    RemoveAccount removeAccount = new RemoveAccount();

    AddConfig addconfig = new AddConfig();
    public DeleteConfig() throws FileNotFoundException {
    }

    @BeforeClass
    public void setUp() throws IOException {
        addconfig.setUp();
        data.setRequestAccName(addconfig.getData().getRequestAccName());
        data.setRequestOrgName(addconfig.getData().getRequestOrgName());

        addconfig.addConfig(config.addconfig(),config.statusCode200(),data.getRequestAccName(),"ADDCONFIG",
                config.statusSuccess().toUpperCase());
        data.setRequestAddConfig(addconfig.getData().getResponseAddConfig());
        System.out.println("setup part running"+data.getRequestAddConfig());
    }

    @Test(dataProvider = "theTestData")
    public void deleteConfig(String body, String statuscodes, String urlParam,
                             String description, String expectedResult) throws IOException {
        System.out.println("\n###############" + description + "###############\n");

        if (urlParam.contains("INJECT_ACCNAME")){
            urlParam = urlParam.replace("INJECT_ACCNAME", data.getRequestAccName());
        }

        String APIUrl = config.configs_url() + "?accountid=" + urlParam ;
        System.out.println(APIUrl);
        System.out.println(body);


        if (body.contains("INJECT_CONFIGNAME")) {
            body = body.replace("INJECT_CONFIGNAME", data.getRequestAddConfig()
                    .replaceAll("\"", ""));
        }

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println(response.asString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        deleteConfigData = ((jsonObject1.getAsJsonObject().get("response").getAsJsonObject()));

        data.setRequestAddConfig(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result")
                .get(0).getAsJsonObject().get("key").getAsString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((response.asString().contains(config.statusSuccess().toUpperCase()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setResponseAddConfig(jsonObject1.getAsJsonObject("response")
                    .getAsJsonObject("results")
                    .getAsJsonArray("result")
                    .get(0).getAsJsonObject().get("key").getAsString());
            data.setStatusMessage(jsonObject1.getAsJsonObject("response")
                    .getAsJsonObject("results")
                    .getAsJsonArray("result")
                    .get(0).getAsJsonObject().get("status").toString());

            Assert.assertEquals(data.getRequestAddConfig(), data.getResponseAddConfig()); //name validation
            Assert.assertEquals(data.getStatusMessage(), expectedResult);

        } else if (response.asString().contains(config.statusFail().toUpperCase())){
            data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response")
                    .getAsJsonObject("results")
                    .getAsJsonArray("result")
                    .get(0).getAsJsonObject().get("reason").toString());
            data.setResponseAddConfig((
                    jsonObject1.getAsJsonObject().get("response")
                            .getAsJsonObject().get("results")
                            .getAsJsonObject().get("result")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("key").getAsString()));
            data.setStatusMessage(jsonObject1.getAsJsonObject("response")
                    .getAsJsonObject("results")
                    .getAsJsonArray("result")
                    .get(0).getAsJsonObject().get("status").toString());

            Assert.assertEquals(data.getRequestAddConfig(), data.getResponseAddConfig()); //name validation
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult); // reason validation
            Assert.assertEquals((data.getStatusMessage()), config.statusFail().toUpperCase()); //status validation
        }
        else{
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            System.out.println("empty arrayList::");
        }
    }
    public GetSet getData() {
        return data;
    }

    @AfterClass
    public void tearDown() throws IOException {

        removeAccount.removeAccount(config.removeACC().replace("INJECT_ACCNAME", data.getRequestAccName()),
                config.statusCode200(), "REMOVEACCOUNT", config.statusSuccess());

        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());

    }
    @DataProvider(name = "theTestData")
    public Object[][] excelDeleteConfig() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.configManagementTestCasesExcelPath());
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