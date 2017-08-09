Ext.define('FHD.view.SASACdemo.homepage.RiskCategoryGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskCategoryGrid',
 	requires: [
	],
	
	//弹出预览窗口
	showAll: function(){
		var me = this;
		me.riskName = me.up('riskCategoryDetail').riskName;
		me.riskDetailWindowForm = Ext.create('FHD.view.SASACdemo.homepage.RiskDetailWindowForm');
		me.riskDetailWindowForm.items.items[0].items.items[0].setValue(me.riskName);//风险名称
		
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'重大风险预测分析',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.riskDetailWindowForm]
		}).show();
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.id = 'riskCategoryGrid_id';
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "企业名称",
	            dataIndex: 'companyName',
	            sortable: true,
	            flex: 1
	        },
	        {
	            header: "所属行业",
	            dataIndex: 'kind',
	            sortable: true,
	            flex: 1
	        },{
	        	header: "风险对收入影响",
				dataIndex:'riskToincome',
				hidden:false,
				flex: 1
			},{
	        	header: "风险对利润影响",
				dataIndex:'riskToprofit',
				hidden:false,
				flex: 1
			},{
	            header: "操作",
	            dataIndex: 'detail',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').showAll()\">查看明细</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	region:'east',
        	title:'风险预警分析',
        	flex: 1,
        	url : __ctxPath + '/app/view/SASACdemo/homepage/riskcategorygrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});