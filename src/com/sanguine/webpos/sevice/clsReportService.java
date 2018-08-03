package com.sanguine.webpos.sevice;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.webpos.bean.clsWaiterAnalysisBean;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsBillDtl;
import com.sanguine.webpos.bean.clsBillItemDtlBean;
import com.sanguine.webpos.bean.clsCostCenterBean;
import com.sanguine.webpos.bean.clsGenericBean;
import com.sanguine.webpos.bean.clsGroupSubGroupItemBean;
import com.sanguine.webpos.bean.clsGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsItemWiseConsumption;
import com.sanguine.webpos.bean.clsKOTAnalysisBean;
import com.sanguine.webpos.bean.clsTaxCalculationDtls;
import com.sanguine.webpos.bean.clsVoidBillDtl;
import com.sanguine.webpos.comparator.clsBillComparator;
import com.sanguine.webpos.comparator.clsBillComplimentaryComparator;
import com.sanguine.webpos.comparator.clsCostCenterComparator;
import com.sanguine.webpos.comparator.clsGroupSubGroupComparator;
import com.sanguine.webpos.comparator.clsGroupSubGroupWiseSalesComparator;
import com.sanguine.webpos.comparator.clsItemConsumptionComparator;
import com.sanguine.webpos.comparator.clsVoidBillComparator;
import com.sanguine.webpos.comparator.clsWaiterWiseSalesComparator;
import com.sanguine.webpos.util.clsGroupWiseComparator;

@Service
public class clsReportService 
{

	@Autowired
	intfBaseService objBaseService;
	
	public List funProcessDayEndReport(String posCode,String fromDate1,String toDate1)
	{
		StringBuilder sbSql = new StringBuilder();
		List list = new ArrayList();
		try
		{
		sbSql.append("select b.strPOSName,DATE_FORMAT(date(a.dtePOSDate),'%d-%m-%Y'),dblHDAmt,dblDiningAmt,dblTakeAway,dblTotalSale,dblFloat"
                 + ",dblCash,dblAdvance,dblTransferIn,dblTotalReceipt,dblPayments,dblWithdrawal,dblTransferOut,dblRefund"
                 + ",dblTotalPay,dblCashInHand,dblNoOfBill,dblNoOfVoidedBill,dblNoOfModifyBill,strWSStockAdjustmentNo,strExciseBillGeneration "
                 + " ,a.dblNetSale,a.dblGrossSale,a.dblAPC"
                 + " from tbldayendprocess a,tblposmaster b where a.strPOSCode=b.strPOSCode ");

         if ("All".equals(posCode))
         {
             sbSql.append(" and date(a.dtePOSDate) between '" + fromDate1 + "' and '" + toDate1 + "' ");
         }
         else
         {
             String temp = posCode;
             StringBuilder sb = new StringBuilder(temp);
             int len = temp.length();
             int lastInd = sb.lastIndexOf(" ");
             String POSCode = sb.substring(lastInd + 1, len).toString();
             sbSql.append(" and a.strPOSCode='" + POSCode + "' and date(a.dtePOSDate) between '" + fromDate1 + "' and '" + toDate1 + "'");
         }
         list = objBaseService.funGetList(sbSql, "sql");
        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List<clsBillDtl> funProcessComplimentaryDetailReport(String strPosCode,String dteFromDate,String dteToDate,String strReasonCode,String strShiftNo)
	{
		List<clsBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQBill = new StringBuilder();
        StringBuilder sqlLiveModifierBuilder = new StringBuilder();
        StringBuilder sqlQModifierBuilder = new StringBuilder();
		try
		{
			sbSqlLive.setLength(0);
            sbSqlQBill.setLength(0);
            sqlLiveModifierBuilder.setLength(0);
            sqlQModifierBuilder.setLength(0);

            //live data              
            sbSqlLive.append("SELECT IFNULL(a.strBillNo,''), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate, IFNULL(b.strItemName,'') "
                    + ",sum(b.dblQuantity),b.dblRate,sum(b.dblQuantity*b.dblRate) AS dblAmount, IFNULL(f.strPosName,'') "
                    + ", IFNULL(g.strWShortName,'NA') AS strWShortName, IFNULL(e.strReasonName,''), IFNULL(a.strRemarks,'') "
                    + ", IFNULL(i.strGroupName,'') AS strGroupName, IFNULL(b.strKOTNo,'') "
                    + ",a.strPOSCode, IFNULL(h.strTableName,'') AS strTableName, IFNULL(b.strItemCode,'        ') "
                    + "FROM tblbillhd a "
                    + "Inner JOIN tblbillcomplementrydtl b ON a.strBillNo = b.strBillNo "
                    + "left outer JOIN tblreasonmaster e ON a.strReasonCode = e.strReasonCode "
                    + "LEFT OUTER "
                    + "JOIN tblposmaster f ON a.strPOSCode=f.strPosCode "
                    + "LEFT OUTER  "
                    + "JOIN tblwaitermaster g ON a.strWaiterNo=g.strWaiterNo "
                    + "LEFT OUTER "
                    + "JOIN tbltablemaster h ON a.strTableNo=h.strTableNo "
                    + "LEFT OUTER "
                    + "JOIN tblitemcurrentstk i ON b.strItemCode=i.strItemCode "
                    + "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

            //live modifiers
            sqlLiveModifierBuilder.append("select ifnull(a.strBillNo,''),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate,b.strModifierName, sum(b.dblQuantity), b.dblRate,sum(b.dblQuantity*b.dblRate) as dblAmount"
                    + " ,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, a.strRemarks,ifnull(i.strGroupName,'') as strGroupName, "
                    + " ifnull(j.strKOTNo,''),a.strPOSCode,ifnull(h.strTableName,'') as strTableName,ifnull(b.strItemCode,'        ')  "
                    + " from tblbillhd a"
                    + " INNER JOIN  tblbillmodifierdtl b on a.strBillNo = b.strBillNo"
                    + " left outer join  tblbillsettlementdtl c on a.strBillNo = c.strBillNo"
                    + " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
                    + " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
                    + " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
                    + " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
                    + " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
                    + " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
                    + " left outer join  tblbilldtl j on b.strBillNo = j.strBillNo  "
                    + " where d.strSettelmentType = 'Complementary' ");

            //Q data
            sbSqlQBill.append("SELECT IFNULL(a.strBillNo,''), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate, IFNULL(b.strItemName,'') "
                    + ",sum(b.dblQuantity),b.dblRate,sum(b.dblQuantity*b.dblRate) AS dblAmount, IFNULL(f.strPosName,'') "
                    + ", IFNULL(g.strWShortName,'NA') AS strWShortName, IFNULL(e.strReasonName,''), IFNULL(a.strRemarks,'') "
                    + ", IFNULL(i.strGroupName,'') AS strGroupName, IFNULL(b.strKOTNo,'') "
                    + ",a.strPOSCode, IFNULL(h.strTableName,'') AS strTableName, IFNULL(b.strItemCode,'        ') "
                    + "FROM tblqbillhd a "
                    + "INNER JOIN tblqbillcomplementrydtl b ON a.strBillNo = b.strBillNo "
                    + "left outer JOIN tblreasonmaster e ON a.strReasonCode = e.strReasonCode "
                    + "LEFT OUTER "
                    + "JOIN tblposmaster f ON a.strPOSCode=f.strPosCode "
                    + "LEFT OUTER  "
                    + "JOIN tblwaitermaster g ON a.strWaiterNo=g.strWaiterNo "
                    + "LEFT OUTER "
                    + "JOIN tbltablemaster h ON a.strTableNo=h.strTableNo "
                    + "LEFT OUTER "
                    + "JOIN tblitemcurrentstk i ON b.strItemCode=i.strItemCode "
                    + "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

            //Q modifiers
            sqlQModifierBuilder.append("select ifnull(a.strBillNo,''),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate,b.strModifierName,sum(b.dblQuantity), b.dblRate,sum(b.dblQuantity*b.dblRate,0) as dblAmount,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName,ifnull(e.strReasonName,'') as strReasonName, a.strRemarks,ifnull(i.strGroupName,'') as strGroupName,\n"
                    + "ifnull(j.strKOTNo,''),a.strPOSCode,ifnull(h.strTableName,'') as strTableName,ifnull(b.strItemCode,'        ')  "
                    + " from tblqbillhd a"
                    + " INNER JOIN  tblqbillmodifierdtl b on a.strBillNo = b.strBillNo"
                    + " left outer join  tblqbillsettlementdtl c on a.strBillNo = c.strBillNo"
                    + " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
                    + " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
                    + " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
                    + " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
                    + " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
                    + " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
                    + " left outer join  tblqbilldtl j on b.strBillNo = j.strBillNo  "
                    + " where d.strSettelmentType = 'Complementary' ");

            if (!strPosCode.equalsIgnoreCase("All"))
            {
                sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
                sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
                sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
                sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            }
            if (!strReasonCode.equalsIgnoreCase("All"))
            {
                sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
                sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
                sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
                sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
            }
            sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
            sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
            sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
            sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
            
            sbSqlLive.append("  "
                    + " group by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode "
                    + " order by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode ");
            sbSqlQBill.append("  "
                    + " group by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode "
                    + " order by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode ");
            sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "'  "
                    + " group by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName "
                    + " order by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName ");
            sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "'  "
                    + " group by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName "
                    + " order by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName ");
            
            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            	Object[] obj = (Object[]) listSqlLive.get(i);
                clsBillDtl objItemDtl = new clsBillDtl();

                objItemDtl.setStrBillNo(obj[0].toString());
                objItemDtl.setDteBillDate(obj[1].toString());
                objItemDtl.setStrItemName(obj[2].toString());
                objItemDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));//itemQty
                objItemDtl.setDblModQuantity(0);//modifierQty
                objItemDtl.setDblRate(Double.parseDouble(obj[4].toString()));
                objItemDtl.setDblAmount(Double.parseDouble(obj[5].toString()));
                objItemDtl.setStrPosName(obj[6].toString());
                objItemDtl.setStrWShortName(obj[7].toString());
                objItemDtl.setStrReasonName(obj[8].toString());
                objItemDtl.setStrRemarks(obj[9].toString());
                objItemDtl.setStrGroupName(obj[10].toString());
                objItemDtl.setStrKOTNo(obj[11].toString());
                objItemDtl.setStrPOSCode(obj[12].toString());
                objItemDtl.setStrTableName(obj[13].toString());
                objItemDtl.setStrItemCode(obj[14].toString());

                listOfCompliItemDtl.add(objItemDtl);
            	}
            }
            
            //QFile
            List listSqlQBill = objBaseService.funGetList(sbSqlQBill,"sql");
            if(listSqlQBill.size()>0)
            {
            	for(int i=0;i<listSqlQBill.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQBill.get(i);
                clsBillDtl objItemDtl = new clsBillDtl();

                objItemDtl.setStrBillNo(obj[0].toString());
                objItemDtl.setDteBillDate(obj[1].toString());
                objItemDtl.setStrItemName(obj[2].toString());
                objItemDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));//itemQty
                objItemDtl.setDblModQuantity(0);//modifierQty
                objItemDtl.setDblRate(Double.parseDouble(obj[4].toString()));
                objItemDtl.setDblAmount(Double.parseDouble(obj[5].toString()));
                objItemDtl.setStrPosName(obj[6].toString());
                objItemDtl.setStrWShortName(obj[7].toString());
                objItemDtl.setStrReasonName(obj[8].toString());
                objItemDtl.setStrRemarks(obj[9].toString());
                objItemDtl.setStrGroupName(obj[10].toString());
                objItemDtl.setStrKOTNo(obj[11].toString());
                objItemDtl.setStrPOSCode(obj[12].toString());
                objItemDtl.setStrTableName(obj[13].toString());
                objItemDtl.setStrItemCode(obj[14].toString());

