package com.export.main;

import java.io.File;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.export.util.CSVUtils;
import com.export.util.DBHelp;

/**
 * 财务订单导出（新版）
 * @author pangpeijie
 *
 */
public class NewDayOrder {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		String beginStr = "2015-11-03";
        String endStr = "2015-11-03";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long begin = sdf.parse(beginStr).getTime();
        long end = sdf.parse(endStr).getTime();
        System.out.println("----------开始导出----------");
        while (begin <= end) {
        	warehouseExport(sdf.format(begin));
        	companyExport(sdf.format(begin));
        	exprotDayOrderFunction(sdf.format(begin));
            Date day = new Date();
            day.setTime(begin);
            begin += 3600 * 24 * 1000;
        }
        System.out.println("----------导出完成----------");
	}
	
	/**
	 * 仓库导出
	 * @param dateStr 日期  格式:2015-10-10
	 */
	private static void warehouseExport(String dateStr){
		HashMap<Integer, String> warehouses = new HashMap<Integer, String>();
		warehouses.put(53131, "妈妈好");
		warehouses.put(52469, "上海EI正品仓");
		warehouses.put(52471, "北京EI正品仓");
		warehouses.put(52353, "总部O2O童车仓");
		warehouses.put(52467, "总部O2O用品仓");
		warehouses.put(52468, "运动用品华东(昆山)O2O仓库（千灯）");
		warehouses.put(52553, "运动用品华北(廊坊)O2O仓库");
		warehouses.put(52572, "昆山服饰（GB Family）O2O仓库（千灯）");
		warehouses.put(52391, "昆山服饰O2O仓库（千灯）");
		warehouses.put(52571, "南通合资公司O2O仓库（千灯）");
		
		Iterator iter = warehouses.entrySet().iterator();
		while (iter.hasNext()){
    		Entry e = (Entry)iter.next();
    		int warehouseId = (Integer)e.getKey();
    		String warehouseName = (String)e.getValue();
    		System.out.println("正在导出:"+warehouseId + "→" + warehouseName);
    		exprotWarehouseFunction(warehouseId, warehouseName, dateStr);
    		System.out.println("导出完成:"+warehouseId + "→" + warehouseName);
    	}
	}
	
	/**
	 * 仓库数据导出
	 * @param warehouseId 仓库id
	 * @param warehouseName 仓库名称
	 * @param dateStr 日期  格式:2015-10-10
	 */
	private static void exprotWarehouseFunction(Integer warehouseId,String warehouseName,String dateStr){
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", orderPay.pay_price / 100 AS \"实付金额\", orderPay.pay_mbean_price / 100 AS \"妈豆优惠\", orderPay.pay_coupon_price / 100 AS \"优惠券抵扣优惠\", orderPay.pay_freight_price / 100 AS \"运费\", orderPay.pay_shop_price / 100 AS \"门店金额\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"支付方式\", orderPay.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 4 THEN \"代发货\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"WHEN 9 THEN \"标记退款退货\"END AS \"订单状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE torder.relation_type WHEN 1 THEN \"一元购\"WHEN 3 THEN \"特卖会\"END AS \"标记\"FROM t_order torder LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.created_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.parent_order_no IS NOT NULL AND torder.order_state IN (4, 5, 6, 0, 9) AND torder.relation_type IS NULL AND torder.warehouse_id = ? UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", - refundLine.refund_amount / 100 AS \"退款金额\", - orderLine.pay_mbean_price / 100 AS \"妈豆优惠\", - orderLine.pay_coupon_act_price / 100 AS \"优惠券抵扣优惠\", 0 AS \"运费\", - (refundLine.refund_amount + orderLine.pay_mbean_price + orderLine.pay_coupon_act_price ) / 100 AS \"门店退款\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE refundLine.refund_type WHEN 1 THEN \"退货\"WHEN 2 THEN \"退款\"END AS \"标记\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine. STATUS = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.relation_type IS NULL AND torder.warehouse_id = ?";
		DBHelp db = new DBHelp();
        ResultSet rs = null;
		String[] params = new String[4];
		params[0] = dateStr;
		params[1] = warehouseId.toString();
		params[2] = dateStr;
		params[3] = warehouseId.toString();
    	rs = db.search(sql, params);
    	String fileName = "C:/order/"+dateStr+"_"+warehouseName+".csv";
    	File file = new File(fileName);
    	CSVUtils.exportResultSetCsv(file, rs);
	}

	/**
	 * 分公司订单导出
	 * @param dateStr 日期  格式:2015-10-10
	 */
	private static void companyExport(String dateStr){
		HashMap<Integer, String> warehouses = new HashMap<Integer, String>();
		warehouses.put(10, "mothercare");
		warehouses.put(22, "成都分公司");
		warehouses.put(9492, "阜阳分公司");
		warehouses.put(31, "杭州分公司");
		warehouses.put(43, "山西分公司");
		warehouses.put(42, "上海分公司");
		warehouses.put(25, "深圳分公司");
		warehouses.put(66, "好孩子E家");
		
		Iterator iter = warehouses.entrySet().iterator();
		while (iter.hasNext()){
    		Entry e = (Entry)iter.next();
    		int companyId = (Integer)e.getKey();
    		String companyName = (String)e.getValue();
    		System.out.println("正在导出:"+companyId + "→" + companyName);
    		exprotCompanyFunction(companyId, companyName, dateStr);
    		System.out.println("导出完成:"+companyId + "→" + companyName);
    	}
	}
	
	/**
	 * 分公司数据导出
	 * @param companyId 
	 * @param companyName
	 * @param dateStr 日期  格式:2015-10-10
	 */
	private static void exprotCompanyFunction(Integer companyId,String companyName,String dateStr){
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", orderPay.pay_price / 100 AS \"实付金额\", orderPay.pay_mbean_price / 100 AS \"妈豆优惠\", orderPay.pay_coupon_price / 100 AS \"优惠券抵扣优惠\", orderPay.pay_freight_price / 100 AS \"运费\", orderPay.pay_shop_price / 100 AS \"门店金额\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"支付方式\", orderPay.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 4 THEN \"代发货\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"WHEN 9 THEN \"标记退款退货\"END AS \"订单状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE torder.relation_type WHEN 1 THEN \"一元购\"WHEN 3 THEN \"特卖会\"END AS \"标记\"FROM t_order torder LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.created_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.parent_order_no IS NOT NULL AND torder.order_state IN (4, 5, 6, 0, 9) AND torder.relation_type IS NULL AND torder.company_id = ? UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", - refundLine.refund_amount / 100 AS \"退款金额\", - orderLine.pay_mbean_price / 100 AS \"妈豆优惠\", - orderLine.pay_coupon_act_price / 100 AS \"优惠券抵扣优惠\", 0 AS \"运费\", - (refundLine.refund_amount + orderLine.pay_mbean_price + orderLine.pay_coupon_act_price ) / 100 AS \"门店退款\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE refundLine.refund_type WHEN 1 THEN \"退货\"WHEN 2 THEN \"退款\"END AS \"标记\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine. STATUS = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.relation_type IS NULL AND torder.company_id = ?";
		DBHelp db = new DBHelp();
        ResultSet rs = null;
		String[] params = new String[4];
		params[0] = dateStr;
		params[1] = companyId.toString();
		params[2] = dateStr;
		params[3] = companyId.toString();
    	rs = db.search(sql, params);
    	String fileName = "C:/order/"+dateStr+"_"+companyName+".csv";
    	File file = new File(fileName);
    	CSVUtils.exportResultSetCsv(file, rs);
	}
	
	/**
	 * 每天订单数据导出
	 * @param companyId 
	 * @param companyName
	 * @param dateStr 日期  格式:2015-10-10
	 */
	private static void exprotDayOrderFunction(String dateStr){
		String sql = "SELECT torder.order_no AS \"订单号\", torder.serial_number AS \"交易号\", \"正常订单\" AS \"正常订单\", orderPay.pay_price / 100 AS \"实付金额\", orderPay.pay_mbean_price / 100 AS \"妈豆优惠\", orderPay.pay_coupon_price / 100 AS \"优惠券抵扣优惠\", orderPay.pay_freight_price / 100 AS \"运费\", orderPay.pay_shop_price / 100 AS \"门店金额\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"支付方式\", orderPay.pay_account AS \"支付账户\", CASE torder.order_state WHEN 0 THEN \"已完成\"WHEN 4 THEN \"代发货\"WHEN 5 THEN \"待收货 \"WHEN 6 THEN \"待评价\"WHEN 9 THEN \"标记退款退货\"END AS \"订单状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", \"\" AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE torder.relation_type WHEN 1 THEN \"一元购\"WHEN 3 THEN \"特卖会\"END AS \"标记\"FROM t_order torder LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND DATE_FORMAT(torder.created_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.parent_order_no IS NOT NULL AND torder.order_state IN (4, 5, 6, 0, 9) AND torder.relation_type IS NULL UNION ALL SELECT refundLine.refund_line_no AS \"退款退货单号\", refundLine.refund_serial_number AS \"交易号\", \"退款订单\" AS \"正常订单\", - refundLine.refund_amount / 100 AS \"退款金额\", - orderLine.pay_mbean_price / 100 AS \"妈豆优惠\", - orderLine.pay_coupon_act_price / 100 AS \"优惠券抵扣优惠\", 0 AS \"运费\", - (refundLine.refund_amount + orderLine.pay_mbean_price + orderLine.pay_coupon_act_price ) / 100 AS \"门店退款\", CASE orderPay.pay_type_id WHEN 0 THEN \"支付宝V1.0\"WHEN 1 THEN \"银联支付\"WHEN 2 THEN \"微信支付\"WHEN 3 THEN \"妈豆支付\"WHEN 4 THEN \"支付宝V1.1\"WHEN 5 THEN \"微信支付\"WHEN 6 THEN \"测试支付宝\"END AS \"退款方式\", refundLine.refund_account AS \"退款账户\", \"退款完成\" AS \"退款状态\", torder.shop_name \"门店名称\", torder.warehouse_name \"仓库名称\", gb_div.DIV_NAME AS \"分公司名称\", torder.created_time \"下单时间\", torder.payment_time \"支付时间\", refundLine.end_time AS \"退款时间\", torder.tml_num_id AS \"好孩子单号\", CASE refundLine.refund_type WHEN 1 THEN \"退货\"WHEN 2 THEN \"退款\"END AS \"标记\"FROM t_refund_line AS refundLine LEFT JOIN t_order_line AS orderLine ON refundLine.order_no = orderLine.order_no AND refundLine.order_line_id = orderLine.good_item_id LEFT JOIN t_order AS torder ON refundLine.order_no = torder.order_no LEFT JOIN t_order_pay orderPay ON torder.order_no = orderPay.order_no LEFT JOIN gb_div ON torder.company_id = gb_div.DIV_NUM_ID WHERE 1 = 1 AND refundLine. STATUS = 4 AND DATE_FORMAT(refundLine.end_time, \"%Y-%m-%d\") = DATE_FORMAT(?, \"%Y-%m-%d\") AND torder.relation_type IS NULL";
		DBHelp db = new DBHelp();
        ResultSet rs = null;
		String[] params = new String[2];
		params[0] = dateStr;
		params[1] = dateStr;
    	rs = db.search(sql, params);
    	String fileName = "C:/order/"+dateStr+"_汇总订单"+".csv";
    	File file = new File(fileName);
    	System.out.println("正在导出:"+dateStr+"_汇总订单");
    	CSVUtils.exportResultSetCsv(file, rs);
    	System.out.println("导出完成:"+dateStr+"_汇总订单");
	}
	
}
