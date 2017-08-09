Ext.define('FHD.view.response.bpm.MakeSolutionPlanFormForBpmApprove', {
	extend:'Ext.panel.Panel',
	aligs:'widget.makesolutionplanformforbpmapprove',
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
                if('responseMore'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.方案制定',status:'done'},
					    		{index: 6, context:'6.方案审批',status:'current'},
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
					    		{index: 4, context:'4.方案制定',status:'done'},
					    		{index: 5, context:'5.方案审批',status:'current'},
					    		{index: 6, context:'6.方案执行',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//提交方法
	submitWindow: function(){
		var me = this;
		if('responseMore'==me.description){//多节点辨识
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
		}else{
			me.btnConfirm();
		}
	},
	//提交审批
	btnConfirm:function(form){
		var me=this;
		if('responseMore'==me.description){
			var approverId = form.items.items[0].value;
			if(!approverId){
				FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}
		}else{
			var approverId = '';
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
						url : __ctxPath+ '/responseplan/workflow/solutionplanbpmapprove.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass: 'yes',
					    	examineApproveIdea: '同意',
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
	},
	
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	initComponent : function() {
		var me=this;
		me.getItems();
		me.bbar={
			items: [
				'->',{
       				iconCls : 'icon-operator-submit',
		    		text: '提交',
    				handler: function () {
    					me.submitWindow();
    				}
				},{
       				iconCls : 'icon-operator-submit',
		    		text: '取消',
    				handler: function () {
    					if(me.winId){
    						Ext.getCmp(me.winId).close();
    					}
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
			maxHeight:200,
            split: true,
            collapsed:true,
            collapsible: true,
            border: false,
        	items: me.flowtaskbarItem
        });
		
        me.previewGrid = Ext.create('FHD.view.response.responsplanemore.SolutionPreviewGrid',{
        	flex:1,
        	pagable : false,
			searchable : false,
			checked: false
        });
        me.previewGrid.initParam({
			executionId : me.executionId,
			businessId : me.businessId
		});
		me.previewGrid.reloadData();
		
        me.previewGridSet = Ext.create('Ext.form.FieldSet',{
				title:'应对列表',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.previewGrid]
	  	});
	  	
		Ext.applyIf(me, {
			border: false,
			layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
			items:[me.flowtaskbar,me.previewGrid],
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