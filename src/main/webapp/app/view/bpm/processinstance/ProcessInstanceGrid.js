
Ext.define('FHD.view.bpm.processinstance.ProcessInstanceGrid',{
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.ProcessInstanceGrid',
    url : __ctxPath + '/jbpm/processInstance/processInstanceList.f',
    model:"show",
    autoDestroy : true,
    extraParams:{assigneeId:__user.empId,dbversion:1},
    reloadData: function() {
		var me = this;
		me.store.load();
	},
    del:function(){
    	var me=this;
    	Ext.MessageBox.confirm('警告', '确认删除？', function showResult(btn){
	        if("yes"==btn){
				var selecteds = me.getSelectionModel().getSelection();
		    	var processInstanceIds=new Array();
		    	for(var i=0;i<selecteds.length;i++){
		    		processInstanceIds.push(selecteds[i].get('processInstanceId'));
				}
				jQuery.ajax({
					type: "POST",
					url: __ctxPath +"/jbpm/removePorcessInstance.f",
					data: "processInstanceIdsStr="+processInstanceIds,
					success: function(msg){
						me.store.load();
						Ext.MessageBox.alert('提示', '操作成功!');
					},
					error: function(){
						Ext.MessageBox.alert('提示',"操作失败!");
					}
				});
	        }
	    });
    },
    editProcessInstance : function (jbpmHistProcinstId,processInstanceId,url,businessId){
    	var me=this;
    	if(url=="null"){
    		url=null;
    	}
		var taskPanel = Ext.create('FHD.view.bpm.processinstance.ProcessInstanceTab',{
			jbpmHistProcinstId : jbpmHistProcinstId,
			processInstanceId : processInstanceId,
			url:url,
			businessId:businessId,
			model:me.model,
			reloadParentData:me.reloadData()
		});
		var window = Ext.create('FHD.ux.Window',{
			title:"查看",
			iconCls:'icon-view',//标题前的图片
			items: taskPanel,
			maximizable: true,
			border:0
		});
		window.show();
	},
	initComponent : function() {
    	var me = this;
    	var tbarItems=new Array();
    	var listeners={};
    	var checked=false;
    	if(me.model=="edit"){
	    	tbarItems.push({name:'del', text : FHD.locale.get('fhd.common.delete'),iconCls: 'icon-del',handler:me.del, disabled : true, scope : this});
	    	listeners={
				/*按钮控制监听*/
				selectionchange:function(selection,selecteds){
					me.down("[name='del']").disable();
				   	if(selecteds.length>0){
				   		var flag=true;
				   		for(var i=0;i<selecteds.length;i++){
				   			if(selecteds[i].get('endactivity')=="end1" || selecteds[i].get('endactivity')=="remove1"){
								flag=false;
				   			}
				   		}
				   		if(flag){
				   			me.down("[name='del']").enable();
				   		}
				   	}
				}
			};
			checked=true;
    	}
    	Ext.apply(me,{
    		checked:checked,
    		cols : [
				{dataIndex:'id',invisible:true},
				{dataIndex:'processInstanceId',invisible:true},
				{dataIndex:'url',invisible:true},
				{dataIndex:'businessId',invisible:true},
				{dataIndex:'endactivity',invisible:true},
				{header: "流程名称",dataIndex: 'businessName',sortable: true,flex:2}, 
				{header: "类型",dataIndex: 'jbpmDeploymentName',sortable: true,flex:2},
				{header: "发起人",dataIndex: 'createByRealname',sortable: false,flex:1},
				{header: "发起时间",dataIndex: 'createTime',sortable: true,flex:2},
				{header: "执行情况",dataIndex: 'endactivityShow',sortable: true,flex:1},
				{header: "完成比例",dataIndex: 'rate',sortable: false,flex:1,align:'left',
					renderer:function(value, metaData, record, rowIndex, colIndex, store){
						return "<font color='#2c4674'>" + value + "%</font>";					
				    }
				},
				{
					header: FHD.locale.get('fhd.common.operate'), dataIndex: 'id', sortable: false, width:100,align:'center',renderer: function(value, metaData, record, colIndex, store, view) { 
						return '<a href="javascript:void(0);" class="icon-view" data-qtitle="" data-qtip="查看" onclick="Ext.getCmp(\''+me.id+'\').editProcessInstance(\''+record.get('id')+'\',\''+record.get('processInstanceId')+'\',\''+record.get('url')+'\',\''+record.get('businessId')+'\')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>';
					}
				}
			],
			/*默认排序方式*/
			storeSorters:[{property:'endactivity',direction:'asc'},{property:'createTime',direction:'desc'}],
			/*工具栏*/
			tbarItems:tbarItems,
			listeners:listeners
    	});
        me.callParent(arguments);
    }
});
