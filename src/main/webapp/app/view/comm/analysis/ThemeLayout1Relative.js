Ext.define('FHD.view.comm.analysis.ThemeLayout1Relative', {
    extend: 'Ext.container.Container',
    alias: 'widget.themelayout1relative',

    border:false,
    //第一行高度比例
    oneHeightRatio:1,
    //第二行高度比例
    twoHeightRatio:1,
    //a宽度比例
    aWidthRatio:1,
    //b宽度比例
    bWidthRatio:1,
    //c宽度比例
    cWidthRatio:1,
    //面板A的title
    aTitle:'A',
    //面板B的title
    bTitle:'B',
    //面板C的title
    cTitle:'C',
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
		//布局1--相对
		me.aPanel = Ext.create('Ext.panel.Panel',{
			title:me.aTitle,
			flex:me.oneHeightRatio,
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
		me.downRegion = Ext.create('Ext.container.Container',{
			layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
	        flex:me.twoHeightRatio,
			items:[me.bPanel,me.cPanel]
		})
		
        Ext.applyIf(me, {
        	layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
	        items:[me.aPanel,me.downRegion]
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