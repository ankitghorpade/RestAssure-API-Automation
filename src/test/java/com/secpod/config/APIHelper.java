package com.secpod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.secpod.utils.GetSet;
import com.secpod.utils.ParsingExcel;
import com.secpod.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHelper {
    public APIHelper() throws FileNotFoundException {
    }
    private final JsonElement jsonData = JsonParser.parseReader(new FileReader("resources/config.txt"));
    GetSet apiValues = new GetSet();
    Utils utils = new Utils();
    ParsingExcel parsingExcel = new ParsingExcel();


    //Getting API values from the individual Excel sheet
    public Object[][] apiDataGenerator(String excelSheet, String excelFilePath) throws IOException {
        List<Utils.DataProviderObject> data = new ArrayList<Utils.DataProviderObject>();
        int rowValueCount = parsingExcel.getRowValueCount(excelSheet, excelFilePath);
            //Looping through all the values from the Excel sheet and converting it to 2D array
            //Reading the format of deleteOrg API and replacing the values fetched from the Excel sheet
        String pathOfIndividualJsonFile = (jsonData.getAsJsonObject().get(excelSheet)).getAsString();
        for (int rowValue = 1; rowValue <= rowValueCount; rowValue++){
            apiValues.setApiValue(parsingExcel.regressionExcelFile(rowValue, excelSheet, excelFilePath));
            if (apiValues.getApiValue().isEmpty()){
                continue;
            }
            try {
                String keyValueContent = JsonParser.parseReader(new FileReader(pathOfIndividualJsonFile)).toString()
                        .replace("INJECT_NEWORGNAME", getValue(apiValues.getApiValue().get("neworgname")))
                        .replace("INJECT_NEWACCNAME", getValue(apiValues.getApiValue().get("newaccname")))
                        .replace("INJECT_ACCNAME", getValue(apiValues.getApiValue().get("account")))
                        .replace("INJECT_EMAIL", getValue(apiValues.getApiValue().get("email")))
                        .replace("INJECT_ADMINID", getValue(apiValues.getApiValue().get("adminid")))
                        .replace("INJECT_SUBSCRIPTIONS", getValue(apiValues.getApiValue().get("numberofsubscriptions")))
                        .replace("INJECT_AGENTAUTOUPGRADE", getValue(apiValues.getApiValue().get("agentautoupgrade")))
                        .replace("INJECT_ENDDATE", getValue(apiValues.getApiValue().get("enddate")))
                        .replace("INJECT_ORGNAME", getValue(apiValues.getApiValue().get("organization")))
                        .replace("INJECT_TYPE", getValue(apiValues.getApiValue().get("type")))
                        .replace("INJECT_ARCH", getValue(apiValues.getApiValue().get("architecture")))
                        .replace("INJECT_KEY", getValue(apiValues.getApiValue().get("key")))
                        .replace("INJECT_VALUE", getValue(apiValues.getApiValue().get("value")))
                        .replace("INJECT_DSI", getValue(apiValues.getApiValue().get("includeDSI")))
                        .replace("INJECT_HOSTNAME", getValue(apiValues.getApiValue().get("hostname")))
                        .replace("INJECT_OS", getValue(apiValues.getApiValue().get("os")))
                        .replace("INJECT_SERVICEPROVISION", getValue(apiValues.getApiValue().get("serviceProvision")))
                        .replace("INJECT_ENABLED", getValue(apiValues.getApiValue().get("enabled")))
                        .replace("INJECT_ROLE", getValue(apiValues.getApiValue().get("user/account/organization")))
                        .replace("INJECT_LOGIN", getValue(apiValues.getApiValue().get("role")))
                        .replace("INJECT_OS", getValue(apiValues.getApiValue().get("os")))
                        .replace("INJECT_IP", getValue(apiValues.getApiValue().get("ip")))
                        .replace("INJECT_MAC", getValue(apiValues.getApiValue().get("mac")))
                        .replace("INJECT_ALIAS", getValue(apiValues.getApiValue().get("alias")))
                        .replace("INJECT_GROUPDESCRIPTION", getValue(apiValues.getApiValue().get("group_description")))
                        .replace("INJECT_GROUPNAME", getValue(apiValues.getApiValue().get("group_name")))
                        .replace("INJECT_NEW_GROUPNAME", getValue(apiValues.getApiValue().get("new_group_name")))
                        .replace("INJECT_PROFILE_NAME", getValue(apiValues.getApiValue().get("profile_name")))
                        .replace("INJECT_CONFIG_NAME", getValue(apiValues.getApiValue().get("config_name")))
                        .replace("INJECT_CONFIGNAME", getValue(apiValues.getApiValue().get("configname")))
                        .replace("INJECT_CONFIGDESC", getValue(apiValues.getApiValue().get("configdesc")))
                        .replace("INJECT_ACCOUNT_ID", getValue(apiValues.getApiValue().get("accountid")))
                        .replace("INJECT_BENCHMARKNAME", getValue(apiValues.getApiValue().get("benchmarkname")))
                        .replace("INJECT_TARGET_ACCOUNT_NAME", getValue(apiValues.getApiValue().get("target_account_name")))
                        .replace("INJECT_TARGET_GROUP", getValue(apiValues.getApiValue().get("target_group")))
                        .replace("INJECT_ACCOUNT_NAME", getValue(apiValues.getApiValue().get("account_name")))
                        .replace("INJECT_TARGET_ORGANIZATION", getValue(apiValues.getApiValue().get("target_organization")))
                        .replace("INJECT_TIME", getValue(apiValues.getApiValue().get("start_time")))
                        .replace("INJECT_MONTH", getValue(apiValues.getApiValue().get("month_of_the_year")))
                        .replace("INJECT_WEEK", getValue(apiValues.getApiValue().get("week_of_the_month")))
                        .replace("INJECT_DAY", getValue(apiValues.getApiValue().get("day_of_the_week")))
                        .replace("INJECT_SCHEDULE", getValue(apiValues.getApiValue().get("schedule")))
                        .replace("INJECT_ACCID",getValue(apiValues.getApiValue().get("account_id")))
                        .replace("INJECT_PA_ID",getValue(apiValues.getApiValue().get("PA_ID")))
                        .replace("INJECT_SCAN",getValue(apiValues.getApiValue().get("scan")))
                        .replace("INJECT_FAMILY", getValue(apiValues.getApiValue().get("family")))
                        .replace("INJECT_PORT", getValue(apiValues.getApiValue().get("port")))
                        .replace("INJECT_DEVICENAME", getValue(apiValues.getApiValue().get("devicename")))
                        .replace("INJECT_INTEGRATIONTYPE", getValue(apiValues.getApiValue().get("integrationtype")))
                        .replace("INJECT_SAVE", getValue(apiValues.getApiValue().get("save")))
                        .replace("INJECT_USERNAME", getValue(apiValues.getApiValue().get("username")))
                        .replace("INJECT_PASSWORD", getValue(apiValues.getApiValue().get("password")))
                        .replace("INJECT_DOMAINNAME", getValue(apiValues.getApiValue().get("domainname")))
                        .replace("INJECT_SSLVERIFY", getValue(apiValues.getApiValue().get("sslverify")))
                        .replace("INJECT_MODE", getValue(apiValues.getApiValue().get("mode")))
                        .replace("INJECT_USERID", getValue(apiValues.getApiValue().get("userID")))
                        .replace("INJECT_USERROLE", getValue(apiValues.getApiValue().get("user_role")))
                        .replace("INJECT_AUTH_LOGINID", getValue(apiValues.getApiValue().get("loginid")))
                        .replace("INJECT_POLICYNAME", getValue(apiValues.getApiValue().get("policyName")))
                        .replace("INJECT_STATUS", getValue(apiValues.getApiValue().get("status")))
                        .replace("INJECT_DESCRIPTION", getValue(apiValues.getApiValue().get("description_of_TCs")))
                        .replace("INJECT_EXPECTED_RESULTS", getValue(apiValues.getApiValue().get("expected_results")))

                        .replace("INJECT_CLIENT_ID", getValue(apiValues.getApiValue().get("clientId")))
                        .replace("INJECT_NEWPOLICYNAME", getValue(apiValues.getApiValue().get("newPolicyName")))
                        .replace("INJECT_AUTHENTICATION_PATH", getValue(apiValues.getApiValue().get("authenticationPath")))
                        .replace("INJECT_ENVIRONMENT_ID", getValue(apiValues.getApiValue().get("environmentId")))
                        .replace("INJECT_POLICYDESCRIPTION", getValue(apiValues.getApiValue().get("policyDescription")))
                        .replace("INJECT_MFA_USERNAME_OPTION", getValue(apiValues.getApiValue().get("usernameOption")));

                data = utils.getDataProviders(keyValueContent, data);
            System.out.println("APIhelper::::::::::" +keyValueContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return utils.convertTo2DArray(data);
    }
     private String getValue(String value) {
        return  (value ==null) ? "":value;
     }

}
