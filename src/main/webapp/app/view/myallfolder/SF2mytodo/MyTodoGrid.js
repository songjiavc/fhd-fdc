
Ext.define('FHD.view.myallfolder.SF2mytodo.MyTodoGrid',{
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.mytodogrid',
    pagable:false,
    autoDestroy : true,
    url : __ctxPath + '/jbpm/processInstance/findVJbpm4TaskBySome.f',
	storeGroupField:'createDate',
	storeGroupDir:'desc',
	overflowX:'hidden',
	featuresCollapsible:false,
	viewConfig:{
		stripeRows:false
	},
	reloadData: function() {
		var me = this;
		me.store.load();
	},
    
    execute : function (grid, ele, rowIndex){
    	
    	var jEle=jQuery(ele);
    	var me = this.up('panel');
		var winId = "win" + Math.random()+"$ewin";
		var taskPanel = Ext.create(jEle.find("[name='url']").val(),{
			executionId : jEle.find("[name='executionId']").val(),
			businessId : jEle.find("[name='businessId']").val(),
			winId: winId,
			typeId :jEle.find("[name='schm']").val(),
			schm :jEle.find("[name='schm']").val()
		});
//		console.log("吉志强在测试MyTodoGrid 30行")
//		console.log("winId:="+winId);
//		console.log("executionId:="+jEle.find("[name='executionId']").val());
//		console.log("businessId:="+jEle.find("[name='businessId']").val());
//		console.log("url:="+jEle.find("[name='url']").val());
//		console.log("schm:="+jEle.find("[name='schm']").val());
		
		
		var window = Ext.create('FHD.ux.Window',{
			id:winId,
			title:FHD.locale.get('fhd.common.execute'),
			iconCls: 'icon-edit',//标题前的图片
			maximizable: true,
			listeners:{
				close : function(){
					me.reloadData();
				}
			}
		});
		window.show();
		window.add(taskPanel);
		taskPanel.reloadData();
	},
	
	rate:function(){
    	var me=this;
    	var selecteds = me.getSelectionModel().getSelection();
    	var executionId=selecteds[0].get('executionId');
		jQuery.ajax({
			type: "POST",
			url: __ctxPath +"/jbpm/saveRateByExecutionId.f",
			data: {executionId:executionId},
			success: function(msg){
				me.store.load();
				FHD.notification('提示', '操作成功!');
			},
			error: function(){
				FHD.alert("操作失败!");
			}
		});
    },
	
    initComponent : function() {
    	var me = this;
    	Ext.apply(me,{
    		//测试百分比用
    		/*tbarItems:[{
				name : 'rate',
				text : '计算百分比',
				iconCls : 'icon-del',
				handler : me.rate,
				scope : this
			}],*/
    		extraParams:{assigneeId:__user.empId,endactivity:"execut1",dbversion:0},
    		checked:false,
    		cols : [
				{dataIndex: 'executionId', invisible:true},
				{dataIndex: 'businessId', invisible:true},
				{dataIndex: 'form', invisible:true},
				//待办执行过程中返回每个待办任务对应的流程实例所对应的计划的schm 吉志强 2017年4月14日16:00:49
				{dataIndex: 'schm', invisible:true},
				{header: FHD.locale.get('fhd.common.operate'), dataIndex: 'operate', sortable: false, width:100,align:'center',renderer: function(value, metaData, record, colIndex, store, view) { 
						return "<a href=\"javascript:void(0);\" class=\"icon-view\" data-qtitle='' data-qtip=\"执行\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;执行<input name='url' type='hidden' value='"+record.get("form")+"'><input name='executionId' type='hidden' value='"+record.get("executionId")+"'><input name='businessId' type='hidden' value='"+record.get("businessId")+"'><input name='schm' type='hidden' value='"+record.get("schm")+"'></a>";//待办执行过程中返回每个待办任务对应的流程实例所对应的计划的schm 吉志强 2017年4月14日16:00:49
					},
					listeners:{
	            		click:{
	            			fn:me.execute
	            		}
	            	}
				},
				{header: FHD.locale.get('fhd.pages.test.field.name'), dataIndex: 'businessName', sortable: false, flex :3},
				{header:"流程节点",dataIndex: 'activityName', sortable: false, flex :2},
				{header: FHD.locale.get('fhd.bpm.task.taskPage.businessType'), dataIndex: 'disName', sortable: true, flex : 2},
				{header: "日期", dataIndex: 'createDate', sortable: true, flex : 1.5}
			]
    	});
        me.callParent(arguments);
    }
});
