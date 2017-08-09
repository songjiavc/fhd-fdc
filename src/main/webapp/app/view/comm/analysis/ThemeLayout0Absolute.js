Ext.define('FHD.view.comm.analysis.ThemeLayout0Absolute', {
    extend: 'Ext.container.Container',
    alias: 'widget.themelayout0absolute',

    border:false,
    //宽度
    width:100,
    //高度
    height:100,
    //面板A的title
    title:'A',
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
		//布局0--绝对
		me.panel = Ext.create('Ext.panel.Panel',{
			title:me.title,
			width:me.width,
			height:me.height,
			tools: [
			    {
			        itemId: 'gear',
			        type: 'gear',
			        handler: function(){
			        	me.win = Ext.create('FHD.view.comm.analysis.ThemeDataSourceSet',{
			        		title:'数据源设置',
			        		businessId: me.businessId,
			        		onePanelId:me.onePanelId
			        	});
			        	me.win.show();
			        }
			    }
			]
		});
		
        Ext.applyIf(me, {
        	layout: {
				type: 'fit'
	        },
	        items:[me.panel]
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