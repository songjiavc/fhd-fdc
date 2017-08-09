Ext.define('FHD.view.response.bpm.MakeSolutionPlanFormForBpm', {
	extend:'Ext.panel.Panel',
	aligs:'widget.makesolutionplanformforbpm',
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	requires: [
    ],
    border: false,
	autoScroll:true,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
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
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.方案制定',status:'current'},
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
					    		{index: 3, context:'3.任务分配',status:'done'},
					    		{index: 4, context:'4.方案制定',status:'current'},
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
	
	initComponent : function() {
		var me=this;
		me.getItems();
		me.bbar={
			items: [
				'->',{
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
						if(!me.isFinish()){
							Ext.MessageBox.show({
					    		title : '提示',
					    		width : 260,
					    		msg : '确认提交吗？',
					    		buttons : Ext.MessageBox.YESNO,
					    		icon : Ext.MessageBox.QUESTION,
					    		fn : function(btn) {
					    			if (btn == 'yes') {//确认
					    				me.body.mask("提交中...","x-mask-loading");
					    				FHD.ajax({
							    		    url : __ctxPath+ '/responseplan/workflow/makesolutionplan.f',
							    		    params : {
							    				executionId : me.executionId,
							    				businessId : me.businessId
											 },
							    			 callback : function(data) {
							    			 	me.body.unmask();
							    				if(data.success){
							    					if(me.winId){
														Ext.getCmp(me.winId).close();
													}
							    				}else{
							    					Ext.Msg.alert("提示","工作流执行失败！");
							    					return false;
							    				}
							    				
							    			 }
							    		});
					    			}
					    		}
					    	});
    					}else{
    						Ext.Msg.alert("提示","每个风险事件应至少有一个应对措施！");
				    		return false;
    					}
    				}
				}
    		]
		};
		
		me.flowtaskbar=Ext.widget('panel',{
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items: me.flowtaskbarItem
        });
		
		me.makesolutionplanform = Ext.create('FHD.view.response.bpm.MakeSolutionPlanForm',{flex:1});
		Ext.applyIf(me, {
			items:[me.flowtaskbar,me.makesolutionplanform],
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
		me.makesolutionplanform.initParam({
			executionId : me.executionId,
			businessId : me.businessId
		});
		me.makesolutionplanform.reloadData();
	},
	isFinish : function(){
		var me = this;
		var flag = false;
		var solutionDatas = me.makesolutionplanform.solutionlistforbpm.store.data;
		Ext.each(solutionDatas.items,function(item){
			if(item.get('measureId') == '' || item.get('measureId') == null ){
				flag = true;
			}
		});
		return flag;
	}
});