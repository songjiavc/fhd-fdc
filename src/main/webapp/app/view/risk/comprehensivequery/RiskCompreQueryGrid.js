Ext.define('FHD.view.risk.comprehensivequery.RiskCompreQueryGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskcomprequerygrid',
	
    //重新加载数据方法
    reloadData: function(form){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/risk/compre/queryriskrbsbyverid.f';
 		me.store.proxy.extraParams = form.getValues();//版本标识
 		me.store.load();
    },
    
    //查看版本风险明细
    viewDetails: function(rowIndex){
    	var me = this;
    	me.getSelectionModel().select(rowIndex);
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var versionCard = me.up('riskcomprecardmain');
    	versionCard.riskRbsDetail.reloadData(selection[0].get('verId'),selection[0].get('riskId'));//版本id
    	versionCard.showRiskRbsDetail();
    },
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
			{
				header: "verId",
				dataIndex:'verId',
				hidden:true
			},
			{
				header: "riskId",
				dataIndex:'riskId',
				hidden:true
			},
	        {
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: false,
	            flex: 1
	        },
	        {
	            header: "风险编号",
	            dataIndex: 'riskCode',
	            sortable: false,
	            flex: 1
	        },
	        {
	            header: "责任部门/人",
	            dataIndex: 'mainName',
	            sortable: false,
	            flex: 1
	        },{
	            header: "相关部门/人",
	            dataIndex: 'relaName',
	            sortable: false,
	            flex: 1
	        },{
	            header: "风险水平",
	            dataIndex: 'score',
	            sortable: false,
	            flex: 1
	        }
			/*{header:'操作',dataIndex:'cz',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
			       xtype:'actioncolumn',
			       items: [{
		                icon: __ctxPath+'/images/icons/application_form_magnify.png',  // Use a URL in the icon config
		                tooltip: '查看明细',
		                handler: function(grid, rowIndex, colIndex) {
		                    me.viewDetails(rowIndex);
		                }
		            }]
			  }*/
        ];
       
        Ext.apply(me,{
        	cols:cols,
		    border: true,
		    checked : true,
		    autoDestroy: true,
		    pagable : true,
		    searchable: false
        });
       
        me.callParent(arguments);

    }

});