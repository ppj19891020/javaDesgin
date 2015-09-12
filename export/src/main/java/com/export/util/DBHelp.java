package com.export.util;
import java.sql.*;

public class DBHelp {

	Connection conn = null;
    ResultSet rs = null;
    //连接数据库
    public void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://rdss48ik5jz4qqhhfm6s9public.mysql.rds.aliyuncs.com:3306/db_gd?useUnicode=true&characterEncoding=UTF-8",
            		"db_mamahao_root","db_mamahao_pwd_123987");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    //查询
    public ResultSet search(String sql, String str[]){
        connect();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            if(str != null){
                for(int i=0;i<str.length;i++){
                    pst.setString(i+1, str[i]);
                }
            }
            rs = pst.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;
    }
    
    //增删修改
    public int addU(String sql, String str[]){
        int a =0;
        /*connect();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            if(str != null){
                for(int i=0;i<str.length-1;i++){
                    pst.setString(i+1, str[i]);
                }
                pst.setInt(str.length, Integer.parseInt(str[str.length-1]));
            }
            a = pst.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        return a;
    }
	
}
