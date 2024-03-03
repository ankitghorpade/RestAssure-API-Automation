package com.secpod.testCases.multiFactorAuthentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class AddMFAPolicy {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();


    public AddMFAPolicy() throws FileNotFoundException {
    }


    @Test(dataProvider = "theTestData")
    public void addMFAPolicy(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);



        if (body.contains("INJECT_POLICYNAME")) {
            body = body.replace("INJECT_POLICYNAME", config.mfa_policyname().replaceAll("\"", ""))
                    .replace("INJECT_POLICYDESCRIPTION", config.mfa_policy_description().replaceAll("\"", ""))
                    .replace("INJECT_CLIENT_ID", config.mfa_client_id().replaceAll("\"", ""))
                    .replace("INJECT_ENVIRONMENT_ID", config.mfa_environment_id().replaceAll("\"", ""))
                    .replace("INJECT_AUTHENTICATION_PATH", config.mfa_authentication_path().replaceAll("\"", ""))
                    .replace("INJECT_MFA_USERNAME_OPTION", config.mfa_username_option().replaceAll("\"", ""));

        }



        //Value Set in Config.txt for PingOne

        config.mfa_policyname();
        config.mfa_policy_description();
        config.mfa_environment_id();
        config.mfa_client_id();
        config.mfa_authentication_path();
        config.mfa_username_option();


        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        System.out.println(body);


        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        System.out.println(response.asString());

        JsonObject jsonResponseObject = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonResponseObject.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setResponseReasonMessage(jsonResponseObject
                .getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setResponseMFAPolicyName(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").toString());
            Assert.assertEquals(data.getResponseMFAPolicyName(), config.mfa_policyname());
            Assert.assertEquals(data.getStatusMessage(), expectedResult);

        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        }

    }

    public GetSet getData() {
        return data;
    }

    @AfterMethod
    public void tearDown() throws IOException {
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {

            RemoveMFAPolicy removeMFAPolicy = new RemoveMFAPolicy();
            removeMFAPolicy.removeMFAPolicy(config.removeMFAPolicy().replace("INJECT_POLICYNAME",
                            config.mfa_policyname().replace("\"", "")),
                    config.statusCode200(), "removeMFAPolicy", config.statusSuccess());
        }
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelAddMFAPolicy() throws IOException {
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



