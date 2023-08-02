package dbProxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class DBConnectingPool {
    private static LinkedList<Connection> idleConnections = new LinkedList<>();
    private static LinkedList<Connection> busyConnections = new LinkedList<>();
    private static int maxCount = 10;
    private static long maxTimeout = 1000 * 30; // 30 seconds


    static {
        // Create the initial connections in the pool
        initializeConnections();
    }

    private static void initializeConnections() {
        try {
            for (int i = 0; i < maxCount; i++) {
                Connection conn = createConnection();
                if (conn != null) {
                    idleConnections.add(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection createConnection() throws SQLException {
        // Create and return a new connection
    	Connection con = null;
		try {
			con = DB.getConnectionFromEvn();
		} catch (Exception e) {
			// TODO Exception 발생 > Connection 을 만들지 못 할 경우에 대한 후처리
			e.printStackTrace();
		}
        return con;
    }

    public static synchronized Connection getConnection() throws Exception {
        Connection conn = null;
        long startWaitTime = System.currentTimeMillis();

        while (conn == null) {		// conn == null 을 만족해도 최소한 한번은 수행함
            if (!idleConnections.isEmpty()) {
                conn = idleConnections.removeFirst();	// FIFO 방식으로 LinkedList 에서 하나씩 꺼내서 유효성 검증을 해야 함
                
                // idle에 꺼내온 connection 의 유효성 검증
                if (isValidConnection(conn)) {
                    busyConnections.add(conn);
                } else {
                    // idle에서 꺼내온 connection 이 유효하지 않은 connection 이라면 close
                    closeConnection(conn);
                    conn = null;
                }
            } else {
                // 모든 connection이 busy 일때 0.1초씩 슬립시키고, 대기시간이 maxTimeout(30초)를 넘어가면 Exception 
                long elapsedTime = System.currentTimeMillis() - startWaitTime;
                if (elapsedTime > maxTimeout) {
                    throw new Exception("Connection pool wait timeout");
                }
                Thread.sleep(100);
            }
        }

        return conn;
    }

    private static boolean isValidConnection(Connection conn) {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void releaseConnection(Connection conn) {
        System.out.println("☆★☆★☆★☆★☆★ before idle.size : "+idleConnections.size());
        System.out.println("☆★☆★☆★☆★☆★ before busy.size : "+busyConnections.size());
        
        busyConnections.remove(conn);

        if (isValidConnection(conn)) {
            idleConnections.add(conn);
        } else {
            closeConnection(conn);
        }
        
        System.out.println("☆★☆★☆★☆★☆★ after idle.size : "+idleConnections.size());
        System.out.println("☆★☆★☆★☆★☆★ after busy.size : "+busyConnections.size());
    }
}

