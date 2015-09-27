package com.export.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CSV导出
 * @author pangpeijie
 */
public class CSVUtils {
	
	/**
     * 导出
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList){
        boolean isSucess=false;
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file,true);
            osw = new OutputStreamWriter(out, "GB2312");
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        
        return isSucess;
    }
    
    /**
     * 通过数据库导出
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportResultSetCsv(File file, ResultSet resultSet){
    	long start = System.currentTimeMillis();
        boolean isSucess=false;
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file,true);
            osw = new OutputStreamWriter(out, "GB2312");
            bw =new BufferedWriter(osw);
            ResultSetMetaData rsmd=resultSet.getMetaData();
            String columnC = "";
            for(int i=1;i<=rsmd.getColumnCount();i++){
            	columnC += ","+rsmd.getColumnName(i).toString();
                if(i == rsmd.getColumnCount()){
                	columnC = columnC.substring(1,columnC.length());
                    bw.append(columnC).append("\r");
                    continue;
                }
            }
            
            HashMap<Integer, String> numType = new HashMap<Integer, String>();//数值类型，不做转换
            numType.put(4, "4");
            numType.put(5, "5");
            numType.put(6, "6");
            numType.put(7, "7");
            numType.put(8, "8");
            
            while (resultSet.next()){
            	String ls = "";
                for(int i=1;i<=rsmd.getColumnCount();i++){
                	if(null == resultSet.getObject(i)){
                		ls += ", ";
                	}else if(null != numType.get(i)){
                		//数值类型
                		ls += "," + resultSet.getString(i);
                	}else if(i == 2){
                		//流水号这列做特殊处理，加上单引号
                		ls += "," + "'" + resultSet.getString(i);
                	}else{
                		//字符串类型
                		ls += "," + "\t" + resultSet.getString(i);
                	}
                    if(i == rsmd.getColumnCount()){
                    	ls = ls.substring(1, ls.length());
                        bw.append(ls).append("\r");
                        continue;
                    }
                }
            }
            bw.flush();
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        System.out.println("耗时："+(System.currentTimeMillis()-start)+"ms");
        return isSucess;
    }
    
    /**
     * 导入
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(File file){
        List<String> dataList=new ArrayList<String>();
        BufferedReader br=null;
        try { 
            br = new BufferedReader(new FileReader(file));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                dataList.add(line);
            }
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }
    
    public static void main(String[] args){
    	List<String> dataList=new ArrayList<String>();
        dataList.add("1,张三,男");
        dataList.add("2,李四,男");
        dataList.add("3,小红,女");
        boolean isSuccess=CSVUtils.exportCsv(new File("D:/ljq.csv"), dataList);
        System.out.println(isSuccess);
    }
	
}
