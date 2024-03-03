package com.secpod.testCases.userManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.organizationManagement.GetOrganization;
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


public class AssignOrgToUser {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();

    String requestUserID;
    static GetSet data = new GetSet();
    //    List<Utils.DataProviderObject> dataprovider = new ArrayList<Utils.DataProviderObject>();

    public AssignOrgToUser() throws FileNotFoundException {
    }

    //creating user as prerequisite, org is not assigned to this user
    @BeforeClass
    public void setUp() throws FileNotFoundException {
        //creating a new user
        AddUser adduser = new AddUser();
        adduser.setUp();
        adduser.addUser(config.addUser(), config.statusCode200(), "Create User", config.statusSuccess());
        requestUserID = adduser.getData().getRequestUserName();
        data.setRequestUserName(requestUserID);

        //getting an existing org from the server
        GetOrganization getOrg = new GetOrganization();
        getOrg.getAllOrgs(config.getAllOrg(),config.statusCode200(), "get organization", config.statusSuccess());

        data.setRequestOrgName(getOrg.getData().getResponseOrgName());

    }

    @Test(dataProvider = "theTestData")
    public void assignOrgToUser(String body, String statuscodes, String description, String expectedResult) {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);



        //adding the userid and orgname to be assigned
        if (body.contains("INJECT_USERID") || body.contains("INJECT_ORGNAME")) {
            body = body.replace("INJECT_USERID", data.getRequestUserName().replaceAll("\"", ""))
                    .replace("INJECT_ORGNAME", data.getRequestOrgName());
            System.out.println("AAAAAAAAAAAAAAA" + data.getRequestUserName());
        }



        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();
        System.out.println(body);
        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(10000L));

        System.out.println(response.asString());
//        System.out.println(response.getContentType());
//        System.out.println(response.getHeaders());

        //set request that's used for request
         JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();


        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

        data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
        data.setUserErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());


        Assert.assertEquals(config.contentType(), response.getContentType());
        if ((data.getStatusMessage().equals(config.statusSuccess()))) {
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            data.setRequestUserName(jsonObject.getAsJsonObject("request").getAsJsonObject("parameters")
                    .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                    .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", ""));
            //making sure the userID key is received in response along with success status
            data.setResponseUserName(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("key").toString()
                    .replaceAll("\"", ""));
            Assert.assertEquals(data.getStatusMessage(), expectedResult);
            Assert.assertEquals(data.getResponseUserName(), data.getRequestUserName());
        } else {
            Assert.assertEquals((data.getStatusMessage()), config.statusFail());
            Assert.assertEquals(data.getUserErrorMessage(), expectedResult);
        }
    }

    public GetSet getData() {
        return data;
    }


    @AfterClass
    public void tearDown() throws FileNotFoundException {
            DeleteUser deleteUser = new DeleteUser();
            deleteUser.deleteUser(config.deleteUser().replace("INJECT_USERID", requestUserID),
            config.statusCode200(), "Deleting Users created in Adduser tests", config.statusSuccess());
    }


    @DataProvider(name = "theTestData")
    public Object[][] excelAssignOrgToUser() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.userManagementTestCasesExcelPath());
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

