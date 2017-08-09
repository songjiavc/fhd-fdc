/**
 * 预案的应对措施详细列表
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.preplan.ExePreplanRelaSolutions', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.exepreplanrelasolutions',
	cols : [],
	height : FHD.getCenterPanelHeight()/2.5,
	initComponent : function() {
		var me = this;
		me.queryUrl = 'chf/execute/preplan/queryexesolutionsbypre.f';
		me.cols = [
				   {header: 'id',dataIndex: 'id',sortable: false,flex : 1,hidden : true},
		           {header: '应对措施',dataIndex: 'name',sortable: true,flex : 1,hidden : false},
		           {header: '责任岗位',dataIndex: 'sysposi',sortable: false,flex : 1,hidden : false},
		           {header: '实际开始时间',dataIndex: 'activeStartDate',sortable: false,flex : 1,hidden : false},
		           {header: '实际完成时间',dataIndex: 'activeFinishDate',sortable: false,flex : 1,hidden : false},
		           {header: '工作描述',dataIndex: 'desc',sortable: false,flex : 1,hidden : false}
	           ];
		Ext.apply(me, {
    		multiSelect: false,
    		checked:false,
            border:true,
            rowLines:true,//显示横向表格线
            autoScroll:true,
    		cols:me.cols,//cols:为需要显示的列
    		pagable : false
        });
		me.callParent(arguments);
	},
	reloadData : function(exeId){
		var me = this;
		me.store.proxy.url = me.queryUrl;
		me.store.proxy.extraParams.exeId = exeId;
		me.store.load();
	}
});