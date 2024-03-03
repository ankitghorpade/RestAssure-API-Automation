package com.secpod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.secpod.utils.GetSet;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;

public class Config {

    GetSet data = new GetSet();
    public Config() throws FileNotFoundException {
    }

    private final JsonElement jsonData = JsonParser.parseReader(new FileReader("resources/config.txt"));
//    @Test
    public String configs_url() {
        return (jsonData.getAsJsonObject().getAsJsonObject("config").get("URL").getAsString());
    }
    public String pa_url() {
        return (jsonData.getAsJsonObject().getAsJsonObject("config").get("PA_URL").getAsString());
    }

    public String SAML() {
        return (jsonData.getAsJsonObject().get("saml")).getAsString();
    }

    public String statusCode200() {
        return (jsonData.getAsJsonObject().getAsJsonObject("status_codes").get("status_200").getAsString());
    }

    public String statusSuccess() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("status_success").toString());
    }

    public String statusFail() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("status_fail")).toString();
    }
    public String statusTrue() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("status_true")).toString();
    }

    public String statusFalse() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("status_false")).toString();
    }

    public String statusOrgErrorMessage() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("status_org_error_message")).toString();
    }

    public String regressionTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("regressionTestCases")).getAsString();
    }

    public String orgAccTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("orgAccTestCases")).getAsString();
    }
    public String deviceManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("deviceManagementTestCases").getAsString());
    }
    public String groupManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("groupManagementTestCases").getAsString());
    }
    public String configManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("configManagementTestCases").getAsString());
    }
    public String complianceManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("complianceManagementTestCases").getAsString());
    }
    public String postureAnomalyTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("postureAnomalyTestCases")).getAsString();
    }

    public String contentType() {
        return (jsonData.getAsJsonObject().get("contentType")).getAsString();
    }
    public String contentType1() {
        return (jsonData.getAsJsonObject().get("contentType1")).getAsString();
    }

    public String accName() {
        return (jsonData.getAsJsonObject().get("accountName")).getAsString();
    }


    public String jsonSchemaValidation1() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation1").getAsString());
    }

    public String jsonSchemaValidation2() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation2").getAsString());
    }

    public String jsonSchemaValidation3() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation3").getAsString());
    }

    public String jsonSchemaValidation4() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation4").getAsString());
    }

    public String getAssetsSchemaJson() {
        return (jsonData.getAsJsonObject().get("getAssets").getAsString());
    }
    public String getProfileNamesSchemaJson() {
        return (jsonData.getAsJsonObject().get("getProfileNames").getAsString());
    }
    public String getProfileReportSchemaJson() {
        return (jsonData.getAsJsonObject().get("getProfileReport").getAsString());
    }
    public String getReportApisSchemaJson() {
        return (jsonData.getAsJsonObject().get("getReportApis").getAsString());
    }
    public String getReportNamesSchemaJson() {
        return (jsonData.getAsJsonObject().get("getReportNames").getAsString());
    }
    public String jsonSchemaValidation6() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation6").getAsString());
    }
    public String jsonSchemaValidation7() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation7").getAsString());
    }
    public String jsonSchemaValidation8() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation8").getAsString());
    }
    public String jsonSchemaValidation9() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation9").getAsString());
    }
    public String jsonSchemaValidation10() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation10").getAsString());
    }
    public String jsonSchemaValidation11() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation11").getAsString());
    }
    public String jsonSchemaValidation12() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation12").getAsString());
    }
    public String jsonSchemaValidation13() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation13").getAsString());
    }
    public String jsonSchemaValidation14() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation14").getAsString());
    }
    public String jsonSchemaValidation15() {
        return (jsonData.getAsJsonObject().get("jsonSchemaValidation15").getAsString());
    }


    public String deletePAScanConfigSchemaJson() {
        return (jsonData.getAsJsonObject().get("deletePAScanConfig").getAsString());
    }
    public String getConfigurationSchemaJson() {
        return (jsonData.getAsJsonObject().get("getConfiguration").getAsString());
    }
    public String getConfigurationStatusSchemaJson() {
        return (jsonData.getAsJsonObject().get("getConfigurationStatus").getAsString());
    }
    public String getPAScanConfigSchemaJson() {
        return (jsonData.getAsJsonObject().get("getPAScanConfig").getAsString());
    }
    public String getWhitelistSchemaJson() {
        return (jsonData.getAsJsonObject().get("getWhitelist").getAsString());
    }
    public String healthCheckSchemaJson() {
        return (jsonData.getAsJsonObject().get("healthCheck").getAsString());
    }

    public String postConfigurationSchemaJson() {
        return (jsonData.getAsJsonObject().get("postConfiguration").getAsString());
    }
    public String getADConfigurationSchemaJson() {
        return (jsonData.getAsJsonObject().get("getADConfiguration").getAsString());
    }
    public String getMFAPolicySchemaJson() {
        return (jsonData.getAsJsonObject().get("getMFAPolicy").getAsString());
    }

    public String getUserMFAPolicySchemaJson() {
        return (jsonData.getAsJsonObject().get("getUserMFAPolicy").getAsString());
    }

    public String getHostResultsSchemaJson() {
        return (jsonData.getAsJsonObject().get("getHostResults").getAsString());
    }
    public String getHostResults1SchemaJson() {
        return (jsonData.getAsJsonObject().get("getHostResults1").getAsString());
    }

    public String commonAPIJson() {
        return (jsonData.getAsJsonObject().get("commonAPIJson")).getAsString();
    }

    public String createORG() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("createorg").toString()
                .replace("INJECT_ORGNAME", "API-" + RandomStringUtils.randomAlphanumeric(4)));
    }

    public String deleteORG() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteorg").toString());
    }

    public String addACC() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addAccount").toString()
                .replace("INJECT_ACCNAME", "API-" + RandomStringUtils.randomAlphanumeric(4)));
    }

    public String removeACC() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("removeAccount").toString());
    }
    public String delDEVICE() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteDevice").toString());
    }
    public String delGROUP() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteGroup").toString());
    }

    //Add group in commonapi.json once needs to be checked for description.
    public String addGROUP() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addGroup").toString());
    }
    public String addDEVICE() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addDevice").toString());
    }
    public String updevALIAS() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("UpdateDeviceAlias").toString());
    }

    public String deldevALIAS() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("RemoveDeviceAlias").toString());
    }
    public String assigndevtoGROUP() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("AssignDevicestoGroup").toString());
    }
    public String unassigndevfromGROUP() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("UnAssignDevicesfromGroup").toString());
    }
    public String deleteConfig() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteconfig").toString());
    }

    public String addconfig() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addConfig").toString()
                .replace("INJECT_CONFIGNAME", "CONFIG-" + RandomStringUtils.randomAlphanumeric(4)
                        .replace("INJECT_CONFIGDESC", "DESC-" + RandomStringUtils.randomAlphanumeric(4))));
    }
    public String addBenchmark() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("AddBenchmark").toString());
    }

    public String deleteBenchmark() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteBenchmark").toString());
    }

    public String provisionBenchmark() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("ProvisionBenchmark").toString());
    }

    public String deleteProvisionBenchmark() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteprovisionbenchmark").toString());
    }
    public String statusDeleteBenchmarkProvision() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("statusDeleteBenchmarkProvision")).toString();
    }

    public String statusDeleteBenchmark() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("statusDeleteBenchmark")).toString();
    }
    public String getACC() throws FileNotFoundException{
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("getaccount")).toString();
    }
    public String getAllConfiguration() throws FileNotFoundException{
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("getAllConfiguration")).toString();
    }
    public String postConfiguration() throws FileNotFoundException{
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("postConfiguration")).toString();
    }
    public String getStatus() throws FileNotFoundException{
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("getStatus")).toString();
    }
    public String deleteAD() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteAD").toString());
    }

    public String addADCONFIG() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addADConfig").toString());
    }
    public String adTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("activeDirectoryTestCases")).getAsString();

    }
    public String userManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("userManagementTestCases")).getAsString();
    }
    public String reportManagementTestCasesExcelPath() {
        return (jsonData.getAsJsonObject().get("reportManagementTestCases")).getAsString();
    }
    public String multiFactorAuthenticatorTestCasesExcelPath(){
        return (jsonData.getAsJsonObject().get("multiFactorAuthenticationTestCases")).getAsString();
    }



    public String addUser() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addUser").toString()
                .replace("INJECT_USERID", "API." + RandomStringUtils.randomAlphanumeric(4) + "@123.com"));
    }

    public String assignOrgToUser() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("assignOrgToUser").toString());
    }

    public String deleteUser() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("deleteUser").toString());
    }

    public String getAllOrg() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("getAllOrg").toString());
    }
    public String withdrawMultiFactor() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("withdrawMultiFactor").toString());
    }

    public String enforceMultiFactor() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("enforceMultiFactor").toString());
    }


    public String addMFAPolicy() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("addMFAPolicy").toString());
    }

    public String removeMFAPolicy() throws FileNotFoundException {
        JsonElement jsonData = JsonParser.parseReader(new FileReader(commonAPIJson()));
        return (jsonData.getAsJsonObject().get("removeMFAPolicy").toString());
    }


    //ankit changes for mfa

    public String mfa_policyname() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_policyname").toString());
    }


    public String mfa_policy_description() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_policy_description").toString());
    }


    public String mfa_environment_id() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_environment_id").toString());
    }


    public String mfa_client_id() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_client_id").toString());
    }


    public String mfa_authentication_path() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_authentication_path").toString());
    }

    public String mfa_username_option() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("mfa_policy_details").get("mfa_username_option").toString());
    }

    public String policy_name() {
        return (jsonData.getAsJsonObject()
                .getAsJsonObject("status_message").get("policy_name").toString());
    }

}
