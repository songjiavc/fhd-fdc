/**
 * 整改优化左侧树
 * 
 */
Ext.define('FHD.view.icm.rectify.RectifyImproveLeftPanel', {
    extend: 'FHD.ux.MenuPanel',
    alias: 'widget.rectifyimproveleftpanel',
    
    requires: [
    	'FHD.view.icm.rectify.RectifyImproveDashboard',
    	'FHD.view.icm.rectify.RectifyImproveMainPanel',
    	'FHD.view.icm.defect.DefectList'
    ],
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	autoScroll:true
        });
        me.rectifyimprovedashboard = {
	        text: '驾驶舱',
	        iconCls:'icon-btn-home',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				FHD.ajax({
					url : __ctxPath+ '/icm/rectify/findImproveChartXmlByComanyId.f',
					async:false,
				    params : {
				    	companyId: __user.companyId
					},
					callback : function(data) {
						if(data){
							var rectifyImproveCenterPanel = me.up('panel').rectifyImproveCenterPanel;
							if(FusionCharts("improve_finish_rate-chart") != undefined){
					 		   	FusionCharts("improve_finish_rate-chart").dispose();
					     	}
							rectifyImproveCenterPanel.removeAll(true);
							rectifyImproveCenterPanel.add(Ext.widget('rectifyimprovedashboard',{
								finishRateXml: data.finishRateXml,
								finishRate: data.finishRate,
								defectLevelXml:data.defectLevelXml,
								orgDefectXml:data.orgDefectXml
							}));
						}
					}
				});
			}
	    };
        me.rectifyimprovemainpanel = {
	        text: '整改计划',
	        iconCls:'icon-btn-assessPlan',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var rectifyImproveCenterPanel = me.up('panel').rectifyImproveCenterPanel;
				rectifyImproveCenterPanel.removeAll(true);
				rectifyImproveCenterPanel.add(Ext.widget('rectifyimprovemainpanel'));
			}
	    };
        me.defectlist = {
	        text: '缺陷管理',
	        iconCls:'icon-btn-defect',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){
				var rectifyImproveCenterPanel = me.up('panel').rectifyImproveCenterPanel;
				rectifyImproveCenterPanel.removeAll(true);
				rectifyImproveCenterPanel.add(Ext.widget('defectlist'));
			}
	    }
        
        me.callParent(arguments);
        
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_CONTROL_ICRECITIFY_DASHBOARD')){
        	me.add(me.rectifyimprovedashboard);
        }
        //整改计划
        if($ifAllGranted('ROLE_ALL_CONTROL_ICRECITIFY_PLAN')){
        	me.add(me.rectifyimprovemainpanel);
        }
        //缺陷管理
        if($ifAllGranted('ROLE_ALL_CONTROL_ICRECITIFY_BUG')){
        	me.add(me.defectlist);
        }
    }
});