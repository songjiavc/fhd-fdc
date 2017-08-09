/**
 * 结果主面板
 * 
 * @author 王鑫
 */
Ext.define('FHD.view.kpi.cmp.kpi.result.ResultMainPanel', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.resultManPanel',
	border:false,
	
	
	getResultCardPanel : function(param){
		var me = this;		
		me.resultCardPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.ResultCardPanel',{
				region:'center',
				pcontainer : me,
				goback: me.goback
			}).load(param);
	},
	
	load : function(param){
		var me = this;
		me.getResultCardPanel(param);
		if(me.resultCardPanel != null){
			me.removeAll(true);
		}
		me.add(me.resultCardPanel);		
		return me;
	},
	goback:function(){
		
	},
	// 初始化方法
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			border : false,
			items : [],
			listeners: {
                destroy: function (me, eOpts) {
                    me.remove(me.resultCardPanel,true);
                    me = null;
                    if (Ext.isIE) {
                        CollectGarbage();
                    }
                }
            }
		});

		me.callParent(arguments);
	}
});