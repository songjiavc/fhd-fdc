Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmSeven', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.assessplanbpmseven',
    
	autoScroll:false,
	//bodyPadding:'0 3 3 3',
	border:false,
	requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	
	initComponent : function() {
		var me=this;
		
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			//bodyPadding:'0 3 3 3',
			businessId:me.businessId
		});
		
		me.pTestStatGrid=Ext.create('FHD.view.icm.assess.component.PracticeTestDraftGrid',{
    		searchable:false,
    		pagable : false,
    		columnWidth:1/1,
    		margin: '7 10 0 30',
    		showAssessDate:true,
    		executionId:me.executionId,
    		businessId:me.businessId
    	});
    	me.pTestStatFieldset = {
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '穿行测试底稿预览',
			items :[me.pTestStatGrid]
		};
    	
    	me.sTestStatGrid=Ext.create('FHD.view.icm.assess.component.SampleTestDraftGrid',{
    		searchable:false,
    		pagable : false,
    		columnWidth:1/1,
    		margin: '7 10 0 30',
    		showAssessDate:true,
    		executionId:me.executionId,
    		businessId:me.businessId
    	});
    	me.sTestStatFieldset={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '抽样测试底稿预览',
			items :[me.sTestStatGrid]
		};
    	
    	//结果分析
    	me.assessResultAnalysis = Ext.create('FHD.view.icm.assess.form.AssessResultAnalysisForm',{
    		columnWidth:1/1,
    		executionId:me.executionId,
    		businessId:me.businessId
    	});
    	me.sampleAnalysisOfResults={
			xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '结果分析',
			items :[me.assessResultAnalysis]
		};
    	
    	me.assessdefecteditgrid=Ext.create('FHD.view.icm.assess.component.AssessDefectEditGrid',{
			searchable:false,
			pagable : false,
    		columnWidth:1/1,
    		margin: '7 10 0 30',
    		isEditable:false,
    		executionId:me.executionId,
			businessId:me.businessId
		});
    	me.defectFieldset = {
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '缺陷清单',
			items :[me.assessdefecteditgrid]
		};
    	
		//审批意见组件
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
		//fieldSet审批意见
		var ideaSet={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title : '审批',
			items : [me.ideaApproval]
		};
		
		me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				id:'assess_plan_bpm_seven_submit_btn',
				handler: function () {
					//提交工作流
					me.submit(me.ideaApproval.isPass,me.ideaApproval.getValue());
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
			flex : 1,
			border:false,
			layout:{
				type:'column'
        	},
        	defaults:{
        		margin:'0 30 10 10',
        		columnWidth:1/1
        	},
        	overflowX:'hidden',
			overflowY:'auto',
        	items:[me.basicInfo,me.pTestStatFieldset,me.sTestStatFieldset,me.sampleAnalysisOfResults,me.defectFieldset,ideaSet]
		});
		
		Ext.applyIf(me, {
			layout : {
				align:'stretch',
				type : 'vbox'
			},
			items:[Ext.widget('flowtaskbar',{
        		jsonArray:[
 		    		{index: 1, context:'1.内控测试',status:'done'},
		    		{index: 2, context:'2.测试结果复核',status:'current'},
		    		{index: 3, context:'3.汇总整理',status:'undo'},
		    		{index: 4, context:'4.缺陷反馈',status:'undo'},
		    		{index: 5, context:'5.缺陷调整',status:'undo'},
		    		{index: 6, context:'6.缺陷确认',status:'undo'}
 		    	]
 	    	}),me.contentContainer]
		});
		
		me.callParent(arguments);
		
		if(me.businessId){
			me.basicInfo.loadData(me.businessId);
		}
	},
	submit:function(isPass,examineApproveIdea){
		var me=this;
		
		Ext.MessageBox.show({
            title: '提示',
            width: 260,
            msg: '提交后将不能修改，您确定要提交么?',
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') {
					//提交按钮不可用
					Ext.getCmp('assess_plan_bpm_seven_submit_btn').setDisabled(true);
					
					FHD.ajax({
						url : __ctxPath+ '/icm/assess/assessPlanExecuteApproval.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea
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
	},
	reloadData:function(){
		var me=this;
		
	}
});