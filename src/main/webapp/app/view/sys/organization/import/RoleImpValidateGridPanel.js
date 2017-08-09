Ext.define('FHD.view.sys.organization.import.RoleImpValidateGridPanel', {
	extend: 'FHD.ux.GridPanel',
	alias: 'widget.roleimpvalidategridpanel',
	
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
		//员工列表项
		me.cols =[{	
			dataIndex : 'id',
			invisible : true
		},{
			header : '行号',
			width : 40,
			dataIndex : 'rowLine',
			sortable : true	
		},{
			header : '角色编号',
			flex : 1,
			dataIndex : 'roleCode',
			sortable : true	
		},{
			text : '角色名称',
			flex : 1,
			dataIndex : 'roleName',
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
			pagable:false,
		    multiSelect: true,
		    url: __ctxPath + '/sys/role/import/findalltmpsysrolesgrid.f',
		    storeAutoLoad: false,
			border:false,
			rowLines:true,//显示横向表格线
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