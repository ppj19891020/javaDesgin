/**
 * 
 */
package com.export.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author bowen
 * @date 2015年8月29日 上午9:46:31 
 */
public class LargeExcelUtil {
	
	private static String transObj(Object obj, String pattern) {
		if (pattern == null || "".equals(pattern)) {
			pattern = "yyyy-MM-dd";
		}
		if (obj == null) {
			return "";
		}
		String textValue = "";
		if (obj instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			textValue = sdf.format(obj);
		} else if (obj instanceof Boolean) {
			if ((Boolean) obj == false) {
				textValue = "0";
			} else {
				textValue = "1";
			}
		} else {
			textValue = String.valueOf(obj);
		}
		return textValue;
	}
	
	public static void writeList(String path,String[] headers, String[] columnName,List<Map<String, Object>> collections,int count) {
		File file = new File(path);
		HSSFWorkbook book = null;
		HSSFSheet sheet = null;
		HSSFRow row = null;
		POIFSFileSystem fs = null;
		FileInputStream in = null;
		OutputStream out = null;
		String pattern = "yyyy-MM-dd HH:mm:ss";
		try {
			if(file.exists()){
				in = new FileInputStream(file);
				fs = new POIFSFileSystem(in);
				book = new HSSFWorkbook(fs);
				in.close();
			}
			out = new FileOutputStream(file);
			if(book == null){
				book = new HSSFWorkbook();
				sheet = book.createSheet();
			}else{
				sheet =book.getSheetAt(0);
			}
			
			if(count == 1){
				HSSFRow first =  sheet.createRow(0);
				for (int j = 0; j < headers.length; j++) {
					HSSFCell cell1 = first.createCell(j);
	                cell1.setCellValue(headers[j]);
				}
				for (int i = 0; i < collections.size(); i++) {
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					Map<String, Object> map = collections.get(i);
					for (int j = 0; j < columnName.length; j++) {
						Cell cell1 = row.createCell(j);
						Object object = map.get(columnName[j]);
						cell1.setCellValue(transObj(object, pattern));
					}
				}
			}else{
				for (int i = 0; i < collections.size(); i++) {
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					Map<String, Object> map = collections.get(i);
					for (int j = 0; j < columnName.length; j++) {
						Cell cell1 = row.createCell(j);
						Object object = map.get(columnName[j]);
						cell1.setCellValue(transObj(object, pattern));
					}
				}
			}
			book.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
