Ext.define('FHD.view.sys.sfindex.IndexPortalPanel', {
    extend: 'Ext.app.PortalPanel',
    alias: 'widget.indexportalpanel',

    getTools: function(){
        return [{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, header, tool){
				debugger;
                var portlet = header.ownerCt;
                portlet.setLoading('Loading...');
                Ext.defer(function() {
                    portlet.setLoading(false);
                }, 2000);
            }
        }];
    },

    //根据选中的树节点，增加portlet模块
    addPortlet: function(nodeId,title){
        var me = this;
        var isExist = false;//是否存在
        /*var j = this.getCookie();//cookie中的title
        if(j){//先判断cookie中有没有该页面
            Ext.each(Ext.decode(j),function (colJson) {
                Ext.each(colJson,function(itemJson){
                    var isT = itemJson.title;
                    if(isT == title){
                        isExist = true;
                    }
                });
            });
        }else if(me.items.items.length > 0){//判断页面初始化元素
            Ext.each(me.items.items,function (colitem) {
                Ext.each(colitem.items.items,function(item){
                    var isT = item.title;
                    if(isT == title){
                        isExist = true;
                    }
                });
            });
        }*/
       // if(!isExist){
		   var comp = Data.getComp(nodeId);
            var colmp = Ext.getCmp('col-1');
            var item={
                id: nodeId,
                title: title,
                items:Ext.create(comp)
            };
            colmp.add(item);
            colmp.doLayout();
       // }

    },

    makePortalViews:function (model) {
		Data.makePortalItems();
		var portalItems = Data.portalModel;
		for (var x=0; x<3; x++) {
			for (var t=0;t<portalItems[x].items.length;t++) {
				var pt=portalItems[x].items[t];
				pt.tools= this.getTools();
				pt.items= Ext.create(pt.comp,{ });
//				pt.listeners = {'close': Ext.bind(this.onClose,this)} ;
			}
		}
	
    },
	changePortalViews:function(items) {
		Data.initDataModelSTT();
		Data.initPortalModelItems();
		Data.initCookieData();
		for(var col=0; col<items.length; col++) {
			var row=0;
			for(row=0; row<items[col].items.length; row++) {
				var it = items[col].items.items[row];
				Data.portalModel[col].items[row] = {"title":it.title, "comp":it.comp, "tools":it.tools, "items":it.items, "listeners":"","id":it.id  };
				Data.cookieData[col][row]={"id":it.id};
				Data.setDataModelSTT(it.id); // 更新 Data.dataModel.stt

			}
		} // EOF col
		Data.saveDataToCookie();//保存cookie
	},

    initComponent: function(){
        var me = this;
		Ext.suspendLayouts();
		Data.loadDataFromCookie();
		this.makePortalViews();
		
        Ext.apply(me, {
            items: Data.portalModel,
			listeners: {
			   afterlayout: function(cmp){
				   // debugger;
				   // layout变更之后，将结果回写到数据定义中
				   this.changePortalViews(cmp.items.items);
			   }
			}			       
	   });
	   Ext.resumeLayouts(true);
        me.callParent(arguments);
    }
});