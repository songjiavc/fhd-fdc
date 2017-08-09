/**
 * 
 * 
 */
 Ext.define('FHD.view.riskAnalysisDemo.AnalysisResultGridPanel',{
    	extend:'FHD.ux.layout.GridPanel',
    	alias: 'widget.analysisResultGridPanel',
    	requires: [
		],
    	
    	initComponent:function(){//初始化
    		var me=this;
	    	me.cols=[
	    		{header:'', dataIndex:'title', flex : 1},
			 	{header : "Coefficients", dataIndex : 'coefficients', sortable : true, flex : 1},
			 	{header : "标准误差", dataIndex : 'error', sortable : true, flex : 1},
			 	{header : "t Stat", dataIndex : 'tStat', sortable : true, flex : 1},
			 	{header : "P-value", dataIndex : 'Pvalue', sortable : true, flex : 1},
			 	{header : "Lower 95%", dataIndex : 'Lower', sortable : true, flex : 1},
			 	{header : "Upper 95%", dataIndex : 'Upper', sortable : true, flex : 1},
			 	{header : "下限 95.0%", dataIndex : 'floor', sortable : true, flex : 1},
				{header : "上限 95.0%", dataIndex:'top', flex : 1}
			];
		Ext.apply(me,{
        	region:'center',
        	margin: '5 5 5 5',
        	url : __ctxPath + "/app/view/riskAnalysisDemo/resultgridlist.json",//查询列表url
		    border: false,
		    checked : false,
		    pagable : false,
    		searchable:false
        });
	    	me.callParent(arguments);
	    	
	    }
});