package com.secpod.testCases.postureAnomaly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.secpod.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class HealthCheck {

    public static Response response;
    Config config = new Config();

    public HealthCheck() throws FileNotFoundException {
    };

    @Test
    public void paHealthCheck(){
        String APIUrl = config.pa_url();
        String healthCheckURL = APIUrl+"healthCheck";
        System.out.println(healthCheckURL);


        RequestSpecification requestspec = new RequestSpecBuilder()
                .setContentType("application/x-www-form-urlencoded")
                .addHeader("Authorization", config.SAML())
                .build();

        response = given().relaxedHTTPSValidation().spec(requestspec).when().get(healthCheckURL);
        JsonObject jsonObject1 = JsonParser.parseString(response.asString()).getAsJsonObject();
        int statusCode = response.getStatusCode();

        if(statusCode == 200){
            System.out.println(response.asString());
            response.then().assertThat().body(matchesJsonSchema(new File(config.healthCheckSchemaJson())));
            Assert.assertEquals(jsonObject1.get("status").toString(), "\"healthy\"");

        }


    }

}
