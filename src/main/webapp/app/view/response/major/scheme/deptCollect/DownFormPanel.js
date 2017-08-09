Ext.define('FHD.view.response.major.scheme.deptCollect.DownFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.downformpanel',
   
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
        
        me.downgrid = Ext.create('FHD.view.response.major.scheme.deptCollect.SchemeListGridPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId: me.executionId,
        	schemeType :me.schemeType,
	  		empType: me.empType,
        });
        me.downgrid.loadData(me.businessId);
	    
        me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'范围',
				  //minHeight: 250,
				  collapsible: true,
				  margin: '5 5 0 5',
				  items:[me.downgrid]
	  	});
        //重大风险信息
        me.majorRiskInfoForm = Ext.create('FHD.view.response.major.scheme.MajorRiskInfoFormPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId:me.executionId
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
	  	});
        me.managerDowngrid = Ext.create('FHD.view.response.major.scheme.deptCollect.ManagerSchemeListGridPanel',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId: me.executionId,
        	schemeType :me.schemeType,
	  		empType: me.empType,
        });
	  	
        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
			layout:{
     	        type: 'column'
     	    },
			  title:'汇总结果',
			  //minHeight: 250,
			  collapsible: true,
			  margin: '5 5 0 5',
			  items:[me.managerDowngrid]
  	});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet,me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });

        
        me.callParent(arguments);
        me.fieldSet3.setVisible(false);
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