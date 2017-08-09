/**
 * 
 * 问卷监控主界面
 */

Ext.define('FHD.view.risk.assess.formulatePlan.AssessPlanQuestMonitorMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.assessPlanQuestMonitorMain',
    requires: [
               'FHD.view.risk.assess.formulatePlan.AssessPlanQuestMoniLeftGrid',
               'FHD.view.risk.assess.formulatePlan.AssessPlanQuestMoniRightGrid'
	],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.leftGridPanel = Ext.widget('assessPlanQuestMoniLeftGrid',{
        	height: '100%',
        	width:	'30%',
        	planId:me.id
        });
        me.leftGridPanel.reloadData();

       me.rightGridPanel = Ext.widget('assessPlanQuestMoniRightGrid',{
        	height:	'100%',
        	width:	'70%'
        });
       //返回按钮
       me.btnReturn = Ext.create('Ext.button.Button',{
 	            text: '返回',
 	            disabled: false,
 	            iconCls: 'icon-arrow-undo',
 	            handler: function () {
 	            	var prt = me.up('formulatePlanCard');
 	            	me.remove(prt.formulateGrid.newPanel);
 	            	prt.showFormulateGrid();
 	            }
 	        });
        
        Ext.apply(me, {
        	border:false,
        	pagable : false,
        	//bbar:['->',me.btnReturn],
        	layout: {
     			align: 'stretch',
     	        type: 'hbox'
     	    },
            items: [me.leftGridPanel, me.rightGridPanel]
        });

        me.callParent(arguments);
    }

});