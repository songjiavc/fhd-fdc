/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.measureedit.RiskMeasureBeforePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskmeasurebeforepanel',
    requires: [
    	'FHD.view.risk.riskedit.Quantification',
    	'FHD.view.risk.measureedit.MeasurePrognosis',
    	'FHD.view.risk.measureedit.RiskClassBaseMainFieldSet'
    ],
    border : 0,
    autoScroll : true,
	autoWidth: true,
	autoHeight: true,
	type : '',
	layout: {
		type: 'vbox',
		align : 'stretch'
	},
	initComponent: function() {
		var me = this;
        me.riskclassbasemainfieldset = Ext.widget('riskclassbasemainfieldset',{btnName : '风险预防方案',type:me.type});
        Ext.apply(me, {
            items: [
               me.riskclassbasemainfieldset
               ]
        });

        me.callParent(arguments);
    },
    
    save : function(){
    	var me = this;
    	var id = me.up('riskMainPanel').riskTree.face.nodeId;
    	if(id == ''){
    		alert('请先进行风险选择');
    		return false;
    	}
    	var form = me.getForm().load();
    	FHD.submit({
    		form : form,
    		url : 'chf/risk/measure/saveMeasure',
    		params : {
    			riskid : id,
    			type : me.type
    		},
    		callback : function(data){
    		
    		}
    	});
    },
    
    loadForm : function(){
		var me = this;
		var id = me.up('riskMainPanel').riskTree.face.nodeId;
		if(id != '') {
    		FHD.ajax({
    	        url:'chf/risk/measure/beforemeasureloadform.f',
    	        params:{
    	        	riskid : id,
    	        	type : me.type
    	        },
    	        callback:function(data){
    	        	me.riskclassbasemainfieldset.modifyLoad(data);
    	        }
    	    });
    	}
	}
	
});