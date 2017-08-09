Ext.define('FHD.view.check.quarterlycheck.plan.QuarterlyCheckPlanPreviewWindow', {
    extend: 'Ext.form.Panel',
    alias: 'widget.QuarterlyCheckPlanPreviewWindow',

    loadData: function(id){
    	var me = this;
    	me.formLoad(id);
    	me.previewGrid.loadData(id);
    },
    
    //加载表单数据
    formLoad: function(id){
    	var me = this;
    	me.form.load({
	       url: __ctxPath + '/check/quarterlycheck/findQuarterlyCheckPlanById.f',
	        params:{id:id},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet1 = {
                xtype:'fieldset',
                title: '基础信息',
                collapsible: true,
                margin: '5 5 0 5',
                defaults: {
                        columnWidth : 1 / 2,
                        margin: '7 30 3 30',
                        labelWidth: 95
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'name'},
         	              	{xtype:'displayfield', fieldLabel : '计划编号', name:'planCode'},
    						{xtype:'displayfield', fieldLabel : '联系人', name : 'cName'},
    						{xtype:'displayfield', fieldLabel : '负责人', name : 'rName'},
    						{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
    						{xtype:'displayfield', fieldLabel : '范围要求', name:'rangeReq'},
    						{xtype:'displayfield', fieldLabel : '工作目标', name:'checkContent'}]
    						
            };
      
        me.previewGrid = Ext.create('FHD.view.check.yearcheck.plan.YearCheckPlanNextGrid',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1
        });
        me.fieldSet2 = {
                xtype:'fieldset',
                title: '评估范围',
                collapsed : false,//初始化收缩
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
         	    items : [me.previewGrid],
         	    listeners: {
         	    	expand:function(){
         	    		//展开时查询列表，否则不能合并单元格
         	    		me.previewGrid.loadData(me.businessId);
         	    	}
         	    }
            };
    
        
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1, me.fieldSet2]
        });

        me.callParent(arguments);
        
    	me.on('resize',function(p){
			me.setHeight(FHD.getCenterPanelHeight()-30);
		});
    }

});