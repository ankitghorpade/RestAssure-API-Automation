package com.secpod.testCases.multiFactorAuthentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.userManagement.DeleteUser;
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

public class WithdrawMultifactor {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    DeleteUser deleteUser = new DeleteUser();
    EnforceMultifactor enforceMultifactor = new EnforceMultifactor();

    public WithdrawMultifactor() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        enforceMultifactor.setUp();
        data.setRequestUserName(enforceMultifactor.getData().getRequestUserName());
        enforceMultifactor.enforceMultiFactor(config.enforceMultiFactor()
                        .replace("INJECT_POLICYNAME", config.policy_name().replaceAll("\"", ""))
                .replace("INJECT_AUTH_LOGINID", data.getRequestUserName()), config.statusCode200(),
                "EnforeMultiFactor", config.statusSuccess());
    }

    @Test(dataProvider = "theTestData")
    public void withdrawMultiFactor(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_USERID")) {
            body = body.replace("INJECT_USERID", data.getRequestUserName().replaceAll("\"", ""));
        }

        System.out.println(body);
        JsonObject jsonRequestObject = JsonParser.parseString(body).getAsJsonObject();

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println(response.asString());

        JsonObject jsonResponseObject = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setResponseReasonMessage(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setRequestUserName(jsonRequestObject.getAsJsonObject("request").getAsJsonObject("parameters")
                    .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                    .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));
            data.setResponseLoginID(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").toString()
                    .replaceAll("\"", ""));
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            Assert.assertEquals(data.getResponseLoginID(), data.getRequestUserName());
        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        }
    }

    public GetSet getData() {
        return data;
    }

    @AfterClass
    public void tearDown() throws FileNotFoundException {
        deleteUser.deleteUser(config.deleteUser().replace("INJECT_USERID", data.getRequestUserName()),
                config.statusCode200(), "Deleting Users created in Adduser tests", config.statusSuccess());
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelWithdrawMultifactor() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.multiFactorAuthenticatorTestCasesExcelPath());
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

