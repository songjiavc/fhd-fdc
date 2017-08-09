Ext.define('FHD.view.SASACdemo.SASACRiskGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.sasacRiskGrid',
 	requires: [
 	           'FHD.view.risk.assess.utils.GridCells'
	],
	
	showAll: function(name){
		var me = this;
		var card = me.up('homePageMainPanel').up('sasaccardpanel');
		card.showRiskCategoryDetail();
		card.riskCategoryDetail.setTitle('风险名称:'+name);
		card.riskCategoryDetail.riskName = name;
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.id = 'sasacRiskGrid_id';
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "风险类别",
	            dataIndex: 'riskKind',
	            sortable: true,
	            flex: 1
	        },
	        {
	            header: "风险名称",
	            dataIndex: 'riskname',
	            sortable: true,
	            flex: 1
	        },{
				dataIndex:'inValue',
				hidden:false,
				width: 30
			},{
	            header: "风险状态",
	            dataIndex: 'riskStatues',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').showAll('"+record.get('riskname')+"')\">查看明细</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	region:'east',
        	title:'按风险类别预警',
        	margin: '1 1 1 1',
        	flex: 1,
        	url : __ctxPath + '/app/view/SASACdemo/riskgrid.json',//查询列表url
        	cols:cols,
		    border: true,
		    checked : false,
		    pagable : true,
		    searchable:false
        });
       
        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [2]);
        });
    }

});