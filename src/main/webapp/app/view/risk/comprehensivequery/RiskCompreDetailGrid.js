Ext.define('FHD.view.risk.comprehensivequery.RiskCompreDetailGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskcompredetailgrid',
	
    //重新加载数据方法
    reloadData: function(verId,rbsId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/risk/compre/queryrisksbyrbsandverid.f';
    	me.store.proxy.extraParams.verId = verId;//版本标识
 		me.store.proxy.extraParams.rbsId = rbsId;//一级风险标识
 		me.store.load();
    },
    
    backGrid: function(){
    	var me = this;
    	me.up('riskcomprecardmain').showRiskCompreQuary();
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