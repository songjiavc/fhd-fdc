/**
 * 评估范围预览列表
 */
Ext.define('FHD.view.risk.assess.formulatePlan.FormulataPreviewPlanDeptGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.formulataPreviewPlanDeptGrid',
	requires: [
       		'FHD.view.risk.assess.formulatePlan.FormulatePlanPreviewGrid'
    ],
	
    seeRisksInfoByDeptId: function(seeAll){
		//查看选中部门的风险明细
    	var me = this;
    	var formulateplanpreview = me.up('formulateplanpreview');
    	var planId = formulateplanpreview.businessId;//计划id
		var formulatePlanPreviewGrid = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanPreviewGrid',{
			height:600,
			width:1100,
			businessId:planId,
			formulatePlanMainPanel:formulateplanpreview.formulatePlanMainPanel,
			listeners:{
				selectionchange: function(){
					formulatePlanPreviewGrid.down("[name='risk_grid_delete']").setDisabled(true);
				}
			}
		});
		formulatePlanPreviewGrid.columns[5].hidden = true;//隐藏操作列
		//隐藏添加按钮
		formulatePlanPreviewGrid.down("[name='risk_grid_add']").setDisabled(true);
		
		if(!seeAll){//是否查看全部
			var selection = me.getSelectionModel().getSelection();
			formulatePlanPreviewGrid.deptId = selection[0].get('id');//部门id
			formulatePlanPreviewGrid.reloadData(planId,formulatePlanPreviewGrid.deptId);
		}else{
			formulatePlanPreviewGrid.reloadData(planId,null);
		}
		
		
		var win = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		//maximized: true,//最大化
    		maximizable: true,
    		width:1110,
    		height:600,
        	items:[formulatePlanPreviewGrid]
		});
    	win.show();
	},
    
	initComponent:function(){
		var me=this;
		me.cols=[
        	{header: '部门名称', dataIndex: 'deptName', sortable : false, flex: 1 },
        	{header: '风险数量', dataIndex: 'riskCournts', sortable : false, flex: 1 },
			{dataIndex : 'id', hidden : true}, 
			{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >查看明细</a>&nbsp;&nbsp;&nbsp;"	//
				},
				listeners:{
	        		click:function(){
	        			me.seeRisksInfoByDeptId(false);
    				}
        		}
			}
		];
		
		me.tbar = [
        		   {/*btype: 'custom',*/ text:'查看全部', iconCls: 'icon-scorecards', handler:function(){
        		   		me.seeRisksInfoByDeptId(true);
        		   }}
                   ];
        
        Ext.apply(me, {
            cols:me.cols,
            storeAutoLoad: false,
            tbarItems:me.tbar,
		    border: true,
		    columnLines: false,
		    checked: false,
		    pagable : false,
		    searchable : true
        });
                   
		me.callParent(arguments);
	}
	
});