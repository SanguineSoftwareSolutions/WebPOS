package com.sanguine.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;

@Repository("clsStructureUpdateDao")
public class clsStructureUpdateDaoImpl implements clsStructureUpdateDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;

	@Override
	public void funUpdateStructure(String clientCode) {
		String sql = "";
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strAdd1`;";
		funExecuteQuery(sql);
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strAdd2`;";
		funExecuteQuery(sql);

		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strCity`;";
		funExecuteQuery(sql);
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strState`;";
		funExecuteQuery(sql);

		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strCountry`;";
		funExecuteQuery(sql);
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strPin`;";
		funExecuteQuery(sql);

		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strPhone`;";
		funExecuteQuery(sql);
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strMobile`;";
		funExecuteQuery(sql);

		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strFax`;";
		funExecuteQuery(sql);
		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strContact`;";
		funExecuteQuery(sql);

		sql = "ALTER TABLE `tblpropertymaster` DROP COLUMN `strEmail`;";
		funExecuteQuery(sql);
		
		sql = "CREATE TABLE IF NOT EXISTS `tblposlinkup` ("
				+ "  `strPOSItemCode` varchar(20) NOT NULL,"
				+ " `strPOSItemName` varchar(100) NOT NULL,"
				+ " `strWSItemCode` varchar(20) NOT NULL DEFAULT '',"
				+ " `strWSItemName` varchar(100) NOT NULL DEFAULT '',"
				+ "  `strClientCode` varchar(20) NOT NULL,"
				+ "  PRIMARY KEY (`strPOSItemCode`,`strClientCode`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

		funExecuteQuery(sql);

		sql = "CREATE TABLE IF NOT EXISTS `tblpossalesdtl` ("
				+ " `strPOSItemCode` varchar(20) NOT NULL,"
				+ "  `strPOSItemName` varchar(100) NOT NULL,"
				+ "  `dblQuantity` decimal(18,4) NOT NULL,"
				+ "  `dblRate` decimal(18,4) NOT NULL,"
				+ "  `strPOSCode` varchar(20) NOT NULL,"
				+ "  `dteBillDate` datetime NOT NULL,"
				+ "  `strClientCode` varchar(20) NOT NULL,"
				+ "  `strWSItemCode` varchar(20) NOT NULL DEFAULT '',"
				+ "  `strSACode` varchar(30) NOT NULL DEFAULT ''"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		funExecuteQuery(sql);

		sql = "CREATE TABLE IF NOT EXISTS `tblpotaxdtl` ("
				+ " `strPOCode` varchar(15) NOT NULL,"
				+ " `intId` bigint(20) NOT NULL AUTO_INCREMENT,"
				+ " `strTaxCode` varchar(15) NOT NULL,"
				+ " `strTaxDesc` varchar(100) NOT NULL,"
				+ "  `strTaxableAmt` decimal(18,4) NOT NULL DEFAULT '0.0000',"
				+ "  `strTaxAmt` decimal(18,4) NOT NULL DEFAULT '0.0000',"
				+ "  `strClientCode` varchar(15) NOT NULL,"
				+ "  PRIMARY KEY (`intId`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";
		funExecuteQuery(sql);
		
		sql=" CREATE TABLE IF NOT EXISTS `tbltallylinkup` ( "
				+ "  `strGroupCode` varchar(50) NOT NULL, "
				+ "  `strGDes` varchar(50) NOT NULL DEFAULT '',  "
				+ " `strGroupName` varchar(255) NOT NULL DEFAULT '', "
				+ "  `strTallyCode` varchar(50) NOT NULL,  "
				+ " `strUserCreated` varchar(50) NOT NULL, "
				+ "  `strUserEdited` varchar(50) NOT NULL, "
				+ "  `dteCreatedDate` datetime NOT NULL,  "
				+ " `dteLastModified` datetime NOT NULL, "
				+ "  `strClientCode` varchar(50) NOT NULL,"
				+ " PRIMARY KEY (`strGroupCode`)) ENGINE=InnoDB DEFAULT CHARSET=latin1; ";
		funExecuteQuery(sql);
		
		
		sql=" CREATE TABLE IF NOT EXISTS `tblimportdatadoc` ( `strOldDocCode` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strNewDocCode` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strTableName` VARCHAR(50) NOT NULL DEFAULT '', "
				+ "	`strSubCode1` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strSubCode2` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strSubCode3` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strTableType` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strClientCode` VARCHAR(50) NOT NULL DEFAULT '' )"
				+ "  COLLATE='latin1_swedish_ci' ENGINE=InnoDB  ";
		funExecuteQuery(sql);
		
		sql = " CREATE TABLE IF NOT EXISTS `tblcountrymaster` ( 	`strCountryCode` VARCHAR(10) NOT NULL,"
				+ "	`strCountryName` VARCHAR(50) NOT NULL,"
				+ "	`strUserCreated` VARCHAR(20) NOT NULL,"
				+ "	`dtCreatedDate` DATETIME NOT NULL,"
				+ "	`strUserModified` VARCHAR(20) NOT NULL,"
				+ "	`dtLastModified` DATETIME NOT NULL,"
				+ "	`strClientCode` VARCHAR(20) NOT NULL,"
				+ "	`strPropertyCode` VARCHAR(20) NOT NULL DEFAULT '',"
				+ "	`intGId` BIGINT(20) NOT NULL,"
				+ "	PRIMARY KEY (`strCountryCode`, `strClientCode`) )"
				+ " COLLATE='latin1_swedish_ci'"
				+ " ENGINE=InnoDB ; ";
		funExecuteQuery(sql);
		
		sql	= " CREATE TABLE IF NOT EXISTS `tblstatemaster` (	`strStateCode` VARCHAR(10) NOT NULL,"
				+ "	`strStateName` VARCHAR(50) NOT NULL,"
				+ "	`strStateDesc` VARCHAR(50) NOT NULL DEFAULT '',"
				+ "	`strCountryCode` VARCHAR(10) NOT NULL,"
				+ "	`strUserCreated` VARCHAR(20) NOT NULL,"
				+ "	`dtCreatedDate` DATETIME NOT NULL,"
				+ "	`strUserModified` VARCHAR(20) NOT NULL,"
				+ "	`dtLastModified` DATETIME NOT NULL,"
				+ "	`strClientCode` VARCHAR(20) NOT NULL,"
				+ "	`strPropertyCode` VARCHAR(20) NOT NULL DEFAULT '',"
				+ "	`intGId` BIGINT(20) NOT NULL,"
				+ "	PRIMARY KEY (`strStateCode`, `strClientCode`) )"
				+ " COLLATE='latin1_swedish_ci'"
				+ " ENGINE=InnoDB ; ";
		funExecuteQuery(sql);
		
		sql	= " CREATE TABLE IF NOT EXISTS `tblcitymaster` (	`strCityCode` VARCHAR(10) NOT NULL,"
				+ "	`strCityName` VARCHAR(50) NOT NULL,"
				+ "	`strCountryCode` VARCHAR(10) NOT NULL,"
				+ "	`strStateCode` VARCHAR(10) NOT NULL,"
				+ "	`strUserCreated` VARCHAR(20) NOT NULL,"
				+ "	`dtCreatedDate` DATETIME NOT NULL,"
				+ "	`strUserModified` VARCHAR(20) NOT NULL,"
				+ "	`dtLastModified` DATETIME NOT NULL,"
				+ "	`intGId` BIGINT(20) NOT NULL,"
				+ "	`strClientCode` VARCHAR(20) NOT NULL,"
				+ "	`strPropertyCode` VARCHAR(20) NOT NULL DEFAULT '',"
				+ "	PRIMARY KEY (`strCityCode`, `strClientCode`) ) COLLATE='latin1_swedish_ci'"
				+ " ENGINE=InnoDB ; ";
		funExecuteQuery(sql);
		
		
		sql="  CREATE TABLE IF NOT EXISTS `tblvehiclemaster` ( "
				+ "`strVehCode` VARCHAR(15) NOT NULL,"
				+ "	`strVehNo` VARCHAR(15) NOT NULL,"
				+ "	`strDesc` VARCHAR(50) NOT NULL DEFAULT '',"
				+ "	`strUserCreated` VARCHAR(50) NOT NULL,"
				+ "	`dtCreatedDate` DATE NOT NULL,"
				+ "	`strUserModified` VARCHAR(50) NOT NULL,"
				+ "	`dtLastModified` DATE NOT NULL,	"
				+ "`strClientCode` VARCHAR(15) NOT NULL,"
				+ " `intId` BIGINT(20) NOT NULL,"
				+ "	PRIMARY KEY (`strVehCode`, `strClientCode`) ) "
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB ;  ";
		
		funExecuteQuery(sql);
		
		
		sql="  CREATE TABLE IF NOT EXISTS `tblroutemaster` ("
				+ "	`strRouteCode` VARCHAR(15) NOT NULL,"
				+ "	`strRouteName` VARCHAR(255) NOT NULL,"
				+ "	`strDesc` VARCHAR(255) NOT NULL DEFAULT '',"
				+ "	`strUserCreated` VARCHAR(50) NOT NULL,"
				+ "	`strUserModified` VARCHAR(50) NOT NULL,"
				+ "	`dtCreatedDate` DATETIME NOT NULL,"
				+ " 	`dtLastModified` DATETIME NOT NULL, "
				+ "	`strClientCode` VARCHAR(15) NOT NULL,"
				+ " `intId` BIGINT(20) NOT NULL, "
				+ "	PRIMARY KEY (`strRouteCode`, `strClientCode`) ) "
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB ;  ";
		funExecuteQuery(sql);
		
		sql= " CREATE TABLE IF NOT EXISTS `tblvehroutedtl` ( "
				+ " 	`intId` BIGINT NOT NULL AUTO_INCREMENT,	"
				+ " `strRouteCode` VARCHAR(15) NOT NULL,"
				+ "	`strVehCode` VARCHAR(15) NOT NULL,	"
				+ "`dtFromDate` DATETIME NOT NULL,"
				+ "	`dtToDate` DATETIME NOT NULL,"
				+ "	`strUserCreated` VARCHAR(50) NOT NULL,"
				+ "	`strUserModified` VARCHAR(50) NOT NULL,"
				+ "	`dtCreatedDate` DATETIME NOT NULL,"
				+ "	`dtLastModified` DATETIME NOT NULL,"
				+ "	`strClientCode` VARCHAR(15) NOT NULL,"
				+ "	PRIMARY KEY (`intId`, `strClientCode`) )"
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB ;   ";
		
		funExecuteQuery(sql);
		
		sql=" CREATE TABLE IF NOT EXISTS `tbluserlocdtl` ( "
				+ "	`strUserCode` VARCHAR(20) NOT NULL,"
				+ "	`strPropertyCode` VARCHAR(10) NOT NULL,"
				+ "	`strLocCode` VARCHAR(20) NOT NULL,"
				+ "	`strClientCode` VARCHAR(20) NOT NULL "
				+ " ) COLLATE='latin1_swedish_ci' "
				+ " ENGINE=InnoDB; ";
		
		funExecuteQuery(sql);
		
		sql=" CREATE TABLE `tbltransportermaster` ( 	`strTransCode` VARCHAR(15) NOT NULL, "
				+ "	`strTransName` VARCHAR(50) NOT NULL, "
				+ "	`strDesc` VARCHAR(50) NOT NULL DEFAULT '',"
				+ " `strUserCreated` VARCHAR(50) NOT NULL, "
				+ "	`dteCreatedDate` DATE NOT NULL,"
				+ "	`strUserModified` VARCHAR(50) NOT NULL,"
				+ "	`dteLastModified` VARCHAR(50) NOT NULL,"
				+ "	`strClientCode` VARCHAR(50) NOT NULL,"
				+ "	`intId` BIGINT(20) NOT NULL DEFAULT '0',"
				+ "	PRIMARY KEY (`strTransCode`, `strClientCode`) ) "
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB; ";
		funExecuteQuery(sql);
		
		sql=" CREATE TABLE `tbltransportermasterdtl` ( 	`strTransCode` VARCHAR(15) NOT NULL, "
				+ " `intId` BIGINT(20) NOT NULL AUTO_INCREMENT, "
				+ "	`strVehCode` VARCHAR(15) NOT NULL, "
				+ "	`strVehNo` VARCHAR(20) NOT NULL DEFAULT '', "
				+ "	`strClientCode` VARCHAR(15) NOT NULL,"
				+ "	PRIMARY KEY (`intId`) ) "
				+ " COLLATE='latin1_swedish_ci' "
				+ " ENGINE=InnoDB AUTO_INCREMENT=13;  ";
		funExecuteQuery(sql);
		
		sql=" CREATE TABLE `tbllinkup` ( 	`strSGCode` VARCHAR(50) NOT NULL, "
				+ "	`strGDes` VARCHAR(50) NOT NULL DEFAULT '', "
				+ "	`strSGName` VARCHAR(255) NOT NULL DEFAULT '', "
				+ "	`strAccountCode` VARCHAR(50) NOT NULL, "
				+ "	`strUserCreated` VARCHAR(50) NOT NULL, "
				+ "	`strUserEdited` VARCHAR(50) NOT NULL, 	"
				+ " `dteCreatedDate` DATETIME NOT NULL, "
				+ "	`dteLastModified` DATETIME NOT NULL, "
				+ "	`strClientCode` VARCHAR(50) NOT NULL, "
				+ "	`strExSuppCode` VARCHAR(50) NOT NULL DEFAULT '', "
				+ "	`strExSuppName` VARCHAR(255) NOT NULL DEFAULT '', "
				+ "	PRIMARY KEY (`strSGCode`) ) "
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB ; ";
		
		funExecuteQuery(sql);
		
		sql= " CREATE TABLE `tblproductstandard` (  	`id` BIGINT(10) NOT NULL AUTO_INCREMENT,  "
				+ "	 `strProdCode` VARCHAR(10) NOT NULL,  "
				+ "	`dblQty` DECIMAL(18,2) NOT NULL DEFAULT '0.00',  "
				+ "	`dblUnitPrice` DECIMAL(18,2) NOT NULL DEFAULT '0.00', "
				+ " 	`dblTotalPrice` DECIMAL(18,2) NOT NULL DEFAULT '0.00',  "
				+ "	`strRemarks` VARCHAR(250) NOT NULL DEFAULT '',  "
				+ "	`strClientCode` VARCHAR(10) NOT NULL DEFAULT '',  "
				+ "	`strStandardType` VARCHAR(20) NOT NULL, "
				+ " 	PRIMARY KEY (`id`, `strClientCode`) "
				+ " ) COLLATE='latin1_swedish_ci' "
				+ " ENGINE=InnoDB AUTO_INCREMENT=16 ; ";
		
		funExecuteQuery(sql);
		
		sql = " CREATE TABLE IF NOT EXISTS `tblcurrencymaster` (  "
				+ "	`strCurrencyCode` VARCHAR(30) NOT NULL, "
				+ "	`intID` INT(11) NOT NULL DEFAULT '0', "
				+ "	`strCurrencyName` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strShortName` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strBankName` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strSwiftCode` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strIbanNo` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strRouting` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strUserCreated` VARCHAR(10) NOT NULL, "
				+ "	`dtCreatedDate` DATETIME NOT NULL, "
				+ "	`strUserModified` VARCHAR(10) NOT NULL, "
				+ "	`dtLastModified` DATETIME NOT NULL, "
				+ "	`strAccountNo` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`dblConvToBaseCurr` DECIMAL(18,2) NOT NULL DEFAULT '1.00', "
				+ "	`strSubUnit` VARCHAR(30) NOT NULL DEFAULT '', "
				+ "	`strClientCode` VARCHAR(30) NOT NULL, "
				+ "	PRIMARY KEY (`strCurrencyCode`, `strClientCode`) ) "
				+ " COLLATE='latin1_swedish_ci' ENGINE=InnoDB ; ";
		
		funExecuteQuery(sql);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Indexing in table
		
		sql= "ALTER TABLE `tblpossalesdtl`	ADD COLUMN `intId` BIGINT NOT NULL AUTO_INCREMENT AFTER `strWSItemCode`,DROP PRIMARY KEY,ADD PRIMARY KEY (`intId`, `strClientCode`);";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblmisdtl` "
			+" ADD INDEX `strMISCode_strProdCode_strClientCode` (`strMISCode`, `strProdCode`, `strClientCode`);";
		funExecuteQuery(sql);
		
		sql = "ALTER TABLE `tblreqdtl` "
				+ " ADD INDEX `strReqCode_strProdCode_strClientCode` (`strReqCode`, `strProdCode`, `strClientCode`);";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblstocktransferhd` "
			+" ADD COLUMN `dblTotalAmt` DECIMAL(18,4) NOT NULL DEFAULT '0.0000' AFTER `intLevel` ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblstocktransferdtl` "
			+" ADD COLUMN `dblTotalPrice` DECIMAL(18,4) NOT NULL DEFAULT '0.0000' AFTER `strClientCode` ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpurchaseindenddtl` "
		   +" CHANGE COLUMN `dblQty` `dblQty` DECIMAL(18,4) NULL DEFAULT NULL AFTER `strProdCode`;";
		funExecuteQuery(sql);
		
		
		sql= " ALTER TABLE `tblproductionorderhd` HANGE COLUMN `strAuthorise` `strAuthorise` VARCHAR(3) NULL DEFAULT NULL AFTER `strBOMFlag`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblbommasterhd`	ADD COLUMN `strBOMType` VARCHAR(1) NOT NULL DEFAULT 'R' AFTER `strMethod`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` ADD COLUMN `dblYieldPer` DECIMAL(10,0) NOT NULL DEFAULT '100.00' AFTER `strNonStockableItem`;  ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblproductmaster`  ADD COLUMN `strBarCode` VARCHAR(50) NOT NULL DEFAULT 'NA' AFTER `dblYieldPer`;" ;
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblproductionorderhd` CHANGE COLUMN `strAuthorise` `strAuthorise` VARCHAR(3) NULL DEFAULT NULL AFTER `strBOMFlag`;" ;
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpartymaster` ADD COLUMN `strPartyIndi` VARCHAR(1) NOT NULL DEFAULT '' AFTER `strClientCode` ; ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblrateconthd`	CHANGE COLUMN `strAuthorise` `strAuthorise` VARCHAR(3) NOT NULL DEFAULT 'No' AFTER `dtLastModified`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` CHANGE COLUMN `strDesc` `strDesc` VARCHAR(1000) NOT NULL DEFAULT '' AFTER `strSaleNo`, "
				+ "	CHANGE COLUMN `strSpecification` `strSpecification` VARCHAR(1000) NOT NULL DEFAULT '' AFTER `strType`;  ";
		funExecuteQuery(sql);
		
		sql = "ALTER TABLE `tblworkorderdtl` DROP PRIMARY KEY; " ;
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblgrnhd`	CHANGE COLUMN `strNarration` `strNarration` VARCHAR(1000) NOT NULL DEFAULT '' AFTER `dblTotal`; ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblgrnhd` CHANGE COLUMN `strBillNo` `strBillNo` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strPONo`; ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblprodchar` CHANGE COLUMN `dblValue` `strTollerance` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strCharCode` ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblprodchar` ADD COLUMN `intId` BIGINT NOT NULL AUTO_INCREMENT AFTER `strProdCode`, DROP PRIMARY KEY,	ADD PRIMARY KEY (`intId`, `strClientCode`); ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblcompanymaster`	DROP PRIMARY KEY, DROP INDEX `intId`,	ADD PRIMARY KEY (`intId`); ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpropertysetup`	ADD COLUMN `strShowAllProdToAllLoc` CHAR(1) NULL DEFAULT 'Y' AFTER `strMonthEnd` ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblattachdocument` ADD COLUMN `strModuleName` VARCHAR(50) NOT NULL AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblstocktransferhd` CHANGE COLUMN `strWOCode` `strWOCode` VARCHAR(500) NOT NULL DEFAULT '' AFTER `strMaterialIssue`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpropertysetup`CHANGE COLUMN `strRangeAdd` `strRangeAdd` VARCHAR(255) NULL DEFAULT NULL AFTER `strMask`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblprodsuppmaster`	ADD COLUMN `dblMargin` DECIMAL(18,2) NOT NULL DEFAULT '0' AFTER `strUOM`;  ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbltaxhd` ADD COLUMN `strTaxOnTaxCode` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup`	ADD COLUMN `strLocWiseProductionOrder` CHAR(1) NULL DEFAULT 'Y' AFTER `strShowAllProdToAllLoc`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` ADD COLUMN `dblMRP` DECIMAL(18,2) NOT NULL DEFAULT '0.00' AFTER `strBarCode`;  ";
		funExecuteQuery(sql);
		
		
		sql= " ALTER TABLE `tblpropertysetup` "
					+ " ADD COLUMN `strShowStockInOP` CHAR(1) NULL DEFAULT 'Y' AFTER `strLocWiseProductionOrder`, "
					+ " ADD COLUMN `strShowAvgQtyInOP` CHAR(1) NULL DEFAULT 'Y' AFTER `strShowStockInOP`, "
					+ " ADD COLUMN `strShowStockInSO` CHAR(1) NULL DEFAULT 'Y' AFTER `strShowAvgQtyInOP`, "
					+ " ADD COLUMN `strShowAvgQtyInSO` CHAR(1) NULL DEFAULT 'Y' AFTER `strShowStockInSO`, "
					+ " ADD COLUMN `strDivisionAdd` VARCHAR(255) NOT NULL DEFAULT '' AFTER `strShowAvgQtyInSO`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblsubgroupmaster` ADD COLUMN `strExciseable` VARCHAR(1) NOT NULL DEFAULT 'N' AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tbltaxhd` ADD COLUMN `strCalTaxOnMRP` VARCHAR(1) NOT NULL DEFAULT 'N' AFTER `strTaxOnSubGroup`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` ADD COLUMN `strPickMRPForTaxCal` VARCHAR(50) NOT NULL DEFAULT 'N' AFTER `dblMRP`;";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblsubgroupmaster`ADD COLUMN `strExciseChapter` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strExciseable` ; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpropertysetup`	ADD COLUMN `strEffectOfDiscOnPO` VARCHAR(1) NULL DEFAULT 'Y' AFTER `strDivisionAdd`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup`	ADD COLUMN `strInvFormat` VARCHAR(30) NOT NULL AFTER `strEffectOfDiscOnPO`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` ADD COLUMN `strExciseable` VARCHAR(1) NOT NULL DEFAULT 'N' AFTER `strPickMRPForTaxCal`; ";		
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tbltaxhd` ADD COLUMN `dblAbatement` DECIMAL(18,2) NOT NULL DEFAULT '0' AFTER `strCalTaxOnMRP`;";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tbltaxhd` ADD COLUMN `strTOTOnMRPItems` VARCHAR(1) NOT NULL DEFAULT 'N' AFTER `dblAbatement`;";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpropertysetup`	ADD COLUMN `strECCNo` VARCHAR(150) NOT NULL DEFAULT '' AFTER `strInvFormat`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblvehiclemaster` ADD COLUMN `intId` BIGINT NOT NULL AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbluserdtl` 	ADD COLUMN `strModule` VARCHAR(10) NULL DEFAULT NULL AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbluserdefinedreport`  ALTER `strTable` DROP DEFAULT; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbluserdefinedreport` CHANGE COLUMN `strTable` `strTable` VARCHAR(500) NOT NULL AFTER `strType`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbluserdefinedreport` ALTER `strGroupBy` DROP DEFAULT, ALTER `strSortBy` DROP DEFAULT; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbluserdefinedreport` "
				+ "	CHANGE COLUMN `strGroupBy` `strGroupBy` VARCHAR(500) NOT NULL AFTER `strFieldSize`, "
				+ "	CHANGE COLUMN `strSortBy` `strSortBy` VARCHAR(500) NOT NULL AFTER `strGroupBy`;  ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblpartymaster` 	ADD COLUMN `strECCNo` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strOperational`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblsubgroupmaster` ADD COLUMN `intSortingNo` INT(10) NOT NULL AFTER `strExciseChapter`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblsubgroupmaster`  ADD COLUMN `strSGDescHeader` VARCHAR(100) NOT NULL DEFAULT '' AFTER `intSortingNo`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblexcelimport` 	ADD COLUMN `strBarCode` VARCHAR(50) NOT NULL DEFAULT '' AFTER `dblPriceChangePercentage`; ";
		funExecuteQuery(sql);
		
		sql ="ALTER TABLE `tblexcelimport` 	CHANGE COLUMN `strBankAdd1` `strBankAdd1` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strBankName`, "
				+ " CHANGE COLUMN `strBankAdd2` `strBankAdd2` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strBankAdd1`,  "
				+ "	CHANGE COLUMN `strMAdd1` `strMAdd1` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strAcCrCode`, "
				+ "	CHANGE COLUMN `strMAdd2` `strMAdd2` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strMAdd1`, "
				+ "	CHANGE COLUMN `strBAdd1` `strBAdd1` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strMCountry`, "
				+ "	CHANGE COLUMN `strBAdd2` `strBAdd2` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strBAdd1`, "
				+ "	CHANGE COLUMN `strSAdd1` `strSAdd1` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strBCountry`, "
				+ "	CHANGE COLUMN `strSAdd2` `strSAdd2` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strSAdd1`; ";
		funExecuteQuery(sql);
		
		sql ="ALTER TABLE `tblpartymaster` "
				+ "	ADD COLUMN `dtInstallions` DATE NOT NULL DEFAULT '1900-01-01' AFTER `strECCNo`, "
				+ "	ADD COLUMN `strAccManager` VARCHAR(100) NOT NULL DEFAULT '' AFTER `dtInstallions`; ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblpartymaster`  "
			+ " ADD COLUMN `strDebtorCode` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strAccManager`; ";
		funExecuteQuery(sql);
		
//		sql =" ALTER TABLE `tblarlinkup`"
//				+ "	ADD COLUMN `strExSuppCode` VARCHAR(50) NOT NULL DEFAULT ''  AFTER `strClientCode`, "
//				+ "	ADD COLUMN `strExSuppName` VARCHAR(255) NOT NULL DEFAULT ''  AFTER `strExSuppCode`; ";
//		
//		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblstockpostingdtl`"
			+ "	CHANGE COLUMN `strDisplyVariance` `strDisplyVariance` VARCHAR(100) NOT NULL DEFAULT '' AFTER `strDisplyQty`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblstockadjustmentdtl` "
				+ "	CHANGE COLUMN `dblQty` `dblQty` DECIMAL(18,4) NOT NULL DEFAULT '0.00' AFTER `strProdCode`,  "
				+ "	CHANGE COLUMN `dblPrice` `dblPrice` DECIMAL(18,4) NOT NULL DEFAULT '0.00' AFTER `strType`, "
				+ "	CHANGE COLUMN `dblWeight` `dblWeight` DECIMAL(18,4) NOT NULL DEFAULT '0.00' AFTER `dblPrice`; ";
		
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblstockadjustmentdtl`"
				+ "	ADD COLUMN `strWSLinkedProdCode` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strDisplayQty`; ";
		funExecuteQuery(sql);
			
		sql="ALTER TABLE `tbluserlocdtl` "
				+ "	ADD COLUMN `strModule` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strClientCode`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblcompanymaster` "
				+ "	ADD COLUMN `strWebBookAPGLModule` VARCHAR(3) NULL DEFAULT 'No' AFTER `strPassword`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strSMSProvider` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strECCNo`, "
				+ " ADD COLUMN `strSMSAPI` VARCHAR(300) NOT NULL DEFAULT '' AFTER `strSMSProvider`,  "
				+ "	ADD COLUMN `strSMSContent` VARCHAR(500) NOT NULL DEFAULT '' AFTER `strSMSAPI`;" ;		
		funExecuteQuery(sql);
		
		sql="  ALTER TABLE `tblpurchaseindendhd` "
				+ "	ADD COLUMN `strAgainst` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strClosePI`, "
				+ "	ADD COLUMN `strDocCode` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strAgainst`; ";
		funExecuteQuery(sql);
		
		
		sql=" DROP FUNCTION IF EXISTS `funGetUOM`; ";
		funExecuteQuery(sql);
		
		sql= " CREATE DEFINER=`root`@`localhost` FUNCTION `funGetUOM`(  	`transQty` varchar(20),  	`recipeConv` double,  	`issueConv` double,  	`receivedUOM` varchar(10),  	`recipeUOM` varchar(10)  )  "
				+ " RETURNS varchar(30) CHARSET latin1 "
				+ "LANGUAGE SQL "
				+ "DETERMINISTIC "
				+ "CONTAINS SQL "
				+ "SQL SECURITY DEFINER "
				+ "COMMENT '' "
				+ "BEGIN "
				+ "  DECLARE uomString varchar(30); "
				+ " set uomString=concat(if(Left(transQty,INSTR(transQty,'.')-1), concat(Left(transQty,INSTR(transQty,'.')-1) " 
				+ "        ,concat(' ',concat(receivedUOM,'.'))),'') "
				+ "        ,if(MID(transQty,INSTR(transQty,'.'),LENGTH(transQty)) "
				+ "        , concat((MID(transQty,INSTR(transQty,'.'),LENGTH(transQty)))*(recipeConv * issueConv), concat(' ',recipeUOM)),'')); "
				+ " RETURN (uomString); "
				+ " END;  ";
		funExecuteQuery(sql);
		
		
		sql= " ALTER TABLE `tblpartymaster` "
				+ "	ADD COLUMN `strGSTNo` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strDebtorCode`;    ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tbltaxhd` "
				+ "	ADD COLUMN `strShortName` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strTOTOnMRPItems`, "
				+ "	ADD COLUMN `strGSTNo` VARCHAR(20) NOT NULL DEFAULT '' AFTER `strShortName`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` "
			+ "	ADD COLUMN `strProdNameMarathi` VARCHAR(200) NOT NULL DEFAULT '' "
			+ " AFTER `strExciseable`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductmaster` "
				+ "	ADD COLUMN `strManufacturerCode` VARCHAR(10) NOT NULL DEFAULT '' AFTER `strProdNameMarathi`; ";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tbluserhd` "
			+" ADD COLUMN `strShowDashBoard` VARCHAR(1) NOT NULL DEFAULT 'N' AFTER `strClientCode`;";
		funExecuteQuery(sql);
		
		sql="ALTER TABLE `tbltaxhd` CHANGE COLUMN `strGSTNo` "
				+ " `strGSTNo` VARCHAR(50) NOT NULL DEFAULT '' AFTER `strShortName`;";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup`  "
				+ " CHANGE COLUMN `strInvFormat` `strInvFormat` VARCHAR(150) NOT NULL AFTER `strEffectOfDiscOnPO`;  " ;
		
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strInvNote` VARCHAR(500) NOT NULL DEFAULT '' AFTER `strSMSContent`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strCurrencyCode` VARCHAR(15) NOT NULL DEFAULT '' AFTER `strInvNote`;  ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strShowAllPropCustomer` VARCHAR(1) NOT NULL DEFAULT 'Y' AFTER `strCurrencyCode`; ";
		funExecuteQuery(sql);
		
		sql =  " ALTER TABLE `tblpartymaster` "
				+ "	ADD COLUMN `strPNHindi` VARCHAR(200) NOT NULL DEFAULT ''  AFTER `strGSTNo`; " ;
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpartymaster` "
				+ "	ADD COLUMN `strLocCode` VARCHAR(15) NOT NULL DEFAULT '' AFTER `strPNHindi`; ";
		funExecuteQuery(sql);
			
		sql=" ALTER TABLE `tblpartymaster` "
				+ "	ADD COLUMN `strPropCode` VARCHAR(10) NOT NULL DEFAULT '01' AFTER `strLocCode`; ";
		funExecuteQuery(sql);
		
		
		sql=" ALTER TABLE `tblreorderlevel` "
				+ "	ADD COLUMN `dblPrice` DOUBLE NOT NULL DEFAULT '0.0' AFTER `strClientCode`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblinvoicehd` "
				+ "	ADD COLUMN `strCurrencyCode` VARCHAR(15) NOT NULL DEFAULT '' AFTER `strSettlementCode`, "
				+ "	ADD COLUMN `dblCurrencyConv` DOUBLE NOT NULL DEFAULT '1.0' AFTER `strCurrencyCode`; ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strEffectOfInvoice` VARCHAR(20) NOT NULL DEFAULT 'DC' AFTER `strShowAllPropCustomer`;  ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblbommasterdtl` "
				+ "	CHANGE COLUMN `dblQty` `dblQty` DECIMAL(18,4) NOT NULL DEFAULT '0.00' AFTER `strChildCode`,  "
				+ "	CHANGE COLUMN `dblWeight` `dblWeight` DECIMAL(18,4) NOT NULL DEFAULT '0.00' AFTER `dblQty`; "; 		
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblreqhd`  "
				+ "	ADD COLUMN `strReqFrom` VARCHAR(10) NOT NULL DEFAULT 'System' AFTER `dtReqiredDate`;  ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblstockpostinghd` "
			+ "	ADD COLUMN `strPhyStkFrom` VARCHAR(10) NOT NULL DEFAULT 'System' AFTER `strConversionUOM`;  ";
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblstockpostingdtl` "
				+ "	ADD COLUMN `dblActualRate` DECIMAL(10,2) NOT NULL DEFAULT '0.00' AFTER `strDisplyVariance`; ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblstockpostingdtl` "
			+ "	ADD COLUMN `dblActualValue` DECIMAL(10,2) NOT NULL DEFAULT '0.00' AFTER `dblActualRate`; ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tbllinkup` "
				+ "	ADD COLUMN `strPropertyCode` VARCHAR(10) NOT NULL DEFAULT '' AFTER `strExSuppName`; ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblpropertysetup` "
				+ "	ADD COLUMN `strEffectOfGRNWebBook` VARCHAR(20) NOT NULL DEFAULT 'Payment' AFTER `strEffectOfInvoice`; ";
		
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tbllinkup` 	DROP PRIMARY KEY,  "
				+ "	ADD PRIMARY KEY (`strSGCode`, `strPropertyCode`, `strClientCode`);  ";
		funExecuteQuery(sql);
		
		sql=" ALTER TABLE `tblproductstandard` "
				+ "	ADD COLUMN `strLocCode` VARCHAR(20) NOT NULL AFTER `strStandardType`, "
				+ "	ADD COLUMN `strPropertyCode` VARCHAR(20) NOT NULL AFTER `strLocCode`, "
				+ "	DROP COLUMN `strLocCode`, "
				+ "	DROP COLUMN `strPropertyCode` ";
		funExecuteQuery(sql);

		sql=" ALTER TABLE `tblproductstandard` "
				+ "	DROP PRIMARY KEY, "
				+ "	ADD PRIMARY KEY (`strProdCode`, `strLocCode`, `strPropertyCode`, `strClientCode`); ";
		funExecuteQuery(sql);
		
		sql = " ALTER TABLE `tblgrnhd` "
				+ "	ADD COLUMN `dblRoundOff` DOUBLE(18,4) NULL DEFAULT '0' AFTER `intLevel`; ";
		
		funExecuteQuery(sql);
		
		sql= " ALTER TABLE `tblproductstandard`  "
				+ "	CHANGE COLUMN `id` `id` BIGINT(10) NOT NULL DEFAULT '0' FIRST, "
				+ "	ADD COLUMN `strLocCode` VARCHAR(20) NOT NULL AFTER `strStandardType`, "
				+ "	ADD COLUMN `strPropertyCode` VARCHAR(20) NOT NULL AFTER `strLocCode`, "
				+ "	DROP PRIMARY KEY, "
				+ "	ADD PRIMARY KEY (`strProdCode`, `strClientCode`, `strLocCode`, `strPropertyCode`);  ";
		funExecuteQuery(sql);
		
		
		
		
		
		
		
		
		
		
		
		////////// set Default Value in product master ////////////////////
		
		sql ="update tblproductmaster a  set a.dblReceiveConversion='1.00' where a.dblReceiveConversion='0.00' ;" ;
		funExecuteQuery(sql);
		
		sql ="update tblproductmaster a  set a.dblIssueConversion='1.00' where a.dblIssueConversion='0.00' ; ";  
		funExecuteQuery(sql);	
		
		sql ="update tblproductmaster a  set a.dblRecipeConversion='1.00' where a.dblRecipeConversion='0.00' ; ";  
		funExecuteQuery(sql);
		

		/////////////////////////////////////////////////////// ////////////////////
		
		
		
		
		
		
		
		
 
		sql=" DROP TABLE IF EXISTS `tbltreemast`; ";
		funExecuteQuery(sql);		
		sql=" CREATE TABLE IF NOT EXISTS `tbltreemast` (  "
				+ "  `strFormName` varchar(50) NOT NULL,  "
				+ "  `strFormDesc` varchar(200) NOT NULL,  "
				+ "   `strRootNode` varchar(50) NOT NULL,  "
				+ "   `intRootIndex` int(11) NOT NULL,  "
				+ " `strType` varchar(50) NOT NULL, "
				+ "  `intFormKey` int(11) NOT NULL, "
				+ " `intFormNo` int(11) NOT NULL, "
				+ " `strImgSrc` varchar(50) NOT NULL, "
				+ " `strImgName` varchar(100) NOT NULL, "
				+ " `strModule` varchar(15) NOT NULL, "
				+ " `strTemp` int(11) NOT NULL, "
				+ " `strActFile` varchar(3) NOT NULL,  "
				+ " `strHelpFile` varchar(150) NOT NULL, "
				+ " `strProcessForm` varchar(4) NOT NULL DEFAULT 'NA', "
				+ " `strAutorisationForm` varchar(4) NOT NULL DEFAULT 'NA', "
				+ " `strRequestMapping` varchar(50) DEFAULT NULL, "
				+ " `strAdd` varchar(255) DEFAULT NULL, "
				+ " `strAuthorise` varchar(255) DEFAULT NULL, "
				+ " `strDelete` varchar(255) DEFAULT NULL, "
				+ " `strDeliveryNote` varchar(255) DEFAULT NULL, "
				+ " `strDirect` varchar(255) DEFAULT NULL, "
				+ " `strEdit` varchar(255) DEFAULT NULL, "
				+ " `strGRN` varchar(255) DEFAULT NULL, "
				+ " `strGrant` varchar(255) DEFAULT NULL, "
				+ " `strMinimumLevel` varchar(255) DEFAULT NULL, "
				+ " `strOpeningStock` varchar(255) DEFAULT NULL, "
				+ " `strPrint` varchar(255) DEFAULT NULL, "
				+ " `strProductionOrder` varchar(255) DEFAULT NULL, "
				+ " `strProject` varchar(255) DEFAULT NULL, "
				+ " `strPurchaseIndent` varchar(255) DEFAULT NULL, "
				+ " `strPurchaseOrder` varchar(255) DEFAULT NULL, "
				+ " `strPurchaseReturn` varchar(255) DEFAULT NULL, "
				+ " `strRateContractor` varchar(255) DEFAULT NULL, "
				+ " `strRequisition` varchar(255) DEFAULT NULL, "
				+ " `strSalesOrder` varchar(255) DEFAULT NULL, "
				+ " `strSalesProjection` varchar(255) DEFAULT NULL, "
				+ " `strSalesReturn` varchar(255) DEFAULT NULL, "
				+ " `strServiceOrder` varchar(255) DEFAULT NULL, "
				+ " `strSubContractorGRN` varchar(255) DEFAULT NULL, "
				+ " `strView` varchar(255) DEFAULT NULL, "
				+ " `strWorkOrder` varchar(255) DEFAULT NULL, "
				+ " `strAuditForm` varchar(255) DEFAULT NULL, "
				+ " `strMIS` varchar(255) DEFAULT NULL, "
				+ " PRIMARY KEY (`strFormName`, `strModule`) "
				+ " ) ENGINE=InnoDB DEFAULT CHARSET=latin1; ";
		
		funExecuteQuery(sql);
		
		sql= " DELETE FROM `tbltreemast`; " ;
		funExecuteQuery(sql);
		
/*----------------WebStock Forms only---------------------------*/		
		String strIndustryType="";
		List<clsCompanyMasterModel> listClsCompanyMasterModel=objSetupMasterService.funGetListCompanyMasterModel();
		if(listClsCompanyMasterModel.size()>0){
			clsCompanyMasterModel objCompanyMasterModel=listClsCompanyMasterModel.get(0);
			strIndustryType=objCompanyMasterModel.getStrIndustryType();
		}
		
		
		sql=" INSERT INTO `tbltreemast` (`strFormName`, `strFormDesc`, `strRootNode`, `intRootIndex`, `strType`, `intFormKey`, `intFormNo`, `strImgSrc`, `strImgName`, `strModule`, `strTemp`, `strActFile`, `strHelpFile`, `strProcessForm`, `strAutorisationForm`, `strRequestMapping`, `strAdd`, `strAuthorise`, `strDelete`, `strDeliveryNote`, `strDirect`, `strEdit`, `strGRN`, `strGrant`, `strMinimumLevel`, `strOpeningStock`, `strPrint`, `strProductionOrder`, `strProject`, `strPurchaseIndent`, `strPurchaseOrder`, `strPurchaseReturn`, `strRateContractor`, `strRequisition`, `strSalesOrder`, `strSalesProjection`, `strSalesReturn`, `strServiceOrder`, `strSubContractorGRN`, `strView`, `strWorkOrder`, `strAuditForm`, `strMIS`) VALUES "
			
//				+" ('Cost Center\\r\\n', 'Cost Center\\r\\n', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAccountHolderMaster', 'Account Holder Master', 'Master', 2, 'M', 2, 2, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmAccountHolderMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmACGroupMaster', 'Group Master', 'Master', 4, 'M', 4, 4, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmACGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAgeingReport', 'Ageing Rport', 'Sub Contracting Report', 3, 'R', 73, 1, '1', 'default.png', '6', 1, '1', '1', 'No', 'No', 'frmAgeingReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAttributeMaster', 'Attribute Master', 'Master', 8, 'M', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmAttributeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAttributeValueMaster', 'Attribute Value Master', 'Master', 8, 'M', 2, 2, '3', 'Attribute-Value-Master.png', '3', 3, '3', '3', 'NO', 'YES', 'frmAttributeValueMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAuditFlash', 'Audit Flash', 'Tools', 8, 'L', 48, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmAuditFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmAuthorisationTool', 'Authorisation', 'Tools', 8, 'L', 10, 2, '1', 'Authourisation.png', '1', 1, '1', '1', 'NO', 'NO', 'frmAuthorisationTool.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmBankMaster', 'Bank Master', 'Master', 3, 'M', 3, 3, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmBankMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmBillPassing', 'Bill Passing', 'Accounts', 5, 'T', 3, 1, '1', 'Bill-Passing.png', '1', 1, '1', '1', 'NO', 'YES', 'frmBillPassing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmBOMMaster', 'Recipe Master', 'Master', 8, 'M', 4, 3, '2', 'ReciepeMaster-BOM-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmBOMMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmCharacteristicsMaster', 'Characteristics Master', 'Master', 8, 'M', 5, 4, '1', 'Characteristics-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmCharMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmChargeMaster', 'Charge Master', 'Master', 5, 'M', 5, 5, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmChargeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmChargeProcessing', 'Charge Processing', 'Processing', 2, 'P', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'YES', 'NO', 'frmChargeProcessing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmCompanyMaster', 'Company Master', 'Master', 1, 'M', 1, 10, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmCompanyMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmCompanyTypeMaster', 'Company Type Master', 'Master', 1, 'M', 1, 12, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmCompanyTypeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmConsReceiptValMiscSuppReqReport', 'Consolidated Receipt Value Misc Suppler Required Report', 'Purchases', 7, 'R', 51, 29, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmConsReceiptValMiscSuppReqReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmConsReceiptValStoreWiseBreskUPReport', 'Consolidated Receipt value Store Wise BreakUP', 'Purchases', 7, 'R', 51, 30, '1', 'default.png', '1', 1, '1', '1', 'NO', 'No', 'frmConsReceiptValStoreWiseBreskUPReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmCostOfIssue', 'Cost Of Issue', 'Stores', 9, 'R', 42, 20, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmCostOfIssue.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmCustomerMaster', 'Customer Master', 'Master', 1, 'M', 52, 1, '1', 'default.png', '6', 1, '1', '1', 'NO', 'No', 'frmCustomerMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDebtorLedger', 'Debtor Ledger Tool', 'Tools', 1, 'L', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmDebtorLedger.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDebtorReceipt', 'Debtor Receipt', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmDebtorReceipt.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeleteTransaction', 'Delete Transaction', 'Tools', 8, 'L', 46, 24, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryChallan', 'Delivery Challan', 'Sales', 2, 'T', 56, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmDeliveryChallan.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryChallanList', 'Delivery Challan List', 'Sub contracting Report', 3, 'R', 63, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryChallanSlip', 'Delivery Challan Slip', 'Sub contracting Report', 3, 'R', 62, 4, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryChallanSlipInvoice', 'Delivery Challan Slip Invoice', 'Sub contracting Report', 3, 'R', 74, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanSlipInvoice.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryNote', 'Delivery Note', 'Sales', 2, 'T', 67, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmDeliveryNote.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryNoteList', 'Delivery Note List', 'Sub contracting Report', 3, 'R', 68, 10, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryNoteList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDeliveryNoteSlip', 'Delivery Note Slip', 'Sub contracting Report', 3, 'R', 67, 9, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryNoteSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDocumentReconciliation', 'Document Reconciliation', 'Tools', 8, 'L', 10, 2, '1', 'Document_Reconciliation_Fla.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDocumentReconciliation.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDueDateMonitoringReport', 'Due Date Monitoring Report', 'Sub Contracting Report', 3, 'R', 75, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDueDateMonitoringReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmEditOtherInfo', 'Edit Other Info', 'Master', 1, 'M', 1, 15, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmEditOtherInfo.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmEmailSending', 'Sending Email', 'Tools', 8, 'L', 51, 29, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmEmailSending.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseAbstractReport', 'Abstract Report', 'Reports', 3, 'R', 21, 3, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseAbstractReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseBillGenerate', 'Bill Generate', 'Transaction', 2, 'L', 19, 3, '1', 'default.png', '2', 1, '1', '1', 'YES', 'NO', 'frmExciseBillGenerate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseBrandMaster', 'Brand Master', 'Master', 1, 'M', 3, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseBrandMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseBrandOpeningMaster', 'Brand Opening Master', 'Transaction', 3, 'T', 8, 4, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseBrandOpeningMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseCategoryMaster', 'Category Master', 'Master', 1, 'M', 13, 5, '1', 'default.png', '0', 1, '1', '1', 'NO', 'NO', 'frmExciseCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseChallan', 'Excise Challan', 'Sales', 2, 'T', 57, 4, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmExciseChallan.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseChataiReport', 'Chatai Report', 'Reports', 3, 'R', 23, 5, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseChataiReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseCityMaster', 'City Master', 'Master', 1, 'M', 18, 9, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseCityMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseDeleteTransaction', 'Delete Transction', 'Transaction', 2, 'R', 20, 5, '1', 'default.png', '2', 1, '1', '1', 'YES', 'NO', 'frmExciseDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseFLR3A', 'FLR- III (3A) Report', 'Reports', 3, 'R', 2, 1, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR3A.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseFLR4Report', 'FLR- 4 Report', 'Reports', 3, 'R', 22, 4, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR4Report.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseFLR6', 'FLR-VI (6A) Report', 'Reports', 3, 'R', 5, 2, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR6.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseLicenceMaster', 'Licence Master', 'Master', 1, 'M', 15, 7, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseLicenceMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseLocationMaster', 'Location Master', 'Master', 1, 'M', 17, 8, '1', 'Location-Master.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseLocationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExcisePhysicalStkPosting', 'Excise Physical Stk Posting', 'Store', 5, 'L', 4, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePhysicalStkPosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExcisePOSDataExportImport', 'Excise POS DataExport Import', 'Store', 5, 'L', 7, 2, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSDataExportImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExcisePOSLinkUp', 'POS Excise Link Up', 'Tools', 4, 'L', 10, 1, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExcisePOSSale', 'Excise POS Sale', 'Tools', 4, 'L', 13, 2, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSSale.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExcisePropertySetUp', 'Property Set Up', 'Master', 1, 'M', 14, 6, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePropertySetUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseRecipeMaster', 'Excise Recipe Master', 'Master', 1, 'M', 16, 5, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseRecipeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseSales', 'Excise Sale', 'Transaction', 2, 'T', 6, 2, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSalesMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseSizeMaster', 'Size Master', 'Master', 1, 'M', 11, 3, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSizeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseSubCategoryMaster', 'Sub Category Master', 'Master', 1, 'M', 16, 4, '1', 'default.png', '0', 1, '1', '1', 'NO', 'NO', 'frmExciseSubCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseSupplierMaster', 'Supplier Master', 'Master', 1, 'M', 9, 4, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSupplierMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExciseTPMaster', 'T P Master', 'Transaction', 2, 'T', 1, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseTPMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmExpiryFlash', 'Expiry Flash', 'Tools', 8, 'L', 49, 27, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmExpiryFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmFoodCost', 'Cost Analysis', 'Stores', 9, 'R', 53, 33, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmFoodCost.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmGRN', 'GRN(Good Receiving Note)', 'Receiving', 4, 'T', 6, 1, '1', 'GRN.png', '1', 1, '1', '1', 'YES', 'YES', 'frmGRN.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmGRNRegisterReport', 'GRN Register Report', 'Receiving Reports', 9, 'R', 73, 5, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGRNRegisterReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmGrnSlip', 'Grn Slip', 'Receiving Reports', 9, 'R', 35, 4, '1', 'GRN-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGrnSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmGRNSummaryReport', 'GRN Summary Report', 'Receiving Reports', 9, 'R', 85, 6, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGRNSummaryReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmGroupConsumption', 'Group Consumption Report', 'Stores', 7, 'R', 52, 31, '1', 'default.pmg', '1', 1, '1', '1', 'NO', 'NO', 'frmGroupConsumption.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmGroupMaster', 'Group Master', 'Master', 8, 'M', 7, 5, '1', 'Group_Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPOSItemMasterImport', 'Import POS Item', 'Tools', 8, 'L', 87, 4, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPOSItemMasterImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmInterfaceMaster', 'Interface Master', 'Master', 7, 'M', 7, 7, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmInterfaceMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmInvoicingPrinting', 'Invoicing Printing', 'Reports', 6, 'R', 20, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmInvoicingPrinting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmInwardOutwardRegister', 'Inward Outward Register', 'Sub Contracting Report', 3, 'R', 77, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmInwardOutwardRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmIssueListingIndigeous', 'Issue Listing Indigeous', 'sub contracting Report', 3, 'R', 78, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmIssueListingIndigeous.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmItemVariancePriceFlash', 'Item Variance Price Flash', 'Tools', 8, 'R', 55, 34, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmItemVariancePriceFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJOAllocation', 'Job Order Allocation', 'Sales', 2, 'T', 65, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmJOAllocation.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobOrder', 'Job Order', 'Sales', 2, 'T', 59, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmJobOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobOrderAllocationSlip', 'Job Order Allocation Slip', 'Sub contracting Report', 3, 'R', 66, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderAllocationSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobOrderList', 'Job Order List', 'Sub contracting Report', 3, 'R', 64, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobOrderSlip', 'Job Order Slip', 'Sub contracting Report', 3, 'R', 63, 5, '1', 'defailt.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobWorkMonitoringReport', 'Job Work Monitoring Report', 'Sub Contracting Report', 3, 'R', 79, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobWorkMonitoringReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJobWorkRegister', 'Job Work Register', 'Sub Contracting Report', 3, 'R', 80, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobWorkRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJV', 'JV Entry', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmJV.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmJVreport', 'JV Report', 'Reports', 6, 'R', 70, 2, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmJVReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLetterMaster', 'Letter Master', 'Master', 10, 'M', 10, 10, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmLetterMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLetterProcessing', 'Letter Processing', 'Processing', 3, 'P', 2, 2, '1', 'default.png', '5', 1, '1', '1', 'YES', 'NO', 'frmLetterProcessing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLocationMaster', 'Location Master', 'Master', 7, 'M', 8, 6, '1', 'Location-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmLocationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLocationWiseProductSlip', 'LocationWise Product Slip', 'Stores', 9, 'R', 41, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmLocationWiseProductSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLockerMaster', 'Locker Master', 'Master', 1, 'M', 1, 13, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmLockerMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMaterialIssueRegisterReport', 'Material Issue Register Report', 'Stores', 9, 'R', 81, 35, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialIssueRegisterReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMaterialIssueSlip', 'Material Issue Slip', 'Stores', 9, 'R', 34, 2, '1', 'Material-Issue-Slip.png', '1', 1, '1', '1', 'NO', 'NA', 'frmMaterialIssueSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMaterialReq', 'Material Requisition', 'Cost Center', 1, 'T', 9, 1, '1', 'Requisition-Form.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMaterialReq.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmMaterialReqSlip', 'Material Requisition Slip', 'Cost Center\\r\\n', 9, 'R', 37, 5, '1', 'Requisition-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialReqSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMaterialReturn', 'Material Return', 'Cost Center', 1, 'T', 10, 2, '1', 'default.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMaterialReturn.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmMaterialReturnDetail', 'Material Return Slip', 'Cost Center\\r\\n', 7, 'R', 54, 33, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialReturnDetail.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMemberPhoto', 'Member Photo', 'Master', 1, 'M', 1, 14, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberPhoto.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMIS', 'Material Issue Slip', 'Store', 2, 'T', 11, 1, '1', 'Material-Issue-Slip.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMIS.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmMISSummaryReport', 'MIS Summary Report', 'Stores', 9, 'R', 84, 36, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMISSummaryReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMonthEnd', 'Month End', 'Tools', 8, 'L', 2, 2, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMonthEnd.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmNarrationMaster', 'Narration Master', 'Master', 1, 'M', 6, 6, '6', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmNarrationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmNonMovingItemsReport', 'Non Moving Items Report', 'Stores', 7, 'R', 48, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmNonMovingItemsReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmOpeningStock', 'Opening Stock', 'Store', 2, 'T', 12, 2, '1', 'Opening-Stocks.png', '1', 1, '1', '1', 'NO', 'YES', 'frmOpeningStock.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmOpeningStockSlip', 'Opening Stock Slip', 'Stores', 7, 'R', 50, 28, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmOpeningStockSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmParameterSetup', 'Parameter Setup', 'Setup', 3, 'S', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmParameterSetup.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPayment', 'Payment', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmPayment.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPaymentReport', 'Payment Report', 'Reports', 6, 'R', 72, 4, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmPaymentReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPendingDocFlash', 'Pending Document Flash', 'Tools', 8, 'L', 47, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPendingDocFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPendingNonStkMIS', 'Pending NonStkable MIS', 'Tools', 8, 'L', 48, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPendingNonStkMIS.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPhysicalStkPosting', 'Physical Stk Posting', 'Store', 2, 'T', 13, 3, '1', 'Physical-Stock-Posting.png', '1', 1, '1', '1', 'NO', 'YES', 'frmPhysicalStkPosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmPhysicalStockPostingSlip', 'Physical Stock Posting Slip', 'Stores', 9, 'R', 10, 2, '1', 'Physical-Stock-Posting.png', '1', 1, '1', '1', 'NA', 'NA', 'frmPhysicalStockPostingSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPOSLinkUp', 'POS Link Up', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmPOSLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPOSSalesDtl', 'POS Sales', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmPOSSalesDtl.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProcessMaster', 'Process Master', 'Master', 8, 'M', 69, 17, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProcessMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProduction', 'Material Production', 'Production', 6, 'T', 14, 3, '1', 'Productions.png', '1', 1, '1', '1', 'NO', 'YES', 'frmProduction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductionOrder', 'Meal Planing', 'Production', 6, 'T', 15, 1, '1', 'Production-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmProductionOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductionOrderSlip', 'Meal Planing Slip', 'Production\\r\\n', 9, 'R', 38, 9, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductionOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductList', 'Product List', 'Listing', 9, 'R', 43, 21, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductMaster', 'Product Master', 'Master', 8, 'M', 16, 7, '1', 'Product-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmProductMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductwiseSupplierwise', 'Productwise Supplierwise', 'Receiving Reports', 9, 'R', 44, 22, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductwiseSupplierwise.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProdWiseSuppRateHis', 'Product Wise Supp Rate Histroy Report', 'Receiving Reports', 9, 'R', 40, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProdWiseSuppRateHis.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPropertyMaster', 'Property Master', 'Master', 7, 'M', 17, 8, '1', 'Property-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmPropertyMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPurchaseIndent', 'Purchase Indent', 'Store', 2, 'T', 19, 4, '1', 'Purchase-Indent.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseIndent.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmPurchaseIndentSlip', 'Purchase Indent Slip', 'Purchases', 9, 'R', 35, 3, '1', 'Purchase-Indent-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPurchaseIndentSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPurchaseOrder', 'Purchase Order', 'Purchase', 3, 'T', 20, 1, '1', 'Purchase-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmPurchaseOrderSlip', 'Purchase Order Slip', 'Purchases', 9, 'R', 36, 4, '1', 'Purchase-Order-Slip.png', '1', 1, '1', '1', 'NO', 'No', 'frmPurchaseOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmPurchaseReturn', 'Purchase Return', 'Purchase', 3, 'T', 21, 2, '1', 'Purchase-Return.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseReturn.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmPurchaseReturnSlip', 'Purchase Return Slip', 'Purchases', 7, 'R', 53, 32, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPurchaseReturnSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmRateContract', 'Rate Contract', 'Purchase', 3, 'T', 22, 3, '1', 'Rate-Contract.png', '1', 1, '1', '1', 'NO', 'YES', 'frmRateContract.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReasonMaster', 'Reason Master', 'Master', 8, 'M', 22, 10, '1', 'Reason-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmReasonMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReceipt', 'Receipt', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmReceipt.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReceiptissueConsolidated', 'Receipt Issue Consolidated', 'Stores', 9, 'R', 52, 32, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReceiptIssueConsolidated.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReceiptRegister', 'Receipt Register Report', 'Receiving Reports', 9, 'R', 46, 24, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReceiptRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmRecipes', 'Recipes List', 'Listing', 9, 'R', 39, 10, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRecipesList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReciptReport', 'Recipt Report', 'Reports', 6, 'R', 71, 3, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmReciptReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReorderLevelReport', 'Reorder Level Report', 'Stores', 7, 'R', 47, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReorderLevelReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmReorderLevelwise', 'Locationwise Productwise Reorder', 'Stores', 9, 'R', 45, 23, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReorderLevelwise.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSalesOrder', 'Sales Order', 'Sales', 2, 'T', 51, 1, '1', 'default.png', '6', 1, '1', '1', 'YES', 'YES', 'frmSalesOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSalesOrderBOM', 'Sales Order BOM', 'Sales', 2, 'T', 55, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSalesOrderBOM.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSalesOrderList', 'Sales Order List', 'Reports', 3, 'R', 61, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSalesOrderList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSalesOrderSlip', 'Sales Order Slip', 'Reports', 3, 'R', 60, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSalesOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSalesOrderStatus', 'Sales Order Status', 'Sales', 2, 'L', 68, 9, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSalesOrderStatus.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSanctionAutherityMaster', 'Sanction Autherity Master', 'Master', 8, 'M', 8, 8, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmSanctionAutherityMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmScrapGenerated', 'Scrap Generated', 'Sub Contracting Report', 3, 'R', 82, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmScrapGenerated.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSecurityShell', 'Security Shell', 'Master', 7, 'M', 23, 11, '1', 'Security-Shell.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSecurityShell.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSetup', 'Property Setup', 'Tools', 8, 'L', 24, 12, '1', 'Setup.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSetup.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSlowMovingItemsReport', 'Slow Moving Items Report', 'Stores', 7, 'R', 49, 27, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmSlowMovingItemsReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStkVarianceFlash', 'Stock Variance Flash', 'Tools', 8, 'L', 50, 28, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStkVarianceFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockAdjustment', 'Stock Adjustment', 'Store', 2, 'T', 25, 5, '1', 'Stock-Adjustment.png', '1', 1, '1', '1', 'NO', 'YES', 'frmStockAdjustment.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmStockAdjustmentFlash', 'Stock Adjustment Flash', 'Tools', 8, 'L', 25, 13, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockAdjustmentFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockAdjustmentSlip', 'Stock Adjustment Slip', 'Stores', 9, 'R', 33, 1, '1', 'Stock-Adjustment-Slip.png', '1', 1, '1', '1', 'NO', 'NA', 'frmStockAdjustmentSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockFlash', 'Stock Flash', 'Tools', 8, 'L', 10, 10, '1', 'Stocks-Flash.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockLedger', 'Stock Ledger', 'Tools', 8, 'L', 10, 12, '1', 'Stock-Ledger.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockLedger.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockLedgerReportCRM', 'Stock Ledger', 'SuB Contracting Report', 3, 'R', 83, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmStockLedgerReportCRM.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockTransfer', 'Stock Transfer', 'Cost Center', 1, 'T', 26, 3, '1', 'Stock-Transfer.png', '1', 1, '1', '1', 'NO', 'YES', 'frmStockTransfer.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
//				+" ('frmStockTransferSlip', 'Stock Transfer Slip', 'Stores', 9, 'R', 37, 6, '1', 'Stock-Transfer-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockTransferSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStructureUpdate', 'Structure Update', 'Tools', 8, 'L', 10, 2, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStructureUpdate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSubCategoryMaster', 'Sub Category Master', 'Master', 1, 'M', 1, 11, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmSubCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSubContractorGRN', 'Sub Contractor GRN', 'Sales', 2, 'T', 66, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSubContractorGRN.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSubContractorGRNSlip', 'Sub-Contractor GRN Slip', 'Sub contracting Report', 3, 'R', 65, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSubContractorGRNSlip.html', '(NULL)', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSubContractorMaster', 'Sub Contractor Master', 'Master', 1, 'M', 58, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSubContractorMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSubGroupMaster', 'Sub Group Master', 'Master', 8, 'M', 27, 13, '1', 'Sub-Group-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSubGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSundryDebtorMaster', 'Sundry Debtor Master', 'Master', 9, 'M', 9, 9, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmSundryDebtorMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmSupplierMaster', 'Supplier Master', 'Master', 8, 'M', 28, 14, '1', 'Supplier-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSupplierMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmTaxMaster', 'Tax Master', 'Master', 8, 'M', 29, 15, '1', 'Tax-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmTaxMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmTCMaster', 'TC Master', 'Master', 8, 'M', 34, 18, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NA', 'frmTCMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmUDReportCategoryMaster', 'UD Report Category Master', 'Master', 8, 'M', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUDReportCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmUOMMaster', 'UOM Master', 'Master', 8, 'M', 35, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmUOMMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmUserDefinedReport', 'User Defined Report', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUserDefinedReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmUserDefinedReportView', 'User Defined Report View', 'Reports', 7, 'R', 25, 25, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUserDefinedReportView.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmUserMaster', 'User Master', 'Master', 7, 'M', 31, 17, '1', 'User-Management.png', '1', 1, '1', '1', 'NO', 'YES', 'frmUserMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebBooksAccountMaster', 'Account Master', 'Master', 1, 'M', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmWebBooksAccountMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebBooksDeleteTransaction', 'Delete Transaction', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmWebBooksDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubDependentMaster', 'Dependent Master', 'Master', 1, 'M', 1, 2, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmDependentMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubGeneralMaster', 'General Master', 'Master', 1, 'M', 1, 9, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmGeneralMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubGroupMaster', 'Group Master', 'Master', 1, 'M', 1, 7, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmWebClubGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubMemberCategoryMaster', 'Member Category Master', 'Master', 1, 'M', 1, 3, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubMemberHistory', 'Member History', 'Master', 1, 'M', 1, 4, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberHistory.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubMemberPreProfile', 'Member Pre-Profile', 'Master', 1, 'M', 1, 5, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberPreProfile.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubMemberProfile', 'Member Profile', 'Master', 1, 'M', 1, 1, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberProfile.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubMembershipFormGenration', 'Membership Form Genration', 'Master', 1, 'M', 1, 6, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMembershipFormGenration.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWebClubPersonMaster', 'Person Master', 'Master', 1, 'M', 1, 8, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmWebClubPersonMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWhatIfAnalysis', 'What If Analysis Tool', 'Tools', 8, 'L', 10, 2, '1', 'What-If-Analysis-Tool.png', '1', 1, '1', '1', 'NO', 'NO', 'frmWhatIfAnalysis.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmWorkOrder', 'Work Order', 'Production', 6, 'T', 32, 2, '1', 'Work-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmWorkOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Listing', 'Listing', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Master', 'Master', 'Tools', 8, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'No', 'No', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Production\\r\\n', 'Production\\r\\n', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Purchases', 'Purchases', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Receiving Reports', 'Receiving Reports', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Stores', 'Stores', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('Sub Contracting Report', 'Sub Contracting Report', 'Reports', 3, 'O', 1, 1, '1', 'default.png', '6', 1, '1', '1', 'No', 'No', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "
//				+" ('frmDatabaseDataImport', 'Data Import', 'Tools', 8, 'L', 86, 1, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDatabaseDataImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
//				+" ('frmRecipeCostiong', 'Recipes Costing', 'Listing', 9, 'R', 87, 11, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRecipeCosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmLossCalculationReport', 'Loss Calculation Report', 'Listing', 9, 'R', 88, 13, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmLossCalculationReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmMasterList', 'Master List', 'Listing', 9, 'R', 44, 22, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMasterList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmTallyLinkUp', 'Tally Link Up', 'Tools', 8, 'L', 189, 23, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmTallyLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmDocumentListingFlashReport', 'Document Listing Flash Report', 'Tools', 8, 'L', 190, 24, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDocumentListingFlashReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmProductPurchaseReciept', 'Product Purchase Reciept', 'Receiving Reports', 9, 'R', 191, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductPurchaseReciept.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				

				+" ('Cost Center\\r\\n', 'Cost Center\\r\\n', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAccountHolderMaster', 'Account Holder Master', 'Master', 2, 'M', 2, 2, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmAccountHolderMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmACGroupMaster', 'Group Master', 'Master', 4, 'M', 4, 4, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmACGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAgeingReport', 'Ageing Rport', 'Sub Contracting Report', 3, 'R', 73, 1, '1', 'default.png', '6', 1, '1', '1', 'No', 'No', 'frmAgeingReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAttributeMaster', 'Attribute Master', 'Master', 8, 'M', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmAttributeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAttributeValueMaster', 'Attribute Value Master', 'Master', 8, 'M', 2, 2, '3', 'Attribute-Value-Master.png', '3', 3, '3', '3', 'NO', 'YES', 'frmAttributeValueMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAuditFlash', 'Audit Flash', 'Tools', 8, 'L', 48, 26, '1', 'audit flash.png', '1', 1, '1', '1', 'NO', 'NO', 'frmAuditFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAuthorisationTool', 'Authorisation', 'Tools', 8, 'L', 10, 2, '1', 'Authourisation.png', '1', 1, '1', '1', 'NO', 'NO', 'frmAuthorisationTool.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmBankMaster', 'Bank Master', 'Master', 3, 'M', 3, 3, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmBankMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmBillPassing', 'Bill Passing', 'Accounts', 5, 'T', 3, 1, '1', 'Bill-Passing.png', '1', 1, '1', '1', 'NO', 'YES', 'frmBillPassing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),";
				if(strIndustryType.equalsIgnoreCase("Manufacture"))
				{
					sql+=" ('frmBOMMaster', 'BOM Master', 'Master', 8, 'M', 4, 3, '2', 'ReciepeMaster-BOM-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmBOMMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " ;
				}
					
				else{
					sql+=" ('frmBOMMaster', 'Recipe Master', 'Master', 8, 'M', 4, 3, '2', 'ReciepeMaster-BOM-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmBOMMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " ;
				}
				
				sql+=" ('frmCharacteristicsMaster', 'Characteristics Master', 'Master', 8, 'M', 5, 4, '1', 'Characteristics-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmCharMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmChargeMaster', 'Charge Master', 'Master', 5, 'M', 5, 5, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmChargeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmChargeProcessing', 'Charge Processing', 'Processing', 2, 'P', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'YES', 'NO', 'frmChargeProcessing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmCompanyMaster', 'Company Master', 'Master', 1, 'M', 1, 10, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmCompanyMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmCompanyTypeMaster', 'Company Type Master', 'Master', 1, 'M', 1, 12, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmCompanyTypeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmConsReceiptValMiscSuppReqReport', 'Consolidated Receipt Value All Supplier Required Report', 'Purchases', 7, 'R', 51, 29, '1', 'Consolidated Receipt Value Misc Suppler Required Report.png', '1', 1, '1', '1', 'NO', 'NO', 'frmConsReceiptValMiscSuppReqReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmConsReceiptValStoreWiseBreskUPReport', 'Consolidated Receipt value Store Wise BreakUP', 'Purchases', 7, 'R', 51, 30, '1', 'Consolidated Receipt value Store Wise BreakUP.png', '1', 1, '1', '1', 'NO', 'No', 'frmConsReceiptValStoreWiseBreskUPReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmCostOfIssue', 'Cost Of Issue', 'Stores', 9, 'R', 42, 20, '1', 'cost of issue.png', '1', 1, '1', '1', 'NO', 'NO', 'frmCostOfIssue.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmCustomerMaster', 'Customer Master', 'Master', 1, 'M', 52, 1, '1', 'default.png', '6', 1, '1', '1', 'NO', 'No', 'frmCustomerMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDebtorLedger', 'Debtor Ledger Tool', 'Tools', 1, 'L', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmDebtorLedger.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDebtorReceipt', 'Debtor Receipt', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmDebtorReceipt.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeleteTransaction', 'Delete Transaction', 'Tools', 8, 'L', 46, 24, '1', 'delete trasaction.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryChallan', 'Delivery Challan', 'Sales', 2, 'T', 56, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmDeliveryChallan.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryChallanList', 'Delivery Challan List', 'Sub contracting Report', 3, 'R', 63, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryChallanSlip', 'Delivery Challan Slip', 'Sub contracting Report', 3, 'R', 62, 4, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryChallanSlipInvoice', 'Delivery Challan Slip Invoice', 'Sub contracting Report', 3, 'R', 74, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryChallanSlipInvoice.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryNote', 'Delivery Note', 'Sales', 2, 'T', 67, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmDeliveryNote.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryNoteList', 'Delivery Note List', 'Sub contracting Report', 3, 'R', 68, 10, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryNoteList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDeliveryNoteSlip', 'Delivery Note Slip', 'Sub contracting Report', 3, 'R', 67, 9, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDeliveryNoteSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDocumentReconciliation', 'Document Reconciliation', 'Tools', 8, 'L', 10, 2, '1', 'Document_Reconciliation_Fla.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDocumentReconciliation.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDueDateMonitoringReport', 'Due Date Monitoring Report', 'Sub Contracting Report', 3, 'R', 75, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmDueDateMonitoringReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmEditOtherInfo', 'Edit Other Info', 'Master', 1, 'M', 1, 15, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmEditOtherInfo.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmEmailSending', 'Sending Email', 'Tools', 8, 'L', 51, 29, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmEmailSending.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseAbstractReport', 'Abstract Report', 'Reports', 3, 'R', 21, 3, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseAbstractReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseAuditFlash', 'Excise Audit Flash', 'Tools', 4, 'L', 14, 3, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseAuditFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseBillGenerate', 'Bill Generate', 'Transaction', 2, 'L', 19, 3, '1', 'default.png', '2', 1, '1', '1', 'YES', 'NO', 'frmExciseBillGenerate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseBrandMaster', 'Brand Master', 'Master', 1, 'M', 3, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseBrandMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseCashMemoReport', 'Cash Memo Report', 'Reports', 3, 'R', 184, 8, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseCashMemoReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
				+" ('frmExciseBrandWiseInquiry', 'Brand Wise Inquiry', 'Reports', 3, 'R', 186, 10, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseBrandWiseInquiry.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseCategoryMaster', 'Category Master', 'Master', 1, 'M', 13, 5, '1', 'default.png', '0', 1, '1', '1', 'NO', 'NO', 'frmExciseCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseChallan', 'Excise Challan', 'Sales', 2, 'T', 57, 4, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmExciseChallan.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseChataiReport', 'Chatai Report', 'Reports', 3, 'R', 23, 5, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseChataiReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseCityMaster', 'City Master', 'Master', 1, 'M', 18, 9, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseCityMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseDeleteTransaction', 'Delete Transction', 'Transaction', 2, 'R', 20, 5, '1', 'default.png', '2', 1, '1', '1', 'YES', 'NO', 'frmExciseDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseFLR3A', 'FLR- III (3A) Report', 'Reports', 3, 'R', 2, 1, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR3A.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseFLR4Report', 'FLR- 4 Report', 'Reports', 3, 'R', 22, 4, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR4Report.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseFLR6', 'FLR-VI (6A) Report', 'Reports', 3, 'R', 5, 2, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseFLR6.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseLicenceMaster', 'Licence Master', 'Master', 1, 'M', 15, 7, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseLicenceMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseLocationMaster', 'Location Master', 'Master', 1, 'M', 17, 8, '1', 'Location-Master.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseLocationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseManualSale', 'Excise Sale', 'Transaction', 2, 'T', 6, 2, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseManualSale.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseOneDayPass', 'One Day Pass', 'Transaction', 2, 'T', 24, 4, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseOneDayPass.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseOpeningStock', 'Opening Stock', 'Transaction', 2, 'T', 8, 5, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseOpeningStock.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePermitMaster', 'Permit Master', 'Master', 1, 'M', 24, 9, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePermitMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePhysicalStkPosting', 'Excise Physical Stk Posting', 'Store', 5, 'L', 4, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePhysicalStkPosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePOSDataExportImport', 'Excise POS DataExport Import', 'Store', 5, 'L', 7, 2, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSDataExportImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePOSLinkUp', 'POS Excise Link Up', 'Tools', 4, 'L', 10, 1, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePOSSale', 'Excise POS Sale', 'Tools', 4, 'L', 13, 2, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePOSSale.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePropertySetUp', 'Property Set Up', 'Master', 1, 'M', 14, 6, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePropertySetUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePurchaseAnylasisReport', 'Purchase Anylasis Report', 'Reports', 3, 'R', 182, 6, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePurchaseAnylasisReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExcisePurchaseReport', 'Purchase Report', 'Reports', 3, 'R', 183, 7, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePurchaseReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseRecipeMaster', 'Excise Recipe Master', 'Master', 1, 'M', 16, 5, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseRecipeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseSalesReport', 'Sales Report', 'Reports', 3, 'R', 185, 9, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSalesReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseSizeMaster', 'Size Master', 'Master', 1, 'M', 11, 3, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSizeMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseStructureUpdate', 'Excise Structure Update', 'Tools', 4, 'L', 15, 4, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseStructureUpdate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseSubCategoryMaster', 'Sub Category Master', 'Master', 1, 'M', 16, 4, '1', 'default.png', '0', 1, '1', '1', 'NO', 'NO', 'frmExciseSubCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseSupplierMaster', 'Supplier Master', 'Master', 1, 'M', 9, 4, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSupplierMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseTransportPass', 'Transport Pass', 'Transaction', 2, 'T', 1, 1, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseTransportPass.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExpiryFlash', 'Expiry Flash', 'Tools', 8, 'L', 49, 27, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmExpiryFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmFoodCost', 'Cost Analysis', 'Stores', 9, 'R', 53, 33, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmFoodCost.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmGRN', 'GRN(Good Receiving Note)', 'Receiving', 4, 'T', 6, 1, '1', 'GRN.png', '1', 1, '1', '1', 'YES', 'YES', 'frmGRN.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmGRNRegisterReport', 'GRN Register Report', 'Receiving Reports', 9, 'R', 73, 5, '1', 'GRN Register Report.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGRNRegisterReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmGrnSlip', 'Grn Slip', 'Receiving Reports', 9, 'R', 35, 4, '1', 'GRN-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGrnSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmGRNSummaryReport', 'GRN Summary Report', 'Receiving Reports', 9, 'R', 85, 6, '1', 'GRN Summary Report.png', '1', 1, '1', '1', 'NO', 'NO', 'frmGRNSummaryReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmGroupConsumption', 'Group Consumption Report', 'Stores', 7, 'R', 52, 31, '1', 'default.pmg', '1', 1, '1', '1', 'NO', 'NO', 'frmGroupConsumption.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmGroupMaster', 'Group Master', 'Master', 8, 'M', 7, 5, '1', 'Group_Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPOSItemMasterImport', 'Import POS Item', 'Tools', 8, 'L', 87, 4, '1', 'import pos item.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPOSItemMasterImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmInterfaceMaster', 'Interface Master', 'Master', 7, 'M', 7, 7, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmInterfaceMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmInvoicingPrinting', 'Invoicing Printing', 'Reports', 6, 'R', 20, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmInvoicingPrinting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmInwardOutwardRegister', 'Inward Outward Register', 'Sub Contracting Report', 3, 'R', 77, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmInwardOutwardRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmIssueListingIndigeous', 'Issue Listing Indigeous', 'sub contracting Report', 3, 'R', 78, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmIssueListingIndigeous.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmItemVariancePriceFlash', 'Item Variance Price Flash', 'Tools', 8, 'R', 55, 34, '1', 'Item Variance Price Flash.png', '1', 1, '1', '1', 'NO', 'NO', 'frmItemVariancePriceFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJOAllocation', 'Job Order Allocation', 'Sales', 2, 'T', 65, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmJOAllocation.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobOrder', 'Job Order', 'Sales', 2, 'T', 59, 5, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmJobOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobOrderAllocationSlip', 'Job Order Allocation Slip', 'Sub contracting Report', 3, 'R', 66, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderAllocationSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobOrderList', 'Job Order List', 'Sub contracting Report', 3, 'R', 64, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobOrderSlip', 'Job Order Slip', 'Sub contracting Report', 3, 'R', 63, 5, '1', 'defailt.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobWorkMonitoringReport', 'Job Work Monitoring Report', 'Sub Contracting Report', 3, 'R', 79, 6, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobWorkMonitoringReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJobWorkRegister', 'Job Work Register', 'Sub Contracting Report', 3, 'R', 80, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmJobWorkRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJV', 'JV Entry', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmJV.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmJVreport', 'JV Report', 'Reports', 6, 'R', 70, 2, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmJVReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLetterMaster', 'Letter Master', 'Master', 10, 'M', 10, 10, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmLetterMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLetterProcessing', 'Letter Processing', 'Processing', 3, 'P', 2, 2, '1', 'default.png', '5', 1, '1', '1', 'YES', 'NO', 'frmLetterProcessing.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLocationMaster', 'Location Master', 'Master', 7, 'M', 8, 6, '1', 'Location-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmLocationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLocationWiseProductSlip', 'LocationWise Product Slip', 'Stores', 9, 'R', 41, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmLocationWiseProductSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLockerMaster', 'Locker Master', 'Master', 1, 'M', 1, 13, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmLockerMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMaterialIssueRegisterReport', 'Material Issue Register Report', 'Stores', 9, 'R', 81, 35, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialIssueRegisterReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMaterialIssueSlip', 'Material Issue Slip', 'Stores', 9, 'R', 34, 2, '1', 'Material-Issue-Slip.png', '1', 1, '1', '1', 'NO', 'NA', 'frmMaterialIssueSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMaterialReq', 'Material Requisition', 'Cost Center', 1, 'T', 9, 1, '1', 'Requisition-Form.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMaterialReq.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmMaterialReqSlip', 'Material Requisition Slip', 'Cost Center\\r\\n', 9, 'R', 37, 5, '1', 'Requisition-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialReqSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMaterialReturn', 'Material Return', 'Cost Center', 1, 'T', 10, 2, '1', 'default.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMaterialReturn.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmMaterialReturnDetail', 'Material Return Slip', 'Cost Center\\r\\n', 7, 'R', 54, 33, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMaterialReturnDetail.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMemberPhoto', 'Member Photo', 'Master', 1, 'M', 1, 14, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberPhoto.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMIS', 'Material Issue Slip', 'Store', 2, 'T', 11, 1, '1', 'Material-Issue-Slip.png', '1', 1, '1', '1', 'YES', 'YES', 'frmMIS.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmMISSummaryReport', 'MIS Summary Report', 'Stores', 9, 'R', 84, 36, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMISSummaryReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMonthEnd', 'Month End', 'Tools', 8, 'L', 2, 2, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMonthEnd.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmNarrationMaster', 'Narration Master', 'Master', 1, 'M', 6, 6, '6', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmNarrationMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmNonMovingItemsReport', 'Non Moving Items Report', 'Stores', 7, 'R', 48, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmNonMovingItemsReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmOpeningStock', 'Opening Stock', 'Store', 2, 'T', 12, 2, '1', 'Opening-Stocks.png', '1', 1, '1', '1', 'NO', 'YES', 'frmOpeningStock.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmOpeningStockSlip', 'Opening Stock Slip', 'Stores', 7, 'R', 50, 28, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmOpeningStockSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmParameterSetup', 'Parameter Setup', 'Setup', 3, 'S', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmParameterSetup.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPayment', 'Payment', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmPayment.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPaymentReport', 'Payment Report', 'Reports', 6, 'R', 72, 4, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmPaymentReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPendingDocFlash', 'Pending Document Flash', 'Tools', 8, 'L', 47, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPendingDocFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPendingNonStkMIS', 'Pending NonStkable MIS', 'Tools', 8, 'L', 48, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPendingNonStkMIS.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPhysicalStkPosting', 'Physical Stk Posting', 'Store', 2, 'T', 13, 3, '1', 'Physical-Stock-Posting.png', '1', 1, '1', '1', 'NO', 'YES', 'frmPhysicalStkPosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmPhysicalStockPostingSlip', 'Physical Stock Posting Slip', 'Stores', 9, 'R', 10, 2, '1', 'Physical-Stock-Posting.png', '1', 1, '1', '1', 'NA', 'NA', 'frmPhysicalStockPostingSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPOSLinkUp', 'POS Link Up', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmPOSLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPOSSalesDtl', 'POS Sales', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmPOSSalesDtl.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProcessMaster', 'Process Master', 'Master', 8, 'M', 69, 17, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProcessMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProduction', 'Material Production', 'Production', 6, 'T', 14, 3, '1', 'Productions.png', '1', 1, '1', '1', 'NO', 'YES', 'frmProduction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), ";
				if(strIndustryType.equalsIgnoreCase("Manufacture"))
				{
					sql+=" ('frmProductionOrder', 'Production Order', 'Production', 6, 'T', 15, 1, '1', 'Production-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmProductionOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), ";
				}
					
				else{
					sql+=" ('frmProductionOrder', 'Meal Planing ', 'Production', 6, 'T', 15, 1, '1', 'Production-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmProductionOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), ";
				}
					
			
				sql+=" ('frmProductionOrderSlip', 'Meal Planing Slip', 'Production\\r\\n', 9, 'R', 38, 9, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductionOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProductList', 'Product List', 'Listing', 9, 'R', 43, 21, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProductMaster', 'Product Master', 'Master', 8, 'M', 16, 7, '1', 'Product-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmProductMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProductwiseSupplierwise', 'Productwise Supplierwise', 'Receiving Reports', 9, 'R', 44, 22, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductwiseSupplierwise.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProdWiseSuppRateHis', 'Product Wise Supp Rate Histroy Report', 'Receiving Reports', 9, 'R', 40, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProdWiseSuppRateHis.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPropertyMaster', 'Property Master', 'Master', 7, 'M', 17, 8, '1', 'Property-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmPropertyMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPurchaseIndent', 'Purchase Indent', 'Store', 2, 'T', 19, 4, '1', 'Purchase-Indent.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseIndent.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmPurchaseIndentSlip', 'Purchase Indent Slip', 'Purchases', 9, 'R', 35, 3, '1', 'Purchase-Indent-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPurchaseIndentSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPurchaseOrder', 'Purchase Order', 'Purchase', 3, 'T', 20, 1, '1', 'Purchase-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmPurchaseOrderSlip', 'Purchase Order Slip', 'Purchases', 9, 'R', 36, 4, '1', 'Purchase-Order-Slip.png', '1', 1, '1', '1', 'NO', 'No', 'frmPurchaseOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPurchaseReturn', 'Purchase Return', 'Purchase', 3, 'T', 21, 2, '1', 'Purchase-Return.png', '1', 1, '1', '1', 'YES', 'YES', 'frmPurchaseReturn.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmPurchaseReturnSlip', 'Purchase Return Slip', 'Purchases', 7, 'R', 53, 32, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPurchaseReturnSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmRateContract', 'Rate Contract', 'Purchase', 3, 'T', 22, 3, '1', 'Rate-Contract.png', '1', 1, '1', '1', 'NO', 'YES', 'frmRateContract.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReasonMaster', 'Reason Master', 'Master', 8, 'M', 22, 10, '1', 'Reason-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmReasonMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReceipt', 'Receipt', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmReceipt.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReceiptissueConsolidated', 'Receipt Issue Consolidated', 'Stores', 9, 'R', 52, 32, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReceiptIssueConsolidated.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReceiptRegister', 'Receipt Register Report', 'Receiving Reports', 9, 'R', 46, 24, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReceiptRegister.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmRecipes', 'Recipes List', 'Listing', 9, 'R', 39, 10, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRecipesList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReciptReport', 'Recipt Report', 'Reports', 6, 'R', 71, 3, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmReciptReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReorderLevelReport', 'Reorder Level Report', 'Stores', 7, 'R', 47, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReorderLevelReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmReorderLevelwise', 'Locationwise Productwise Reorder', 'Stores', 9, 'R', 45, 23, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmReorderLevelwise.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSalesOrder', 'Sales Order', 'Sales', 2, 'T', 51, 1, '1', 'default.png', '6', 1, '1', '1', 'YES', 'YES', 'frmSalesOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSalesOrderBOM', 'Sales Order BOM', 'Sales', 2, 'T', 55, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSalesOrderBOM.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSalesOrderList', 'Sales Order List', 'Reports', 3, 'R', 61, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSalesOrderList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSalesOrderSlip', 'Sales Order Slip', 'Reports', 3, 'R', 60, 3, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSalesOrderSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSalesOrderStatus', 'Sales Order Status', 'Sales', 2, 'T', 68, 9, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSalesOrderStatus.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSanctionAutherityMaster', 'Sanction Autherity Master', 'Master', 8, 'M', 8, 8, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmSanctionAutherityMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmScrapGenerated', 'Scrap Generated', 'Sub Contracting Report', 3, 'R', 82, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmScrapGenerated.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSecurityShell', 'Security Shell', 'Master', 7, 'M', 23, 11, '1', 'Security-Shell.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSecurityShell.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSetup', 'Property Setup', 'Tools', 8, 'L', 24, 12, '1', 'Setup.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSetup.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSlowMovingItemsReport', 'Slow Moving Items Report', 'Stores', 7, 'R', 49, 27, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmSlowMovingItemsReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStkVarianceFlash', 'Stock Variance Flash', 'Tools', 8, 'L', 50, 28, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStkVarianceFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockAdjustment', 'Stock Adjustment', 'Store', 2, 'T', 25, 5, '1', 'Stock-Adjustment.png', '1', 1, '1', '1', 'NO', 'YES', 'frmStockAdjustment.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmStockAdjustmentFlash', 'Stock Adjustment Flash', 'Tools', 8, 'L', 25, 13, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockAdjustmentFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockAdjustmentSlip', 'Stock Adjustment Slip', 'Stores', 9, 'R', 33, 1, '1', 'Stock-Adjustment-Slip.png', '1', 1, '1', '1', 'NO', 'NA', 'frmStockAdjustmentSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockFlash', 'Stock Flash', 'Tools', 8, 'L', 10, 10, '1', 'Stocks-Flash.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockLedger', 'Stock Ledger', 'Tools', 8, 'L', 10, 12, '1', 'Stock-Ledger.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockLedger.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
//				+" ('frmStockLedgerReportCRM', 'Stock Ledger', 'SuB Contracting Report', 3, 'R', 83, 8, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmStockLedgerReportCRM.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockTransfer', 'Stock Transfer', 'Cost Center', 1, 'T', 26, 3, '1', 'Stock-Transfer.png', '1', 1, '1', '1', 'NO', 'YES', 'frmStockTransfer.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Yes', NULL), "
				+" ('frmStockTransferSlip', 'Stock Transfer Slip', 'Stores', 9, 'R', 37, 6, '1', 'Stock-Transfer-Slip.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockTransferSlip.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStructureUpdate', 'Structure Update', 'Tools', 8, 'L', 10, 2, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStructureUpdate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSubCategoryMaster', 'Sub Category Master', 'Master', 1, 'M', 1, 11, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmSubCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSubContractorGRN', 'Sub Contractor GRN', 'Sales', 2, 'T', 66, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'Yes', 'frmSubContractorGRN.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSubContractorGRNSlip', 'Sub-Contractor GRN Slip', 'Sub contracting Report', 3, 'R', 65, 7, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSubContractorGRNSlip.html', '(NULL)', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSubContractorMaster', 'Sub Contractor Master', 'Master', 1, 'M', 58, 2, '1', 'default.png', '6', 1, '1', '1', 'NO', 'NO', 'frmSubContractorMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSubGroupMaster', 'Sub Group Master', 'Master', 8, 'M', 27, 13, '1', 'Sub-Group-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSubGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSundryDebtorMaster', 'Sundry Debtor Master', 'Master', 9, 'M', 9, 9, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmSundryDebtorMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSupplierMaster', 'Supplier Master', 'Master', 8, 'M', 28, 14, '1', 'Supplier-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmSupplierMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmTaxMaster', 'Tax Master', 'Master', 8, 'M', 29, 15, '1', 'Tax-Master.png', '1', 1, '1', '1', 'NO', 'YES', 'frmTaxMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmTCMaster', 'TC Master', 'Master', 8, 'M', 34, 18, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NA', 'frmTCMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmUDReportCategoryMaster', 'UD Report Category Master', 'Master', 8, 'M', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUDReportCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmUOMMaster', 'UOM Master', 'Master', 8, 'M', 35, 19, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmUOMMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmUserDefinedReport', 'User Defined Report', 'Tools', 8, 'L', 1, 1, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUserDefinedReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmUserDefinedReportView', 'User Defined Report View', 'Reports', 7, 'R', 25, 25, '12', 'Attribute-Master.png', '1', 1, '1', '1', 'NO', '1', 'frmUserDefinedReportView.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmUserMaster', 'User Master', 'Master', 7, 'M', 31, 17, '1', 'User-Management.png', '1', 1, '1', '1', 'NO', 'YES', 'frmUserMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebBooksAccountMaster', 'Account Master', 'Master', 1, 'M', 1, 1, '1', 'default.png', '5', 1, '1', '1', 'NO', 'NO', 'frmWebBooksAccountMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebBooksDeleteTransaction', 'Delete Transaction', 'Transaction', 1, 'T', 1, 1, '12', 'default.png', '5', 1, '1', '1', 'NO', '1', 'frmWebBooksDeleteTransaction.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubDependentMaster', 'Dependent Master', 'Master', 1, 'M', 1, 2, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmDependentMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubGeneralMaster', 'General Master', 'Master', 1, 'M', 1, 9, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmGeneralMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubGroupMaster', 'Group Master', 'Master', 1, 'M', 1, 7, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmWebClubGroupMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubMemberCategoryMaster', 'Member Category Master', 'Master', 1, 'M', 1, 3, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberCategoryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubMemberHistory', 'Member History', 'Master', 1, 'M', 1, 4, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberHistory.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubMemberPreProfile', 'Member Pre-Profile', 'Master', 1, 'M', 1, 5, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberPreProfile.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubMemberProfile', 'Member Profile', 'Master', 1, 'M', 1, 1, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMemberProfile.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubMembershipFormGenration', 'Membership Form Genration', 'Master', 1, 'M', 1, 6, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmMembershipFormGenration.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWebClubPersonMaster', 'Person Master', 'Master', 1, 'M', 1, 8, '1', 'default.png', '4', 1, '1', '1', 'NO', 'NO', 'frmWebClubPersonMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWhatIfAnalysis', 'What If Analysis Tool', 'Tools', 8, 'L', 10, 2, '1', 'What-If-Analysis-Tool.png', '1', 1, '1', '1', 'NO', 'NO', 'frmWhatIfAnalysis.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmWorkOrder', 'Work Order', 'Production', 6, 'T', 32, 2, '1', 'Work-Order.png', '1', 1, '1', '1', 'YES', 'YES', 'frmWorkOrder.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Listing', 'Listing', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Master', 'Master', 'Tools', 8, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'No', 'No', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Production\\r\\n', 'Production\\r\\n', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Purchases', 'Purchases', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Receiving Reports', 'Receiving Reports', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Stores', 'Stores', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Sub Contracting Report', 'Sub Contracting Report', 'Reports', 3, 'O', 1, 1, '1', 'default.png', '6', 1, '1', '1', 'No', 'No', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "
				+" ('frmDatabaseDataImport', 'Data Import', 'Tools', 8, 'L', 86, 1, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDatabaseDataImport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
				+" ('frmRecipeCostiong', 'Recipes Costing', 'Listing', 9, 'R', 87, 11, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRecipeCosting.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmLossCalculationReport', 'Loss Calculation Report', 'Listing', 9, 'R', 88, 13, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmLossCalculationReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMasterList', 'Master List', 'Listing', 9, 'R', 44, 22, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmMasterList.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseCategoryWiseSale', 'Category Wise Sale', 'Reports', 3, 'R', 187, 11, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseCategoryWiseSale.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmBrandWiseClosingReport', 'Brand WiseClosing Report', 'Reports', 3, 'R', 188, 12, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmBrandWiseClosingReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL) ,"
				+" ('frmExciseUserMaster', 'Excise User Master', 'Master', 1, 'M', 25, 10, '2', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseUserMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmExciseSecurityShell','Excise Security Shell', 'Tools', 8, 'T', 11, 11, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseSecurityShell.html',NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmTallyLinkUp', 'Tally Link Up', 'Tools', 8, 'L', 189, 23, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmTallyLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmDocumentListingFlashReport', 'Document Listing Flash Report', 'Tools', 8, 'L', 190, 24, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmDocumentListingFlashReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProductPurchaseReciept', 'Product Purchase Reciept', 'Receiving Reports', 9, 'R', 191, 25, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductPurchaseReciept.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
				+" ('frmExciseMonthEnd', 'Excise Month End', 'Tools', 8, 'T', 12, 12, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseMonthEnd.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "            
				+" ('frmExcisePropertyMaster', 'Excise Property Master', 'Master', 1, 'M', 26, 11, '1', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExcisePropertyMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 

				+" ('frmCountryMaster', 'Country Master', 'Master', 8, 'M', 18, 102, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmWSCountryMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
				+" ('frmStateMaster', 'State Master', 'Master', 8, 'M', 19, 103, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmWSStateMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmCityMaster', 'City Master', 'Master', 8, 'M', 20, 104, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmWSCityMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmVehicleMaster', 'Vehicle Master', 'Master', 8, 'M', 21, 105, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmVehicleMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmRouteMaster', 'Route Master', 'Master', 8, 'M', 22, 106, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRouteMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmVehicleRouteMaster', 'Vehicle Route Master', 'Master', 8, 'M', 23, 105, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmVehicleRouteMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmAvgConsumptionReport', 'Avg Consumption Report', 'Stores', 9, 'R', 85, 36, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmAvgConsumptionReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "	
				+" ('frmStockTransferFlash', 'Stock Transfer Flash', 'Tools', 8, 'L', 86, 86, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmStockTransferFlash.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "
				+" ('frmTransporterMaster', 'Transporter Master', 'Master', 8, 'M', 22, 106, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmTransporterMaster.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "
				+" ('frmExportTallyFile', 'Export Tally File', 'Tools', 8, 'L', 87, 87, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmExportTallyFile.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  "
				+" ('frmPurchaseRegisterReport', 'Purchase Register Report', 'Purchases', 7, 'R', 54, 33, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPurchaseRegisterReport.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('Excise Report', 'Excise Report', 'Reports', 7, 'O', 1, 1, '1', 'default.png', '1', 1, '1', '1', 'NA', 'NA', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmRG-23A-Part-I', 'From RG-23A-Part-I', 'Excise Report', 7, 'R', 1, 108, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmRG-23A-Part-I.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmARLinkUp', 'AR Link Up', 'Tools', '8', 'L', '190', '24', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmARLinkUp.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSaleVSPurchase', 'Sale vs Purchase', 'Stores', 7, 'R', 1, 1, '1', 'default.png', '1', 1, '1', '1','NA', 'NA', 'frmSaleVSPurchase.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStockAdjustmentMonthly', 'Stock Adjustment Monthly', 'Stores', '9', 'R', '33', '1', '1', 'Stock-Adjustment-Slip.png', '1', '1', '1', '1', 'NO', 'NO', 'frmStockAdjustmentMonthly.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
		        +" ('frmGrnAndInvoiceComparision', 'Comparision GRN and  Invoice', 'Stores', '8', 'R', '1', '1', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmGrnAndInvoiceComparision.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmBulkProductUpdate', 'Bulk Product Update', 'Master', '8', 'M', '23', '108', '1', 'Product-Master.png', '1', '1', '1', '1', 'NO', 'YES', 'frmBulkProductUpdate.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
		        +" ('frmFundFlow', 'Fund Flow Report', 'Tools', '8', 'L', '87', '88', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmFundFlowReport.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmTransectionFlash', 'Transection Flash', 'Tools', '8', 'L', '87', '89', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmTransectionFlash.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmManufactureMaster', 'Manufacture Master', 'Master', 1, 'M', 61, 2, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmManufactureMaster.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProductWiseGRNReport', 'Loc.Cat. Product Wise GRN Report', 'Receiving Reports', 9, 'R', 192, 26, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmProductWiseGRNReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMISLocationWiseCategoryWiseReport', 'MIS LocationWise CategroyWise Report', 'Stores', '9', 'R', '86', '37', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmMISLocationWiseCategoryWiseReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmSupplierWiseProductGRNReport', 'Supp.Cat. Product Wise GRN Report', 'Receiving Reports', 9, 'R', 193, 27, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmSupplierWiseProductGRNReport.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL) ,"
				+" ('frmTransactionListing', 'Transection Listing', 'Tools', '8', 'L', '88', '90', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmTransactionListing.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), " 
				+" ('frmCurrencyMaster', 'Currency Master', 'Master', '8', 'M', '23', '106', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmCurrencyMaster.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmMISLocationWiseReport', 'MIS LocationWise  Report', 'Stores', '9', 'R', '86', '37', '1', 'default.png', '1', '1', '1', '1', 'NO', 'NO', 'frmMISLocationWiseReport.html' , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				// for Excise structure update form show in excise module; // START shown those form which is needed///
				//+" ('frmExciseStructureUpdate', 'Excise Structure Update', 'Tools', 4, 'L', 15, 4, '12', 'default.png', '2', 1, '1', '1', 'NO', 'NO', 'frmExciseStructureUpdate.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmPendingRecipe', 'Pending Recipe List', 'Listing', 9, 'R', 45, 23, '1', 'default.png', '1', 1, '1', '1', 'NO', 'NO', 'frmPendingRecipe.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmStandardRequisition', 'Standard Requisition', 'Cost Center', '1', 'T', '27', '3', '1', 'default.png', '1', '1', '1', '1', 'NO', 'YES', 'frmStandardRequisition.html',  NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL), "
				+" ('frmProfitLossWebBook', 'Profit Loss Report', 'Reports', '6', 'R', '10', '10', '1', 'default.png', '5', '1', '1', '1', 'NO', 'NO', 'frmProfitLossWebBook.html', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL) ";
		
		
		
		
		
		
		
		
		
		
			
		
		
		
		
		
		
		
		
		
		
		
		
		
		
				////--------------------END----------------------------/////
		
		
			funExecuteQuery(sql);
				
				

		/*----------------WebStock Forms END---------------------------*/	
	
		
		
	}

	@SuppressWarnings("finally")
	private int funExecuteQuery(String sql) {
		try {
			Query query = sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			query.executeUpdate();
		} catch (Exception e) {
		} finally {
			return 0;
		}
	}
	


	@Override
	public void funClearTransaction(String clientCode,String[] str ) {
		for(int i=0; i<str.length;i++){
			String sql="";
			switch(str[i]){
			
////////////////////////WebStock Transaction Start///////////////////////////////////////////				
							
				case"Bill Passing":
				{
						sql="truncate table tblbillpassdtl";
						funExecuteQuery(sql);
				 		sql="truncate table tblbillpasshd";
				 		funExecuteQuery(sql);
				 		sql="truncate table tblbillpassingtaxdtl";
				 		funExecuteQuery(sql);
				 		break; 
				}
				
				case"GRN(Good Receiving Note)":
				{ 
					sql="truncate table tblgrndtl";
					funExecuteQuery(sql);
					sql="truncate table tblgrnhd";
					funExecuteQuery(sql);
					sql="truncate table tblgrntaxdtl";
					funExecuteQuery(sql);
					sql="truncate table tblgrnmisdtl";
					funExecuteQuery(sql);
					sql="truncate table tblbatchhd";
					funExecuteQuery(sql);
					
					break; 
				}
				
				case"Opening Stock":
				{ 	sql="truncate table tblinitialinvdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinitialinventory";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Return":
				{ 
					sql="truncate table tblmaterialreturndtl";
					funExecuteQuery(sql);
					sql="truncate table tblmaterialreturnhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Issue Slip":
				{ 
					sql="truncate table tblmisdtl";
					funExecuteQuery(sql);
					sql="truncate table tblmishd";
					funExecuteQuery(sql);
					sql="truncate table tblmrpidtl";
					funExecuteQuery(sql);
					break;
				}
				
			
				
				case"Material Production":
				{ 
					sql="truncate table tblproductiondtl";
					funExecuteQuery(sql);
					sql="truncate table tblproductionhd";
					funExecuteQuery(sql);
					break; 
				}
				case"Meal Planing":
				{ 
					sql="truncate table tblproductionorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblproductionorderhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Indent":
				{ 
					sql="truncate table tblpurchaseindenddtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchaseindendhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Order":
				{ 
					sql="truncate table tblpurchaseorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchaseorderhd";
					funExecuteQuery(sql);
					sql="truncate table tblpotaxdtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Return":
				{ 
					sql="truncate table tblpurchasereturndtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchasereturnhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Requisition":
				{ 
					sql="truncate table tblreqdtl";
					funExecuteQuery(sql);
					sql="truncate table tblreqhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Stock Adjustment":
				{ 
					sql="truncate table tblstockadjustmentdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstockadjustmenthd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Physical Stk Posting":
				{ 
					sql="truncate table tblstockpostingdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstockpostinghd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Stock Transfer":
				{ 
					sql="truncate table tblstocktransferdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstocktransferhd";
					funExecuteQuery(sql);
					break;
				}
				
				case"Work Order":
				{ 
					sql="truncate table tblworkorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblworkorderhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Rate Contract":
				{ 
					sql="truncate table tblratecontdtl";
					funExecuteQuery(sql);
					sql="truncate table tblrateconthd";
					funExecuteQuery(sql);
					break; 
				}
				
				
				
////////////////////////WebStock Transaction End///////////////////////////////////////////				
				
////////////////////////WebCRM Transaction Start///////////////////////////////////////////					
		
		
				case"Delivery Challan":
				{ 
					sql="truncate table tbldeliverychallanhd";
					funExecuteQuery(sql);
					sql="truncate table tbldeliverychallandtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Delivery Note":
				{ 
					sql="truncate table tbldeliverynotehd";
					funExecuteQuery(sql);
					sql="truncate table tbldeliverynotedtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Excise Challan":
				{ 
					
					break; 
				}
				
				case"Invoice":
				{ 
					sql="truncate table tblinvoicedtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvoicehd";
					funExecuteQuery(sql);
					sql="truncate table tblinvprodtaxdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvsalesorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvtaxdtl";
					funExecuteQuery(sql);
					
					break; 
				}
				
				case"Job Order Allocation":
				{ 
					sql="truncate table tbljoborderallocationhd";
					funExecuteQuery(sql);
					sql="truncate table tbljoborderallocationdtl";
					funExecuteQuery(sql);
					break;  
				}
				
				
				case"Job Order":
				{ 
					sql="truncate table tbljoborderhd";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sales Order":
				{ 
					sql="truncate table tblsalesorderhd";
					funExecuteQuery(sql);
					sql="truncate table tblsalesorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblsaleschar";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sales Order BOM":
				{ 
					sql="truncate table tblsalesorderbom";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sub Contractor GRN":
				{ 
					sql="truncate table tblscreturnhd";
					funExecuteQuery(sql);
					sql="truncate table tblscreturndtl";
					funExecuteQuery(sql);
					
					
					break; 
					
				}
		
		
		
		
		
////////////////////////WebCRM Transaction END///////////////////////////////////////////	
				default:
				{ 
					sql="truncate table tbltempitemstock";
					funExecuteQuery(sql);
					sql="truncate table tblpossalesdtl";
					funExecuteQuery(sql);
				
				}
				
				
				
				
				
				
			}
			
		}
	}
	
	@Override
	public void	funClearTransactionByPropertyAndLoction(String clientCode,String[] str,String propName,String locName) {
		for(int i=0; i<str.length;i++){
			String sql="";
			List<String> listPropertyCode = new ArrayList<>();
			String sqlPropertyCode="select strPropertyCode from tblpropertymaster where strPropertyName='"+propName+"' and strClientCode='"+clientCode+"' ";
			listPropertyCode=objGlobalFunctionsService.funGetDataList(sqlPropertyCode, "sql");
			String propCode=listPropertyCode.get(0);
			List<String> listlocCode = new ArrayList<>();
			String sqllocCode="select strLocCode from tblpropertymaster where strLocName='"+locName+"' and strClientCode='"+clientCode+"' ";
			listlocCode=objGlobalFunctionsService.funGetDataList(sqllocCode, "sql");
			String locCode=listlocCode.get(0);
			String condition="";
			
			
			switch(str[i]){

////////////////////////WebStock Transaction Start///////////////////////////////////////////	
				case"Bill Passing":
				{
						sql="truncate table tblbillpassdtl";
						funExecuteQuery(sql);
				 		sql="truncate table tblbillpasshd";
				 		funExecuteQuery(sql);
				 		sql="truncate table tblbillpassingtaxdtl";
				 		funExecuteQuery(sql);
				 		break; 
				}
				
				case"GRN(Good Receiving Note)":
				{ 
					sql="DELETE FROM tblgrndtl WHERE strIssueLocation='"+locCode+"' and strGRNCode LIKE '%"+propCode+"'";
					funExecuteQuery(sql);
					sql="DELETE FROM tblgrnhd WHERE strlocCode='"+locCode+"' and strGRNCode LIKE '%"+propCode+"'";
					funExecuteQuery(sql);
					sql="DELETE FROM tblgrntaxdtl WHERE strIssueLocation='"+locCode+"' and strGRNCode LIKE '%"+propCode+"'";
					funExecuteQuery(sql);
					sql="DELETE FROM tblgrnmisdtl WHERE strIssueLocation='"+locCode+"' and strGRNCode LIKE '%"+propCode+"'";
					funExecuteQuery(sql);
					sql="DELETE FROM tblbatchhd WHERE strIssueLocation='"+locCode+"' and strGRNCode LIKE '%"+propCode+"'";
					funExecuteQuery(sql);
					
					break; 
				}
				
				case"Opening Stock":
				{ 	sql="truncate table tblinitialinvdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinitialinventory";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Return":
				{ 
					sql="truncate table tblmaterialreturndtl";
					funExecuteQuery(sql);
					sql="truncate table tblmaterialreturnhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Issue Slip":
				{ 
					sql="truncate table tblmisdtl";
					funExecuteQuery(sql);
					sql="truncate table tblmishd";
					funExecuteQuery(sql);
					sql="truncate table tblmrpidtl";
					funExecuteQuery(sql);
					break;
				}
				
			
				
				case"Material Production":
				{ 
					sql="truncate table tblproductiondtl";
					funExecuteQuery(sql);
					sql="truncate table tblproductionhd";
					funExecuteQuery(sql);
					break; 
				}
				case"Meal Planing":
				{ 
					sql="truncate table tblproductionorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblproductionorderhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Indent":
				{ 
					sql="truncate table tblpurchaseindenddtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchaseindendhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Order":
				{ 
					sql="truncate table tblpurchaseorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchaseorderhd";
					funExecuteQuery(sql);
					sql="truncate table tblpotaxdtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Purchase Return":
				{ 
					sql="truncate table tblpurchasereturndtl";
					funExecuteQuery(sql);
					sql="truncate table tblpurchasereturnhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Material Requisition":
				{ 
					sql="truncate table tblreqdtl";
					funExecuteQuery(sql);
					sql="truncate table tblreqhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Stock Adjustment":
				{ 
					sql="truncate table tblstockadjustmentdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstockadjustmenthd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Physical Stk Posting":
				{ 
					sql="truncate table tblstockpostingdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstockpostinghd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Stock Transfer":
				{ 
					sql="truncate table tblstocktransferdtl";
					funExecuteQuery(sql);
					sql="truncate table tblstocktransferhd";
					funExecuteQuery(sql);
					break;
				}
				
				case"Work Order":
				{ 
					sql="truncate table tblworkorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblworkorderhd";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Rate Contract":
				{ 
					sql="truncate table tblratecontdtl";
					funExecuteQuery(sql);
					sql="truncate table tblrateconthd";
					funExecuteQuery(sql);
					break; 
				}
				default:
				{ 
					sql="truncate table tbltempitemstock";
					funExecuteQuery(sql);
					sql="truncate table tblpossalesdtl";
					funExecuteQuery(sql);
				
				}
				
////////////////////////WebStock Transaction End///////////////////////////////////////////				
				
////////////////////////WebCRM Transaction Start///////////////////////////////////////////					
				
				
				case"Delivery Challan":
				{ 
					sql="truncate table tbldeliverychallanhd";
					funExecuteQuery(sql);
					sql="truncate table tbldeliverychallandtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Delivery Note":
				{ 
					sql="truncate table tbldeliverynotehd";
					funExecuteQuery(sql);
					sql="truncate table tbldeliverynotedtl";
					funExecuteQuery(sql);
					break; 
				}
				
				case"Excise Challan":
				{ 
					
					break; 
				}
				
				case"Invoice":
				{ 
					sql="truncate table tblinvoicedtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvoicehd";
					funExecuteQuery(sql);
					sql="truncate table tblinvprodtaxdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvsalesorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvtaxdtl";
					funExecuteQuery(sql);
					sql="truncate table tblinvtaxgst";
					funExecuteQuery(sql);
					
					
					break; 
				}
				
				case"Job Order Allocation":
				{ 
					sql="truncate table tbljoborderallocationhd";
					funExecuteQuery(sql);
					sql="truncate table tbljoborderallocationdtl";
					funExecuteQuery(sql);
					break;  
				}
				
				
				case"Job Order":
				{ 
					sql="truncate table tbljoborderhd";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sales Order":
				{ 
					sql="truncate table tblsalesorderhd";
					funExecuteQuery(sql);
					sql="truncate table tblsalesorderdtl";
					funExecuteQuery(sql);
					sql="truncate table tblsaleschar";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sales Order BOM":
				{ 
					sql="truncate table tblsalesorderbom";
					funExecuteQuery(sql);
					
					break; 
					
				}
				
				case"Sub Contractor GRN":
				{ 
					sql="truncate table tblscreturnhd";
					funExecuteQuery(sql);
					sql="truncate table tblscreturndtl";
					funExecuteQuery(sql);
					
					
					break; 
					
				}
				
				
				
				
				
////////////////////////WebCRM Transaction END///////////////////////////////////////////					
				
			}
			
		}
	}
	

	@Override
	public void funClearMaster(String clientCode,String[] str) {
		
		for(int i=0; i<str.length;i++){
			String sql="";
			switch(str[i])
			{
			
			
////////////////////////WebStock Master Start///////////////////////////////////////////		
				case"Attachement Master":
				{
					sql = "truncate table tblattachdocument";
					funExecuteQuery(sql);
					break;
				}
				case"Attribute Master":
				{
					sql = "truncate table tblattributemaster";
					funExecuteQuery(sql);
					break;
				}
				case"Attribute Value Master":
				{
					sql = "truncate table tblattvaluemaster";
					funExecuteQuery(sql);
					break;
				}
				case"Reciepe Master":
				{
					sql = "truncate table tblbommasterdtl";
					funExecuteQuery(sql);
					sql = "truncate table tblbommasterhd";
					funExecuteQuery(sql);
					break;
				}
				case"Currency Master":
				{
					sql = "truncate table tblcurrencymaster";
					funExecuteQuery(sql);
					break;
				}
				case"Group Master":
				{
					sql = "truncate table tblgroupmaster";
					funExecuteQuery(sql);
					break;
				}
				case"Location Master":
				{
					sql = "truncate table tbllocationmaster";
					funExecuteQuery(sql);
					break;
				}
				case"Product Master":
				{
					
					sql = "truncate table tblprodchar";
					funExecuteQuery(sql);					
					sql = "truncate table tblproductmaster";
					funExecuteQuery(sql);
					sql = "truncate table tblprodsuppmaster";
					funExecuteQuery(sql);
					sql = "truncate table tblreorderlevel";
					funExecuteQuery(sql);
					sql = "truncate table tblprodprocess";
					funExecuteQuery(sql);
					break;
				}
				case"Supplier Master":
				{
					sql = "truncate table tblpartymaster";
					funExecuteQuery(sql);
					break;
				}
				case"Property Master":
				{
					sql = "truncate table tblpropertymaster";
					funExecuteQuery(sql);
					sql = "truncate table tblpropertysetup";
					funExecuteQuery(sql);
					sql = "truncate table tblworkflow";
					funExecuteQuery(sql);
					sql = "truncate table tblworkflowforslabbasedauth";
					funExecuteQuery(sql);
					sql = "truncate table tblprocessmaster";
					funExecuteQuery(sql);
					sql = "truncate table tblprocesssetup";
					funExecuteQuery(sql);
					break;
				}
				case"Reason Master":
				{
					sql = "truncate table tblreasonmaster";
					funExecuteQuery(sql);
					break;
				}
				case"Settlement Master":
				{
					sql = "truncate table tblsettlementmaster";
					funExecuteQuery(sql);
					break;
				}
				case"Sub Group Master":
				{
					sql = "truncate table tblsubgroupmaster";
					funExecuteQuery(sql);
					break;
				}
				case"Tax Master":
				{
					sql = "truncate table tbltaxdtl";
					funExecuteQuery(sql);
					sql = "truncate table tbltaxhd";
					funExecuteQuery(sql);
					sql = "truncate table tbltaxsettlementmaster";
					funExecuteQuery(sql);
					break;
				}
				case"TC Master":
				{
					sql = "truncate table tbltcmaster";
					funExecuteQuery(sql);
					sql = "truncate table tbltctransdtl";
					funExecuteQuery(sql);
					break;
				}
				case"UOM Master":
				{
					sql = "truncate table tbluommaster";
					funExecuteQuery(sql);
					break;
				}
				case"User Master":
				{
					sql = "truncate table tbluserhd";
					funExecuteQuery(sql);
					sql = "truncate table tbluserdtl";
					funExecuteQuery(sql);
					break;
				}
				
				
				
				
	////////////////////////WebStock Master End///////////////////////////////////////////			
				
////////////////////////WebCRM Master Start///////////////////////////////////////////	
				
				case"Security Shell":
				{
					sql = "truncate table tbluserdtl";
					funExecuteQuery(sql);
					break;
				}
				
				case"Customer Master":
				{
					sql = "Delete from tblpartymaster where strPType='cust'";
					funExecuteQuery(sql);
					break;
				}
				
				case"Sub Contractor Master":
				{
					sql = "Delete from tblpartymaster where strPType='subc'";
					funExecuteQuery(sql);
					break;
				}
				
				
				
////////////////////////WebCRM Master End///////////////////////////////////////////	
				
				
				
				
				
				
				
				
				
				
				
				
			}
		}
	}
}
