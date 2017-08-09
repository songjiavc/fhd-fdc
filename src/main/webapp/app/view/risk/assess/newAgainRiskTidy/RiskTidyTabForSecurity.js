/**
 */
Ext.define('FHD.view.risk.assess.newAgainRiskTidy.RiskTidyTabForSecurity', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.risktidytabforsecurity',
    requires: [
       	'FHD.view.risk.assess.newAgainRiskTidy.RiskAssessGridForSecurity',
    	'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyGridForSecurity'
    ],
    //提交方法
	submitWindow: function(){
		var me = this;
		var isPass = me.riskAssessApproveDown.ideaApproval.isPass;
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
		var isPass = me.riskAssessApproveDown.ideaApproval.isPass;
		var examineApproveIdea = me.riskAssessApproveDown.ideaApproval.getValue();
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
    				me.riskTidyMan.body.mask("提交中...","x-mask-loading");
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/assess/riskTidy/submitRiskTidyForSecurity.f',
					    params : {
					    	businessId:me.riskTidyMan.businessId,
					    	executionId:me.riskTidyMan.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId,
					    	approverKey : 'approveFore'
						},
						callback : function(data) {
							me.riskTidyMan.body.unmask();
							if(Ext.getCmp(me.riskTidyMan.winId) != null){
								Ext.getCmp(me.riskTidyMan.winId).close();
							}else{
								window.location.reload();
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
    
    //统计标签活动时，默认收起树panel
    listeners: {
	  	tabchange:function(tabPanel, newCard, oldCard, eOpts){
	  		var me = this;
    		newCard.store.load();
	  	}
    },
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//获取维度列表
    	Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
		    params: {
		    	assessPlanId : me.riskTidyMan.businessId
            },
		    async:  false,
		    success: function(response){
		        me.dimList = Ext.JSON.decode(response.responseText).templateRelaDimensionMapList;
	    }});
    	
    	//加载评估打分结果
    	me.riskAssessGrid = Ext.widget('riskassessgridforsecurity',{riskTidyMan : me.riskTidyMan, title : '评估打分',dimList :　me.dimList });
        //加载整理风险列表
        me.riskTidyGrid = Ext.widget('risktidygridforsecurity',{riskTidyMan : me.riskTidyMan, title : '风险辨识',dimList :　me.dimList});
    	var bbar =[
			'->',{
				id : 'riskTidyButton1',
				text : '提交',
				iconCls : 'icon-operator-submit',
				handler : function() {
					me.riskAssessApproveDown = Ext.create('FHD.view.risk.riskidentify.approve.RiskIdentifyApproveDown',{
		    	  		region: 'center',
		    	  		businessId : me.riskTidyMan.businessId,
		    	  		executionId: me.riskTidyMan.executionId,
		    	  		winId : me.riskTidyMan.winId,
		    	  		margin : '0 0 0 0'
		    	  	});
					me.submitWindow();
				}
			}
        ];
    	Ext.apply(me,{
    		deferredRender: false,
            region:'center',
            plain: true,
            border : false,
            bbar : bbar,
            items : [me.riskTidyGrid,me.riskAssessGrid]
        });
        me.callParent(arguments);
        
    }
});