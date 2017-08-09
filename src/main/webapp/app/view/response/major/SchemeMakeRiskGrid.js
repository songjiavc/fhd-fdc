Ext.define('FHD.view.response.major.SchemeMakeRiskGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.schememakeriskgrid',
 	requires: [
	],
	
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
	            header: "重大风险",
	            dataIndex: 'riskName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "状态",
	            dataIndex: 'riskName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "操作",
	            dataIndex: 'riskName',
	            sortable: true,
	            width:40,
	            flex:2
	        }
        ];
        Ext.apply(me,{
        	region:'center',
        	url : __ctxPath + "/majorResponse/getRiskTask",
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				var cardPanel = me.up('schememakecard');
        				cardPanel.changeLayout("form");
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : true
        });
        me.callParent(arguments);

    }

});