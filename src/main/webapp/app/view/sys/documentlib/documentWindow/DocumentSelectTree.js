Ext.define('FHD.view.sys.documentlib.documentWindow.DocumentSelectTree', {
	extend : 'Ext.container.Container',
	alias : 'widget.documentSelectTree',
	border:true,
	split: true,
    collapsible: false,
    width: 265,
    maxWidth: 300,
	layout : {
		type : 'accordion',
		titleCollapse: true,
        animate: false,
        collapseFirst: true,
        activeOnTop: false			
	},
	/**
	 * 知识树是否显示
	 */
	knowtreevisable:true,
	
	/**
	 * 案例树是否显示
	 */
	casetreevisable:true,
	
	/**
	 * 制度树是否显示
	 */
	systemtreevisable:true,
	
	documentEventUrl: '/sys/document/finddocumentlabraryspage.f',//查询列表url
	
	reloadStore:function(url,extraParams){
		var me = this;
		//alert(extraParams.id);
		me.findParentBy(function(container, component){
			//获得指标列表的grid
			var candidategrid = container.candidategrid;
			//参数赋值
			candidategrid.store.proxy.url = url;
			candidategrid.store.proxy.extraParams = extraParams; 
			//重新加载grid中的数据
			candidategrid.store.load({
				callback: function(records, operation, success) {
					me.face.candidatelabel.setText('(' + records.length + ')');
				}
			});
			
		});
	},
	init:function(){
		var me = this;

		/**
		 * 知识树
		 */
		if(me.knowtreevisable){
			
			//初始化参数
	    	var extraParams = {};
	    	extraParams.rbs = true;
	    	extraParams.canChecked = false;
	    	me.knowtree = Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentTreePanel', {
	    		typeId: 'document_library_knowledge',
	    		title: '知识库',
	    		listeners: {
	   				select: function(t,r,i,o){
	   					var typeId = r.data.id;
	   					var type = r.data.type;
	                	var url = __ctxPath + me.documentEventUrl;
	        			var extraParams = {typeId:typeId, type:type};
	                	me.reloadStore(url,extraParams);
	                }
	            }
	        });
			me.add(me.knowtree);
		}
		
		/**
		 * 加载案例树
		 */
		if(me.casetreevisable){
			me.casetree = Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentTreePanel',{
				typeId: 'document_library_case',
				title: '案例库',
				listeners: {
	   				select: function(t,r,i,o){
	   					var typeId = r.data.id;
	   					var type = r.data.type;
	                	var url = __ctxPath + me.documentEventUrl;
	        			var extraParams = {typeId:typeId, type:type};
	                	me.reloadStore(url,extraParams);
	                }
	            }
		    });
			me.add(me.casetree);
		}
		
		
		/**
		 * 加载制度树
		 */
		if(me.systemtreevisable){
			me.systemtree = Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentTreePanel',{
				typeId: 'document_library_system',
				title: '制度库',
				listeners: {
	   				select: function(t,r,i,o){
	   					var typeId = r.data.id;
	   					var type = r.data.type;
	                	var url = __ctxPath + me.documentEventUrl;
	        			var extraParams = {typeId:typeId, type:type};
	                	me.reloadStore(url,extraParams);
	                }
	            }
		    });
			me.add(me.systemtree);
		}
		
	},
	
	initComponent : function() {
		
		var me = this;
		Ext.applyIf(me, {
			
		});
		
		me.callParent(arguments);
		me.init();
	}
});