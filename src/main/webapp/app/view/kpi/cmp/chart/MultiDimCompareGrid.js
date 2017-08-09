Ext.define('FHD.view.kpi.cmp.chart.MultiDimCompareGrid', {
    extend: 'FHD.view.kpi.cmp.kpi.KpiGridPanel',

    title: FHD.locale.get('fhd.kpi.kpi.form.kpilist'),
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },
    
    initComponent: function() {
        var me = this;
        
        Ext.apply(me, {
        	url : __ctxPath + "/kpi/cmp/findcategoryrelakpi.f",
            extraParams:{
            	objectId : '',
            	year : FHD.data.yearId,
            	month : FHD.data.monthId,
            	quarter : FHD.data.quarterId,
            	week : FHD.data.weekId,
            	eType : FHD.data.eType,
            	isNewValue : FHD.data.isNewValue,
            	dataType: ''
            }
        });
        
        me.callParent(arguments);
    }
});