package abc.ffff.sap.rfc;

import lghausys.twms.util.CommonUtil;
import lghausys.twms.util.StringUtil;

import com.sap.conn.jco.*;

import abc.def.SapJCo3Connect;

import devon.core.collection.LData;
import devon.core.collection.LMultiData;
import devon.core.log.LLog;

public class zRfcFunction111 {
    public JCoTable oderitems;
    public JCoTable returntbl;
    static SapJCo3Connect sapConnect = new SapJCo3Connect();

    private String SALESDOCUMENT;

    public zRfcFunction111() {
    }

    public boolean loginCheck() {
        boolean bb = true;
        if (!com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered()) {
            bb = false;
        }
        return bb;
    }

    /**
     * 완제품 자재 발주
     * @param :  하단 참조
     * String division    사업부:2
     * String salesGrp    영업그룹(여신영역):3
     * String salesOff    영업팀:4
     * String reqDateH    납기요청일(입력):8
     * String partnNumbAg 거래처:로그인대리점(숫자만):10
     * String partnNumbWe 원부자재_배송처(인도처):10
     * String partnNumbPe 영업사원코드:10
     * String partnNumbSo 실제현장코드:ERP인도처코드:10
     * String distrChan   유통경로
     * String soAgtOrder  발주상세내역.
     * @return: String
     * @throws Exception 
     */

