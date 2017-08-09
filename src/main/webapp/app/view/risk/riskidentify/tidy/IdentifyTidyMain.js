Ext.define('FHD.view.risk.riskidentify.tidy.IdentifyTidyMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.identifyTidyMain',
    requires: [],
    
    reloadData : function(){
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
                if('complex'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
			    		jsonArray:[
				    		{index: 1, context:'1.计划制定',status:'done'},
				    		{index: 2, context:'2.计划主管审批',status:'done'},
				    		{index: 3, context:'3.计划领导审批',status:'done'},
				    		{index: 4, context:'4.任务分配',status:'done'},
				    		{index: 5, context:'5.风险辨识',status:'done'},
				    		{index: 6, context:'6.辨识汇总',status:'done'},
				    		{index: 7, context:'7.单位主管审批',status:'done'},
				    		{index: 8, context:'8.单位领导审批',status:'done'},
				    		{index: 9, context:'9.业务分管副总审批',status:'done'},
				    		{index: 10, context:'10.结果整理',status:'current'},
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
				    		{index: 5, context:'5.辨识汇总',status:'done'},
				    		{index: 6, context:'6.结果整理',status:'current'}
				    	],
				    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
    //提交方法
	submitWindow: function(form){
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
	//确认提交
	btnConfirm:function(form){
		var me = this;
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
    			if(me.subWin_assess){
    				me.subWin_assess.hide();
					form.approver.clearValues();
    			}
    			me.body.mask("提交中...","x-mask-loading");
				FHD.ajax({//ajax调用
					url : __ctxPath+ '/access/riskidentify/submitidentifytidy.f',
				    params : {
				    	executionId:me.executionId,
				    	approverId: approverId,
				    	description: me.description,
				    	businessId: me.businessId
					},
					callback : function(data) {
						me.body.unmask();
						if(me.winId){
							Ext.getCmp(me.winId).close();
						}
					}
				});
    		}
    	});
	},
	//关闭选择审批人窗口
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
    // 初始化方法
    initComponent: function() {
    	Ext.Ajax.timeout = 1000000;
        var me = this;
        me.getItems();
        me.bbar = [
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					//提交工作流
					me.submitWindow();
	            }
			}
		];
        
        me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
    	
        Ext.apply(me, {
    		layout: {
    			align: 'stretch',
    			type: 'vbox'
    	    },
        	border:false,
        	items: [me.flowtaskbar],
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
    
    FHD.ajax({
        url: __ctxPath + '/assess/riskTidy/riskRbsOrRe.f',
        params : {
        	assessPlanId: me.businessId
        },
        callback: function (data) {
            if (data && data.success) {
            	var riskRbsOrReListMap = {ids:data.riskRbsOrReListMap};
            	var ids = {ids:data.ids};
            	me.riskTidyTab = Ext.create('FHD.view.risk.riskidentify.tidy.IdentifyTidyGrid',{riskTidyMan : me,schm:me.schm,operType : 'common'});
				me.riskTidyTab.store.proxy.extraParams.assessPlanId = me.businessId;
				me.assessTree = Ext.create("FHD.view.risk.riskidentify.tidy.IdentifyTidyTree",{extraRiskParams : riskRbsOrReListMap, region:'west',
			    		extraParams : ids, businessId : me.businessId, riskTidyMan : me,
			    		assessPlanId : me.businessId, collapseds : false});
	    		me.tidyPanel = Ext.create('Ext.panel.Panel',{
		    		flex: 1,
					businessId: me.businessId,
					border: false,
					layout: 'border',
					margin : '0 0 0 0',
					items: [me.assessTree,me.riskTidyTab]
				});
				me.add(me.tidyPanel);
            }
        }
    });
  }
});