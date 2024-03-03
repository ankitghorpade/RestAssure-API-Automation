package com.secpod.testCases.groupManagement;

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
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class DeleteGroup {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();
    DeleteOrg deleteOrg = new DeleteOrg();

    public DeleteGroup() throws FileNotFoundException {
    }

    //creating the org & acc and adding device
    @BeforeClass
    public void setUp() throws IOException {
        removeAccount.setUp();
        data.setRequestOrgName(removeAccount.getData().getRequestOrgName());
        data.setRequestAccName(removeAccount.getData().getRequestAccName());

        AddGroup addGroup = new AddGroup();

        addGroup.addGroup(config.addGROUP(), config.statusCode200(), data.getRequestAccName(),
                "ADDING GROUP", config.statusSuccess().toUpperCase());

        System.out.println("\n*********************************************\n");
        data.setRequestGroupName(addGroup.getData().getRequestGroupName());

    }

    @Test(dataProvider = "theTestData")
    public void deleteGroup(String body, String statuscodes, String urlParam, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");

        if (urlParam.contains("INJECT_ACCNAME")){
            urlParam = urlParam.replace("INJECT_ACCNAME",data.getRequestAccName());
        }

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        if (body.contains("INJECT_GROUPNAME")) {
            body = body.replace("INJECT_GROUPNAME", data.getRequestGroupName()
                    .replaceAll("\"", ""));
        }

        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        data.setRequestGroupName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").getAsString().replaceAll("\"", ""));

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));

        System.out.println(response.asString());

        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());

        Assert.assertEquals(config.contentType(), response.getContentType());

        if ((data.getStatusMessage().equals(config.statusSuccess().toUpperCase()))) {

            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));

            data.setResponseGroupName(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").getAsString());

            Assert.assertEquals(data.getRequestGroupName(), data.getResponseGroupName());
            Assert.assertEquals(data.getStatusMessage(), expectedResult);

        } else if ((data.getStatusMessage().equals(config.statusFail().toUpperCase()))) {

            data.setResponseAddDevice(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());

            Assert.assertEquals(data.getResponseAddDevice(), expectedResult);
        }
        else {
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            System.out.println("This is not a Proper Response, Please raise a bug for this case");
        }
    }

    public  GetSet getData() {
        return data;
    }

    //adding group if delete is successful
    @AfterMethod
    public void adGroup() throws IOException {
        if (String.valueOf(response.statusCode()).equals(config.statusCode200())) {

            if ((data.getStatusMessage().equals(config.statusSuccess().toUpperCase()))) {

                AddGroup addGroup1 = new AddGroup();
                addGroup1.addGroup(config.addGROUP(), config.statusCode200(), data.getRequestAccName(),
                        "ADDING GROUP", config.statusSuccess().toUpperCase());
                System.out.println("\n*********************************************\n");
            }
        }
        else {
            System.out.println("This is not a Proper Response, Please raise a bug for this case");
        }
    }

    //deleting the org
    @AfterClass
    public void tearDown() throws FileNotFoundException {
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                config.statusCode200(), "DELETEORG", config.statusSuccess());
    }

    @DataProvider(name = "theTestData")
    public Object[][]  excelDeleteGroup() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.groupManagementTestCasesExcelPath());
        Object[][] arr = new Object[tcs.length][5];
        for (int i = 0; i < tcs.length; i++) {
            arr[i][0] = tcs[i][0];
            arr[i][1] = tcs[i][1];
            arr[i][2] = tcs[i][2];
            arr[i][3] = tcs[i][3];
            arr[i][4] = tcs[i][4];
        }
        return arr;
    }

}
