Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmEleven', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.assessplanbpmeleven',
    
    autoScroll:false,
    //bodyPadding:'0 3 3 3',
    border:false,
    requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
    
	initComponent : function() {
	    var me = this;
	    
	    me.flowImage = Ext.widget('image',{
        	src : __ctxPath + '/images/icm/assess/steps66.jpg',
            width: 850
        });
	    
	    me.bbar=[
 		    '->',
 		    {
 				text:'提交',
 				iconCls: 'icon-operator-submit',
 				id:'assess_plan_bpm_eleven_submit_btn',
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
	    
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			//bodyPadding:'0 3 3 3',
			businessId:me.businessId
		});
		
		me.assessGuidelinesGrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
			columnWidth:1/1,
			margin: '7 10 0 30',
			
			assessPlanId:me.businessId
		});
		
		me.assessGuidelinesGridPanel={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : true,
			columnWidth:1/1,
			collapsible : true,
			title: '评价标准查看',
			items :[me.assessGuidelinesGrid]
		};
		
		me.assessDefectGrid=Ext.create('FHD.view.icm.assess.component.AssessDefectFeedbackGrid',{
			margin: '7 10 0 30',
			defectsIsAvailable:false,
			feedbackIsAvailable:false,
			isOwnerOrg:'N',
			columnWidth:1/1,
			businessId:me.businessId
		});
		
		me.assessDefectGridPanel={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '缺陷确认',
			items :[me.assessDefectGrid]
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
        	items:[me.basicInfo,me.assessGuidelinesGridPanel,me.assessDefectGridPanel,ideaSet]
		});
		
		Ext.applyIf(me, {
			layout : {
				align:'stretch',
				type : 'vbox'
			},
			items:[Ext.widget('flowtaskbar',{
        		jsonArray:[
 		    		{index: 1, context:'1.内控测试',status:'done'},
		    		{index: 2, context:'2.测试结果复核',status:'done'},
		    		{index: 3, context:'3.汇总整理',status:'done'},
		    		{index: 4, context:'4.缺陷反馈',status:'done'},
		    		{index: 5, context:'5.缺陷调整',status:'done'},
		    		{index: 6, context:'6.缺陷确认',status:'current'}
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
					Ext.getCmp('assess_plan_bpm_eleven_submit_btn').setDisabled(true);
					
					FHD.ajax({
						url : __ctxPath+ '/icm/assess/assessDefectRecognition.f',
						params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea
						},
						callback : function(data) {
							if(data==true){
								if(me.winId){
									Ext.getCmp(me.winId).close();
								}else{
									//单点登录执行待办关闭window
									FHD.closeWindow();
								}
							}else{
								FHD.notification(data,FHD.locale.get('fhd.common.prompt'));
							}
						}
					});
                }
            }
		});
	},
	reloadData : function(){
		var me=this;
		
	}
});