<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Counter Master</title>
<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
    /* add padding to account for vertical scrollbar */
    padding-right: 20px;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
    height: 200px;
}

</style>
<script type="text/javascript">
	$(document).ready(function() {

// 		 $('input#txtCounterCode').mlKeyboard({layout: 'en_US'});
// 		  $('input#txtCounterName').mlKeyboard({layout: 'en_US'});
		 
		  $("form").submit(function(event){
// 			  if($("#txtCounterName").val().trim()=="")
// 				{
// 					alert("Please Enter Counter Name");
// 					return false;
// 				}
			  
// 			  else{
// 				  flg=funChekTable();
// 				  return flg;
// 			  }
			});
		});
	
</script>
<script type="text/javascript">
var field;


	/**
	* Reset The Group Name TextField
	**/
	function funResetFields()
	{
// 		$("#txtCounterCode").focus();
		
    }
	
	
		
		function funHelp(transactionName)
		{	  
	     
	       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
	

		/**
		* Success Message After Saving Record
		**/
		$(document).ready(function()
		{
			var message='';
			<%if (session.getAttribute("success") != null) 
			{
				if(session.getAttribute("successMessage") != null)
				{%>
					message='<%=session.getAttribute("successMessage").toString()%>';
				    <%
				    session.removeAttribute("successMessage");
				}
				boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
				session.removeAttribute("success");
				if (test) 
				{
					%>alert("Data Saved \n\n"+message);<%
				}
			}%>
		});
		
		

		
					 
</script>		
</head>
<body onload="funLoadMenuHeadDtlData()">

	<div id="formHeading">
	<label>CounterMaster</label>
	</div>

<br/>
<br/>

	<s:form name="DiscountMaster" method="POST" action="savePOSDiscount Master.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<div class="title" style="margin-left: 190px;">
		
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Discount Code</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountCode" path="strDiscountCode" ondblclick="funHelp('POSCounterMaster')"/>
					</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountName" path="strDiscountName" />
					</div>
				</div>
					
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;">
						<label class="title">POS</label>
					</div>
	    			<div class="element-input col-lg-6" style="width: 50%;">
						<s:select id="cmbPOSName" path="strPosCode" items="${posList}" />
					</div> 	
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >From Date</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtFromDate" required="required" path="dteFromDate" pattern="\d{1,2}-\d{1,2}-\d{4}"/>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >To Date</label>
	    			</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtToDate" required="required" path="dteToDate" pattern="\d{1,2}-\d{1,2}-\d{4}" />
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Discount On</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:select id="cmbDiscountOn" path="strDiscountOn" >
						    <option value="All">All</option>
						    <option value="Item">Item</option>
						    <option value="Group">Group</option>
						    <option value="SubGroup">SubGroup</option>
						</s:select>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;">
						<s:input id="txtDiscountOnCode" required="required" path="strDiscountOnCode" />
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title">Discount Type</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:select id="cmbUserName" path="strDiscountType" >
								<option value="Percentage">Percentage</option>
								<option value="Amount">Amount</option>
						</s:select>
					</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountValue" required="required" path="dblDiscountValue" style="text-align: right;" />
					</div>
					<div class="element-input col-lg-6" style="width: 15%;margin-right: 5px;">
						<input id="btnAdd" type="button" value="Add" onclick="return btnAdd_onclick();" style="width: 100px;">
					</div>
					<div class="element-input col-lg-6" style="width: 15%;">
						<input id="btnRemove" type="button" value="Remove" onclick="return btnRemove_onclick(); ">
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
				
					<div class="col-xs-4" style="margin-left: -25%; width: 100%;">
				      	 		<div id="tableLoad" style="margin-left: -12px;">	
							
									<table class="scroll" style="width:70%;" >
				    					<thead>
				        					<tr> 
				            					<th style="width:36.5%">Name</th>
												<th style="width:36.5%">Discount Type</th>
												<th style="width:5.5%">Value</th>
				        					</tr>	
				    					</thead>
				    				</table>
				    		
				    				<table class="scroll" id="tblDiscDtl" style="width: 70%;">
				    					<tbody>				
									
										</tbody>
				    				</table>
				    				
				    			</div>
				   	 </div>
				   	 	 
			   	 </div>
			   	 
			   	 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 55px;">
				   	    <p align="center">
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" tabindex="3" class="form_button" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/></div>
					    </p>
				 </div>
				
		</div>
	
<!-- 		<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Discount Code</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input id="txtDiscountCode" name="txtDiscountCode" path="strDiscountCode" cssClass="searchTextBox" ondblclick="funHelp('POSCounterMaster')"/> --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input id="txtDiscountName" name="txtDiscountName" path="strDiscountName" cssClass="BoxW124px" /> --%>
					
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>POS</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:select id="cmbPOSName" name="cmbPOSName" path="strPosCode" items="${posList}" cssClass="BoxW124px" /> --%>
<!-- 				</td> -->
<!-- 				<td></td> -->
<!-- 				<td></td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 			<td><label>From Date</label></td> -->
<%-- 						<td><s:input id="txtFromDate" required="required" --%>
<%-- 								path="dteFromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" --%>
<%-- 								cssClass="calenderTextBox" /></td> --%>

<!-- 						<td><label>To Date</label></td> -->
<%-- 						<td><s:input id="txtToDate" required="required" path="dteToDate" --%>
<%-- 								pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox" /></td> --%>
 		
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Discount On</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 				<s:select id="cmbDiscountOn" name="cmbDiscountOn" path="strDiscountOn" cssClass="BoxW124px" > --%>
<!-- 				    <option value="All">All</option> -->
<!-- 				    <option value="Item">Item</option> -->
<!-- 				    <option value="Group">Group</option> -->
<!-- 				    <option value="SubGroup">SubGroup</option> -->
<%-- 				</s:select> --%>
<!-- 				</td> -->
				
<%-- 				<td><s:input id="txtDiscountOnCode" required="required" path="strDiscountOnCode" cssClass="BoxW124px" /></td> --%>
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Discount Type</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:select id="cmbUserName" name="cmbUserName" path="strDiscountType" cssClass="BoxW124px" > --%>
<!-- 					<option value="Percentage">Percentage</option> -->
<!-- 					<option value="Amount">Amount</option> -->
<%-- 					</s:select> --%>
<!-- 				</td> -->
<%-- 				<td><s:input id="txtDiscountValue" required="required" path="dblDiscountValue" cssClass="BoxW124px" /></td> --%>
<!-- 				 <td colspan=""><input id="btnAdd" type="button" class="smallButton" value="Add" onclick="return btnAdd_onclick();"></input> -->
<!-- 			  <td colspan=""><input id="btnRemove" type="button" class="smallButton" value="Remove" onclick="return btnRemove_onclick();"></input> -->
<!-- 			</tr> -->
			
<!-- 			<tr></tr> -->
<!-- 		</table> -->
<!-- 		<table border="1" class="myTable" style="width: 80%; margin:auto;" > -->
<!-- 										<thead> -->
<!-- 										<tr> -->
<!-- 											<th style="width:36.5%">Name</th> -->
<!-- 											<th style="width:36.5%">Discount Type</th> -->
<!-- 											<th style="width:5.5%">Value</th> -->
<!-- 										</tr> -->
										
<!-- 										</thead> -->
<!-- 										</table> -->
<!-- 										<div style="background-color: #a4d7ff;border: 1px solid #ccc;display: block; height: 150px; -->
<!-- 			    				margin:auto;overflow-x: hidden; overflow-y: scroll;width: 80%;"> -->
<!-- 									<table id="tblDiscDtl" class="transTablex col5-center" style="width: 100%;"> -->
<!-- 									<tbody>     -->
<%-- 											<col style="width:40%;"><!--  COl1   --> --%>
<%-- 											<col style="width:40%"><!--  COl2   --> --%>
<%-- 											<col style="width:4%;"><!--  COl3   -->								 --%>
<!-- 									</tbody>							 -->
<!-- 									</table> -->
									
						
<!-- 							</div> -->
<!-- 		<br /> -->
<!-- 		<br /> -->
		
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="Submit" tabindex="3" class="form_button" /> -->
<!-- 			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 		</p> -->

	</s:form>
</body>
</html>
