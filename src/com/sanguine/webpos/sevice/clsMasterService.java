package com.sanguine.webpos.sevice;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.model.clsBaseModel;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSWiseItemIncentiveDtlBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsCostCenterMasterModel;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel;
import com.sanguine.webpos.model.clsDeliveryBoyMasterModel;
import com.sanguine.webpos.model.clsDiscountMasterModel;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsMenuHeadMasterModel;
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsPOSMasterModel;
import com.sanguine.webpos.model.clsPricingMasterHdModel;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsSetupHdModel;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;
import com.sanguine.webpos.model.clsSubMenuHeadMasterModel;
import com.sanguine.webpos.model.clsWaiterMasterModel;

@Service
public class clsMasterService {

	@Autowired
	intfBaseService obBaseService;
	
	 public void funSaveReasonMaster(clsBaseModel objBaseModel){
		 try{
			 obBaseService.funSave(objBaseModel); 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 		
	 }
	 public clsReasonMasterModel funLoadReasonMaster(Map hmParameters){
		 clsReasonMasterModel obReasonMasterModel=null;
		 try{
			 obReasonMasterModel = (clsReasonMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getReason", hmParameters);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return obReasonMasterModel;
	 }
	 
	 public void funSaveSettlementMaster(clsBaseModel objBaseModel){
		 try{
			 obBaseService.funSave(objBaseModel); 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 	
	 }
	 public clsSettlementMasterModel funLoadSettlementMaster(Map hmParameters){
		 clsSettlementMasterModel obSettlementMasterModel=null;
		 try{
			 obSettlementMasterModel = (clsSettlementMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getSettlementMaster", hmParameters);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return obSettlementMasterModel;
	 }
	 
	 public List funLoadSettlementDtl(String clientCode){
		 
		List list=null;
		try {
			list = obBaseService.funLoadAll(new clsSettlementMasterModel(),clientCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return list;
	 }
	 
	 public void funSaveUpdateAreaMaster(clsBaseModel objBaseModel){
		 try{
			 obBaseService.funSave(objBaseModel); 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		} 
	 
		public clsAreaMasterModel funSelectedAreaMasterData(String areaCode,String clientCode)
		{
			clsAreaMasterModel objAreaMasterModel = null;
		
		 try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("areaCode",areaCode);
			hmParameters.put("clientCode",clientCode);
			objAreaMasterModel = (clsAreaMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getAreaMaster", hmParameters);
						
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return objAreaMasterModel;
		 	
	 }
		
		public void	funSaveUpdateCostCenterMaster(clsBaseModel objBaseModel)
		{
			try{
				 obBaseService.funSave(objBaseModel); 
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		}
		
		public clsCostCenterMasterModel funSelectedCostCenterMasterData(String costCenterCode,String clientCode)
		{
			clsCostCenterMasterModel objCostCenterMasterModel = null;
			try
			{
				Map<String,String> hmParameters=new HashMap<String,String>();
				hmParameters.put("costCenterCode",costCenterCode);
				hmParameters.put("clientCode",clientCode);
				objCostCenterMasterModel = (clsCostCenterMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCostCenterMaster", hmParameters);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return objCostCenterMasterModel;
		}
		
		public void funSaveUpdateCustomerAreaMaster(clsBaseModel objBaseModel)
		{
			try
			{
				obBaseService.funSave(objBaseModel);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public clsCustomerAreaMasterModel funSelectedCustomerAreaMasterData(String custAreaCode,String clientCode)
		{
			clsCustomerAreaMasterModel objCustomerAreaMasterModel = null;
		
		 try
		{
			 Map<String,String> hmParameters=new HashMap<String,String>();
			 hmParameters.put("buildingCode",custAreaCode);
			 hmParameters.put("clientCode",clientCode);
				
			 objCustomerAreaMasterModel = (clsCustomerAreaMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerArea", hmParameters);
							
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return objCustomerAreaMasterModel;
		 	
	 }
		
	public 	void funSaveCustomerMaster(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public clsCustomerMasterModel funSelectedCustomerMasterData(String customerCode,String clientCode)
	{
		clsCustomerMasterModel objCustomerMasterModel=null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("customerCode",customerCode);
			hmParameters.put("clientCode",clientCode);
			
			objCustomerMasterModel = (clsCustomerMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerMaster", hmParameters);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objCustomerMasterModel;
	}
	
	public List funGetPOSList(String clientCode)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y' and strClientCode='"+clientCode+"' ");
			list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public 	void funSaveUpdateCustomerTypeMaster(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public List<clsCustomerTypeMasterModel> funFillCustomerTypeCombo(String strClientCode)
	{
		List<clsCustomerTypeMasterModel> list=null;
		try
		{
			list=obBaseService.funLoadAll(new clsCustomerTypeMasterModel(),strClientCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public List funGetCityList(String strClientCode)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select a.strCityName,a.strState,a.strCountry  from tblsetup a where a.strClientCode="+strClientCode);
	        
	        list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public clsCustomerTypeMasterModel funSelectedCustomerTypeMasterData(String customerCode,String clientCode)
	{
		clsCustomerTypeMasterModel objCustomerMasterModel=null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("custTypeCode",customerCode);
			hmParameters.put("clientCode",clientCode);
			
			objCustomerMasterModel = (clsCustomerTypeMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerType", hmParameters);														
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objCustomerMasterModel;
	}
	
	public void funSaveUpdateDeliverPersonMaster(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public clsDeliveryBoyMasterModel funSelectedDeliveryBoyMasterData(String dpCode,String clientCode)
	{
		clsDeliveryBoyMasterModel objDeliveryBoyMasterModel=null; 
		try
		{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("dpCode",dpCode);
		hmParameters.put("clientCode",clientCode);
		objDeliveryBoyMasterModel = (clsDeliveryBoyMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getDeliveryBoyMaster", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objDeliveryBoyMasterModel;
	}
	
	public void funSaveUpdateGroupMaster(clsBaseModel objBaseModel)
	{
		try
		{
		obBaseService.funSave(objBaseModel);	
		}
		catch(Exception e)
		{
			
		}
	}
	
	public clsGroupMasterModel funSelectedGroupMasterData(String groupCode,String clientCode)
	{
		clsGroupMasterModel objGroupMasterModel=null;
		try
		{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("groupCode",groupCode);
		hmParameters.put("clientCode",clientCode);
		
		objGroupMasterModel = (clsGroupMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getGroupMaster", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objGroupMasterModel;
		
	}
	
	public void funSaveUpdateMenuHeadMasterData(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void funSaveUpdateSubMenuHeadMasterData(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public clsMenuHeadMasterModel funSelectedMenuHeadMasterData(String menuHeadCode,String clientCode)
	{
		clsMenuHeadMasterModel objMenuHeadMasterModel = null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("menuCode",menuHeadCode);
			hmParameters.put("clientCode",clientCode);
			objMenuHeadMasterModel = (clsMenuHeadMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getMenuHeadMaster", hmParameters);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objMenuHeadMasterModel;
	}
	
	public clsSubMenuHeadMasterModel funSelectedSubMenuHeadMasterData(String subMenuHeadCode,String clientCode)
	{
		clsSubMenuHeadMasterModel objSubMenuHeadMasterModel = null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("subMenuCode",subMenuHeadCode);
			hmParameters.put("clientCode",clientCode);
			objSubMenuHeadMasterModel = (clsSubMenuHeadMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getSubMenuHeadMaster", hmParameters);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objSubMenuHeadMasterModel;
	}
	
	public clsMenuItemMasterModel funGetMenuItemMasterData(String itemCode,String clientCode)
	{
		clsMenuItemMasterModel objMenuItemMasterModel=null;
		try
		{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("itemCode",itemCode);
		hmParameters.put("clientCode",clientCode);
		
		 objMenuItemMasterModel = (clsMenuItemMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemMaster", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objMenuItemMasterModel;
	}
	
	public void funSaveUpdateMenuItemMaster(clsBaseModel objBaseModel)
	{
		try
		{
		obBaseService.funSave(objBaseModel);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public List<clsSubGroupMasterHdModel> funLoadAllSubGroup(String strClientCode)
	{
		List<clsSubGroupMasterHdModel> list=null;
		try
		{
			list=obBaseService.funLoadAll(new clsSubGroupMasterHdModel(),strClientCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public void funSaveUpdatePosMasterData(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public clsPOSMasterModel funSelectedPOSMasterData(String posCode,String clientCode)
	{
		clsPOSMasterModel objPOSModel = null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("posCode",posCode);
			hmParameters.put("clientCode",clientCode);
			objPOSModel = (clsPOSMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getPOSMaster", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objPOSModel;
	}
	
	public List<clsMenuHeadMasterModel> funLoadAllMenuHeadForMaster(String clientCode) throws Exception
	{
		List<clsMenuHeadMasterModel> list =null;
		list =obBaseService.funLoadAll(new clsMenuHeadMasterModel(),clientCode);
		return list;
	}
	public List<clsSubMenuHeadMasterModel> funLoadAllSubMenuHeadMaster(String clientCode)
	{
		List<clsSubMenuHeadMasterModel> list = null;
		try
		{
		 list =obBaseService.funLoadAll(new clsSubMenuHeadMasterModel(),clientCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List<clsAreaMasterModel> funGetAllAreaForMaster(String clientCode)
	{
		List<clsAreaMasterModel> list = null;
		try
		{
		list = obBaseService.funLoadAll(new clsAreaMasterModel(), clientCode);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List<clsCostCenterMasterModel> funGetAllCostCenterMaster(String clientCode)
	{
		List<clsCostCenterMasterModel> list = null;
		try
		{
		list = obBaseService.funLoadAll(new clsCostCenterMasterModel(), clientCode);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public clsPricingMasterHdModel funGetMenuItemPricingMaster(String pricingId,String clientCode)
	{
		clsPricingMasterHdModel objPricingMasterModel = null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("longPricingId",pricingId);
			hmParameters.put("clientCode",clientCode);
			objPricingMasterModel = (clsPricingMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemPricing", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objPricingMasterModel;
	}
	
	public void funSaveUpdatePricingMaster(clsBaseModel objBaseModel)
	{
		
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public clsPricingMasterHdModel funCheckDuplicateItemPricing(String strItemCode,String strPosCode,String strAreaCode,String strHourlyPricing,String clientCode) throws Exception
	{
		clsPricingMasterHdModel objPricingMasterHdModel = null; 
		try
		{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("strPosCode",strPosCode);
		hmParameters.put("strItemCode",strItemCode);
		hmParameters.put("strAreaCode",strAreaCode);
		hmParameters.put("strHourlyPricing",strHourlyPricing);
		hmParameters.put("clientCode",clientCode);
		objPricingMasterHdModel = (clsPricingMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemPricing", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return objPricingMasterHdModel;
	}
	
	public clsPricingMasterHdModel funLoadDataToUpdateItemPrice(String pricingId,String clientCode)
	{
		clsPricingMasterHdModel objPricingMasterModel = null;
		try
		{
		long strPricingId=Long.parseLong(pricingId);
		objPricingMasterModel = (clsPricingMasterHdModel) obBaseService.funGetMenuItemPricingMaster("getMenuItemPricing", strPricingId, clientCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objPricingMasterModel;
	}
	
	public void funSaveUpdateWaiterMaster(clsBaseModel objBaseModel)
	{
		try
		{
			obBaseService.funSave(objBaseModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public clsWaiterMasterModel funGetSelectedWaiterMasterData(String waiterNo,String clientCode)
	{
		clsWaiterMasterModel objWaiterMasterModel=null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("waiterNo",waiterNo);
			hmParameters.put("clientCode",clientCode);
			objWaiterMasterModel = (clsWaiterMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getWaiterMaster", hmParameters);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objWaiterMasterModel;
	}
	
	public void funSaveUpdateItemWiseIncentiveMaster(clsBaseModel objBaseModel)
	{
		try
		{
		obBaseService.funSave(objBaseModel);	
		}
		catch(Exception e)
		{
			
		}
	}
	
	public List<clsAreaMasterModel> funLoadClientWiseArea(String strClientCode)
	{
		List<clsAreaMasterModel> list=null;
		try
		{
			list=obBaseService.funLoadAll(new clsAreaMasterModel(),strClientCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public List funGetAllGroupNamesForBillSeries(String clientCode,boolean addFilter,String filter)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append(" select a.strGroupName,a.strGroupCode from tblgrouphd a ");
            if (addFilter)
            {
            	sqlBuilder.append(" where a.strGroupCode NOT IN ");
            	sqlBuilder.append(filter);
            }
            list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funGetAllSubGroupNamesForBillSeries(String clientCode,boolean addFilter,String filter)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strSubGroupName,a.strSubGroupCode from tblsubgrouphd a ");
            if (addFilter)
            {
            	sqlBuilder.append(" where a.strSubGroupCode NOT IN ");
            	sqlBuilder.append(filter);
            }

            list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public List funGetAllMenuHeadNamesForBillSeries(String clientCode,boolean addFilter,String filter)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strMenuName,a.strMenuCode from tblmenuhd a ");
            if (addFilter)
            {
            	sqlBuilder.append(" where a.strMenuCode NOT IN ");
            	sqlBuilder.append(filter);
            }

            list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funGetAllRevenueHeadNamesForBillSeries(String clientCode,boolean addFilter,String filter)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strRevenueHead,a.strRevenueHead  from tblitemmaster a ");
            if (addFilter)
            {
            	sqlBuilder.append(" where a.strRevenueHead NOT IN ");
            	sqlBuilder.append(filter);
            }
            sqlBuilder.append(" group by a.strRevenueHead;");
            
            list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List <clsSettlementMasterModel> funLoadSettlementData(String strClientCode){
		List <clsSettlementMasterModel> list=null;
		try{
			 list=obBaseService.funLoadAll(new clsSettlementMasterModel(),strClientCode);
   			 clsSettlementMasterModel objModel = null;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	 
	public clsDiscountMasterModel funSelectedDiscountMasterData(String discCode,String clientCode)
	{
		clsDiscountMasterModel objDiscMasterModel = null;
	
	 try
	{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("discCode",discCode);
		hmParameters.put("clientCode",clientCode);
		objDiscMasterModel = (clsDiscountMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getDiscountMaster", hmParameters);
					
		System.out.println();
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}
	return objDiscMasterModel;
	 	
 }

	public void funSaveUpdateDiscountMaster(clsBaseModel objBaseModel){
	 try{
		 obBaseService.funSave(objBaseModel); 
	 }catch(Exception e){
		 e.printStackTrace();
	 }
	} 	
	
	public clsMenuItemMasterModel funSelectedDiscountDiscountOnItemData(String code,String clientCode)
	{
		clsMenuItemMasterModel objMenuItemMasterModel=null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("itemCode",code);
			hmParameters.put("clientCode",clientCode);
			objMenuItemMasterModel = (clsMenuItemMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemMaster", hmParameters);
		}
		catch(Exception e)
		{
			
		}
		return objMenuItemMasterModel;
		
	}
	public clsGroupMasterModel funSelectedDiscountDiscountOnGroupData(String code,String clientCode)
	{
		clsGroupMasterModel objGroupMasterModel=null;
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("groupCode",code);
			hmParameters.put("clientCode",clientCode);
			objGroupMasterModel = (clsGroupMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getGroupMaster", hmParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objGroupMasterModel;
	}
	
	
	public clsSubGroupMasterHdModel funSelectedDiscountDiscountOnSubGroupData(String code,String clientCode)
	{
		clsSubGroupMasterHdModel objSubGroupMasterModel=null;
		
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("subGroupCode",code);
		hmParameters.put("clientCode",clientCode);
		objSubGroupMasterModel = (clsSubGroupMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getSubGroupMaster", hmParameters);
		
		return objSubGroupMasterModel;
	}
	
	
	public clsSetupHdModel funGetPOSWisePropertySetup(String POSCode,String clientCode) throws Exception
	{
		clsSetupHdModel objSetupHdModel= new clsSetupHdModel();
		List list = obBaseService.funLoadAllPOSWise(objSetupHdModel,clientCode,POSCode);
		if(list.size()>0)
			objSetupHdModel = (clsSetupHdModel) list.get(0);
		
		return objSetupHdModel;
	}
	
	public List funFillCityCombo(String strClientCode)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select a.strCityName,a.strState,a.strCountry  from tblsetup a where a.strClientCode="+strClientCode);
	        
	        list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funFillPOSCombo(String strClientCode)
	{
		List list = null;
		try
		{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y'");
		list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List funFullPOSCombo(String strClientCode)
	{
		List list = null;
		try
		{
			StringBuilder sqlBuilder = new StringBuilder("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y' and strClientCode="+strClientCode);
			list=obBaseService.funGetList(sqlBuilder, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public void funSaveUpdatePOSWiseItemIncentive(clsBaseModel objBaseModel){
		 try{
			 obBaseService.funSave(objBaseModel); 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		} 

	public List funGetListItemWiseIncentive(String clientCode,String posCode) 
	{
		List listRet = new ArrayList();
		 boolean flgPreviousRecordFound=false;
		try
		{
			List list = new ArrayList();
			StringBuilder hqlQuery = new StringBuilder();
	 		hqlQuery.setLength(0);
	 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPOSCode,b.strPosName,a.strIncentiveType,a.dblIncentiveValue "
                + " FROM tblposwiseitemwiseincentives a  left outer join tblposmaster b on a.strPosCode=b.strPosCode"); 
	 		
	 		if(!posCode.equalsIgnoreCase("All"))
            {
	 			
	 			hqlQuery.append(" Where a.strPOSCode='").append(posCode).append("' "); 
            }
	 		hqlQuery.append(" order by a.strItemCode ");
            
	 		list=obBaseService.funGetList(hqlQuery, "sql");
    		if(list.size()>0)
    		{
    			 flgPreviousRecordFound=true;
    		for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] obj = (Object[]) list.get(cnt);
				clsPOSWiseItemIncentiveDtlBean objBean = new clsPOSWiseItemIncentiveDtlBean();
				objBean.setStrItemCode(obj[0].toString());
				objBean.setStrItemName(obj[1].toString());
				objBean.setStrPOSCode(obj[2].toString());
//				objBean.setStrPOSName(obj[3].toString());
				objBean.setStrIncentiveType(obj[4].toString());
				objBean.setStrIncentiveValue(obj[5].toString());
				listRet.add(objBean);
			}
    		}
    		
    		 if(!flgPreviousRecordFound)
             {
    			 hqlQuery.setLength(0);
    			 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPosCode,b.strPosName"
    			 						+ " FROM tblmenuitempricingdtl a  "
    			 						+ " left outer join tblposmaster b on a.strPosCode=b.strPosCode "); 
    			 		
    			 		if(!posCode.equalsIgnoreCase("All"))
    		            {
    			 			
    			 			hqlQuery.append(" Where a.strPOSCode='").append(posCode).append("' "); 
    		            }
    			 		hqlQuery.append(" order by a.strItemCode ");
    		            
    					
    			 		list=obBaseService.funGetList(hqlQuery, "sql");
    			 		
    			 		if(list.size()>0)
    		    		{
    			 			
    					for(int cnt=0;cnt<list.size();cnt++)
    					{
    						Object[] obj = (Object[]) list.get(cnt);
    					    clsPOSWiseItemIncentiveDtlBean objBean = new clsPOSWiseItemIncentiveDtlBean();
    						objBean.setStrItemCode(obj[0].toString());
    						objBean.setStrItemName(obj[1].toString());
    						objBean.setStrPOSCode(obj[2].toString());
//    						objBean.setStrPOSName(obj[3].toString());
    						objBean.setStrIncentiveType("amt");
    						objBean.setStrIncentiveValue("0.0");
    						listRet.add(objBean);
    					}
    		    		}
    		  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listRet;
	}

	public List funGetAllReasonMaster(String clientCode){
		 
		List list=null;
		try {
			list = obBaseService.funLoadAll(new clsReasonMasterModel(),clientCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return list;
	 }
	
}
