Ext.define('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskIntegralityGrid',
 	requires: [
 	           'FHD.view.risk.assess.utils.GridCells'
	],
	
	showAll: function(riskname){
		var me = this;
		var card = me.up('riskIntegralityMain').up('riskIntegralityMainCard');
		card.riskIntegralityRelaGrid.setTitle(riskname);
		card.riskIntegralityRelaGrid.riskname = riskname;
		card.showRiskIntegralityRelaGrid();
	},
	
	//查看相关风险事件表单
	relaForm: function(riskName){
		var me = this;
		me.riskDetailWindowForm = Ext.create('FHD.view.SASACdemo.homepage.RiskDetailWindowForm');
		me.riskDetailWindowForm.items.items[0].items.items[0].setValue(riskName);//风险名称
		
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
        
        var cols = [
	        {
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: true,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').showAll('"+record.get('riskName')+"')\">"
	            			+value+"</a>&nbsp;&nbsp;&nbsp;"	
				}
	        },{
	        	header: "风险状态",
				dataIndex:'riskStatus',
				hidden:false,
				flex: 1
			},{
	            header: "相关风险事件",
	            dataIndex: 'event',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\"  onclick=\"Ext.getCmp('" + me.id + "').relaForm('"+record.get('riskName')+"')\">"
	            			+value+"</a>&nbsp;&nbsp;&nbsp;"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/app/view/SASACdemo/RiskIntegrality/RiskIntegralitygrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    searchable:false
        });
       
        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [1,2]);
        });
    }

});