/**
 * 风险图谱
 * @author 郭鹏
 * 20170502
 */
Ext.define('FHD.view.risk.analyse.AnalyseMainPanelNew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.analysemainpanelnew',
	header : true,
    layout: {
        type: 'border'
    },
    border : true,
    
    
     nodeClick: function (record) {
        var me = this;
        var id = record.data.id;
        me.nodeId = id;
        me.node = record;
        me.nodeType = 'risk';
        //刷新右侧容器数据
        me.riskCard.initParams("risk",id);
        me.riskCard.reloadData(id);
    },
    initComponent: function () {
        var me = this;
        
         var me = this;
        //风险库右侧面板
        me.riskCard = Ext.create('FHD.view.risk.cmp.chart.RiskHeatMapPanel',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        //风险树
        me.riskTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel',{
            typeId: me.typeId,
            region: 'west',
            border: true,
            searchable:true,
			toolbar : true,
			expandable : false,
			hideCollapseTool : true,
            rbs: true,
            typeId: me.typeId,
            showLight: true,
            width: 200,
            maxWidth: 350,
            collapsible : false,
            split: true,
            autoDestroy: true,
        	listeners: {	
   				//树单击事件
          itemclick : function (tablepanel, record, item, index, e, options) {
                 me.nodeClick(record);
			   
                }
            }
        });

        Ext.applyIf(me, {
            items: [me.riskTree,me.riskCard]
        });

        me.callParent(arguments);
    }  
});