package dbProxy;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DbTask {

    // 전문번호, 접수시간, json입력
    public static HashMap<String, String> LogJobb1(HashMap<String, String> mapParam) throws Exception, SQLException {
        String sql = "";
        HashMap<String, String> mapOut = new HashMap<String, String>();
... 중략
        try {
			if (msg == null) {
				msg	= msg.replace("'","''");	//메시지
			}
			
			if (requestbody == ""){
				requestbody = "null";
			}else{
				requestbody = "'" + requestbody + "'";
			}
			
			if (responseBody == ""){
				responseBody = "null";
			}else{
				responseBody = "'" + responseBody + "'";
			}
			
			if (total.compareTo("") == 0) {
				total = "null";
			}
			
			if (ord.compareTo("") == 0) {
				ord = "null";
			}
			
			if (requestbody.compareTo("") == 0) {
				requestbody = "null";
			}
			
			if (responseBody.compareTo("") == 0) {
				responseBody = "null";
			}
			
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;

            try {
                conn = DBConnectingPool.getConnection();
                st = conn.createStatement();

    			sql = String.format("insert into JobLog(\r\n"
    					+ "		seq,			--시퀀스\r\n"
    					+ "		rcvurl,			--접수url\r\n"
    					+ "		rspurl,			--응답url\r\n"
    					+ "		ifname,			--인터페이스명칭\r\n"
    					+ "		rfcname,		--rfc명칭\r\n"
    					+ "		transactionid,	--트랜잭션id\r\n"
    					+ "		total,			--총갯수\r\n"
    					+ "		ord,			--순서\r\n"
    					+ "		syncdiv,		--동기,비동기구분\r\n"
    				    + "		requestbody,	--송신데이터\r\n"
    				    + "		responseBody,	--수신데이터\r\n"
    					+ "		source,			--접속소스\r\n"
    					+ "		target,			--접속타겟\r\n"
    					+ "		createtime,		--등록일시\r\n"
    					+ "		endtime,		--수정일시\r\n"
    					+ "		status,			--최종상태\r\n"
    					+ "		msg				--메시지\r\n"
    					+ "		)\r\n"
    					+ "	values\r\n"
    					+ "	   ( \r\n"
    					+ "		nextval('seq_resreqlog'),--시퀀스\r\n"
    					+ "		'%s',	--접수url\r\n"
    					+ "		'%s',	--응답url\r\n"
    					+ "		'%s',	--인터페이스명칭\r\n"
    					+ "		'%s',	--rfc명칭\r\n"
    					+ "		'%s',	--트랜잭션id\r\n"
    					+ "		 %s,	--총갯수\r\n"
    					+ "		 %s,	--순서\r\n"
    					+ "		'%s',	--동기,비동기구분\r\n"
    					+ "	    %s,		--송신데이터\r\n"
    					+ "	 	%s,		--수신데이터\r\n"
    					+ "		'%s',	--접속소스\r\n"
    					+ "		'%s',	--접속타겟\r\n"
    					+ "		now(),	--등록일시\r\n"
    					+ "		now(),	--수정일시\r\n"
    					+ "		'%s',	--최종상태\r\n"
    					+ "		'%s'	--메시지\r\n"
    					+ "	)"
    					,rcvurl	 	
    					,rspurl	 	
    					,ifname 	
    					,rfcname	
    					,transactionid	
    					,total
    					,ord
    					,syncdiv	
    					,requestbody
    					,responseBody
    					,source
    					,target
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

    // 전문번호, 접수시간, json입력
    public static HashMap<String, String> LogJobb2(HashMap<String, String> mapParam) throws Exception, SQLException {
        String sql = "";
        HashMap<String, String> mapOut = new HashMap<String, String>();
        String seq = mapParam.get("seq");
        String total = mapParam.get("total");
        String ord = mapParam.get("ord");
        String responseBody = mapParam.get("responseBody");
        String status = mapParam.get("status");
        Integer nSeq = null;
        String msg = mapParam.get("msg").replace("'", "''");
        
        
        try {
            if (responseBody != null) {
                responseBody = responseBody.replace("'", "''");
            }

            if (seq != null) {
                nSeq = Integer.parseInt(seq);
            }

            if (total.compareTo("") == 0) {
                total = "null";
            }
            if (ord.compareTo("") == 0) {
                ord = "null";
            }

            Connection conn = null;
            Statement st = null;

            try {
                conn = DBConnectingPool.getConnection();
                st = conn.createStatement();
    			sql = String.format(""
    					+ "update JobLog set \r\n"					
    					+ "			 responseBody = '%s'	--수신데이터   \r\n"
    					+ "			,total = %s				--total갯수	\r\n"
    					+ "			,ord = %s				--ord	    \r\n"					
    					+ "			,status = '%s'			--최종상태	    \r\n"
    					+ "			,msg = '%s'			--메시지	    \r\n"
    					+ "         ,endtime = now() \r\n"
    					+ "			where seq = %s"
    					,responseBody
    					,total
    					,ord
    					,status
    					,msg
    					,seq);
    			st.execute(sql);
            }catch(SQLException se) {
            	System.out.println("☆★ LogJobb2 SQLException :"+se.toString());
            } finally {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                	DBConnectingPool.bringBackConnection(conn);
                }
            }
        } catch (Exception e) {
            System.out.println("☆★ LogJobb2 Exception :"+e.toString());
            System.out.println(sql);
        }
        return mapOut;
    }

}
