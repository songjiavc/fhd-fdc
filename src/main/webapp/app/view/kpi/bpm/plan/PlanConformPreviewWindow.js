Ext.define('FHD.view.kpi.bpm.plan.PlanConformPreviewWindow', {
    extend: 'Ext.form.Panel',
    alias: 'widget.planConformPreviewWindow',
    loadData: function(id){
    	var me = this;
    	me.formLoad(id);
    	//me.planConformSubmitInfoGrid.reloadData(id);
    },
    
    //加载表单数据
    formLoad: function(id){
    	var me = this;
    	me.form.load({
	        url:__ctxPath + '/access/planconform/findpreviewgridbyplanid.f',
	        params:{
	        	id: id
	        },
	        failure:function(form,action) {
	            //alert("err 155");
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
         	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
         	              	{xtype:'displayfield', fieldLabel : '计划编号', name:'planCode'},
    						{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
    						{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'},
    						{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
    						//{xtype:'displayfield', fieldLabel : '评估模板', name : 'templateName'},
    						{xtype:'displayfield', fieldLabel : '范围要求', name:'rangeReq'},
    						{xtype:'displayfield', fieldLabel : '工作目标', name:'workTage'}]
    						
            };
      
        me.previewGrid = Ext.create('FHD.view.kpi.bpm.plan.TargetValueGatherPlan',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	planId: me.planId,
        	isEdit: false // 是否只是窗口展示
        });
        me.previewGrid.loadData(me.businessId);
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
        /*me.planConformSubmitInfoGrid = Ext.create('FHD.view.risk.planconform.PlanConformSubmitInfoGrid');
        me.fieldSet3 = {
                xtype:'fieldset',
                title: '提交信息',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 5 5',
         	    items : [
         	    		me.planConformSubmitInfoGrid
         	    ]
            };*/
        
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1, me.fieldSet2/*, me.fieldSet3*/]
        });

        me.callParent(arguments);
        
    	me.on('resize',function(p){
			me.setHeight(FHD.getCenterPanelHeight()-30);
		});
    }

});