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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class UpdateMFAPolicy {




    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();


    public UpdateMFAPolicy() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        AddMFAPolicy addMfaPolicy = new AddMFAPolicy();
        addMfaPolicy.addMFAPolicy(config.addMFAPolicy(), config.statusCode200(), "addMFAPolicy", config.statusSuccess());
        data.setRequestMFAPolicyName(config.mfa_policyname().replace("\"", ""));
        data.setRequestPolicyDescription(config.mfa_policy_description().replace("\"", ""));
        data.setRequestAuthenticationPath(config.mfa_authentication_path().replace("\"", ""));
        data.setRequestEnvironmentId(config.mfa_environment_id().replace("\"", ""));
        data.setRequestClientId(config.mfa_client_id().replace("\"", ""));
        data.setRequestUsernameOption(config.mfa_username_option().replace("\"", ""));

    }

    @Test(dataProvider = "theTestData")
    public void updateMFAPolicy(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        System.out.println("Test" +data.getRequestMFAPolicyName());

        if (body.contains("INJECT_POLICYNAME"))  {
            body = body.replace("INJECT_POLICYNAME", data.getRequestMFAPolicyName().replaceAll("\"", ""))
            .replace("INJECT_POLICYDESCRIPTION", data.getRequestPolicyDescription().replaceAll("\"", ""))
            .replace("INJECT_CLIENT_ID", data.getRequestClientId().replaceAll("\"", ""))
            .replace("INJECT_ENVIRONMENT_ID", data.getRequestEnvironmentId().replaceAll("\"", ""))
            .replace("INJECT_AUTHENTICATION_PATH", data.getRequestAuthenticationPath().replaceAll("\"", ""))
            .replace("INJECT_MFA_USERNAME_OPTION", data.getRequestUsernameOption().replaceAll("\"", ""));

        }

        System.out.println(body);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        data.setRequestNewMFAPolicyName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(1).getAsJsonObject().get("value").getAsString().replaceAll("\"", ""));

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


        data.setResponseMFAPolicyName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));
//        data.setResponsePolicyDescription(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
//                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
//                .get(1).getAsJsonObject().get("value").toString());
//        data.setResponseEnvironmentId(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
//                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
//                .get(2).getAsJsonObject().get("value").toString());
//        data.setResponseClientId(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
//                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
//                .get(3).getAsJsonObject().get("value").toString());
//        data.setResponseAuthenticationPath(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
//                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
//                .get(4).getAsJsonObject().get("value").toString());
//        data.setResponseUsernameOption(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
//                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
//                .get(5).getAsJsonObject().get("value").toString());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setResponseMFAPolicyName(jsonResponseObject.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").getAsString());

//            System.out.println("print demo" +data.getResponseMFAPolicyName()+"  "+data.getRequestNewMFAPolicyName());

            Assert.assertEquals(data.getResponseMFAPolicyName(), data.getRequestNewMFAPolicyName());
            Assert.assertEquals(data.getStatusMessage(), expectedResult);

        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            System.out.println("getStatus Message for False::" + data.getStatusMessage());
            System.out.println("fail status in config" +config.statusFail());

            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
            System.out.println(data.getResponseReasonMessage());
            System.out.println(expectedResult);

        }

    }

    public GetSet getData() {
        return data;
    }

    @AfterMethod
    public void tearDown() throws IOException {
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {

            RemoveMFAPolicy removeMFAPolicy = new RemoveMFAPolicy();
            removeMFAPolicy.removeMFAPolicy(config.removeMFAPolicy().replace("INJECT_POLICYNAME", data.getRequestNewMFAPolicyName()),
                    config.statusSuccess(), "removeMFAPolicy", config.statusSuccess());
        }
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelUpdateMFAPolicy() throws IOException {
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

