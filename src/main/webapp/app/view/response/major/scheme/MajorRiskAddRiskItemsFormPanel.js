Ext.define('FHD.view.response.major.scheme.MajorRiskAddRiskItemsFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskaddriskitemsformpanel',
    layout: {
        type: 'form'
    },
    //保存风险事项
    saveItem:function(){
    	var me = this;
    	//设置方案类型
    	me.itemAddForm.itemType.setValue(me.schemeType);
    	var itemForm = me.itemAddForm.getForm();
    	var ret = {};
    	if(itemForm.isValid()){
    		FHD.submit({
    			form:itemForm,
    			params:{
    				//计划id
        			planId:me.businessId,
        			//重大风险id
        			riskId:me.majorRiskInfoForm.majorRiskId.getValue(),
        			deptId:me.majorRiskInfoForm.deptId.getValue(),
        			empType:me.empType, //普通员工制定
        			schemeObjectId:me.schemeObjectId,
            		executionObjectId:me.executionObjectId,
    			},
    			url: __ctxPath + '/majorResponse/saveItem',
    			callback:function(data){
    				var result = data.data;
    				var isOK = data.success;
    				if(isOK){
    					ret.result = result;
    					ret.isValid = true;//保存成功了
    				}else{
    					ret.isValid = false;//保存失败了
    				}
    				me.openCounterWin(ret,me.businessId,me.schemeType,me.empType,me.schemeObjectId,me.executionObjectId);
    				//me.riskcounterGrid.initParam(result.itemId,me.schemeObjectId,me.executionObjectId,me.businessId,me.schemeType,me.empType)
    			}
    		});
    	}else{
    		ret.isValid = false;
    	}
    	return ret;
	},
	openCounterWin:function(param,businessId,schemeType,empType,schemeObjectId,executionObjectId){
		var me = this;
		var itemId = param.result.itemId;
		me.win = Ext.create('FHD.view.response.major.scheme.MajorRiskAddCounterWindow',{//添加风险事项弹窗
    		modal: true,
    		businessId: me.businessId,
    		schemeObjectId:schemeObjectId,
    		executionObjectId:executionObjectId,
    		schemeType:schemeType,
    		empType:empType,
    		itemId:itemId,
		   	onSubmit:function(win){
		   		 //me.win.addCounterForm.saveCounter(itemId);
		   	 me.riskcounterGrid.loadData(itemId);
    		}
		}).show();
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.majorRiskInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId:me.executionId
        });
	    
        me.fieldSet1 = Ext.create('Ext.form.FieldSet',{
				layout:"column",
				title:'重大风险信息',
				collapsible : true,
				collapsed:true,
				margin: '0 0 0 0',
				items:[me.majorRiskInfoForm]
	  	});
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
		
        me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
			layout: 'column',
			title:'方案信息',
			collapsible : true,
			collapsed:true,
			items:[me.schemeInfoForm]
        });
        me.riskcounterGrid = Ext.create('FHD.view.response.major.scheme.MajorRiskCounterGrid',{
        	businessId: me.businessId,
    		schemeObjectId:me.schemeObjectId,
    		executionObjectId:me.executionObjectId,
    		schemeType:me.schemeType,
    		empType:me.empType
        });
        me.itemAddForm = Ext.create('FHD.view.response.major.scheme.MajorRiskItemsAddForm',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
    		schemeObjectId:me.schemeObjectId,
    		executionObjectId:me.executionObjectId,
    		schemeType:me.schemeType,
    		empType:me.empType
        });
 
        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
        	layout: 'column',
     	   	defaults: {
               columnWidth : 1 ,
               labelWidth: 95
     	   	},
			title:'风险事项',
			collapsible : true,
			collapsed:false,
			margin: '0 0 0 0',
			items:[me.itemAddForm,me.riskcounterGrid]
        });
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });
        me.callParent(arguments);
    }
});