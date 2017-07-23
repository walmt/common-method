package com.gzpsc.util;

import org.apache.poi.ss.usermodel.Cell;

import java.text.SimpleDateFormat;

/**
 * Created by 云翔 on 2017/5/8.
 */
public class ExcelUtil {
    /**
     * 把单元格内容转成String
     *
     * @param cellType
     * @param cell
     * @return
     */
    public static String transformExcelCellToString(int cellType, Cell cell) {
        return transformExcelCellToString(cellType, cell, "yyyy/MM/dd");
    }

    public static String transformExcelCellToString(int cellType, Cell cell, String DateFormatt) {
        String cellValue = null;
        switch (cellType) {
            case Cell.CELL_TYPE_STRING: // 文本
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC: // 数字、日期
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat fmt = new SimpleDateFormat(DateFormatt);
                    cellValue = fmt.format(cell.getDateCellValue()); // 日期型
                } else {
                    cellValue = String.valueOf(cell.getNumericCellValue()); // 数字
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN: // 布尔型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_BLANK: // 空白
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_ERROR: // 错误
                cellValue = "错误";
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                try {
                    cellValue = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                            SimpleDateFormat fmt = new SimpleDateFormat(DateFormatt);
                            cellValue = fmt.format(cell.getDateCellValue()); // 日期型
                        } else {
                            cellValue = String.valueOf(cell.getNumericCellValue()); // 数字
                        }
                    } catch (IllegalStateException ex) {
                        cellValue = "-1" ;
                    }
                }
                break;
            default:
                cellValue = "错误";
        }
        return cellValue;
    }
}

