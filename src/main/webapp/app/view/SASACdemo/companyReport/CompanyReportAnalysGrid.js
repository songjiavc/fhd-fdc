Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportAnalysGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.companyReportAnalysGrid',
 	requires: [
 	           
	],
	
	analysisFormWin: function(riskName){
		var me = this;
		me.companyReportRiskAnalyForm = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportRiskAnalyForm');
		me.companyReportRiskAnalyForm.riskName.setValue(riskName);//风险名称
		
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'风险分析表',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
   			buttonAlign: 'center',
   			fbar: [
   					{ xtype: 'button', text: '保存', handler:function(){
   						FHD.notification('操作成功！','提示');
   						me.preWin.hide();}
   					}
				  ],
    		items: [me.companyReportRiskAnalyForm]
		}).show();
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
	        {
	        	header: "影响下期目标的主要风险",
				dataIndex:'riskName',
				hidden:false,
				flex: 2
			},{
	            header: "风险分析",
	            dataIndex: '',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').analysisFormWin('"+record.get('riskName')+"')\">填写风险分析表</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/app/view/SASACdemo/companyReport/nextgrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});