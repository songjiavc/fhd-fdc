Ext.define('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityRelaGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskIntegralityRelaGrid',
 	requires: [
	],
	
	//返回上级页面
	back: function(){
		var me = this;
		var card = me.up('riskIntegralityMainCard');
		card.showRiskIntegralityMain();
	},
	
	riskDetailForm: function(){
		var me = this;
		me.riskDetailWindowForm = Ext.create('FHD.view.SASACdemo.homepage.RiskDetailWindowForm');
		me.riskDetailWindowForm.items.items[0].items.items[0].setValue(me.riskname);//风险名称
		
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
        me.id = 'riskIntegralityRelaGrid_id';
        
        var cols = [
	        {
	            header: "企业名称",
	            dataIndex: 'companyName',
	            sortable: true,
	            flex: 1
	        },{
	        	header: "所属行业",
				dataIndex:'kind',
				hidden:false,
				flex: 1
			},{
	        	header: "预警状态",
				dataIndex:'riskStatus',
				hidden:false,
				flex: 1
			},{
	            header: "情况说明",
	            dataIndex: 'event',
	            sortable: false,
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view){
	            	return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').riskDetailForm()\">查看风险详情</a>"	
				}
	        }
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/app/view/SASACdemo/RiskIntegrality/riskIntegralityrelagrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    bbar: {items: [ '->',{  text: '返回', //返回按钮
						            iconCls: 'icon-control-repeat-blue',
						            handler: function () {
						            	me.back()
						            }
					            }
							]
					},
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});