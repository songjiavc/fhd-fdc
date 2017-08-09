Ext.define('FHD.view.risk.cmp.form.RiskBasicFormDetail', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskbasicformdetail',

    /**
     * 常量
     */
    detailUrl: '/cmp/risk/findRiskDetailInfoById',
    
    /**
     * 变量
     */
    riskId: null,	//查看的风险id
    
    /**
     * 重新加载数据
     */
    reloadData:Ext.emptyFn(),

    /**
     * 添加基本信息
     */
    addBasicComponent: function () {
        var me = this;

        //上级风险
        var parentName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '上级风险',
            name: 'parentName',
            margin : '7 30 3 30',
            columnWidth: .5
        });

        //编码
        var code = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '风险编号',
            margin: '3 3 3 30',
            name: 'code',
            margin : '7 30 3 30',
            columnWidth: .5
        });

        //风险名称
        var name = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: '风险名称',
            margin: '7 30 3 30', 
            name: 'name',
            columnWidth: 1
        });
        
        //风险描述
        var desc = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: '风险描述',
            margin: '7 30 3 30', 
            name: 'desc',
            columnWidth: 1
        });
        
        //责任部门
        var respDeptName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: '责任部门',
            margin: '7 30 3 30', 
            name: 'respDeptName',
            columnWidth: .5
        });
        
        //相关部门
        var relaDeptName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: '相关部门',
            margin: '7 30 3 30', 
            name: 'relaDeptName',
            columnWidth: .5
        });
        
        //影响指标
        var influKpiName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '影响指标',
            name: 'influKpiName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        //影响流程
        var influProcessureName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '影响流程',
            name: 'influProcessureName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        basicfieldSet = Ext.widget('fieldset', {
			title : '风险信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			margin : '10 10 10 10',
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : [parentName, code, name, desc, respDeptName, relaDeptName,influKpiName,influProcessureName]
		});
        
        return basicfieldSet;
    },
    
    /**
     * 添加指标信息
     */
    addKpiComponentTest: function () {
        var me = this;

        //影响指标
        var influKpiName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '影响指标',
            name: 'influKpiName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        //风险指标
        var riskKpiName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '风险指标',
            name: 'riskKpiName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        kpifieldSet = Ext.widget('fieldset', {
			title : '指标信息',
			xtype : 'fieldset', // 基本信息fieldset
			width : '99%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : [influKpiName, riskKpiName]
		});
        
        return kpifieldSet;
    },
    
    /**
     * 添加流程信息
     */
    addProcessComponentTest: function () {
        var me = this;

        //影响流程
        var influProcessureName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '影响流程',
            name: 'influProcessureName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        //控制流程
        var controlProcessureName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '控制流程',
            name: 'controlProcessureName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        processfieldSet = Ext.widget('fieldset', {
			title : '流程信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : [influProcessureName, controlProcessureName]
		});
        
        return processfieldSet;
    },
    
    /**
     * 添加风险信息
     */
    addRiskComponent: function () {
        var me = this;

        //风险动因
        var riskReasonName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '风险动因',
            name: 'riskReasonName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        //风险影响
        var riskInfluenceName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '风险影响',
            name: 'riskInfluenceName',
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
        me.riskfieldSet = Ext.widget('fieldset', {
			title : '关联风险信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : [riskReasonName, riskInfluenceName]
		});
    },
    
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    } 
});