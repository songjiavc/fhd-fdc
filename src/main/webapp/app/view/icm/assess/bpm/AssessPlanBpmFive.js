Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmFive', {
    extend:'Ext.panel.Panel',
    alias: 'widget.assessplanbpmfive',
    
	autoScroll:false,
	//bodyPadding:'0 3 3 3',
	border:false,
	requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	
	initComponent: function() {
		var me=this;
		
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			//bodyPadding:'0 3 3 3',
			businessId:me.businessId
		});
		//任务分配列表
		var assessPlanProcessRelaEmpGrid=Ext.create('FHD.view.icm.assess.component.AssessPlanProcessRelaEmpGrid',{
			columnWidth:1/1,
			margin: '7 10 0 30',
			businessId:me.businessId
		});
		//fieldSet
		var childItems={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title : '评价范围',
			items : [assessPlanProcessRelaEmpGrid]
		};
		
		me.bbar=[
			'->',
			{
				 text: '提交',
				 iconCls: 'icon-operator-submit',
				 id:'assess_plan_bpm_five_submit_btn',
				 handler: function(){
					 //提交工作流
					 me.submit();
				 }
			}/*,
			{
				text:'关闭',
				iconCls: 'icon-control-fastforward-blue',
				handler: function () {
					if(me.winId){
						Ext.getCmp(me.winId).close();
					}
	            }
			}
			*/
		];
		
		me.contentContainer = Ext.create('Ext.panel.Panel',{
			flex:1,
			layout:{
				type:'column'
        	},
        	defaults:{
        		margin:'0 30 10 10',
        		columnWidth:1/1
        	},
        	overflowX:'hidden',
			overflowY:'auto',
			border:false,
        	items:[me.basicInfo,childItems]
		});
		
		Ext.applyIf(me, {
			layout : {
				align:'stretch',
				type : 'vbox'
			},
			items:[Ext.widget('flowtaskbar',{
        		jsonArray:[
 		    		{index: 1, context:'1.计划制定',status:'done'},
 		    		{index: 2, context:'2.计划审批',status:'done'},
 		    		{index: 3, context:'3.任务分配',status:'done'},
 		    		{index: 4, context:'4.任务分配审批',status:'done'},
 		    		{index: 5, context:'5.计划发布',status:'current'}
 		    	]
 	    	}),me.contentContainer]
		});
		
		me.callParent(arguments);
		
		if(me.businessId){
			me.basicInfo.loadData(me.businessId);
		}
	},
	submit:function(){
		var me=this;
		
		FHD.ajax({
			url: __ctxPath+ '/icm/assess/saveAssessorAndAssessResultById.f',
			async:false,
		    params : {
		    	assessPlanId:me.businessId
			},
			callback : function(data) {
				if(data){
					Ext.MessageBox.show({
			            title: '提示',
			            width: 260,
			            msg: '计划发布后，系统会将该计划分发给各个评价人进行测试，您确定发布该计划吗?',
			            buttons: Ext.MessageBox.YESNO,
			            icon: Ext.MessageBox.QUESTION,
			            fn: function (btn) {
			                if (btn == 'yes') {
								//提交按钮不可用
								Ext.getCmp('assess_plan_bpm_five_submit_btn').setDisabled(true);
								
								//提交工作流
								FHD.ajax({
									url: __ctxPath+ '/icm/assess/assessPlanPublish.f',
								    params : {
								    	businessId:me.businessId,
								    	executionId:me.executionId
									},
									callback : function(data) {
										if(me.winId){
											Ext.getCmp(me.winId).close();
										}else{
											//单点登录执行待办关闭window
											FHD.closeWindow();
										}
									}
								});
			                }
			            }
					});
				}
			}
		});
	},
	reloadData:function(){
		var me=this;
		
	}
});
