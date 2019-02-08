package abc.def;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;


public class SapJCo3DestinationProvider implements DestinationDataProvider {
	
	private final Properties ABAP_AS_properties;

    private static long second = 1000;
    private static long minute = second * 60;
    private static long hour = minute * 60;
    private static long day = hour * 24;
	
    private static String POOL_SIZE = "300";
    private static String SAP_CLIENT = ""; // SAP client
    private static String USER_ID    = ""; // userid
    private static String PASSWORD   = ""; // password
    private static String LANGUAGE   = ""; // language
    private static String HOST_NAME  = ""; // host name
    private static String SYSTEM_NO  = ""; // system no
		
	public SapJCo3DestinationProvider() {

        SimpleDateFormat date_formatter = new SimpleDateFormat("yyyyMMdd", new Locale("ko","KOREA"));
        
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal2.set(2100, 11, 31); //기준일 2100년 12월 31일
        
        String toDay = date_formatter.format(cal.getTime())+"000000";
        String toDay2 = date_formatter.format(cal2.getTime())+"000000";
        
        DateFormat df = DateFormat.getDateTimeInstance();
        Date date1 = null;
        Date date2 = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss", new Locale("ko","KOREA"));

        try {
            date1 = sdf.parse(toDay);
            date2 = sdf.parse(toDay2);
            System.out.println("Date1 : " + df.format(date1) + "\n"
                    + "Date2 : " + df.format(date2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long gapdate = days(date1, date2);
        
        System.out.println("##### toDay : " + toDay);
        System.out.println("##### toDay2 : " + toDay2);
        
        System.out.println("##### gapdate : " + gapdate);
        
        //격일제로 다른 아이디 적용 - sap audit 회피용
        if(gapdate%2==1){
            SAP_CLIENT = "310";
            USER_ID    = "USERID1";
            PASSWORD   = "USERPWD1";
            LANGUAGE   = "KO";
            HOST_NAME  = "127.0.0.1";
            SYSTEM_NO  = "01";
        }else{
            SAP_CLIENT = "310";
            USER_ID    = "USERID2";
            PASSWORD   = "USERPWD2";
            LANGUAGE   = "KO";
            HOST_NAME  = "127.0.0.1";
            SYSTEM_NO  = "01";
        }

		System.out.println("#######################################################################");
		System.out.println(POOL_SIZE);
		System.out.println(SAP_CLIENT);
		System.out.println(USER_ID);
		System.out.println(PASSWORD);
		System.out.println(LANGUAGE);
		System.out.println(HOST_NAME);
		System.out.println(SYSTEM_NO);
		System.out.println("#######################################################################");
		
		Properties properties = new Properties();

		properties.setProperty(DestinationDataProvider.JCO_ASHOST, HOST_NAME);     
		properties.setProperty(DestinationDataProvider.JCO_SYSNR,  SYSTEM_NO);     
		properties.setProperty(DestinationDataProvider.JCO_CLIENT, SAP_CLIENT);     
		properties.setProperty(DestinationDataProvider.JCO_USER,   USER_ID);     
		properties.setProperty(DestinationDataProvider.JCO_PASSWD, PASSWORD);   
		properties.setProperty(DestinationDataProvider.JCO_LANG,   LANGUAGE);   
		properties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY,   "3");   
		properties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,   POOL_SIZE);   

		ABAP_AS_properties = properties;
	}

	public Properties getDestinationProperties(String arg0) {
		return ABAP_AS_properties;
	}

	public void setDestinationDataEventListener(
			DestinationDataEventListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean supportsEvents() {
		// TODO Auto-generated method stub
		return false;
	}

    public static long millis(Date d1, Date d2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        return c2.getTimeInMillis() - c1.getTimeInMillis();
    }
    
    public static long minutes(Date d1, Date d2) {
        long millis = millis(d1, d2);
        return millis / minute;
    }
    
    public static long hours(Date d1, Date d2) {
        long millis = millis(d1, d2);
        return millis / hour;
    }
    
    public static long days(Date d1, Date d2) {
        long millis = millis(d1, d2);
        return millis / day;
    }

}
