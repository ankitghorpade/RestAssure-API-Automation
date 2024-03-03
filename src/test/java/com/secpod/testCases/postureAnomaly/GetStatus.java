package com.secpod.testCases.postureAnomaly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.APIHelper;
import com.secpod.config.Config;
import com.secpod.utils.GetSet;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;


public class GetStatus {
    public static Response response;
    Config config = new Config();
    APIHelper apiHelper = new APIHelper();
    GetSet data = new GetSet();

    public GetStatus() throws FileNotFoundException {
    };

    @Test(dataProvider = "theTestData")
    public void getStatus(String body, String statuscodes, String description, String expectedResult) throws InterruptedException {

        System.out.println("\n###############" + description + "###############\n");

        String APIUrl = config.pa_url() + "getStatus";
        System.out.println(APIUrl);


        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", config.SAML())
                .setBody(body)
                .build();

        long startTime = System.currentTimeMillis();
        long timeout = 300000;  // Timeout (5 minutes)
        JsonObject jsonObject1 = null;


        // Poll the PA scanner status until the scan completes or timeout is reached
        while (System.currentTimeMillis() - startTime < timeout) {
            response = given().relaxedHTTPSValidation().spec(requestspec).when().post(APIUrl);
            //response.then().time(Matchers.lessThanOrEqualTo(5000L));

            jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();

            String actual_result = jsonObject1.get("message").getAsString().replaceAll("\"", "");
            System.out.println("expected:: "+expectedResult.replaceAll("\"", ""));
            System.out.println("Actual:: "+actual_result);
            System.out.println(response.asString());
            System.out.println();


            // Check if the scan has completed
            if (actual_result.equals(expectedResult.replaceAll("\"", ""))) {
                System.out.println(response.asString());
                break;  // Exit the loop if the scan has completed
            }
            Thread.sleep(5000); // Adding a delay before polling again
//            try {
//                Thread.sleep(5000);  // Poll every 5 seconds
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        String finalStatus = jsonObject1.get("message").getAsString().replaceAll("\"", "");;
        Assert.assertEquals(finalStatus, expectedResult.replaceAll("\"", ""));
    }


    public  GetSet getData() {
        return data;
    }


    @DataProvider(name = "theTestData")
    public Object[][]  excelGetStatus() throws IOException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        Object[][] tcs = apiHelper.apiDataGenerator(nameofCurrMethod, config.postureAnomalyTestCasesExcelPath());
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
