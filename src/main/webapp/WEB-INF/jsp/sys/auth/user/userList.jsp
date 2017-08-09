
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/includes.jsp"%>
<%@ taglib uri="fhd-tag" prefix="fhd" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><spring:message code="fhd.sys.auth.user.user"/></title>
	<script type="text/javascript">
		var mv_grid;
		
		function userAssignAuthority(idValue){
			//alert(idValue);
			//清空选择的用户.
			$("[name=id]:checkbox").each(function() {
				$(this).attr("checked", false); 
			});
			var s = document.getElementsByName("id");
		    for(var i=0;i <s.length;i++){ 
				if(idValue == s[i].value){
					//选中用户
					s[i].checked=true;
				}
		    }
	
			openWindow('<spring:message code="fhd.sys.auth.user.user"/><spring:message code="fhd.sys.auth.authority.assignAuthority"/>',800,440,"${ctx}/sys/auth/userAssignAuthority.do?id="+idValue,'no');
		}

		function userAssignRole(idValue){
			//alert(idValue);
			//清空选择的用户.
			$("[name=id]:checkbox").each(function() {
				$(this).attr("checked", false); 
			});
			var s = document.getElementsByName("id");
		    for(var i=0;i <s.length;i++){ 
				if(idValue == s[i].value){
					//选中用户
					s[i].checked=true;
				}
		    }
	
			openWindow('<spring:message code="fhd.sys.auth.user.user"/><spring:message code="fhd.sys.auth.user.assignRole"/>',814,249,"${ctx}/sys/auth/userAssignRole.do?id="+idValue,'no');
		}
		
		var heightBody;
		Ext.onReady(function(){
			heightBody=Ext.getBody().getHeight();
			var toolbar = Ext.create('Ext.toolbar.Toolbar',{
				items: [
					{text: '<spring:message code="fhd.common.add" />',handler:saveUser,iconCls:'icon-add'},'-',
					{text: '<spring:message code="fhd.common.modify" />',id:'edit',handler:updateUser,iconCls:'icon-edit',disabled:true},'-',
					{text: '<spring:message code="fhd.common.delete" />',id:'del',handler:removeUser,iconCls:'icon-del',disabled:true},'-',
					{text: '密码重置',id:'set', handler:resetPwd,iconCls:'icon-bullet-wrench ',disabled:true},'-',
					{text: '<spring:message code="fhd.common.query" />',handler:showAndHide,iconCls:'icon-zoom',id:'adquery'}
				]
			});
	
			var hs = [
				{header:'<spring:message code="fhd.common.username" />',dataIndex:'username',flex:1},
				{header:'<spring:message code="fhd.sys.auth.role.realName" />',dataIndex:'realname',flex:1},
				{header:'<spring:message code="fhd.common.status" />',dataIndex:'userStatus',flex:1,renderer:function(val){var ops=document.getElementById('userStatus').options;for(var i=0;i<ops.length;i++){if(ops[i].value==val)return ops[i].text;}}},
				{header:'<spring:message code="fhd.common.lockState" />',dataIndex:'lockstate',flex:1,renderer:function(val){if(val=='false')return '<spring:message code="fhd.common.notLocked"/>';if(val=='true')return '<spring:message code="fhd.common.lock"/>'}},
				{header:'<spring:message code="fhd.common.enable" />',dataIndex:'enable',flex:1,renderer:function(val){if(val=='false')return '<spring:message code="fhd.common.false"/>';if(val=='true')return '<spring:message code="fhd.common.true"/>'}},
				{header:'<spring:message code="fhd.common.regdate" />',dataIndex:'regdate',flex:1},
				{header:'<spring:message code="fhd.common.abatedate" />',dataIndex:'expiryDate',flex:1},
				{header:'<spring:message code="fhd.common.credentialsexpiryDate" />',dataIndex:'credentialsexpiryDate',flex:1},
				{header:'mac地址',dataIndex:'mac',flex:1},
				{header:'<spring:message code="fhd.common.operate" />',dataIndex:'id',flex:1,renderer:function(val){
					return "<a href='javascript:void(0)' onclick='javascript:userAssignAuthority(\""+val+"\");'><spring:message code="fhd.sys.auth.authority.assignAuthority"/></a>"
					+"&nbsp;||&nbsp;"
					+"<a href='javascript:void(0)' onclick='javascript:userAssignRole(\""+val+"\");'><spring:message code="fhd.sys.auth.user.assignRole"/></a>"
				}}
			]
			
			var store = Ext.create('Ext.data.Store',{
				pageSize: 20,
				idProperty: 'id',
				autoLoad : true,
				fields: ['id','username','realname','userStatus','lockstate','enable','regdate','expiryDate','credentialsexpiryDate','mac'],
				proxy:new Ext.data.proxy.Ajax({   
					url: '${ctx}/sys/auth/userListJSON.do',  
					reader: {
						type : 'json',
						root : 'datas',
						totalProperty :'totalCount'
					},
					timeout: 1000000  
				})
			})
			
			mv_grid = Ext.create('Ext.grid.Panel', {
				
				selModel:Ext.create('Ext.selection.CheckboxModel'),
				store: store,
				columns: hs,
				height: Ext.getBody().getHeight(),
				width: document.body.offsetWidth,
				tbar : toolbar,
				renderTo: 'list',
				bbar : Ext.create('Ext.PagingToolbar', {
					pageSize: 20,
					store: store,
					displayInfo: true,
					xtype: 'pagingtoolbar',
					displayMsg: '显示{0} - {1} 条,共 {2}条',
					emptyMsg: '没有记录',
					items:[{
						xtype:'combobox',
						store: Ext.create('Ext.data.ArrayStore',{
							fields: ['psize'],
							data : [[10],[20],[30],[50],[100],[200]]
						}),
						displayField:'psize',
						typeAhead: true,
						mode: 'local',
						width:75,
						forceSelection: true,
						triggerAction: 'all',
						emptyText:'每页条数',
						selectOnFocus:true,
						listeners:{
							select:function(c,r,o){
								store.pageSize = r[0].data.psize;
								//解决选择grid列表下面的参数时，原扩展参数丢失的问题
								store.proxy.extraParams.limit = r[0].data.psize;
								//me.store.proxy.extraParams = {limit:r[0].data.psize};
								store.load();
							}
						}
					}]
				})
			});
			
			
			mv_grid.getSelectionModel().on('selectionchange',function(grid,selected){
				Ext.getCmp("edit").disable();
				Ext.getCmp("del").disable();
				Ext.getCmp("set").disable();
		    	if(selected.length==1){
					Ext.getCmp("edit").enable();
					Ext.getCmp("del").enable();
					Ext.getCmp("set").enable();
		    	}else if(selected.length>1){
					Ext.getCmp("del").enable();
		    	}
			});
			//打开或关闭高级查询
			function showAndHide(){
				var divShows = document.getElementById("rmRiskTaxisDivId").style.display;
				if(divShows == 'none'){
					document.getElementById("rmRiskTaxisDivId").style.display='';
					var divHeight=Ext.get("rmRiskTaxisDivId").getHeight();
					var height=heightBody-divHeight;
					mv_grid.setHeight(height);			
				}else{
					document.getElementById("rmRiskTaxisDivId").style.display='none';
					mv_grid.setHeight(heightBody);
				}
			}
		});
		
		//新建用户
		function saveUser(){
			Ext.create('Ext.window.Window', {
				title: '新建用户信息',
				height: 320,
				width: 600,
				html:"<iframe src='${ctx}/sys/auth/userAdd.do' style='width:100%;height:100%;margin:0px 0px' scrolling='no' frameBorder='0'></iframe>"
			}).show();
		}

		//密码重置
		function resetPwd(){
			var rows = mv_grid.getSelectionModel().getSelections();
			if(rows.length==0){
				window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>","最少选择一个用户，进行操作!"); 
			}else{ 
				Ext.MessageBox.confirm("<spring:message code='fhd.common.alertBox'/>","确定重置密码吗？",function(btn){ 
					if(btn=='yes'){
						if(rows){
							var userIds = new Array();
							for (var i = 0; i < rows.length; i++) {
								userIds.push(rows[i].get("id"));
							}
							Ext.Ajax.request({
								url:'${ctx}/sys/auth/resetPwd.do',
							    method:'post',
							    params:{
									userIds: userIds
							    },
							    success:function(response){
							      	if(response.responseText=='true'){
							      		window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>","<spring:message code='fhd.common.operateSuccess'/>");
								    }else{
								    	window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>","<spring:message code='fhd.common.operateFailure'/>");
								    }
							    }
							});
						} 
					} 
				}); 
			}
		}
		
		//更新
		function updateUser(){
			//var id = document.getElementsByName("id");s
			
			var rows =mv_grid.getSelectionModel().getSelections(); 
			if(rows.length==0){
				Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>", '<spring:message code="fhd.common.updateSelect"/>'); 
				return;
			}
			if(rows.length>1){
				Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>",'<spring:message code="fhd.common.updateTip"/>');
				return;
			}
			var id=rows[0].get("id");
			var ret = FHD.openWindow("修改用户信息", 550,292, "${ctx}/sys/auth/userMod.do?id="+id,'no');
			
		}
		
		//删除
		function removeUser(){
			var rows =mv_grid.getSelectionModel().getSelections();
			if(rows.length==0){
				window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>", '<spring:message code="fhd.common.removeTip"/>'); 
				return;
			}
			
			Ext.MessageBox.confirm("<spring:message code='fhd.common.prompt'/>","<spring:message code='fhd.common.makeSureDelete'/>",function(btn){
				if(btn=='yes'){
					var ids = '';
					for(var i=0;i<rows.length;i++)
						ids+=rows[i].get('id')+',';
						
					FHD.ajaxReq('${ctx}/sys/auth/userDel.do',{ids:ids},function(data){
						if(data!='true'){
							window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>","<spring:message code='fhd.common.operateFailure'/>");
						}else{ 
							window.top.Ext.ux.Toast.msg("<spring:message code='fhd.common.prompt'/>","<spring:message code='fhd.common.operateSuccess'/>");
						}
						mv_grid.store.reload();
						Ext.getCmp("edit").disable();
			    		Ext.getCmp("del").disable();
			    		Ext.getCmp("set").disable();
					});
				}else{
					return;
				}
			});
		}	
		
		//查询
		function queryUser(){
			// 数据重新加载
		    mv_grid.store.baseParams = {
	    		userName: document.getElementById("userName").value,
	    		realname: document.getElementById("realName").value
			};
		    mv_grid.store.load({
		    	params:{		    			 
		    		start:0,
		    		limit:20			    		
		    	}
			});	
		}
	
		function keypup(event){
			if(event.keyCode==13){
				queryUser();
				return;
			}
		}
		Ext.EventManager.onWindowResize(function(width ,height){
   			var height1 = heightBody-Ext.get("rmRiskTaxisDivId").getHeight();
   			var divShows = document.getElementById("rmRiskTaxisDivId").style.display;
   			if(divShows=='none'){
   				mv_grid.setWidth(width);
   				mv_grid.setHeight(height);
   			}else{
   				mv_grid.setWidth(width);
   				mv_grid.setHeight(height1);
   			}
		});
	</script>
