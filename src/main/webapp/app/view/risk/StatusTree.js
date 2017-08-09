Ext.define('FHD.view.risk.StatusTree',{
	extend: 'FHD.ux.TreePanel',
	alias: 'widget.fhdzztreepanel',
	
	/**
	 * public
	 * 接口方法
	 */
	itemclick: function(tablepanel, record, item, index, e, options){},
	firstNodeClick: function(store,records,successful,eOpts){},
	
	type : '',//solution应对库，history历史事件库
	
	initComponent:function(){
		var me = this;
		
		//初始化
		me.root = {
	        "id": "root",
	        "text": "状态",
	        "dbid": "root",
	        "leaf": false,
	        "code": "root",
	        "type": "root",
	        "expanded": true
	    };
	    
	    if(me.type == 'solution'){
	    	me.url = __ctxPath + '/response/gettreeresponsecount.f';
	    }else if(me.type == 'history'){
	    	me.url = __ctxPath + '/historyevent/gettreehistorycount.f?schm='+me.typeId;
	    }else{
	    	me.url = __ctxPath + '/app/view/risk/StatusTree.json';
	    }
        
		Ext.apply(me,{
			rootVisible: false,
			expandable : false,
			rowLines:false,
			listeners: {
                itemclick: function (tablepanel, record, item, index, e, options) {
                	me.itemclick(tablepanel, record, item, index, e, options);
                },
                load: function(store,records,successful,eOpts){
                	me.firstNodeClick(store,records,successful,eOpts);
                }
            }
		});
		me.callParent(arguments);
	}
});