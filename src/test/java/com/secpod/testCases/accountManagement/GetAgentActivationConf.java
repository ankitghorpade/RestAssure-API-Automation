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
import net.lingala.zip4j.ZipFile;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetAgentActivationConf {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    RemoveAccount removeAccount = new RemoveAccount();
    List<String> list = new ArrayList<String>();
    List<String> fieldNames = new ArrayList<String>();

    public GetAgentActivationConf() throws FileNotFoundException {
    }


    @BeforeClass
    void setUp() throws FileNotFoundException {
        removeAccount.setUp();
        data.setRequestOrgName(removeAccount.getData().getRequestOrgName());
        data.setRequestAccName(removeAccount.getData().getRequestAccName());
    }

    @Test(dataProvider = "theTestData")
    void getAgentActivationConf(String body, String statuscodes, String description, String expectedResult) throws IOException, InterruptedException {
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
            File agentConfFile = new File("agentConfFile.zip");
            agentConfFile.delete();
            Files.copy(response.asInputStream(), agentConfFile.toPath());
//        System.out.println(agentInstallerZipFile.length());
//        String totalSize = FileUtils.byteCountToDisplaySize(agentConfFile.length());
//        System.out.println("agentInstallerZipFileSize::" + totalSize);
            try {
                ZipFile zipFile = new ZipFile(agentConfFile);
                zipFile.extractAll("agentConf");
                zipFile.close();
                //Creating a File object for directory
                File directoryPath = new File("agentConf");
                //List of all files and directories
                String[] contents = directoryPath.list();
                System.out.println("List of files and directories in the specified directory:");
                for (int i = 0; i < Objects.requireNonNull(contents).length; ) {
                    System.out.println(contents[i]);
                    list = Files.readAllLines(Path.of(directoryPath + "/" + contents[i]), Charset.defaultCharset());
                    System.out.println("Contents of the array list: ");
//                System.out.println((list.get(i + 1)));
                    fieldNames.add(list.get(i + 1).replace("ServerURL=", ""));
                    System.out.println(fieldNames.get(i));
                    Assert.assertEquals(fieldNames.get(i), config.configs_url().replace("/perform", ""));
                    Assert.assertEquals(config.contentType1(), response.getContentType());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    public Object[][]  excelGetAgentActivationConf() throws IOException {
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
