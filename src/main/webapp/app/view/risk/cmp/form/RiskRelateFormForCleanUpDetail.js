/**
 * add by songjia
 * 提供给辨识整理环节的风险录入和展示页面
 */
Ext.define('FHD.view.risk.cmp.form.RiskRelateFormForCleanUpDetail', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskrelateformforcleanupdetail',
	autoHeight : true,
	autoWidth : true,
	width : '99%',
	defaults : {
		margin : '5 30 5 30',
		height : 24,
		labelWidth : 100
	},
	layout : {
		type : 'column'
	},
	/**
	 * 常量
	 */
	
	findUrl : '/cmp/risk/findRiskEditInfoByScoreObjectIdForCleanUpDetail.f',

	/**
	 * 加载页面内容
	 * @param {} scoreObjectId
	 */
	reloadData : function (scoreObjectId){
       
       	var me = this;
        
		FHD.ajax({
			params: {
               scoreObjectId : scoreObjectId
            },
            url: __ctxPath + me.findUrl,
            
			callback : function(data){
		        me.form.setValues({
		            parentName: data.parentName,
		            code:data.code,
		            name:data.name,
		            desc:data.desc,
		            respDeptName:data.respDeptName,
		            relaDeptName:data.relaDeptName,
		            responseText : data.responseText
		        });
//		        me.doLayout();
			}
		});
    },
	initComponent : function() {
		// 基本信息
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
        
         //应对描述
        var responseText = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: '应对措施',
            margin: '7 30 3 30', 
            name: 'responseText',
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

		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [parentName,code,name,desc,responseText,respDeptName,relaDeptName]//,relafieldSet
		});
		me.callParent(arguments);
	}
});