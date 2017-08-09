Ext.define('FHD.view.SASACdemo.SASACCompanyWarnGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.sasacCompanyWarnGrid',
 	requires: [
	],
	
	//查看风险状态详细
	showAll: function(name){
		var me = this;
		var card = me.up('homePageMainPanel').up('sasaccardpanel');
		card.sasackpiRiskWaring.setTitle('企业名称:'+name);
		card.showSasackpiRiskWaring();
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.id = 'sasacCompanyWarnGrid_id';
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "企业名称",
	            dataIndex: 'name',
	            sortable: true,
	            flex: 1
	        },
	        {
	            header: "所属行业",
	            dataIndex: 'kind',
	            sortable: true,
	            flex: 1
	        },{
				dataIndex:'inValue',
				hidden:false,
				width: 30
			},{
	            header: "收入风险状态",
	            dataIndex: 'income',
	            sortable: false,
	            width:40,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
					return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').showAll('"+record.get('name')+"')\">查看明细</a>&nbsp;&nbsp;&nbsp;"	
				}
	        },{
				dataIndex:'pValue',
				hidden:false,
				width: 30
			},{
	            header: "利润风险状态",
	            dataIndex: 'profit',
	            sortable: false,
	            width:40,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
					return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').showAll('"+record.get('name')+"')\">查看明细</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	region:'center',
        	title:'按企业预警',
        	flex: 1,
        	margin: '1 1 1 1',
        	url : __ctxPath + '/app/view/SASACdemo/companywarn.json',//查询列表url
        	cols:cols,
		    border: true,
		    checked : false,
		    pagable : true,
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});