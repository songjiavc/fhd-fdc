/*
 * 方案制定环节总panel
 */
Ext.define('FHD.view.response.major.scheme.MajorRiskSchemeDownFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorschemedownformpanel',
    schemeObjectId:"",
    executionObjectId:"",
    //保存方案信息
    saveSchemeForm:function(){
    	var me = this;
    	//设置方案类型
    	me.schemeAddForm.schemeType.setValue(me.schemeType);
    	var schemeForm = me.schemeAddForm.getForm();
    	var ret = {};
    	if(schemeForm.isValid()){
    		FHD.submit({
    			form:schemeForm,
    			params:{
    				//计划id
        			planId:me.businessId,
        			//重大风险id
        			riskId:me.majorRiskInfoForm.majorRiskId.getValue(),
        			deptId:me.majorRiskInfoForm.deptId.getValue(),
        			empType:me.empType, //普通员工制定
    			},
    			url: __ctxPath + '/majorResponse/saveScheme',
    			callback:function(data){
    				var result = data.data;
    				var isOK = data.success;
    				if(isOK){
    					ret.result = result;
    					ret.isValid = true;//保存成功了
    				}else{
    					ret.isValid = false;//保存失败了
    				}
    				me.riskItemsGrid.initParam(ret.result.schemeObjectId,ret.result.executionObjectId,me.businessId,me.schemeType,me.empType);
    			}
    		});
    	}else{
    		ret.isValid = false;
    	}
    	return ret;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        //计划基础信息
        me.fieldSet1 = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed : true,//初始化收缩
            margin: '5 5 0 5',
            defaults: {
                    columnWidth : 1 / 2,
                    margin: '7 30 3 30',
                    labelWidth: 95
                },
            layout: {type: 'column'},
     	    items : [{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
					{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
					{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
					{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'}]
        	};
        //重大风险信息
        me.majorRiskInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId:me.executionId
        });
        me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				title:'重大风险信息',
				collapsible : true,
				collapsed:true,
				margin: '5 5 0 5',
				items:[me.majorRiskInfoForm]
	  	});
        //方案制定表单
        me.schemeAddForm = Ext.create('FHD.view.response.major.scheme.MajorRiskSchemeAddForm',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId: me.executionId,
        	schemeType :me.schemeType,
	  		empType: me.empType,
        });
        //风险事项列表
        me.riskItemsGrid = Ext.create('FHD.view.response.major.scheme.MajorRiskItemsGrid',{
        	businessId: me.businessId,
        	executionId:me.executionId,
        	schemeType :me.schemeType,
	  		empType: me.empType,
        });
        
        me.saveSchemeBtn = { 
        		xtype: 'button',
        		minWidth:60,
        		maxWidth:80,
        		text: '保存方案信息',
        		margin: '20 5 5 450',
        		handler:function(){
        			 me.riskItemsGrid.setVisible(true);
        			 me.riskItemsGrid.initData();
        			 me.saveSchemeForm();
        			}
        };
        
        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
			layout: 'column',
     	   	defaults: {
               columnWidth : 1 ,
               labelWidth: 95
     	   	},
			title:'方案信息',
			collapsible : true,
			collapsed:false,
			margin: '5 5 5 5',
			items:[me.schemeAddForm,me.saveSchemeBtn,me.riskItemsGrid]
        });
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });
        me.callParent(arguments);
        me.riskItemsGrid.setVisible(false);
        me.form.load({
    	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
    	        params:{businessId: me.businessId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
       
    }
});