/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.check.yearcheck.plan.YearCheckPlanOwenMarkOneGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.yearCheckPlanOwenMarkOneGrid',
	requires: [
    ],
 
	openCheckView:function (orgId){
		var me = this;
    	me.detailGrild = Ext.create('FHD.view.check.yearcheck.mark.YearCheckPlanOwenDetailGrid', {
			height : 500
		});
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.detailAllForm]
		});
		me.detailGrild.store.proxy.url = __ctxPath + '/check/yearcheck/findCheckRuleByEmpId.f';
	    me.detailGrild.store.proxy.extraParams.businessId = me.businessId;
	    me.detailGrild.store.proxy.extraParams.executionId = me.executionId;
	    me.detailGrild.store.proxy.extraParams.orgId = orgId;
	    me.detailGrild.store.load();
		me.formwindow.show();
	},
	initComponent:function(){
		var me=this;
		me.cols = [{
							header : '考评部门',
							dataIndex : 'orgName',
							sortable : true,
							flex : 5
						}, {
							header : '评价人',
							dataIndex : 'empName',
							sortable : true,
							flex : 5
						}, {
							header : '自评分数',
							dataIndex : 'owenScore',
							sortable : true,
							flex : 5
						}, {
							header : "操作",
							dataIndex : 'id',
							sortable : true,
							flex : .3,
							renderer : function(value, metaData, record,
									colIndex, store, view) {
										debugger;
								return "<a href=\"javascript:void(0);\" "
										+ "onclick=\"openCheckView('"
										+ value +"')\">查看明细</a>";
							}
						}, {
							dataIndex : 'id',
							hidden : true
						}];
        
        Ext.apply(me, {
            cols:me.cols,
		    border: true,
		    columnLines: true,
		    checked: false,
		    pagable : false,
		    searchable : true,
		    autoScroll:true
        });
                   
		me.callParent(arguments);
	}
	
});