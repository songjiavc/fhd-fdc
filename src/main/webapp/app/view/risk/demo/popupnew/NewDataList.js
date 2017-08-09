/*
 * 详细页面 
 * */
Ext.define('FHD.view.risk.demo.popupnew.NewDataList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.newdatalist',
    requires: [
    	'FHD.view.risk.demo.popupnew.NewDataViewForm'
    ],
    pagable:true,
    layout: 'fit',
    flex : 7,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	url : __ctxPath + '/app/view/risk/demo/popupnew/NewDataList.json',
    initComponent: function(){
    	var me = this;
		//列表
        me.on('selectionchange',me.setstatus);//选择记录发生改变时改变按钮可用状态
        me.cols = [
       		{header: '', dataIndex: 'css', sortable: true, flex: 0.5,renderer:function(value,metaData,record,colIndex,store,view) { 
    			metaData.tdAttr = 'data-qtip="'+value+'"';  
    			var CSS=record.get('css');
    		    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='"+CSS+"'></div>";  
    		}},
        	{header : '类型',dataIndex : 'type',sortable : false, flex : 0.5}, 
    		{header : '编号',dataIndex : 'code',sortable : false, flex : 1}, 
 			{header : '名称',dataIndex : 'name',sortable : false, flex : 5,
 				renderer : function(value,metaData,record,colIndex,store,view){ 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.type + "','" + record.data.name +"','" + record.data.code +"','"+ record.data.manageOrg +"')\" >" + value + "</a>"; 
					}
 			},
 			{header : '责任部门',dataIndex : 'manageOrg',sortable : false, flex:1}, 
 			{header : '状态',dataIndex :'status',sortable : false,flex:1,
				renderer:function(value){
					if(value=='S'){
						return '已保存';
					}else if(value=='P'){
						return '已提交';
					}else{
						return '已处理';
					}
				}
 			},
			{header : '创建时间',dataIndex :'createTime',sortable : false, flex : 1},
			{header : '操作',dataIndex : 'name',sortable : false, flex : 1,
 				renderer : function(value,metaData,record,colIndex,store,view){ 
						return "<a href='javascript:void(0)' onclick='' >" + "再评估" + "</a>"; 
					}
 			}
		];
        me.tbarItems = [
        	{
		    	iconCls : 'icon-add',
		    	text: '上报',
		    	tooltip : '上报',
		    	handler :me.addHistEvent,
		    	scope : this
		    },
		     '-',
		    {
		    	iconCls : 'icon-edit',
		    	tooltip: '修改',
		    	text: '修改',
		    	handler :me.addPreplan,
		    	scope : this
		    },
		     '-',
		    {
		    	iconCls : 'icon-del',
		    	tooltip: '删除',
		    	text: '删除',
		    	handler :me.addPreplan,
		    	scope : this
		    }
        ];
        Ext.apply(me,{
        	border:me.border
        });
        me.callParent(arguments);
    },
    //新增方案
    addHistEvent:function(){
    	var me=this;
    	var histeventform = Ext.create('FHD.view.risk.demo.popupnew.HistEventForm');
		var win = Ext.create('FHD.ux.Window',{
			title:'历史事件详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(histeventform);
    },
    //编辑方案
    editPlan: function(button){
    },
    //删除方案
    delPlan: function(){
    },
    //新增预案
    addPreplan: function(){
    	
    },
	reloadData:function(){
		var me=this;
//		me.store.proxy.url = __ctxPath + '/app/view/response/responseplan/ResponsePlanList.json',
//        me.store.proxy.extraParams = {
//        	status : 'S'
//        };
		me.store.load();
	},
	showMoreInfo : function(type, name, code, manageOrg){ 
		var solutionviewform = Ext.widget('newdataviewform', {type : type,name : name, code: code, manageOrg: manageOrg});
		var win = Ext.create('FHD.ux.Window',{
			title:'详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionviewform);
	}
});