package Library.Utils;

import javax.swing.*;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class JDBCUtil extends JFrame {
    private static  String url;
    private static  String username;
    private static  String password;

    static {
        try {
            Properties prop = new Properties();
            prop.load(new FileReader("src\\main\\resources\\driver.properties"));
            url = prop.getProperty("url");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "数据库连接失败: " + e.getMessage());
            System.exit(0);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }
//    public static ResultSet select(Connection conn, PreparedStatement ps, String sql,List<String> objs) {
//        try {
//            ps = conn.prepareStatement(sql);
//            if (objs != null) {
//                int i = 1;
//                for (Object obj : objs) {
//                    ps.setObject(i++, obj);
//                }
//            }
//            return ps.executeQuery();
//
//        } catch (Exception e) {
//            throw new RuntimeException("执行查询语句时出错，错误语句："+sql);
//        }
//    }
    public static void startTransaction(Connection conn) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void commitTransaction(Connection conn) {

        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
