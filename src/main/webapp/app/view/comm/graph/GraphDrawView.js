/*
* 调用方法，带参数的url，方式为‘FHD.view.comm.graph.GraphDrawView?’+id
* 例如：FHD.view.comm.graph.GraphDrawView?07b4bd00-6281-4de1-882f-c1c5910af1a9
*/
Ext.define('FHD.view.comm.graph.GraphDrawView',{
	extend: 'Ext.container.Container',
	alias: 'widget.graphdrawview',
	autoScroll : false,
	// 初始化方法
    initComponent: function() {
    	var me = this;
    	me.callParent(arguments);//<div style='height:100%;width:100%;text-align: center;overflow: auto;'></div>
    	me.html = "<iframe src='"+__ctxPath+"/comm/graph/findGraph.f?id="+me.typeId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
    }
})