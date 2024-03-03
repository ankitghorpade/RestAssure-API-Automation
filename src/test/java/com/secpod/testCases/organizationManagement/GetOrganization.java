package com.secpod.testCases.organizationManagement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
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
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetOrganization {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    JsonArray  getOrg;

    public GetOrganization() throws FileNotFoundException {
    }

    @BeforeClass
    public void setUp() throws FileNotFoundException {
        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.setUp();
        data.setRequestOrgName(deleteOrg.getData().getRequestOrgName());
    }

    @Test(dataProvider = "theTestData")
    public void getOrganization(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_ORGNAME")){
            body = body.replace("INJECT_ORGNAME", data.getRequestOrgName()
                    .replaceAll("\"", ""));
        }
        System.out.println(body);
//        body = body.replace("INJECT_ORGNAME", data.getRequestOrgName());

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        data.setRequestOrgName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
//        getOrg = ((jsonObject1.getAsJsonObject().get("organizations").getAsJsonArray()));
        if (response.asString().contains(config.statusFail())){
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation11())));
            data.setStatusMessage(jsonObject1.getAsJsonArray("organizations").get(0).getAsJsonObject()
                    .get("status").toString());
            data.setOrgErrorMessage(jsonObject1.getAsJsonArray("organizations").get(0)
                    .getAsJsonObject().get("reason").toString());
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);
//            System.out.println("empty arrayList::" + response.asString());
        }else {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation2())));
            data.setResponseOrgName(String.valueOf(jsonObject1.getAsJsonObject().get("organizations").getAsJsonArray().get(0)
                    .getAsJsonObject().get("organizationinfo").getAsJsonObject().get("name").getAsString()));
            Assert.assertEquals(data.getRequestOrgName(), data.getResponseOrgName());
        }
    }

    public void getAllOrgs(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);


        System.out.println(body);
//        body = body.replace("INJECT_ORGNAME", data.getRequestOrgName());

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();


        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
//        response.then().time(Matchers.lessThanOrEqualTo(15000L));
        System.out.println(response.asString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        getOrg = ((jsonObject1.getAsJsonObject().get("organizations").getAsJsonArray()));
        if (getOrg.isEmpty()){
            System.out.println("empty arrayList::" + response.asString());
        }else {

            data.setResponseOrgName(String.valueOf(jsonObject1.getAsJsonObject().get("organizations").getAsJsonArray().get(0)
                    .getAsJsonObject().get("organizationinfo").getAsJsonObject().get("name").getAsString()));
            Assert.assertNotNull(data.getResponseOrgName());
        }

    }

    public  GetSet getData() {
        return data;
    }


    @AfterMethod
    public void tearDown() throws FileNotFoundException {
        if ((!Objects.equals(data.getStatusMessage(), config.statusFail()))){
            System.out.println("arrayList::" + response.asString());
        }else {
            DeleteOrg deleteOrg = new DeleteOrg();
            deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getResponseOrgName()),
                    config.statusCode200(), "DELETEORG", config.statusSuccess());
            System.out.println("\n------------------------------------------\n");
        }

    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelGetOrganization() throws IOException {
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
