package com.export.main;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.export.util.DBHelp;
import com.export.util.LargeExcelUtil;

public class WarehouseExport2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        try {
        	String beginStr = "2015-09-01";
            String endStr = "2015-10-31";
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
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", orderPay.pay_price / 100 AS \"实付金额\", orderPay.pay_mbean_price / 100 AS \"妈豆优惠\", orderPay.pay_coupon_price / 100 AS \"优惠券抵扣优惠\", orderPay.pay_freight_price / 100 AS \"运费\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"支付方式\", orderPay.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"END AS \"订单状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE torder.relation_type WHEN 1 THEN \"一元购\"WHEN 3 THEN \"特卖会\"ELSE \"\"END AS \"标记\"FROM t_order torder LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.payment_time, \"%Y-%m-%d\") = ? AND torder.parent_order_no IS NOT NULL AND torder.order_state IN (4, 5, 6, 0, 9) UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", - refundLine.refund_amount / 100 AS \"退款金额\", - orderLine.pay_mbean_price / 100 AS \"妈豆优惠\", - orderLine.pay_coupon_act_price / 100 AS \"优惠券抵扣优惠\", 0 AS \"运费\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE torder.relation_type WHEN 1 THEN \"一元购\"WHEN 3 THEN \"特卖会\"ELSE \"\"END AS \"标记\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine. STATUS = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") = ?";
        DBHelp db = new DBHelp();
        ResultSet rs = null;
        String headers[] = {"订单号","交易号","正常订单","实付金额","妈豆优惠","优惠券抵扣优惠","运费","支付方式","支付账户","订单状态","门店名称","仓库名称","分公司名称","下单时间","支付时间","退款时间","好孩子单号","标记"};
        String columnName[] = {"订单号","交易号","正常订单","实付金额","妈豆优惠","优惠券抵扣优惠","运费","支付方式","支付账户","订单状态","门店名称","仓库名称","分公司名称","下单时间","支付时间","退款时间","好孩子单号","标记"};
		String[] params = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	for(int i=0;i<params.length;i++){
    		params[i] = sdf.format(date);
    	}
    	
    	rs = db.search(sql, params);
		List<Map<String, Object>> temps = new ArrayList<Map<String,Object>>();
		String path = "C:/order/"+sdf.format(date)+".xls";
    	while(rs.next()){
        	Map<String, Object> temp = new HashMap<String, Object>();
        	temp.put("订单号", rs.getString("订单号"));
        	temp.put("交易号", rs.getString("交易号"));
        	temp.put("正常订单", rs.getString("正常订单"));
        	temp.put("实付金额", rs.getString("实付金额"));
        	temp.put("妈豆优惠", rs.getString("妈豆优惠"));
        	temp.put("优惠券抵扣优惠", rs.getString("优惠券抵扣优惠"));
        	temp.put("运费", rs.getString("运费"));
        	temp.put("支付方式", rs.getString("支付方式"));
        	temp.put("支付账户", rs.getString("支付账户"));
        	temp.put("订单状态", rs.getString("订单状态"));
        	temp.put("门店名称", rs.getString("门店名称"));
        	temp.put("仓库名称", rs.getString("仓库名称"));
        	temp.put("分公司名称", rs.getString("分公司名称"));
        	temp.put("下单时间", rs.getString("下单时间"));
        	temp.put("支付时间", rs.getString("支付时间"));
        	temp.put("退款时间", rs.getString("退款时间"));
        	temp.put("好孩子单号", rs.getString("好孩子单号"));
        	temp.put("标记", rs.getString("标记"));
        	temps.add(temp);
        }
    	if(temps.size()>0){
    		LargeExcelUtil.writeList2(path, headers, columnName, temps);
    		temps.clear();
    		System.gc();
    	}
	}
	
}
