Ext.define('FHD.view.response.major.scheme.MajorRiskRiskItemsInfoFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskriskitemsinfoformpanel',
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
        me.description = {xtype:'displayfield', fieldLabel : '风险事项', name:'description'};
        me.flow = {xtype:'displayfield', fieldLabel : '相关流程', name:'flow'};
        me.reason = {xtype:'displayfield', fieldLabel : '产生动因', name:'reason'};
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.description,me.flow,me.reason]
        });
        me.callParent(arguments);
        me.form.load({
	        url: __ctxPath + '/majorResponse/loadItemById',
	        params:{
	        	planId: me.businessId,
	    		schemeObjectId:me.schemeObjectId,
	    		executionObjectId:me.executionObjectId,
	    		schemeType:me.schemeType,
	    		empType:me.empType,
	    		itemId:me.itemId
	        	},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
      
    }
});