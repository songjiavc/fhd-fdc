/**
 * 
 * 工作计划左侧功能树
 * 
 * @author 胡迪新
 */
Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanCenterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.constructplancenterpanel',
	
    layout:'fit',
    // 初始化方法
    initComponent: function() {
        var me = this;
        	FHD.ajax({
			url : __ctxPath+ '/icm/icsystem/findconstructplanchartxmlbycomanyid.f',
			async:false,
		    params : {
		    	companyId: __user.companyId
			},
			callback : function(data) {
				if(data){
					if(FusionCharts("improve_finish_rate-chart") != undefined){
			 		   	FusionCharts("improve_finish_rate-chart").dispose();
			     	}
					me.constructplandashboard = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanDashboard',{
						finishRateXml: data.finishRateXml,
						finishRate: data.finishRate
					})
				}
			}
		});
        me.callParent(arguments);
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_ENV_ICSYSTEM_DASHBOARD')){
        	me.add(me.constructplandashboard);
        }
    }
});