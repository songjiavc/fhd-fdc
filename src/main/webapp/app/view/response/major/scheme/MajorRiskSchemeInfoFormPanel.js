Ext.define('FHD.view.response.major.scheme.MajorRiskSchemeInfoFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskschemeinfoformpanel',
    defaults: {
        columnWidth : 1/3,
        margin: '7 30 3 30',
        labelWidth: 95
    },
    layout: {
	        type: 'column'
	    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.description = {xtype:'displayfield', fieldLabel : '风险简述', name:'description'};
        me.flow = {xtype:'displayfield', fieldLabel : '风险分析', name:'analysis'};
        me.reason = {xtype:'displayfield', fieldLabel : '管理目标', name:'target'};
        me.strategy = {xtype:'displayfield', fieldLabel : '应对策略', name:'strategy'};
        me.startTime = {xtype:'displayfield', fieldLabel : '实施时间', name:'startTime'};
        me.finishTime = {xtype:'displayfield', fieldLabel : '结束时间', name:'finishTime'};
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.description,me.flow,me.reason, me.strategy,me.startTime,me.finishTime]
        });
        me.callParent(arguments);
        me.form.load({
	        url: __ctxPath + '/majorResponse/loadSchemefById',
	        params:{
	        	schemeObjectId:me.schemeObjectId,
	    		executionObjectId:me.executionObjectId,
	    		schemeType:me.schemeType,
	    		empType:me.empType
	    		},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
      
    }
});