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

public class GetUserMFAPolicy {


    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    DeleteUser deleteUser = new DeleteUser();
    EnforceMultifactor enforceMultifactor = new EnforceMultifactor();





    public GetUserMFAPolicy() throws FileNotFoundException {
    }


    @BeforeClass
    void setUp() throws FileNotFoundException {
        deleteUser.setUp();

        data.setRequestUserName(deleteUser.getData().getRequestUserName());
        AddMFAPolicy addMfaPolicy = new AddMFAPolicy();
        addMfaPolicy.addMFAPolicy(config.addMFAPolicy(), config.statusCode200(), "addMFAPolicy", config.statusSuccess());



//        data.setRequestUserName(enforceMultifactor.getData().getRequestUserName());

        System.out.println("Print this1 " + data.getRequestUserName());
        System.out.println("Print this2" +config.mfa_policyname());

        enforceMultifactor.enforceMultiFactor(config.enforceMultiFactor().replace("INJECT_POLICYNAME", config.mfa_policyname().replace("\"", ""))
                .replace("INJECT_AUTH_LOGINID",data.getRequestUserName()), config.statusCode200(), "EnforeMultiFactor", config.statusSuccess());
        System.out.println("Print this " + data.getRequestUserName());
//        System.out.println("Print this " + data.getResponseUserName());
    }

    @Test(dataProvider = "theTestData")
    public void getUserMFAPolicy(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);


        if (body.contains("INJECT_USERID")) {
            body = body.replace("INJECT_USERID", data.getRequestUserName().replaceAll("\"", ""));
        }

        System.out.println(body);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        data.setRequestMFAPolicyName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));


        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println(response.asString());

        JsonObject jsonResponseObject = JsonParser.parseString(response.asString()).getAsJsonObject();

//
//        data.setResponseMFAPolicyName(jsonResponseObject.getAsJsonObject().get("policyName").getAsString());
//        System.out.println(data.getResponseMFAPolicyName());
//        System.out.println(data.getRequestMFAPolicyName());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if (response.asString().contains(config.statusFail().replace("f", "F"))) {
            data.setStatusMessage(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
            data.setResponseReasonMessage(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
            Assert.assertEquals((data.getStatusMessage().toLowerCase()), config.statusFail());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        } else {

            data.setResponseMFAPolicyName(jsonResponseObject.getAsJsonObject().get("policyName").toString());

            response.then().assertThat().body(matchesJsonSchema(new File(config.getUserMFAPolicySchemaJson())));
            System.out.println("Print this 11:" + data.getResponseMFAPolicyName());
            System.out.println("Print this 12:" + expectedResult);
            Assert.assertEquals(data.getResponseMFAPolicyName(), expectedResult);

        }

}

    public GetSet getData() {
        return data;
    }



    @AfterClass
    public void tearDown() throws FileNotFoundException {
        WithdrawMultifactor multiFactor = new WithdrawMultifactor();
        multiFactor.withdrawMultiFactor(config.withdrawMultiFactor()
                        .replace("INJECT_AUTH_LOGINID", data.getRequestUserName()),
                config.statusCode200(), "WithdrawMultiFactor", config.statusSuccess());

        RemoveMFAPolicy removeMFAPolicy = new RemoveMFAPolicy();
        removeMFAPolicy.removeMFAPolicy(config.removeMFAPolicy().replace("INJECT_POLICYNAME",
                        config.mfa_policyname().replace("\"", "")),
                config.statusCode200(), "removeMFAPolicy", config.statusSuccess());

        deleteUser.deleteUser(config.deleteUser().replace("INJECT_USERID", data.getRequestUserName()),
                config.statusCode200(), "Deleting Users created in Adduser tests", config.statusSuccess());

        System.out.println("\n------------------------------------------\n");
    }
    @DataProvider(name = "theTestData")
    public Object[][] excelGetUserMFAPolicy() throws IOException {
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


