Ext.define('FHD.view.response.major.scheme.MajorRiskAddCounterFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskaddcounterformpanel',
    saveCounter:function(itemId){
		var me = this;
		var form = me.couterAddForm.getForm();
		var executionEmpId = null;
		executionEmpId = me.couterAddForm.empInput.getValue();
		me.couterAddForm.counterType.setValue(me.schemeType);
		var ret = {};
		if(form.isValid()){
    		FHD.submit({
    			form:form,
    			params:{
    				itemId:itemId,
    				executionEmpId:executionEmpId,
    			},
    			url: __ctxPath + '/majorResponse/saveCounter',
    			callback:function(data){
    				var result = data.data;
    				var isOK = data.success;
    				if(isOK){
    					ret.result = result;
    					ret.isValid = true;//保存成功了
    				}else{
    					ret.isValid = false;//保存失败了
    				}
    			}
    		});
    		
    	}else{
    		return false;
    	}
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.schemeInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskSchemeInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
    		schemeObjectId:me.schemeObjectId,
    		executionObjectId:me.executionObjectId,
    		schemeType:me.schemeType,
    		empType:me.empType
        });
        me.fieldSet1 = Ext.create('Ext.form.FieldSet',{
			layout: 'column',
			title:'方案信息',
			collapsible : true,
			collapsed:true,
			items:[me.schemeInfoForm]
        });
        
        me.itemInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskRiskItemsInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
    		schemeObjectId:me.schemeObjectId,
    		executionObjectId:me.executionObjectId,
    		schemeType:me.schemeType,
    		empType:me.empType,
    		itemId:me.itemId
        });
        me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
			layout: 'column',
			title:'风险事项',
			collapsible : true,
			collapsed:true,
			items:[me.itemInfoForm]
        });
        
        me.couterAddForm = Ext.create("FHD.view.response.major.scheme.MajorRiskCounterAddForm",{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
    		schemeObjectId:me.schemeObjectId,
    		executionObjectId:me.executionObjectId,
    		schemeType:me.schemeType,
    		empType:me.empType,
    		itemId:me.itemId
        });
		me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
			layout: 'column',
			title:'措施信息',
			collapsible : true,
			collapsed:false,
			items:[me.couterAddForm]
		});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });
        me.callParent(arguments);
       
    }
});