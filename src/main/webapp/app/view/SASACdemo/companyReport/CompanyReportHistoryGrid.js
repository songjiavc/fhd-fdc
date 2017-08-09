Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportHistoryGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.companyReportHistoryGrid',
 	requires: [
 	           
	],
	
	//查看历史报告
	historyReport: function(){
		var me = this;
		me.historyReportForm = Ext.create('FHD.view.SASACdemo.companyReport.HistoryReportForm');
		
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'历史事件信息',
   		 	height: 550,
    		width: 1000,
    		maximizable: true,
   			layout: 'fit',
   			buttonAlign: 'center',
   			fbar: [
   					{ xtype: 'button', text: '保存', handler:function(){
   						FHD.notification('操作成功！','提示');
   						me.preWin.hide();}
   					}
				  ],
    		items: [me.historyReportForm]
		}).show();
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
	        {
	        	header: "结果表现的原因分析",
				dataIndex:'result',
				hidden:false,
				flex: 2
			},{
	        	header: "应对情况说明",
				dataIndex:'',
				hidden:false,
				flex: 2
			},{
	            header: "历史事件报告",
	            dataIndex: '',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').historyReport()\">历史事件报告单</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/app/view/SASACdemo/companyReport/companyhistorygrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});