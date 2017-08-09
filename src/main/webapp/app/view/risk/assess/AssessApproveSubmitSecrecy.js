/**
 * 
 * 风险整理上下面板
 */

Ext.define('FHD.view.risk.assess.AssessApproveSubmitSecrecy', {
    extend: 'Ext.form.Panel',
    alias: 'widget.assessApproveSubmitSecrecy',
    
    requires: [
               'FHD.view.risk.assess.AssessApproveGridSecrecy',
               'FHD.ux.icm.common.FlowTaskBar',
               'FHD.view.risk.assess.utils.GridCells'
              ],
    
	reloadData : function(){
	  	
	},  
    
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
    				me.body.mask("提交中...","x-mask-loading");
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/access/approval/submitriskidentifyapprovalbysupervisor.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId,
					    	approverKey : 'approveOne'
						},
						callback : function(data) {
							me.body.unmask();
							if(me.winId != null){
								Ext.getCmp(me.winId).close();
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
	  
    // 初始化方法
    initComponent: function() {
    	Ext.Ajax.timeout = 1000000;
    	var me = this;
        me.assessApproveGrid = null;
        
        Ext.apply(me, {
        	border : false,
        	region : 'center',
        	margin : '0 0 0 0',
//        	layout: {
//    	        type: 'border',
//    	        padding: '0 0 5	0'
//    	    },
            bbar : {
            	id : 'assessApproveIds', 	
				items : [
					'->',
					{
		            	id : 'assessApproveSubmitIdButton1',
		            	iconCls : 'icon-operator-submit',
		    			text: '提交',
		    				handler:function(){
		    					if(me.jbmpType == 'complex'){
		    						me.submitWindow();
		    					}else if(me.jbmpType == 'simple'){
		    						Ext.MessageBox.show({
			    			    		title : '提示',
			    			    		width : 260,
			    			    		msg : '确认提交吗？',
			    			    		buttons : Ext.MessageBox.YESNO,
			    			    		icon : Ext.MessageBox.QUESTION,
			    			    		fn : function(btn) {
			    			    			if (btn == 'yes') {//确认删除
			    			    				me.body.mask("提交中...","x-mask-loading");
			    		    					FHD.ajax({
			    		    			            url: __ctxPath + '/assess/quaassess/approvalColl.f',
			    		    			            params:{
			    		    			            	assessPlanId: me.businessId,
			    		    			            	executionId: me.executionId
			    		    			            },
			    		    			            callback: function (data) {
			    		    			            	if(me.winId){
			    		    			            		me.body.unmask();
			    					    					Ext.getCmp(me.winId).close();
			    					    				}else{
			    					    					me.body.unmask();
			    					    					window.location.reload();
			    					    				}
			    		    			            }
			    		    			        });
			    			    			}
			    			    		}
			    			    	});
		    					}
		    				}
		    			},{
		    				id : 'assessApproveSubmitIdButton2',
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
        
        Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f?executionId=' + me.executionId + "&type=jbpm",
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        var type = Ext.JSON.decode(text).type;
		        
            		me.jbmpType = 'simple';
            		me.flowtaskbar = Ext.widget('panel',{
            			border:false,
            			collapsible : true,
            			region : 'north',
            			collapsed:true,
            			items:Ext.widget('flowtaskbar',{
            	    		jsonArray:[
            		    		{index: 1, context:'1.计划制定',status:'done'},
        			    		{index: 2, context:'2.计划审批',status:'done'},
        			    		{index: 3, context:'3.任务分配',status:'done'},
        			    		{index: 4, context:'4.风险评估',status:'done'},
        			    		{index: 5, context:'5.任务审批',status:'current'},
        			    		{index: 6, context:'6.结果整理',status:'undo'}
            		    	],
            		    	margin : '5 5 5 5'
                	})});
		        
		        var array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.3});
		        });
		        
		        me.assessApproveGrid = Ext.widget('assessApproveGridSecrecy',{
		        	url: __ctxPath + '/assess/quaassess/findLeaderDept.f',
		        	assessApproveSubmit : me, array : array, businessId: me.businessId, executionId: me.executionId});
		        me.add(me.flowtaskbar);
		        me.add(me.assessApproveGrid);
		        
		        me.assessApproveGrid.store.proxy.extraParams.assessPlanId = me.businessId;
			    me.assessApproveGrid.store.proxy.extraParams.executionId = me.executionId;
			    me.assessApproveGrid.store.load();
		    }
		});
    }
});