Ext.define('FHD.view.response.major.task.MajorRiskTaskDownFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskdownformpanel',
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet1 = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed : false,//初始化收缩
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
        
        me.selectEmpForm = Ext.create('FHD.view.response.major.task.MajorRiskTaskSelectEmpFormPanel',{
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
				title:'任务分配',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.selectEmpForm]
	  	});
	
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.fieldSet1, me.fieldSet2]
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