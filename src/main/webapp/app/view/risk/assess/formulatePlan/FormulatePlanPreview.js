/**
 * 
 * 计划制定预览
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanPreview', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateplanpreview',
    requires: [
    			'FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitGridPanel',
    			'FHD.view.risk.assess.formulatePlan.FormulataSubmitPreviewGridPanel',
    			'FHD.view.risk.assess.utils.GridCells',
    			'FHD.view.risk.assess.formulatePlan.FormulataPreviewPlanDeptGrid'
    ],
    
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
         	              	{xtype:'displayfield', fieldLabel : '工作类型', name:'workType'},
    						{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
    						{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'},
    						{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
    						{xtype:'displayfield', fieldLabel : '采集频率', name : 'collectRate'},
    						{xtype:'displayfield', fieldLabel : '评估模板', name : 'templateName'},
    						{xtype:'displayfield', fieldLabel : '范围要求', name:'rangeReq'},
    						{xtype:'displayfield', fieldLabel : '工作目标', name:'workTage'}]
    						
            };
      
        //me.formulateApproverSubmitGridPanel = Ext.widget('formulateApproverSubmitGridPanel');
        me.formulataPreviewPlanDeptGrid = Ext.widget('formulataPreviewPlanDeptGrid');
        me.fieldSet4 = {
                xtype:'fieldset',
                title: '评估范围',
                collapsed : true,//初始化收缩
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
         	    items : [me.formulataPreviewPlanDeptGrid],
         	    listeners: {
         	    	expand:function(){
         	    		//展开时查询列表，否则不能合并单元格
         	    		//var formulateGrid = Ext.getCmp('formulateGridId');
         	    		me.formulataPreviewPlanDeptGrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
					    me.formulataPreviewPlanDeptGrid.store.proxy.extraParams.planId = me.pid; //formulateGrid.pid
					    me.formulataPreviewPlanDeptGrid.store.load();
         	    	}
         	    }
            };
        me.formulatasubmitpreviewGridPanel = Ext.widget('formulatasubmitpreviewGridPanel');
        me.fieldSet5 = {
                xtype:'fieldset',
                title: '提交信息',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 5 5',
         	    items : [
         	    		me.formulatasubmitpreviewGridPanel
         	    		//{xtype:'displayfield', fieldLabel : '审批人', name:'approverName'}
         	    ]
            };
        
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1, me.fieldSet4, me.fieldSet5]
        });

        me.callParent(arguments);
    	me.on('resize',function(p){
			me.setHeight(FHD.getCenterPanelHeight()-30);
		});
    }

});