</head>
<body style="overflow:hidden">
	<div id="rmRiskTaxisDivId" style='display:none'>
		<form id="sysUserForm" name="sysUserForm" action="" method="post" onkeypress="keypup(event);">
			<span style='display:none'><fhd:dictEntrySelect value="" id="userStatus" dictName="userStatus" path="userStatus" cssClass="required"/></span>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
				<tr><td class="fhd_query_title" >&nbsp;<img src="${ctx}/images/plus.gif"  onclick="showAndHide(this)" width="15" height="15" /><spring:message code="fhd.common.querycondition"></spring:message></td></tr>
			</table>
			<!-- 查询条件 -->
			<table id="showTable" width="100%" border="0" cellpadding="0" cellspacing="0"
				class="fhd_query_table" style="display:block;">
				<tr>  
					<th><spring:message code="fhd.sys.auth.user.username"/>：</th>
					<td><input type="text" name="userName" id="userName" value="${param.username}"/></td>                                             
					<th>真实姓名：</th>
					<td><input type="text" name="realName" id="realName" value="${param.realname}"/></td>
				</tr>
				<tr>
					<td align="center" colspan="4">
						<input type="button" value="<spring:message code="fhd.common.search" />" class="fhd_btn" onclick="queryUser()"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id='list'></div>
</body>
</html>