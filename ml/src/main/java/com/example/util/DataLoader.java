package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.model.UserRecord;

public class DataLoader {
    //veri exl yükleme

    public List<UserRecord> loadDataFromExcel(String filePath) {
        List<UserRecord> recordList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || row.getCell(1) == null)
                    continue;

                try {
                    // raw
                    String clientCode = getCellValueAsString(row.getCell(1));
                    String gender = getCellValueAsString(row.getCell(17));

                    String brand = getCellValueAsString(row.getCell(11));
                    String category = getCellValueAsString(row.getCell(12));
                    if (category == null || category.isEmpty())
                        continue; // kategorisi boş verileri modelden hariç tut

                    int amount = getIntValue(row.getCell(5));

                    double price = getNumericValue(row.getCell(6)); 
                    double lineNetTotal = getNumericValue(row.getCell(7));

                    UserRecord record = new UserRecord(
                            clientCode,
                            gender,
                            lineNetTotal,
                            brand,
                            category,
                            price,
                            amount // e
                    );

                    recordList.add(record);

                } catch (Exception e) {
                    System.err.println("Satır " + i + " atla: " + e.getMessage());
                }
            }

            System.out.println("yüklenen kayıt: " + recordList.size());

        } catch (IOException e) {
            System.err.println("hatas " + e.getMessage());
        }

        return recordList;
    }

    private int getIntValue(Cell cell) {
        if (cell == null)
            return 0;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            return 0;
        }

        return 0;
    }
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());

            default:
                return "";
        }
    }

    private double getNumericValue(Cell cell) {
        if (cell == null)
            return 0;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                return Double.parseDouble(cell.getStringCellValue().replace(",", "."));
            }
        } catch (Exception e) {
            return 0;
        }
    }
}