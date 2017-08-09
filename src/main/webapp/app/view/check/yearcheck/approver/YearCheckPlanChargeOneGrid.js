/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.check.yearcheck.approver.YearCheckPlanChargeOneGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.yearCheckPlanChargeOneGrid',
	requires: [
    ],
 
	openCheckView:function (orgId){
		var me = this;
    	me.detailGrild = Ext.create('FHD.view.check.yearcheck.approver.YearCheckPlanChargeDetailGrid', {
			height : 500
		});
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			width:800,
			height:500,
			title : '考评信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.detailGrild],
			buttons : [{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}]
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
							flex :0.3
						}, {
							header : '评价人',
							dataIndex : 'empName',
							sortable : true,
							flex :0.2
						}, {
							header : '自评分数',
							dataIndex : 'owenScore',
							sortable : true,
							flex : 0.2
						},{
							header : '审计处得分',
							dataIndex : 'auditScore',
							sortable : true,
							flex : 0.2
						}, {
							header : "操作",
							dataIndex : 'id',
							sortable : true,
							flex : 0.1,
							renderer : function(value, metaData, record,
									colIndex, store, view) {
								return "<a href=\"javascript:void(0);\" "
										+ "onclick=\"Ext.getCmp('" + me.id + "').openCheckView('"
										+ value +"')\">查看明细</a>";
							}
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