                listOfCompliItemDtl.add(objItemDtl);
            	}
            }
            
            Comparator<clsBillDtl> posNameComparator = new Comparator<clsBillDtl>()
            {

                @Override
                public int compare(clsBillDtl o1, clsBillDtl o2)
                {
                    return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
                }
            };

            Comparator<clsBillDtl> billDateComparator = new Comparator<clsBillDtl>()
            {

                @Override
                public int compare(clsBillDtl o1, clsBillDtl o2)
                {
                    return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
                }
            };
            Comparator<clsBillDtl> billNoComparator = new Comparator<clsBillDtl>()
            {

                @Override
                public int compare(clsBillDtl o1, clsBillDtl o2)
                {
                    return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
                }
            };
            Comparator<clsBillDtl> kotNoComparator = new Comparator<clsBillDtl>()
            {

                @Override
                public int compare(clsBillDtl o1, clsBillDtl o2)
                {
                    return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
                }
            };
            Comparator<clsBillDtl> itemCodeComparator = new Comparator<clsBillDtl>()
            {

                @Override
                public int compare(clsBillDtl o1, clsBillDtl o2)
                {
                    return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
                }
            };

            Collections.sort(listOfCompliItemDtl, new clsBillComplimentaryComparator(
                    posNameComparator, billDateComparator, billNoComparator, kotNoComparator, itemCodeComparator
            ));            

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfCompliItemDtl;
	
	}
	
	public List<clsBillDtl> funProcessComplimentarySummaryReport(String strPosCode,String dteFromDate,String dteToDate,String strReasonCode,String strShiftNo) 
	{
		List<clsBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQBill = new StringBuilder();
        StringBuilder sqlLiveModifierBuilder = new StringBuilder();
        StringBuilder sqlQModifierBuilder = new StringBuilder();
		try
		{
		sbSqlLive.setLength(0);
        sbSqlQBill.setLength(0);
        sqlLiveModifierBuilder.setLength(0);
        sqlQModifierBuilder.setLength(0);
       
        sbSqlLive.append("select ifnull(a.strBillNo,'')as strBillNo, ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblRate*b.dblQuantity), 0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, ifnull(a.strRemarks,'') as strRemarks  "
                + "from tblbillhd a   "
                + "INNER JOIN tblbillcomplementrydtl b on a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                + "left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode   "
                + "left outer join tblposmaster f on a.strPOSCode=f.strPosCode   "
                + "left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo  "
                + "left outer join tbltablemaster h on  a.strTableNo=h.strTableNo  "
                + "left outer join tblitemcurrentstk i on b.strItemCode=i.strItemCode  "
                + "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");
        //live modifiers
        sqlLiveModifierBuilder.append("select ifnull(a.strBillNo,''),ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblQuantity*b.dblRate),0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,''), ifnull(a.strRemarks,'') as strRemarks "
                + " from tblbillhd a"
                + " INNER JOIN  tblbillmodifierdtl b on a.strBillNo = b.strBillNo"
                + " left outer join  tblbillsettlementdtl c on a.strBillNo = c.strBillNo"
                + " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
                + " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
                + " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
                + " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
                + " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
                + " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
                + " left outer join  tblbilldtl j on b.strBillNo = j.strBillNo  "
                + " where d.strSettelmentType = 'Complementary' ");

        //Q data
        sbSqlQBill.append("select ifnull(a.strBillNo,'')as strBillNo, ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblRate*b.dblQuantity), 0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, ifnull(a.strRemarks,'') as strRemarks  "
                + "from tblqbillhd a   "
                + "INNER JOIN  tblqbillcomplementrydtl b on a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                + "left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode   "
                + "left outer join tblposmaster f on a.strPOSCode=f.strPosCode   "
                + "left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo  "
                + "left outer join tbltablemaster h on  a.strTableNo=h.strTableNo  "
                + "left outer join tblitemcurrentstk i on b.strItemCode=i.strItemCode  "
                + "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

        //Q modifiers
        sqlQModifierBuilder.append("select ifnull(a.strBillNo,''),ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,sum(b.dblQuantity*b.dblRate) as dblAmount ,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName, e.strReasonName, a.strRemarks "
                + " from tblqbillhd a"
                + " INNER JOIN  tblqbillmodifierdtl b on a.strBillNo = b.strBillNo"
                + " left outer join  tblqbillsettlementdtl c on a.strBillNo = c.strBillNo"
                + " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
                + " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
                + " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
                + " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
                + " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
                + " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
                + " left outer join  tblqbilldtl j on b.strBillNo = j.strBillNo  "
                + " where d.strSettelmentType = 'Complementary' ");

        if (!strPosCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
        }
        if (!strReasonCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
        }
        sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        
        sbSqlLive.append("  "
                + " group by a.strPOSCode,a.strBillNo "
                + " order by a.strPOSCode,a.strBillNo ");
        sbSqlQBill.append("  "
                + " group by a.strPOSCode,a.strBillNo "
                + " order by a.strPOSCode,a.strBillNo ");
        sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "'  "
                + " group by a.strPOSCode,a.strBillNo "
                + " order by a.strPOSCode,a.strBillNo ");
        sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "'  "
                + " group by a.strPOSCode,a.strBillNo "
                + " order by a.strPOSCode,a.strBillNo ");

        //live data
        List ListSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
        if(ListSqlLive.size()>0)
        {
        	for(int i=0;i<ListSqlLive.size();i++)
        	{
        	Object[] obj = (Object[]) ListSqlLive.get(i); 
            clsBillDtl objItemDtl = new clsBillDtl();

            objItemDtl.setStrBillNo(obj[0].toString());
            objItemDtl.setDteBillDate(obj[1].toString());
            objItemDtl.setDblAmount(Double.parseDouble(obj[2].toString()));
            objItemDtl.setStrPosName(obj[3].toString());
            objItemDtl.setStrWShortName(obj[4].toString());
            objItemDtl.setStrReasonName(obj[5].toString());
            objItemDtl.setStrRemarks(obj[6].toString());

            listOfCompliItemDtl.add(objItemDtl);
        	}
        }
        
        //QFile
        List listSqlQBill = objBaseService.funGetList(sbSqlQBill,"sql");
        if(listSqlQBill.size()>0)
        {
        	for(int i=0;i<listSqlQBill.size();i++)
        	{
        	Object[] obj = (Object[]) listSqlQBill.get(i);
            clsBillDtl objItemDtl = new clsBillDtl();

            objItemDtl.setStrBillNo(obj[0].toString());
            objItemDtl.setDteBillDate(obj[1].toString());
            objItemDtl.setDblAmount(Double.parseDouble(obj[2].toString()));
            objItemDtl.setStrPosName(obj[3].toString());
            objItemDtl.setStrWShortName(obj[4].toString());
            objItemDtl.setStrReasonName(obj[5].toString());
            objItemDtl.setStrRemarks(obj[6].toString());

            listOfCompliItemDtl.add(objItemDtl);
        	}
        }
        
        Comparator<clsBillDtl> posNameComparator = new Comparator<clsBillDtl>()
        {
        	@Override
             public int compare(clsBillDtl o1,clsBillDtl o2)
              {
        		return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
              }
         };

          Comparator<clsBillDtl> billDateComparator = new Comparator<clsBillDtl>()
          {
        	  @Override
              public int compare(clsBillDtl o1, clsBillDtl o2)
              {
        		  return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
              }
           };
           Comparator<clsBillDtl> billNoComparator = new Comparator<clsBillDtl>()
           {
        	   @Override
               public int compare(clsBillDtl o1, clsBillDtl o2)
               {
        		   return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
               }
           };

           Collections.sort(listOfCompliItemDtl, new clsBillComplimentaryComparator(
                        posNameComparator, billDateComparator, billNoComparator
           ));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfCompliItemDtl;
	}
	
	public List<clsBillDtl> funProcessComplimentaryGroupWiseReport(String strPosCode,String dteFromDate,String dteToDate,String strReasonCode,String strShiftNo)
	{
		List<clsBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQBill = new StringBuilder();
        StringBuilder sqlLiveModifierBuilder = new StringBuilder();
        StringBuilder sqlQModifierBuilder = new StringBuilder();
        
		sbSqlLive.setLength(0);
        sbSqlQBill.setLength(0);
        sqlLiveModifierBuilder.setLength(0);
        sqlQModifierBuilder.setLength(0);
        try
        {
        //live data
        sbSqlLive.append("SELECT e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strItemName,b.dblRate"
                + ", SUM(b.dblQuantity) AS dblQnty,SUM(b.dblRate* b.dblQuantity) AS dblAmount "
                + "FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                + "WHERE a.strBillNo = b.strBillNo  "
                + "AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  "
        );

        //live modifiers
        sqlLiveModifierBuilder.append(" select e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strModifierName"
                + ",b.dblRate,sum(b.dblQuantity),SUM(b.dblRate*b.dblQuantity) as dblAmount"
                + " from tblbillhd a,tblbillmodifierdtl b,tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h"
                + " where a.strBillNo = b.strBillNo "
                + " and  a.strBillNo = c.strBillNo "
                + " and c.strSettlementCode = d.strSettelmentCode "
                + " and  a.strPOSCode=e.strPosCode  "
                + " and left(b.strItemCode,7)=f.strItemCode"
                + " and f.strSubGroupCode=g.strSubGroupCode"
                + " and g.strGroupCode=h.strGroupCode"
                + " and d.strSettelmentType='Complementary' ");

        //Q data
        sbSqlQBill.append("SELECT e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strItemName,b.dblRate"
                + ", SUM(b.dblQuantity) AS dblQnty,SUM(b.dblRate* b.dblQuantity) AS dblAmount "
                + "FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                + "WHERE a.strBillNo = b.strBillNo  "
                + "AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  ");

        //Q modifiers
        sqlQModifierBuilder.append("select e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strModifierName"
                + ",b.dblRate,sum(b.dblQuantity),SUM(b.dblRate*b.dblQuantity) as dblAmount"
                + " from tblqbillhd a,tblqbillmodifierdtl b,tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e \n"
                + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h"
                + " where a.strBillNo = b.strBillNo "
                + " and  a.strBillNo = c.strBillNo "
                + " and c.strSettlementCode = d.strSettelmentCode "
                + " and  a.strPOSCode=e.strPosCode  "
                + " and left(b.strItemCode,7)=f.strItemCode"
                + " and f.strSubGroupCode=g.strSubGroupCode"
                + " and g.strGroupCode=h.strGroupCode"
                + " and d.strSettelmentType='Complementary'");

        if (!strPosCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
            sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
        }
        if (!strReasonCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
            sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
        }
        sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        
        sbSqlLive.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
                + " group by h.strGroupCode,b.strItemCode"
                + " order by h.strGroupCode,b.strItemCode;");
        sbSqlQBill.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
                + " group by h.strGroupCode,b.strItemCode"
                + " order by h.strGroupCode,b.strItemCode;");
        sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
                + " group by h.strGroupCode,b.strItemCode,b.strModifierName"
                + " order by h.strGroupCode,b.strItemCode;");
        sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
                + " group by h.strGroupCode,b.strItemCode,b.strModifierName"
                + " order by h.strGroupCode,b.strItemCode;");

        //live data
        List listSqlLive =objBaseService.funGetList(sbSqlLive,"sql");
        if(listSqlLive.size()>0)
        {
        	for(int i=0;i<listSqlLive.size();i++)
        	{
        	Object[] obj = (Object[]) listSqlLive.get(i);
            clsBillDtl objItemDtl = new clsBillDtl();

            objItemDtl.setStrPosName(obj[0].toString());
            objItemDtl.setStrGroupCode(obj[1].toString());
            objItemDtl.setStrGroupName(obj[2].toString());
            objItemDtl.setStrItemCode(obj[3].toString());
            objItemDtl.setStrItemName(obj[4].toString());
            objItemDtl.setDblRate(Double.parseDouble(obj[5].toString()));
            objItemDtl.setDblQuantity(Double.parseDouble(obj[6].toString()));
            objItemDtl.setDblAmount(Double.parseDouble(obj[7].toString()));

            listOfCompliItemDtl.add(objItemDtl);
        	}
        }
        
        //QFile
        List listSqlQBill = objBaseService.funGetList(sbSqlQBill,"sql");
        if(listSqlQBill.size()>0)
        {
        	for(int i=0;i<listSqlQBill.size();i++)
        	{
        	Object[] obj = (Object[]) listSqlQBill.get(i);
        	clsBillDtl objItemDtl = new clsBillDtl();

            objItemDtl.setStrPosName(obj[0].toString());
            objItemDtl.setStrGroupCode(obj[1].toString());
            objItemDtl.setStrGroupName(obj[2].toString());
            objItemDtl.setStrItemCode(obj[3].toString());
            objItemDtl.setStrItemName(obj[4].toString());
            objItemDtl.setDblRate(Double.parseDouble(obj[5].toString()));
            objItemDtl.setDblQuantity(Double.parseDouble(obj[6].toString()));
            objItemDtl.setDblAmount(Double.parseDouble(obj[7].toString()));

            listOfCompliItemDtl.add(objItemDtl);
        	}
        }
        	
        Comparator<clsBillDtl> posNameComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
            }
        };
        Comparator<clsBillDtl> groupNameComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrGroupName().compareToIgnoreCase(o2.getStrGroupName());
            }
        };

        Comparator<clsBillDtl> itemNameComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrItemName().compareToIgnoreCase(o2.getStrItemName());
            }
        };

        Collections.sort(listOfCompliItemDtl, new clsBillComplimentaryComparator(
                posNameComparator, groupNameComparator, itemNameComparator
        ));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfCompliItemDtl;
	}
	
	public List<clsItemWiseConsumption> funProcessItemWiseConsumptionReport(String posCode,String fromDate,String toDate,String groupCode,String costCenterCode,String strShiftNo,String printZeroAmountModi)
	{
		StringBuilder sbSql = new StringBuilder();
        StringBuilder sbSqlMod = new StringBuilder();
        StringBuilder sbFilters = new StringBuilder();
        Map<String, clsItemWiseConsumption> hmItemWiseConsumption = new HashMap<String, clsItemWiseConsumption>();
        String costCenterCd = "", costCenterNm = "";
        int sqlNo = 0;
        List<clsItemWiseConsumption> list = new ArrayList<clsItemWiseConsumption>();
        
        try
        {
        // Code for Sales Qty for bill detail and bill modifier live & q data
        // for Sales Qty for bill detail live data  
        sbSql.setLength(0);

        sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), SUM(b.dblamount),b.dblRate, e.strposname,SUM(b.dblDiscountAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
        }
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        
        sbSql.append(" group by b.strItemCode,a.strBillNo  order by j.strCostCenterCode,b.strItemName");
        List listSqlLive = objBaseService.funGetList(sbSql,"sql");
        if(listSqlLive.size()>0)
        {
        	for(int i=0;i<listSqlLive.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlLive.get(i);
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString())));
                objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obj[4].toString()));
                //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSales.getDouble(3));
            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setSaleQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setComplimentaryQty(0);
                objItemWiseConsumption.setNcQty(0);
                objItemWiseConsumption.setSubTotal(Double.parseDouble(obj[3].toString()));
                objItemWiseConsumption.setDiscAmt(Double.parseDouble(obj[6].toString()));
                objItemWiseConsumption.setSaleAmt(Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString()));
                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setPromoQty(0);
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[11].toString());
                objItemWiseConsumption.setCostCenterName(obj[12].toString());
                costCenterCd = obj[11].toString();
                costCenterNm = obj[12].toString();
                objItemWiseConsumption.setExternalCode(obj[10].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);

            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }
            sbSqlMod.setLength(0);
            if (printZeroAmountModi.equalsIgnoreCase("Yes"))
            {
                //for Sales Qty for bill modifier live data 

                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                        + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType!='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + " group by b.strItemCode,b.strModifierName ");
            }
            else
            {
                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                        + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType!='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' AND b.dblamount>0"
                        + " group by b.strItemCode,b.strModifierName ");
            }

            List listSqlMod = objBaseService.funGetList(sbSqlMod,"sql");
            if(listSqlMod.size()>0)
            {
            	for(int j=0;j<listSqlMod.size();j++)
            	{
                Object[] objMod = (Object[]) listSqlMod.get(j);
                if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString()))
                {
                    objItemWiseConsumption = hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString());
                    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + ((Double.parseDouble(objMod[3].toString())) - Double.parseDouble(objMod[6].toString())));
                    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(objMod[3].toString()));
                    
                }
                else
                {
                    sqlNo++;
                    objItemWiseConsumption = new clsItemWiseConsumption();
                    objItemWiseConsumption.setItemCode(objMod[0].toString());
                    objItemWiseConsumption.setItemName(objMod[1].toString());
                    objItemWiseConsumption.setSubGroupName(objMod[7].toString());
                    objItemWiseConsumption.setGroupName(objMod[8].toString());
                    objItemWiseConsumption.setSaleQty(Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setComplimentaryQty(0);
                    objItemWiseConsumption.setNcQty(0);
                    objItemWiseConsumption.setSubTotal(Double.parseDouble(objMod[3].toString()));
                    objItemWiseConsumption.setDiscAmt(Double.parseDouble(objMod[6].toString()));
                    objItemWiseConsumption.setSaleAmt(Double.parseDouble(objMod[3].toString()) - Double.parseDouble(objMod[6].toString()));
                    objItemWiseConsumption.setPOSName(objMod[5].toString());
                    objItemWiseConsumption.setPromoQty(0);
                    objItemWiseConsumption.setSeqNo(sqlNo);
                    objItemWiseConsumption.setCostCenterCode(costCenterCd);
                    objItemWiseConsumption.setCostCenterName(costCenterNm);
                    objItemWiseConsumption.setExternalCode(objMod[10].toString());
                    double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
                    //objItemWiseConsumption.setTotalQty(totalRowQty);
                    objItemWiseConsumption.setTotalQty(0);

                }
                if (null != objItemWiseConsumption)
                {
                    hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(), objItemWiseConsumption);
                }

            }
        	}
          
        }
		}
       

        // for Sales Qty for bill detail q data 
        sbSql.setLength(0);

        sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), SUM(b.dblamount),b.dblRate, e.strposname,SUM(b.dblDiscountAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSql.append(" group by b.strItemCode,a.strBillNo order by j.strCostCenterCode,b.strItemName");

        List listSqlQBill = objBaseService.funGetList(sbSql,"sql");
        if(listSqlQBill.size()>0)
        {
        	for(int i=0;i<listSqlQBill.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlQBill.get(i);
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString())));
                objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obj[3].toString()));
                
            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setSaleQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setComplimentaryQty(0);
                objItemWiseConsumption.setNcQty(0);
                objItemWiseConsumption.setSubTotal(Double.parseDouble(obj[3].toString()));
                objItemWiseConsumption.setDiscAmt(Double.parseDouble(obj[6].toString()));
                objItemWiseConsumption.setSaleAmt(Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString()));
                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setPromoQty(0);
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[11].toString());
                objItemWiseConsumption.setCostCenterName(obj[12].toString());
                costCenterCd = obj[11].toString();
                costCenterNm = obj[12].toString();
                objItemWiseConsumption.setExternalCode(obj[10].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);

            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }
            sbSqlMod.setLength(0);
            if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
            {
                // Code for Sales Qty for modifier live & q data

                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
                        + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType!='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + " group by b.strItemCode,b.strModifierName ");
            }
            else
            {
                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
                        + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType!='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' And  b.dblamount >0"
                        + " group by b.strItemCode,b.strModifierName ");
            }
            sbSqlMod.append(sbFilters);

            List listQBillMod = objBaseService.funGetList(sbSqlMod,"sql");
            if(listQBillMod.size()>0)
            {
            	for(int j=0;j<listQBillMod.size();j++)
            	{
            	Object[] objMod = (Object[]) listQBillMod.get(j);	
                if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString()))
                {
                    objItemWiseConsumption = hmItemWiseConsumption.get(objMod[0].toString() + "!" +objMod[1].toString());
                    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(objMod[3].toString()) - Double.parseDouble(objMod[6].toString())));
                    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(objMod[3].toString()));
                    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSalesMod.getDouble(3));
                }
                else
                {
                    sqlNo++;
                    objItemWiseConsumption = new clsItemWiseConsumption();
                    objItemWiseConsumption.setItemCode(objMod[0].toString());
                    objItemWiseConsumption.setItemName(objMod[1].toString());
                    objItemWiseConsumption.setSubGroupName(objMod[7].toString());
                    objItemWiseConsumption.setGroupName(objMod[8].toString());
                    objItemWiseConsumption.setSaleQty(Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setComplimentaryQty(0);
                    objItemWiseConsumption.setNcQty(0);
                    objItemWiseConsumption.setSubTotal(Double.parseDouble(objMod[3].toString()));
                    objItemWiseConsumption.setDiscAmt(Double.parseDouble(objMod[6].toString()));
                    objItemWiseConsumption.setSaleAmt(Double.parseDouble(objMod[3].toString()) - Double.parseDouble(objMod[6].toString()));
                    objItemWiseConsumption.setPOSName(objMod[5].toString());
                    objItemWiseConsumption.setPromoQty(0);
                    objItemWiseConsumption.setSeqNo(sqlNo);
                    objItemWiseConsumption.setCostCenterCode(costCenterCd);
                    objItemWiseConsumption.setCostCenterName(costCenterNm);
                    objItemWiseConsumption.setExternalCode(objMod[10].toString());
                    double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
                    //objItemWiseConsumption.setTotalQty(totalRowQty);
                    objItemWiseConsumption.setTotalQty(0);
                }
                if (null != objItemWiseConsumption)
                {
                    hmItemWiseConsumption.put(objMod[0].toString()+ "!" + objMod[1].toString(), objItemWiseConsumption);
                }
            }
            

        }
        	}
		}
        

        // Code for Complimentary Qty for live & q bill detail and bill modifier data   
        //for Complimentary Qty for live bill detail
        sbSql.setLength(0);

        sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), SUM(b.dblamount),b.dblRate,e.strposname,SUM(b.dblDiscountAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,b.strItemName");
        //System.out.println(sbSql);

        List listSqlLiveBillComplimentary = objBaseService.funGetList(sbSql,"sql");
        if(listSqlLiveBillComplimentary.size()>0)
        {
        	for(int i=0;i<listSqlLiveBillComplimentary.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlLiveBillComplimentary.get(i);
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble( obj[2].toString()));

                objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));

            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleQty(0);
                objItemWiseConsumption.setNcQty(0);

                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setPromoQty(0);
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[11].toString());
                objItemWiseConsumption.setCostCenterName(obj[12].toString());
                costCenterCd = obj[11].toString();
                costCenterNm = obj[12].toString();
                objItemWiseConsumption.setExternalCode(obj[10].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);
                ///System.out.println("New= " + rsComplimentary.getString(1) + objItemWiseConsumption.getComplimentaryQty());
            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }

            sbSqlMod.setLength(0);
            if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
            {
                //for Complimentary Qty for live bill modifier

                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                        + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo  "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + " group by b.strItemCode,b.strModifierName ");
            }
            else
            {
                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                        + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' "
                        + " AND '" + toDate + "' AND  b.dblamount >0"
                        + " group by b.strItemCode,b.strModifierName ");
            }
            sbSqlMod.append(sbFilters);
            //System.out.println(sbSqlMod);

            List listSqlQbillModComplimentary = objBaseService.funGetList(sbSqlMod,"sql");
            if(listSqlQbillModComplimentary.size()>0)
            {
            	for(int j=0;j<listSqlQbillModComplimentary.size();j++)
            	{
            	Object[] objMod = (Object[]) listSqlQbillModComplimentary.get(j);
                if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString()))
                {
                    objItemWiseConsumption = hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString());
                    objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble(objMod[2].toString()));

                }
                else
                {
                    sqlNo++;
                    objItemWiseConsumption = new clsItemWiseConsumption();
                    objItemWiseConsumption.setItemCode(objMod[0].toString());
                    objItemWiseConsumption.setItemName(objMod[1].toString());
                    objItemWiseConsumption.setSubGroupName(objMod[7].toString());
                    objItemWiseConsumption.setGroupName(objMod[8].toString());
                    objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setSaleQty(0);
                    objItemWiseConsumption.setNcQty(0);

                    objItemWiseConsumption.setPOSName(objMod[5].toString());
                    objItemWiseConsumption.setSeqNo(sqlNo);
                    objItemWiseConsumption.setPromoQty(0);
                    objItemWiseConsumption.setCostCenterCode(costCenterCd);
                    objItemWiseConsumption.setCostCenterName(costCenterNm);
                    objItemWiseConsumption.setExternalCode(objMod[10].toString());
                    //System.out.println("New= " + rsModComplimentary.getString(1) + objItemWiseConsumption.getComplimentaryQty());
                    double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
                    //objItemWiseConsumption.setTotalQty(totalRowQty);
                    objItemWiseConsumption.setTotalQty(0);
                }
                if (null != objItemWiseConsumption)
                {
                    hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(), objItemWiseConsumption);
                }
            }
        	}
          
        }
		}
       

        //for Complimentary Qty for q bill details
        sbSql.setLength(0);

        sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), SUM(b.dblamount),b.dblRate,e.strposname,SUM(b.dblDiscountAmt)"
                + ",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND a.strPOSCode=e.strPosCode  "
                + "AND b.strItemCode=f.strItemCode  "
                + "AND f.strSubGroupCode=g.strSubGroupCode  "
                + "AND g.strGroupCode=h.strGroupCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,b.strItemName");
        //System.out.println(sbSql);

        List listQBillComplimentary = objBaseService.funGetList(sbSql,"sql");
        if(listQBillComplimentary.size()>0)
        {
        	for(int i=0;i<listQBillComplimentary.size();i++)
        	{
        		Object[] obj = (Object[]) listQBillComplimentary.get(i);
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble(obj[2].toString()));

                objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));

            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleQty(0);
                objItemWiseConsumption.setNcQty(0);

                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setPromoQty(0);
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[11].toString());
                objItemWiseConsumption.setCostCenterName(obj[12].toString());
                costCenterCd = obj[11].toString();
                costCenterNm = obj[12].toString();
                objItemWiseConsumption.setExternalCode(obj[10].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);
            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }

            sbSqlMod.setLength(0);
            if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
            {
                //for Complimentary Qty for q bill modifier 

                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
                        + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + " group by b.strItemCode,b.strModifierName ");
            }
            else
            {
                sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
                        + " ,e.strposname,SUM(b.dblDiscAmt),g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
                        + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
                        + " where a.strBillNo=b.strBillNo "
                        + " and date(a.dteBillDate)=date(b.dteBillDate) "
                        + " and a.strBillNo=c.strBillNo "
                        + " and date(a.dteBillDate)=date(c.dteBillDate) "
                        + " and c.strSettlementCode=d.strSettelmentCode "
                        + " and a.strPOSCode=e.strPosCode "
                        + " and left(b.strItemCode,7)=f.strItemCode "
                        + " and f.strSubGroupCode=g.strSubGroupCode "
                        + " and g.strGroupCode=h.strGroupCode "
                        + " and d.strSettelmentType='Complementary' "
                        + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
                        + " and a.strBillNo='" + obj[9].toString() + "' "
                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + " AND  b.dblamount >0"
                        + " group by b.strItemCode,b.strModifierName ");
            }
            sbSqlMod.append(sbFilters);
            //System.out.println(sbSqlMod);

            List listQBillModComplimentary = objBaseService.funGetList(sbSqlMod,"sql");
            if(listQBillModComplimentary.size()>0)
            {
            	for(int j=0;j<listQBillModComplimentary.size();j++)
            	{
            	Object[] objMod = (Object[]) listQBillModComplimentary.get(j); 
                if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString()))
                {
                    objItemWiseConsumption = hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString());
                    objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble(objMod[2].toString()));

                }
                else
                {
                    sqlNo++;
                    objItemWiseConsumption = new clsItemWiseConsumption();
                    objItemWiseConsumption.setItemCode(objMod[0].toString());
                    objItemWiseConsumption.setItemName(objMod[0].toString());
                    objItemWiseConsumption.setSubGroupName(objMod[7].toString());
                    objItemWiseConsumption.setGroupName(objMod[8].toString());
                    objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(objMod[2].toString()));
                    objItemWiseConsumption.setSaleQty(0);
                    objItemWiseConsumption.setNcQty(0);

                    objItemWiseConsumption.setPOSName(objMod[5].toString());
                    objItemWiseConsumption.setPromoQty(0);
                    objItemWiseConsumption.setSeqNo(sqlNo);
                    objItemWiseConsumption.setCostCenterCode(costCenterCd);
                    objItemWiseConsumption.setCostCenterName(costCenterNm);
                    objItemWiseConsumption.setExternalCode(objMod[10].toString());
                    double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
                    //objItemWiseConsumption.setTotalQty(totalRowQty);
                    objItemWiseConsumption.setTotalQty(0);
                }
                if (null != objItemWiseConsumption)
                {
                    hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(), objItemWiseConsumption);
                }
            }
        	}
          
        }
		}
       

        // Code for NC Qty    
        sbSql.setLength(0);

        sbSql.append("SELECT a.stritemcode,upper(b.stritemname), SUM(a.dblQuantity), SUM(a.dblQuantity*a.dblRate),a.dblRate, c.strposname,0 AS DiscAmt,d.strSubGroupName,e.strGroupName,b.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblnonchargablekot a, tblitemmaster b, tblposmaster c,tblsubgrouphd d,tblgrouphd e,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE LEFT(a.strItemCode,7)=b.strItemCode  "
                + "AND a.strPOSCode=c.strPosCode  "
                + "AND b.strSubGroupCode=d.strSubGroupCode  "
                + "AND d.strGroupCode=e.strGroupCode  "
                + "and a.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteNCKOTDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }

        sbSql.append(" group by a.strItemCode order by j.strCostCenterCode,b.strItemName");
        //System.out.println(sbSql);

        List listNCKOT = objBaseService.funGetList(sbSql,"sql");
        if(listNCKOT.size()>0)
        {
        	for(int i=0;i<listNCKOT.size();i++)
        	{
        	Object[] obj = (Object[]) listNCKOT.get(i);	
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                objItemWiseConsumption.setNcQty(objItemWiseConsumption.getNcQty() + Double.parseDouble(obj[2].toString()));

            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setNcQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleQty(0);
                objItemWiseConsumption.setComplimentaryQty(0);

                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setPromoQty(0);
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[10].toString());
                objItemWiseConsumption.setCostCenterName(obj[11].toString());
                costCenterCd = obj[10].toString();
                costCenterNm = obj[11].toString();
                objItemWiseConsumption.setExternalCode(obj[9].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);
            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }
        }
		}
      

        // Code for promotion Qty for Q
        sbSql.setLength(0);
        sbSql.append("SELECT b.strItemCode,upper(c.strItemName), SUM(b.dblQuantity), SUM(b.dblAmount),b.dblRate,f.strPosName,0,d.strSubGroupName,e.strGroupName,c.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblqbillhd a,tblqbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND b.strItemCode=c.strItemCode  "
                + "AND c.strSubGroupCode=d.strSubGroupCode  "
                + "AND d.strGroupCode=e.strGroupCode  "
                + "AND a.strPOSCode=f.strPosCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSql.append(" group by b.strItemCode,a.strBillNo  order by j.strCostCenterCode,c.strItemName");
        //System.out.println(sbSql);

        List listPromotionQQty = objBaseService.funGetList(sbSql,"sql");
        if(listPromotionQQty.size()>0)
        {
        	for(int i=0;i<listPromotionQQty.size();i++)
        	{
        	Object[] obj = (Object[]) listPromotionQQty.get(i);	
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                double saleQty = objItemWiseConsumption.getSaleQty();
                if (saleQty > 0)
                {
                    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));
                    objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() - Double.parseDouble(obj[2].toString()));
                }

                objItemWiseConsumption.setPromoQty(objItemWiseConsumption.getPromoQty() + Double.parseDouble(obj[2].toString()));
                double qty = objItemWiseConsumption.getTotalQty();
                //objItemWiseConsumption.setTotalQty(qty + objItemWiseConsumption.getPromoQty());
            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setNcQty(0);
                objItemWiseConsumption.setPromoQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleQty(0);
                objItemWiseConsumption.setComplimentaryQty(0);

                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[10].toString());
                objItemWiseConsumption.setCostCenterName(obj[11].toString());
                objItemWiseConsumption.setExternalCode(obj[9].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);
            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }
        }
		}
       
        // Code for promotion Qty for live
        sbSql.setLength(0);
        sbSql.append("SELECT b.strItemCode,upper(c.strItemName), SUM(b.dblQuantity), SUM(b.dblAmount),b.dblRate,f.strPosName,0,d.strSubGroupName,e.strGroupName,c.strExternalCode "
                + ",i.strCostCenterCode,j.strCostCenterName "
                + "FROM tblbillhd a,tblbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f,tblmenuitempricingdtl i,tblcostcentermaster j "
                + "WHERE a.strBillNo=b.strBillNo  "
                + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
                + "AND b.strItemCode=c.strItemCode  "
                + "AND c.strSubGroupCode=d.strSubGroupCode  "
                + "AND d.strGroupCode=e.strGroupCode  "
                + "AND a.strPOSCode=f.strPosCode  "
                + "and b.strItemCode=i.strItemCode "
                + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
                + "and i.strCostCenterCode=j.strCostCenterCode "
                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
        }

        if (!groupCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
        }

        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
        }
        sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,c.strItemName");
        //System.out.println(sbSql);

        List listQBillPromotion = objBaseService.funGetList(sbSql,"sql");
        if(listQBillPromotion.size()>0)
        {
        	for(int i=0;i<listQBillPromotion.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillPromotion.get(i);	
            clsItemWiseConsumption objItemWiseConsumption = null;
            if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString()))
            {
                objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
                double saleQty = objItemWiseConsumption.getSaleQty();
                if (saleQty > 0)
                {
                    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));
                    objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() - Double.parseDouble(obj[2].toString()));
                }

                objItemWiseConsumption.setPromoQty(objItemWiseConsumption.getPromoQty() + Double.parseDouble(obj[2].toString()));
                double qty = objItemWiseConsumption.getTotalQty();
                //objItemWiseConsumption.setTotalQty(qty + objItemWiseConsumption.getPromoQty());
            }
            else
            {
                sqlNo++;
                objItemWiseConsumption = new clsItemWiseConsumption();
                objItemWiseConsumption.setItemCode(obj[0].toString());
                objItemWiseConsumption.setItemName(obj[1].toString());
                objItemWiseConsumption.setSubGroupName(obj[7].toString());
                objItemWiseConsumption.setGroupName(obj[8].toString());
                objItemWiseConsumption.setNcQty(0);
                objItemWiseConsumption.setPromoQty(Double.parseDouble(obj[2].toString()));
                objItemWiseConsumption.setSaleQty(0);
                objItemWiseConsumption.setComplimentaryQty(0);

                objItemWiseConsumption.setPOSName(obj[5].toString());
                objItemWiseConsumption.setSeqNo(sqlNo);
                objItemWiseConsumption.setCostCenterCode(obj[10].toString());
                objItemWiseConsumption.setCostCenterName(obj[11].toString());
                objItemWiseConsumption.setExternalCode(obj[9].toString());
                double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
                //objItemWiseConsumption.setTotalQty(totalRowQty);
                objItemWiseConsumption.setTotalQty(0);
            }
            if (null != objItemWiseConsumption)
            {
                hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
            }
        }
		}
        

        
        for (Map.Entry<String, clsItemWiseConsumption> entry : hmItemWiseConsumption.entrySet())
        {
            clsItemWiseConsumption objItemComp = entry.getValue();
            double totalRowQty = objItemComp.getSaleQty() + objItemComp.getComplimentaryQty() + objItemComp.getNcQty() + objItemComp.getPromoQty();
            objItemComp.setTotalQty(totalRowQty);
            list.add(objItemComp);
        }

        //sort list 
        // Collections.sort(list, clsItemWiseConsumption.comparatorItemConsumptionColumnDtl);
        Comparator<clsItemWiseConsumption> posNameComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                return o1.getPOSName().compareToIgnoreCase(o2.getPOSName());
            }
        };
        Comparator<clsItemWiseConsumption> costCenterNameComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                return o1.getCostCenterName().compareToIgnoreCase(o2.getCostCenterName());
            }
        };

        Comparator<clsItemWiseConsumption> groupNameComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
            }
        };

        Comparator<clsItemWiseConsumption> subGroupNameComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                return o1.getSubGroupName().compareToIgnoreCase(o2.getSubGroupName());
            }
        };

        Comparator<clsItemWiseConsumption> itemCodeComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                return o1.getItemName().compareToIgnoreCase(o2.getItemName());
            }
        };

        Comparator<clsItemWiseConsumption> seqNoComparator = new Comparator<clsItemWiseConsumption>()
        {

            @Override
            public int compare(clsItemWiseConsumption o1, clsItemWiseConsumption o2)
            {
                int seqNo1 = o1.getSeqNo();
                int seqNo2 = o2.getSeqNo();

                if (seqNo1 == seqNo2)
                {
                    return 0;
                }
                else if (seqNo1 > seqNo2)
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        };

        Collections.sort(list, new clsItemConsumptionComparator(posNameComparator, costCenterNameComparator, groupNameComparator, subGroupNameComparator, itemCodeComparator
        ));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return list;
	}
	
	public List funProcessQFileSettlementWiseReport(String posCode,String fromDate,String toDate)
	{
		
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sqlFilter = new StringBuilder();
        DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
        DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
        List listSqlQFileData = new ArrayList();
        try
        {
        
        sbSqlQFile.setLength(0);	
        sbSqlQFile.append("select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
                + ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
                + "from tblsettelmenthd a "
                + "left outer join tblqbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                + "left outer join tblqbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");

        sqlFilter.append(" where a.strSettelmentType!='Complementary' "
                + "and a.strApplicable='Yes' ");

        if (!"All".equalsIgnoreCase(posCode))
        {
            sqlFilter.append("and  c.strPosCode='" + posCode + "' ");
        }

        sqlFilter.append("group by a.strSettelmentCode "
                + "order by b.dblSettlementAmt desc ");

        sbSqlQFile.append(sqlFilter);
       
       
        listSqlQFileData = objBaseService.funGetList(sbSqlQFile,"sql");

        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listSqlQFileData;
	}
	
	public List funProcessLiveSettlementWiseReport(String posCode,String fromDate,String toDate)
	{
		StringBuilder sbSqlLive = new StringBuilder();
       
        StringBuilder sqlFilter = new StringBuilder();
        DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
        DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
        List listSqlLiveData = new ArrayList();
        try
        {
        sbSqlLive.setLength(0);	
        sbSqlLive.append("select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
                + ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
                + "from tblsettelmenthd a "
                + "left outer join tblbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                + "left outer join tblbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");
        
        sqlFilter.append(" where a.strSettelmentType!='Complementary' "
                + "and a.strApplicable='Yes' ");

        if (!"All".equalsIgnoreCase(posCode))
        {
            sqlFilter.append("and  c.strPosCode='" + posCode + "' ");
        }

        sqlFilter.append("group by a.strSettelmentCode "
                + "order by b.dblSettlementAmt desc ");

        sbSqlLive.append(sqlFilter);
       
        listSqlLiveData = objBaseService.funGetList(sbSqlLive,"sql");
       
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listSqlLiveData;
	}
	
	
	public List<clsGroupSubGroupItemBean> funProcessSubGroupWiseReport(String posCode,String fromDate,String toDate,String strUserCode,String strShiftNo)
	{
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        StringBuilder sqlModLive = new StringBuilder();
        StringBuilder sqlModQFile = new StringBuilder();
        
        List<clsGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsGroupSubGroupItemBean>();
        sbSqlLive.setLength(0);
        sbSqlQFile.setLength(0);
        sbSqlFilters.setLength(0);
        sqlModLive.setLength(0);
        sqlModQFile.setLength(0);

        try
        {
        sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
                + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
                + "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d "
                + ",tblposmaster f "
                + "where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strPOSCode=f.strPOSCode  "
                + " and a.strClientCode=b.strClientCode   "
                + "and b.strItemCode=d.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode ");

        sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
                + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
                + "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d "
                + ",tblposmaster f "
                + "where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strPOSCode=f.strPOSCode "
                + " and a.strClientCode=b.strClientCode   "
                + "and b.strItemCode=d.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode ");

        sqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
                + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
                + ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
                + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
                + ",tblsubgrouphd c"
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strPOSCode=f.strPosCode  "
                + " and a.strClientCode=b.strClientCode  "
                + " and LEFT(b.strItemCode,7)=d.strItemCode "
                + " and d.strSubGroupCode=c.strSubGroupCode "
                + " and b.dblamount>0 ");

        sqlModQFile.append("select c.strSubGroupCode,c.strSubGroupName"
                + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
                + ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
                + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
                + ",tblsubgrouphd c"
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strPOSCode=f.strPosCode "
                + " and a.strClientCode=b.strClientCode  "
                + " and LEFT(b.strItemCode,7)=d.strItemCode "
                + " and d.strSubGroupCode=c.strSubGroupCode "
                + " and b.dblamount>0 ");

        sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
        }
        sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        sbSqlFilters.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

        sbSqlLive.append(sbSqlFilters);
        sbSqlQFile.append(sbSqlFilters);
        sqlModLive.append(sbSqlFilters);
        sqlModQFile.append(sbSqlFilters);

        List listSqlLiveData = objBaseService.funGetList(sbSqlLive,"sql");
        if(listSqlLiveData.size()>0)
        {
        	for(int i=0;i<listSqlLiveData.size();i++)
        	{
        	Object[] obj = (Object[]) listSqlLiveData.get(i);	
            clsGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
            objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString());   //SubGroup Code
            objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());  //SubGroup Name
            objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString()));   //Qty
            objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));   //sub total
            objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString()));     //amt-disAmt
            objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString()));     //dis amt
            objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString());    //POS Name

            listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
        }
		}
      

        List listLiveModData = objBaseService.funGetList(sqlModLive,"sql");
        if(listLiveModData.size()>0)
        {
            for(int i=0;i<listLiveModData.size();i++)
            {
            Object[] obj = (Object[]) listLiveModData.get(i);	
        	clsGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
            objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString());   //SubGroup Code
            objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());  //SubGroup Name
            objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString()));   //Qty
            objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));   //sub total
            objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString()));     //amt-disAmt
            objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString()));     //dis amt
            objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString());    //POS Name

            listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
            }
        }
      

        List listQfileData = objBaseService.funGetList(sbSqlQFile,"sql");
        if(listQfileData.size()>0)
        {
            for(int i=0;i<listQfileData.size();i++)
            {
            Object[] obj = (Object[]) listQfileData.get(i);
        	clsGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
            objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString());   //SubGroup Code
            objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());  //SubGroup Name
            objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString()));   //Qty
            objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));   //sub total
            objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString()));     //amt-disAmt
            objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString()));     //dis amt
            objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString());    //POS Name

            listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
            }
        }
        

        List listQfileModData = objBaseService.funGetList(sqlModQFile,"sql");
        if(listQfileModData.size()>0)
        {
        	for(int i=0;i<listQfileModData.size();i++)
        	{
            Object[] obj = (Object[]) listQfileModData.get(i);
        	clsGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
            objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString());    //SubGroup Code
            objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());//SubGroup Name
            objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString()));   //Qty
            objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));   //sub total
            objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString()));     //amt-disAmt
            objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString()));     //dis amt
            objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString());    //POS Name

            listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
        }
        }
        
        Comparator<clsGroupSubGroupItemBean> subGroupNameComparator = new Comparator<clsGroupSubGroupItemBean>()
        {

            @Override
            public int compare(clsGroupSubGroupItemBean o1, clsGroupSubGroupItemBean o2)
            {
                return o1.getStrSubGroupName().compareToIgnoreCase(o2.getStrSubGroupName());
            }
        };

        Collections.sort(listOfGroupSubGroupWiseSales, subGroupNameComparator);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfGroupSubGroupWiseSales;
	}
	
	public List<clsTaxCalculationDtls> funProcessTaxWiseReport(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sqlQBuilder = new StringBuilder();
        List<clsTaxCalculationDtls> listOfTaxData = new ArrayList<clsTaxCalculationDtls>();
        try
        {
        //live
        sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate, b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname\n"
                + "FROM tblBillHd a\n"
                + "INNER JOIN tblBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
                + "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode\n"
                + "LEFT OUTER\n"
                + "JOIN tblposmaster d ON a.strposcode=d.strposcode\n"
                + "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and  '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
        
        List lisSqlLive= objBaseService.funGetList(sqlBuilder,"sql");
        
        if(lisSqlLive.size()>0)
        {
        	for(int i=0;i<lisSqlLive.size();i++)
        	{
            Object[] obj = (Object[]) lisSqlLive.get(i);
        	clsTaxCalculationDtls objBeanTaxCalculation = new clsTaxCalculationDtls();
        	objBeanTaxCalculation.setStrBillNo(obj[0].toString());
        	objBeanTaxCalculation.setDteBillDate(obj[1].toString());
        	objBeanTaxCalculation.setTaxCode(obj[2].toString());
        	objBeanTaxCalculation.setStrTaxDesc(obj[3].toString());
        	objBeanTaxCalculation.setStrPOSCode(obj[4].toString());
        	objBeanTaxCalculation.setTaxableAmount(Double.parseDouble(obj[5].toString()));
        	objBeanTaxCalculation.setTaxAmount(Double.parseDouble(obj[6].toString()));
        	objBeanTaxCalculation.setDblGrandTotal(Double.parseDouble(obj[7].toString()));
        	objBeanTaxCalculation.setStrPOSName(obj[8].toString());

            listOfTaxData.add(objBeanTaxCalculation);
        	}
        }

        sqlQBuilder.setLength(0);
        sqlQBuilder.append("SELECT a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate, b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname\n"
                + "FROM tblqBillHd a\n"
                + "INNER JOIN tblqBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
                + "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode\n"
                + "LEFT OUTER\n"
                + "JOIN tblposmaster d ON a.strposcode=d.strposcode\n"
                + "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and  '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
        
        List listQBillData = objBaseService.funGetList(sqlQBuilder,"sql");
        if(listQBillData.size()>0)
        {
        	for(int i=0;i<listQBillData.size();i++)
        	{	
            Object[] obj = (Object[]) listQBillData.get(i);
        	clsTaxCalculationDtls objBeanTaxCalculation = new clsTaxCalculationDtls();
            objBeanTaxCalculation.setStrBillNo(obj[0].toString());
            objBeanTaxCalculation.setDteBillDate(obj[1].toString());
            objBeanTaxCalculation.setTaxCode(obj[2].toString());
            objBeanTaxCalculation.setStrTaxDesc(obj[3].toString());
            objBeanTaxCalculation.setStrPOSCode(obj[4].toString());
            objBeanTaxCalculation.setTaxableAmount(Double.parseDouble(obj[5].toString()));
            objBeanTaxCalculation.setTaxAmount(Double.parseDouble(obj[6].toString()));
            objBeanTaxCalculation.setDblGrandTotal(Double.parseDouble(obj[7].toString()));
            objBeanTaxCalculation.setStrPOSName(obj[8].toString());

            listOfTaxData.add(objBeanTaxCalculation);
        	}
        }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfTaxData;
	}
	
	public List<clsVoidBillDtl> funProcessVoidBillSummaryReport(String posCode,String fromDate,String toDate,String strShiftNo,String reasonCode)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsVoidBillDtl> listOfVoidBillData = new ArrayList<clsVoidBillDtl>();
        try
		{
		//Bill detail data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate, "
                + " Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime, "
                + " a.dblModifiedAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,a.strRemark,a.strVoidBillType "
                + " from tblvoidbillhd a,tblvoidbilldtl b "
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and b.strTransType='VB' "
                + " and a.strTransType='VB' "
                + " and (a.dblModifiedAmount)>0 "
                + " and Date(a.dteModifyVoidBill)  Between '" + fromDate + "' and '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
        }
        if (!reasonCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
        sqlBuilder.append(" group by a.strBillNo ");

        List listLiveVoidBillData = objBaseService.funGetList(sqlBuilder,"sql");
       
        if(listLiveVoidBillData.size()>0)
        {
        	for(int i=0;i<listLiveVoidBillData.size();i++)
        	{
        	Object[] obj = (Object[]) listLiveVoidBillData.get(i);	
            String billDate = obj[1].toString();
            String dateParts[] = billDate.split("-");
            String dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

            String voidedBillDate = obj[1].toString();
            String dateParts1[] = voidedBillDate.split("-");
            String dteVoidedBillDate = dateParts1[2] + "-" + dateParts1[1] + "-" + dateParts1[0];

            clsVoidBillDtl objVoidBill = new clsVoidBillDtl();
            objVoidBill.setStrBillNo(obj[0].toString());          //BillNo
            objVoidBill.setDteBillDate(dteBillDate);        //Bill Date
            objVoidBill.setStrWaiterNo(dteVoidedBillDate);        //Voided Date
            objVoidBill.setStrTableNo(obj[3].toString());         //Entry Time
            objVoidBill.setStrSettlementCode(obj[4].toString());  //Voided Time
            objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString()));          //Bill Amount
            objVoidBill.setStrReasonName(obj[6].toString());      //Reason
            objVoidBill.setStrVoidedUser(obj[7].toString());      //User voided
            objVoidBill.setStrUserCreated(obj[8].toString());     //User Created
            objVoidBill.setStrRemarks(obj[9].toString());         //Remarks   
            objVoidBill.setStrVoidBillType(obj[10].toString());         //Void Bill Type

            listOfVoidBillData.add(objVoidBill);
        }
    	}
        
        //Bill Modifier data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate "
                + " ,Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime "
                + " ,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
                + " from tblvoidbillhd a, tblvoidmodifierdtl b "
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strTransType='VB' "
                + " and (a.dblModifiedAmount)>0 "
                + " and Date(a.dteModifyVoidBill) Between '" + fromDate + "' and '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append(" and a.strPosCode='" + posCode + "' ");
        }
        if (!reasonCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
        }
        sqlBuilder.append(" and a.intShiftCode='" + strShiftNo + "'  ");
        sqlBuilder.append(" group by a.strBillNo  ");

        List listQBillVoidData = objBaseService.funGetList(sqlBuilder,"sql");
        if(listQBillVoidData.size()>0)
        {
        	for(int i=0;i<listQBillVoidData.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillVoidData.get(i);	
            String billDate = obj[1].toString();
            String dateParts[] = billDate.split("-");
            String dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

            String voidedBillDate = obj[1].toString();
            String dateParts1[] = voidedBillDate.split("-");
            String dteVoidedBillDate = dateParts1[2] + "-" + dateParts1[1] + "-" + dateParts1[0];

            clsVoidBillDtl objVoidBill = new clsVoidBillDtl();
            objVoidBill.setStrBillNo(obj[0].toString());          //BillNo
            objVoidBill.setDteBillDate(dteBillDate);        //Bill Date
            objVoidBill.setStrWaiterNo(dteVoidedBillDate);        //Voided Date
            objVoidBill.setStrTableNo(obj[3].toString());         //Entry Time
            objVoidBill.setStrSettlementCode(obj[4].toString());  //Voided Time
            objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString()));          //Bill Amount
            objVoidBill.setStrReasonName(obj[6].toString());      //Reason
            objVoidBill.setStrVoidedUser(obj[7].toString());      //User Edited
            objVoidBill.setStrUserCreated(obj[8].toString());     //User Created
            objVoidBill.setStrRemarks(obj[9].toString());         //Remarks   
            objVoidBill.setStrVoidBillType(obj[10].toString());         //Void Bill Type

            listOfVoidBillData.add(objVoidBill);
        	}
        }
        
        Comparator<clsVoidBillDtl> reasonNameComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getStrReasonName().compareToIgnoreCase(o2.getStrReasonName());
            }
        };

        Comparator<clsVoidBillDtl> billDateComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
            }
        };

        Comparator<clsVoidBillDtl> billNoComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
            }
        };

        Collections.sort(listOfVoidBillData, new clsVoidBillComparator(
                reasonNameComparator,
                billDateComparator,
                billNoComparator)
        );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return listOfVoidBillData;

	}
	
	public List<clsVoidBillDtl> funProcessVoidBillDetailReport(String posCode,String fromDate,String toDate,String strShiftNo,String reasonCode)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsVoidBillDtl> listOfVoidBillData = new ArrayList<clsVoidBillDtl>();
		try
		{
        //Bill detail data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as BillDate,DATE_FORMAT(a.dteModifyVoidBill,'%d-%m-%Y') as VoidedDate, "
                + " TIME_FORMAT(a.dteBillDate, '%H:%i') AS EntryTime, TIME_FORMAT(a.dteModifyVoidBill, '%H:%i') VoidedTime,b.strItemName, "
                + " sum(b.intQuantity),sum(b.dblAmount) as BillAmount,b.strReasonName as Reason, "
                + " a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
                + " from tblvoidbillhd a,tblvoidbilldtl b"
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and b.strTransType='VB' "
                + " and a.strTransType='VB'  "
                + " and (a.dblModifiedAmount)>0 "
                + " and Date(a.dteModifyVoidBill) Between '" + fromDate + "' and '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
        }
        if (!reasonCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
        sqlBuilder.append("group by a.strBillNo,b.strItemCode ");

        List listLiveBillVoidData = objBaseService.funGetList(sqlBuilder,"sql");
        
        if(listLiveBillVoidData.size()>0)
        {
        	for(int i=0;i<listLiveBillVoidData.size();i++)
        	{
        	Object[] obj = (Object[]) listLiveBillVoidData.get(i);	
        	clsVoidBillDtl objVoidBill = new clsVoidBillDtl();
            objVoidBill.setStrBillNo(obj[0].toString());          //BillNo
            objVoidBill.setDteBillDate(obj[1].toString());        //Bill Date
            objVoidBill.setStrWaiterNo(obj[2].toString());        //Voided Date
            objVoidBill.setStrTableNo(obj[3].toString());         //Entry Time
            objVoidBill.setStrSettlementCode(obj[4].toString());  //Voided Time
            objVoidBill.setStrItemName(obj[5].toString());        //ItemName
            objVoidBill.setIntQuantity(Double.parseDouble(obj[6].toString()));        //Quantity
            objVoidBill.setDblAmount(Double.parseDouble(obj[7].toString()));          //Bill Amount
            objVoidBill.setStrReasonName(obj[8].toString());      //Reason
            objVoidBill.setStrVoidedUser(obj[9].toString());      //User Edited
            objVoidBill.setStrUserCreated(obj[10].toString());     //User Created
            objVoidBill.setStrRemarks(obj[11].toString());         //Remarks   
            objVoidBill.setStrVoidBillType(obj[12].toString());         //Void Bill Type

            listOfVoidBillData.add(objVoidBill);
        	}
        }
        
        //Bill Modifier data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as BillDate,DATE_FORMAT(a.dteModifyVoidBill,'%d-%m-%Y') as VoidedDate, "
                + " TIME_FORMAT(a.dteBillDate, '%H:%i') AS EntryTime, TIME_FORMAT(a.dteModifyVoidBill, '%H:%i') VoidedTime,b.strModifierName, "
                + " sum(b.dblQuantity),sum(b.dblAmount) as BillAmount,ifnull(c.strReasonName,'NA') as Reason, "
                + " a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
                + " from tblvoidbillhd a,tblvoidmodifierdtl b "
                + " left outer join tblreasonmaster c on b.strReasonCode=c.strReasonCode "
                + " where a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strTransType='VB' "
                + " and (a.dblModifiedAmount)>0 "
                + " and Date(a.dteModifyVoidBill) Between '" + fromDate + "' and '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append(" and a.strPosCode='" + posCode + "' ");
        }
        if (!reasonCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
        }
        sqlBuilder.append(" and a.intShiftCode='" + strShiftNo + "'  ");
        sqlBuilder.append(" group by a.strBillNo,b.strModifierCode  ");

        List listQBillVoidData = objBaseService.funGetList(sqlBuilder,"sql");
        if(listQBillVoidData.size()>0)
        {
        	for(int i=0;i<listQBillVoidData.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillVoidData.get(i);
            clsVoidBillDtl objVoidBill = new clsVoidBillDtl();
            objVoidBill.setStrBillNo(obj[0].toString());          //BillNo
            objVoidBill.setDteBillDate(obj[1].toString());        //Bill Date
            objVoidBill.setStrWaiterNo(obj[2].toString());        //Voided Date
            objVoidBill.setStrTableNo(obj[3].toString());         //Entry Time
            objVoidBill.setStrSettlementCode(obj[4].toString());  //Voided Time
            objVoidBill.setStrItemName(obj[5].toString());        //ItemName
            objVoidBill.setIntQuantity(Double.parseDouble(obj[6].toString()));        //Quantity
            objVoidBill.setDblAmount(Double.parseDouble(obj[7].toString()));          //Bill Amount
            objVoidBill.setStrReasonName(obj[8].toString());      //Reason
            objVoidBill.setStrVoidedUser(obj[9].toString());      //User Edited
            objVoidBill.setStrUserCreated(obj[10].toString());     //User Created
            objVoidBill.setStrRemarks(obj[11].toString());         //Remarks   
            objVoidBill.setStrVoidBillType(obj[12].toString());         //Void Bill Type

            listOfVoidBillData.add(objVoidBill);
        	}
        }
        
        Comparator<clsVoidBillDtl> reasonNameComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getStrReasonName().compareToIgnoreCase(o2.getStrReasonName());
            }
        };

        Comparator<clsVoidBillDtl> billDateComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
            }
        };

        Comparator<clsVoidBillDtl> billNoComparator = new Comparator<clsVoidBillDtl>()
        {

            @Override
            public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
            {
                return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
            }
        };

        Collections.sort(listOfVoidBillData, new clsVoidBillComparator(
                reasonNameComparator,
                billDateComparator,
                billNoComparator)
        );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfVoidBillData;
	}
	
	public List<clsBillDtl> funProcessWaiterWiseIncentivesSummaryReport(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sqlQBuilder = new StringBuilder();
    	sqlBuilder.setLength(0);
        List<clsBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();

        try
        {
        //Q Data
        sqlQBuilder.setLength(0);
        sqlQBuilder.append("select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount,"
                + "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,a.strBillNo,d.strIncentives "
                + "from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
                + "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and b.strItemCode=c.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode "
                + "and a.strWaiterNo=e.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
        	sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
        sqlQBuilder.append("group by e.strWaiterNo "
                + "order by e.strWFullName ");

        List listSqlLiveWaiterWiseItemSales = objBaseService.funGetList(sqlQBuilder,"sql");
        if(listSqlLiveWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listSqlLiveWaiterWiseItemSales.size();i++)
        	{	
            Object[] obj = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);
        	clsBillDtl objBillDtlBean = new clsBillDtl();

        	objBillDtlBean.setStrWaiterNo(obj[0].toString());
        	objBillDtlBean.setStrWShortName(obj[1].toString());
        	objBillDtlBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        	objBillDtlBean.setDblAmount(Double.parseDouble(obj[3].toString()));
        	objBillDtlBean.setDblIncentive(Double.parseDouble(obj[4].toString()));
        	objBillDtlBean.setStrBillNo(obj[5].toString());
        	objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[6].toString()));

            listOfWaiterWiseItemSales.add(objBillDtlBean);
        	}
        }
        
        //Live Data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount,"
                + "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,a.strBillNo,d.strIncentives "
                + "from tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
                + "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and b.strItemCode=c.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode "
                + "and a.strWaiterNo=e.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");

        sqlBuilder.append("group by e.strWaiterNo "
                + "order by e.strWFullName ");

        List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder,"sql");
        if(listQBillWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listQBillWaiterWiseItemSales.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);	
            clsBillDtl objBillDtlBean = new clsBillDtl();

            objBillDtlBean.setStrWaiterNo(obj[0].toString());
            objBillDtlBean.setStrWShortName(obj[1].toString());
            objBillDtlBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
            objBillDtlBean.setDblAmount(Double.parseDouble(obj[3].toString()));
            objBillDtlBean.setDblIncentive(Double.parseDouble(obj[4].toString()));
            objBillDtlBean.setStrBillNo(obj[5].toString());
            objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[6].toString()));

            listOfWaiterWiseItemSales.add(objBillDtlBean);
        	}
        }
        
        Comparator<clsBillDtl> waiterCodeComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrWShortName().compareTo(o2.getStrWShortName());
            }
        };

        Collections.sort(listOfWaiterWiseItemSales, new clsWaiterWiseSalesComparator(waiterCodeComparator));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfWaiterWiseItemSales;
	}
	
	public List<clsBillDtl> funProcessWaiterWiseIncentivesDetailReport(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sqlQBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
        List<clsBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();
        
        try
        {
        //Q Data
        sqlQBuilder.setLength(0);
        sqlQBuilder.append("select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,d.strSubGroupCode,d.strSubGroupName,a.strBillNo "
                + ",sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount, "
                + "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,round(d.strIncentives,2) as strIncentivePer,a.strBillNo "
                + "from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
                + "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and b.strItemCode=c.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode "
                + "and a.strWaiterNo=e.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
        	sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");

        sqlQBuilder.append("group by e.strWaiterNo,a.strBillNo "
                + "order by e.strWFullName,a.strBillNo ");

        List listLiveBillWaiterWiseItemSales = objBaseService.funGetList(sqlQBuilder,"sql");
        if(listLiveBillWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listLiveBillWaiterWiseItemSales.size();i++)
        	{
        		Object[] obj = (Object[]) listLiveBillWaiterWiseItemSales.get(i);
        	clsBillDtl objBillDtlBean = new clsBillDtl();

        	objBillDtlBean.setStrWaiterNo(obj[0].toString());
            objBillDtlBean.setStrWShortName(obj[1].toString());
            objBillDtlBean.setStrSubGroupCode(obj[2].toString());
            objBillDtlBean.setStrSubGroupName(obj[3].toString());
            objBillDtlBean.setStrBillNo(obj[4].toString());
            objBillDtlBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
            objBillDtlBean.setDblAmount(Double.parseDouble(obj[6].toString()));
            objBillDtlBean.setDblIncentive(Double.parseDouble(obj[7].toString()));
            objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[8].toString()));
            objBillDtlBean.setStrBillNo(obj[9].toString());

            listOfWaiterWiseItemSales.add(objBillDtlBean);
        	}
        }
       

        //Live Data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,d.strSubGroupCode,d.strSubGroupName,a.strBillNo "
                + ",sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount, "
                + "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,round(d.strIncentives,2) as strIncentivePer,a.strBillNo "
                + "from tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
                + "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and b.strItemCode=c.strItemCode "
                + "and c.strSubGroupCode=d.strSubGroupCode "
                + "and a.strWaiterNo=e.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");

        sqlBuilder.append("group by e.strWaiterNo,a.strBillNo "
                + "order by e.strWFullName,a.strBillNo ");

        List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder,"sql");
        if(listQBillWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listQBillWaiterWiseItemSales.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);
            clsBillDtl objBillDtlBean = new clsBillDtl();

            objBillDtlBean.setStrWaiterNo(obj[0].toString());
            objBillDtlBean.setStrWShortName(obj[1].toString());
            objBillDtlBean.setStrSubGroupCode(obj[2].toString());
            objBillDtlBean.setStrSubGroupName(obj[3].toString());
            objBillDtlBean.setStrBillNo(obj[4].toString());
            objBillDtlBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
            objBillDtlBean.setDblAmount(Double.parseDouble(obj[6].toString()));
            objBillDtlBean.setDblIncentive(Double.parseDouble(obj[7].toString()));
            objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[8].toString()));
            objBillDtlBean.setStrBillNo(obj[9].toString());

            listOfWaiterWiseItemSales.add(objBillDtlBean);
        	}
        }
        

        Comparator<clsBillDtl> waiterCodeComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrWShortName().compareTo(o2.getStrWShortName());
            }
        };

        Comparator<clsBillDtl> billNoComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrBillNo().compareTo(o2.getStrBillNo());
            }
        };

        Comparator<clsBillDtl> subGroupCodeComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrSubGroupCode().compareTo(o2.getStrSubGroupCode());
            }
        };

        Collections.sort(listOfWaiterWiseItemSales, new clsWaiterWiseSalesComparator(waiterCodeComparator, billNoComparator, subGroupCodeComparator));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfWaiterWiseItemSales;
        
	}
	
	public List<clsBillDtl> funProcessWaiterWiseItemReport(String posCode,String fromDate,String toDate,String strShiftNo,String waiterCode)
	{
		StringBuilder sqlBuilder = new StringBuilder();
        List<clsBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();

        try
        {
        //Q Data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select b.strItemCode,b.strItemName,b.dblRate,sum(b.dblQuantity),sum(b.dblAmount),c.strWaiterNo,c.strWShortName "
                + "from tblqbillhd a,tblqbilldtl b,tblwaitermaster c "
                + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and a.strWaiterNo=c.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
        if (!waiterCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and c.strWaiterNo='" + waiterCode + "' ");
        }
        sqlBuilder.append("group by c.strWaiterNo,b.strItemCode "
                + "order by c.strWFullName,b.strItemCode ");

        List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder,"sql");
        if(listQBillWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listQBillWaiterWiseItemSales.size();i++)
        	{
        	Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);	
            clsBillDtl objBeanBillDtl = new clsBillDtl();

            objBeanBillDtl.setStrItemCode(obj[0].toString());
            objBeanBillDtl.setStrItemName(obj[1].toString());
            objBeanBillDtl.setDblRate(Double.parseDouble(obj[2].toString()));
            objBeanBillDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));
            objBeanBillDtl.setDblAmount(Double.parseDouble(obj[4].toString()));
            objBeanBillDtl.setStrWaiterNo(obj[5].toString());
            objBeanBillDtl.setStrWShortName(obj[6].toString());

            listOfWaiterWiseItemSales.add(objBeanBillDtl);
        	}
        }
        
        //Live Data
        sqlBuilder.setLength(0);
        sqlBuilder.append("select b.strItemCode,b.strItemName,b.dblRate,sum(b.dblQuantity),sum(b.dblAmount),c.strWaiterNo,c.strWShortName "
                + "from tblbillhd a,tblbilldtl b,tblwaitermaster c "
                + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strBillNo=b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + "and a.strWaiterNo=c.strWaiterNo ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
        if (!waiterCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and c.strWaiterNo='" + waiterCode + "' ");
        }
        sqlBuilder.append("group by c.strWaiterNo,b.strItemCode "
                + "order by c.strWFullName,b.strItemCode ");

        List listSqlLiveWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder,"sql");
        if(listSqlLiveWaiterWiseItemSales.size()>0)
        {
        	for(int i=0;i<listSqlLiveWaiterWiseItemSales.size();i++)
        	{
        	Object[] obj = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);	
            clsBillDtl objBeanBillDtl = new clsBillDtl();

            objBeanBillDtl.setStrItemCode(obj[0].toString());
            objBeanBillDtl.setStrItemName(obj[1].toString());
            objBeanBillDtl.setDblRate(Double.parseDouble(obj[2].toString()));
            objBeanBillDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));
            objBeanBillDtl.setDblAmount(Double.parseDouble(obj[4].toString()));
            objBeanBillDtl.setStrWaiterNo(obj[5].toString());
            objBeanBillDtl.setStrWShortName(obj[6].toString());

            listOfWaiterWiseItemSales.add(objBeanBillDtl);
        	}
        }
       
        Comparator<clsBillDtl> waiterCodeComparator = new Comparator<clsBillDtl>()
        {

            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrWShortName().compareTo(o2.getStrWShortName());
            }
        };

        Comparator<clsBillDtl> itemCodeCodeComparator = new Comparator<clsBillDtl>()
        {
            @Override
            public int compare(clsBillDtl o1, clsBillDtl o2)
            {
                return o1.getStrItemCode().substring(0, 7).compareTo(o2.getStrItemCode().substring(0, 7));
            }
        };
        Collections.sort(listOfWaiterWiseItemSales, new clsWaiterWiseSalesComparator(waiterCodeComparator, itemCodeCodeComparator));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfWaiterWiseItemSales;
	}
	
	public LinkedHashMap funProcessPosWiseSalesReport(String fromDate,String toDate,String strViewType)
	{
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        StringBuilder sqlModLive = new StringBuilder();
        StringBuilder sqlModQfile = new StringBuilder();
        
        LinkedHashMap resMap = new LinkedHashMap();
	    double total = 0.0; 
        
        List list = new ArrayList();
    	List listcol=new ArrayList();
    	List totalList=new ArrayList();
		totalList.add("Total");
        
		sbSqlLive.setLength(0);
        sbSqlQFile.setLength(0);
        sbSqlFilters.setLength(0);
        sqlModLive.setLength(0);
        sqlModQfile.setLength(0);

        try
        {
        if(strViewType.equalsIgnoreCase("ITEM WISE"))
        {
        sbSqlLive.append("  select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity),sum(a.dblTaxAmount) "
                + "  ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount), "
                + " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                + " from tblbilldtl a,tblbillhd b,tblposmaster c "
                + " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
                + " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                + " group by a.strItemCode,c.strPOSName  order by b.dteBillDate   ");

        sbSqlQFile.append(" select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity),sum(a.dblTaxAmount) "
                + " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount), "
                + " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                + " from tblqbilldtl a,tblqbillhd b,tblposmaster c "
                + " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
                + " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                + " group by a.strItemCode,c.strPOSName  order by b.dteBillDate   ");

       sqlModLive.append(" select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0.0', "
                + " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount), "
                + " sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode  "
                + " from tblbillmodifierdtl a,tblbillhd b,tblposmaster c  "
                + " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  "
                + " and a.dblamount>0  "
                + " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                + " group by a.strItemCode,c.strPOSName  order by b.dteBillDate  ");

        sqlModQfile.append(" select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0', "
                + " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount), "
                + " sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                + " from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d "
                + " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
                + " and a.strItemCode=d.strItemCode and a.dblamount>0 "
                + " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                + " group by a.strItemCode,c.strPOSName  order by b.dteBillDate  ");
        List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
        if(listSqlLive.size()>0)
        {
        	for(int i=0;i<listSqlLive.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlLive.get(i);
        		List arrList=new ArrayList();
        		arrList.add(obj[0].toString());
        		arrList.add(obj[1].toString());
        		arrList.add(obj[2].toString());
        		arrList.add(obj[7].toString());
        		total+=Double.parseDouble(obj[7].toString());
        		list.add(arrList);
        	}
        }
        
        List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
        if(listSqlQFile.size()>0)
        {
        	for(int i=0;i<listSqlQFile.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlQFile.get(i);
        		List arrList=new ArrayList();
        		arrList.add(obj[0].toString());
        		arrList.add(obj[1].toString());
        		arrList.add(obj[2].toString());
        		arrList.add(obj[7].toString());
        		total+=Double.parseDouble(obj[7].toString());
        		list.add(arrList);
        	}
        }
        
        List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
        if(listSqlModLive.size()>0)
        {
        	for(int i=0;i<listSqlModLive.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlModLive.get(i);
        		List arrList=new ArrayList();
        		arrList.add(obj[0].toString());
        		arrList.add(obj[1].toString());
        		arrList.add(obj[2].toString());
        		arrList.add(obj[7].toString());
        		total+=Double.parseDouble(obj[7].toString());
        		list.add(arrList);
        	}
        }
        
        List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
        if(listSqlModQFile.size()>0)
        {
        	for(int i=0;i<listSqlModQFile.size();i++)
        	{
        		Object[] obj = (Object[]) listSqlModQFile.get(i);
        		List arrList=new ArrayList();
        		arrList.add(obj[0].toString());
        		arrList.add(obj[1].toString());
        		arrList.add(obj[2].toString());
        		arrList.add(obj[7].toString());
        		total+=Double.parseDouble(obj[7].toString());
        		list.add(arrList);
        	}
        }
        listcol.add("Item Code");
		listcol.add("Item Name");
		listcol.add("POS Name");
		listcol.add("Total");
        }
        else if(strViewType.equalsIgnoreCase("GROUP WISE"))
        {
        	sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);
            
			sbSqlLive.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
                    + " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

            sbSqlQFile.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
                    + " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
                    + " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode   ");

            sqlModLive.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,"
                    + " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
                    + " and LEFT(b.strItemCode,7)=d.strItemCode  and d.strSubGroupCode=e.strSubGroupCode "
                    + " and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

            sqlModQfile.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' , "
                    + " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");
            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
            		total+=Double.parseDouble(obj[7].toString());
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
				
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            listcol.add("Group Code");
			listcol.add("Group Name");
			listcol.add("POS Name");
			listcol.add("Total");
				
        }
        else if(strViewType.equalsIgnoreCase("SUB GROUP WISE"))
        {
        	sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);
            
            sbSqlLive.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate , "
                    + " sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
                    + " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

            sbSqlQFile.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , "
                    + " sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
                    + " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

            sqlModLive.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),"
                    + " sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode  ");

            sqlModQfile.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
                    + " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");
            
            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            listcol.add("Sub Group Code");
			listcol.add("Sub Group Name");
			listcol.add("POS Name");
			listcol.add("Total");
        }
        else if(strViewType.equalsIgnoreCase("MENU HEAD WISE"))
        {
        	sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);

            sbSqlLive.append(" SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode   "
                    + " FROM tblbilldtl a "
                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode  "
                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

            sbSqlQFile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
                    + " FROM tblqbilldtl a "
                    + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

            sqlModLive.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode "
                    + " FROM tblbillmodifierdtl a "
                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.dblAmount>0   "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

            sqlModQfile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
                    + " FROM tblqbillmodifierdtl a "
                    + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode"
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.dblAmount>0    "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            listcol.add("MENU HEAD  Code");
			listcol.add("MENU HEAD  Name");
			listcol.add("POS Name");
			listcol.add("Total");
		
        }
        totalList.add(total);
		
		resMap.put("List", list);
		resMap.put("totalList", totalList);
		resMap.put("listcol", listcol);
       
       }
       catch(Exception e)
       {
    	e.printStackTrace();
       }
       return resMap; 

	}
	
	public List funProcessPosWiseGroupWiseReport(String fromDate,String toDate)
	{
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
	    double total=0.0;
	        
		try
		{
			sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);
            
			sbSqlLive.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
                    + " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

            sbSqlQFile.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
                    + " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
                    + " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode   ");

            sqlModLive.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,"
                    + " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
                    + " and LEFT(b.strItemCode,7)=d.strItemCode  and d.strSubGroupCode=e.strSubGroupCode "
                    + " and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

            sqlModQfile.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' , "
                    + " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                    + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");
            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
            		total+=Double.parseDouble(obj[7].toString());
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
				
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funProcessDayEndSubGroupWiseReport(String fromDate,String toDate)
	{
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
	    double total=0.0;
		try
		{
			sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);
            
            sbSqlLive.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate , "
                    + " sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
                    + " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

            sbSqlQFile.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , "
                    + " sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
                    + " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
                    + " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

            sqlModLive.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),"
                    + " sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode  ");

            sqlModQfile.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
                    + " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
                    + " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");
            
            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funProcessPosWiseMenuHeadWiseReport(String fromDate,String toDate)
	{
		StringBuilder sbSqlLive = new StringBuilder();
        StringBuilder sbSqlQFile = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
	    double total=0.0;
		try
		{
			sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQfile.setLength(0);

            sbSqlLive.append(" SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode   "
                    + " FROM tblbilldtl a "
                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode  "
                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

            sbSqlQFile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
                    + " FROM tblqbilldtl a "
                    + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

            sqlModLive.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode "
                    + " FROM tblbillmodifierdtl a "
                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode  "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.dblAmount>0   "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

            sqlModQfile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
                    + " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
                    + " FROM tblqbillmodifierdtl a "
                    + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
                    + " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
                    + " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode"
                    + " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.dblAmount>0    "
                    + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
                    + " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

            List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
            if(listSqlLive.size()>0)
            {
            	for(int i=0;i<listSqlLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
            if(listSqlQFile.size()>0)
            {
            	for(int i=0;i<listSqlQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
            if(listSqlModLive.size()>0)
            {
            	for(int i=0;i<listSqlModLive.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModLive.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
            
            List listSqlModQFile = objBaseService.funGetList(sqlModQfile,"sql");
            if(listSqlModQFile.size()>0)
            {
            	for(int i=0;i<listSqlModQFile.size();i++)
            	{
            		Object[] obj = (Object[]) listSqlModQFile.get(i);
            		List arrList=new ArrayList();
					
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total+=Double.parseDouble(obj[7].toString());
					list.add(arrList);
            	}
            }
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funProcessDailyCollection(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		List<clsBillItemDtlBean> listOfBillData = new ArrayList<clsBillItemDtlBean>();
		Map mapMultiSettleBills = new HashMap();

		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
            DecimalFormat decimalFormat2Deci = new DecimalFormat("0.00");

            //live
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT a.strBillNo, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate,b.strPosName "
                    + ", IFNULL(d.strSettelmentDesc,'') AS strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt "
                    + ", SUM(c.dblSettlementAmt) AS dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
                    + ",ifnull(e.strTableName,''),a.strUserEdited,ifnull(f.strCustomerName,'') "
                    + "FROM tblbillhd a "
                    + "join tblposmaster b on a.strPOSCode=b.strPOSCode  "
                    + "join tblbillsettlementdtl c on a.strBillNo=c.strBillNo AND DATE(a.dteBillDate)= DATE(c.dteBillDate) AND a.strClientCode=c.strClientCode "
                    + "join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode  "
                    + "left outer join tbltablemaster e on a.strTableNo=e.strTableNo "
                    + "left outer join tblcustomermaster f on a.strCustomerCode=f.strCustomerCode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
            }
            sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
            sqlBuilder.append("GROUP BY a.strClientCode, DATE(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
                    + "ORDER BY d.strSettelmentCode ");

            List listSqlLiveData = objBaseService.funGetList(sqlBuilder,"sql");
           
            if(listSqlLiveData.size()>0)
            {
            	for(int i=0;i<listSqlLiveData.size();i++)
            	{
            	Object[] obj = (Object[]) listSqlLiveData.get(i);	
                String key = obj[0].toString() + "!" + obj[1].toString();

                clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
                if (mapMultiSettleBills.containsKey(key))//billNo
                {
                	objBillItemDtlBean.setStrBillNo(obj[0].toString());
                    objBillItemDtlBean.setDteBillDate(obj[1].toString());
                    objBillItemDtlBean.setStrPosName(obj[2].toString());
                    objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
                    objBillItemDtlBean.setDblDiscountAmt(0.00);
                    objBillItemDtlBean.setDblTaxAmt(0.00);
                    objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
                    objBillItemDtlBean.setDblSubTotal(0.00);
                    objBillItemDtlBean.setIntBillSeriesPaxNo(0);
                }
                else
                {
                	objBillItemDtlBean.setStrBillNo(obj[0].toString());
                	objBillItemDtlBean.setDteBillDate(obj[1].toString());
                	objBillItemDtlBean.setStrPosName(obj[2].toString());
                	objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
                	objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));
                	objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[5].toString()));
                	objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
                	objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[7].toString()));
                    objBillItemDtlBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[9].toString()));
                    objBillItemDtlBean.setStrItemCode(obj[10].toString());
                    objBillItemDtlBean.setStrDiscType(obj[11].toString());
                    objBillItemDtlBean.setStrItemName(obj[12].toString());

                    if (objBillItemDtlBean.getDblSubTotal() > 0)
                    {
                    	objBillItemDtlBean.setDblDiscountPer((objBillItemDtlBean.getDblDiscountAmt() / objBillItemDtlBean.getDblSubTotal()) * 100);
                    }
                }
                listOfBillData.add(objBillItemDtlBean);

                if (obj[8].toString().equalsIgnoreCase("MultiSettle"))
                {
                    mapMultiSettleBills.put(key, obj[0].toString());
                }
            }
            }

            //QFile
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT a.strBillNo, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate,b.strPosName "
                    + ", IFNULL(d.strSettelmentDesc,'') AS strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt "
                    + ", SUM(c.dblSettlementAmt) AS dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
                    + ",ifnull(e.strTableName,''),a.strUserEdited,ifnull(f.strCustomerName,'') "
                    + "FROM tblqbillhd a "
                    + "join tblposmaster b on a.strPOSCode=b.strPOSCode  "
                    + "join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo AND DATE(a.dteBillDate)= DATE(c.dteBillDate) AND a.strClientCode=c.strClientCode "
                    + "join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode  "
                    + "left outer join tbltablemaster e on a.strTableNo=e.strTableNo "
                    + "left outer join tblcustomermaster f on a.strCustomerCode=f.strCustomerCode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
            }
            sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
            sqlBuilder.append("GROUP BY a.strClientCode, DATE(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
                    + "ORDER BY d.strSettelmentCode ");

            List listSqlQBillData = objBaseService.funGetList(sqlBuilder,"sql");
            if(listSqlQBillData.size()>0)
            {
            	for(int i=0;i<listSqlQBillData.size();i++)
            	{
            	Object[] obj = (Object[]) listSqlQBillData.get(i);	
                String key = obj[0].toString() + "!" + obj[1].toString();

                clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
                if (mapMultiSettleBills.containsKey(key))//billNo
                {
                	objBillItemDtlBean.setStrBillNo(obj[0].toString());
                	objBillItemDtlBean.setDteBillDate(obj[1].toString());
                	objBillItemDtlBean.setStrPosName(obj[2].toString());
                	objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
                	objBillItemDtlBean.setDblDiscountAmt(0.00);
                	objBillItemDtlBean.setDblTaxAmt(0.00);
                	objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
                	objBillItemDtlBean.setDblSubTotal(0.00);
                	objBillItemDtlBean.setIntBillSeriesPaxNo(0);
                }
                else
                {
                	objBillItemDtlBean.setStrBillNo(obj[0].toString());
                	objBillItemDtlBean.setDteBillDate(obj[1].toString());
                	objBillItemDtlBean.setStrPosName(obj[2].toString());
                	objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
                	objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));
                	objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[5].toString()));
                	objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
                	objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[7].toString()));
                	objBillItemDtlBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[9].toString()));
                	objBillItemDtlBean.setStrItemCode(obj[10].toString());
                	objBillItemDtlBean.setStrDiscType(obj[11].toString());
                	objBillItemDtlBean.setStrItemName(obj[12].toString());

                    if (objBillItemDtlBean.getDblSubTotal() > 0)
                    {
                    	objBillItemDtlBean.setDblDiscountPer((objBillItemDtlBean.getDblDiscountAmt() / objBillItemDtlBean.getDblSubTotal()) * 100);
                    }
                }
                listOfBillData.add(objBillItemDtlBean);

                if (obj[8].toString().equalsIgnoreCase("MultiSettle"))
                {
                    mapMultiSettleBills.put(key, obj[0].toString());
                }
            	}
            }

            Comparator<clsBillItemDtlBean> settlementModeComparator = new Comparator<clsBillItemDtlBean>()
            {

                @Override
                public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
                {
                    return o2.getStrSettelmentMode().compareToIgnoreCase(o1.getStrSettelmentMode());
                }
            };

            Comparator<clsBillItemDtlBean> billDateComparator = new Comparator<clsBillItemDtlBean>()
            {

                @Override
                public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
                {
                    return o2.getDteBillDate().compareToIgnoreCase(o1.getDteBillDate());
                }
            };

            Comparator<clsBillItemDtlBean> billNoComparator = new Comparator<clsBillItemDtlBean>()
            {

                @Override
                public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
                {
                    return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
                }
            };

            Collections.sort(listOfBillData, new clsBillComparator(settlementModeComparator, billDateComparator, billNoComparator));

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfBillData;
	}
	
	public List funProcessDailyCollectionForVoidBillDataReport(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		List<clsVoidBillDtl> listOfVoidBillData = new ArrayList<clsVoidBillDtl>();
		StringBuilder sqlBuilder = new StringBuilder();
		try
		{
	      //Bill detail data
        sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT a.strBillNo "
                + ",DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate, DATE_FORMAT(DATE(a.dteModifyVoidBill),'%d-%m-%Y') AS VoidedDate "
                + ",TIME(a.dteBillDate) AS EntryTime, TIME(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount AS BillAmount "
                + ",a.strReasonName AS Reason,a.strUserEdited AS UserEdited,a.strUserCreated,a.strRemark "
                + " from tblvoidbillhd a,tblvoidbilldtl b "
                + " where a.strBillNo=b.strBillNo "
                + " and b.strTransType='VB' "
                + " and a.strTransType='VB' "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and Date(a.dteModifyVoidBill)  Between '" + fromDate + "' and '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
        }
        sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
        sqlBuilder.append(" group by a.dteBillDate,a.strBillNo ");

        List listVoidData = objBaseService.funGetList(sqlBuilder,"sql");
       
        if(listVoidData.size()>0)
        {
        	for(int i=0;i<listVoidData.size();i++)
        	{
        	Object[] obj = (Object[]) listVoidData.get(i);	
            clsVoidBillDtl objVoidBill = new clsVoidBillDtl();
            objVoidBill.setStrBillNo(obj[0].toString());          //BillNo
            objVoidBill.setDteBillDate(obj[1].toString());        //Bill Date
            objVoidBill.setStrWaiterNo(obj[2].toString());        //Voided Date
            objVoidBill.setStrTableNo(obj[3].toString());         //Entry Time
            objVoidBill.setStrSettlementCode(obj[4].toString());  //Voided Time
            objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString()));          //Bill Amount
            objVoidBill.setStrReasonName(obj[6].toString());      //Reason
            objVoidBill.setStrClientCode(obj[7].toString());      //User Edited
            objVoidBill.setStrUserCreated(obj[8].toString());     //User Created
            objVoidBill.setStrRemarks(obj[9].toString());         //Remarks   

            listOfVoidBillData.add(objVoidBill);
        	}
        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfVoidBillData;
	}
	
	public List funProcessDailySalesReport(String posCode,String fromDate,String toDate,String strShiftNo,String userCode)
	{
		List<clsBillItemDtlBean> listOfDailySaleData = new ArrayList<clsBillItemDtlBean>();
        StringBuilder sbSqlBillWise = new StringBuilder();
        StringBuilder sbSqlBillWiseQFile = new StringBuilder();

        try
        {
        sbSqlBillWise.setLength(0);
        sbSqlBillWise.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
                + ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode"
                + ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt"
                + ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
                + ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
                + ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
                + ",a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,'' "
                + "from tblbillhd  a "
                + "left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
                + "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                + "left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                + "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlBillWise.append(" and a.strPOSCode='" + posCode + "' ");
        }
        sbSqlBillWise.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
        sbSqlBillWise.append(" order by a.strBillNo desc");
        //System.out.println("Bill Wise Report Live Query="+sbSqlBillWise);

        sbSqlBillWiseQFile.setLength(0);
        sbSqlBillWiseQFile.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
                + ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
                + ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt "
                + ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated "
                + ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges "
                + ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
                + ",a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,'' "
                + "from tblqbillhd  a "
                + "left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
                + "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                + "left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                + "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

        if (!posCode.equals("All"))
        {
            sbSqlBillWiseQFile.append(" and a.strPOSCode='" + posCode + "' ");
        }
        sbSqlBillWiseQFile.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
        sbSqlBillWiseQFile.append(" order by a.strBillNo desc");

        List listLiveData = objBaseService.funGetList(sbSqlBillWise,"sql");
        if(listLiveData.size()>0)
        {
        	for(int i=0;i<listLiveData.size();i++)
        	{
        	Object[] obj = (Object[]) listLiveData.get(i);	
            clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
            objBillItemDtlBean.setStrBillNo(obj[0].toString());          //BillNo
            objBillItemDtlBean.setDteBillDate(obj[1].toString());        //Bill Date
            objBillItemDtlBean.setStrItemCode(obj[3].toString());        //Table Name    
            objBillItemDtlBean.setStrPosName(obj[4].toString());         //POS Name
            objBillItemDtlBean.setStrSettelmentMode(obj[5].toString());  //Settle Mode
            objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[6].toString()));        //Sub Total
            objBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[7].toString()));     //Disc Per
            objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[8].toString()));     //Disc Amt
            objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[9].toString()));         //Tax Amt
            objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[10].toString()));  //Settle Amt
            objBillItemDtlBean.setStrDiscType(obj[11].toString());       //User Created
            objBillItemDtlBean.setStrDiscValue(obj[13].toString());      //Date Created
            objBillItemDtlBean.setStrItemName(obj[20].toString());       //Customer Name
            objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[18].toString()));         //Delivery Charges
            listOfDailySaleData.add(objBillItemDtlBean);
        	}
        }
        
        List listQFileData = objBaseService.funGetList(sbSqlBillWiseQFile,"sql");
        if(listQFileData.size()>0)
        {
        	for(int i=0;i<listQFileData.size();i++)
        	{
        	Object[] obj = (Object[]) listQFileData.get(i);	
            clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
            objBillItemDtlBean.setStrBillNo(obj[0].toString());          //BillNo
            objBillItemDtlBean.setDteBillDate(obj[1].toString());        //Bill Date
            objBillItemDtlBean.setStrItemCode(obj[3].toString());        //Table Name    
            objBillItemDtlBean.setStrPosName(obj[4].toString());         //POS Name
            objBillItemDtlBean.setStrSettelmentMode(obj[5].toString());  //Settle Mode
            objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[6].toString()));        //Sub Total
            objBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[7].toString()));     //Disc Per
            objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[8].toString()));     //Disc Amt
            objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[9].toString()));         //Tax Amt
            objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[10].toString()));  //Settle Amt
            objBillItemDtlBean.setStrDiscType(obj[11].toString());       //User Created
            objBillItemDtlBean.setStrDiscValue(obj[13].toString());      //Date Created
            objBillItemDtlBean.setStrItemName(obj[20].toString());       //Customer Name
            objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[18].toString()));         //Delivery Charges
            listOfDailySaleData.add(objBillItemDtlBean);
        	}
        }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfDailySaleData;
	}
	
	public List funProcessSubGroupWiseSummaryReport(String posCode,String fromDate,String toDate,String strShiftNo,String strUserCode)
	{
		List listRet = new ArrayList();
		List<clsGroupSubGroupWiseSales> listOfData = new ArrayList<clsGroupSubGroupWiseSales>();
		try
		{
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sql = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			Map<String, clsGroupSubGroupWiseSales> mapSubGroupWiseDtl = new HashMap<>();
			
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);
			sql.setLength(0);
			sqlModLive.setLength(0);
			sqlModQFile.setLength(0);
			

			sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
							+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'"
							+ strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
							+ "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d "
							+ ",tblposmaster f "
							+ "where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ " and a.strPOSCode=f.strPOSCode "
							+ "and b.strItemCode=d.strItemCode "
							+ "and c.strSubGroupCode=d.strSubGroupCode ");

			sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
							+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'"
							+ strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
							+ "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d "
							+ ",tblposmaster f "
							+ "where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ " and a.strPOSCode=f.strPOSCode "
							+ "and b.strItemCode=d.strItemCode "
							+ "and c.strSubGroupCode=d.strSubGroupCode ");

			sqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName"
					+ ",'"
					+ strUserCode
					+ "','0' ,'0.00','0.00' "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd c" + " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode "
					+ " and left(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=c.strSubGroupCode " + "  ");

			sqlModQFile.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName"
					+ ",'"
					+ strUserCode
					+ "','0' ,'0.00','0.00' "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd c" + " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode "
					+ " and left(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=c.strSubGroupCode " + "  ");

			sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '"
					+ fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			//sbSqlFilters.append(" AND a.intShiftCode = '" + strShiftNo+ "' ");

			sbSqlFilters
					.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters);
			sqlModQFile.append(sbSqlFilters);

			List listLiveData = objBaseService.funGetList(sbSqlLive,"sql");
			if(listLiveData.size()>0)
			{
				for(int i=0;i<listLiveData.size();i++)
				{
				Object[] obj = (Object[]) listLiveData.get(i);	
				clsGroupSubGroupWiseSales objDtlBean = new clsGroupSubGroupWiseSales();
				String subGroupCode = obj[0].toString();

                if (mapSubGroupWiseDtl.containsKey(subGroupCode))
                {
                	objDtlBean = mapSubGroupWiseDtl.get(subGroupCode);
                	objDtlBean.setQty(objDtlBean.getQty() + Double.parseDouble(obj[2].toString()));
                	objDtlBean.setSubTotal(objDtlBean.getSubTotal() + Double.parseDouble(obj[7].toString()));
                    objDtlBean.setDiscAmt(objDtlBean.getDiscAmt() + Double.parseDouble(obj[8].toString()));
                    objDtlBean.setDblNetTotal(objDtlBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
                    mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
                }
                else
                {
                	objDtlBean = new clsGroupSubGroupWiseSales();
                	objDtlBean.setSubGroupCode(obj[0].toString()); 
                	objDtlBean.setSubGroupName(obj[1].toString());
                	objDtlBean.setPosName(obj[4].toString());
                	objDtlBean.setQty(Double.parseDouble(obj[2].toString()));
                	objDtlBean.setSubTotal(Double.parseDouble(obj[7].toString()));
                	objDtlBean.setDiscAmt(Double.parseDouble(obj[8].toString()));
                	objDtlBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));

                    mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
                }
				}
			}
			
			List listQData = objBaseService.funGetList(sbSqlQFile,"sql");
			if(listQData.size()>0)
			{
				for(int i=0;i<listQData.size();i++)
				{
				Object[] obj = (Object[]) listQData.get(i);	
				clsGroupSubGroupWiseSales objDtlBean = new clsGroupSubGroupWiseSales();
				String subGroupCode = obj[0].toString();

                if (mapSubGroupWiseDtl.containsKey(subGroupCode))
                {
                	objDtlBean = mapSubGroupWiseDtl.get(subGroupCode);
                	objDtlBean.setQty(objDtlBean.getQty() + Double.parseDouble(obj[2].toString()));
                	objDtlBean.setSubTotal(objDtlBean.getSubTotal() + Double.parseDouble(obj[7].toString()));
                    objDtlBean.setDiscAmt(objDtlBean.getDiscAmt() + Double.parseDouble(obj[8].toString()));
                    objDtlBean.setDblNetTotal(objDtlBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
                    mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
                }
                else
                {
                	objDtlBean = new clsGroupSubGroupWiseSales();
                	objDtlBean.setSubGroupCode(obj[0].toString()); 
                	objDtlBean.setSubGroupName(obj[1].toString());
                	objDtlBean.setPosName(obj[4].toString());
                	objDtlBean.setQty(Double.parseDouble(obj[2].toString()));
                	objDtlBean.setSubTotal(Double.parseDouble(obj[7].toString()));
                	objDtlBean.setDiscAmt(Double.parseDouble(obj[8].toString()));
                	objDtlBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));

                    mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
                }
				}
			}
			Comparator<clsGroupSubGroupWiseSales> subGroupCodeComparator = new Comparator<clsGroupSubGroupWiseSales>()
		            {

		                @Override
		                public int compare(clsGroupSubGroupWiseSales o1, clsGroupSubGroupWiseSales o2)
		                {
		                    return o1.getSubGroupCode().compareToIgnoreCase(o2.getSubGroupCode());
		                }
		            };
		            Comparator<clsGroupSubGroupWiseSales> subGroupNameComparator = new Comparator<clsGroupSubGroupWiseSales>()
		            {

		                @Override
		                public int compare(clsGroupSubGroupWiseSales o1, clsGroupSubGroupWiseSales o2)
		                {
		                    return o1.getSubGroupName().compareToIgnoreCase(o2.getSubGroupName());
		                }
		            };

		            listOfData.addAll(mapSubGroupWiseDtl.values());

		            Collections.sort(listOfData, new clsGroupSubGroupWiseSalesComparator(
		                    subGroupCodeComparator,
		                    subGroupNameComparator)
		            );
		     
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfData;
	}
	
	public List funProcessTaxSummaryForSubGroupWiseSummaryReport(String posCode,String fromDate,String toDate)
	{
		List<clsTaxCalculationDtls> listOfTaxData = new ArrayList<clsTaxCalculationDtls>();
		try
		{
		StringBuilder sqlTax = new StringBuilder();
		sqlTax.append("select c.strTaxDesc,sum(b.dblTaxableAmount),sum(b.dblTaxAmount) \n" 
                + "from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c\n" 
                + "where a.strBillNo=b.strBillNo and b.strTaxCode=c.strTaxCode\n" 
                + "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"'\n");
        if (!posCode.equals("All"))
        {
            sqlTax.append(" AND a.strPOSCode = '" + posCode + "' ");
        }
        sqlTax.append(" group by b.strTaxCode");
        List listTax = objBaseService.funGetList(sqlTax,"sql");
        if(listTax.size()>0)
        {
           for(int i=0;i<listTax.size();i++)
           {
           Object[] obj = (Object[]) listTax.get(i);   
           clsTaxCalculationDtls objBean = new clsTaxCalculationDtls();
           objBean.setStrTaxDesc(obj[0].toString());
           objBean.setTaxableAmount(Double.parseDouble(obj[1].toString()));
           objBean.setTaxAmount(Double.parseDouble(obj[2].toString()));
           listOfTaxData.add(objBean);
           }
        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfTaxData;
	}
	
	public List funProcessSettlementSummaryForSubGroupWiseSummaryReport(String posCode,String fromDate,String toDate)
	{
		List<clsBillItemDtlBean> listOfSettlementData = new ArrayList<clsBillItemDtlBean>();
        StringBuilder sqlSettlement = new StringBuilder();
        try
        {
        sqlSettlement.append("select c.strSettelmentDesc,sum(b.dblSettlementAmt) \n" 
                + "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c\n" 
                + "where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode\n" 
                + "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"'\n");
        if (!posCode.equals("All"))
        {
            sqlSettlement.append(" AND a.strPOSCode = '" + posCode + "' ");
        }
        sqlSettlement.append(" group by b.strSettlementCode,c.strSettelmentDesc");
        List listSettlementData = objBaseService.funGetList(sqlSettlement,"sql");
        if(listSettlementData.size()>0)
        {	
        for(int i=0;i<listSettlementData.size();i++)
        {
          Object[] obj = (Object[]) listSettlementData.get(i);
          clsBillItemDtlBean objBean = new clsBillItemDtlBean();
          objBean.setStrSettelmentDesc(obj[0].toString());
          objBean.setDblSettlementAmt(Double.parseDouble(obj[1].toString()));
          listOfSettlementData.add(objBean);
        }
		}
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfSettlementData;
	}
	

	public List funProcessLiveBlindSettlementWiseReport(String posCode,String fromDate,String toDate,String qType)
	{
		StringBuilder sbSqlLive = new StringBuilder();
       
        StringBuilder sqlFilter = new StringBuilder();
        DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
        DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
        List listSqlLiveData = new ArrayList();
        try
        {
        if(qType.equalsIgnoreCase("live"))
        {
        sbSqlLive.setLength(0);	
        sbSqlLive.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname,count(*) "
                + "FROM tblbillhd a, tblbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
                + "Where a.strBillNo = b.strBillNo "
                + " and date(a.dteBillDate)=date(b.dteBillDate) "
                + " and a.strClientCode=b.strClientCode "
                + "and a.strposcode=d.strposcode "
                + "and b.strSettlementCode = c.strSettelmentCode "
                + " and c.strSettelmentType!='Complementary' AND c.strSettelmentType!='cash' ");
        sqlFilter.append("and date(a.dteBillDate ) BETWEEN  '" + fromDate + "' AND '" + toDate + "' ");
        if (!"All".equalsIgnoreCase(posCode))
        {
            sqlFilter.append("and  a.strPosCode='" + posCode + "' ");
        }
        
       //sqlFilter.append(" and a.intShiftCode = '" + shiftNo + "' ");
       

        sqlFilter.append("GROUP BY c.strSettelmentDesc, a.strPosCode");

        sbSqlLive.append(sqlFilter);
       
       
        listSqlLiveData = objBaseService.funGetList(sbSqlLive,"sql");
        }
        else
        {
        	sbSqlLive.setLength(0);		
        	sbSqlLive.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname,count(*) "
                    + "FROM tblqbillhd a, tblqbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
                    + "Where a.strBillNo = b.strBillNo "
                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strClientCode=b.strClientCode "
                    + "and a.strposcode=d.strposcode "
                    + "and b.strSettlementCode = c.strSettelmentCode "
                    + " and c.strSettelmentType!='Complementary' AND c.strSettelmentType!='cash' ");

            sqlFilter.append("and date(a.dteBillDate ) BETWEEN  '" + fromDate + "' AND '" + toDate + "' ");
            if (!"All".equalsIgnoreCase(posCode))
            {
                sqlFilter.append("and  a.strPosCode='" + posCode + "' ");
            }
           
           // sqlFilter.append(" and a.intShiftCode = '" + shiftNo + "' ");
           

            sqlFilter.append("GROUP BY c.strSettelmentDesc, a.strPosCode");

            sbSqlLive.append(sqlFilter);
           
            listSqlLiveData = objBaseService.funGetList(sbSqlLive,"sql");
        }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listSqlLiveData;
	}
	
	public List funProcessLiveDataForOperatorWiseReport(String posCode,String fromDate,String toDate,String userCode,String strShiftNo,String settleCode)
	{
		StringBuilder sqlBuilder = new StringBuilder(); 
		List listSettlementWiseBills = new ArrayList();
		
		try
		{
		sqlBuilder.setLength(0);
         sqlBuilder.append("SELECT a.strBillNo,date(a.dteBillDate),ifnull(e.strUserCode,'SANGUINE'),a.strUserEdited,b.strPOSName "
                 + ", IFNULL(a.dblSubTotal,0.00)dblSubTotal "
                 + ", IFNULL(a.dblDiscountAmt,0.00)dblDiscountAmt,(dblSubTotal-dblDiscountAmt)dblNetTotal"
                 + ",a.dblTaxAmt, IFNULL(d.strSettelmentDesc,'') strSettelmentDesc "
                 + ", IFNULL(sum(c.dblSettlementAmt),0.00)dblSettlementAmt,a.strSettelmentMode "
                 + "FROM tblbillhd a "
                 + "LEFT OUTER JOIN tblposmaster b ON a.strPOSCode=b.strPOSCode "
                 + "LEFT OUTER JOIN tblbillsettlementdtl c ON a.strBillNo=c.strBillNo AND a.strClientCode=c.strClientCode AND DATE(a.dteBillDate)= DATE(c.dteBillDate) "
                 + "LEFT OUTER JOIN tblsettelmenthd d ON c.strSettlementCode=d.strSettelmentCode "
                 + "left outer join tbluserhd e on a.strUserEdited=e.strUserCode "
                 + "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
         if (!posCode.equalsIgnoreCase("All"))
         {
             sqlBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
         }
         if (!userCode.equalsIgnoreCase("All"))
         {
             sqlBuilder.append("  and e.strUserCode='" + userCode + "'");
         }
         sqlBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
         if (!settleCode.equalsIgnoreCase("All"))
         {
             sqlBuilder.append("  and d.strSettelmentCode='" + settleCode + "'");
         }

         sqlBuilder.append("group by a.dteBillDate,a.strBillNo,c.strSettlementCode "
                 + "order by e.strUserCode,a.dteBillDate ");
         listSettlementWiseBills = objBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
         return listSettlementWiseBills;
	}
	
	public List funProcessQFileDataForOperatorWiseReport(String posCode,String fromDate,String toDate,String userCode,String strShiftNo,String settleCode)
	{
		StringBuilder sqlBuilder = new StringBuilder(); 
		List listSettlementWiseBills = new ArrayList();
		
		try
		{
		sqlBuilder.setLength(0);
        sqlBuilder.append("SELECT a.strBillNo,date(a.dteBillDate),ifnull(e.strUserCode,'SANGUINE'),a.strUserEdited,b.strPOSName "
                + ", IFNULL(a.dblSubTotal,0.00)dblSubTotal "
                + ", IFNULL(a.dblDiscountAmt,0.00)dblDiscountAmt,(dblSubTotal-dblDiscountAmt)dblNetTotal"
                + ",a.dblTaxAmt, IFNULL(d.strSettelmentDesc,'') strSettelmentDesc "
                + ", IFNULL(sum(c.dblSettlementAmt),0.00)dblSettlementAmt,a.strSettelmentMode "
                + "FROM tblqbillhd a "
                + "LEFT OUTER JOIN tblposmaster b ON a.strPOSCode=b.strPOSCode "
                + "LEFT OUTER JOIN tblqbillsettlementdtl c ON a.strBillNo=c.strBillNo AND a.strClientCode=c.strClientCode AND DATE(a.dteBillDate)= DATE(c.dteBillDate) "
                + "LEFT OUTER JOIN tblsettelmenthd d ON c.strSettlementCode=d.strSettelmentCode "
                + "left outer join tbluserhd e on a.strUserEdited=e.strUserCode "
                + "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
        }
        if (!userCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("  and e.strUserCode='" + userCode + "'");
        }
        sqlBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
        if (!settleCode.equalsIgnoreCase("All"))
        {
            sqlBuilder.append("  and d.strSettelmentCode='" + settleCode + "'");
        }

        sqlBuilder.append("group by a.dteBillDate,a.strBillNo,c.strSettlementCode "
                + "order by e.strUserCode,a.dteBillDate ");
        
        listSettlementWiseBills = objBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return listSettlementWiseBills;
	}
	
	public List funProcessGuestCreditReport(String posCode,String fromDate,String toDate)
	{
		StringBuilder sqlLiveBuilder = new StringBuilder();
        StringBuilder sqlQBuilder = new StringBuilder();
        List<clsBillDtl> listOfGuestCreditData = new ArrayList<>();

        try
        {
        sqlLiveBuilder.append("select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) "
                + ",h.strPosName,d.strSettelmentDesc,a.strKOTNo,b.strPOSCode,b.strRemarks,ifnull(e.strTableName,'') as strTableName"
                + ",f.strCustomerName,ifnull(g.strWShortName,'') as strWShortName,b.dblDeliveryCharges "
                + ",a.dblDiscountAmt,a.dblTaxAmount,(a.dblAmount-a.dblDiscountAmt+a.dblTaxAmount)GrandTotal "
                + ",f.longMobileNo,ifnull(i.strReasonName,'') "
                + "from tblbilldtl a "
                + "left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                + "left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                + "left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
                + "left outer join tblcustomermaster f on c.strCustomerCode=f.strCustomerCode "
                + "left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
                + "left outer join tblposmaster h on b.strPOSCode=h.strPosCode "
                + "left outer join tblreasonmaster i on b.strReasonCode=i.strReasonCode "
                + "where date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "'  "
                + "and d.strSettelmentType='Credit' ");

        sqlQBuilder.append("select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) "
                + ",h.strPosName,d.strSettelmentDesc,a.strKOTNo,b.strPOSCode,b.strRemarks,ifnull(e.strTableName,'') as strTableName"
                + ",f.strCustomerName,ifnull(g.strWShortName,'') as strWShortName,b.dblDeliveryCharges "
                + ",a.dblDiscountAmt,a.dblTaxAmount,(a.dblAmount-a.dblDiscountAmt+a.dblTaxAmount)GrandTotal "
                + ",f.longMobileNo,ifnull(i.strReasonName,'') "
                + "from tblqbilldtl a "
                + "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                + "left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo  and date(a.dteBillDate)=date(c.dteBillDate) "
                + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                + "left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
                + "left outer join tblcustomermaster f on c.strCustomerCode=f.strCustomerCode "
                + "left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
                + "left outer join tblposmaster h on b.strPOSCode=h.strPosCode "
                + "left outer join tblreasonmaster i on b.strReasonCode=i.strReasonCode "
                + "where date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "'  "
                + "and d.strSettelmentType='Credit' ");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlLiveBuilder.append("and b.strPOSCode='" + posCode + "' ");
            sqlQBuilder.append("and b.strPOSCode='" + posCode + "' ");
        }

        sqlLiveBuilder.append("group by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode "
                + "order by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode ");
        sqlQBuilder.append("group by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode "
                + "order by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode ");


        //live
        List listLiveData = objBaseService.funGetList(sqlLiveBuilder,"sql");
        if(listLiveData.size()>0)
        {
            for(int i=0;i<listLiveData.size();i++)
            {
            Object[] obj = (Object[]) listLiveData.get(i);	
        	clsBillDtl objBillDtlBean = new clsBillDtl();
            String dteBillDate = "";
            String billDate = obj[6].toString();
            String dateParts[] = billDate.split("-");
            dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

            objBillDtlBean.setStrBillNo(obj[0].toString());
            objBillDtlBean.setStrItemCode(obj[1].toString());
            objBillDtlBean.setStrItemName(obj[2].toString());
            objBillDtlBean.setDblRate(Double.parseDouble(obj[3].toString()));
            objBillDtlBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
            objBillDtlBean.setDblAmount(Double.parseDouble(obj[5].toString()));
            objBillDtlBean.setDteBillDate(dteBillDate);
            objBillDtlBean.setStrPosName(obj[7].toString());
            objBillDtlBean.setStrSettlementName(obj[8].toString());
            objBillDtlBean.setStrKOTNo(obj[9].toString());
            objBillDtlBean.setStrPOSCode(obj[10].toString());
            objBillDtlBean.setStrRemarks(obj[11].toString());
            objBillDtlBean.setStrTableName(obj[12].toString());
            objBillDtlBean.setStrCustomerName(obj[13].toString());
            objBillDtlBean.setStrWShortName(obj[14].toString());
            objBillDtlBean.setDblDelCharges(Double.parseDouble(obj[15].toString()));
            objBillDtlBean.setDblDiscountAmt(Double.parseDouble(obj[16].toString()));//disc
            objBillDtlBean.setDblTaxAmount(Double.parseDouble(obj[17].toString()));//tax
            objBillDtlBean.setDblBillAmt(Double.parseDouble(obj[18].toString()));//grandtotal
            objBillDtlBean.setLongMobileNo(Long.parseLong(obj[19].toString()));
            objBillDtlBean.setStrReasonName(obj[20].toString());

            listOfGuestCreditData.add(objBillDtlBean);
        	}
        }
        
        //Q
       List listQFileData = objBaseService.funGetList(sqlQBuilder,"sql");
        if(listQFileData.size()>0)
        {
        	for(int i=0;i<listQFileData.size();i++)
            {
        	Object[] obj = (Object[]) listQFileData.get(i);	
         	clsBillDtl objBillDtlBean = new clsBillDtl();
             String dteBillDate = "";
             String billDate = obj[6].toString();
             String dateParts[] = billDate.split("-");
             dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

             objBillDtlBean.setStrBillNo(obj[0].toString());
             objBillDtlBean.setStrItemCode(obj[1].toString());
             objBillDtlBean.setStrItemName(obj[2].toString());
             objBillDtlBean.setDblRate(Double.parseDouble(obj[3].toString()));
             objBillDtlBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
             objBillDtlBean.setDblAmount(Double.parseDouble(obj[5].toString()));
             objBillDtlBean.setDteBillDate(dteBillDate);
             objBillDtlBean.setStrPosName(obj[7].toString());
             objBillDtlBean.setStrSettlementName(obj[8].toString());
             objBillDtlBean.setStrKOTNo(obj[9].toString());
             objBillDtlBean.setStrPOSCode(obj[10].toString());
             objBillDtlBean.setStrRemarks(obj[11].toString());
             objBillDtlBean.setStrTableName(obj[12].toString());
             objBillDtlBean.setStrCustomerName(obj[13].toString());
             objBillDtlBean.setStrWShortName(obj[14].toString());
             objBillDtlBean.setDblDelCharges(Double.parseDouble(obj[15].toString()));
             objBillDtlBean.setDblDiscountAmt(Double.parseDouble(obj[16].toString()));//disc
             objBillDtlBean.setDblTaxAmount(Double.parseDouble(obj[17].toString()));//tax
             objBillDtlBean.setDblBillAmt(Double.parseDouble(obj[18].toString()));//grandtotal
             objBillDtlBean.setLongMobileNo(Long.parseLong(obj[19].toString()));
             objBillDtlBean.setStrReasonName(obj[20].toString());

             listOfGuestCreditData.add(objBillDtlBean);
        }
        }
        
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfGuestCreditData;
      
	}
	
	public List funProcessItemMasterListing()
	{
		 List<clsBillDtl> listOfItemMasterListing = new ArrayList<>();
         StringBuilder sqlBuilder = new StringBuilder();

         try
         {
         sqlBuilder.setLength(0);
         sqlBuilder.append("select a.strItemCode,a.strItemName,b.strSubGroupName,c.strGroupName,ifnull(a.strTaxIndicator,'')  "
                 + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c "
                 + "where a.strSubGroupCode=b.strSubGroupCode "
                 + "and b.strGroupCode=c.strGroupCode "
                 + "group by c.strGroupCode,b.strSubGroupCode,a.strItemCode,a.strItemName "
                 + "order by c.strGroupCode,b.strSubGroupCode,a.strItemCode,a.strItemName ");

         List listItemMaster = objBaseService.funGetList(sqlBuilder,"sql");
         if(listItemMaster.size()>0)
         {
        	 for(int i=0;i<listItemMaster.size();i++)
        	 {
        	 Object[] obj = (Object[]) listItemMaster.get(i);	 
        	 clsBillDtl objItemDtl = new clsBillDtl();

             objItemDtl.setStrItemCode(obj[0].toString());
             objItemDtl.setStrItemName(obj[1].toString());
             objItemDtl.setStrSubGroupName(obj[2].toString());
             objItemDtl.setStrGroupName(obj[3].toString());
             objItemDtl.setStrTaxIndicator(obj[4].toString());

             listOfItemMasterListing.add(objItemDtl);
        	 }
         }
         }
         catch(Exception e)
         {
        	 e.printStackTrace();
         }
         return listOfItemMasterListing;
        
	}
	
	public List funProcessAuditFlashReport(String fromDate, String toDate,
			String posName, String userName, String strReportType,
			String reasonName, String auditType, String clientCode,String strSorting,
			String strType,String strPOSCode,String reasonCode,String userCode)
	{
		StringBuilder sbSql = new StringBuilder();
		List listArrColHeader = new ArrayList();
		LinkedHashMap resMap = new LinkedHashMap();
		List totalList = new ArrayList();
		List listArr = new ArrayList();
		Map map = new HashMap();
		int colCount = 5;
		
		try {
			double sumBillAmt = 0.00, sumNewAmt = 0.00;
	        double sumQty = 0.00, discAmt = 0.0,sumTotalAmt=0.00;
			switch (auditType) 
			{
			

			// Modified Bill
			case "Modified Bill":

				
				if (strReportType.equalsIgnoreCase("Summary")) 
				{
			
					sbSql.setLength(0);
					 sbSql.append("select a.strBillNo as BillNo, DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate ,"
		                        + " DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as ModifiedDate ,TIME_FORMAT(time(a.dteBillDate),'%h:%i') as EntryTime , "
		                        + " TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') as ModifyTime,a.dblActualAmount as BillAmt ,"
		                        + " a.dblModifiedAmount as NetAmt,a.strUserCreated as UserCreated, "
		                        + " a.strUserEdited as UserEdited,ifnull(b.strReasonName,'') as ReasonName,ifnull(a.strRemark,'')"
		                        + " ,(a.dblActualAmount-a.dblModifiedAmount) as DiscAmt "
		                        + " from tblvoidbillhd a left outer join tblreasonmaster b on a.strReasonCode=b.strReasonCode ");
		                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strUserCreated='" + userCode + "'"
		                            + " and a.strPOSCode='"+strPOSCode+"'  and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strPOSCode='"+strPOSCode+"'  and  "
		                            + "a.strreasonCode='" + reasonCode + "' group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strUserCreated='" + userCode + "'  "
		                            + "and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strPOSCode='"+strPOSCode+"' "
		                            + "and a.strUserCreated='" + userCode + "' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strUserCreated='" + userCode + "' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
		                {

		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strPOSCode='"+strPOSCode+"' "
		                            + "group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                else
		                {
		                    sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
		                            + fromDate + "' and '" + toDate + "' group by a.strBillNo,a.dteModifyVoidBill");
		                }
		                if ("Item Void".equalsIgnoreCase(strType))
		                {
		                    sbSql.append(" and a.strVoidBillType ='" +strType + "' ");
		                }
		                else if ("Full Void".equalsIgnoreCase(strType))
		                {
		                    sbSql.append(" and a.strVoidBillType ='Bill Void' ");
		                }
		                else
		                {
		                    sbSql.append(" and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' ) ");
		                }
		                if (strSorting.equals("BILL"))
		                {
		                    sbSql.append(" order by a.strBillNo");
		                }
		                else
		                {
		                    sbSql.append(" order by a.dblActualAmount");
		                }
	                List listSql = objBaseService.funGetList(sbSql, "sql");

					if (listSql.size() > 0) {

						for (int i = 0; i < listSql.size(); i++) {
							Object[] obj = (Object[]) listSql.get(i);
							clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
							objBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBillItemDtlBean.setStrEntryTime(obj[2].toString());
							objBillItemDtlBean.setStrModifiyTime(obj[3].toString());
							objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[4].toString()));
							objBillItemDtlBean.setDblAmountTemp(Double.parseDouble(obj[5].toString()));
							objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[6].toString()));
							objBillItemDtlBean.setStrUserCreated(obj[7].toString());
							objBillItemDtlBean.setStrUserEdited(obj[8].toString());
							objBillItemDtlBean.setStrReasonName(obj[9].toString());
							objBillItemDtlBean.setStrRemark(obj[10].toString());
							
							listArr.add(objBillItemDtlBean);
							
							sumBillAmt = sumBillAmt + Double.parseDouble(obj[5].toString());
		                    sumNewAmt = sumNewAmt + Double.parseDouble(obj[6].toString());
		                    discAmt += Double.parseDouble(obj[11].toString());
						}

					}
					totalList.add("Total");
					totalList.add(sumBillAmt);
					totalList.add(sumNewAmt);
					totalList.add(discAmt);
					totalList.add(" ");
					resMap.put("totalList", totalList);
					resMap.put("listArr", listArr);
					resMap.put("ColHeader", listArrColHeader);
				}
					else {
						
						sbSql.setLength(0);
		                sbSql.append("select a.strBillNo,DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as ModifiedDate,"
		                        + "Time(a.dteBillDate) EntryTime,Time(a.dteModifyVoidBill) ModifiedTime,a.strItemName,a.intQuantity,sum(a.dblAmount) as Amount,"
		                        + "b.strUserCreated as Usercreated,b.strUserEdited as UserEdited,ifnull(b.strRemark,'') "
		                        + " from tblvoidbilldtl a, tblvoidbillhd b ");

		                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and a.strPOSCode='"+strPOSCode+"' and "
		                            + "a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' and  a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'"
		                            + " and a.strPOSCode='"+strPOSCode+"' and a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
		                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and a.strPOSCode='"+strPOSCode+"' and a.strUserCreated='" + userCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
		                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and a.strPOSCode='"+strPOSCode+"'  "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
		                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and  a.strUserCreated='" + userCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
		                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and  a.strreasonCode='" + reasonCode + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                else
		                {
		                    sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
		                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
		                            + "group by a.strItemName,a.strBillNo ");
		                }
		                if ("Item Void".equalsIgnoreCase(strType))
		                {
		                    sbSql.append("and b.strVoidBillType ='" + strType + "' ");
		                }
		                else if ("Full Void".equalsIgnoreCase(strType))
		                {
		                    sbSql.append("and b.strVoidBillType ='Bill Void' ");
		                }
		                else
		                {
		                    sbSql.append("and (b.strVoidBillType = 'Bill Void' or b.strVoidBillType = 'ITEM VOID') ");
		                }
		                if (strSorting.equals("BILL"))
		                {
		                    sbSql.append(" order by a.strBillNo asc");
		                }
		                else
		                {
		                    sbSql.append(" order by sum(a.dblAmount) asc");
		                }

						List listSql = objBaseService.funGetList(sbSql, "sql");

						if (listSql.size() > 0) {

							for (int i = 0; i < listSql.size(); i++) {
								Object[] obj = (Object[]) listSql.get(i);
								clsBillItemDtlBean objAuditFlashBean = new clsBillItemDtlBean();
								objAuditFlashBean.setStrBillNo(obj[0].toString());
								objAuditFlashBean.setDteBillDate(obj[1].toString());
								objAuditFlashBean.setStrEntryTime(obj[3].toString());
								objAuditFlashBean.setStrModifiyTime(obj[4].toString());
								objAuditFlashBean.setStrItemName(obj[5].toString());
								objAuditFlashBean.setDblQuantity(Integer.parseInt(obj[6].toString()));
								objAuditFlashBean.setBillAmt(Double.parseDouble(obj[7].toString()));
								objAuditFlashBean.setStrUserCreated(obj[8].toString());
								objAuditFlashBean.setStrUserEdited(obj[9].toString());
								objAuditFlashBean.setStrRemark(obj[10].toString());
								
								listArr.add(objAuditFlashBean);

								sumQty = sumQty + Double.parseDouble(obj[6].toString());
			                    sumBillAmt = sumBillAmt + Double.parseDouble(obj[7].toString());
							}

						}
						
				}	
				break;
				
			case "Voided Bill":
				StringBuilder sbSqlMod = new StringBuilder();
				List<clsBillItemDtlBean> arrListVoidBillWise = new ArrayList<clsBillItemDtlBean>();
				
				sbSql.setLength(0);
				if (strReportType.equalsIgnoreCase("Summary")) {

					sbSql.setLength(0);
	                sbSql.append("select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
	                        + "TIME_FORMAT(time(a.dteBillDate),'%h:%i')  As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime, a.dblModifiedAmount,"
	                        + "a.strUserEdited as UserEdited, a.strReasonName as Reason,ifnull(a.strRemark,'')"
	                        + ",b.strPosCode,b.strPosName "
	                        + " from tblvoidbillhd a,tblposmaster b "
	                        + " where a.strTransType='VB'  "
	                        + " and a.strPosCode=b.strPosCode ");
	                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
	                {
	                    sbSql.append(" and a.strPOSCode='"+strPOSCode+ "' and "
	                            + "strUserCreated='" + userCode + "' and strreasonCode='" + reasonCode + "' and "
	                            + "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
	                {
	                    sbSql.append("  and a.strPOSCode='"+strPOSCode+ "' and "
	                            + "strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) between '"
	                            + fromDate + "' and '" + toDate + "' ");
	                }
	                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
	                {
	                    sbSql.append("  and a.strUserCreated='" + userCode + "' "
	                            + "and strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
	                            + "between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
	                {
	                    sbSql.append("  and a.strPOSCode='"+strPOSCode+ "' "
	                            + "and strUserCreated='" + userCode + "'  and Date(a.dteModifyVoidBill) "
	                            + "between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
	                {
	                    sbSql.append("  and strreasonCode='" + reasonCode + "' "
	                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
	                {
	                    sbSql.append("  and a.strPOSCode='"+strPOSCode+ "' and "
	                            + "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else if ("All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
	                {
	                    sbSql.append("  and strUserCreated='" + userCode + "' and "
	                            + "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
	                }
	                else
	                {
	                    sbSql.append("  and Date(a.dteModifyVoidBill) between '" + fromDate
	                            + "' and '" + toDate + "' ");
	                }
	                if ("Item Void".equalsIgnoreCase(strType))
	                {
	                    sbSql.append("and a.strVoidBillType ='" + strType + "'");
	                }
	                else if ("Full Void".equalsIgnoreCase(strType))
	                {
	                    sbSql.append("and a.strVoidBillType ='Bill Void'");
	                }
	                else
	                {
	                    sbSql.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
	                }
	                sbSql.append(" group by a.strBillNo,date(a.dteBillDate) ");
	                
	                if (strSorting.equalsIgnoreCase("BILL"))
	                {
	                    sbSql.append(" order by a.strBillNo");

	                    List listData =  objBaseService.funGetList(sbSql,"sql");
	                    if(listData.size()>0)
	                    {
	                        for(int i=0;i<listData.size();i++)
	                        {	
	                    	Object[] obj = (Object[]) listData.get(i);
	                        double amountTemp = Double.parseDouble(obj[5].toString());
	                        String billNo = obj[0].toString();
	                        StringBuilder sql = new StringBuilder();
	                        sql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
	                        List list2 = objBaseService.funGetList(sql,"sql");
	                        int count=0;
	                        if(list2.size()>0)
	                        {	
	                        count = (int) list2.get(0);
	                        }
	                        if (count > 0)
	                        {
	                            sql.setLength(0);
	                            sql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='" + billNo + "' ");
	                            list2 = objBaseService.funGetList(sql,"sql");
	                            Double temp=0.0;
	                            if(list2.size()>0)
	                            {
	                            temp = (Double) list2.get(0);
	                            }
	                            amountTemp = amountTemp + temp;
	                        }
	                        
	                        clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
	                        objBillItemDtlBean.setStrPosCode(obj[9].toString());
	                        objBillItemDtlBean.setStrPosName(obj[10].toString());
	                        objBillItemDtlBean.setStrBillNo(obj[0].toString());
	                        objBillItemDtlBean.setDteBillDate(obj[1].toString());
	                        objBillItemDtlBean.setDteVoidedDate(obj[2].toString());
	                        objBillItemDtlBean.setStrEntryTime(obj[3].toString());//
	                        objBillItemDtlBean.setStrVoidedTime(obj[4].toString());
	                        objBillItemDtlBean.setDblAmountTemp(amountTemp);
	                        objBillItemDtlBean.setStrUserEdited(obj[6].toString());
	                        objBillItemDtlBean.setStrReasonName(obj[7].toString());
	                        objBillItemDtlBean.setStrRemark(obj[8].toString());
	                        
	                        sumTotalAmt = sumTotalAmt + amountTemp;
	                    }

	                    }

	                }
	                else
	                {

	                    List listData = objBaseService.funGetList(sbSql,"sql");
	                    if(listData.size()>0)
	                    {
	                        for(int i=0;i<listData.size();i++)
	                        {
	                    	Object[] obj = (Object[]) listData.get(i);
	                        clsBillItemDtlBean objBillItemDtlBean = new clsBillItemDtlBean();
	                        double amountTemp = Double.parseDouble(obj[5].toString());
	                        String billNo = obj[0].toString();
	                        //obj.setDblAmount(amountTemp);
	                        //obj.setStrBillNo(billNo);
	                        StringBuilder sql = new StringBuilder(); 
	                        sql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
	                        List list2 = objBaseService.funGetList(sql,"sql");
	                        int count=0;
	                        if(list2.size()>0)
	                        {	
	                        count = (int) list2.get(0);
	                        }
	                        if (count > 0)
	                        {
	                            sql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='" + billNo + "' ");
	                            list2 = objBaseService.funGetList(sql,"sql");
	                            Double temp=0.0;
	                            if(list2.size()>0)
		                        {	
	                            temp = (Double) list2.get(0);
		                        }
	                            amountTemp = amountTemp + temp;
	                            //obj.setDblAmount(amountTemp);
	                        }
	                        objBillItemDtlBean.setStrPosCode(obj[9].toString());
	                        objBillItemDtlBean.setStrPosName(obj[10].toString());
	                        objBillItemDtlBean.setStrBillNo(obj[0].toString());
	                        objBillItemDtlBean.setDteBillDate(obj[1].toString());
	                        objBillItemDtlBean.setDteVoidedDate(obj[2].toString());
	                        objBillItemDtlBean.setStrEntryTime(obj[3].toString());//
	                        objBillItemDtlBean.setStrVoidedTime(obj[4].toString());
	                        objBillItemDtlBean.setDblAmountTemp(amountTemp);
	                        objBillItemDtlBean.setStrUserEdited(obj[6].toString());
	                        objBillItemDtlBean.setStrReasonName(obj[7].toString());
	                        objBillItemDtlBean.setStrRemark(obj[8].toString());

	                        arrListVoidBillWise.add(objBillItemDtlBean);
	                        sumTotalAmt = sumTotalAmt + amountTemp;
	                    }
	                    }

	                }
	                Comparator<clsBillItemDtlBean> compareBillItem = new Comparator<clsBillItemDtlBean>()
	                        {

	                            @Override
	                            public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
	                            {
	                                double dblAmount = o1.getDblAmountTemp();
	                                double dblAmount2 = o2.getDblAmountTemp();

	                                if (dblAmount == dblAmount2)
	                                {
	                                    return 0;
	                                }
	                                else if (dblAmount > dblAmount2)
	                                {
	                                    return 1;
	                                }
	                                else
	                                {
	                                    return -1;
	                                }

	                            }
	                        };
	                        
	                        Collections.sort(arrListVoidBillWise, compareBillItem);
	                        for (clsBillItemDtlBean obj : arrListVoidBillWise)
	                        {
	                        clsBillItemDtlBean objBean = new clsBillItemDtlBean();     
	                        objBean.setStrPosCode( obj.getStrPosCode());
	                        objBean.setStrPosName(obj.getStrPosName());
	                        objBean.setStrBillNo(obj.getStrBillNo());
	                        objBean.setDteBillDate(obj.getDteBillDate());
	                        objBean.setDteVoidedDate(obj.getDteVoidedDate());
	                        objBean.setStrEntryTime(obj.getStrEntryTime());
	                        objBean.setStrVoidedTime(obj.getStrVoidedTime());
	                        objBean.setDblAmountTemp(obj.getDblAmountTemp());
	                        objBean.setStrUserEdited(obj.getStrUserEdited());
	                        objBean.setStrReasonName(obj.getStrReasonName());
	                        objBean.setStrRemark(obj.getStrRemark());
	                           
	                        listArr.add(objBean);    
	                        }
	                       
	                       
	    				
				}
				 else {
					 sbSql.setLength(0);
			            sbSql.append("(select a.strBillNo as strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
			                    + "TIME_FORMAT(time(a.dteBillDate),'%h:%i') As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strItemName,"
			                    + "b.intQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'')"
			                    + ",c.strPosCode,c.strPosName "
			                    + "from tblvoidbillhd a,tblvoidbilldtl b,tblposmaster c "
			                    + "where a.strBillNo=b.strBillNo "
			                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
			                    + "and a.strPosCode=c.strPosCode ");
			            
			                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                            + "and a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode
			                            + "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' and "
			                            + "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if ("All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strUserCreated='" + userCode + "' "
			                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strreasonCode='" + reasonCode + "' "
			                            + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                            + "and a.strUserCreated='" + userCode + "' and Date(a.dteModifyVoidBill) "
			                            + "between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                            + "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
			                            + "between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
			                {
			                    sbSql.append(" and b.strTransType='VB' and a.strUserCreated='" + userCode + "' "
			                            + "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill)"
			                            + " between '" + fromDate + "' and '" + toDate + "'");
			                }
			                else
			                {
			                    sbSql.append(" and b.strTransType='VB' and Date(a.dteModifyVoidBill) "
			                            + "between '" + fromDate + "' and '" + toDate + "'");
			                }
			                if ("Item Void".equalsIgnoreCase(strType))
			                {
			                    sbSql.append("and a.strVoidBillType ='" + strType + "'");
			                }
			                else if ("Full Void".equalsIgnoreCase(strType))
			                {
			                    sbSql.append("and a.strVoidBillType ='Bill Void'");
			                }
			                else
			                {
			                    sbSql.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
			                }

			                sbSql.append(" group by a.strBillNo,b.strItemCode)");


			                sbSqlMod.setLength(0);
			                sbSqlMod.append("(select a.strBillNo as strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
			                        + "TIME_FORMAT(time(a.dteBillDate),'%h:%i')  As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strModifierName,"
			                        + "b.dblQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'')"
			                        + ",c.strPosCode,c.strPosName "
			                        + "from tblvoidbillhd a,tblvoidmodifierdtl b,tblposmaster c "
			                        + "where a.strBillNo=b.strBillNo "
			                        + "and date(a.dteBillDate)=date(b.dteBillDate) "
			                        + "and a.strPosCode=c.strPosCode ");

			                    if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                                + "and a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode
			                                + "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' and "
			                                + "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if ("All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strUserCreated='" + userCode + "' "
			                                + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strreasonCode='" + reasonCode + "' "
			                                + "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if (!"All".equals(posName) && !"All".equals(userName) && "All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                                + "and a.strUserCreated='" + userCode + "' and Date(a.dteModifyVoidBill) "
			                                + "between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='"+strPOSCode+"' "
			                                + "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
			                                + "between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and a.strUserCreated='" + userCode + "' "
			                                + "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill)"
			                                + " between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    else
			                    {
			                        sbSqlMod.append(" and a.strTransType='VB' and Date(a.dteModifyVoidBill) "
			                                + "between '" + fromDate + "' and '" + toDate + "'");
			                    }
			                    if ("Item Void".equalsIgnoreCase(strType))
			                    {
			                        sbSqlMod.append("and a.strVoidBillType ='" + strType + "'");
			                    }
			                    else if ("Full Void".equalsIgnoreCase(strType))
			                    {
			                        sbSqlMod.append("and a.strVoidBillType ='Bill Void'");
			                    }
			                    else
			                    {
			                        sbSqlMod.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
			                    }

			                    sbSqlMod.append(" group by a.strBillNo,b.strModifierCode)");
			                    if (strSorting.equals("BILL"))
			                    {
			                        sbSqlMod.append(" order by strBillNo");
			                    }
			                    else
			                    {
			                        sbSqlMod.append(" order by BillAmount");
			                    }
			                

			                StringBuilder sql = new StringBuilder();
			                sql.append(sbSql.toString() + " union " + sbSqlMod.toString());	                    
			                List listData = objBaseService.funGetList(sql, "sql");
	                    if(listData.size()>0)
	                    {
	                    	for(int i=0;i<listData.size();i++)
	                    	{	
	                        Object[] obj =(Object[])listData.get(i);
	                        clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                        objBean.setStrPosCode(obj[11].toString());
	                        objBean.setStrPosName(obj[12].toString());
	                        objBean.setStrBillNo(obj[0].toString());
	                        objBean.setDteBillDate(obj[1].toString());
	                        objBean.setDteVoidedDate(obj[2].toString());
	                        objBean.setStrEntryTime(obj[3].toString());
	                        objBean.setStrVoidedTime(obj[4].toString());
	                        objBean.setStrItemName(obj[5].toString());
	                        objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
	                        objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
	                        objBean.setStrUserEdited(obj[8].toString());
	                        objBean.setStrReasonName(obj[9].toString());
	                        objBean.setStrRemark(obj[10].toString());
	                    	listArr.add(objBean);
	                        
	                        sumQty = sumQty + Double.parseDouble(obj[6].toString());
	                        sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
	                    	}
	                    }
	                    
				 }
				break;
			case "Voided Advanced Order":

				sbSqlMod = new StringBuilder();
				List<clsBillItemDtlBean> arrListVoidAdvOrder = new ArrayList<clsBillItemDtlBean>();
				sbSql.setLength(0);
				
				if (strReportType.equalsIgnoreCase("Summary")) 
				{
					
					sbSql.setLength(0);
					sbSql.append("select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y')  as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y') as VoidedDate,"
							+ "Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount,"
							+ "a.strUserEdited as UserEdited, a.strReasonName as Reason"
							+ " from tblvoidbillhd a ");
					
					if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and "
								+ "strUserCreated='"
								+ userCode
								+ "' and strreasonCode='"
								+ reasonCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and  "
								+ "strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strUserCreated='"
								+ userCode
								+ "' "
								+ "and strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' "
								+ "and strUserCreated='"
								+ userCode
								+ "'  and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strreasonCode='"
								+ reasonCode
								+ "' "
								+ "and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strUserCreated='"
								+ userCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else {
						sbSql.append(" where a.strTransType='AOVB' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");
					} 
					sbSql.append(" group by a.strBillNo");
	                if (strSorting.equalsIgnoreCase("BILL"))
	                {
	                	sbSql.append(" order by a.strBillNo");
	                    List listData = objBaseService.funGetList(sbSql,"sql");
	                    if(listData.size()>0)
	                    {
	                    	for(int i=0;i<listData.size();i++)
	                    	{	
	                    	clsBillItemDtlBean objBean = new clsBillItemDtlBean();	
	                        Object[] obj = (Object[]) listData.get(i);
	                    	double amountTemp = Double.parseDouble(obj[5].toString());
	                        String billNo = obj[0].toString();
	                        sbSql.setLength(0);
	                        sbSql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
	                        List list2 = objBaseService.funGetList(sbSql,"sql");
	                        int count=0;
	                        if(list2.size()>0)
	                        {	
	                        count = (int) list2.get(0);
	                        }
	                        if (count > 0)
	                        {
	                            sbSql.setLength(0);
	                            sbSql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='" + billNo + "' ");
	                            list2 = objBaseService.funGetList(sbSql,"sql");
	                            Double temp=0.0;
	                            if(list2.size()>0)
	                            {	
	                            temp = (Double) list2.get(0);
	                            }
	                            amountTemp = amountTemp + temp;
	                        }
	                        objBean.setStrBillNo(obj[0].toString());
	                        objBean.setDteBillDate(obj[1].toString());
	                        objBean.setDteVoidedDate(obj[2].toString());
	                        objBean.setStrEntryTime(obj[3].toString());
	                        objBean.setStrVoidedTime(obj[4].toString());
	                        objBean.setDblModifiedAmount(amountTemp);
	                        objBean.setStrUserEdited(obj[6].toString());
	                        objBean.setStrReasonName(obj[7].toString());
	                        arrListVoidAdvOrder.add(objBean);
	                        
	                        sumTotalAmt = sumTotalAmt + amountTemp;
	                    }
	                    }
	                    
	                }
	                else
	                {
	                   List listData = objBaseService.funGetList(sbSql,"sql");
	                   if(listData.size()>0)
	                    {
	                       for(int i=0;i<listData.size();i++)
	                       {
	                    	Object[] obj = (Object[]) listData.get(i);   
	                	   clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                        double amountTemp = Double.parseDouble(obj[5].toString());
	                        String billNo = obj[0].toString();
	                        sbSql.setLength(0);
	                        sbSql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
	                        List list2 = objBaseService.funGetList(sbSql,"sql");
	                        int count = 0;
	                        if(list2.size()>0)
	                        {
	                        count = (int) list2.get(0);
	                        }
	                        if (count > 0)
	                        {
	                            sbSql.setLength(0);
	                            sbSql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='" + billNo + "' ");
	                            list2 = objBaseService.funGetList(sbSql,"sql");
	                            Double temp = 0.0;
	                            if(list2.size()>0)
	                            {
	                            temp = (Double) list2.get(0);
	                            }
	                            amountTemp = amountTemp + temp;
	                        }

	                        objBean.setStrBillNo(obj[0].toString());
	                        objBean.setDteBillDate(obj[1].toString());
	                        objBean.setDteVoidedDate(obj[2].toString());
	                        objBean.setStrEntryTime(obj[3].toString());
	                        objBean.setStrVoidedTime(obj[4].toString());
	                        objBean.setDblModifiedAmount(amountTemp);
	                        objBean.setStrUserEdited(obj[6].toString());
	                        objBean.setStrReasonName(obj[7].toString());
	                        arrListVoidAdvOrder.add(objBean);
	                        sumTotalAmt = sumTotalAmt + amountTemp;
	                       }
	                    }
	                }
	                Comparator<clsBillItemDtlBean> compareBillItem = new Comparator<clsBillItemDtlBean>()
	                {
	                    @Override
	                    public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
	                    {
	                        double dblAmount = o1.getDblModifiedAmount();
	                        double dblAmount2 = o2.getDblModifiedAmount();

	                        if (dblAmount == dblAmount2)
	                        {
	                            return 0;
	                        }
	                        else if (dblAmount > dblAmount2)
	                        {
	                            return 1;
	                        }
	                        else
	                        {
	                            return -1;
	                        }
	                    }
	                };
	                Collections.sort(arrListVoidAdvOrder, compareBillItem);
	                for (clsBillItemDtlBean obj : arrListVoidAdvOrder)
	                {
	                	clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                	objBean.setStrBillNo(obj.getStrBillNo());
	                	objBean.setDteBillDate(obj.getDteBillDate());
	                	objBean.setDteVoidedDate(obj.getDteVoidedDate());
	                	objBean.setStrEntryTime(obj.getStrEntryTime());
	                	objBean.setStrVoidedTime(obj.getStrVoidedTime());
	                	objBean.setDblModifiedAmount(obj.getDblModifiedAmount());
	                	objBean.setStrUserEdited(obj.getStrUserEdited());
	                	objBean.setStrReasonName(obj.getStrReasonName());
	                	listArr.add(objBean);
	                   
	                }
	               
					
				}
				else
				{
					
					StringBuilder sqlFilter = new StringBuilder();
					sbSql.setLength(0);
					sbSql.append("select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y') as VoidedDate,"
							+ "TIME_FORMAT(time(a.dteBillDate),'%h:%i') As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strItemName,"
							+ "b.intQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'') "
							+ " from tblvoidbillhd a ");
					if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and "
								+ "strUserCreated='"
								+ userCode
								+ "' and strreasonCode='"
								+ reasonCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and  "
								+ "strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strUserCreated='"
								+ userCode
								+ "' "
								+ "and strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' "
								+ "and strUserCreated='"
								+ userCode
								+ "'  and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strreasonCode='"
								+ reasonCode
								+ "' "
								+ "and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strUserCreated='"
								+ userCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else {
						sbSql.append(" where a.strTransType='AOVB' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");
					}
					sbSql.append(" group by a.strBillNo,b.strItemCode");
	                
	                sbSqlMod.setLength(0);
					sbSqlMod.append("select a.strBillNo, DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y')  as VoidedDate,"
							+ "Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime,b.strModifierName,"
							+ "b.dblQuantity,b.dblAmount ,a.strReasonName,a.strUserEdited "
							+ "from tblvoidbillhd a,tblvoidmodifierdtl b where a.strBillNo=b.strBillNo ");

					if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and b.strPosCode='"
								+ strPOSCode
								+ "' "
								+ "and a.strUserCreated='"
								+ userCode
								+ "' and a.strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' and "
								+ "Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strUserCreated='"
								+ userCode
								+ "' "
								+ "and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strreasonCode='"
								+ reasonCode
								+ "' "
								+ "and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' "
								+ "and a.strUserCreated='"
								+ userCode
								+ "' and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='"
								+ strPOSCode
								+ "' "
								+ "and a.strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strUserCreated='"
								+ userCode
								+ "' "
								+ "and a.strreasonCode='"
								+ reasonCode
								+ "' and Date(a.dteModifyVoidBill)"
								+ " between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "'");
					} else {
						sbSqlMod.append(" and a.strTransType='AOVB' and Date(a.dteModifyVoidBill) "
								+ "between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "'");
					}
					sbSqlMod.append(" group by a.strBillNo,b.strModifierCode");
					StringBuilder sql = new StringBuilder();
				    sql.append("SELECT strBillNo, BillDate,  VoidedDate, EntryTime, VoidedTime,strItemName,intQuantity,BillAmount,Reason,"
				    		+ "UserEdited, strRemark"
							+ "from( "					
				    		+ sbSql.toString() + " union " + sbSqlMod.toString()
				    		+")d");
				    sqlFilter.setLength(0);
				    if (strSorting.equalsIgnoreCase("BILL"))
                    {
				    	
				    	sqlFilter.append(" order by a.strBillNo");
				    	
                    }
                    else
                    {
                    	sqlFilter.append(" order by b.dblAmount");
                    }
				    sql.append(sqlFilter);
				    List listData = objBaseService.funGetList(sql, "sql");
				    if(listData.size()>0)
	                {
				    	for(int i=0;i<listData.size();i++)
				    	{	
	                    Object[] obj = (Object[]) listData.get(i);
	                    clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                    objBean.setStrBillNo(obj[0].toString());
	                    objBean.setDteBillDate(obj[1].toString());
	                    objBean.setDteVoidedDate(obj[2].toString());
	                    objBean.setStrEntryTime(obj[3].toString());
	                    objBean.setStrVoidedTime(obj[4].toString());
	                    objBean.setStrItemName(obj[5].toString());
	                    objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
	                    objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
	                    objBean.setStrUserEdited(obj[8].toString());
	                    objBean.setStrReasonName(obj[9].toString());
	                    objBean.setStrRemark(obj[10].toString());
	                    listArr.add(objBean);
	                    
	                    sumQty = sumQty + Double.parseDouble(obj[6].toString());
	                    sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
	                }
				 }
				 
			}
			break;

			case "Line Void":

				sbSql.setLength(0);
				sbSql.append("select b.strPosName,DATE_FORMAT(Date(a.dteDateCreated),'%d-%m-%Y'),TIME_FORMAT(time(a.dteDateCreated),'%h:%i') "
                    + " ,a.strItemName,a.dblItemQuantity,a.dblAmount,a.strKOTNo,a.strUserCreated  "
                    + " from tbllinevoid a,tblposmaster b "
                    + " where a.strPosCode=b.strPosCode ");

				if (!"All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strUserCreated='" + userCode + "' "
							+ "and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName)
						&& "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strUserCreated='" + userCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and  Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				}
				if ("Amount".equalsIgnoreCase(strSorting))
	            {
	                sbSql.append(" order by a.dblAmount");
	            }
			
				List listData =objBaseService.funGetList(sbSql,"sql");
				if(listData.size()>0)
	            {
					for(int i=0;i<listData.size();i++)
					{
	                Object[] obj = (Object[]) listData.get(i);
	                clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                objBean.setStrPosName(obj[0].toString());
	                objBean.setDteVoidedDate(obj[1].toString());
	                objBean.setStrVoidedTime(obj[2].toString());
	                objBean.setStrItemName(obj[3].toString());
	                objBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
	                objBean.setBillAmt(Double.parseDouble(obj[5].toString()));
	                objBean.setStrKOTNo(obj[6].toString());
	                objBean.setStrUserEdited(obj[7].toString());
	                listArr.add(objBean);
	                
	                sumQty = sumQty + Double.parseDouble(obj[4].toString());
	                sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
					}
	            }
				
			
			break;
				
			case "Voided KOT":
				sbSqlMod = new StringBuilder();

				sbSql.setLength(0);
				if (strReportType.equalsIgnoreCase("Summary")) 
				{
					
		            double pax = 0.00;
		            
		            resMap.put("ColHeader", listArrColHeader);

					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo,a.intPaxNo,"
                        + " sum(a.dblAmount),c.strReasonName,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
                        + " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
                        + ",tblreasonmaster c,tblposmaster d,tbltablemaster e "
                        + " where a.strreasonCode=c.strreasonCode "
                        + " and a.strPOSCode=d.strPOSCode and a.strTableNo=e.strTableNo ");
					if ("Item Void".equalsIgnoreCase(strType))
		            {
		                sbSql.append(" and a.strVoidBillType ='" + strType + "' ");
		            }
		            else if ("Full Void".equalsIgnoreCase(strType))
		            {
		                sbSql.append(" and a.strVoidBillType ='Full KOT Void' ");
		            }
		            else
		            {
		                sbSql.append(" and (a.strVoidBillType = 'Full KOT Void' or a.strVoidBillType = 'ITEM VOID' ) ");
		            }
					
					if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					}
					sbSql.append(" Group By a.strPOSCode,a.strTableNo,b.strWShortName,a.strKOTNo,a.intPaxNo,"
	                        + "c.strReasonName,a.strUserCreated");
					if ("Amount".equalsIgnoreCase(strSorting))
	                {
	                    sbSql.append(" order by sum(a.dblAmount)");
	                }
					
					
					List list = objBaseService.funGetList(sbSql,"sql");
	                if(list.size()>0)
	                {
	                	for(int i=0;i<list.size();i++)
	                	{
	                	clsBillItemDtlBean objBean = new clsBillItemDtlBean();	
	                    Object[] obj = (Object[]) list.get(i);
	                    objBean.setStrPosName(obj[0].toString());
	                    objBean.setStrTableName(obj[1].toString());
	                    objBean.setStrWaiterName(obj[2].toString());
	                    objBean.setStrKOTNo(obj[3].toString());
	                    objBean.setIntPaxNo(Integer.parseInt(obj[4].toString()));
	                    objBean.setDblAmount(Double.parseDouble(obj[5].toString()));
	                    objBean.setStrReasonName(obj[6].toString());
	                    objBean.setStrUserCreated(obj[7].toString());
	                    objBean.setDteDateCreated(obj[8].toString());
	                    objBean.setStrRemark(obj[9].toString());
	                    listArr.add(objBean);
	                   
	                    pax = pax + Integer.parseInt(obj[4].toString());
	                    sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
	                	}
	                }
	               
				}
				else
				{
					
		            double pax = 0.00;

					
					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo "
							+ " ,a.strItemName,a.intPaxNo,a.dblItemQuantity,a.dblAmount,c.strReasonName "
							+ " ,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
							+ " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
							+ " ,tblreasonmaster c,tblposmaster d,tbltablemaster e "
							+ " where a.strreasonCode=c.strreasonCode and a.strPOSCode=d.strPOSCode "
							+ " and a.strTableNo=e.strTableNo ");
					if ("Item Void".equalsIgnoreCase(strType))
	                {
	                    sbSql.append("and a.strVoidBillType ='" + strType + "' ");
	                }
	                else if ("Full Void".equalsIgnoreCase(strType))
	                {
	                    sbSql.append("and a.strVoidBillType ='Full KOT Void' ");
	                }
	                else
	                {
	                    sbSql.append("and (a.strVoidBillType = 'Full KOT Void' or a.strVoidBillType = 'ITEM VOID' ) ");
	                }

					if (!"All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate
								+ "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName)
							&& "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode
								+ "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					}
					
					if ("Amount".equalsIgnoreCase(strSorting))
	                {
	                    sbSql.append(" order by a.dblAmount");
	                }
					
					List listOfData= objBaseService.funGetList(sbSql,"sql");
	                if(listOfData.size()>0)
	                {
	                	for(int i=0;i<listOfData.size();i++)
	                	{
	                    Object[] obj = (Object[]) listOfData.get(i);
	                    clsBillItemDtlBean objBean = new clsBillItemDtlBean();
	                    objBean.setStrPosName(obj[0].toString());
	                    objBean.setStrTableName(obj[1].toString());
	                    objBean.setStrWaiterName(obj[2].toString());
	                    objBean.setStrKOTNo(obj[3].toString());
	                    objBean.setStrItemName(obj[4].toString());
	                    objBean.setIntPaxNo(Integer.parseInt(obj[5].toString()));
	                    objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
	                    objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
	                    objBean.setStrReasonName(obj[8].toString());
	                    objBean.setStrUserCreated(obj[9].toString());
	                    objBean.setDteDateCreated(obj[10].toString());
	                    objBean.setStrRemark(obj[11].toString());
	                    listArr.add(objBean);
	                    
	                    pax = pax + Integer.parseInt(obj[5].toString());
	                    sumQty = sumQty + Double.parseDouble(obj[6].toString());
	                    sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
	                	}
	                }
	               
				
				}
				break;
				
			case "Time Audit":	
				
				sbSql.setLength(0);
				sbSql.append("SELECT a.strbillno, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate "
                    + ",TIME_FORMAT(TIME(a.dteBillDate),'%h:%i') AS BillTime, TIME_FORMAT(TIME(b.dteBillDate),'%h:%i') AS KOTTime "
                    + ",TIME_FORMAT(TIME(a.dteSettleDate),'%h:%i')SettleTime, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
                    + ",DATE_FORMAT(DATE(a.dteSettleDate),'%d-%m-%Y')SettleDate,a.strUserCreated,a.strUserEdited, IFNULL(a.strRemarks,'') "
                    + ",concat(SEC_TO_TIME(TIMESTAMPDIFF(second,a.dteBillDate,a.dteSettleDate)),'') AS diffInBillnSettled  "
                    + "from tblbillhd a, tblbilldtl b where a.strBillNo=b.strBillNo ");

				if (!"All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode
							+ "' and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName)
						&& "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and Date(a.dteBillDate) between '"
							+ fromDate + "' and '" + toDate + "'");
				}
				sbSql.append(" group by a.strBillNo");

				List listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) {

					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						clsBillItemDtlBean objBean = new clsBillItemDtlBean();
						objBean.setStrBillNo(obj[0].toString());
						objBean.setDteBillDate(obj[1].toString());
						objBean.setStrKotTime(obj[3].toString());
						objBean.setStrEntryTime(obj[2].toString());
						objBean.setStrVoidedTime(obj[4].toString());
						objBean.setStrDifference(obj[10].toString());
						objBean.setStrUserCreated(obj[7].toString());
						objBean.setStrUserEdited(obj[8].toString());
						objBean.setStrRemark(obj[9].toString());
						
						listArr.add(objBean);

					}

				}

				sbSql.setLength(0);
				sbSql.append("SELECT a.strbillno, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate "
                    + ",TIME_FORMAT(TIME(a.dteBillDate),'%h:%i') AS BillTime, TIME_FORMAT(TIME(b.dteBillDate),'%h:%i') AS KOTTime "
                    + ",TIME_FORMAT(TIME(a.dteSettleDate),'%h:%i')SettleTime, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
                    + ",DATE_FORMAT(DATE(a.dteSettleDate),'%d-%m-%Y')SettleDate,a.strUserCreated,a.strUserEdited, IFNULL(a.strRemarks,'') "
                    + ",concat(SEC_TO_TIME(TIMESTAMPDIFF(second,a.dteBillDate,a.dteSettleDate)),'') AS diffInBillnSettled  "
                    + "from tblqbillhd a, tblqbilldtl b where a.strBillNo=b.strBillNo ");

				if (!"All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode
							+ "' and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName)
						&& "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName)
						&& !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode
							+ "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and Date(a.dteBillDate) between '"
							+ fromDate + "' and '" + toDate + "'");
				}
				sbSql.append(" group by a.strBillNo");

				listSql = objBaseService.funGetList(sbSql,"sql");

				if (listSql.size() > 0) {

					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						clsBillItemDtlBean objBean = new clsBillItemDtlBean();
						objBean.setStrBillNo(obj[0].toString());
						objBean.setDteBillDate(obj[1].toString());
						objBean.setStrKotTime(obj[3].toString());
						objBean.setStrEntryTime(obj[2].toString());
						objBean.setStrVoidedTime(obj[4].toString());
						objBean.setStrDifference(obj[10].toString());
						objBean.setStrUserCreated(obj[7].toString());
						objBean.setStrUserEdited(obj[8].toString());
						objBean.setStrRemark(obj[9].toString());
						
						listArr.add(objBean);
					}

				}
				
				break;
				
			case "KOT Analysis":
				StringBuilder sbSqlLive = new StringBuilder();
				StringBuilder sbSqlQFile = new StringBuilder();
				StringBuilder sbFilters = new StringBuilder();
				int noOfKOTs = 0;
	            List<clsKOTAnalysisBean> listOfKOTAnalysis = new LinkedList<clsKOTAnalysisBean>();
	            String operation = "Billed KOT";
	            
				if (strReportType.equalsIgnoreCase("Summary")) 
				{
					
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);

					if (!"All".equalsIgnoreCase(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
		            }
		            if (!"All".equalsIgnoreCase(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }

		            //live billed KOTs
		            sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
		                    + "from tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and b.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>0 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

		            sbSqlLive.append(sbFilters);

		            sbSqlLive.append("group by a.strBillNo,b.strKOTNo "
		                    + "order by a.strBillNo,b.strKOTNo");

		            List listBilledKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listBilledKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listBilledKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[1].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[3].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[4].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[7].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		           

		            //Q billed KOTs
		            sbSqlQFile.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
		                    + "from tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and b.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>0 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
		            sbSqlQFile.append(sbFilters);
		            sbSqlQFile.append("group by a.strBillNo,b.strKOTNo "
		                    + "order by a.strBillNo,b.strKOTNo");

		            listBilledKOTs = objBaseService.funGetList(sbSqlQFile,"sql");
		            if(listBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listBilledKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listBilledKOTs.get(i);
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[1].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[3].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[4].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[7].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            
		            //voided billed KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }
		            sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo,b.strTransType "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
		                    + ",b.strReasonName,b.strRemarks "
		                    + "from tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and a.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>2 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strBillNo,b.strKOTNo "
		                    + "order by a.strBillNo,b.strKOTNo");
		            List listVoidedBilledKOTs =objBaseService.funGetList(sbSqlLive,"sql");
		            if(listVoidedBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listVoidedBilledKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listVoidedBilledKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                operation = obj[1].toString();
		                if (obj[1].toString().equalsIgnoreCase("VB"))
		                {
		                    operation = "Void Bill";
		                }
		                if (obj[1].toString().equalsIgnoreCase("USBill"))
		                {
		                    operation = "Unseetled Bill";
		                }
		                if (obj[1].toString().equalsIgnoreCase("MB"))
		                {
		                    operation = "Modified Bill";
		                }

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[4].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[5].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[6].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[7].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[8].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[9].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[10].toString());//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            
		            //line voided KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "Line Void";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode= '"+strPOSCode +"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }

		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Line Void' strOperationType "
		                    + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
		                    + "from tbllinevoid a "
		                    + "where LENGTH(a.strKOTNo)>2 "
		                    + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo "
		                    + "order by a.strKOTNo");
		            List listLineVoidedKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listLineVoidedKOTs.size()>0)
		            {
		            	for(int i=0;i<listLineVoidedKOTs.size();i++)
		            	{	
		            	Object[] obj = (Object[]) listLineVoidedKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName("");//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName("");//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		           

		            //voided KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "Void KOT";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode +"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }
		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,a.strType strOperationType "
		                    + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
		                    + ",b.strTableName,c.strWShortName,d.strReasonName,a.strRemark "
		                    + "from tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d "
		                    + "where a.strTableNo=b.strTableNo  "
		                    + "and a.strWaiterNo=c.strWaiterNo "
		                    + "and a.strReasonCode=d.strReasonCode "
		                    + "and LENGTH(a.strKOTNo)>2 "
		                    + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo,a.strType "
		                    + "order by a.strKOTNo");
		            List listVoidedKOT =objBaseService.funGetList(sbSqlLive,"sql");
		            if(listVoidedKOT.size()>0)
		            {
		            	for(int i=0;i<listVoidedKOT.size();i++)
		            	{
		            	Object[] obj = (Object[]) listVoidedKOT.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                if (obj[1].toString().equalsIgnoreCase("VKot"))
		                {
		                    operation = "Void KOT";
		                }
		                else if (obj[1].toString().equalsIgnoreCase("MVKot"))
		                {
		                    operation = "Move KOT";
		                }
		                else
		                {
		                    operation = "Void KOT";
		                }
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[4].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[5].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[6].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[7].toString());//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		          
		            //NC KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "NC KOT";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode +"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }

		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'NC KOT' strOperationType "
		                    + ",DATE_FORMAT(date(a.dteNCKOTDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteNCKOTDate),'%h:%i')tmeKOTTime "
		                    + ",a.strTableNo,b.strTableName,c.strReasonCode,c.strReasonName,a.strRemark "
		                    + "from tblnonchargablekot a,tbltablemaster b,tblreasonmaster c "
		                    + "where LENGTH(a.strKOTNo)>2 "
		                    + "and a.strTableNo=b.strTableNo "
		                    + "and a.strReasonCode=c.strReasonCode "
		                    + "and Date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo "
		                    + "order by a.strKOTNo");
		            List listNCKOTs =objBaseService.funGetList(sbSqlLive,"sql");
		            if(listNCKOTs.size()>0)
		            {
		            	for(int i=0;i<listNCKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listNCKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName("");//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[7].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[8].toString());//remarks   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		          

		            //sorting
		            Comparator<clsKOTAnalysisBean> kotComaparator = new Comparator<clsKOTAnalysisBean>()
		            {

		                @Override
		                public int compare(clsKOTAnalysisBean o1, clsKOTAnalysisBean o2)
		                {
		                    return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
		                }
		            };
		            Collections.sort(listOfKOTAnalysis, kotComaparator);
		            //sorting//

		            //fill table data
		            for (clsKOTAnalysisBean objKOTAnalysisBean : listOfKOTAnalysis)
		            {
		            	clsKOTAnalysisBean objBean = new clsKOTAnalysisBean();
		            	objBean.setStrKOTNo(objKOTAnalysisBean.getStrKOTNo());
		            	objBean.setStrOperationType(objKOTAnalysisBean.getStrOperationType());
		            	objBean.setDteKOTDate(objKOTAnalysisBean.getDteKOTDate());
		            	objBean.setTmeKOTTime(objKOTAnalysisBean.getTmeKOTTime());
		            	objBean.setStrBillNo( objKOTAnalysisBean.getStrBillNo());
		            	objBean.setStrTableName(objKOTAnalysisBean.getStrTableName());
		            	objBean.setStrWaiterName(objKOTAnalysisBean.getStrWaiterName());
		            	objBean.setStrReasonName(objKOTAnalysisBean.getStrReasonName());
		            	objBean.setStrRemarks(objKOTAnalysisBean.getStrRemarks());
		            	listArr.add(objBean);
		               
		                noOfKOTs++;
		            }
		           
				}
				else
				{
					
					
					listOfKOTAnalysis = new LinkedList<clsKOTAnalysisBean>();
					double totalQuantity = 0.0;

		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "Billed KOT";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode +"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }

		            //live billed KOTs
		            sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName"
		                    + ",b.strItemCode,b.strItemName,sum(b.dblQuantity) "
		                    + "from tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and b.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>0 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

		            sbSqlLive.append(sbFilters);

		            sbSqlLive.append("group by a.strBillNo,b.strKOTNo,b.strItemCode "
		                    + "order by a.strBillNo,b.strKOTNo");

		            List listBilledKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listBilledKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listBilledKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[1].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[3].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[4].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[7].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks   
		                objKOTAnalysisBean.setStrItemCode(obj[8].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[9].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));//itemQty   

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		           
		            //Q billed KOTs
		            sbSqlQFile.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName"
		                    + ",b.strItemCode,b.strItemName,sum(b.dblQuantity) "
		                    + "from tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and b.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>0 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
		            sbSqlQFile.append(sbFilters);
		            sbSqlQFile.append("group by a.strBillNo,b.strKOTNo,b.strItemCode "
		                    + "order by a.strBillNo,b.strKOTNo");

		            listBilledKOTs = objBaseService.funGetList(sbSqlQFile,"sql");
		            if(listBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listBilledKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listBilledKOTs.get(i);
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[1].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[3].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[4].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[7].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks 
		                objKOTAnalysisBean.setStrItemCode(obj[8].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[9].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));//itemQty 

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            
		            //voided billed KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }
		            sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo,b.strTransType "
		                    + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
		                    + ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
		                    + ",b.strReasonName,b.strRemarks"
		                    + ",b.strItemCode,b.strItemName,sum(b.intQuantity) "
		                    + "from tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d "
		                    + "where a.strBillNo=b.strBillNo  "
		                    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		                    + "and a.strTableNo=c.strTableNo "
		                    + "and a.strWaiterNo=d.strWaiterNo "
		                    + "and LENGTH(b.strKOTNo)>2 "
		                    + "and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strBillNo,b.strKOTNo,b.strItemCode "
		                    + "order by a.strBillNo,b.strKOTNo");
		            List listVoidedBilledKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listVoidedBilledKOTs.size()>0)
		            {
		            	for(int i=0;i<listVoidedBilledKOTs.size();i++)
		            	{	
		            	Object[] obj = (Object[]) listVoidedBilledKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                operation = obj[1].toString();
		                if (obj[1].toString().equalsIgnoreCase("VB"))
		                {
		                    operation = "Void Bill";
		                }
		                if (obj[1].toString().equalsIgnoreCase("USBill"))
		                {
		                    operation = "Unseetled Bill";
		                }
		                if (obj[1].toString().equalsIgnoreCase("MB"))
		                {
		                    operation = "Modified Bill";
		                }

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo(obj[4].toString());//billNo
		                objKOTAnalysisBean.setStrTableNo(obj[5].toString());//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[6].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo(obj[7].toString());//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[8].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[9].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[10].toString());//remarks  
		                objKOTAnalysisBean.setStrItemCode(obj[11].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[12].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[13].toString()));//itemQty 

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            
		            //line voided KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "Line Void";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }

		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Line Void' strOperationType "
		                    + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime"
		                    + ",a.strItemCode,a.strItemName,sum(a.dblItemQuantity) "
		                    + "from tbllinevoid a "
		                    + "where LENGTH(a.strKOTNo)>2 "
		                    + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo,a.strItemCode "
		                    + "order by a.strKOTNo");
		            List listLineVoidedKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listLineVoidedKOTs.size()>0)
		            {
		            	for(int i=0;i<listLineVoidedKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listLineVoidedKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName("");//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName("");//waiterName
		                objKOTAnalysisBean.setStrReasonName("");//reason
		                objKOTAnalysisBean.setStrRemarks("");//remarks  
		                objKOTAnalysisBean.setStrItemCode(obj[4].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[5].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[6].toString()));//itemQty 

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		           

		            //voided KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "Void KOT";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }
		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Void KOT' strOperationType "
		                    + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
		                    + ",b.strTableName,c.strWShortName,d.strReasonName,a.strRemark"
		                    + ",a.strItemCode,a.strItemName,sum(a.dblItemQuantity) "
		                    + "from tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d "
		                    + "where a.strTableNo=b.strTableNo  "
		                    + "and a.strWaiterNo=c.strWaiterNo "
		                    + "and a.strReasonCode=d.strReasonCode "
		                    + "and LENGTH(a.strKOTNo)>2 "
		                    + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo,a.strItemCode "
		                    + "order by a.strKOTNo");
		            List listVoidedKOT = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listVoidedKOT.size()>0)
		            {
		            	for(int i=0;i<listVoidedKOT.size();i++)
		            	{
		            	Object[] obj = (Object[]) listVoidedKOT.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[4].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName(obj[5].toString());//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[6].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[7].toString());//remarks   
		                objKOTAnalysisBean.setStrItemCode(obj[8].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[9].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));//itemQty 

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            

		            //NC KOTs
		            sbSqlLive.setLength(0);
		            sbSqlQFile.setLength(0);
		            sbFilters.setLength(0);
		            operation = "NC KOT";

		            if (!"All".equals(posName))
		            {
		                sbFilters.append("and a.strPOSCode='"+ strPOSCode +"' ");
		            }
		            if (!"All".equals(userName))
		            {
		                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
		            }
		            if (!reasonName.equalsIgnoreCase("All"))
		            {
		                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
		            }

		            sbSqlLive.append("select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'NC KOT' strOperationType "
		                    + ",DATE_FORMAT(date(a.dteNCKOTDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteNCKOTDate),'%h:%i')tmeKOTTime "
		                    + ",a.strTableNo,b.strTableName,c.strReasonCode,c.strReasonName,a.strRemark"
		                    + ",a.strItemCode,d.strItemName,sum(a.dblQuantity) "
		                    + "from tblnonchargablekot a,tbltablemaster b,tblreasonmaster c,tblitemmaster d "
		                    + "where LENGTH(a.strKOTNo)>2 "
		                    + "and a.strTableNo=b.strTableNo "
		                    + "and a.strReasonCode=c.strReasonCode "
		                    + "and a.strItemCode=d.strItemCode "
		                    + "and Date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' ");
		            sbSqlLive.append(sbFilters);
		            sbSqlLive.append("group by a.strKOTNo,a.strItemCode "
		                    + "order by a.strKOTNo");
		            List listNCKOTs = objBaseService.funGetList(sbSqlLive,"sql");
		            if(listNCKOTs.size()>0)
		            {
		            	for(int i=0;i<listNCKOTs.size();i++)
		            	{
		            	Object[] obj = (Object[]) listNCKOTs.get(i);	
		                clsKOTAnalysisBean objKOTAnalysisBean = new clsKOTAnalysisBean();

		                objKOTAnalysisBean.setStrKOTNo(obj[0].toString());//kotNO
		                objKOTAnalysisBean.setStrOperationType(operation);//operation
		                objKOTAnalysisBean.setDteKOTDate(obj[2].toString());//date
		                objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());//time
		                objKOTAnalysisBean.setStrBillNo("");//billNo
		                objKOTAnalysisBean.setStrTableNo("");//tableNO
		                objKOTAnalysisBean.setStrTableName(obj[5].toString());//tableName
		                objKOTAnalysisBean.setStrWaiterNo("");//waiterNo
		                objKOTAnalysisBean.setStrWaiterName("");//waiterName
		                objKOTAnalysisBean.setStrReasonName(obj[7].toString());//reason
		                objKOTAnalysisBean.setStrRemarks(obj[8].toString());//remarks   
		                objKOTAnalysisBean.setStrItemCode(obj[9].toString());//itemCode   
		                objKOTAnalysisBean.setStrItemName(obj[10].toString());//itemName   
		                objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[11].toString()));//itemQty 

		                listOfKOTAnalysis.add(objKOTAnalysisBean);
		            	}
		            }
		            

		            //sorting
		            Comparator<clsKOTAnalysisBean> kotComaparator = new Comparator<clsKOTAnalysisBean>()
		            {

		                @Override
		                public int compare(clsKOTAnalysisBean o1, clsKOTAnalysisBean o2)
		                {
		                    return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
		                }
		            };
		            Collections.sort(listOfKOTAnalysis, kotComaparator);
		            //sorting//

		            //fill table data
		            for (clsKOTAnalysisBean objKOTAnalysisBean : listOfKOTAnalysis)
		            {
		            	clsKOTAnalysisBean objBean = new clsKOTAnalysisBean();
		            	objBean.setStrKOTNo(objKOTAnalysisBean.getStrKOTNo());
		            	objBean.setStrOperationType(objKOTAnalysisBean.getStrOperationType());
		            	objBean.setDteKOTDate(objKOTAnalysisBean.getDteKOTDate());
		            	objBean.setTmeKOTTime(objKOTAnalysisBean.getTmeKOTTime());
		            	objBean.setStrBillNo(objKOTAnalysisBean.getStrBillNo());
		            	objBean.setStrItemName(objKOTAnalysisBean.getStrItemName());
		            	objBean.setDblQty(objKOTAnalysisBean.getDblQty());
		            	objBean.setStrTableName(objKOTAnalysisBean.getStrTableName());
		            	objBean.setStrWaiterName(objKOTAnalysisBean.getStrWaiterName());
		            	objBean.setStrReasonName( objKOTAnalysisBean.getStrReasonName());
		            	objBean.setStrRemarks(objKOTAnalysisBean.getStrRemarks());
		            	listArr.add(objBean);
		               

		                totalQuantity += objKOTAnalysisBean.getDblQty();
		            }
		            

				}
				
				
				break;
				
			case "Moved KOT":
				int pax = 0;
				if (strReportType.equalsIgnoreCase("Summary")) 
				{
				
                sumQty = 0.00;
                sumTotalAmt = 0.00;
                pax = 0;

                sbSql.setLength(0);
                sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo,a.intPaxNo,"
                        + " sum(a.dblAmount),c.strReasonName,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y') "
                        + " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
                        + ",tblreasonmaster c,tblposmaster d,tbltablemaster e "
                        + " where a.strreasonCode=c.strreasonCode "
                        + " and a.strPOSCode=d.strPOSCode and a.strTableNo=e.strTableNo "
                        + " and a.strType='MVKot' ");

                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
                {
                    sbSql.append(" and a.strPOSCode='"+strPOSCode +"' and a.strUserCreated='" + userCode + "' "
                            + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
                            + "and a.strreasonCode='" + reasonCode + "'");
                }
                else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
                {
                    sbSql.append(" and a.strPOSCode='"+strPOSCode +"' and Date(a.dteDateCreated) between '"
                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
                }
                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
                {
                    sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
                }
                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
                {
                    sbSql.append(" and a.strPOSCode='"+strPOSCode +"' and Date(a.dteDateCreated) between '"
                            + fromDate + "' and '" + toDate + "'");
                }
                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
                {
                    sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
                }
                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
                {
                    sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
                            + fromDate + "' and '" + toDate + "'");
                }
                else
                {
                    sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
                }
                sbSql.append(" Group By a.strPOSCode,a.strTableNo,b.strWShortName,a.strKOTNo,a.intPaxNo,"
                        + "c.strReasonName,a.strUserCreated");
                //System.out.println(sbSql.toString());
                List list = objBaseService.funGetList(sbSql,"sql");
                if(list.size()>0)
                {
                	for(int i=0;i<list.size();i++)
                	{
                		Object[] obj = (Object[]) list.get(i);
                		clsBillItemDtlBean objBean = new clsBillItemDtlBean();
                		objBean.setStrPosName(obj[0].toString());
                		objBean.setStrTableName(obj[1].toString());
                		objBean.setStrWaiterName(obj[2].toString());
                		objBean.setStrKOTNo(obj[3].toString());
                		objBean.setIntPaxNo(Integer.parseInt(obj[4].toString()));
                		objBean.setDblAmount(Double.parseDouble(obj[5].toString()));
                		objBean.setStrReasonName(obj[6].toString());
                		objBean.setStrUserCreated(obj[7].toString());
                		objBean.setDteDateCreated(obj[8].toString());
                		listArr.add(objBean);
                		pax = pax + Integer.parseInt(obj[4].toString());
                		sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
                	}
                }
                resMap.put("listArr", listArr);
				}
				else
				{
					listArrColHeader.add("POS");
					listArrColHeader.add("Table");
					listArrColHeader.add("Waiter");
					listArrColHeader.add("KOT No");
					listArrColHeader.add("Item Name");
					listArrColHeader.add("Pax");
					listArrColHeader.add("Qty");
					listArrColHeader.add("Amount");
					listArrColHeader.add("Reason");
					listArrColHeader.add("User Created");
					listArrColHeader.add("Date Created");
					listArrColHeader.add("Remarks");
					resMap.put("ColHeader", listArrColHeader);
		           
		            sbSql.setLength(0);
		            sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo "
		                    + " ,a.strItemName,a.intPaxNo,a.dblItemQuantity,a.dblAmount,c.strReasonName "
		                    + " ,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
		                    + " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
		                    + " ,tblreasonmaster c,tblposmaster d,tbltablemaster e "
		                    + " where a.strreasonCode=c.strreasonCode and a.strPOSCode=d.strPOSCode "
		                    + " and a.strTableNo=e.strTableNo and a.strType='MVKot' ");

		                if (!"All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strPOSCode='"+strPOSCode+"' and a.strUserCreated='" + userCode + "' "
		                            + "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
		                            + "and a.strreasonCode='" + reasonCode + "'");
		                }
		                else if (!"All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strPOSCode='"+strPOSCode+"' and Date(a.dteDateCreated) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
		                }
		                else if (!"All".equals(posName) && "All".equals(userName) && "All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strPOSCode='"+strPOSCode+"' and Date(a.dteDateCreated) between '"
		                            + fromDate + "' and '" + toDate + "'");
		                }
		                else if ("All".equals(posName) && !"All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
		                            + fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
		                }
		                else if ("All".equals(posName) && "All".equals(userName) && !"All".equals(reasonName))
		                {
		                    sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
		                            + fromDate + "' and '" + toDate + "'");
		                }
		                else
		                {
		                    sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
		                }

		               List list = objBaseService.funGetList(sbSql,"sql");
		               if(list.size()>0)
		                {
		                   for(int i=0;i<list.size();i++)
		                   {
		            	   Object[] obj = (Object[])list.get(i);
		            	   clsBillItemDtlBean objBean = new clsBillItemDtlBean();
		            	   objBean.setStrPosName(obj[0].toString());
		            	   objBean.setStrTableName(obj[1].toString());
		            	   objBean.setStrWaiterName(obj[2].toString());
		            	   objBean.setStrKOTNo(obj[3].toString());
		            	   objBean.setStrItemName(obj[4].toString());
		            	   objBean.setIntPaxNo(Integer.parseInt(obj[5].toString()));
		            	   objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
		            	   objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
		            	   objBean.setStrReasonName(obj[8].toString());
		            	   objBean.setStrUserCreated(obj[9].toString());
		            	   objBean.setDteDateCreated(obj[10].toString());
		            	   objBean.setStrRemark(obj[11].toString());	
		            	   listArr.add(objBean);
		                   
		                    pax = pax + Integer.parseInt(obj[5].toString());
		                    sumQty = sumQty + Double.parseDouble(obj[6].toString());
		                    sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
		                   }
		                }
		               
				}          
				
				break;
				
			case "Waiter Audit":
				
				Map<String, clsWaiterAnalysisBean> mapWaiterWise = new HashMap();
			    Map<String, String> mapKotsSave = new HashMap();
				List<clsWaiterAnalysisBean> listOfWaiterAnalysis = new LinkedList<clsWaiterAnalysisBean>();
				sbSqlLive = new StringBuilder();
				sbSqlQFile = new StringBuilder();
				sbFilters = new StringBuilder();
		        
	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbFilters.setLength(0);
	            operation = "Billed KOT";

	            if (!"All".equals(posName))
	            {
	                sbFilters.append("and a.strPOSCode='"+strPOSCode +"' ");
	            }
	            if (!"All".equals(userName))
	            {
	                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
	            }
	            if (!reasonCode.equalsIgnoreCase("All"))
	            {
	                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
	            }

	            //live billed KOTs
	            sbSqlLive.append("SELECT d.strWaiterNo,d.strWShortName,d.strWFullName,b.strKOTNo\n"
	                    + "FROM tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d\n"
	                    + "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) AND a.strTableNo=c.strTableNo "
	                    + "AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n ");

	            sbSqlLive.append(sbFilters);

	            sbSqlLive.append("GROUP BY b.strWaiterNo,b.strKOTNo\n"
	                    + "order by d.strWShortName,b.strKOTNo");

	            List listBilledKOTs = objBaseService.funGetList(sbSqlLive,"sql");
	            if(listBilledKOTs.size()>0)
	            {
	            	for(int i=0;i<listBilledKOTs.size();i++)
	            	{
	            	Object[] obj = (Object[]) listBilledKOTs.get(i);	
	                String waiterNo = obj[0].toString();
	                String kotNo = obj[3].toString();

	                if (mapWaiterWise.containsKey(waiterNo))
	                {
	                    clsWaiterAnalysisBean objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
	                    if (!mapKotsSave.containsKey(kotNo))
	                    {
	                        objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
	                    }
	                }
	                else
	                {
	                    clsWaiterAnalysisBean objWaiterAnalysisBean = new clsWaiterAnalysisBean();

	                    objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
	                    objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
	                    objWaiterAnalysisBean.setNoOfKot(1);
	                    objWaiterAnalysisBean.setNoOfVoidKot(0);
	                    objWaiterAnalysisBean.setNoOfMoveKot(0);
	                    listOfWaiterAnalysis.add(objWaiterAnalysisBean);
	                    mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
	                    mapKotsSave.put(kotNo, kotNo);
	                }
	            	}
	            }
	            
	            //Q billed KOTs
	            sbSqlQFile.append("SELECT d.strWaiterNo,d.strWShortName,d.strWFullName,b.strKOTNo\n"
	                    + "FROM tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d\n"
	                    + "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) AND a.strTableNo=c.strTableNo "
	                    + "AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n ");

	            sbSqlQFile.append(sbFilters);
	            sbSqlQFile.append("GROUP BY b.strWaiterNo,b.strKOTNo\n"
	                    + "order by d.strWShortName,b.strKOTNo");

	            listBilledKOTs = objBaseService.funGetList(sbSqlQFile,"sql");
	            if(listBilledKOTs.size()>0)
	            {
	            	for(int i=0;i<listBilledKOTs.size();i++)
	            	{
	            	Object[] obj = (Object[]) listBilledKOTs.get(i);	
	                String waiterNo = obj[0].toString();
	                String kotNo = obj[3].toString();

	                if (mapWaiterWise.containsKey(waiterNo))
	                {
	                    clsWaiterAnalysisBean objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
	                    if (!mapKotsSave.containsKey(kotNo))
	                    {
	                        objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
	                    }
	                }
	                else
	                {
	                    clsWaiterAnalysisBean objWaiterAnalysisBean = new clsWaiterAnalysisBean();

	                    objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
	                    objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
	                    objWaiterAnalysisBean.setNoOfKot(1);
	                    objWaiterAnalysisBean.setNoOfVoidKot(0);
	                    objWaiterAnalysisBean.setNoOfMoveKot(0);
	                    listOfWaiterAnalysis.add(objWaiterAnalysisBean);
	                    mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
	                    mapKotsSave.put(kotNo, kotNo);
	                }
	            	}
	            }
	           

	            //voided KOTs
	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbFilters.setLength(0);
	            operation = "Void KOT";

	            if (!"All".equals(posName))
	            {
	                sbFilters.append("and a.strPOSCode='"+strPOSCode+"' ");
	            }
	            if (!"All".equals(userName))
	            {
	                sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
	            }
	            if (!reasonName.equalsIgnoreCase("All"))
	            {
	                sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
	            }

	            sbSqlLive.append("SELECT c.strWaiterNo,c.strWShortName,c.strWFullName,a.strType strOperationType,a.strKOTNo\n"
	                    + "FROM tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d\n"
	                    + "WHERE a.strTableNo=b.strTableNo AND a.strWaiterNo=c.strWaiterNo AND a.strReasonCode=d.strReasonCode \n"
	                    + "AND LENGTH(a.strKOTNo)>2 AND DATE(a.dteDateCreated) BETWEEN '" + fromDate + "' and '" + toDate + "' ");

	            sbSqlLive.append(sbFilters);
	            sbSqlLive.append("group by a.strWaiterNo,a.strKOTNo "
	                    + "order by c.strWFullName,a.strKOTNo");
	            List listVoidedKOT =objBaseService.funGetList(sbSqlLive,"sql");
	            int noOfKot = 0;
	            double noOfVoidKotPer = 0.0, noOfMoveKotPer = 0.0;
	            if(listVoidedKOT.size()>0)
	            {
	            	for(int i=0;i<listVoidedKOT.size();i++)
	            	{
	            	Object[] obj = (Object[]) listVoidedKOT.get(i);	
	                clsWaiterAnalysisBean objWaiterAnalysisBean = new clsWaiterAnalysisBean();

	                if (obj[3].toString().equalsIgnoreCase("VKot"))
	                {
	                    operation = "Void KOT";
	                }
	                else if (obj[3].toString().equalsIgnoreCase("MVKot"))
	                {
	                    operation = "Move KOT";
	                }
	                else
	                {
	                    operation = "Void KOT";
	                }

	                String waiterNo = obj[0].toString();
	                String kotNo = obj[4].toString();

	                if (mapWaiterWise.containsKey(waiterNo))
	                {
	                    objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
	                    if (operation.equalsIgnoreCase("Void KOT"))
	                    {
	                        objWaiterAnalysisBean.setNoOfVoidKot(objWaiterAnalysisBean.getNoOfVoidKot() + 1);
	                    }
	                    else
	                    {
	                        objWaiterAnalysisBean.setNoOfMoveKot(objWaiterAnalysisBean.getNoOfMoveKot() + 1);
	                    }
	                    if (!mapKotsSave.containsKey(kotNo))
	                    {
	                        objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
	                    }
	                }
	                else
	                {
	                    objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
	                    objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
	                    objWaiterAnalysisBean.setNoOfKot(1);
	                    if (operation.equalsIgnoreCase("Void KOT"))
	                    {
	                        objWaiterAnalysisBean.setNoOfVoidKot(1);
	                    }
	                    else
	                    {
	                        objWaiterAnalysisBean.setNoOfMoveKot(1);
	                    }
	                    listOfWaiterAnalysis.add(objWaiterAnalysisBean);
	                    mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
	                    mapKotsSave.put(kotNo, kotNo);
	                }
	            }
	            }
	           
	            //sorting
	            Comparator<clsWaiterAnalysisBean> kotComaparator = new Comparator<clsWaiterAnalysisBean>()
	            {

	                @Override
	                public int compare(clsWaiterAnalysisBean o1, clsWaiterAnalysisBean o2)
	                {
	                    return o1.getStrWaiterName().compareToIgnoreCase(o2.getStrWaiterName());
	                }
	            };
	            Collections.sort(listOfWaiterAnalysis, kotComaparator);
	            //sorting//

	            //fill table data
	            DecimalFormat df1 = new DecimalFormat("0");
	            double totNoOfKot = 0.0, totNoOfVoidKot = 0.0, totNoOfMoveKot = 0.0;
	            for (Map.Entry<String, clsWaiterAnalysisBean> entrySet : mapWaiterWise.entrySet())
	            {
	                clsWaiterAnalysisBean objWaiterAnalysisBean = entrySet.getValue();
	                clsWaiterAnalysisBean objBean = new clsWaiterAnalysisBean();
	                noOfKot = objWaiterAnalysisBean.getNoOfKot();
	                totNoOfKot = totNoOfKot + objWaiterAnalysisBean.getNoOfKot();
	                noOfVoidKotPer = objWaiterAnalysisBean.getNoOfVoidKot();
	                totNoOfVoidKot = totNoOfVoidKot + objWaiterAnalysisBean.getNoOfVoidKot();
	                noOfMoveKotPer = objWaiterAnalysisBean.getNoOfMoveKot();
	                totNoOfMoveKot = totNoOfMoveKot + objWaiterAnalysisBean.getNoOfMoveKot();
	                if (noOfKot > 0)
	                {
	                    noOfVoidKotPer = ((noOfVoidKotPer / noOfKot) * 100);
	                    noOfMoveKotPer = ((noOfMoveKotPer / noOfKot) * 100);
	                }
	                objBean.setStrWaiterName( objWaiterAnalysisBean.getStrWaiterName());
	                objBean.setNoOfKot(noOfKot);
	                objBean.setNoOfVoidKot(objWaiterAnalysisBean.getNoOfVoidKot());
	                objBean.setNoOfVoidKotPer(noOfVoidKotPer);
	                objBean.setNoOfMoveKot(objWaiterAnalysisBean.getNoOfMoveKot());
	                objBean.setNoOfMoveKotPer(noOfMoveKotPer);
	                listArr.add(objBean);
	                
	            }
	          
				
				break;
			
			}//end of switch
			}//end of try
			catch(Exception e)
			{
				e.printStackTrace();
			}
		return listArr;
	}
	
	public List funProcessCoseCenterWiseSummaryReport(String posCode,String fromDate,String toDate,String strReportType,String strShiftNo,String costCenterCode,String userCode)
	{
		 StringBuilder sbSqlLive = new StringBuilder();
	     StringBuilder sbSqlQFile = new StringBuilder();
	     StringBuilder sbSqlModLive = new StringBuilder();
	     StringBuilder sbSqlModQFile = new StringBuilder();
	     StringBuilder sbSqlFilters = new StringBuilder();
	     List<clsCostCenterBean> listOfCostCenterDtl = new LinkedList<>();
	     String gAreaWisePricing="";
	     
	     try
	     {
	    StringBuilder sqlAreaWisePrice = new StringBuilder();
	    sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
	    List listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
	    if(listAraWiePrice.size()>0)
	    {
	    	gAreaWisePricing = (String) listAraWiePrice.get(0);
	    }
	     	sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            // Live Sql
	            sbSqlLive.append("SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
	                    + ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscountAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscountAmt)dblSalesAmount "
	                    + " from tblbilldtl c,tblbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
	                    + " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and c.strBillNo = d.strBillNo and d.strPOSCode = e.strPOSCode and b.strItemCode = c.strItemCode "
	                    + " and (b.strposcode =d.strposcode  or b.strPosCode='All') and a.strCostCenterCode = b.strCostCenterCode "
	                    + " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
	            if (gAreaWisePricing.equals("Y"))
	            {
	                sbSqlLive.append(" and d.strAreaCode=b.strAreaCode ");
	            }

	            // QFile Sql    
	            sbSqlQFile.append("SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
	                    + ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscountAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscountAmt)dblSalesAmount "
	                    + " from tblqbilldtl c,tblqbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
	                    + " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and c.strBillNo = d.strBillNo "
	                    + " and d.strPOSCode = e.strPOSCode "
	                    + " and b.strItemCode = c.strItemCode "
	                    + " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
	                    + " and a.strCostCenterCode = b.strCostCenterCode "
	                    + " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
	            if (gAreaWisePricing.equals("Y"))
	            {
	                sbSqlQFile.append(" and d.strAreaCode=b.strAreaCode ");
	            }

	            sbSqlModLive.append("SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
	                    + ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscAmt)dblSalesAmount  "
	                    + " from tblbillmodifierdtl c,tblbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
	                    + " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and c.strBillNo = d.strBillNo "
	                    + " and d.strPOSCode = e.strPOSCode "
	                    + " and b.strItemCode = left(c.strItemCode,7) "
	                    + " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
	                    + " and a.strCostCenterCode = b.strCostCenterCode "
	                    + " and c.dblAmount>0 "
	                    + " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
	            if (gAreaWisePricing.equals("Y"))
	            {
	            	sbSqlModLive.append(" and d.strAreaCode=b.strAreaCode ");
	            }

	            sbSqlModQFile.append("SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
	                    + ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscAmt)dblSalesAmount  "
	                    + " from tblqbillmodifierdtl c,tblqbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
	                    + " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and c.strBillNo = d.strBillNo "
	                    + " and d.strPOSCode = e.strPOSCode "
	                    + " and b.strItemCode = left(c.strItemCode,7) "
	                    + " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
	                    + " and a.strCostCenterCode = b.strCostCenterCode "
	                    + " and c.dblAmount>0 "
	                    + " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
	            if (gAreaWisePricing.equals("Y"))
	            {
	            	sbSqlModQFile.append(" and d.strAreaCode=b.strAreaCode ");
	            }

	            if (!posCode.equalsIgnoreCase("All"))
	            {
	                sbSqlFilters.append(" AND d.strPOSCode = '" + posCode + "' ");
	            }
	            //sbSqlFilters.append(" and b.intShiftCode ='" + strShiftNo + "' ");
	            
	            if (!costCenterCode.equalsIgnoreCase("All"))
	            {
	                sbSqlFilters.append(" and a.strCostCenterCode='" + costCenterCode + "' ");
	            }
	            sbSqlFilters.append("GROUP BY e.strPOSName,b.strCostCenterCode,a.strCostCenterName "
	                    + "order BY e.strPOSName,b.strCostCenterCode,a.strCostCenterName ");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);
	            sbSqlModLive.append(sbSqlFilters.toString());
	            sbSqlModQFile.append(sbSqlFilters.toString());

	            //live data
	            List listCostCenterData = objBaseService.funGetList(sbSqlLive,"sql");
	            if(listCostCenterData.size()>0)
	            {
	            	for(int i=0;i<listCostCenterData.size();i++)
	            	{
	            	Object[] obj = (Object[]) listCostCenterData.get(i);	
	                clsCostCenterBean objCostCenterBean = new clsCostCenterBean();

	                objCostCenterBean.setStrPOSCode(obj[0].toString());//posCode
	                objCostCenterBean.setStrPOSName(obj[1].toString());//posName
	                objCostCenterBean.setStrCostCenterCode(obj[2].toString());//costCenterCode
	                objCostCenterBean.setStrCostCenterName(obj[3].toString());//costCenterName
	                objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));//subTotal
	                objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));//discount
	                objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));//salesAmount

	                listOfCostCenterDtl.add(objCostCenterBean);
	            	}
	            }
	            
	            //live modifier data
	            listCostCenterData = objBaseService.funGetList(sbSqlModLive,"sql");
	            if(listCostCenterData.size()>0)
	            {
	                for(int i=0;i<listCostCenterData.size();i++)
	                {
	                Object[] obj = (Object[]) listCostCenterData.get(i);	
	            	clsCostCenterBean objCostCenterBean = new clsCostCenterBean();

	                objCostCenterBean.setStrPOSCode(obj[0].toString());//posCode
	                objCostCenterBean.setStrPOSName(obj[1].toString());//posName
	                objCostCenterBean.setStrCostCenterCode(obj[2].toString());//costCenterCode
	                objCostCenterBean.setStrCostCenterName(obj[3].toString());//costCenterName
	                objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));//subTotal
	                objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));//discount
	                objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));//salesAmount
	                listOfCostCenterDtl.add(objCostCenterBean);
	                }
	            }
	           
	            //Q data
	            listCostCenterData = objBaseService.funGetList(sbSqlQFile,"sql");
	            if(listCostCenterData.size()>0)
	            {
	            	for(int i=0;i<listCostCenterData.size();i++)
	            	{
	            	Object[] obj = (Object[]) listCostCenterData.get(i);	
	                clsCostCenterBean objCostCenterBean = new clsCostCenterBean();

	                objCostCenterBean.setStrPOSCode(obj[0].toString());//posCode
	                objCostCenterBean.setStrPOSName(obj[1].toString());//posName
	                objCostCenterBean.setStrCostCenterCode(obj[2].toString());//costCenterCode
	                objCostCenterBean.setStrCostCenterName(obj[3].toString());//costCenterName
	                objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));//subTotal
	                objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));//discount
	                objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));//salesAmount
	                listOfCostCenterDtl.add(objCostCenterBean);
	            	}
	            }
	            
	            //Q modifier data
	            listCostCenterData = objBaseService.funGetList(sbSqlModQFile,"sql");
	            if(listCostCenterData.size()>0)
	            {
	            	for(int i=0;i<listCostCenterData.size();i++)
	            	{
	            	Object[] obj = (Object[]) listCostCenterData.get(i);	
	                clsCostCenterBean objCostCenterBean = new clsCostCenterBean();
	                objCostCenterBean.setStrPOSCode(obj[0].toString());//posCode
	                objCostCenterBean.setStrPOSName(obj[1].toString());//posName
	                objCostCenterBean.setStrCostCenterCode(obj[2].toString());//costCenterCode
	                objCostCenterBean.setStrCostCenterName(obj[3].toString());//costCenterName
	                objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));//subTotal
	                objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));//discount
	                objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));//salesAmount
	                listOfCostCenterDtl.add(objCostCenterBean);
	            	}
	            }
	            

	            Comparator<clsCostCenterBean> posCodeComparator = new Comparator<clsCostCenterBean>()
	            {

	                @Override
	                public int compare(clsCostCenterBean o1, clsCostCenterBean o2)
	                {
	                    return o1.getStrPOSCode().compareTo(o2.getStrPOSCode());
	                }
	            };
	            Comparator<clsCostCenterBean> costCenterCodeComparator = new Comparator<clsCostCenterBean>()
	            {

	                @Override
	                public int compare(clsCostCenterBean o1, clsCostCenterBean o2)
	                {
	                    return o1.getStrCostCenterCode().compareTo(o2.getStrCostCenterCode());
	                }
	            };

	            Collections.sort(listOfCostCenterDtl, new clsCostCenterComparator(posCodeComparator, costCenterCodeComparator));

         
         
	     }
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	     return listOfCostCenterDtl;
	}
	
	public List funProcessCostCenterWiseDetailReport(String posCode,String fromDate,String toDate,String strReportType,String strShiftNo,String costCenterCode,String userCode)
	{
		 StringBuilder sbSqlLive = new StringBuilder();
	     StringBuilder sbSqlQFile = new StringBuilder();
	     StringBuilder sbSqlModLive = new StringBuilder();
	     StringBuilder sbSqlModQFile = new StringBuilder();
	     StringBuilder sbSqlFilters = new StringBuilder();
	     List listOfCostCenterDtl = new ArrayList();
	     String gAreaWisePricing="";
	     
	     try
	     {
	    StringBuilder sqlAreaWisePrice = new StringBuilder();
	    sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
	    List listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
	    if(listAraWiePrice.size()>0)
	    {
	    	gAreaWisePricing = (String) listAraWiePrice.get(0);
	    }
	    
		sbSqlQFile.setLength(0);
        sbSqlQFile.append("SELECT ifnull(f.strCostCenterName,''),a.strItemName,sum(a.dblQuantity), sum(a.dblAmount)"
                + " ,b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
                + " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt)\n"
                + " FROM tblqbilldtl a inner join tblqbillhd b on a.strBillNo=b.strBillNo \n"
                + " inner join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
                + " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
        if (gAreaWisePricing.equals("Y"))
        {
            sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
        }
        sbSqlQFile.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
                + " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSqlQFile.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
        }
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlQFile.append(" and b.strPOSCode='" + posCode + "' ");
        }

        sbSqlLive.setLength(0);
        sbSqlLive.append("SELECT ifnull(f.strCostCenterName,''),a.strItemName,sum(a.dblQuantity), sum(a.dblAmount)"
                + " ,b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
                + " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt)\n"
                + " FROM tblbilldtl a inner join tblbillhd b on a.strBillNo=b.strBillNo \n"
                + " inner join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
                + " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
        if (gAreaWisePricing.equals("Y"))
        {
            sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
        }
        sbSqlLive.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
                + " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
        }
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlLive.append(" and b.strPOSCode='" + posCode + "' ");
            sbSqlQFile.append(" and b.strPOSCode='" + posCode + "' ");
        }

        sbSqlLive.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode,a.strItemName");
        sbSqlQFile.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode,a.strItemName");
        System.out.println("detail live=" + sbSqlQFile);
        System.out.println("detail Q=" + sbSqlLive);
        
        List listOfData = objBaseService.funGetList(sbSqlLive, "sql");
        if(listOfData.size()>0)
        {
        	for(int i=0;i<listOfData.size();i++)
        	{
        		Object[] obj = (Object[]) listOfData.get(i);
        		clsCostCenterBean objBean = new clsCostCenterBean();
        		objBean.setStrCostCenterName(obj[0].toString());
        		objBean.setStrItemName(obj[1].toString());
        		objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        		objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
        		objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
        		objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
        		listOfCostCenterDtl.add(objBean);
        	}
        }
        
        listOfData = objBaseService.funGetList(sbSqlQFile, "sql");
        if(listOfData.size()>0)
        {
        	for(int i=0;i<listOfData.size();i++)
        	{
        		Object[] obj = (Object[]) listOfData.get(i);
        		clsCostCenterBean objBean = new clsCostCenterBean();
        		objBean.setStrCostCenterName(obj[0].toString());
        		objBean.setStrItemName(obj[1].toString());
        		objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        		objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
        		objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
        		objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
        		listOfCostCenterDtl.add(objBean);
        	}
        }

        sbSqlModLive.setLength(0);
        sbSqlModLive.append("SELECT ifnull(f.strCostCenterName,''),a.strModifierName,sum(a.dblQuantity)"
                + " ,sum(a.dblAmount),b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
                + " ,sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt) "
                + " FROM tblbillmodifierdtl a inner join tblbillhd b on a.strBillNo=b.strBillNo "
                + " inner join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)  = d.strItemCode "
                + " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
        if (gAreaWisePricing.equals("Y"))
        {
            sbSqlModLive.append("and b.strAreaCode= d.strAreaCode ");
        }
        sbSqlModLive.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
                + " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + " and a.dblAmount>0 ");
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSqlModLive.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
        }

        sbSqlModQFile.setLength(0);
        sbSqlModQFile.append("SELECT ifnull(f.strCostCenterName,''),a.strModifierName,sum(a.dblQuantity) "
                + " ,sum(a.dblAmount),b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
                + " ,sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt) "
                + " FROM tblqbillmodifierdtl a inner join tblqbillhd b on a.strBillNo=b.strBillNo "
                + " inner join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)  = d.strItemCode "
                + " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
        if (gAreaWisePricing.equals("Y"))
        {
            sbSqlModQFile.append(" and b.strAreaCode= d.strAreaCode ");
        }
        sbSqlModQFile.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
                + " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + " and a.dblAmount>0 ");
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sbSqlModQFile.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
        }
        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlModQFile.append(" and b.strPOSCode='" + posCode + "' ");
            sbSqlModLive.append(" and b.strPOSCode='" + posCode + "' ");
        }

        sbSqlModQFile.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode, d.strItemName");
        sbSqlModLive.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode, d.strItemName");

        listOfData = objBaseService.funGetList(sbSqlModLive, "sql");
        if(listOfData.size()>0)
        {
        	for(int i=0;i<listOfData.size();i++)
        	{
        		Object[] obj = (Object[]) listOfData.get(i);
        		clsCostCenterBean objBean = new clsCostCenterBean();
        		objBean.setStrCostCenterName(obj[0].toString());
        		objBean.setStrItemName(obj[1].toString());
        		objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        		objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
        		objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
        		objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
        		listOfCostCenterDtl.add(objBean);
        	}
        }
        
        listOfData = objBaseService.funGetList(sbSqlModQFile, "sql");
        if(listOfData.size()>0)
        {
        	for(int i=0;i<listOfData.size();i++)
        	{
        		Object[] obj = (Object[]) listOfData.get(i);
        		clsCostCenterBean objBean = new clsCostCenterBean();
        		objBean.setStrCostCenterName(obj[0].toString());
        		objBean.setStrItemName(obj[1].toString());
        		objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        		objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
        		objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
        		objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
        		listOfCostCenterDtl.add(objBean);
        	}
        }
        
        //Non chargable kots
        StringBuilder sqlNonChargableKOts = new StringBuilder();

        sqlNonChargableKOts.append("select ifnull(c.strCostCenterName,''),b.strItemName,sum(a.dblQuantity),0.00 as dblAmount,a.strPOSCode,'user',a.dblRate,0.00 as dblAmt_dblDisc,0.00 as dblDisc "
                + "from tblnonchargablekot a,tblmenuitempricingdtl b,tblcostcentermaster c "
                + "where date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' "
                + "and a.strItemCode=b.strItemCode "
                + "and (a.strposcode =b.strposcode  or b.strPosCode='All') "
                + "and b.strCostCenterCode=c.strCostCenterCode and b.strHourlyPricing='NO' ");
        if (!costCenterCode.equalsIgnoreCase("All"))
        {
            sqlNonChargableKOts.append(" and b.strCostCenterCode='" + costCenterCode + "' ");
        }
        if (!posCode.equalsIgnoreCase("All"))
        {
            sqlNonChargableKOts.append(" and a.strPOSCode='" + posCode + "' ");
        }
        sqlNonChargableKOts.append("group by a.strItemCode,b.strItemName,c.strCostCenterCode,c.strCostCenterName ");
        sqlNonChargableKOts.append("order by a.strItemCode,b.strItemName,c.strCostCenterCode,c.strCostCenterName ");

        listOfData = objBaseService.funGetList(sqlNonChargableKOts, "sql");
        if(listOfData.size()>0)
        {
        	for(int i=0;i<listOfData.size();i++)
        	{
        		Object[] obj = (Object[]) listOfData.get(i);
        		clsCostCenterBean objBean = new clsCostCenterBean();
        		objBean.setStrCostCenterName(obj[0].toString());
        		objBean.setStrItemName(obj[1].toString());
        		objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
        		objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
        		objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
        		objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
        		listOfCostCenterDtl.add(objBean);
        	}
        }
        
        
                Comparator<clsCostCenterBean> costCenterCodeComparator = new Comparator<clsCostCenterBean>()
                {

                    @Override
                    public int compare(clsCostCenterBean o1, clsCostCenterBean o2)
                    {
                        return o1.getStrCostCenterCode().compareTo(o2.getStrCostCenterCode());
                    }
                };

                Collections.sort(listOfCostCenterDtl, new clsCostCenterComparator( costCenterCodeComparator));

	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	     
	    return listOfCostCenterDtl; 
	}
	
	public List funProcessBillWiseReport(String posCode,String fromDate,String toDate,String shiftNo,String queryType)
	{
		
		StringBuilder sqlBuilder = new StringBuilder();
		List listRet = new ArrayList();
		try
		{
		// live
		if(queryType.equalsIgnoreCase("liveData"))
		{
		sqlBuilder.setLength(0);
		sqlBuilder.append("select a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate ,b.strPosName, "
				+ "ifnull(d.strSettelmentDesc,'') as strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt  "
				+ ",sum(c.dblSettlementAmt) as dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
				+ "from  tblbillhd a,tblposmaster b,tblbillsettlementdtl c,tblsettelmenthd d "
				+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "' "
				+ "and a.strPOSCode=b.strPOSCode "
				+ "and a.strBillNo=c.strBillNo "
				+ "and c.strSettlementCode=d.strSettelmentCode "
				+ "and date(a.dteBillDate)=date(c.dteBillDate) "
				+ "and a.strClientCode=c.strClientCode ");
		if (!posCode.equalsIgnoreCase("All"))
		{
			sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
		}
		if (!shiftNo.equalsIgnoreCase("All"))
		{
			sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
		}
		sqlBuilder.append("GROUP BY b.strPosName,a.strClientCode,date(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
				+ "ORDER BY a.strSettelmentMode,a.strBillNo ASC ");
		listRet = objBaseService.funGetList(sqlBuilder, "sql");
		}
		else if(queryType.equalsIgnoreCase("qData"))
		{
			// QFile
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate ,b.strPosName, "
								+ "ifnull(d.strSettelmentDesc,'') as strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt   "
								+ ",sum(c.dblSettlementAmt) as dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
								+ "from  tblqbillhd a,tblposmaster b,tblqbillsettlementdtl c,tblsettelmenthd d "
								+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "' "
								+ "and a.strPOSCode=b.strPOSCode "
								+ "and a.strBillNo=c.strBillNo "
								+ "and c.strSettlementCode=d.strSettelmentCode "
								+ "and date(a.dteBillDate)=date(c.dteBillDate) "
								+ "and a.strClientCode=c.strClientCode ");
			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if (!shiftNo.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
			}
			sqlBuilder.append("GROUP BY b.strPosName,a.strClientCode,date(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
								+ "ORDER BY a.strSettelmentMode,a.strBillNo ASC ");
			listRet = objBaseService.funGetList(sqlBuilder, "sql");
		}
		else
		{
			sqlBuilder.setLength(0);
			sqlBuilder.append("SELECT a.strBillNo "
					+ ",DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate, DATE_FORMAT(DATE(a.dteModifyVoidBill),'%d-%m-%Y') AS VoidedDate "
					+ ",TIME(a.dteBillDate) AS EntryTime, TIME(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount AS BillAmount "
					+ ",a.strReasonName AS Reason,a.strUserEdited AS UserEdited,a.strUserCreated,a.strRemark "
					+ " from tblvoidbillhd a,tblvoidbilldtl b "
					+ " where a.strBillNo=b.strBillNo "
					+ " and b.strTransType='VB' "
					+ " and a.strTransType='VB' "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and Date(a.dteModifyVoidBill)  Between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
			}
			if (!shiftNo.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
			}
			sqlBuilder.append(" group by a.dteBillDate,a.strBillNo ");
			listRet = objBaseService.funGetList(sqlBuilder, "sql");
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listRet;
	}
	
	public List funProcessGroupSubGroupWiseReport(String posCode,String fromDate,String toDate,String shiftNo,String subGroupCode,String groupCode,String userCode)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsGroupSubGroupItemBean>();
		try
		{
			Map<String, clsGroupSubGroupItemBean> mapItemDtl = new HashMap<>();
            clsGroupSubGroupItemBean objGroupSubGroupItemBean = null;

            //QFile
            sqlBuilder.setLength(0);
            sqlBuilder.append("select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
                    + ",ifnull(sum(b.dblAmount),0) as Amount,b.strItemCode "
                    + "from tblqbillhd a "
                    + "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
                    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
                    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
                    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
                    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
                    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') "
                    + "and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "') "
                    + "Group By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName "
                    + "order By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName");
            List listOfData =objBaseService.funGetList(sqlBuilder,"sql");
            if(listOfData.size()>0)
            {
            	for(int i=0;i<listOfData.size();i++)
            	{
            	Object[] obj = (Object[]) listOfData.get(i);
            	
            	String itemCode = obj[5].toString();

                if (mapItemDtl.containsKey(itemCode))
                {
                	objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

                	objGroupSubGroupItemBean.setDblQuantity(objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble( obj[3].toString())));
                	objGroupSubGroupItemBean.setDblAmount(objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
                else
                {
                	objGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
                	objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
                	objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
                	objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
                	objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
                	objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
                	objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
            	}
            }
            

            //QFile modifiers
            sqlBuilder.setLength(0);
            sqlBuilder.append("select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
                    + ",ifnull(sum(f.dblAmount),0) as Amount,f.strItemCode "
                    + "from tblqbillhd a "
                    + "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "left outer join tblqbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
                    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
                    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
                    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
                    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
                    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
                    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') "
                    + "and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "') "
                    + "and b.strItemCode=left(f.strItemCode,7) "
                    + "and f.dblAmount>0 "
                    + "Group By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName "
                    + "order By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName");
            listOfData =objBaseService.funGetList(sqlBuilder,"sql");
            if(listOfData.size()>0)
            {
            	for(int i=0;i<listOfData.size();i++)
            	{
            	Object[] obj = (Object[]) listOfData.get(i);
            	String itemCode = obj[5].toString();

                if (mapItemDtl.containsKey(itemCode))
                {
                	objGroupSubGroupItemBean = mapItemDtl.get(itemCode);
                	objGroupSubGroupItemBean.setDblQuantity(objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble( obj[3].toString())));
                	objGroupSubGroupItemBean.setDblAmount(objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
                else
                {
                	objGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
                	objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
                	objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
                	objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
                	objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
                	objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
                	objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
            	}
            }
            
            //Live
            sqlBuilder.setLength(0);
            sqlBuilder.append("select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
                    + ",ifnull(sum(b.dblAmount),0) as Amount,b.strItemCode "
                    + "from tblbillhd a "
                    + "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
                    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
                    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
                    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
                    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
                    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') "
                    + "and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "') "
                    + "Group By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName "
                    + "order By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName");
            listOfData =objBaseService.funGetList(sqlBuilder,"sql");
            if(listOfData.size()>0)
            {
            	for(int i=0;i<listOfData.size();i++)
            	{
            	Object[] obj = (Object[]) listOfData.get(i);
            	String itemCode = obj[5].toString();

                if (mapItemDtl.containsKey(itemCode))
                {
                	objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

                	objGroupSubGroupItemBean.setDblQuantity(objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble( obj[3].toString())));
                	objGroupSubGroupItemBean.setDblAmount(objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
                else
                {
                	objGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
                	objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
                	objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
                	objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
                	objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
                	objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
                	objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
            	}
            }
          
            //Live modifiers
            sqlBuilder.setLength(0);
            sqlBuilder.append("select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
                    + ",ifnull(sum(f.dblAmount),0) as Amount,f.strItemCode "
                    + "from tblbillhd a "
                    + "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "left outer join tblbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
                    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
                    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
                    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
                    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
                    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
                    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') "
                    + "and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "') "
                    + "and b.strItemCode=left(f.strItemCode,7) "
                    + "and f.dblAmount>0 "
                    + "Group By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName "
                    + "order By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName");
            listOfData =objBaseService.funGetList(sqlBuilder,"sql");
            if(listOfData.size()>0)
            {
            	for(int i=0;i<listOfData.size();i++)
            	{
            	Object[] obj = (Object[]) listOfData.get(i);
                String itemCode = obj[5].toString();

                if (mapItemDtl.containsKey(itemCode))
                {
                	objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

                	objGroupSubGroupItemBean.setDblQuantity(objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble( obj[3].toString())));
                	objGroupSubGroupItemBean.setDblAmount(objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
                else
                {
                	objGroupSubGroupItemBean = new clsGroupSubGroupItemBean();
                	objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
                	objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
                	objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
                	objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
                	objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
                	objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

                    mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
                }
            	}
            }
           
            Comparator<clsGroupSubGroupItemBean> groupComparator = new Comparator<clsGroupSubGroupItemBean>()
            {

                @Override
                public int compare(clsGroupSubGroupItemBean o1, clsGroupSubGroupItemBean o2)
                {
                    return o1.getStrGroupName().compareToIgnoreCase(o2.getStrGroupName());
                }
            };

            Comparator<clsGroupSubGroupItemBean> subGroupComparator = new Comparator<clsGroupSubGroupItemBean>()
            {

                @Override
                public int compare(clsGroupSubGroupItemBean o1, clsGroupSubGroupItemBean o2)
                {
                    return o1.getStrSubGroupName().compareToIgnoreCase(o2.getStrSubGroupName());
                }
            };

            Comparator<clsGroupSubGroupItemBean> codeComparator = new Comparator<clsGroupSubGroupItemBean>()
            {

                @Override
                public int compare(clsGroupSubGroupItemBean o1, clsGroupSubGroupItemBean o2)
                {
                    return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
                }
            };

            listOfGroupSubGroupWiseSales.addAll(mapItemDtl.values());

            Collections.sort(listOfGroupSubGroupWiseSales, new clsGroupSubGroupComparator(
                    groupComparator,
                    subGroupComparator,
                    codeComparator)
            );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return listOfGroupSubGroupWiseSales;
	}
	
	public List funProcessGroupWiseReport(String strPOSCode,String fromDate,String toDate,String strUserCode,String strShiftNo,String strSGCode)
	{
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();
		List<clsGroupWaiseSalesBean> list = new ArrayList<>();
		
		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);
		
		try
		{
		sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
				+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
				+ ",f.strPosName, '" + strUserCode + "'"
				+ ",b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
				+ "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
				+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
				+ ",tblitemmaster e,tblposmaster f "
				+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
				+ "and a.strClientCode=b.strClientCode "
				+ "and b.strItemCode=e.strItemCode "
				+ "and c.strGroupCode=d.strGroupCode "
				+ "and d.strSubGroupCode=e.strSubGroupCode ");

		sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
				+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
				+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
				+ " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
				+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
				+ ",tblitemmaster e,tblposmaster f "
				+ "where a.strBillNo=b.strBillNo "
				+ "and a.strPOSCode=f.strPOSCode  "
				+ "and a.strClientCode=b.strClientCode   "
				+ "and b.strItemCode=e.strItemCode "
				+ "and c.strGroupCode=d.strGroupCode "
				+ "and d.strSubGroupCode=e.strSubGroupCode ");

		sqlModLive.append("select c.strGroupCode,c.strGroupName"
				+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
				+ ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
				+ " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
				+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
				+ ",tblsubgrouphd e,tblgrouphd c "
				+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
				+ "and a.strClientCode=b.strClientCode  "
				+ " and LEFT(b.strItemCode,7)=d.strItemCode "
				+ " and d.strSubGroupCode=e.strSubGroupCode "
				+ "and e.strGroupCode=c.strGroupCode "
				+ " and b.dblamount>0 ");

		sqlModQFile.append("select c.strGroupCode,c.strGroupName"
				+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode + "','0' "
				+ ",sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
				+ " sum(b.dblAmount)-sum(b.dblDiscAmt) "
				+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
				+ ",tblsubgrouphd e,tblgrouphd c "
				+ " where a.strBillNo=b.strBillNo "
				+ "and a.strPOSCode=f.strPosCode   "
				+ "and a.strClientCode=b.strClientCode   "
				+ " and LEFT(b.strItemCode,7)=d.strItemCode "
				+ " and d.strSubGroupCode=e.strSubGroupCode "
				+ "and e.strGroupCode=c.strGroupCode "
				+ " and b.dblamount>0 ");

		sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
		if (!strPOSCode.equalsIgnoreCase("All"))
		{
			sbSqlFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
		}

		sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");

		if (!strSGCode.equalsIgnoreCase("All"))
		{
			sbSqlFilters.append("AND d.strSubGroupCode='" + strSGCode + "' ");
		}
		sbSqlFilters.append(" Group BY c.strGroupCode, c.strGroupName, a.strPoscode ");

		sbSqlLive.append(sbSqlFilters);
		sbSqlQFile.append(sbSqlFilters);
		sqlModLive.append(sbSqlFilters);
		sqlModQFile.append(sbSqlFilters);

		Map<String, clsGroupWaiseSalesBean> mapGroup = new HashMap<>();

		List listSqlLive = objBaseService.funGetList(sbSqlLive,"sql");
		if (listSqlLive.size() > 0)
		{
			for (int i = 0; i < listSqlLive.size(); i++)
			{
				Object[] obj = (Object[]) listSqlLive.get(i);

				String groupCode = obj[0].toString();
				String groupName = obj[1].toString();
				double qty = Double.parseDouble(obj[2].toString());
				double netTotal = Double.parseDouble(obj[3].toString());
				String posName = obj[4].toString();
				double subTotal = Double.parseDouble(obj[7].toString());
				double discAmt = Double.parseDouble(obj[8].toString());
				double salesAmt = Double.parseDouble(obj[10].toString());

				if (mapGroup.containsKey(groupCode))
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
					objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
				else
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(qty);
					objClsGroupWaiseSalesBean.setSubTotal(subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}

			}
		}

		List listSqlQFile = objBaseService.funGetList(sbSqlQFile,"sql");
		if (listSqlQFile.size() > 0)
		{

			for (int i = 0; i < listSqlQFile.size(); i++)
			{
				Object[] obj = (Object[]) listSqlQFile.get(i);

				String groupCode = obj[0].toString();
				String groupName = obj[1].toString();
				double qty = Double.parseDouble(obj[2].toString());
				double netTotal = Double.parseDouble(obj[3].toString());
				String posName = obj[4].toString();
				double subTotal = Double.parseDouble(obj[7].toString());
				double discAmt = Double.parseDouble(obj[8].toString());
				double salesAmt = Double.parseDouble(obj[10].toString());

				if (mapGroup.containsKey(groupCode))
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
					objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
				else
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(qty);
					objClsGroupWaiseSalesBean.setSubTotal(subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
			}
		}

		List listSqlModLive = objBaseService.funGetList(sqlModLive,"sql");
		if (listSqlModLive.size() > 0)
		{

			for (int i = 0; i < listSqlModLive.size(); i++)
			{
				Object[] obj = (Object[]) listSqlModLive.get(i);

				String groupCode = obj[0].toString();
				String groupName = obj[1].toString();
				double qty = Double.parseDouble(obj[2].toString());
				double netTotal = Double.parseDouble(obj[3].toString());
				String posName = obj[4].toString();
				double subTotal = Double.parseDouble(obj[7].toString());
				double discAmt = Double.parseDouble(obj[8].toString());
				double salesAmt = Double.parseDouble(obj[10].toString());

				if (mapGroup.containsKey(groupCode))
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
					objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
				else
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(qty);
					objClsGroupWaiseSalesBean.setSubTotal(subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
			}
		}

		List listSqlModQFile = objBaseService.funGetList(sqlModQFile,"sql");
		if (listSqlModQFile.size() > 0)
		{
			for (int i = 0; i < listSqlModQFile.size(); i++)
			{
				Object[] obj = (Object[]) listSqlModQFile.get(i);

				String groupCode = obj[0].toString();
				String groupName = obj[1].toString();
				double qty = Double.parseDouble(obj[2].toString());
				double netTotal = Double.parseDouble(obj[3].toString());
				String posName = obj[4].toString();
				double subTotal = Double.parseDouble(obj[7].toString());
				double discAmt = Double.parseDouble(obj[8].toString());
				double salesAmt = Double.parseDouble(obj[10].toString());

				if (mapGroup.containsKey(groupCode))
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
					objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
				else
				{
					clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
					objClsGroupWaiseSalesBean.setGroupName(groupName);
					objClsGroupWaiseSalesBean.setPosName(posName);
					objClsGroupWaiseSalesBean.setQty(qty);
					objClsGroupWaiseSalesBean.setSubTotal(subTotal);
					objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
					objClsGroupWaiseSalesBean.setNetTotal(netTotal);
					objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

					mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
				}
			}
		}

		for (clsGroupWaiseSalesBean objGroupWaiseSalesBean : mapGroup.values())
		{
			list.add(objGroupWaiseSalesBean);
		}

		Comparator<clsGroupWaiseSalesBean> groupComparator = new Comparator<clsGroupWaiseSalesBean>()
		{

			@Override
			public int compare(clsGroupWaiseSalesBean o1, clsGroupWaiseSalesBean o2)
			{
				return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
			}
		};

		Collections.sort(list, new clsGroupWiseComparator(groupComparator));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return list;
	}
	
	public List funProcessItemWiseReport(String posCode,String fromDate,String toDate,String shiftNo,String printComplimentaryYN,String type,String strUserCode)
	{
		List list = new ArrayList();
		StringBuilder sqlLive = new StringBuilder();
		StringBuilder sqlLiveCompli = new StringBuilder();
		StringBuilder sqlQFile = new StringBuilder();
		StringBuilder sqlQCompli = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();
		
		try
		{
		String sqlFilters = "";

		if (!posCode.equalsIgnoreCase("All"))
		{
			sqlFilters += " AND b.strPOSCode = '" + posCode + "' ";
		}
		if (!shiftNo.equalsIgnoreCase("All"))
		{
			sqlFilters += " AND b.intShiftCode = '" + shiftNo + "' ";
		}
		
		if(type.equalsIgnoreCase("Live"))
		{
		sqlLive.append("select a.strItemCode,a.strItemName,c.strPOSName"
				+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
				+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				+ " and a.strClientCode=b.strClientCode ");
		sqlLive.append(sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ");
		list = objBaseService.funGetList(sqlLive,"sql");
		
		}
		else if(type.equalsIgnoreCase("LiveCompli"))
		{
		sqlLiveCompli.append("select a.strItemCode,a.strItemName,c.strPOSName"
				+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
				+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblbillcomplementrydtl a,tblbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				+ " and a.strClientCode=b.strClientCode ");
		sqlLiveCompli.append("  GROUP BY a.strItemCode,a.strItemName ");
		list = objBaseService.funGetList(sqlLiveCompli,"sql");
		
		}
		else if(type.equalsIgnoreCase("QFile"))
		{
		sqlQFile.append("select a.strItemCode,a.strItemName,c.strPOSName"
				+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
				+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				+ " and a.strClientCode=b.strClientCode ");
		sqlQFile.append("  GROUP BY a.strItemCode,a.strItemName ");
		list = objBaseService.funGetList(sqlQFile,"sql");
		
		}
		else if(type.equalsIgnoreCase("QCompli"))
		{
		sqlQCompli.append("select a.strItemCode,a.strItemName,c.strPOSName"
				+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
				+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblqbillcomplementrydtl a,tblqbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				+ " and a.strClientCode=b.strClientCode ");
		sqlQCompli.append("  GROUP BY a.strItemCode,a.strItemName ");
		list = objBaseService.funGetList(sqlQCompli,"sql");
		
		}
		else if(type.equalsIgnoreCase("ModLive"))
		{
		sqlModLive.append("select a.strItemCode,a.strModifierName,c.strPOSName"
				+ ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and a.dblamount>0 \n"
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
				+ " and a.strClientCode=b.strClientCode  ");
		sqlModLive.append("  GROUP BY a.strItemCode,a.strModifierName ");
		list = objBaseService.funGetList(sqlModLive,"sql");
		
		}
		else if(type.equalsIgnoreCase("ModQFile"))
		{
		sqlModQFile.append("select a.strItemCode,a.strModifierName,c.strPOSName"
				+ ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
				+ "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
				+ "and b.strPOSCode=c.strPosCode "
				+ "and a.dblamount>0 \n"
				+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
				+ "and a.strClientCode=b.strClientCode  ");
		sqlModQFile.append("  GROUP BY a.strItemCode,a.strModifierName ");
		list = objBaseService.funGetList(sqlModQFile,"sql");
		
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funProcessMenuHeadWiseReport(String posCode,String fromDate,String toDate,String strUserCode,String shiftNo)
	{
		List<clsGenericBean> listOfMenuHead = new ArrayList<clsGenericBean>();
		try
		{
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			String gAreaWisePricing="";
			List listAraWiePrice = new ArrayList();
			
			StringBuilder sqlAreaWisePrice = new StringBuilder();
		    sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
		    listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
		    if(listAraWiePrice.size()>0)
		    {
		    	gAreaWisePricing = (String) listAraWiePrice.get(0);
		    }
			
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);

			sbSqlQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount) ,sum(a.dblDiscountAmt) "
					+ "FROM tblqbilldtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			sbSqlLive.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate) ,sum(a.dblAmount),sum(a.dblDiscountAmt) "
					+ " FROM tblbilldtl a\n"
					+ " left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			sqlModLive.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
					+ "FROM tblbillmodifierdtl a\n"
					+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ");

			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sqlModLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sqlModLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sqlModLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
					+ " and a.strClientCode=b.strClientCode ");

			sqlModQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
					+ "FROM tblqbillmodifierdtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ");

			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sqlModQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sqlModQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sqlModQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
					+ " and a.strClientCode=b.strClientCode ");

			if (!posCode.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
			}
			if (!shiftNo.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append(" AND b.intShiftCode = '" + shiftNo + "' ");
			}
			sbSqlFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters.toString());
			sqlModQFile.append(sbSqlFilters.toString());

			Map<String, clsGenericBean> mapMenuDtl = new HashMap<String, clsGenericBean>();

			// live data			
			List list = objBaseService.funGetList(sbSqlLive,"sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}

		
			// live modifiers			
			list = objBaseService.funGetList(sqlModLive,"sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// Q data			
			list = objBaseService.funGetList(sbSqlQFile,"sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// live modifiers			
			list = objBaseService.funGetList(sqlModQFile,"sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// convert to list
			
			for (clsGenericBean objBean : mapMenuDtl.values())
			{
				listOfMenuHead.add(objBean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return (List) listOfMenuHead;
	}
	
	public List funProcessNonChargableKotSettlementReport(String posCode,String fromDate,String toDate,String reasonCode)
	{
		List<clsBillDtl> listOfNCKOTData = new ArrayList<clsBillDtl>();
		try
		{
		StringBuilder sqlBuilder = new StringBuilder();

		// live
		sqlBuilder.setLength(0);
		sqlBuilder.append("select a.strKOTNo, DATE_FORMAT(a.dteNCKOTDate,'%d-%m-%Y %H:%i'), a.strTableNo, b.strReasonName,d.strPosName,\n"
				+ "a.strRemark,  a.strItemCode, c.strItemName, a.dblQuantity, a.dblRate, a.dblQuantity * a.dblRate as Amount\n"
				+ ",e.strTableName\n"
				+ "from tblnonchargablekot a, tblreasonmaster b, tblitemmaster c,tblposmaster d,tbltablemaster e\n"
				+ "where  a.strReasonCode = b.strReasonCode \n"
				+ "and a.strTableNo=e.strTableNo \n"
				+ "and a.strItemCode = c.strItemCode  and a.strPosCode=d.strPOSCode\n"
				+ "and date(a.dteNCKOTDate) between '" + fromDate + "' and  '" + toDate + "'\n ");
		if (!posCode.equalsIgnoreCase("All"))
		{
			sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
		}
		if (!reasonCode.equalsIgnoreCase("All"))
		{
			sqlBuilder.append("and a.strReasonCode='" + reasonCode + "'  ");
		}

		List list = objBaseService.funGetList(sqlBuilder,"sql");
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				Object[] objArr = (Object[]) list.get(i);

				clsBillDtl obj = new clsBillDtl();
				
				obj.setStrKOTNo(objArr[0].toString());
				obj.setDteNCKOTDate(objArr[1].toString());
				obj.setStrTableNo(objArr[2].toString());
				obj.setStrReasonName(objArr[3].toString());
				obj.setStrPosName(objArr[4].toString());
				obj.setStrRemarks(objArr[5].toString());
				obj.setStrItemCode(objArr[6].toString());
				obj.setStrItemName(objArr[7].toString());
				obj.setDblQuantity(Double.parseDouble(objArr[8].toString()));
				obj.setDblRate(Double.parseDouble(objArr[9].toString()));
				obj.setDblAmount(Double.parseDouble(objArr[10].toString()));
				obj.setStrTableName(objArr[11].toString());
				
				
				listOfNCKOTData.add(obj);
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfNCKOTData;
	}
	
	public List funProcessSalesSummaryReport(String payCode,String reportType,String fromDate,String toDate,String posCode)
	{
		StringBuilder sb=new StringBuilder();
		List list =new ArrayList();

		List<HashMap> listRet = new ArrayList<HashMap>();
		try
		{
		
		if(reportType.equalsIgnoreCase("Daily"))
		 {
			if(payCode.equals("ALL"))
	        {
				sb.setLength(0);
	              sb.append("select a.strPOSCode,c.strPosName,date(a.dteBillDate)"
	              + ",sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
	              + " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
	              + " where a.strSettlementCode=b.strSettelmentCode "
	              + " and a.strPOSCode=c.strPosCode"
	              + " and date(dteBillDate) between '"+fromDate+"' and '"+toDate+"' ");
	              if(!posCode.equals("ALL"))
	              {
	                sb.append(" and a.strPOSCode='"+posCode+"' ");
	              }
	              sb.append(" group by a.strPOSCode,date(a.dteBillDate) order by date(a.dteBillDate);");
	        }
			else
			{
				  sb.setLength(0);
	              sb.append("select a.strPOSCode,c.strPosName,date(a.dteBillDate)"
	                  + ",sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
	                  + " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
	                  + " where a.strSettlementCode=b.strSettelmentCode "
	                  + " and a.strPOSCode=c.strPosCode"
	                  + " and date(dteBillDate) between '"+fromDate+"' and '"+toDate+"' and b.strSettelmentCode='"+payCode+"' ");
	              if(!posCode.equals("ALL"))
	              {
	                sb.append(" and a.strPOSCode='"+posCode+"' ");
	              }
	              sb.append(" group by a.strPOSCode,date(a.dteBillDate) order by date(a.dteBillDate);");
			}
			list= objBaseService.funGetList(sb, "sql");
		   
		 }
		else
		{
			if(payCode.equals("ALL"))
            {
                sb.setLength(0);
                sb.append("select a.strPOSCode,c.strPOSName,monthname(date(a.dteBillDate)),year(date(a.dteBillDate)) "
                + " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
                + " where a.strSettlementCode=b.strSettelmentCode "
                + " and a.strPOSCode=c.strPOSCode "
                + " and month(date(dteBillDate)) between '"+fromDate+"' and '"+toDate+"' ");
                if(!posCode.equals("ALL"))
                {
                    sb.append(" and a.strPOSCode='"+posCode+"' ");
                }
                sb.append("  group by a.strPOSCode,month(date(dteBillDate))"
                + " order by a.strPOSCode,month(date(dteBillDate)) ");
                
                
            }
            else
            {
               
                sb.setLength(0);
                sb.append("select a.strPOSCode,c.strPOSName,monthname(date(a.dteBillDate)),year(date(a.dteBillDate)) "
                    + " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
                    + " where a.strSettlementCode=b.strSettelmentCode "
                    + " and a.strPOSCode=c.strPOSCode "
                    + " and month(date(dteBillDate)) between '"+fromDate+"' and '"+toDate+"' and b.strSettelmentCode='"+payCode+"' ");
                if(!posCode.equals("ALL"))
                {
                    sb.append(" and a.strPOSCode='"+posCode+"' ");
                }
                sb.append("  group by a.strPOSCode,month(date(dteBillDate))"
                    + " order by a.strPOSCode,month(date(dteBillDate)) ");
            }
			list = objBaseService.funGetList(sb, "sql");
	  	   
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public Map funProcessSalesSummaryReport(String strposCode,String strBillDate,String settleAmtType)
	{
		List listRet = new ArrayList();
		StringBuilder sb = new StringBuilder();
		HashMap hmSettelmentDesc=new HashMap();
		try
		{
		if(settleAmtType.equalsIgnoreCase("dailySettleAmt"))
		{
		sb.setLength(0);
        sb.append("select a.strPOSCode,date(a.dteBillDate)"
            + ",b.strSettelmentDesc,sum(a.dblSettlementAmt),sum(a.dblSettlementAmt) "
            + " from vqbillhdsettlementdtl a,tblsettelmenthd b "
            + " where a.strSettlementCode=b.strSettelmentCode "
            + " and date(dteBillDate) = '"+strBillDate+"' and a.strPOSCode='"+strposCode+"' "
            + " group by a.strPOSCode,date(a.dteBillDate),b.strSettelmentDesc "
            + " order by a.strPOSCode,date(a.dteBillDate),b.strSettelmentDesc;");
	    	
  	  List listSql1= objBaseService.funGetList(sb, "sql");
  	  if(listSql1.size()>0)
	      {
  		List listSettlementAmt=new ArrayList();
	    		
  		for(int j=0 ;j<listSql1.size();j++ )
	    	{
	    	 Object[] obj1 = (Object[]) listSql1.get(j);
	    	hmSettelmentDesc.put(obj1[2].toString(),obj1[3].toString());
	        }
  		
	    }
		}
		else
		{
			sb.setLength(0);
            sb.append("select a.strPOSCode,date(a.dteBillDate)"
                + ",b.strSettelmentDesc,sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
                + " from vqbillhdsettlementdtl a,tblsettelmenthd b "
                + " where a.strSettlementCode=b.strSettelmentCode "
                + " and monthname(date(dteBillDate)) ='"+strBillDate+"' and a.strPOSCode='"+strposCode+"' "
                + " group by a.strPOSCode,month(date(dteBillDate)),b.strSettelmentDesc  "
                + " order by a.strPOSCode,month(date(dteBillDate)),b.strSettelmentDesc ;");
	    	
   	  	  List listSql1= objBaseService.funGetList(sb, "sql");
   	  	  if(listSql1.size()>0)
 	  	      {
   	  		  List listSettlementAmt=new ArrayList();
 	  	    		
 	    		for(int j=0 ;j<listSql1.size();j++ )
 	  	    	{
 	  	    	 Object[] obj1 = (Object[]) listSql1.get(j);
 	  	    	hmSettelmentDesc.put(obj1[2].toString(),obj1[3].toString());
 	  	        }
 	    		
		}
		
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return hmSettelmentDesc;
	}
	
	public List funProcessVoidKotReport(String posCode,String reportSubType,String fromDate,String toDate,String qType)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		List list = new ArrayList();
		try
		{
			
			if(qType.equalsIgnoreCase("liveNoOfKotData"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct b.strKOTNo), SUM(b.dblQuantity)\n"
		                + " FROM tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d\n"
		                + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
		                + " AND a.strTableNo=c.strTableNo AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 \n"
		                + " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'");
		        list = objBaseService.funGetList(sqlBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("qFileNoOfKotData"))
			{
				StringBuilder sqlQBuilder = new StringBuilder();
		        sqlQBuilder.setLength(0);
		        sqlQBuilder.append("SELECT COUNT(distinct b.strKOTNo), ifnull(SUM(b.dblQuantity),0) \n"
		                + " FROM tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d\n"
		                + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
		                + " AND a.strTableNo=c.strTableNo AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 \n"
		                + " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'");
		        list = objBaseService.funGetList(sqlQBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("liveVoidedBilledKOTs"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct b.strKOTNo),ifnull(sum(b.intQuantity),0) \n"
		                + "FROM tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d\n"
		                + "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
		                + "AND a.strTableNo=c.strTableNo AND a.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>2 \n"
		                + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n");
		        list = objBaseService.funGetList(sqlBuilder,"sql");	
			}
			else if(qType.equalsIgnoreCase("lineVoidedKOTs"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
		                + "FROM tbllinevoid a\n"
		                + "WHERE LENGTH(a.strKOTNo)>2 AND DATE(a.dteDateCreated) BETWEEN '" + fromDate + "' and '" + toDate + "'\n"
		        );
		        list = objBaseService.funGetList(sqlBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("voidedKOT"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
		                + "FROM tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d\n"
		                + "WHERE a.strTableNo=b.strTableNo AND a.strWaiterNo=c.strWaiterNo "
		                + " AND a.strReasonCode=d.strReasonCode \n"
		                + "AND LENGTH(a.strKOTNo)>2 "
		                + " and LEFT(a.strItemName,3)!='-->' "
		                + " AND DATE(a.dteDateCreated) BETWEEN '" + fromDate + "' and '" + toDate + "'\n"
		        );
		        list = objBaseService.funGetList(sqlBuilder,"sql");	
			}
			else if(qType.equalsIgnoreCase("ncKots"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblQuantity),0) \n"
		                + "FROM tblnonchargablekot a,tbltablemaster b,tblreasonmaster c\n"
		                + "WHERE LENGTH(a.strKOTNo)>2 AND a.strTableNo=b.strTableNo AND a.strReasonCode=c.strReasonCode \n"
		                + "AND DATE(a.dteNCKOTDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n"
		        );
		        list = objBaseService.funGetList(sqlBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("voidKOTData"))
			{
				sqlBuilder.setLength(0);
		        sqlBuilder.append("select a.strItemCode,a.strItemName,ifnull(d.strTableName,''),"
		                + " (a.dblAmount/a.dblItemQuantity) as dblRate,sum(a.dblItemQuantity)as dblItemQuantity,sum(a.dblAmount) as dblAmount "
		                + " ,a.strRemark,a.strKOTNo,a.strPosCode,b.strPosName,a.strUserCreated ,DATE_FORMAT(a.dteVoidedDate,'%d-%m-%Y %H:%i'),ifnull(c.strReasonName,'')"
		                + ",ifnull(e.strWShortName,''),if(a.strVoidBillType='N','Move KOT',a.strVoidBillType),DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y %H:%i'),if(LEFT(a.strItemName,3)='-->','Y','N') isModifier  "
		                + " from tblvoidkot a "
		                + " left outer join tblposmaster b on a.strPOSCode=b.strPosCode "
		                + " left outer join tblreasonmaster c on a.strReasonCode=c.strReasonCode "
		                + " left outer join tbltablemaster d on a.strTableNo=d.strTableNo"
		                + " left outer join tblwaitermaster e on a.strWaiterNo=e.strWaiterNo "
		                + " where date(a.dteVoidedDate) Between '" + fromDate + "' and '" + toDate + "' "
		                + "  ");
		        if (!posCode.equalsIgnoreCase("All"))
		        {
		            sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
		        }
		        if (reportSubType.equals("Void KOT"))
		        {
		            sqlBuilder.append(" and (a.strType='VKot' or a.strType='DVKot') ");
		        }
		        else if (reportSubType.equals("Move KOT"))
		        {
		            sqlBuilder.append(" and a.strType='MVKot' ");
		        }

		        sqlBuilder.append(" group by a.strposcode,a.strusercreated,a.strkotno,a.strItemCode "
		                + " having  dblAmount>if(isModifier='Y',0,-1) "
		                + " order by a.strposcode,a.strusercreated,a.strkotno,a.strItemCode ");

		        list = objBaseService.funGetList(sqlBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("voidedKotCountForNotModif"))
			{
				//which is not modifiers
		        sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
		                + "FROM tblvoidkot a \n"
		                + "WHERE DATE(a.dteVoidedDate) BETWEEN '" + fromDate + "' and '" + toDate + "' "
		                + " and LEFT(a.strItemName,3)!='-->' "
		                + " and a.strType!='MVKot' ");

		        list = objBaseService.funGetList(sqlBuilder,"sql");
			}
			else if(qType.equalsIgnoreCase("voidedKotCountForModif"))
			{
				//which is modifiers but chargable
		        sqlBuilder.setLength(0);
		        sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
		                + "FROM tblvoidkot a \n"
		                + "WHERE DATE(a.dteVoidedDate) BETWEEN '" + fromDate + "' and '" + toDate + "' "
		                + " and LEFT(a.strItemName,3)='-->' "
		                + "and a.dblAmount>0 "
		                + " and a.strType!='MVKot' ");
		         list = objBaseService.funGetList(sqlBuilder,"sql");
			}	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funProcessTaxBreakUpSummaryReport(String posCode,String fromDate,String toDate,String strShiftNo)
	{
		List<clsTaxCalculationDtls> listOfTaxDtl = new LinkedList<>();
		try
		{
			Map<String, clsTaxCalculationDtls> mapTaxDtl = new HashMap<>();
            StringBuilder sqlTaxBuilder = new StringBuilder();
            StringBuilder sqlMenuBreakupBuilder = new StringBuilder();
            DecimalFormat decimalFormat2Decimal = new DecimalFormat("0.00");

            //live tax
            sqlTaxBuilder.setLength(0);
            sqlTaxBuilder.append("SELECT b.strTaxCode,c.strTaxDesc,sum(b.dblTaxableAmount) as dblTaxableAmount,sum(b.dblTaxAmount) as dblTaxAmount "
                    + "FROM tblBillHd a "
                    + "INNER JOIN tblBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
                    + "LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlTaxBuilder.append("and a.strPOSCode='" + posCode + "'  ");
            }
            sqlTaxBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
            sqlTaxBuilder.append("group by c.strTaxCode,c.strTaxDesc ");

            List listSqlLiveTaxDtl = objBaseService.funGetList(sqlTaxBuilder,"sql");
            if(listSqlLiveTaxDtl.size()>0)
            {
            	for(int i=0;i<listSqlLiveTaxDtl.size();i++)
            	{
            	Object[] obj = (Object[]) listSqlLiveTaxDtl.get(i);	
                if (mapTaxDtl.containsKey(obj[0].toString()))//taxCode
                {
                    clsTaxCalculationDtls objTaxCalculationDtlBean = mapTaxDtl.get(obj[0].toString());
                    objTaxCalculationDtlBean.setTaxableAmount(objTaxCalculationDtlBean.getTaxableAmount() + Double.parseDouble(obj[2].toString()));
                    objTaxCalculationDtlBean.setTaxAmount(objTaxCalculationDtlBean.getTaxAmount() + Double.parseDouble(obj[3].toString()));
                }
                else
                {
                    clsTaxCalculationDtls objTaxCalculationDtlBean = new clsTaxCalculationDtls();

                    objTaxCalculationDtlBean.setTaxCode(obj[0].toString());
                    objTaxCalculationDtlBean.setTaxName(obj[1].toString());
                    objTaxCalculationDtlBean.setTaxableAmount(Double.parseDouble(obj[2].toString()));
                    objTaxCalculationDtlBean.setTaxAmount(Double.parseDouble(obj[3].toString()));

                    mapTaxDtl.put(obj[0].toString(), objTaxCalculationDtlBean);

                }
            	}
            }
            //Q tax
            sqlTaxBuilder.setLength(0);
            sqlTaxBuilder.append("SELECT b.strTaxCode,c.strTaxDesc,sum(b.dblTaxableAmount) as dblTaxableAmount,sum(b.dblTaxAmount) as dblTaxAmount "
                    + "FROM tblqBillHd a "
                    + "INNER JOIN tblqBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
                    + "LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlTaxBuilder.append("and a.strPOSCode='" + posCode + "'  ");
            }
            sqlTaxBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
            sqlTaxBuilder.append("group by c.strTaxCode,c.strTaxDesc ");

            List listSqlQBillTaxDtl = objBaseService.funGetList(sqlTaxBuilder,"sql");
            if(listSqlQBillTaxDtl.size()>0)
            {
            	for(int i=0;i<listSqlQBillTaxDtl.size();i++)
            	{
            	Object[] obj = (Object[]) listSqlQBillTaxDtl.get(i);	
                if (mapTaxDtl.containsKey(obj[0].toString()))//taxCode
                {
                    clsTaxCalculationDtls objTaxCalculationDtlBean = mapTaxDtl.get(obj[0].toString());
                    objTaxCalculationDtlBean.setTaxableAmount(objTaxCalculationDtlBean.getTaxableAmount() + Double.parseDouble(obj[2].toString()));
                    objTaxCalculationDtlBean.setTaxAmount(objTaxCalculationDtlBean.getTaxAmount() + Double.parseDouble(obj[3].toString()));
                }
                else
                {
                    clsTaxCalculationDtls objTaxCalculationDtlBean = new clsTaxCalculationDtls();

                    objTaxCalculationDtlBean.setTaxCode(obj[0].toString());
                    objTaxCalculationDtlBean.setTaxName(obj[1].toString());
                    objTaxCalculationDtlBean.setTaxableAmount(Double.parseDouble(obj[2].toString()));
                    objTaxCalculationDtlBean.setTaxAmount(Double.parseDouble(obj[3].toString()));

                    mapTaxDtl.put(obj[0].toString(), objTaxCalculationDtlBean);

                }
            	}
            }
            
            for (clsTaxCalculationDtls objTaxDtl : mapTaxDtl.values())
            {
                listOfTaxDtl.add(objTaxDtl);
            }
            Comparator<clsTaxCalculationDtls> taxNameComparator = new Comparator<clsTaxCalculationDtls>()
            {

                @Override
                public int compare(clsTaxCalculationDtls o1, clsTaxCalculationDtls o2)
                {
                    return o1.getTaxName().compareToIgnoreCase(o2.getTaxName());
                }
            };

            Collections.sort(listOfTaxDtl, taxNameComparator);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfTaxDtl;
	}
	
	public List funProcessDiscountWiseReport(String posCode,String fromDate,String toDate,String type,String querySubType,String strUserCode,String strShiftNo)
	{
		List<clsBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();
        StringBuilder sbSqlLiveDisc = new StringBuilder();
        StringBuilder sbSqlQFileDisc = new StringBuilder();
        
        try
        {
        	
        if(type.equalsIgnoreCase("Summary"))
        {
        sbSqlLiveDisc.setLength(0);
        sbSqlLiveDisc.append("select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
                + " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
                + " from \n"
                + " tblbillhd a\n"
                + " left outer join tblbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
                + " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
                + " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
                + " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
                + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + " and a.strClientCode=b.strClientCode ");

        sbSqlQFileDisc.setLength(0);
        sbSqlQFileDisc.append("select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
                + " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
                + " from \n"
                + " tblqbillhd a\n"
                + " left outer join tblqbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
                + " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
                + " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
                + " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
                + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                + " and a.strClientCode=b.strClientCode ");

        if (!posCode.equalsIgnoreCase("All"))
        {
            sbSqlLiveDisc.append(" and a.strPOSCode='" + posCode + "' ");
            sbSqlQFileDisc.append(" and a.strPOSCode='" + posCode + "' ");
        }

        List listLiveDisc = objBaseService.funGetList(sbSqlLiveDisc,"sql");
        if(listLiveDisc.size()>0)
        {
        	for(int i=0;i<listLiveDisc.size();i++)
        	{
        	Object[] obj = (Object[]) listLiveDisc.get(i);	
            clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

            objBeanBillItemDtlBean.setStrPosName(obj[0].toString());    //POSName
            objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());   //BillDate
            objBeanBillItemDtlBean.setStrBillNo(obj[2].toString());     //BillNo
            objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[3].toString()));//DiscPer
            objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString())); //DiscAmt
            objBeanBillItemDtlBean.setDblBillDiscPer(Double.parseDouble(obj[5].toString()));//DiscOnAmt
            objBeanBillItemDtlBean.setStrDiscType(obj[6].toString());   //DiscType
            objBeanBillItemDtlBean.setStrDiscValue(obj[7].toString());   //DiscValue
            objBeanBillItemDtlBean.setStrItemCode(obj[8].toString());    //DiscReason
            objBeanBillItemDtlBean.setStrItemName(obj[9].toString());   //DiscRemark
            objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[10].toString()));   //SubTotal
            objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[11].toString())); //GrandTotal
            objBeanBillItemDtlBean.setStrSettelmentMode(obj[12].toString()); //UserEdited

            listOfBillItemDtl.add(objBeanBillItemDtlBean);
        	}
        }
       
        List listQfileDisc = objBaseService.funGetList(sbSqlQFileDisc,"sql");
        if(listQfileDisc.size()>0)
        {
        	for(int i=0;i<listQfileDisc.size();i++)
        	{
        	Object[] obj = (Object[]) listQfileDisc.get(i);	
            clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

            objBeanBillItemDtlBean.setStrPosName(obj[0].toString());    //POSName
            objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());   //BillDate
            objBeanBillItemDtlBean.setStrBillNo(obj[2].toString());     //BillNo
            objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[3].toString()));//DiscPer
            objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString())); //DiscAmt
            objBeanBillItemDtlBean.setDblBillDiscPer(Double.parseDouble(obj[5].toString()));//DiscOnAmt
            objBeanBillItemDtlBean.setStrDiscType(obj[6].toString());   //DiscType
            objBeanBillItemDtlBean.setStrDiscValue(obj[7].toString());   //DiscValue
            objBeanBillItemDtlBean.setStrItemCode(obj[8].toString());    //DiscReason
            objBeanBillItemDtlBean.setStrItemName(obj[9].toString());   //DiscRemark
            objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[10].toString()));   //SubTotal
            objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[11].toString())); //GrandTotal
            objBeanBillItemDtlBean.setStrSettelmentMode(obj[12].toString()); //UserEdited

            listOfBillItemDtl.add(objBeanBillItemDtlBean);
        	}
        }
        }
        else if(type.equalsIgnoreCase("Detail"))
        {
        	StringBuilder sqlModiBuilder = new StringBuilder();
            StringBuilder sqlItemBuilder = new StringBuilder();
            
            if(type.equalsIgnoreCase("Detail")&& querySubType.equalsIgnoreCase(""))
            {
            sqlItemBuilder.setLength(0);
            sqlItemBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
                    + ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer  "
                    + ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
                    + ",ifnull(e.strUserEdited,'') "
                    + "from tblbillhd a "
                    + "inner join  tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
                    + "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
                    + "and b.dblDiscountPer>0 ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
                sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
                    + "order by a.strBillNo,b.strItemCode,b.strItemName");
            List listLiveDisc = objBaseService.funGetList(sqlItemBuilder,"sql");
            if(listLiveDisc.size()>0)
            {
            	for(int i=0;i<listLiveDisc.size();i++)
            	{
            	Object[] obj = (Object[]) listLiveDisc.get(i);	
                clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

                objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
                objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
                objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
                objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
                objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
                objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
                objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
                objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
                objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
                objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
                objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
                objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
                objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
                objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); //UserEdited

                listOfBillItemDtl.add(objBeanBillItemDtlBean);
            	}
            }
           
            //live modifiers
            sqlModiBuilder.setLength(0);
            sqlModiBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
                    + ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
                    + " ,ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
                    + ",ifnull(e.strUserEdited,'') "
                    + "from tblbillhd a "
                    + "inner join  tblbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
                    + "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
                    + "and b.dblDiscPer>0 ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
                sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
                    + "order by a.strBillNo,b.strItemCode,b.strModifierName");
            listLiveDisc = objBaseService.funGetList(sqlModiBuilder,"sql");
           if(listLiveDisc.size()>0)
            {
        	   for(int i=0;i<listLiveDisc.size();i++)
        	   {
        		Object[] obj = (Object[]) listLiveDisc.get(i);   
                clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

                objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
                objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
                objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
                objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
                objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
                objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
                objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
                objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
                objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
                objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
                objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
                objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
                objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
                objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); //UserEdited
                 
                listOfBillItemDtl.add(objBeanBillItemDtlBean);
        	   }
            }
           
            //QFile
            sqlItemBuilder.setLength(0);
            sqlItemBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
                    + ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer "
                    + ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
                    + ",ifnull(e.strUserEdited,'') "
                    + "from tblqbillhd a "
                    + "inner join  tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
                    + "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
                    + "and b.dblDiscountPer>0 ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
                sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
                    + "order by a.strBillNo,b.strItemCode,b.strItemName");
            List listQDisc =objBaseService.funGetList(sqlItemBuilder,"sql");
            if(listQDisc.size()>0)
            {
            	for(int i=0;i<listQDisc.size();i++)
            	{
            	Object[] obj = (Object[]) listQDisc.get(i);	
                clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

                objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
                objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
                objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
                objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
                objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
                objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
                objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
                objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
                objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
                objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
                objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
                objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
                objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
                objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); //UserEdited
                listOfBillItemDtl.add(objBeanBillItemDtlBean);
            	}
            }
            
            //QFile modifiers
            sqlModiBuilder.setLength(0);
            sqlModiBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
                    + ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
                    + ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
                    + ",ifnull(e.strUserEdited,'') "
                    + "from tblqbillhd a "
                    + "inner join  tblqbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
                    + "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
                    + "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode  "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
                    + "and b.dblDiscPer>0 ");
            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
                sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
            }
            sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
                    + "order by a.strBillNo,b.strItemCode,b.strModifierName");
            listQDisc =objBaseService.funGetList(sqlModiBuilder,"sql");
            if(listQDisc.size()>0)
            {
            	for(int i=0;i<listQDisc.size();i++)
            	{
            	Object[] obj = (Object[]) listQDisc.get(i);	
                clsBillItemDtlBean objBeanBillItemDtlBean = new clsBillItemDtlBean();

                objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
                objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
                objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
                objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
                objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
                objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
                objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
                objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
                objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
                objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
                objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
                objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
                objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
                objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); //UserEdited
                listOfBillItemDtl.add(objBeanBillItemDtlBean);
            	}
            }
            }
            else if(querySubType.equalsIgnoreCase("liveGross"))
            {
            	sqlItemBuilder.setLength(0);
                sqlItemBuilder.append("select sum(a.dblSettlementAmt) "
                        + "from tblbillsettlementdtl a,tblbillhd b,tblposmaster c "
                        + "where a.strBillNo=b.strBillNo "
                        + "and date(a.dteBillDate)=date(b.dteBillDate) "
                        + "and b.strPOSCode=c.strPosCode "
                        + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
                if (!posCode.equalsIgnoreCase("All"))
                {
                    sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
                }
                listOfBillItemDtl = objBaseService.funGetList(sqlItemBuilder,"sql");
            }
            else if(querySubType.equalsIgnoreCase("qGross"))
            {
            	sqlItemBuilder.setLength(0);
                sqlItemBuilder.append("select ifnull(sum(a.dblSettlementAmt),0) "
                        + "from tblqbillsettlementdtl a,tblqbillhd b,tblposmaster c "
                        + "where a.strBillNo=b.strBillNo "
                        + "and date(a.dteBillDate)=date(b.dteBillDate) "
                        + "and b.strPOSCode=c.strPosCode "
                        + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
                if (!posCode.equalsIgnoreCase("All"))
                {
                    sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
                }
                listOfBillItemDtl = objBaseService.funGetList(sqlItemBuilder,"sql");
            }
            	
        }
        else 
        {
        	StringBuilder sbSqlLive = new StringBuilder();
            StringBuilder sbSqlQFile = new StringBuilder();
            StringBuilder sbSqlFilters = new StringBuilder();
            StringBuilder sqlModLive = new StringBuilder();
            StringBuilder sqlModQFile = new StringBuilder();

            sbSqlLive.setLength(0);
            sbSqlQFile.setLength(0);
            sbSqlFilters.setLength(0);
            sqlModLive.setLength(0);
            sqlModQFile.setLength(0);
            
            sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
            if (!posCode.equals("All"))
            {
                sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
            }
            sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
            sbSqlFilters.append(" Group BY c.strGroupCode ");

            if(querySubType.equalsIgnoreCase("live"))
            {
            sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
                    + ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
                    + ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
                    + "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
                    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
                    + ",tblitemmaster e,tblposmaster f "
                    + "where a.strBillNo=b.strBillNo "
                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strPOSCode=f.strPOSCode  "
                    + " and a.strClientCode=b.strClientCode "
                    + "and b.strItemCode=e.strItemCode "
                    + "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");
            sbSqlLive.append(sbSqlFilters);
            listOfBillItemDtl = objBaseService.funGetList(sbSqlLive,"sql");
            }
            else if(querySubType.equalsIgnoreCase("qFile"))
            {
            sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
                    + ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
                    + ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
                    + " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
                    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
                    + ",tblitemmaster e,tblposmaster f "
                    + "where a.strBillNo=b.strBillNo "
                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strPOSCode=f.strPOSCode  "
                    + " and a.strClientCode=b.strClientCode   "
                    + "and b.strItemCode=e.strItemCode "
                    + "and c.strGroupCode=d.strGroupCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode ");
            sbSqlQFile.append(sbSqlFilters);
            listOfBillItemDtl = objBaseService.funGetList(sbSqlQFile,"sql");
            }
            else if(querySubType.equalsIgnoreCase("modLive"))
            {
            sqlModLive.append("select c.strGroupCode,c.strGroupName"
                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
                    + ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
                    + " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
                    + ",tblsubgrouphd e,tblgrouphd c "
                    + " where a.strBillNo=b.strBillNo "
                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strPOSCode=f.strPosCode  "
                    + " and a.strClientCode=b.strClientCode  "
                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode "
                    + " and e.strGroupCode=c.strGroupCode "
                    + " and b.dblamount>0 ");
            sqlModLive.append(sbSqlFilters);
            listOfBillItemDtl = objBaseService.funGetList(sqlModLive,"sql");
        	}
        	else if(querySubType.equalsIgnoreCase("modQFile"))
        	{
            sqlModQFile.append("select c.strGroupCode,c.strGroupName"
                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
                    + ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
                    + " sum(b.dblAmount)-sum(b.dblDiscAmt) "
                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
                    + ",tblsubgrouphd e,tblgrouphd c "
                    + " where a.strBillNo=b.strBillNo "
                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strPOSCode=f.strPosCode   "
                    + " and a.strClientCode=b.strClientCode   "
                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
                    + " and d.strSubGroupCode=e.strSubGroupCode "
                    + " and e.strGroupCode=c.strGroupCode "
                    + " and b.dblamount>0 ");
            sqlModQFile.append(sbSqlFilters);
            listOfBillItemDtl = objBaseService.funGetList(sqlModQFile,"sql");
        	}
        }
        
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return listOfBillItemDtl;
        
	}
	
	public LinkedHashMap funProcessDayWiseSalesSummary(String withDiscount,
			String fromDate, String toDate, String strOperationType,String strSettlementCode,
			String strPOSCode,String strPOSName,String groupCode,String groupName) 
	{	
		StringBuilder sbSql = new StringBuilder();
		List jColDataArr = new ArrayList();
		List jGroupArr =  new ArrayList();
		List jTaxArr =  new ArrayList();
		List jSettleArr = new ArrayList();

		List jColHeaderArr = new ArrayList();
		List jDateArr =  new ArrayList();
		LinkedHashMap jOBjRet = new LinkedHashMap();
		
		Map map = new HashMap();

		jColHeaderArr.add("DATE");
		jColHeaderArr.add("POS");
		int colCount = 3;
		try {

			// Q Date and POS
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
                    + "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) "
                    + "order by date(a.dteBillDate); ");
			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {

					jDateArr.add((String) listSql.get(i));

				}

			}

			// Live Bill Date
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
                    + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) "
					+ "order by date(a.dteBillDate); ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					jDateArr.add(obj.toString());

				}

			}
			
			// Add Group Column
			StringBuilder sqlGroups = new StringBuilder(); 
			sqlGroups.setLength(0);
			sqlGroups.append("select a.strGroupName from tblgrouphd a ");
			listSql = objBaseService.funGetList(sqlGroups, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					jGroupArr.add(obj.toString());
					jColHeaderArr.add(obj.toString());
					colCount++;
				}
			}

			// fill Live settlement whose amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc "
                    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "WHERE a.strBillNo=b.strBillNo  "
                    + "AND b.strSettlementCode=c.strSettelmentCode  "
                    + "and b.dblSettlementAmt>0 "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc "
                    + "ORDER BY strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					jColHeaderArr.add(obj.toString());
					jSettleArr.add(obj.toString());
					colCount++;
				}
			}

			// fill Q settlement whoes amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc "
                    + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + "WHERE a.strBillNo=b.strBillNo  "
                    + "AND b.strSettlementCode=c.strSettelmentCode  "
                    + "and b.dblSettlementAmt>0 "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc "
					+ "ORDER BY c.strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					jColHeaderArr.add(obj.toString());
					jSettleArr.add(obj.toString());
					colCount++;
				}
			}
			// Tax Column
			 String taxCalType = "";
			 StringBuilder sqlTax = new StringBuilder();
			sqlTax.append("select a.strTaxDesc,a.strTaxCalculation "
                    + " from tbltaxhd a order by a.strTaxCode");
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					  taxCalType =obj[1].toString() ;
					jColHeaderArr.add(obj[0].toString());
					jTaxArr.add(obj[0].toString());
					colCount++;
				}
			}

			// Grand Total
			jColHeaderArr.add("GRAND Total");
			StringBuilder sqlGrandTotal =new StringBuilder();
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(b.dblSettlementAmt) "
                    + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + "WHERE a.strBillNo=b.strBillNo  "
                    + "AND b.strSettlementCode=c.strSettelmentCode  "
                    + "and b.dblSettlementAmt>0 "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGrandTotal.append("and a.strOperationType='" + strOperationType+"' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(sqlGrandTotal + " and a.strPOSCode='"
						+ strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlGrandTotal.append("GROUP BY DATE(a.dteBillDate) "
					+ "ORDER BY DATE(a.dteBillDate); ");

			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			int size = listSql.size();

			for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
				List jRowArr = new ArrayList();

				jRowArr.add(jDateArr.get(tblRow));
				jRowArr.add(strPOSName);
				for (int i = 2; i < colCount; i++) {
					jRowArr.add(i, 0.00);
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (jColHeaderArr.get(i).toString()
								.equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString()
										.equalsIgnoreCase(obj[0].toString()))
							jRowArr.add(i,obj[1].toString());

					}

				}
				map.put(tblRow, jRowArr);
			}

			// Live
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(b.dblSettlementAmt) "
                    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "WHERE a.strBillNo=b.strBillNo  "
                    + "AND b.strSettlementCode=c.strSettelmentCode  "
                    + "and b.dblSettlementAmt>0 "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGrandTotal.append("and a.strOperationType='" + strOperationType+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(" and a.strPOSCode='"
						+ strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlGrandTotal.append("GROUP BY DATE(a.dteBillDate) "
					+ "ORDER BY DATE(a.dteBillDate); ");
			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			size = listSql.size();

			for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList(); 
				jRowArr =  (List) map.get(tblRow);
				for (int i = 3; i < colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (jColHeaderArr.get(i).toString()
								.equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString()
										.equalsIgnoreCase(obj[0].toString())) 
						{
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.add(i, value);

						}

					}

				}
				map.put(tblRow, jRowArr);

			}

			String columnForSalesAmount = "sum(b.dblAmount) ";
			if (withDiscount.equalsIgnoreCase("Y")) {
				columnForSalesAmount = "sum(b.dblAmount) ";
			} else {
				columnForSalesAmount = "sum(b.dblAmount)-sum(b.dblDiscountAmt) ";
			}

			String columnForModiSalesAmount = "SUM(h.dblAmount) ";
			if (withDiscount.equalsIgnoreCase("Y")) {
				columnForModiSalesAmount = "SUM(h.dblAmount) ";
			} else {
				columnForModiSalesAmount = "SUM(h.dblAmount)-sum(h.dblDiscAmt) ";
			}

			// fill Q data group
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForSalesAmount + " "
                    + "from tblqbillhd a,tblqbilldtl b,tblitemmaster e "
                    + ",tblsubgrouphd f ,tblgrouphd g  "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strItemCode=e.strItemCode "
                    + "and e.strSubGroupCode=f.strSubGroupCode "
                    + "and f.strGroupCode=g.strGroupCode "
                    + "AND b.dblAmount>0  "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			
			sbSql.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName; ");

			listSql = objBaseService.funGetList(sbSql, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}
			// Q Modifier Group data
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForModiSalesAmount + " "
                    + "FROM tblqbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblqbillmodifierdtl h "
                    + "WHERE a.strBillNo=h.strBillNo  "
                    + "AND e.strSubGroupCode=f.strSubGroupCode  "
                    + "AND f.strGroupCode=g.strGroupCode  "
                    + "and h.dblAmount>0 "
                    + "AND a.strBillNo=h.strBillNo  "
                    + "AND e.strItemCode=LEFT(h.strItemCode,7) "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sbSql.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName ");
			
			listSql = objBaseService.funGetList(sbSql, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {

								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			// Settlement Data
			StringBuilder sqlTransRecords = new StringBuilder();
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode + "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
                    + "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
                
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='"
						+ strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='"
						+ strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");
			
			listSql = objBaseService.funGetList(sqlTransRecords,"sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList(); 
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[2].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[3].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[3].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			// Tax Data
			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
                    + "from "
                    + "tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strTaxCode=c.strTaxCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append( "and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode "
					+ "order by date(a.dteBillDate),b.strTaxCode; ");

			
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// Live Group Data
			sqlGroups.setLength(0);
			sqlGroups.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForSalesAmount + " "
                    + "from tblbillhd a,tblbilldtl b,tblitemmaster e "
                    + ",tblsubgrouphd f ,tblgrouphd g  "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strItemCode=e.strItemCode "
                    + "and e.strSubGroupCode=f.strSubGroupCode "
                    + "and f.strGroupCode=g.strGroupCode "
                    + "AND b.dblAmount>0  "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode
						+ "' ");
			}
			sqlGroups.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName  ");

			
			listSql = objBaseService.funGetList(sqlGroups, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList(); 
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// Live Modifier Group Data
			sqlGroups.setLength(0);
			sqlGroups.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForModiSalesAmount + " "
                    + "FROM tblbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblbillmodifierdtl h "
                    + "WHERE a.strBillNo=h.strBillNo  "
                    + "AND e.strSubGroupCode=f.strSubGroupCode  "
                    + "AND f.strGroupCode=g.strGroupCode  "
                    + "and h.dblAmount>0 "
                    + "AND a.strBillNo=h.strBillNo  "
                    + "AND e.strItemCode=LEFT(h.strItemCode,7) "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType
						+ "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode
						+ "' ");
			}
			sqlGroups.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName ");
			listSql = objBaseService.funGetList(sqlGroups, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTransRecords.setLength(0);
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode + "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
                    + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='"
						+ strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='"
						+ strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[2].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[3].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[3].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
                    + "from "
                    + "tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strTaxCode=c.strTaxCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append( " and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode "
					+ "order by date(a.dteBillDate),b.strTaxCode; ");

			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (jDateArr.get(tblRow).toString()
								.equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (jColHeaderArr.get(tblCol).toString()
										.equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double)jRowArr.get(tblCol)) {
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double
												.parseDouble(obj[2].toString())
												+ (Double)jRowArr.get(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}
			jOBjRet.put("Col Header", jColHeaderArr);
			jOBjRet.put("Col Count", colCount);
			jOBjRet.put("Row Count", jDateArr.size());
			for (int tblRow = 0; tblRow < jDateArr.size(); tblRow++) {
				List jArr = new ArrayList(); 
				jArr = (List) map.get(tblRow);
				jOBjRet.put("" + tblRow, jArr);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jOBjRet;

	}

}