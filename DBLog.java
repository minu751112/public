package dbProxy;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBLog {

    // 전문번호, 접수시간, json입력
    public static HashMap<String, String> LogJobb1(HashMap<String, String> mapParam) throws Exception, SQLException {
        String sql = "";
        HashMap<String, String> mapOut = new HashMap<String, String>();
... 중략
        try {
... 생략
			
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;

            try {
                conn = DBConnectingPool.getConnection();
                st = conn.createStatement();

    			sql = String.format("insert into JobLog(\r\n"
    					+ "		seq,			--시퀀스\r\n"
    					+ "		status,			--최종상태\r\n"
    					+ "		msg				--메시지\r\n"
    					+ "		)\r\n"
    					+ "	values\r\n"
    					+ "	   ( \r\n"
    					+ "		nextval('seq_resreqlog'),--시퀀스\r\n"
    					+ "		'%s',	--최종상태\r\n"
    					+ "		'%s'	--메시지\r\n"
    					+ "	)"
    					,status
    					,msg);
    			st.execute(sql);
    			
    			
    			sql = "select currval('seq_resreqlog')";
    			rs = st.executeQuery(sql);
                if (rs.next()) {
                    String f_seq = rs.getString(1);
                    mapOut.put("seq", f_seq);
                }
            }catch(SQLException se) {
            	System.out.println("☆★ LogJobb1 SQLException :"+se.toString());
            }finally {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                	DBConnectingPool.bringBackConnection(conn);
                }
                
            }
        } catch (Exception e) {
            System.out.println("☆★ LogJobb1 Exception :"+e.toString());
            System.out.println(sql);
        }
        return mapOut;
    }
}
