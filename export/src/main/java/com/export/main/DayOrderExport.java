package com.export.main;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.export.util.DBHelp;
import com.export.util.LargeExcelUtil;

/**
 * 日报订单导出
 * @author pangpeijie
 *
 */
public class DayOrderExport {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        try {
        	String beginStr = "2015-08-01";
            String endStr = "2015-08-31";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long begin = sdf.parse(beginStr).getTime();
            long end = sdf.parse(endStr).getTime();
            System.out.println("开始导出");
            while (begin <= end) {
            	exprotFunction(sdf.parse(sdf.format(begin)));
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
	
	/**
	 * 导出文件每天的数据
	 * @param date
	 * @throws Exception
	 */
	private static void exprotFunction(Date date) throws Exception{
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", torder.pay_price / 100 AS \"实付金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"支付方式\", torder.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"END AS \"订单状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\"FROM t_order torder LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.payment_time,\"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(torder.payment_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.parent_order_no IS NOT NULL AND torder.order_state IN (4,5, 6, 0,9) AND torder.company_id IN(31,22,43,25,42,9492,10,66) AND torder.order_type != 4 UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", -refundLine.refund_amount / 100 AS \"退款金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine.status = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.company_id IN(31,22,43,25,42,9492,10,66)AND torder.order_type != 4 UNION ALL SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", torder.pay_price / 100 AS \"实付金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"支付方式\", torder.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"END AS \"订单状态\", \"\" AS \"门店名称\", \"\" AS \"仓库名称\", \"妈妈好分公司\" AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\"FROM t_order torder LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.payment_time,\"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(torder.payment_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.order_state IN (4,5, 6, 0,9) AND torder.parent_order_no IS NOT NULL AND (torder.company_id = 63 OR torder.order_type = 4) UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", -refundLine.refund_amount / 100 AS \"退款金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", \"\" AS \"门店名称\", \"\" AS \"仓库名称\", \"妈妈好分公司\" AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine.status = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND (torder.company_id = 63 OR torder.order_type = 4) UNION ALL SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", torder.pay_price / 100 AS \"实付金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"支付方式\", torder.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"END AS \"订单状态\", \"\" AS \"门店名称\", \"总仓\" AS \"仓库名称\", \"\"  AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\"FROM t_order torder LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.payment_time,\"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(torder.payment_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.order_state IN (4,5, 6, 0,9) AND torder.parent_order_no IS NOT NULL AND torder.warehouse_type = 2 AND torder.order_type != 4 UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", -refundLine.refund_amount / 100 AS \"退款金额\", CASE torder.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", \"\" AS \"门店名称\", \"总仓\" AS \"仓库名称\", \"\" AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine.status = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") >= DATE_FORMAT(?, \"%Y-%m-%d\") AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") <= DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.warehouse_type = 2 AND torder.order_type != 4";
        DBHelp db = new DBHelp();
        ResultSet rs = null;
        int listSize = 10000;
        String headers[] = {"订单号","交易号","正常订单","实付金额","支付方式","支付账户","订单状态","门店名称","仓库名称","分公司名称","下单时间","支付时间","退款时间"};
        String columnName[] = {"订单号","交易号","正常订单","实付金额","支付方式","支付账户","订单状态","门店名称","仓库名称","分公司名称","下单时间","支付时间","退款时间"};
		String[] params = new String[12];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	for(int i=0;i<params.length;i++){
    		params[i] = sdf.format(date);
    	}
    	
    	rs = db.search(sql, params);
		List<Map<String, Object>> temps = new ArrayList<Map<String,Object>>(listSize);
		String path = "C:/order/"+sdf.format(date)+".xls";
    	int count = 0;//导出次数
    	while(rs.next()){
        	Map<String, Object> temp = new HashMap<String, Object>();
        	temp.put("订单号", rs.getString("订单号"));
        	temp.put("交易号", rs.getString("交易号"));
        	temp.put("正常订单", rs.getString("正常订单"));
        	temp.put("实付金额", rs.getString("实付金额"));
        	temp.put("支付方式", rs.getString("支付方式"));
        	temp.put("支付账户", rs.getString("支付账户"));
        	temp.put("订单状态", rs.getString("订单状态"));
        	temp.put("门店名称", rs.getString("门店名称"));
        	temp.put("仓库名称", rs.getString("仓库名称"));
        	temp.put("分公司名称", rs.getString("分公司名称"));
        	temp.put("下单时间", rs.getString("下单时间"));
        	temp.put("支付时间", rs.getString("支付时间"));
        	temp.put("退款时间", rs.getString("退款时间"));
        	temps.add(temp);
        	if(temps.size() == listSize){
        		count++;
        		//导出订单数据，并清空list
        		LargeExcelUtil.writeList(path, headers, columnName, temps, count);
        		temps.clear();
        	}
        }
    	if(temps.size()>0){
    		count++;
    		LargeExcelUtil.writeList(path, headers, columnName, temps, count);
    		temps.clear();
    		System.gc();
    	}
    	System.out.println(sdf.format(date)+"：导出成功，共需要"+count+"次生成文件");
		System.gc();
	}
	
}
