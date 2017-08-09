/**
 */
Ext.define('FHD.view.risk.assess.newAgainRiskTidy.RiskTidyTab', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.riskTidyTab',
    
    requires: [
               	'FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyGrid'
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
						url : __ctxPath+ '/access/approval/submitriskidentifyapprovalbysupervisor.f',
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
	  		///吉志强 为了不让前台报错 2017年4月27日18:10:18 鸡鸡鸡
//	  		var treePanel = me.up('newRiskTidyMan').assessTree;
//	  		if('riskTreeGrid'==newCard.id.split('-')[0]||'kpiTreeGrid'==newCard.id.split('-')[0]||
//	  				'orgTreeGrid'==newCard.id.split('-')[0]||'processTreeGrid'==newCard.id.split('-')[0]){
//	  			treePanel.collapse('left',true);
//	  		}else{
//	  			treePanel.expand(true);
//	  		}
	  	///吉志强 为了不让前台报错 2017年4月27日18:10:18 鸡鸡鸡
	  	}
    },
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.riskTidyGrid = Ext.widget('newRiskTidyGrid',{riskTidyMan : me.riskTidyMan, title : '风险列表'});
        me.riskTidyGrid.store.proxy.extraParams.assessPlanId = me.riskTidyMan.businessId;
       	/*
		me.riskTidyCard = Ext.widget('newRiskTidyCard',{title : '图表分析', riskTidyMan : me.riskTidyMan});
        
        me.riskCategoryPanel = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.treeGrid.RiskTreeGrid',{
  			riskTidyMan : me.riskTidyMan,
  			title: '分类统计'
  		});
        me.orgTreeGrid = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.treeGrid.OrgTreeGrid',{
  			riskTidyMan : me.riskTidyMan,
  			title: '组织统计'
  		});
        me.kpiprocessPanel = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.treeGrid.KpiTreeGrid',{
  			riskTidyMan : me.riskTidyMan,
  			title: '目标统计'
  		});
        me.processPanel = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.treeGrid.ProcessTreeGrid',{
  			riskTidyMan : me.riskTidyMan,
  			title: '流程统计'
  		});
  		*/
        if(me.riskTidyMan.nav){
        	var bbar =[
				'->',{
					id : 'riskTidyButton1',
					text : '提交',
					iconCls : 'icon-operator-submit',
					handler : function() {
						if(me.riskTidyMan.jbmpType == 'complex'){
							me.riskAssessApproveDown = Ext.create('FHD.view.risk.riskidentify.approve.RiskIdentifyApproveDown',{
				    	  		region: 'center',
				    	  		businessId : me.riskTidyMan.businessId,
				    	  		executionId: me.riskTidyMan.executionId,
				    	  		winId : me.riskTidyMan.winId,
				    	  		margin : '0 0 0 0'
				    	  	});
    						me.submitWindow();
    					}else if(me.riskTidyMan.jbmpType == 'simple'){
    						Ext.MessageBox.show({
    				    		title : '提示',
    				    		width : 260,
    				    		msg : '确认提交吗？',
    				    		buttons : Ext.MessageBox.YESNO,
    				    		icon : Ext.MessageBox.QUESTION,
    				    		fn : function(btn) {
    				    			if (btn == 'yes') {//确认删除
    				    				Ext.Ajax.timeout = 1000000;
    									me.riskTidyMan.body.mask("提交中...","x-mask-loading");
    									
    									FHD.ajax({
    							            url: __ctxPath + '/assess/riskTidy/submitRiskTidy.f',
    							            params: {
    							            	params : Ext.JSON.encode(me.riskDatas),
    							            	executionId : me.riskTidyMan.executionId,
    							            	assessPlanId : me.riskTidyMan.businessId
    							            },
    							            callback: function (data) {
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
    					}
					}
				}
	        ];
        	
        	Ext.apply(me,{
        		deferredRender: false,
                region:'center',
                plain: true,
                border : false,
                bbar : bbar
            });
        }else{
        	Ext.apply(me,{
        		deferredRender: false,
                region:'center',
                plain: true
            });
        }
    	
        me.callParent(arguments);
        //按权限显示tab
        FHD.ajax({
            url: __ctxPath + '/assess/risktidy/findsummarizingtab.f',
            callback: function (data) {
            	me.add(me.riskTidyGrid);
            	me.add(me.riskCategoryPanel);
                if(data.orgSummarizing){
                	me.add(me.orgTreeGrid);
                }
                if(data.strategySummarizing){
                	me.add(me.kpiprocessPanel);
                }
                if(data.processSummarizing){
                	me.add(me.processPanel);
                }
                me.add(me.riskTidyCard);
                me.setActiveTab(me.riskTidyGrid);
                me.getTabBar().insert(0, {xtype:'tbfill'});
            }
        });
        
    }
});