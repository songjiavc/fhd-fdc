Ext.define('FHD.view.comm.analysis.ThemeLayout2Absolute', {
    extend: 'Ext.container.Container',
    alias: 'widget.themelayout2absolute',

    border:false,
    //a宽度
    aWidth:100,
    //a高度
    aHeight:100,
    //b宽度
    bWidth:100,
    //a高度
    bHeight:100,
    //c宽度
    cWidth:100,
    //c高度
    cHeight:100,
    //d宽度
    cWidth:100,
    //d高度
    cHeight:100,
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
        
		//布局1--绝对
        me.aPanel = Ext.create('Ext.panel.Panel',{
			title:me.aTitle,
			width:me.aWidth,
			height:me.aHeight,
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
			width:me.bWidth,
			height:me.bHeight,
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
			width:me.cWidth,
			height:me.cHeight,
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
			width:me.dWidth,
			height:me.dHeight,
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
			items:[me.aPanel,me.bPanel]
		});
		me.downRegion = Ext.create('Ext.container.Container',{
			layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
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