Ext.define('FHD.view.risk.managereport.managereportmake.ManageReportPlanRange', {
    extend: 'Ext.form.Panel',
    alias: 'widget.managereportplanrange',
    requires: [
	],
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed : true,//初始化收缩
            margin: '5 5 5 5',
            defaults: {
                    columnWidth : 1 / 2,
                    margin: '7 30 7 30',
                    labelWidth: 95
                },
            layout: {
     	        type: 'column'
     	    },
     	    listeners: {
         	    	expand:function(){
         	    		me.rightgrid.setHeight(me.getHeight()-180);
         	    	},
         	    	collapse:function(){
         	    		me.rightgrid.setHeight(me.getHeight()-100);
         	    	}
         	},
     	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
						{xtype:'displayfield', fieldLabel : '起止日期', name : 'createByRealname'},
						{xtype:'displayfield', fieldLabel : '联系人', name : 'createTime'},
						{xtype:'displayfield', fieldLabel : '负责人', name : 'updateTime'}]
        };
        
        me.rightgrid = Ext.create('FHD.view.risk.managereport.managereportmake.ManageReportPlanDeptTakerGrid',{flex:1,margin:2,columnWidth :1});

        var fieldSet3 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'风险管理事件选择',
				  collapsible: true,
				  margin: '5 5 5 5',
				  items:[me.rightgrid]
	  	});
        
        Ext.apply(me, {
        	autoScroll:false,
        	border:false,
            items : [me.fieldSet,fieldSet3]
        });

        me.callParent(arguments);

		me.on('resize',function(p){
			me.rightgrid.setHeight(me.getHeight()-100);
		});
    }

});