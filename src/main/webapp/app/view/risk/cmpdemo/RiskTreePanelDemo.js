Ext.define('FHD.view.risk.cmpdemo.RiskTreePanelDemo', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.risktreepaneldemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.riskNormalTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel', {
        	region:'west',
        	width:'33%'
        });
		
        me.riskLightTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel', {
        	region:'center',
        	width:'34%',
        	showLight : true
        });
		
        //初始化
        me.riskCheckboxTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel', {
        	region:'east',
        	width:'33%',
        	type:'checkboxtree',
        	cascade : true,	//false不进行级联
        	buttonAlign:'center',
        	buttons:[{
        		text:'设置初始值',
        		handler:function(){
        			me.riskCheckboxTree.store.proxy.extraParams.checkedIds = 'CW,CW01';
        			me.riskCheckboxTree.store.load();
        		}
        	},{
        		text:'获取选中值',
        		handler:function(){
        			var ids = me.riskCheckboxTree.getCheckedIds();
        			//alert(ids);
        		}
        	}]
        });
		
        Ext.applyIf(me, {
            layout: 'border',
            items: [me.riskNormalTree,me.riskLightTree,me.riskCheckboxTree]
        });
        
        me.callParent(arguments);
      
    }
});