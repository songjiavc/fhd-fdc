/**
 * 结果主面板
 * 
 * @author 王鑫
 */
Ext.define('FHD.view.kpi.cmp.kpi.memo.MemoMainPanel', {
	extend : 'Ext.panel.Panel',
	layout:{
     	    	type:'hbox',
                align:'top'
     	    },
				 
	getData: function(kgrid){
		var me = this;
		me.queryMemoUrl = __ctxPath + "/kpi/kpimemo/kpimemolistloader.f";//树查询url
		me.memorecordgrid.store.proxy.url = me.queryMemoUrl;//动态赋给机构列表url
  		me.memorecordgrid.store.proxy.extraParams.kgrid = kgrid;
  		me.memorecordgrid.reloadData();
	},
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		me.memorecordgrid = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoRecordGrid',{
        	border:false,
        	flex: 1,
        	pagable:false,
        	memomainpanel: me
        	
        });
        me.memopanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoPanel',{
        	border:false,
        	flex: 1,
        	memomainpanel: me,
        	kgrid: me.kgrid
        });
       
		Ext.apply(me, {
			border : false,
			items : [me.memorecordgrid ,me.memopanel]
		});

		me.callParent(arguments);
	}
});