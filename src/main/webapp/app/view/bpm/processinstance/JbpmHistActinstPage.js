
Ext.define('FHD.view.bpm.processinstance.JbpmHistActinstPage',{
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.JbpmHistActinstPage',
	url : __ctxPath + '/jbpm/processInstance/vJbpmHistActinstPage.f',
	mergeTaskAssigneeUrl : __ctxPath + '/jbpm/mergeTaskAssignee.f',
	
    jbpmHistProcinstId:"",
    processInstanceId:"",
    model:"show",
    border:0,
	reloadParentData:function(){
		
	},
    reloadData:function(){
		var me=this;
		me.store.load();
	},
    initComponent : function() {
    	var me = this;
    	
    	var tbarItems=new Array();
    	var listeners={};
    	var checked=false;
    	if(me.model=="edit"){
    		/**
    		 * 强制转办
    		 */
	    	tbarItems.push({name:'update', text : "转办",iconCls: 'icon-edit',handler:me.update, disabled : true, scope : this});
	    	tbarItems.push({name:'untread', text : "退回",iconCls: 'icon-ibm-backButton',handler:me.untread, disabled : true, scope : this});
	    	tbarItems.push({name:'remove', text : FHD.locale.get('fhd.common.delete'),iconCls: 'icon-del',handler:me.remove, disabled : true, scope : this});
	    	listeners={
				//按钮控制监听
				selectionchange:function(selection,selecteds){
					me.down("[name='update']").disable();
					me.down("[name='untread']").disable();
					me.down("[name='remove']").disable();
					if(selecteds.length==1){
						if(selecteds[0].get('dbversion')==0){
							me.down("[name='update']").enable();
							me.down("[name='untread']").enable();
						}
					}
					if(selecteds.length>=1){
						for (var i in selecteds) {
							if(selecteds[i].get('dbversion')==0){
								me.down("[name='remove']").enable();
							}else{
								me.down("[name='remove']").disable();
								break;
							}
						}
					}
				}
			};
			checked=true;
    	}
    	Ext.apply(me,{
    		extraParams:{jbpmHistProcinstId:me.jbpmHistProcinstId,type:"task"},
    		checked:checked,
    		cols : [
				{dataIndex:'id',invisible:true},
				{dataIndex:'executionId',invisible:true},
				{dataIndex:'dbversion',invisible:true},
				{dataIndex:'jbpmHistTaskId',invisible:true},
				{dataIndex:'assigneeId',invisible:true},
				{dataIndex:'assigneeCode',invisible:true},
				{dataIndex:'transition',invisible:true},
				{header: "任务名称",dataIndex: 'activityName',sortable: true,flex:1.5,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				}, 
				{header: "状态",dataIndex: 'dbversionStr',sortable: true,flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				},
				{header: "承办人",dataIndex: 'assigneeRealName',sortable: false,flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
					}
				},
				{header: "承办人部门",dataIndex: 'assigneeOrgName',sortable: false,flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				},
				{header: "到达时间",dataIndex: 'startStr',sortable: true,flex:1.5,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				},
				{header: "操作时间",dataIndex: 'endStr',sortable: true,flex:1.5,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				},
				{header: "承办人公司",dataIndex: 'assigneeCompanyName',sortable: false,flex:1.5,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.get('dbversion')=='0'){
							return "<font color='green'>"+value+"</font>";
						}else{
							return value;
						}
						
					}
				}
			],
			/*默认排序方式*/
			storeSorters:[{property:'dbversionStr',direction:'asc'},{property:'startStr',direction:'desc'}],
			/*工具栏*/
			tbarItems:tbarItems,
			listeners:listeners
    	});
        me.callParent(arguments);
    },
   	update:function(){
		var me=this;
		var selecteds = me.getSelectionModel().getSelection();
		var selected=selecteds[0];
		var jbpmHistTaskId=selected.get('jbpmHistTaskId');
		var assigneeId=selected.get('assigneeId');
		var assigneeCode=selected.get('assigneeCode');
		var assigneeRealName=selected.get('assigneeRealName');
		var empSelectorWindow = Ext.create('FHD.ux.org.EmpSelectorWindow',{
			type:'emp',
			multiSelect:false,
			onSubmit:function(empSelectorWindow){
				var empId=null;
				empSelectorWindow.selectedgrid.store.each(function(r){
					empId=r.get("id");
				});
				if(empId&&assigneeId!=empId){
					jQuery.ajax({
						type: "POST",
						url: me.mergeTaskAssigneeUrl,
						data: {
							taskId:jbpmHistTaskId,
							empId:empId
						},
						success: function(msg){
							FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
							me.store.load();
						},
						error: function(){
							FHD.alert("操作失败！");
						}
					});
				}
			}
		});
		empSelectorWindow.setValue([{id:assigneeId,empno:assigneeCode,empname:assigneeRealName}]);
		empSelectorWindow.show();
    },
    untread:function(){
		var me=this;
		var selecteds = me.getSelectionModel().getSelection();
		var selected=selecteds[0];
		var id=selected.get('id');
		var executionId=selected.get('executionId');
		var window = Ext.create('FHD.ux.Window',{
			iconCls:'icon-ibm-backButton',
			title:"流程退回",
			width:400,
			height:140,
			items: UntreadActinstForm,
			maximizable: true,
			border:0,
			bbar:{
				xtype:'toolbar',
				items:[
					'->',
					{
						xtype:'button',
						iconCls : "icon-ibm-backButton",
			        	text:'退回',
						handler : function(){
							UntreadActinstForm.submitData();
						}
					},{
						xtype:'button',
						iconCls : "icon-ibm-close",
			        	text:'关闭',
						handler : function(){
							window.close();
						}
					}
				]
			}
		});
		window.show();
		var UntreadActinstForm = Ext.create('FHD.view.bpm.processinstance.untread.UntreadActinstForm',{
			jbpmHistActinstId:id,
			executionId:executionId,
			processInstanceId:me.processInstanceId,
			a_submitData:function(){
				me.reloadData();
				window.close();
			}
		});
		window.add(UntreadActinstForm);
    },
	remove:function(){
		var me=this;
		Ext.MessageBox.confirm('警告', '请注意：删除该代办可能造成：“分支汇总”或“删除流程实例”。您是否确认删除？', function showResult(btn){
			if("yes"==btn){
				var selecteds = me.getSelectionModel().getSelection();
				var taskIds=new Array();
				for(var i in selecteds){
					jbpmHistTaskId=selecteds[i].get('jbpmHistTaskId');
					taskIds.push(jbpmHistTaskId);
				}
				jQuery.ajax({
					type: "POST",
					url: __ctxPath +"/jbpm/removeTask.f",
					data: "taskIdsStr="+taskIds,
					success: function(msg){
						me.store.load();
						me.reloadParentData();
						Ext.MessageBox.alert('提示', '操作成功!');
					},
					error: function(){
						Ext.MessageBox.alert('提示',"操作失败!");
					}
				});
			}
		});
    }
});
