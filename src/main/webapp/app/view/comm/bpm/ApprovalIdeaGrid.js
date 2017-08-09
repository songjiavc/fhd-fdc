/*
 * 评价流程的任务分配可编辑列表
 * */
Ext.define('FHD.view.comm.bpm.ApprovalIdeaGrid',{
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.approvalideagrid',
	
	url:__ctxPath + "/jbpm/processInstance/jbpmHistActinstPage.f",
	extraParams:{
		executionId:'',
		dbversion:1
	},
	multiSelect:false,
	pagable:false,
	autoScroll:true,
	checked:false,
	
	initComponent : function() {
		var me = this;
		if(me.executionId){
			me.extraParams.executionId=me.executionId;
		}else{
			me.extraParams.executionId="null";
			/**
			 * 暂时注释待发布后恢复
			 */
			/*FHD.alert("操作失败！");*/
		}
		me.cols=[
		{dataIndex:'id',hidden:true},
		{dataIndex:'dbversion',hidden:true},
		{dataIndex:'assigneeRealCode',hidden:true},
		{
            header: '流程节点名称',
            dataIndex: 'activityName',
            sortable: true,
            flex: 2
        },
        {
            header: '审批人',
            dataIndex: 'assigneeRealname',
            sortable: false,
            flex: 1,
			renderer:function(value,metaData,record,colIndex,store,view) {
				return value+"("+record.get('assigneeRealCode')+")";
			}
        },
        {
            header: '审批操作',
            dataIndex: 'ea_Operate',
            sortable: false,
            flex: 1,
            renderer:function(value){
            	if(value=='yes'){
            		return '同意';
            	}else if(value=='no'){
            		return '不同意';
            	}
            }
        },
        {
            header: '审批意见',
            dataIndex: 'ea_Content',
            sortable: false,
            flex: 5
        },
        {
            header: '审批时间',
            dataIndex: 'endStr',
            sortable: false,
            flex: 2
        }];
		
		me.callParent(arguments);
	},
	reloadData:function(){
		var me=this;
		
		me.store.load();
	}
});