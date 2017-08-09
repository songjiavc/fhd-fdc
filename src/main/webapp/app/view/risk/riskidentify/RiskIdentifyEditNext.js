Ext.define('FHD.view.risk.riskidentify.RiskIdentifyEditNext', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskIdentifyEditNext',
    requires: [
	],
    //加载数据
    loadData : function(id){
    	var me = this;
    	me.downPanel.loadData(id);
    },
    
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
         	    		if(me.downPanel){
         	    			me.downPanel.setHeight(me.getHeight()-180);
         	    		}
         	    	},
         	    	collapse:function(){
         	    		if(me.downPanel){
         	    			me.downPanel.setHeight(me.getHeight()-100);
         	    		}
         	    	}
         	},
     	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'ri_planName'},
						{xtype:'displayfield', fieldLabel : '起止日期', name : 'ri_date'},
						{xtype:'displayfield', fieldLabel : '联系人', name : 'ri_contactor'},
						{xtype:'displayfield', fieldLabel : '负责人', name : 'ri_responser'}]
        };
        
        var downPanelUrl = "";
        if(me._description == "complex"){
        	downPanelUrl = "FHD.view.risk.riskidentify.PlanConformEditNextGridriskIdentifyNew";
        }else{
        	downPanelUrl = "FHD.view.risk.planconformNew.deptFlow.PlanConformEditNextGridriskIdentifyNew";
        }
        
        //风险事件选择列表
        me.downPanel = Ext.create(downPanelUrl,{
			flex: 1,
			executionId: me.executionId,
			businessId: me.businessId,
			//吉志强 2017年4月17日10:02:17添加计划id和分库标识，为了在下一步的时候按风险添加共用
			planId:me.businessId,
			schm:me.schm,
			winId: me.winId,
			margin: 2,
			columnWidth: 1
		});

        me.fieldSet3 = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				  title:'风险事件选择',
				  collapsible: true,
				  margin: '5 5 5 5',
				  items:[me.downPanel]
	  	});
        
        Ext.apply(me, {
        	autoScroll:false,
        	border:false,
            items : [me.fieldSet,me.fieldSet3]
        });

        me.callParent(arguments);

		me.on('resize',function(p){
			if(me.downPanel){
				me.downPanel.setHeight(me.getHeight()-100);
			}
		});
    }

});