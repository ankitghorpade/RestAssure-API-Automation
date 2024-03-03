package com.secpod.testCases.complianceManagement;

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
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class AddBenchmark {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    static GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();

    public AddBenchmark() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        removeAccount.setUp();
        data.setRequestOrgName(removeAccount.getData().getRequestOrgName());
        data.setRequestAccName(removeAccount.getData().getRequestAccName());

    }

    @Test(dataProvider = "theTestData")
    public void addBenchmark(String body, String statuscodes, String description, String expectedResult) {
        System.out.println("\n####################" + description + "#################\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_TARGET_ACCOUNT_NAME")) {
            body = body.replace("INJECT_TARGET_ACCOUNT_NAME", data.getRequestAccName().replaceAll("\"", ""));
        }
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        data.setRequestBenchmarkName(jsonObject.getAsJsonObject("request")
                .getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject()
                .getAsJsonArray("parameter")
                .get(1).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();


        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
        response.then().time(Matchers.lessThanOrEqualTo(15000L));
        System.out.println(response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString().trim()).getAsJsonObject();
        data.setStatusMessage(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setResponseReasonMessage(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
        data.setRequestReasonMessage(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
        data.setKey(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("key").getAsString());

        String RequestKey = "Added " + data.getRequestBenchmarkName() + " for " + data.getRequestAccName();

        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equalsIgnoreCase(config.statusSuccess()))) {
//            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            Assert.assertEquals(data.getStatusMessage(), expectedResult.toUpperCase());
            Assert.assertEquals(data.getKey(), RequestKey);
            
        } else if (expectedResult.contains("INJECT_TARGET_ACCOUNT_NAME")) {
            Assert.assertEquals(data.getRequestReasonMessage(), expectedResult.replace("INJECT_TARGET_ACCOUNT_NAME",
                    data.getRequestAccName()));
        } else {

            Assert.assertEquals((data.getStatusMessage()), config.statusFail().toUpperCase());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        }
    }

    public GetSet getData() {
        return data;
    }

    @AfterClass
    public void deleteBenchmark() throws FileNotFoundException {
        DeleteBenchmark deleteBenchmark = new DeleteBenchmark();
        deleteBenchmark.deleteBenchmark(config.deleteBenchmark()
                        .replace("INJECT_ACCNAME", data.getRequestAccName())
                                .replace("INJECT_BENCHMARKNAME", data.getRequestBenchmarkName()),
                config.statusCode200(), "DELETEBENCHMARK", config.statusDeleteBenchmark());
        System.out.println("\n------------------------------------------\n");

        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelAddBenchmark() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod ,config.complianceManagementTestCasesExcelPath());
        Object[][] arr = new Object[tcs.length][4];
        for (int i = 0; i < tcs.length; i++) {
            arr[i][0] = tcs[i][0];
            arr[i][1] = tcs[i][1];
            arr[i][2] = tcs[i][3];
            arr[i][3] = tcs[i][4];
        }
        System.out.println(Arrays.deepToString(arr));
        return arr;
    }
}
