/**
 * 
 * 计划制定表单
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanRang', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateplanrang',
    requires: [
               'FHD.view.risk.assess.formulatePlan.FormulatePlanEdit'
	],
    
    load : function(id){
    	var me = this;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        //me.id = 'formulatePlanRangId';
        //var formulatePlanEdit = Ext.getCmp('formulatePlanEdit');
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
        
        //me.rightgrid = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanPreviewGrid',{flex:1,margin:2,columnWidth :1});
        //调整页面测试
        me.rightgrid = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateDeptUndertakerGrid',{flex:1,margin:2,columnWidth :1,typeId:me.typeId,
        	schm : me.schm,
	 		planId:me.planId,
	 		formulatePlanMainPanel : me.formulatePlanMainPanel,
	 		flowType : me.flowType,});

        var fieldSet3 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'评估范围',
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
    		//me.rightgrid.setHeight(FHD.getCenterPanelHeight()-120);
			me.rightgrid.setHeight(me.getHeight()-100);
		});
    }

});