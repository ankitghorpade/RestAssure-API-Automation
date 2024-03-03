package com.secpod.utils;

import com.secpod.config.Config;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class ParsingExcel {

    Config config = new Config();
    public ParsingExcel() throws FileNotFoundException {
    }

    public Map<String, String> regressionExcelFile(int rowValue, String sheetName, String excelFilePath) {
//    public void regressionExcelFile(){

        Map<String, String> hashMap = new HashMap<String, String>();
        try {
            File file = new File(excelFilePath);//creating a new file instance
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
//            ArrayList<Object> sheetName = new ArrayList<Object>();
            // for each sheet in the workbook
//            sheetName.add(workbook.getSheetName(i));
            XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));//creating a Sheet object to retrieve object
//            Iterator<Row> itr = sheet.iterator();//iterating over excel file
            Row row = sheet.getRow(0);
            int rowIndex = sheet.getLastRowNum();
//            System.out.println("rowIndex:::::::::" + rowIndex);
            int totalNoOfCols = row.getLastCellNum() -1;
//            System.out.println("totalNoOfCols:::::::::" + totalNoOfCols);

            List <String> myList = new ArrayList<>();
            for (int k = 0; k <= totalNoOfCols; k++){
//                row = sheet.getRow(0);
                Cell c = (row.getCell(k));
                String firstRowValue = c.toString();
                myList.add(firstRowValue);
            }
//            System.out.println(myList);
//            for (int j = 1; j <= rowIndex; j++) {

            for (int columnIndex = 0; columnIndex <= totalNoOfCols; columnIndex++) {
                row = sheet.getRow(rowValue);
                Cell c = (row.getCell(columnIndex));
                String cellValue = c.toString();
                if (Objects.equals(cellValue, "")) {
                    return hashMap;
                }
                    String keyName = myList.get(columnIndex);
                    String valueName = cellValue.trim();
//
//                    // Add to map
                    hashMap.put(keyName, valueName);
                    workbook.close();

            }
//            System.out.println(hashMap);

            return hashMap;
        } catch (Exception e) {
//            System.out.println("test::::" + e.getMessage());
//            e.printStackTrace();
        }
        return hashMap;
    }

    public Integer getRowValueCount(String sheetName, String excelFilePath) {
        int totalNumberOfRows = 0;
        try {
            File file = new File(excelFilePath);//creating a new file instance
            FileInputStream fis = new FileInputStream(file);//Creating instance  of the Input stream using file instance
            XSSFWorkbook workbook = new XSSFWorkbook(fis); //Crating the instance of the workbook using the instance of the file input stream
//            ArrayList<Object> sheetName = new ArrayList<Object>();  //Crating the instance of the Array list of the objects

            // for each sheet in the workbook
//            sheetName.add(workbook.getSheetName(i));
            XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));//getting the 0th index position sheet form the workbook
            totalNumberOfRows = sheet.getPhysicalNumberOfRows() -1;
        } catch (Exception e) {
//            System.out.println("test::::" + e.getMessage());
//            e.printStackTrace();
        }
        System.out.println("totalNumberOfRows::" + totalNumberOfRows);
        return totalNumberOfRows;
    }
}
