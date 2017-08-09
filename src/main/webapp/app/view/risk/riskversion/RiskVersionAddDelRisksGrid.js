Ext.define('FHD.view.risk.riskversion.RiskVersionAddDelRisksGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskversionadddelrisksgrid',
	
    //重新加载数据方法
    reloadData: function(verId,isAdd){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/risk/riskhistory/findaddrisksbyverid.f';
    	me.store.proxy.extraParams.verId = verId;//版本标识
    	me.store.proxy.extraParams.isAdd = isAdd;//是新增还是删除
 		me.store.load();
    },
    
    backGrid: function(){
    	var me = this;
    	me.up('riskversioncardmain').showVersionGrid();
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
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: false,
	            flex: 3
	        },{
	            header: "风险编号",
	            dataIndex: 'riskCode',
	            sortable: false,
	            flex: 1
	        },
	        {
	            header: "风险描述",
	            dataIndex: 'riskDesc',
	            sortable: false,
	            flex: 2
	        },
	        {
	            header: "责任部门/人",
	            dataIndex: 'mainDeptName',
	            sortable: false,
	            flex: 1
	        },{
	            header: "相关部门/人",
	            dataIndex: 'relaDeptName',
	            sortable: false,
	            flex: 1
	        },{
	            header: "风险水平",
	            dataIndex: 'scoreStr',
	            sortable: false,
	            flex: 1
	        }
			
        ];
       
        Ext.apply(me,{
        	cols:cols,
        	tbarItems:[{
        		text:'返回',
                iconCls: 'icon-control-repeat',
    			handler:function(){
    				me.backGrid();
    			}
			}],
		    border: true,
		    checked : true,
		    autoDestroy: true,
		    pagable : false,
		    searchable: false
        });
       
        me.callParent(arguments);

    }

});