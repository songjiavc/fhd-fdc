Ext.define('FHD.view.risk.riskidentify.taskset.RiskIdentifyTAskSetMain',{
 	extend: 'Ext.panel.Panel',
 	alias: 'widget.riskIdentifyTAskSetMain',
 	requires: [
	],
	border: false,
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
                if('complex'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'current'},
					    		{index: 5, context:'5.风险辨识',status:'undo'},
					    		{index: 6, context:'6.辨识汇总',status:'undo'},
					    		{index: 7, context:'7.单位主管审批',status:'undo'},
					    		{index: 8, context:'8.单位领导审批',status:'undo'},
					    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
					    		{index: 10, context:'10.结果整理',status:'undo'},
					    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
					    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }else{
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划审批',status:'done'},
					    		{index: 3, context:'3.任务分配',status:'current'},
					    		{index: 4, context:'4.风险辨识',status:'undo'},
					    		{index: 5, context:'5.辨识汇总',status:'undo'},
					    		{index: 6, context:'6.结果整理',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//提交工作流
	submit:function(){
		var me=this;
		var empIds = me.riskIdentifyTaskForm.empInput.getValue();
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({
			url : __ctxPath+ '/access/riskidentify/submitriskidentifytask.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	pingguEmpIds: empIds
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					Ext.getCmp(me.winId).close();
				}else{
					window.location.reload();
				}
			}
		});
	},
	
    reloadData:function(){
		var me=this;
	},
 	
    initComponent: function () {
    	
    	var me = this;
    	me.getItems();
    	me.riskIdentifyTaskForm = Ext.create('FHD.view.risk.riskidentify.taskset.RiskIdentifyTaskForm',{
    		flex: 1,
			businessId: me.businessId,
			executionId : me.executionId,
			border: false,
			schm:me.schm,
			margin : '0 0 0 0'
		});
		me.riskIdentifyTaskForm.formReLoad(me.businessId);
		me.bbar={
			items: ['->',
				   {
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
    					//提交工作流
    					var empIds = me.riskIdentifyTaskForm.empInput.getValue();
						if(!empIds.length){
							FHD.notification('辨识人不能为空！',FHD.locale.get('fhd.common.prompt'));
							return ;
						}
						Ext.MessageBox.show({
				    		title : '提示',
				    		width : 260,
				    		msg : '确认提交吗？',
				    		buttons : Ext.MessageBox.YESNO,
				    		icon : Ext.MessageBox.QUESTION,
				    		fn : function(btn) {
				    			if (btn == 'yes') {//确认删除
				    				me.riskIdentifyTaskForm.saveEmpRisks();
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
			border:false,
 			height:500,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
        	items:[me.flowtaskbar,me.riskIdentifyTaskForm],
        	margin : '0 0 0 0',
    	//工作流窗口最大化
    	listeners: {
			beforerender : function () {
				var me = this;
				FHD.ajax({
		            url: __ctxPath+ '/assess/quaassess/findAssessName.f',
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
    }

});