/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.check.yearcheck.tidy.YearCheckResultTidyOneGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.yearCheckResultTidyOneGrid',
	requires: [
    ],
	openCheckView:function (orgId){
		var me = this;
		var buttons;
		if(me.isApprove)
			{
	me.detailGrild = Ext.create('FHD.view.check.yearcheck.tidy.YearCheckResultDetailGrid', {
			height : 500
		});
			buttons=[{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}]
			}else{
					me.detailGrild = Ext.create('FHD.view.check.yearcheck.tidy.YearCheckResultTidyDetailGrid', {
			height : 500
		});
			buttons=[
				{
					text: '修改',
					handler:function(){
					var modified=me.detailGrild.getStore().getModifiedRecords();
					var jsonArray = [];
					Ext.each(modified, function(m) {
						jsonArray.push(m.data);
						})
					var data = Ext.JSON.encode(jsonArray);
						FHD.ajax({
				            url: __ctxPath + '/check/yearcheck/savaMarkInfo.s',
				            params: {
				            	businessId:me.businessId,
		    					executionId:me.executionId,
				            	data:data
				            },
				            callback: function (data) {
				            	me.store.load();
				            	Ext.MessageBox.alert('修改信息','修改成功');
				            	me.formwindow.close();
				            	me.store.load();
				            	//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
				            }
				        });
					}
				},     
    			{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    	    ]

			}
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
			buttons: buttons
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
							flex : 0.2
						},{
							header : '自评分数',
							dataIndex : 'owenScore',
							sortable : true,
							flex : 0.2
						},{
							header : '审计处得分',
							dataIndex : 'auditScore',
							sortable : true,
							flex : 0.2
						},{
							header : '专项评分',
							dataIndex : 'riskScore',
							sortable : true,
							flex : 0.2
						},{
							header : '最终得分',
							dataIndex : 'finalScore',
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