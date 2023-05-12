package com.example.history.bean;

import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.codec.digest.DigestUtils;

public class JdbcTools {
    public static Connection getConnection() {
        Connection con = null;
        Log.d("dd","get");
        try {

            // 加载数据库驱动
//            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
            Log.d("getConnection","getConnection1");
            // 获取数据库连接
            con = DriverManager.getConnection("jdbc:mysql://139.155.248.158:3306/db_app?userUnicode=true&characterEncoding=utf-8", "root", "A1B2C3D4E5678");
//            con = DriverManager.getConnection("jdbc:mysql:///db2?userUnicode=true&characterEncoding=utf-8", "root", "1111");
            Log.d("getConnection","getConnection");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    /*
     * 查询
     */
    public static <T> List<T> query(String sql, Class<T> clazz,Object...obj) throws SQLException{
        List<T> list = new ArrayList<T>();
        // 获取数据库的链接
        Connection con = getConnection();
        // 获取sql预处理对象
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 替换sql中的？占位符
            pstmt = con.prepareStatement(sql);
            if(obj != null) {
                for (int i = 0; i < obj.length; i++) {
                    pstmt.setObject(i + 1, obj[i]);
                }
            }
            // 执行sql语句
            rs = pstmt.executeQuery();

            String columnName;
            Object columnValue;

            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()) {
                T entity = clazz.newInstance();

                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    columnName = metaData.getColumnName(i + 1);
                    columnValue = rs.getObject(columnName);

                    Field f = clazz.getDeclaredField(columnName);
                    f.setAccessible(true);
                    f.set(entity, columnValue);
                }
                list.add(entity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) con.close();
        }
        return list;
    }
    /*
     * 更新
     */
    public static int update(String sql, Object...obj) throws SQLException {
        int result = 0;
        Connection con = getConnection();
        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);

            if(obj != null) {
                for (int i = 0; i < obj.length; i++) {
                    pstmt.setObject(i + 1, obj[i]);
                }
            }
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(pstmt != null) pstmt.close();
            if(con != null) con.close();
        }


        return result;
    }

}
