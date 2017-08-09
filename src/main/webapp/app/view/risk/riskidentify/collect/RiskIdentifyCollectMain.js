Ext.define('FHD.view.risk.riskidentify.collect.RiskIdentifyCollectMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskIdentifyCollectMain',
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
                if('complex'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
			    		jsonArray:[
				    		{index: 1, context:'1.计划制定',status:'done'},
				    		{index: 2, context:'2.计划主管审批',status:'done'},
				    		{index: 3, context:'3.计划领导审批',status:'done'},
				    		{index: 4, context:'4.任务分配',status:'done'},
				    		{index: 5, context:'5.风险辨识',status:'done'},
				    		{index: 6, context:'6.辨识汇总',status:'current'},
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
				    		{index: 3, context:'3.任务分配',status:'done'},
				    		{index: 4, context:'4.风险辨识',status:'done'},
				    		{index: 5, context:'5.辨识汇总',status:'current'},
				    		{index: 6, context:'6.结果整理',status:'undo'}
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
		if('complex'==me.description){//多节点辨识
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
		if('complex'==me.description){//多节点辨识
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
						url : __ctxPath+ '/access/riskidentify/submitidentifycollect.f',
					    params : {
					    	executionId:me.executionId,
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
    
  	reloadData : function(){
  	
  	},  
    // 初始化方法
    initComponent: function() {
    	Ext.Ajax.timeout = 1000000;
    	var me = this;
    	me.getItems();
        me.assessApproveGrid = null;
        me.flowtaskbar = Ext.widget('panel',{
			//region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
    	
    	me.assessApproveGrid = Ext.create('FHD.view.risk.riskidentify.collect.RiskIdentifyCollectGrid',{
    		flex: 1,
    		businessId: me.businessId,
    		executionId: me.executionId
    	});
    	me.assessApproveGrid.reloadData(me.businessId,me.executionId);
        
        Ext.apply(me, {
        	border:false,
        	margin : '0 0 0 0',
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items: [me.flowtaskbar,me.assessApproveGrid],
            bbar : {
				items : [
					'->',
					{
		            	iconCls : 'icon-operator-submit',
		    			text: '提交',
		    				handler:function(){
		    					me.submitWindow();
		    				}
		    			},{
		    				iconCls : 'icon-operator-submit',
		    				text: '取消',
		    				handler:function(){
		    					if(me.winId){
		    						Ext.getCmp(me.winId).close();
		    					}
		    				}
		    		}
            ]
            },
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