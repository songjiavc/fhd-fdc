/*
 * 评价流程的可编辑列表
 * 入参：parameter:{assessPlanId:'评价计划Id',assessPlanType:'评价类型'}
 * */
Ext.define('FHD.view.response.responseplan.ResponsePlanRelaStandardViewGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.responseplanrelastandardviewgrid',
	url:__ctxPath +'/app/view/response/responseplan/SelectRiskGroupInfo.json',
	extraParams:{
		businessId:''
	},
	pagable :false,
	cols:new Array(),
	tbarItems:new Array(),
	bbarItems:new Array(),
	sortableColumns : false,
	//可编辑列表为只读属性
	readOnly : false,
	searchable : false,
	requires : [
    	'FHD.view.risk.assess.utils.GridCells'
	],
	initComponent:function(){
		var me=this;
		var isDiagnosisStore=Ext.create('Ext.data.Store', {
		    fields: ['value', 'text'],
		    data : [
		        {"value":true, "text":"张三"},
		        {"value":false, "text":"李四"}
		    ]
		});
		me.cols=[
		   {dataIndex : 'id',hidden:true},
		    {
				header : '部门名称',dataIndex : 'orgOr',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '风险数量',dataIndex : 'riskNum',flex : 2,
	    		renderer : function(value,metaData,record,colIndex,store,view){ 
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
				}
			},
			{
				header : '责任人',dataIndex : 'empOr',flex : 2,editor:Ext.create('Ext.form.ComboBox', {
				    store: isDiagnosisStore,
				    queryMode: 'local',
				    displayField:'text',
				    valueField: 'value'
				}),
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		if(value){
						metaData.tdAttr = 'data-qtip="请选择风险应对方案制订责任人。" style="background-color:#FFFBE6"';
						return value;
					}else{
						metaData.tdAttr = 'data-qtip="请选择风险应对方案制订责任人。" style="background-color:#FFFBE6"';
						return '';
					}
				}
			}
		];
		me.callParent(arguments);
		me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        });
	},
    reloadData:function(){
    	var me=this;
    	me.store.load();
    },
    showMoreInfo : function(){ 
		var responseplanrelarisklist = Ext.create('FHD.view.response.responseplan.ResponsePlanRelaRiskList',{readOnly : true});
		var win = Ext.create('FHD.ux.Window',{
			title:'风险详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(responseplanrelarisklist);
	}
});