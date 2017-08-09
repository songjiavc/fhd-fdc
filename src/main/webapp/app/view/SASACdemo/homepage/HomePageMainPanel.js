Ext.define('FHD.view.SASACdemo.homepage.HomePageMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.homePageMainPanel',

    requires: [
		'FHD.view.SASACdemo.SASACCompanyWarnGrid',
		'FHD.view.SASACdemo.SASACRiskGrid'
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.sasacCompanyWarnGrid = Ext.widget('sasacCompanyWarnGrid');//左下侧列表
        
        me.sasacRiskGrid = Ext.widget('sasacRiskGrid');//右下侧列表
        
        me.SASACchartPointLine = Ext.create('FHD.view.SASACdemo.homepage.SASACchartPointLine',{
        	height:300,
        	region: 'west',
        	border:true,
        	flex: 1
        });
        me.SASACchartPointLine.chart.setTitle('收入');
        
        me.SASACchartPointLineRight = Ext.create('FHD.view.SASACdemo.homepage.SASACchartPointLine',{
        	height:300,
        	title: '利润',
        	region: 'center',
        	border:true,
        	flex: 1
        });
        me.SASACchartPointLineRight.chart.setTitle('利润');
        
        me.panel1 = Ext.create('Ext.panel.Panel',{
        	border : false,
        	typeId: me.typeId,
        	height: 300,
        	layout: 'border',
            region: 'center',
            items: [me.sasacCompanyWarnGrid,me.sasacRiskGrid]
        });
        
        me.panel2 = Ext.create('Ext.panel.Panel',{
        	border : true,
        	margin: '1 1 0 1',
        	height: 300,
        	layout: 'border',
        	region: 'north',
        	items: [me.SASACchartPointLine,me.SASACchartPointLineRight]
        });
        
        Ext.applyIf(me, {
        	border : false,
        	layout: 'border',
            items: [me.panel1, me.panel2]
        });

        me.callParent(arguments);
    }

});