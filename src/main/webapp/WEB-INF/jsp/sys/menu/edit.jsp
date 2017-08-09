<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/includes.jsp"%>
<%@ taglib uri="fhd-tag" prefix="fhd" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><spring:message code="editmenu"/></title>
</head>
<body onload="load();">

	<form:form commandName="menuForm" action="" method="post">
		<form:hidden path="id" />
		<form:hidden path="seqNo" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr><td class="fhd_query_title" >&nbsp;<spring:message code="menu"/><spring:message code="edit"/></td></tr>
		</table>
		<table id="showTable" width="100%"  border="0" cellpadding="0" cellspacing="0" class="fhd_form_table">
			<tr>
				<th><spring:message code="name" />：</th>
				<td><form:input path="name" cssClass="required"></form:input></td>
			</tr>
			<tr>
				<th><spring:message code="parentMenuName" />：</th>
				<td><form:input path="parentMenu.name" disabled="true"></form:input></td>
			</tr>
			<tr>
				<th><spring:message code="title" />：</th>
				<td><form:input path="title"></form:input></td>
			</tr>
			<tr>
				<th><spring:message code="sn" />：</th>
				<td><form:input path="sort" cssClass="required"/></td>
			</tr>
			<tr>
				<th><spring:message code="menuLevel"/>：</th>
				<td><form:input path="rank" cssClass="required"/></td>
			</tr>
			<tr>
				<th><spring:message code="isLeaf" />：</th>
				<td>
					<select id="isLeaf" name="isLeaf" onchange="changeMenuType();">
						<option value=""><spring:message code="pleaseSelect"/></option>
						<option value="true" <c:if test="${menuForm.isLeaf eq 'true'}">selected="selected"</c:if>><spring:message code="true"/></option>
						<option value="false" <c:if test="${menuForm.isLeaf eq 'false'}">selected="selected"</c:if>><spring:message code="false"/></option>
					</select>
				</td>
			</tr>
			<tr>
				<th><spring:message code="functionName" />：</th>
				<td>
					<form:input path="authorityName" onclick="popWindows();" cssClass="lookup"/>
					<form:hidden path="authority_id"/>
				</td>
			</tr>
			<tr>
				<th colspan="2" align="center">
					<input type="button" id="submits" value="<spring:message code="save_btn"/>" class="fhd_btn" />
					&nbsp;&nbsp;
					<input type="reset" value="<spring:message code="reset_btn"/>"  class="fhd_btn" />
				</th>
			</tr>
		</table>
	</form:form>
	
	<script type="text/javascript">
		function popWindows(){
			FHD.openWindow('选择功能点', 800, 440, '${ctx}/sys/auth/functionList.do','no');
		}

		function selectFunction(code,name){
			document.getElementById("authorityName").value = name;
			document.getElementById("authority_id").value = code;
		}

		function deSelectFunction(){
			document.getElementById("authorityName").value="";
			document.getElementById("authority_id").value="";
		}
	
		function changeMenuType(){
			//获取select对象 
			var selectedObj = $("#isLeaf option:selected"); 
			//获取当前selected的值 
			var selected = selectedObj.get(0).value;
			if(selected == "true"){
				$("#authorityName").removeAttr("disabled");
			} else {
				document.getElementById("authorityName").value= "";
				document.getElementById("authority_id").value= "";
				$("#authorityName").attr("disabled","true");
			}
		}
		
		$(document).ready(function(){
			var validator = $("#menuForm").validate({
				errorElement: "em",
				success: function(label) {
					label.text("ok!").addClass("success");
				},
				rules: {
					name:{
						maxlength:100
					},
					title:{
						maxlength:100
					},
					url:{
						maxlength:255
					},
					rank:{
						maxlength:7
					},
					sort:{
						maxlength:7
					},
					isLeaf:{
						required:true
					}
				}
			});
			
			//获取select对象 
			var selectedObj = $("#isLeaf option:selected"); 
			//获取当前selected的值 
			var selected = selectedObj.get(0).value;
			if(selected == "true"){
				$("#authorityName").removeAttr("disabled");
			} else {
				document.getElementById("authorityName").value= "";
				document.getElementById("authority_id").value= "";
				$("#authorityName").attr("disabled","true");
			}

			$("#submits").click(function() {
				if(true == validator.form()){
					var msgTip = FHD.opWait();
					var options = {
						url:'${ctx}/sys/menu/edit.do',
						type:'POST',
						success:function(response) {
			     			msgTip.hide();
			     			if(response=='false'){
			     				window.top.Ext.ux.Toast.msg('提示', '操作失败!');
			     				
			     			}else if(response=='true'){
			     				window.top.Ext.ux.Toast.msg('提示', '操作成功!');
			         			
			         			closeWindow();
							}else if("isNull"==response){
								window.top.Ext.ux.Toast.msg('提示',"该菜单已经被删除！");
			     			}else{
			     				window.top.Ext.ux.Toast.msg('提示', response);
			     			}
			     		}
					};
					$('#menuForm').ajaxSubmit(options);
					return false;
				}else{
					return false;
				}
		    });
			
			$("#reset").click(function() {
		        validator.resetForm();
		    });
		});
	
		function load(){
			document.getElementById("authorityName").value= "${menuForm.authority.authorityName}";
			document.getElementById("authority_id").value= "${menuForm.authority.id}";
		}
	</script>
</body>
</html>