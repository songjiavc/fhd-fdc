Ext.define('FHD.view.response.responsplanemore.ResponsePlanApproverTwoMain', {
	extend:'Ext.panel.Panel',
	aligs:'widget.responsePlanApproverTwoMain',
	requires: [
    ],
    border: false,
	//提交方法
	submitWindow: function(){
		var me = this;
		var isPass = me.ideaApproval.isPass;
		if('no'== isPass){
			me.btnConfirm();
		}else{
			me.formulateSubmitMainPanel = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel');
			var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;//审批人页面
			me.subWin_assess = Ext.create('FHD.ux.Window', {
				title:'选择审批人',
	   		 	height: 200,
	    		width: 600,
	    		layout: {
	     	        type: 'fit'
	     	    },
	   			buttonAlign: 'center',
	   			closeAction: 'hide',
	    		items: [me.formulateSubmitMainPanel],
	   			fbar: [
	   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(formulateApproverEdit);}},
	   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit);}}
					  ]
			}).show();
		}
	},
	//提交审批
	btnConfirm:function(form){
		var me=this;
		var isPass = me.ideaApproval.isPass;
		var examineApproveIdea = me.ideaApproval.getValue();
		if('no' == isPass){
			var approverId = '';
		}else{
			var approverId = form.items.items[0].value;
			if(!approverId){
				FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}
		}
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认提交吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认
    				if(me.subWin_assess){
    					me.subWin_assess.hide();
						form.approver.clearValues();
    				}
    				me.body.mask("提交中...","x-mask-loading");
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/responseplan/workflow/solutionplanapprovebyleader.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId
						},
						callback : function(data) {
							me.body.unmask();
							if(me.winId){
								Ext.getCmp(me.winId).close();
							}
						}
					});
    			}
    		}
    	});
	},
	//关闭提交窗口
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
	
	reloadData:function(){
		var me=this;
		me.previewGrid.initParam({
			executionId : me.executionId,
			businessId : me.businessId
		});
		me.previewGrid.reloadData();
	},
	
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	initComponent : function() {
		var me=this;
		me.bbar={
			items: [
				'->',{
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
    					me.submitWindow();
    				}
				}
    		]
		};
		//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
		me.ideaApprovalSet = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				title:'审批意见',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.ideaApproval]
	  	});
		me.flowtaskbar=Ext.widget('panel',{
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items: Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.应对计划主管审批',status:'done'},
					    		{index: 3, context:'3.应对计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.方案制定',status:'done'},
					    		{index: 6, context:'6.方案审批',status:'done'},
					    		{index: 7, context:'7.单位主管审批',status:'done'},
					    		{index: 8, context:'8.单位领导审批',status:'current'},
					    		{index: 9, context:'9.业务分管副总审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	})
        });
		
        me.previewGrid = Ext.create('FHD.view.response.responsplanemore.SolutionPreviewGrid',{
        	pagable : false,
			searchable : false,
			checked: false
        });
        
        me.previewGridSet = Ext.create('Ext.form.FieldSet',{
				title:'应对列表',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.previewGrid]
	  	});
		Ext.applyIf(me, {
			autoScroll:true,
			items:[me.flowtaskbar,me.previewGridSet,me.ideaApprovalSet],
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
	}
	
});