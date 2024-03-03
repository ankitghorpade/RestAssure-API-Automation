package com.secpod.testCases.configManagement;

import com.google.gson.JsonArray;
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
import org.hamcrest.Matchers;
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

public class GetConfig {


    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    JsonArray getConfigData;
    JsonObject getFailedConfigData;
    AddConfig addconfig = new AddConfig();

    RemoveAccount removeAccount = new RemoveAccount();

    public GetConfig() throws FileNotFoundException {
    }
    @BeforeClass
    void setUp() throws IOException {
        addconfig.setUp();
        data.setRequestAccName(addconfig.getData().getRequestAccName());
        data.setRequestOrgName(addconfig.getData().getRequestOrgName());

        addconfig.addConfig(config.addconfig(),config.statusCode200(),data.getRequestAccName(),"ADDCONFIG",
                config.statusSuccess().toUpperCase());
        data.setRequestAddConfig(addconfig.getData().getResponseAddConfig());
    }

    @Test(dataProvider = "theTestData")
    void getConfig(String body, String statuscodes, String urlParam, String description, String expectedResult)
            throws IOException {
        System.out.println("\n###############" + description + "###############\n");


        if (urlParam.contains("INJECT_ACCNAME")) {
            urlParam = urlParam.replace("INJECT_ACCNAME", data.getRequestAccName());

        }
        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

            if (body.contains("INJECT_CONFIGNAME")) {
                body = body.replace("INJECT_CONFIGNAME", data.getRequestAddConfig()
                        .replaceAll("\"", ""));
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
        getConfigData = jsonObject1.getAsJsonArray("settings");  // Access the "settings" array


            if (!getConfigData.isEmpty()) { // Check if the array is not empty
                response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation14())));

                data.setRequestGetConfig(getConfigData.get(0).getAsJsonObject()
                        .getAsJsonObject("configuration")
                        .get("name").getAsString());
                data.setResponseGetConfig(getConfigData.get(0)
                        .getAsJsonObject().getAsJsonObject("configuration")
                        .get("name").getAsString());

                Assert.assertEquals(data.getRequestGetConfig(), data.getResponseGetConfig());  //config Name validation
                Assert.assertEquals(config.contentType(), response.getContentType());  //content type validation

            }
            else {
                Assert.assertEquals(config.statusFail(), expectedResult);
                System.out.println("Empty array due to invalid config name : " + response.asString());
            }
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
    public Object[][]  excelGetConfig() throws IOException {
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
