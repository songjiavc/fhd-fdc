Ext.define('FHD.view.check.quarterlycheck.plan.quarterlyCheckPlanNextForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.quarterlyCheckPlanNextForm',
    requires: [],
    // 初始化方法
    
    
    initComponent: function () {
        var me = this;
        me.fieldSet = {
            xtype: 'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed: true, //初始化收缩
            margin: '5 5 5 5',
            defaults: {
                columnWidth: 1 / 2,
                margin: '7 30 7 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: '计划名称',
                name: 'pc_planName'
            }, {
                xtype: 'displayfield',
                fieldLabel: '起止日期',
                name: 'pc_date'
            }, {
                xtype: 'displayfield',
                fieldLabel: '联系人',
                name: 'pc_contactor'
            }, {
                xtype: 'displayfield',
                fieldLabel: '负责人',
                name: 'pc_responser'
            }]
        };
         me.empGird=Ext.create('FHD.view.check.yearcheck.plan.YearCheckPlanNextGrid',{  
            flex: 1,
            margin: 2,
            columnWidth: 1,
            height:420,
            executionId: me.executionId,
			winId: me.winId,
			businessId: me.businessId
            }
         )
         me.fieldSet2 = Ext.create('Ext.form.FieldSet', {
            layout: {
                type: 'column'
            },
            title: '考核部门调配',
            collapsible: true,
            margin: '5 5 5 5',
            items: [me.empGird]
        });
        
         Ext.apply(me, {
        	border:false,
            items : [me.fieldSet,me.fieldSet2]
        });

       me.callParent(arguments);
    }
  
});