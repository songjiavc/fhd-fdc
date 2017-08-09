/**
 * 估计是用做测试用的页面
 * @author zhengjunxiang
 */
Ext.define('FHD.view.process.graph.GraphDraw',{
	extend: 'Ext.panel.Panel',
	alias: 'widget.graphdraw',
	html:"<iframe src='"+__ctxPath+"/graph/findprocessgraph.f?viewType=graphdraw' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>",
	// 初始化方法
    initComponent: function() {
    	var me = this;
    	me.callParent(arguments);
    }
})