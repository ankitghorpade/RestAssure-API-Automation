package com.secpod.testCases.organizationManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.accountManagement.RemoveAccount;
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

public class UpdateServiceProvision {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();

    public UpdateServiceProvision() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        removeAccount.setUp();
        data.setRequestOrgName(removeAccount.getData().getRequestOrgName());
        data.setRequestAccName(removeAccount.getData().getRequestAccName());
    }

    @Test(dataProvider = "theTestData")
    void updateAccount(String body, String statuscodes, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_ORGNAME")) {
            body = body.replace("INJECT_ORGNAME", data.getRequestOrgName().replaceAll("\"", ""));
        } else if (body.contains("INJECT_ACCNAME")) {
            body = body.replace("INJECT_ACCNAME", data.getRequestAccName().replaceAll("\"", ""));
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

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
        String orgAccName = (jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").toString()
                    .replaceAll("\"", ""));

        Assert.assertEquals(config.contentType(), response.getContentType());
            if (orgAccName.equals(data.getRequestOrgName())){
                response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
                Assert.assertEquals(data.getStatusMessage(), config.statusSuccess());
                Assert.assertEquals(orgAccName, data.getRequestOrgName());
            }else if(orgAccName.equals(data.getRequestAccName())) {
                response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
                Assert.assertEquals(data.getStatusMessage(), config.statusSuccess());
                Assert.assertEquals(orgAccName, data.getRequestAccName());
            } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);
        }
    }

    public  GetSet getData() {
        return data;
    }

    @AfterClass
    public void tearDown() throws FileNotFoundException {
        if ((data.getStatusMessage().equals(config.statusFail()))) {
            removeAccount.removeAccount(config.removeACC().replace("INJECT_ACCNAME", data.getRequestAccName()),
                    config.statusCode200(), "REMOVEACCOUNT", config.statusSuccess());
            DeleteOrg deleteOrg = new DeleteOrg();
            deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                    config.statusSuccess(), "DELETEORG", config.statusSuccess());
            System.out.println("\n------------------------------------------\n");
        }
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelUpdateServiceProvision() throws IOException {
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
