Ext.define('FHD.view.sys.organization.import.OrgImpValidateGridPanel', {
	extend: 'FHD.ux.GridPanel',
	alias: 'widget.orgimpvalidategridpanel',
	
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
		//机构列表项
		me.cols =[{	
			dataIndex : 'id',
			invisible : true
		},{
			header : '行号',
			width : 40,
			dataIndex : 'rowLine',
			sortable : true	
		},{
			text : '部门名称',
			flex : 1,
			dataIndex : 'orgName',
			sortable : true
		},{
			text : '机构类型',
			flex : 1,
			dataIndex : 'orgType',
			sortable : true,
			renderer:function(dataIndex) { 
				  if(dataIndex == "0orgtype_c"){
					  return "总公司";
				  }else if(dataIndex == "0orgtype_d"){
					  return "总公司部门";
				  }else if(dataIndex == "0orgtype_sc"){
					  return "分公司";
				  }else if(dataIndex == "0orgtype_sd"){
					  return "分公司部门"; 
				  }
			}
		},{
			text : '机构层级',
			flex : 1,
			dataIndex : 'orgLevel',
			sortable : true
		},{
			text : '排序',
			flex : 1,
			dataIndex : 'sn',
			sortable : true
		},{
			text : '校验结果',
			flex : 2,
			dataIndex : 'error',
			sortable : true,
			renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
		}];
		
		Ext.apply(me, {
			pagable: false,
			url : __ctxPath + '/sys/organization/import/findalltmpsysorganizations.f',//搜索框url
			storeAutoLoad: false,
			multiSelect: true,
			border: false,
			rowLines: true,//显示横向表格线
			checked: false, //复选框
			autoScroll:true,
			viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return record.get("error") ? "row-s" : "";
                }
            }
		});
		me.callParent(arguments);
    }
});