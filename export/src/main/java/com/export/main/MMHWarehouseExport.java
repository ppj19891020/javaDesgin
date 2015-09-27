package com.export.main;
import java.io.FileOutputStream;  
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;  
import java.util.ArrayList;  
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;  
  




import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;  
import org.apache.poi.hssf.usermodel.HSSFCellStyle;  
import org.apache.poi.hssf.usermodel.HSSFRow;  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  

import com.export.util.DBHelp;

/**
 * 妈妈好仓库报表数据导出
 * @author pangpeijie
 */
public class MMHWarehouseExport {

	public static void main(String[] args) {
		try {
        	String beginStr = "2015-08-01";
            String endStr = "2015-08-31";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long begin = sdf.parse(beginStr).getTime();
            long end = sdf.parse(endStr).getTime();
            System.out.println("开始导出");
            // 第一步，创建一个webbook，对应一个Excel文件  
            HSSFWorkbook wb = new HSSFWorkbook();  
            while (begin <= end) {
            	export(wb,sdf.parse(sdf.format(begin)));
                Date day = new Date();
                day.setTime(begin);
                begin += 3600 * 24 * 1000;
            }
            System.out.println("导出完成");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
	
	private static void export(HSSFWorkbook wb,Date date) throws SQLException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", orderLine.good_item_name AS \"商品名\", orderLine.allocated_count AS \"数量\", orderLine.pay_unit_price/ 100 AS \"单价\", torder.warehouse_name AS \"总仓名称\", CASE orderLine.type WHEN 2 THEN \"退款完成\"ELSE \"正常状态\"END AS \"状态\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\"FROM t_order_line AS orderLine LEFT JOIN t_order AS torder ON orderLine.order_no = torder.order_no WHERE torder.order_state IN (4, 5, 6, 0, 9) AND DATE_FORMAT(torder.payment_time,\"%Y-%m-%d\") = ? AND (torder.company_id = 63 OR torder.order_type = 4)";
		DBHelp db = new DBHelp();
        ResultSet rs = null;
        String[] params = new String[1];
        params[0] = sdf.format(date);
        rs = db.search(sql, params);
        String headers[] = {"订单号","交易号","商品名","数量","单价","总仓名称","状态","下单时间","支付时间"};
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet(sdf.format(date));  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
  
        HSSFCell cell = row.createCell((short) 0);
        for(int i=0;i<headers.length;i++){
        	cell.setCellValue(headers[i]);
        	cell.setCellStyle(style); 
        	cell = row.createCell((short) i+1);
        }
        
        List<Map<String, Object>> temps = new ArrayList<Map<String,Object>>();
        
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到
        while(rs.next()){
        	Map<String, Object> temp = new HashMap<String, Object>();
        	temp.put("1", rs.getString("订单号"));
        	temp.put("2", rs.getString("交易号"));
        	temp.put("3", rs.getString("商品名"));
        	temp.put("4", rs.getString("数量"));
        	temp.put("5", rs.getString("单价"));
        	temp.put("6", rs.getString("总仓名称"));
        	temp.put("7", rs.getString("状态"));
        	temp.put("8", rs.getString("下单时间"));
        	temp.put("9", rs.getString("支付时间"));
        	temps.add(temp);
        }
        
        for(int i=0;i<temps.size();i++){
        	row = sheet.createRow(sheet.getLastRowNum() + 1);
        	Map<String, Object> temp = temps.get(i);
        	row.createCell(0).setCellValue(temp.get("1")==null?"":temp.get("1").toString());
        	row.createCell(1).setCellValue(temp.get("2")==null?"":temp.get("2").toString());
        	row.createCell(2).setCellValue(temp.get("3")==null?"":temp.get("3").toString());
        	row.createCell(3).setCellValue(temp.get("4")==null?"":temp.get("4").toString());
        	row.createCell(4).setCellValue(temp.get("5")==null?"":temp.get("5").toString());
        	row.createCell(5).setCellValue(temp.get("6")==null?"":temp.get("6").toString());
        	row.createCell(6).setCellValue(temp.get("7")==null?"":temp.get("7").toString());
        	row.createCell(7).setCellValue(temp.get("8")==null?"":temp.get("8").toString());
        	row.createCell(8).setCellValue(temp.get("9")==null?"":temp.get("9").toString());
        }
        System.out.println(sdf.format(date)+"导出完成");
        // 第六步，将文件存到指定位置  
        try  
        {  
            FileOutputStream fout = new FileOutputStream("D:/妈妈好数据.xls");  
            wb.write(fout);  
            fout.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();
        }  
		
	}
	
}
