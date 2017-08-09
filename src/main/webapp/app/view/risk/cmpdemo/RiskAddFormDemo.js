Ext.define('FHD.view.risk.cmpdemo.RiskAddFormDemo', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.riskaddformdemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.addForm = Ext.create('FHD.view.risk.cmp.RiskAddForm', {
        	title:'添加风险(全)',
			type:'rbs',	//如果是re,上级风险只能选择叶子节点
			pid:'CW01',
			border:false,
			//hiddenSaveBtn:false,
			//state:2,
			callback:function(data){
				alert('风险添加成功'+data.id);
			}
		});
        
        me.addShortForm = Ext.create('FHD.view.risk.cmp.RiskShortAddForm', {
        	title:'添加风险(事件)',
			type:'re',	//如果是re,上级风险只能选择叶子节点
			pid:'CW01',
			border:false,
			callback:function(data){
				alert('风险添加成功'+data.id);
			}
		});

        Ext.apply(me, {
            autoScroll: false,	//不显示滚动条
            border: false,
            bodyPadding: "5 5 5 5",
            layout:'fit',
            items: [me.addShortForm,me.addForm]
        });
        
        me.callParent(arguments);
      
    }
});