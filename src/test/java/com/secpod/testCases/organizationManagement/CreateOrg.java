package com.secpod.testCases.organizationManagement;

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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class CreateOrg {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    static GetSet data = new GetSet();
    //    List<Utils.DataProviderObject> dataprovider = new ArrayList<Utils.DataProviderObject>();

    public CreateOrg() throws FileNotFoundException {
    }

    @Test(dataProvider = "theTestData")
    public void createOrg(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        data.setRequestOrgName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));

        if (body.contains("INJECT_ORGNAME")){
            body = body.replace("INJECT_ORGNAME", data.getRequestOrgName().replaceAll("\"", ""));
        }

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();
        System.out.println(body);
        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));

        System.out.println(response.asString());
//        System.out.println(response.getContentType());
//        System.out.println(response.getHeaders());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setResponseOrgName(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").toString()
                    .replaceAll("\"", ""));
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            Assert.assertEquals(data.getResponseOrgName(), data.getRequestOrgName().trim());
        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);
        }
    }

    public  GetSet getData() {
        return data;
    }



    @AfterMethod
    public void tearDown() throws FileNotFoundException {
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            DeleteOrg deleteOrg = new DeleteOrg();
            deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getResponseOrgName()),
                    config.statusCode200(), "DELETEORG", config.statusSuccess());
            System.out.println("\n------------------------------------------\n");
        }

    }


    @DataProvider(name = "theTestData")
    public Object[][] excelCreateOrg () throws IOException {
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

