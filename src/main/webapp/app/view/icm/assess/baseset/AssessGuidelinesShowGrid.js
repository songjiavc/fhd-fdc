Ext.define('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.assessguidelinesshowgrid',
	
	url: __ctxPath + '/icm/assess/baseset/findGuidelinesPropertyByAssessPlanId.f',
	
	storeGroupField: 'assessGuidelinesType',
	pagable:false,
	checked:false,
	
	initComponent: function() {
    	var me = this;
    	
    	Ext.apply(me,{
    		cols: [
				{
				    header: '缺陷影响程度',
				    dataIndex: 'type',
				    sortable: true,
				    flex: 3,
				    renderer:function(value,metaData,record,colIndex,store,view) { 
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return value;  
					}
				},
				{
				    header: '描述',
				    dataIndex: 'content',
				    sortable: true,
				    flex: 10,
				    renderer:function(value,metaData,record,colIndex,store,view) { 
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return value;  
					}
				},
				{
				    header: '评价标准模板类型',
				    dataIndex: 'assessGuidelinesType',
				    sortable: true,
				    flex: 1,
				    renderer:function(value,metaData,record,colIndex,store,view) { 
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return value;  
					}
				},
				{
					dataIndex:'id',
					hidden:true
				},
				{
					dataIndex:'assessGuidelinesId',
					hidden:true
				},
		        {
		        	dataIndex:'planId',
		        	hidden:true
		        }
    		]
    	});
        
    	me.callParent(arguments);
	},
    //重新加载数据
    reloadData: function() {
    	var me = this;
    	
    	me.store.load();
    }
});