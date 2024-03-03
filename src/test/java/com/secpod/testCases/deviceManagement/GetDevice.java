package com.secpod.testCases.deviceManagement;

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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class GetDevice {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();
    JsonArray getDeviceData;
    public GetDevice() throws FileNotFoundException {
    }


    @Test(dataProvider = "theTestData")
    void getDevice(String body, String statuscodes, String urlParam, String description, String expectedResult) throws IOException {
        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.configs_url() + "?accountid=" + urlParam;
        System.out.println(APIUrl);

        String getDeviceValue = JsonParser.parseString(body).getAsJsonObject().getAsJsonObject("request")
                .getAsJsonObject("parameters")
                .getAsJsonArray("parameterset").get(0).getAsJsonObject().getAsJsonArray("parameter")
                .get(0).getAsJsonObject().get("value").toString().replaceAll("\"", "");

        if (body.contains("null")) {
            body = body.replace("\"parameterset\":[{\"parameter\":[{\"key\":\"null\",\"value\":\"null\"}]}]", "");
        }
        System.out.println(body);

        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .addFormParam("data", body)
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
        response.then().time(Matchers.lessThanOrEqualTo(5000L));
        System.out.println(response.asString());

        try {
            getDeviceData = (JsonParser.parseString(response.asString()).getAsJsonObject().get("devices")
                    .getAsJsonObject().get("device").getAsJsonArray());

        }catch (Exception e){
//            e.printStackTrace();
        }

        if (getDeviceData.isEmpty()) {
            System.out.println("empty arrayList::" + response.asString());
            try {
                JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
                data.setStatusMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                        .getAsJsonArray("result").get(0).getAsJsonObject().get("status").toString());
                data.setOrgErrorMessage(jsonObject1.getAsJsonObject("response").getAsJsonObject("results")
                        .getAsJsonArray("result").get(0).getAsJsonObject().get("reason").toString());
                Assert.assertEquals(data.getStatusMessage(), config.statusFail().toUpperCase());
                Assert.assertEquals(data.getOrgErrorMessage(), expectedResult);
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
                response.then().assertThat().body(matchesJsonSchema(new File(config.jsonSchemaValidation6())));
                String hostName = String.valueOf(JsonParser.parseString(response.asString()).getAsJsonObject().get("devices")
                        .getAsJsonObject().get("device").getAsJsonArray().get(0).getAsJsonObject().get("host-name").getAsString());
                String ipAddress = String.valueOf(JsonParser.parseString(response.asString()).getAsJsonObject().get("devices")
                        .getAsJsonObject().get("device").getAsJsonArray().get(0).getAsJsonObject().get("ip-address").getAsString());
                String macAddress = String.valueOf(JsonParser.parseString(response.asString()).getAsJsonObject().get("devices")
                        .getAsJsonObject().get("device").getAsJsonArray().get(0).getAsJsonObject().get("mac-address").getAsString());
                String serialnumber = String.valueOf(JsonParser.parseString(response.asString()).getAsJsonObject().get("devices")
                        .getAsJsonObject().get("device").getAsJsonArray().get(0).getAsJsonObject().get("serialNumber").getAsString());
                System.out.println("hostName::" + hostName +"\n" + "IP::" + ipAddress + "\n" + "MAC::" + macAddress + "\n"
                        + "serialNumber::" + serialnumber);

            }
        }

    public  GetSet getData() {
        return data;
    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelGetDevice() throws IOException {
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
