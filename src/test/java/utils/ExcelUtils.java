package utils;

import org.apache.poi.xssf.usermodel.*;

import java.io.*;

public class ExcelUtils {
    public FileOutputStream fs;
    public XSSFWorkbook    wb;
    public XSSFSheet       ws;
    public XSSFRow         row;
    public XSSFCell        cell;

    /** Opens existing file + sheet, or creates them if absent. */
    public void openExcel(String path, String sheetName) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            wb = new XSSFWorkbook(fis);
            fis.close();
            ws = wb.getSheet(sheetName);
            if (ws == null) ws = wb.createSheet(sheetName);
        } else {
            wb = new XSSFWorkbook();
            ws = wb.createSheet(sheetName);
        }
    }

    public void setData(int rowNum, int cellNum, String data) {
        row = ws.getRow(rowNum);
        if (row == null) row = ws.createRow(rowNum);
        cell = row.createCell(cellNum);
        cell.setCellValue(data);
    }

    public void saveData(String path) throws IOException {
        fs = new FileOutputStream(path);
        wb.write(fs);
        fs.close();
        wb.close();
    }
}