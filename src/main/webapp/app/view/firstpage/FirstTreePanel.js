/*
 * add by 宋佳
 * 
 * 登录菜单树展示
 */

Ext.define('FHD.view.firstpage.FirstTreePanel',{
	extend : 'FHD.ux.TreePanel',
	alias: 'widget.firsttreepanel',
	searchable:false,
	toolbar : false,
	expandable : false,
	hideCollapseTool : true,
	rootVisible : false,
	tools : [],
	bodyStyle: {
	    padding: '10px'
	},
	viewConfig : {
		stripeRows:false,
		style: {
		}
	},
	url : __ctxPath + '/sys/menu/getAuthorityTreeLoader.f',
    itemclickTree : function(re){
    	//undo 如果是叶子节点则展开
    	var me = this;
    	me.currentNode = re;
    	if(me.currentNode.data.leaf == false){
    		me.expandNode(me.currentNode);
    	}else{
			var url = me.currentNode.data.code;
			var temp = url.split('?');//url传参，以“？”分隔
			var paramStr ;
			var paramObj = {};
			if(temp.length>1){
				url = temp[0];
				paramStr = temp[1];
				var params = paramStr.split("&");
				var param = [];
				if(params.length > 0){
					Ext.Array.each(params,function(item,i){
						param = item.split("=");
						paramObj[param[0]] = param[1];
					});
				}
			}
			Ext.applyIf(paramObj,{
					itemId:url,
					title:  me.currentNode.data.text,
					tabTip: me.currentNode.data.text,
					autoDestroy : true,
					header :false
			});
			var centerPanel = Ext.getCmp('center-panel');
			centerPanel.removeAll(true);
			if(url.startWith('FHD')){
				/**
				 * 创建的时候去记录当前tab页签创建的组件所在的tab
				 */
				var testPanel = Ext.create(url,paramObj);
				centerPanel.add(testPanel);
				//undo 宋佳添加  判断是否有treepanel组件并且tree的布局是border，则自动隐藏menu  对于
				/*
				if(centerPanel.down('treepanel')){
					Ext.getCmp('leftMenuPanel').collapse();
				}
				*/
				var menuText = me.getExtendSelectName(me.currentNode);
				Ext.getCmp('middlePanel').setTitle(me.title+"  >  "+menuText);
				/*  undo 更新导航条保证给用户指示路径
				 *  1. 找到导航条
				 *  2. 给导航条传入输入值
				 *  3. 更新导航条
				 * */
			} else if (url.startWith('/pages')){
				centerPanel.add({
					itemId:url,
					title:  me.currentNode.data.text,
					tabTip: me.currentNode.data.text,
					layout:'fit',
					autoWidth:true,
					border:false,
					//iconCls: 'tabs',
					closable:true,
					autoLoad :{ url: __ctxPath+url,scripts: true}
				});
			}else{
				centerPanel.add({
					itemId:url,
					title:  me.currentNode.data.text,
					tabTip: me.currentNode.data.text,
					layout:'fit',
					autoWidth:true,
					border:false,
					//iconCls: 'tabs',
					closable:true,
					html : '<iframe width=\'100%\' height=\'100%\' frameborder=\'0\' src=\''+__ctxPath+url+'\'></iframe>'
					//autoLoad :{ url: 'pages/icon.jsp',scripts: true}
					//items:[{xtype:'dictTypelist'}]
				});
			}
    	}
	},
	getExtendSelectName : function(selectNode){
//		while(selectNode.get)
		var rtnTitle = "";
		while(selectNode.parentNode){
			rtnTitle =  Ext.String.insert(rtnTitle,selectNode.data.text + "  >  ",0);
			selectNode = selectNode.parentNode;
		}
		if(rtnTitle != ""){
			rtnTitle = rtnTitle.substring(0,rtnTitle.length-3);
		}
		return rtnTitle;
	},
	listeners : {
		itemclick :function(view,re){
			var me = this;
			me.itemclickTree(re);
		}
	}
});

