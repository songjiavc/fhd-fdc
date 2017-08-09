Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanMenuPanel', {
    extend: 'FHD.ux.MenuPanel',
    alias: 'widget.constructplanmenupanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	autoScroll:true
        });
        me.constructPlanDashboard = {
	        text: '驾驶舱',
	        iconCls:'icon-btn-home',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				FHD.ajax({
					url : __ctxPath+ '/icm/icsystem/findconstructplanchartxmlbycomanyid.f',
					
					async:false,
				    params : {
				    	companyId: __user.companyId
					},
					callback : function(data) {
						if(data){
							var constructplancenterpanel = me.up('panel').constructplancenterpanel;
							if(FusionCharts("constructpaln_finish_rate-chart") != undefined){
					 		   	FusionCharts("constructpaln_finish_rate-chart").dispose();
					     	}
							constructplancenterpanel.removeAll(true);
							constructplancenterpanel.add(Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanDashboard',{
								finishRateXml: data.finishRateXml,
								finishRate: data.finishRate,
								processXml:data.processXml,
								diagnosisXml:data.diagnosisXml
							}));
						}
					}
				});
				
			
			}
	    };
        me.constructPlanEditPanel = {
	        text: '建设计划',
	        iconCls:'icon-btn-assessPlan',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var constructplancenterpanel = me.up('panel').constructplancenterpanel;
				constructplancenterpanel.removeAll(true);
				var constructplaneditpanel = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanEditPanel');
				constructplancenterpanel.add(constructplaneditpanel);
				constructplaneditpanel.reloadData();
			}
	    };
        me.constructPlanTestReportList = {
	        text: '内控手册',
	        iconCls:'icon-btn-execute',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var constructplancenterpanel = me.up('panel').constructplancenterpanel;
				constructplancenterpanel.removeAll(true);
				var constructplantestreportlist = Ext.create('FHD.view.comm.report.icsystem.ConstructPlanTestReportList');
//					constructplantestreportlist.reloadData();
				constructplancenterpanel.add(constructplantestreportlist);
			}
	    };
        me.callParent(arguments);
        
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_ENV_ICSYSTEM_DASHBOARD')){
        	me.add(me.constructPlanDashboard);
        }
        //建设计划
        if($ifAllGranted('ROLE_ALL_ENV_ICSYSTEM_PLAN')){
        	me.add(me.constructPlanEditPanel);
        }
        //内控手册
        if($ifAllGranted('ROLE_ALL_ENV_ICSYSTEM_MANUAL')){
        	me.add(me.constructPlanTestReportList);
        }
    }
});