package com.secpod.testCases.deviceManagement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetDeviceReport {

    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    String dsiValue;
    public GetDeviceReport() throws FileNotFoundException {
    }


    @Test(dataProvider = "theTestData")
    void getDeviceReport(String body, String statuscodes, String urlParam, String description, String expectedResult) throws IOException, InterruptedException {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        dsiValue = JsonParser.parseString(body).getAsJsonObject().getAsJsonObject("request")
                .getAsJsonObject("parameters").getAsJsonArray("parameterset").get(0)
                .getAsJsonObject().getAsJsonArray("parameter").get(1).getAsJsonObject().get("value")
                .toString().replaceAll("\"", "");
        System.out.println("DSIVALUE:::" + dsiValue);

        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
//        System.out.println(response.asString());

        if (Objects.equals(response.getContentType(), config.contentType())) {
            System.out.println(response.asString());
            response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation1())));
            JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
            data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
            data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                    .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
            Assert.assertEquals((data.getStatusMessage()), config.statusFail().toUpperCase());
            Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);

        } else {
            //Checking Agent installer zip file exist or not
            File vulnerableDSIDatazip = new File("vulnerabilityDSIJson.zip");
            vulnerableDSIDatazip.delete();
            FileUtils.deleteDirectory(new File("vulnerableDSIData"));
            Files.copy(response.asInputStream(), Path.of("vulnerabilityDSIJson.zip"));
            ZipFile zipFile = new ZipFile(vulnerableDSIDatazip);
            zipFile.extractAll("vulnerableDSIData");
            zipFile.close();
            //Creating a File object for directory
            File directoryPath = new File("vulnerableDSIData/results");
            //List of all files and directories
            String[] contents = directoryPath.list();
            System.out.println("List of files and directories in the specified directory:");
            for (int i = 0; i < Objects.requireNonNull(contents).length; i++) {
                System.out.println("DSIVALUE::" + contents[i].contains("DSI"));
                long bytes = Files.size(Path.of(directoryPath + "/" + contents[i]));
                int vulnDSISize = (int) (bytes / 1024);
                int expectedvulnDSISize = (Integer.parseInt(expectedResult.replace("\"", "")
                        .replace(" KB", "")));
                if (contents.length >= 3 ) {
                    System.out.println("vulnDSIJson is downloaded successfully for all the devices in the accountManagement");
                    break;
                }
                else if (contents[i].contains("DSI")){
                    if (Objects.equals(dsiValue, "true") && vulnDSISize >= expectedvulnDSISize ) {
                        System.out.println("DSI json validated::" + contents[i] +"\nvulnDSISize::" + vulnDSISize + " KB");
                    }else{
                        System.out.println("VulnerabilityDSI json doestn't match the condition\n vulnDSISize::" + vulnDSISize
                                + " KB\nexpectedvulnDSISize::" + expectedvulnDSISize + " KB");
                        Assert.assertEquals(config.statusSuccess(), config.statusFail());
                    }
                } else if (Objects.equals(dsiValue, "false") && vulnDSISize >= expectedvulnDSISize) {
                    System.out.println("Vulnerability json validated::" + contents[i] + "\nvulnDSISize::" + vulnDSISize + " KB");
                }
            }
        }
    }



        public GetSet getData () {
            return data;
        }


        @DataProvider(name = "theTestData")
        public Object[][]  excelGetDeviceReport() throws IOException {
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