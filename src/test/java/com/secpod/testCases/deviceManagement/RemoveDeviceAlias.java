package com.secpod.testCases.deviceManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.organizationManagement.DeleteOrg;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
public class RemoveDeviceAlias {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    DeleteOrg deleteOrg = new DeleteOrg();

    public RemoveDeviceAlias() throws FileNotFoundException {
    }

    //creating the org & acc & adding and updating device
    @BeforeClass
    public void setUp() throws IOException {
        UpdateDeviceAlias updateDeviceAlias = new UpdateDeviceAlias();
        updateDeviceAlias.setUp();

        data.setRequestOrgName(updateDeviceAlias.getData().getRequestOrgName());
        data.setRequestAccName(updateDeviceAlias.getData().getRequestAccName());
        data.setRequestHostName(updateDeviceAlias.getData().getRequestHostName());

        updateDeviceAlias.updateDeviceAlias(config.updevALIAS().replace("INJECT_HOSTNAME", data.getRequestHostName())
                        .replace("INJECT_ALIAS", data.getRequestHostName() +
                                RandomStringUtils.randomNumeric(2)),
                config.statusCode200(), data.getRequestAccName(), "UPDATE DEVICE ALIAS",
                config.statusSuccess().toUpperCase());
        data.setRequestAliasName(updateDeviceAlias.getData().getRequestAliasName());

        System.out.println("\n*********************************************\n");


    }

    @Test(dataProvider = "theTestData")
    public void removeDeviceAlias(String body, String statuscodes, String urlParam, String description, String expectedResult) {
        System.out.println("\n###############" + description + "###############\n");

        if (urlParam.contains("INJECT_ACCNAME")){
            urlParam = urlParam.replace("INJECT_ACCNAME",data.getRequestAccName());
        }

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        if (body.contains("INJECT_ALIAS")) {
            body = body.replace("INJECT_ALIAS", data.getRequestAliasName()
                    .replaceAll("\"", ""));
        }

        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        data.setRequestAliasName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").getAsString());

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));

        System.out.println(response.asString());

        Assert.assertEquals(config.contentType(), response.getContentType());
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());

        Assert.assertEquals(config.contentType(), response.getContentType());

        if ((data.getStatusMessage().equals(config.statusSuccess().toUpperCase()))) {

            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));

            data.setResponseAliasName(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").getAsString());

            Assert.assertEquals(data.getRequestAliasName(), data.getResponseAliasName());
            Assert.assertEquals(data.getStatusMessage(), expectedResult);

        } else if ((data.getStatusMessage().equals(config.statusFail().toUpperCase()))) {

            data.setResponseAddDevice(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());

            Assert.assertEquals( data.getResponseAddDevice(), expectedResult);

        }
        else {
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            System.out.println("This is not a Proper Response, Please raise a bug for this case");
        }
    }

    public  GetSet getData() {
        return data;
    }

    //Updating Device Alias Name again if remove is success
    @AfterMethod
    public void upDevice() throws IOException {
        if (String.valueOf(response.statusCode()).equals(config.statusCode200())) {

            if ((data.getStatusMessage().equals(config.statusSuccess().toUpperCase()))) {

                UpdateDeviceAlias updateDeviceAlias1 = new UpdateDeviceAlias();

                updateDeviceAlias1.updateDeviceAlias(config.updevALIAS().replace("INJECT_HOSTNAME",
                                data.getResponseAliasName()).replace("INJECT_ALIAS", data.getResponseAliasName() +
                                RandomStringUtils.randomNumeric(2)), config.statusCode200(),
                        data.getRequestAccName(), "UPDATE DEVICE ALIAS", config.statusSuccess().toUpperCase());

                data.setRequestAliasName(updateDeviceAlias1.getData().getRequestAliasName());
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
    public Object[][]  excelRemoveDeviceAlias() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.deviceManagementTestCasesExcelPath());
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
