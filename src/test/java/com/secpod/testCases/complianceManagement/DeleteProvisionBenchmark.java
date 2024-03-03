package com.secpod.testCases.complianceManagement;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class DeleteProvisionBenchmark {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    ProvisionBenchmark provisionBenchmark = new ProvisionBenchmark();


    public DeleteProvisionBenchmark() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        provisionBenchmark.setUp();
        provisionBenchmark.provisionBenchmark(config.provisionBenchmark(), config.statusCode200(), "provision benchmark",
                config.statusSuccess().toUpperCase());
        data.setRequestAccName(provisionBenchmark.getData().getRequestAccName());
        data.setRequestOrgName(provisionBenchmark.getData().getRequestOrgName());
        data.setRequestBenchmarkName((provisionBenchmark.getData().getRequestBenchmarkName()));
    }

    @Test(dataProvider = "theTestData")
    public void deleteProvisionBenchmark(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_ORGNAME") || (body.contains("INJECT_BENCHMARKNAME"))) {
            body = body.replace("INJECT_ORGNAME", data.getRequestOrgName())
                    .replace("INJECT_BENCHMARKNAME", data.getRequestBenchmarkName());
        }

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

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setRequestReasonMessage(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
        data.setResponseReasonMessage(jsonObject1.getAsJsonObject("response")
                .getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equalsIgnoreCase(config.statusSuccess()))) {
//            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation13())));
            Assert.assertEquals(data.getStatusMessage(), config.statusSuccess().toUpperCase());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail().toUpperCase());
            Assert.assertEquals(data.getResponseReasonMessage(), expectedResult);
        }
    }

    public GetSet getData() {
        return data;
    }


    @AfterClass
    public void tearDown() throws IOException {

        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());
    }

    @DataProvider(name = "theTestData")
    public Object[][] excelDeleteProvisionBenchmark() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod,config.complianceManagementTestCasesExcelPath());
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

