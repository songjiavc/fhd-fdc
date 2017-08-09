Ext.define('FHD.view.response.responseplan.ResponsePlanMenuPanel', {
    extend: 'FHD.ux.MenuPanel',
    alias: 'widget.responseplanmenupanel',
    
    requires: [
    	'FHD.view.response.responseplan.ResponsePlanEditPanel',
    	'FHD.view.response.responseplan.bpm.SolutionApplyFormForBpm',
    	'FHD.view.response.responseplan.bpm.ResponsePlanApproveForBpm',
    	'FHD.view.response.responseplan.bpm.SolutionApproveFormForBpm',
    	'FHD.view.response.ExecutionForm',
    	'FHD.view.response.responseplan.bpm.SolutionBpmApprove'
//    	'FHD.view.risk.responseplandemo.ResponsePlanDashboard'
    ],
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	items:[{
		        text: '驾驶舱',
		        iconCls:'icon-btn-home',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					
				}
		    },{
		        text: '应对计划',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var responseplaneditpanel = Ext.widget('responseplaneditpanel');
					responseplancenterpanel.add(responseplaneditpanel);
//					responseplaneditpanel.reloadData();
				}
//		    },{
//		        text: '计划审批',
//		        iconCls:'icon-btn-assessPlan',
//		        scale: 'large',
//				iconAlign: 'top',
//				handler:function(){	
//					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
//					responseplancenterpanel.removeAll(true);
//					var responseplanapproveforbpm = Ext.create('FHD.view.response.responseplan.bpm.ResponsePlanApproveForBpm');
//					responseplancenterpanel.add(responseplanapproveforbpm);
////					responseplaneditpanel.reloadData();
//				}
		    },{
		        text: '任务分配',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var responseplanemployeeforeachbpm = Ext.create('FHD.view.response.responseplan.bpm.ResponsePlanEmployeeForeachBpm');
					responseplancenterpanel.add(responseplanemployeeforeachbpm);
//					responseplaneditpanel.reloadData();
				}
		    },{
		        text: '方案制定',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var solutionapplyformforbpm = Ext.create('FHD.view.response.responseplan.bpm.SolutionApplyFormForBpm');
					responseplancenterpanel.add(solutionapplyformforbpm);
//					responseplaneditpanel.reloadData();
				}
//		    },{
//		        text: '方案审批',
//		        iconCls:'icon-btn-assessPlan',
//		        scale: 'large',
//				iconAlign: 'top',
//				handler:function(){	
//					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
//					responseplancenterpanel.removeAll(true);
//					var solutionapproveformforbpm = Ext.create('FHD.view.response.responseplan.bpm.SolutionApproveFormForBpm');
//					responseplancenterpanel.add(solutionapproveformforbpm);
////					responseplaneditpanel.reloadData();
//				}
		    },{
		        text: '方案执行',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var executionformforbpm = Ext.create('FHD.view.response.new.bpm.ExecutionForm');
					responseplancenterpanel.add(executionformforbpm);
//					responseplaneditpanel.reloadData();
				}
		    },{
		        text: '风险应对库',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var executionformforbpm = Ext.create('FHD.view.response.SolutionList');
					responseplancenterpanel.add(executionformforbpm);
//					responseplaneditpanel.reloadData();
				}
		    },{
		        text: '应对预案',
		        iconCls:'icon-btn-assessPlan',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					var responseplancenterpanel = me.up('panel').responseplancenterpanel;
					responseplancenterpanel.removeAll(true);
					var preplantab = Ext.create('FHD.view.response.PreplanTab');
					responseplancenterpanel.add(preplantab);
//					responseplaneditpanel.reloadData();
				}
		    }
		 ]
        });

        me.callParent(arguments);
    }
});