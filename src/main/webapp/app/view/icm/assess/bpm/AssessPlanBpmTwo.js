/*
 * 内控评价计划，审批页面
 * businessId:评价计划Id
 */
Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmTwo',{
	extend:'Ext.panel.Panel',
	aligs:'widget.assessplanbpmtwo',
	
	autoScroll:false,
	border:false,
	requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	
	initComponent : function() {
		var me=this;
		
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			//magin:'0 3 3 3',
			isShowGroupPers:false,
			businessId:me.businessId
		});
		//评价计划可编辑列表
		me.assessPlanGrid=Ext.create('FHD.view.icm.assess.component.AssessPlanRelaProcessGrid',{
			checked:false,
			extraParams:{
				assessPlanId:me.businessId
			}
		});
		//fieldSet
		var childItems={
			xtype : 'fieldset',
			layout : {
				type : 'fit'
			},
			collapsed : false,
			collapsible : true,
			title : '评价范围',
			items : [me.assessPlanGrid]
		};
		//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
    	//fieldSet
		var fieldSet={
			xtype:'fieldset',
			title:'审批',
			collapsible : true,
			layout:{
				type:'column'
			},
			items:[me.ideaApproval]
		};
		
		me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				id:'assess_plan_bpm_two_submit_btn',
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
        	items:[me.basicInfo,childItems,fieldSet]
		});
		
		Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
        	items:[Ext.widget('flowtaskbar',{
        		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划审批',status:'current'},
		    		{index: 3, context:'3.任务分配',status:'undo'},
		    		{index: 4, context:'4.任务分配审批',status:'undo'},
		    		{index: 5, context:'5.计划发布',status:'undo'}
		    	]
	    	}),me.contentContainer]
		});
		
		me.callParent(arguments);
		
		me.load();
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
            		Ext.getCmp('assess_plan_bpm_two_submit_btn').setDisabled(true);
					
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/icm/assess/assessPlanApproval.f',
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
	operateColumn:function(){
		var me=this;
		
		if('ca_assessment_measure_0'== me.assessPlanMeasureId){
			//穿行测试--隐藏抽样测试列
			me.assessPlanGrid.down('[dataIndex=isPracticeTest]').show();
			me.assessPlanGrid.down('[dataIndex=practiceNum]').show();
			me.assessPlanGrid.down('[dataIndex=isSampleTest]').hide();
			me.assessPlanGrid.down('[dataIndex=coverageRate]').hide();
		}else if('ca_assessment_measure_1' == me.assessPlanMeasureId){
			//抽样测试--隐藏穿行测试列
			me.assessPlanGrid.down('[dataIndex=isPracticeTest]').hide();
			me.assessPlanGrid.down('[dataIndex=practiceNum]').hide();
			me.assessPlanGrid.down('[dataIndex=isSampleTest]').show();
			me.assessPlanGrid.down('[dataIndex=coverageRate]').show();
		}else if('ca_assessment_measure_2' == me.assessPlanMeasureId){
			//全部：穿行测试和抽样测试
			me.assessPlanGrid.down('[dataIndex=isPracticeTest]').show();
			me.assessPlanGrid.down('[dataIndex=practiceNum]').show();
			me.assessPlanGrid.down('[dataIndex=isSampleTest]').show();
			me.assessPlanGrid.down('[dataIndex=coverageRate]').show();
		}
	},
	load:function(){
		var me=this;

		me.basicInfo.getForm().load({
            url: __ctxPath+'/icm/assess/findAssessPlanForView.f',
            params : {
            	assessPlanId:me.businessId
			},
            success: function (form, action) {
            	me.assessPlanTypeId = action.result.data.typeId;
				//评价类型
				if('ca_assessment_etype_self' != me.assessPlanTypeId){
					//组成员显示
					me.down('[name=handlerEmp]').show();
				}else{
					if(!me.isShowGroupPers){
						me.down('[name=handlerEmp]').hide();
					}
				}
				me.assessPlanMeasureId = action.result.data.assessMeasureId;
				me.operateColumn();
         	   	return true;
            },
            failure: function (form, action) {
         	   	return false;
            }
		});
	},
	reloadData:function(){
		var me=this;
		
	}
});