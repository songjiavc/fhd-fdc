Ext.define('FHD.view.response.major.scheme.result.ResultDownFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.resultdownformpanel',
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet = {
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
            layout: {
     	        type: 'column'
     	    },
     	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
						{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
						{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
						{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'}]
        };
        me.deptGrid = Ext.create('FHD.view.response.major.scheme.result.ResultApproveGridPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId
        });
        me.deptGrid.loadData(me.businessId);
	    
        me.fieldSet1 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'应对范围',
				  //minHeight: 250,
				  collapsible: true,
				  margin: '5 5 0 5',
				  items:[me.deptGrid]
	  	});
        me.downgrid = Ext.create('FHD.view.response.major.scheme.result.ResultSchemeListGridPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	schemeType :me.schemeType,
	  		empType: me.empType,
        });
        me.downgrid.loadData(me.businessId);
	    
        me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'方案列表',
				  //minHeight: 250,
				  collapsible: true,
				  margin: '5 5 0 5',
				  items:[me.downgrid]
	  	});
      /*  //重大风险信息
        me.majorRiskInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId
        });
        me.fieldSet1 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				title:'重大风险信息',
				collapsible : true,
				collapsed:true,
				margin: '5 5 0 5',
				items:[me.majorRiskInfoForm]
	  	});*/
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet,me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });

        
        me.callParent(arguments);
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