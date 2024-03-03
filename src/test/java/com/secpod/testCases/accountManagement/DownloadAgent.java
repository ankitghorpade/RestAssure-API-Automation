package com.secpod.testCases.accountManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.testCases.organizationManagement.DeleteOrg;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class DownloadAgent {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();

    public DownloadAgent() throws FileNotFoundException {
    }

    @BeforeClass
    void setUp() throws FileNotFoundException {
        removeAccount.setUp();
        data.setRequestOrgName(removeAccount.getData().getRequestOrgName());
        data.setRequestAccName(removeAccount.getData().getRequestAccName());
    }

    @Test(dataProvider = "theTestData")
    void downloadAgent(String body, String statuscodes, String description, String expectedResult) throws IOException {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url();
        System.out.println(APIUrl);

        if (body.contains("INJECT_ACCNAME")) {
            body = body.replace("INJECT_ACCNAME", data.getRequestAccName()
                    .replaceAll("\"", ""));
        }
        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
//        System.out.println(response.asString());

        if (Objects.equals(response.getContentType(), config.contentType())){
            System.out.println(response.asString());
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
            data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
            data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
            Assert.assertEquals((data.getStatusMessage()), config.statusFail().toUpperCase());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);

        }else {
            //Checking Agent installer zip file exist or not
            File agentInstallerZipFile = new File("agentInstaller.zip");
            agentInstallerZipFile.delete();
            Files.copy(response.asInputStream(), agentInstallerZipFile.toPath());
//        System.out.println(agentInstallerZipFile.length());
            String totalSize = FileUtils.byteCountToDisplaySize(agentInstallerZipFile.length());
            System.out.println("agentInstallerZipFileSize::" + totalSize);
            Assert.assertEquals(totalSize, expectedResult.replace("\"", "" ));
            Assert.assertEquals(config.contentType1(), response.getContentType());
        }
    }

    public  GetSet getData() {
        return data;
    }


    @AfterClass
    public void tearDown() throws FileNotFoundException {
        removeAccount.removeAccount(config.removeACC().replace("INJECT_ACCNAME", data.getRequestAccName()),
                config.statusCode200(), "REMOVEACCOUNT", config.statusSuccess());
        DeleteOrg deleteOrg = new DeleteOrg();
        deleteOrg.deleteOrg(config.deleteORG().replace("INJECT_ORGNAME", data.getRequestOrgName()),
                config.statusSuccess(), "DELETEORG", config.statusSuccess());
    }

    @DataProvider(name = "theTestData")
    public Object[][]  excelDownloadAgent() throws IOException {
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
