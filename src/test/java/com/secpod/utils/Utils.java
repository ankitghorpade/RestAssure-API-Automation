package com.secpod.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;

public class Utils {

    public List<DataProviderObject> getDataProviders(String keyValueContent, List<DataProviderObject> data) {
        JsonElement jsonData = JsonParser.parseString(keyValueContent);
        JsonElement jsonBody = jsonData.getAsJsonObject().get("body");
        String body, statuscodes, urlparam, description, expectedResult;

        if (jsonBody.isJsonPrimitive()) {
            body = jsonData.getAsJsonObject().get("body").getAsString();
        } else {
            body = jsonData.getAsJsonObject().get("body").toString();
        }

        statuscodes = String.valueOf(jsonData.getAsJsonObject().get("status").getAsString());

        jsonBody = jsonData.getAsJsonObject().get("urlparam");
        if (jsonBody.isJsonPrimitive()) {
            urlparam = jsonData.getAsJsonObject().get("urlparam").getAsString();
        } else {
            urlparam = jsonData.getAsJsonObject().get("urlparam").toString();
        }


//        urlparam = String.valueOf(jsonData.getAsJsonObject().get("urlparam").asString)
//        description = jsonData.getAsJsonObject().get("description_of_TCs").toString()
        description = String.valueOf(jsonData.getAsJsonObject().get("description_of_TC").getAsString());
        expectedResult = String.valueOf(jsonData.getAsJsonObject().get("expected_results").toString());
        data.add(new DataProviderObject(body, statuscodes, urlparam, description, expectedResult));

        return data;
    }

    public Object[][] convertTo2DArray(List<DataProviderObject> objects) {

        Object[][] arr = new Object[objects.size()][5];

        for (int i = 0; i < objects.size(); i++){
            arr[i][0] = objects.get(i).getbody();
            arr[i][1] = objects.get(i).getStatusCodes();
            arr[i][2] = objects.get(i).getUrlParam();
            arr[i][3] = objects.get(i).getDescription();
            arr[i][4] = objects.get(i).getexpectedResult();
        }

        return arr;
    }

    public static class DataProviderObject {

        private String body;
        private String statusCodes;
        private String urlParam;
        private String description;
        private String expectedResult;
        public DataProviderObject(String body, String statusCodes, String urlParam, String description, String expectedResult) {

            this.body = body;
            this.statusCodes = statusCodes;
            this.urlParam = urlParam;
            this.description = description;
            this.expectedResult = expectedResult;
        }

        public String getbody() {
            return body;
        }

        public void setbody(String body) {
            this.body = body;
        }

        public String getStatusCodes() {
            return statusCodes;
        }

        public void setStatusCodes(String statusCodes) {
            this.statusCodes = statusCodes;
        }

        public String getUrlParam() {
            return urlParam;
        }

        public void setUrlParam(String urlParam) {
            this.urlParam = urlParam;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getexpectedResult() {
            return expectedResult;
        }

        public void setexpectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

    }
}
