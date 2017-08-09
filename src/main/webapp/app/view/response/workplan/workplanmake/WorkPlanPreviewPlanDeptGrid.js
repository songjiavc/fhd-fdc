/**
 * 评估范围预览列表
 */
Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanPreviewPlanDeptGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.workPlanPreviewPlanDeptGrid',
	requires: [
       		'FHD.view.response.workplan.workplanmake.WorkPlanPreviewGrid'
    ],
	
    seeRisksInfoByDeptId: function(seeAll){
		//查看选中部门的风险明细
    	var me = this;
    	var workPlanMakePreview = me.up('workPlanMakePreview');
    	
		var workPlanPreviewGrid = Ext.create('FHD.view.response.workplan.workplanmake.WorkPlanPreviewGrid',{
			height:600,
			width:1100,
			workPlanMakeEditMain:workPlanMakePreview.workPlanMakeEditMain,
			listeners:{
				selectionchange: function(){
					//Ext.getCmp('workplan_previewgrid_delete').setDisabled(true);//隐藏删除按钮
				}
			}
		});
		workPlanPreviewGrid.columns[5].hidden = true;//隐藏操作列
		//隐藏添加按钮
		//Ext.getCmp('workplan_previewgrid_add').setDisabled(true);
		if (workPlanPreviewGrid.down("[name='workplan_previewgrid_delete']")) {
            workPlanPreviewGrid.down("[name='workplan_previewgrid_delete']").setVisible(false);
        }
        if (workPlanPreviewGrid.down("[name='workplan_previewgrid_add']")) {
            workPlanPreviewGrid.down("[name='workplan_previewgrid_add']").setVisible(false);
        }
		var planId = workPlanMakePreview.businessId;//计划id
		
		if(!seeAll){//是否查看全部
			var selection = me.getSelectionModel().getSelection();
			workPlanPreviewGrid.deptId = selection[0].get('id');//部门id
			workPlanPreviewGrid.reloadData(planId,workPlanPreviewGrid.deptId);
		}else{
			workPlanPreviewGrid.reloadData(planId,null);
		}
		
		
		var win = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		//maximized: true,//最大化
    		maximizable: true,
    		width:1110,
    		height:600,
        	items:[workPlanPreviewGrid]
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
        		   {text:'查看全部', iconCls: 'icon-scorecards', handler:function(){
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