    public LData setSalesOrder( String division
                              , String salesGrp
                              , String salesOff
                              , String reqDateH
                              , String partnNumbAg
                              , String partnNumbWe
                              , String partnNumbPe
                              , String partnNumbSo
                              , String transpzoneWe
                              , String streetWe
                              , String cityWe
                              , String telNo
                              , String nameWe
                              , String distrChan
                              , String setOrdYn
                              , String stdYmd
                              , LMultiData soAgtOrder
                              ) throws Exception {
        
        LData result = new LData();
        
        String mtrlCd = "";         //자재코드
        String ordQty = "";         //발주수량
        String pmsSapSaleAmt = "";  //판매금액
        String mtrlOrdIno = "";     //자재발주품번
        String rem = "";            //비고
        
        String storeLoc = "8800";   
        
        
        LLog.debug.println(this.getClass().getName() + " 유통경로 : " + distrChan);

        try {
            JCoFunction function = sapConnect.getFunction("Z_RFC_SD_ORDER01");
            JCoParameterList importParam = function.getImportParameterList();
            
            importParam.setValue("DOC_TYPE", "ZI03");
            importParam.setValue("SALES_ORG", "3000");
            importParam.setValue("DISTR_CHAN", distrChan);
            importParam.setValue("DIVISION", division);
            importParam.setValue("SALES_GRP", salesGrp);
            importParam.setValue("SALES_OFF", salesOff);
            importParam.setValue("REQ_DATE_H", reqDateH);
            importParam.setValue("PARTN_ROLE_AG", "AG");
            importParam.setValue("PARTN_NUMB_AG", partnNumbAg);
            importParam.setValue("PARTN_ROLE_WE", "WE");
            importParam.setValue("PARTN_NUMB_WE", partnNumbWe);
            importParam.setValue("PARTN_ROLE_PE", "VE");
            importParam.setValue("PARTN_NUMB_PE", partnNumbPe);
            importParam.setValue("LANGU_WE", "3");
            importParam.setValue("I_SETYN", setOrdYn);    //Set 주문 판단 파라메터
            importParam.setValue("PRICE_DATE", stdYmd);    //견적기준일
            
            importParam.setValue("TRANSPZONE_WE", transpzoneWe); //운송지역코드
            if(!nullToString(transpzoneWe).equals("")) {
                importParam.setValue("STREET_WE", streetWe); //주소2(번지)
                importParam.setValue("CITY_WE", cityWe);
                importParam.setValue("TELEPHONE_WE", telNo);
                importParam.setValue("NAME_WE", nameWe);
            }
            
            //importParam.setValue("SO"        , "PARTN_ROLE_SO"); YV 불필요
            importParam.setValue("PARTN_NUMB_SO", partnNumbSo);
            LLog.debug.println("##### Z_RFC_SD_ORDER01 -- HEADER PARAMETER #####");
            LLog.debug.println(importParam.toString());
            LLog.debug.println("##### Z_RFC_SD_ORDER01 -- HEADER PARAMETER #####");

            //LLog.debug.println(this.getClass().getName() + " soAgtOrder : " + soAgtOrder);
            if( soAgtOrder != null)
            {
                //LLog.debug.println(this.getClass().getName() + " soAgtOrder.getDataCount() : " + soAgtOrder.getDataCount());
                JCoTable oder_items = function.getTableParameterList().getTable("ORDER_ITEMS");
                for(int i = 0; i<soAgtOrder.getDataCount(); i++)
                {
                    mtrlCd  = soAgtOrder.getString("mtrlCd" , i); //자재코드
                    ordQty  = soAgtOrder.getString("ordQty" , i); //수량
                    pmsSapSaleAmt = soAgtOrder.getString("pmsSapSaleAmt", i); //판매금액
                    mtrlOrdIno = soAgtOrder.getString("mtrlOrdIno", i); //자재발주품번
                    rem = soAgtOrder.getString("rem", i); //비고

                    storeLoc = soAgtOrder.getString("storeLoc", i); //저장위치 : 그리드에서 넘어온 저장위치가 빈값이면 8800을 넣어줌 
                    
//                    LLog.debug.println(this.getClass().getName() + " mtrlCd  : " + mtrlCd );
//                    LLog.debug.println(this.getClass().getName() + " ordQty  : " + ordQty );
//                    LLog.debug.println(this.getClass().getName() + " pmsSapSaleAmt : " + pmsSapSaleAmt);
//                    LLog.debug.println(this.getClass().getName() + " mtrlOrdIno : " + mtrlOrdIno);
//                    LLog.debug.println(this.getClass().getName() + " storeLoc : " + storeLoc);

                    oder_items.appendRow();
                    oder_items.setValue("ITM_NUMBER", mtrlOrdIno); //자재발주품번
                    oder_items.setValue("MATERIAL", mtrlCd); //자재코드
                    oder_items.setValue("PLANT", "3010"); //플랜트
                    oder_items.setValue("REQ_QTY", ordQty); //발주수량
                    oder_items.setValue("NETWR", pmsSapSaleAmt); //판매금액
                    oder_items.setValue("TEXT_ID", "0000");
                    oder_items.setValue("LANGU", "KO");
                    oder_items.setValue("TEXT_LINE", rem);
                    oder_items.setValue("STORE_LOC", storeLoc);  
                    
                    LLog.debug.println("##### Z_RFC_SD_ORDER01 -- ORDER_ITEMS PARAMETER #####");
                    LLog.debug.println(" oder_items["+i+"] : " + oder_items);
                    LLog.debug.println("##### Z_RFC_SD_ORDER01 -- ORDER_ITEMS PARAMETER #####");
                }
                sapConnect.executeFunction(function, true, true); // executeFunction(function);
            }
            
            oderitems = function.getTableParameterList().getTable("ORDER_ITEMS");
            returntbl = function.getTableParameterList().getTable("RETURN");
            LLog.debug.println(this.getClass().getName() + " oderitems.getNumRows() : " + oderitems.getNumRows());
            LLog.debug.println(this.getClass().getName() + " returntbl.getNumRows() : " + returntbl.getNumRows());
            
            JCoParameterList exportParam = function.getExportParameterList();
            SALESDOCUMENT = nullToString((String)exportParam.getValue("SALESDOCUMENT"));
            LLog.debug.println(this.getClass().getName() + " SALESDOCUMENT : " + SALESDOCUMENT);
            
            String SALESDOCUMENT = isNullGetEmpty(exportParam.getValue("SALESDOCUMENT")+"", false);
            if(!"".equals(SALESDOCUMENT)) {
                LLog.debug.println(this.getClass().getName() + " returntbl.getNumbRows()" + returntbl.getNumRows());
                result.setString("SALESDOCUMENT", SALESDOCUMENT);
            } else {
                result.setString("SALESDOCUMENT", "");
            }
            result.setNullToInitialize(true);
        } catch (Exception e) {
             LLog.err.println(this.getClass().getName() + " e \n" + e.getMessage());
             e.printStackTrace();
             throw e;
        } 
        return result;
    }

    public String nullToString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }
    

	public String isNullGetEmpty(String sTarget, boolean isHtmlView) {
		
		String returnStr = "";
		
		if(sTarget != null && sTarget.trim().length() > 0) {
			returnStr = sTarget;
		} else {
			if(isHtmlView) {
				returnStr = "&nbsp;";
			}
		}
		return returnStr; 
	}


}
