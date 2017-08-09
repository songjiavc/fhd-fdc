/**
 * 
 * 整改优化中心panel
 * 
 */
Ext.define('FHD.view.icm.rectify.RectifyImproveCenterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.rectifyimprovecenterpanel',
	
    layout:'fit',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
       	FHD.ajax({
			url : __ctxPath+ '/icm/rectify/findImproveChartXmlByComanyId.f',
			async:false,
		    params : {
		    	companyId: __user.companyId
			},
			callback : function(data) {
				if(data){
					if(FusionCharts("improve_finish_rate-chart") != undefined){
			 		   	FusionCharts("improve_finish_rate-chart").dispose();
			     	}
					me.rectifyimprovedashboard = Ext.widget('rectifyimprovedashboard',{
						finishRateXml: data.finishRateXml,
						finishRate: data.finishRate,
						defectLevelXml:data.defectLevelXml,
						orgDefectXml:data.orgDefectXml
					})
				}
			}
		});
        me.callParent(arguments);
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_CONTROL_ICRECITIFY_DASHBOARD')){
        	me.add(me.rectifyimprovedashboard);
        }
    }
});