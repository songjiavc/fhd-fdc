Ext.define('FHD.view.comm.analysis.ThemeLayout2Relative', {
    extend: 'Ext.container.Container',
    alias: 'widget.themelayout2relative',

    border:false,
    //第一行的高度比例
    oneHeightRatio:1,
    //第二行的高度比例
    twoHeightRatio:1,
    //A的宽度比例
    aWidthRatio:1,
    //B的宽度比例
    bWidthRatio:1,
    //C的宽度比例
    cWidthRatio:1,
    //D的宽度比例
    dWidthRatio:1,
    //面板A的title
    aTitle:'A',
    //面板B的title
    bTitle:'B',
    //面板C的title
    cTitle:'C',
    //面板D的title
    dTitle:'D',
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
		//布局2--相对
		me.aPanel = Ext.create('Ext.panel.Panel',{
			title:me.aTitle,
			flex:me.aWidthRatio,
			tools: [
			    {
			        itemId: 'gear',
			        type: 'gear',
			        handler: function(){
			        	me.win = Ext.create('FHD.view.comm.analysis.ThemeDataSourceSet',{
			        		title:'数据源设置',
			        		businessId: me.businessId,
			        		panelId:me.onePanelId
			        	});
			        	me.win.show();
			        }
			    }
			]
		});
		me.bPanel = Ext.create('Ext.panel.Panel',{
			title:me.bTitle,
			flex:me.bWidthRatio,
			tools: [
			    {
			        itemId: 'gear',
			        type: 'gear',
			        handler: function(){
			        	me.win = Ext.create('FHD.view.comm.analysis.ThemeDataSourceSet',{
			        		title:'数据源设置',
			        		businessId: me.businessId,
			        		panelId:me.twoPanelId
			        	});
			        	me.win.show();
			        }
			    }
			]
		});
		me.cPanel = Ext.create('Ext.panel.Panel',{
			title:me.cTitle,
			flex:me.cWidthRatio,
			tools: [
			    {
			        itemId: 'gear',
			        type: 'gear',
			        handler: function(){
			        	me.win = Ext.create('FHD.view.comm.analysis.ThemeDataSourceSet',{
			        		title:'数据源设置',
			        		businessId: me.businessId,
			        		panelId:me.threePanelId
			        	});
			        	me.win.show();
			        }
			    }
			]
		});
		me.dPanel = Ext.create('Ext.panel.Panel',{
			title:me.dTitle,
			flex:me.dWidthRatio,
			tools: [
			    {
			        itemId: 'gear',
			        type: 'gear',
			        handler: function(){
			        	me.win = Ext.create('FHD.view.comm.analysis.ThemeDataSourceSet',{
			        		title:'数据源设置',
			        		businessId: me.businessId,
			        		panelId:me.fourPanelId
			        	});
			        	me.win.show();
			        }
			    }
			]
		});
		me.upRegion = Ext.create('Ext.container.Container',{
			layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
	        flex:me.oneHeightRatio,
			items:[me.aPanel,me.bPanel]
		});
		me.downRegion = Ext.create('Ext.container.Container',{
			layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
	        flex:me.twoHeightRatio,
			items:[me.cPanel,me.dPanel]
		});
		
        Ext.applyIf(me, {
        	layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
	        items:[me.upRegion,me.downRegion]
        });
        
        me.callParent(arguments);
    },
   	reloadData:function(){
   		var me=this;
   		
   		/*
   		 * TODO
   		 * 添加时：不生成，只初始化
   		 * 修改时：根据主题分析id查询需要的数据，进行封装，调用FHD.ux.FusionChartPanel或FHD.ux.GridPanel
   		 * 生成每个子panel对应的图表或列表，然后，放到相应的panel中展示
   		 */
   	}
});