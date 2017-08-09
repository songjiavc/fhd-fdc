Ext.define('FHD.view.SASACdemo.homepage.KpiRiskWaringGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.kpiRiskWaringGrid',
 	requires: [
	],
	
	
    // 初始化方法
    initComponent: function() {
        var me = this;
       	var store = Ext.create('Ext.data.Store', {
		    storeId:'Store',
		    fields:['month'],
		    groupField: 'department',
		    data: {'months':[
				        {"month":'一月'},
						{"month":'二月'},
						{"month":'三月'},
						{"month":'四月'},
						{"month":'五月'},
						{"month":'六月'},
						{"month":'七月'},
						{"month":'八月'},
						{"month":'九月'},
						{"month":'十月'},
						{"month":'十一月'},
						{"month":'十二月'}
				    ]},
				    proxy: {
				        type: 'memory',
				        reader: {
				            type: 'json',
				            root: 'months'
				        }
				    }
		});
        Ext.apply(me,{
	    	columnLines: false,
	    	border: false,
	    	region: 'center',
	    	store: Ext.data.StoreManager.lookup('Store'),
	    	//url: __ctxPath + '/app/view/SASACdemo/homepage/kpiriskwarngrid.json',
	        columns: [{
			            text     : '月份',
			            //columnwidt: '10%',
			            flex: 1,
			            sortable : false,
			            dataIndex: 'month'
				        }, {
				            text: '总收入',
				            columns: [{
				                text     : '实际值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '下月预测值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '预警状态',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '季度末预测值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '预警状态',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }]
					        }, {
					            text: '总利润',
					            columns: [{
				                text     : '实际值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '下月预测值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '预警状态',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '季度末预测值',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }, {
				                text     : '预警状态',
				                columnwidt: '20%',
				                sortable : true,
				                dataIndex: ''
				            }]
				        }]
	        });
       
        me.callParent(arguments);
    }

});