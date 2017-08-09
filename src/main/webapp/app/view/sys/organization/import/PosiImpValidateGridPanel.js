Ext.define('FHD.view.sys.organization.import.PosiImpValidateGridPanel', {
	extend: 'FHD.ux.GridPanel',
	alias: 'widget.posiImpValidateGridPanel',
	
	//加载表单数据
	reloadData:function(gridData){
		var me=this;
		me.store.loadData(gridData);
	},
	//查看全部，加载列表数据
	checkGridData: function(url){
		var me = this;
		FHD.ajax({
			url: url,
			callback: function (data) {
				me.reloadData(data.datas);
			}
		});
	},
    // 初始化方法
	initComponent: function() {
		var me = this;
		//列表项
		me.cols =[{	
			dataIndex : 'id',
			invisible : true
		},{
			header : '行号',
			width : 40,
			dataIndex : 'rowLine',
			sortable : true	
		},{
			text : '岗位编号',
			flex : 1,
			dataIndex : 'posiCode',
			sortable : true
		},{
			text : '岗位名称',
			flex : 1,
			dataIndex : 'posiName',
			sortable : true
		},{
			text : '所属机构',
			flex : 1,
			dataIndex : 'orgName',
			sortable : true
		},{
			text : '排序',
			flex : 1,
			dataIndex : 'sn',
			sortable : true
		},{
			text : '校验结果',
			flex : 2,
			dataIndex : 'validateInfo',
			sortable : true,
			renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
		}];
		
		Ext.apply(me, {
			pagable: false,
			storeAutoLoad: false,
			url: __ctxPath + '/sys/position/import/findalltmpsyspositions.f',
			multiSelect: true,
			border: false,
			rowLines: true,//显示横向表格线
			checked: false, //复选框
			autoScroll:true,
			viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return record.get("validateInfo") ? "row-s" : "";
                }
            }
		});
		me.callParent(arguments);
    }
   
});