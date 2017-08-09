/**
 * 
 * 任务分配
 */
Ext.define('FHD.view.response.workplan.workplantask.TaskMainpanel',{
 	extend: 'Ext.panel.Panel',
 	border:false,
 	height:500,
 	alias: 'widget.taskMainpanel',
 	requires: [
	],
	//获得导航item
	getItems: function(){
		var me = this;
		FHD.ajax({
            url: __ctxPath + '/access/riskidentify/findriskidentifydescription.f',
            params : {
            	executionId: me.executionId
	    	},
            async: false,
            callback: function (data) {
            	var items;
                me.description = data;
                if('responseMore'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'current'},
					    		{index: 5, context:'5.方案制定',status:'undo'},
					    		{index: 6, context:'6.方案审批',status:'undo'},
					    		{index: 7, context:'7.单位主管审批',status:'undo'},
					    		{index: 8, context:'8.单位领导审批',status:'undo'},
					    		{index: 9, context:'9.业务分管副总审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }else{
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划审批',status:'done'},
					    		{index: 3, context:'3.任务分配',status:'current'},
					    		{index: 4, context:'4.方案制定',status:'undo'},
					    		{index: 5, context:'5.方案审批',status:'undo'},
					    		{index: 6, context:'6.方案执行',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//提交
 	submit:function(){
		var me=this;
		var empIds = [];
		var items = me.taskGridPanel.store.data.items;
		for(var k in items){
				if(!items[k].data.empId){
					 empIds = [];
					 break;
				}else{
					empIds.push(me.taskGridPanel.store.data.items[k].data.empId);
				}
		}
		if(!empIds.length){
			FHD.notification('评估人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/responseplan/workflow/submitrisktaskdistribute.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	pingguEmpIds: empIds
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					Ext.getCmp(me.winId).close();
				}
			}
		});
	},
 	//初始化
    initComponent: function () {
    	var me = this;
    	me.getItems();
    	me.taskGridPanel=Ext.create('FHD.view.response.workplan.workplantask.TaskGridPanel',{
			businessId:me.businessId,
			border:false,
			margin : '0 0 0 0'
		});
		me.bbar={
			items: ['->',
				   {
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
    					//提交工作流
    					Ext.MessageBox.show({
				    		title : '提示',
				    		width : 260,
				    		msg : '确认提交吗？',
				    		buttons : Ext.MessageBox.YESNO,
				    		icon : Ext.MessageBox.QUESTION,
				    		fn : function(btn) {
				    			if (btn == 'yes') {//确认删除
				    				me.submit();
				    			}
				    		}
				    	});
    				} 
    			}
    		]
		};
		
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
		
		Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
        	items:[me.flowtaskbar,me.taskGridPanel],
        	margin : '0 0 0 0',
	    	//工作流窗口最大化
	    	listeners: {
				beforerender : function () {
					var me = this;
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params : {
			            	assessPlanId: me.businessId
				    	},
			            callback: function (data) {
			                if (data && data.success) {
			                	var assessPlanName = data.assessPlanName;
			                	me.flowtaskbar.setTitle('计划名称:' + assessPlanName);
			                } 
			            }
		        	});
				}
			}
		});
    		
    	me.callParent(arguments);
    },
    
    reloadData:function(){
		var me=this;
	}

});