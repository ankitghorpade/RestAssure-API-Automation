package com.secpod.testCases.accountManagement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.organizationManagement.DeleteOrg;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetAccount {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();
    JsonArray getAcc;

    public GetAccount() throws FileNotFoundException {
    }

    @BeforeClass
    public void setUp() throws FileNotFoundException {
        removeAccount.setUp();
        data.setRequestAccName(removeAccount.getData().getRequestAccName());
    }

    @Test(dataProvider = "theTestData")
    public void getAccount(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_ACCNAME")) {
            body = body.replace("INJECT_ACCNAME", data.getRequestAccName()
                    .replaceAll("\"", ""));
        }

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        data.setAccID(String.valueOf(jsonObject1.getAsJsonObject().get("accounts").getAsJsonArray().get(0)
                .getAsJsonObject().get("accountinfo").getAsJsonObject().get("id").getAsString()));
        getAcc = ((jsonObject1.getAsJsonObject().get("accounts").getAsJsonArray()));
        if (getAcc.isEmpty()){
            System.out.println("empty arrayList::" + response.asString());
        }else {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation3())));
            data.setResponseAccName(String.valueOf(jsonObject1.getAsJsonObject().get("accounts").getAsJsonArray().get(0)
                    .getAsJsonObject().get("accountinfo").getAsJsonObject().get("name").getAsString()));
            data.setResponseOrgName(String.valueOf(jsonObject1.getAsJsonObject().get("accounts").getAsJsonArray().get(0)
                    .getAsJsonObject().get("accountinfo").getAsJsonObject().get("organization").getAsString()));
            Assert.assertEquals(data.getRequestAccName(), data.getResponseAccName());
        }
    }

    @AfterMethod
    public void removeAcc() throws FileNotFoundException {
        if (getAcc.isEmpty()){
            System.out.println("empty arrayList::" + response.asString());
        }else {
            removeAccount.removeAccount(config.removeACC().replace("INJECT_ACCNAME", data.getResponseAccName()),
                    config.statusCode200(), "REMOVEACCOUNT", config.statusSuccess());
        }
    }

    @AfterClass
    public void tearDown() throws FileNotFoundException {
        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getResponseOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());
    }
    public GetSet getData() {
        return data;
    }
    @DataProvider(name = "theTestData")
    public Object[][]  excelGetAccount() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.orgAccTestCasesExcelPath());
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
