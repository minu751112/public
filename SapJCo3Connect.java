package abc.def;

import java.util.HashMap;
import java.util.Map;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.JCoRepository;

public class SapJCo3Connect {
	
	static String SAP_SERVER = "SAP_SERVER";
	public static JCoRepository repo;
	public static JCoDestination dest; 
	private static SapJCo3DestinationProvider sapProvider = new SapJCo3DestinationProvider();
	
	public SapJCo3Connect() {
		if(!com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered()) {
			com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(sapProvider);
		}
		
		try {
			dest = JCoDestinationManager.getDestination(SAP_SERVER);
			repo = dest.getRepository();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public JCoFunction getFunction(String functionName) {
		JCoFunction function = null;
		try {
			function = repo.getFunction(functionName);
		} catch (JCoException e) {
			e.printStackTrace();
			throw new RuntimeException("get function error");
		}
		if(function == null) {
			throw new RuntimeException("no function");
		}
		
		return function;
	}
	
	public void executeFunction(JCoFunction function, boolean impLog, boolean expLog) {
		try {
			JCoContext.begin(dest);
			function.execute(dest);
		} catch (JCoException e) {
	    	if(impLog){
	    		sapImportLogger(function);
	    	}else if(expLog){
	    		sapExportLogger(function);
	    	}
			e.printStackTrace();
		} finally {	
		    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ SAPJCO3!!!!!");
			try {
				JCoContext.end(dest);
			} catch (JCoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void executeFunction(JCoFunction function) {
		executeFunction(function, false, false);
	}
	
	public void clear() {
		repo.clear();
	}

    public void sapImportLogger(JCoFunction function){
    	
    	JCoParameterList jcoImport = function.getImportParameterList();
        System.out.println("☆★☆★☆★☆★☆★☆  Start Import Parameter List  ☆★☆★☆★☆★☆★☆★☆★");
        System.out.println("RFC_FUNCTION : " + function.getName());  
        
        JCoFieldIterator iter = jcoImport.getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            String fieldName = f.getName();
            String fieldValue = f.getValue().toString();
        
        	System.out.println("["+fieldName+"] : " + fieldValue);
        }
        System.out.println("☆★☆★☆★☆★☆★☆  Finish Import Parameter List  ☆★☆★☆★☆★☆★☆★☆★");
        
        sapTableLogger(function, "Import");
    }
    
    public void sapExportLogger(JCoFunction function){
    	
    	JCoParameterList jcoExport = function.getExportParameterList();
        System.out.println("☆★☆★☆★☆★☆★☆  Start Export Parameter List  ☆★☆★☆★☆★☆★☆★☆★");
    	System.out.println("RFC_FUNCTION : " + function.getName());  
    	
        JCoFieldIterator iter = jcoExport.getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            String fieldName = f.getName();
            String fieldValue = f.getValue().toString();
        
        	System.out.println("["+fieldName+"] : " + fieldValue);
        }
        System.out.println("☆★☆★☆★☆★☆★☆  Finish Export Parameter List  ☆★☆★☆★☆★☆★☆★☆★");
    	
    	sapTableLogger(function, "Export");
    	
    }
    
    public void sapTableLogger(JCoFunction function, String Type){
    	
    	int tableLength = function.getTableParameterList().getFieldCount();
    	
    	for(int i=0 ; i<tableLength ; i++){

    		JCoTable jcoTable = function.getTableParameterList().getTable(function.getTableParameterList().getString(i));
    		
    		if(jcoTable.isEmpty()){
    			
    		}else{
        		System.out.println("○●○●○●○●○●○●○●○● [START] "+Type+" Table - " + function.getTableParameterList().getString(i) + " ○●○●○●○●○●○●○●○●");
    	    	
    	    	int recordCnt = jcoTable.getNumRows();
    	        int fieldCnt = jcoTable.getFieldCount();
    	        
    	        for ( int idx = 0 ; idx < recordCnt ; idx++ ) {
    	        	jcoTable.setRow(idx);
    	            Map<String, Object> rowData = new HashMap<String, Object>();
    	            
    	            for ( int colIdx = 0 ; colIdx < fieldCnt ; colIdx++ ) {
    	            	
    	                String fieldName = jcoTable.getString(colIdx);
    	                rowData.put(fieldName, jcoTable.getValue(colIdx));
    	            }
    	            System.out.println(rowData);
    	        }
    	        System.out.println("○●○●○●○●○●○●○●○● [FINISH] "+Type+" Table - " + function.getTableParameterList().getString(i) + " ○●○●○●○●○●○●○●○●");
    		}
    	}

    }
	
}
