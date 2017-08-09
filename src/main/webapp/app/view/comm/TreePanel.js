Ext.define('FHD.view.comm.TreePanel',{
	extend: 'Ext.tree.Panel',
	alias: 'widget.fhdtree',
	
	//treeloader
	url:'',
	extraParams:{},
	//是否可搜索
	searchable:true,
	searchParamName:'query',
	//根结点
	root:{},
	//根结点是否可见
	rootVisible:true,
	//默认根不可选
	rootCheck:false,
	//单选: 'single'、多选: 'multiple'(默认)、级联多选:'cascade'(同时选父和子);'parentCascade'(选父);'childCascade'(选子)
	checkModel:'multiple',
	//是否叶子可选
	onlyLeafCheckable:false,
	//默认是否展开
	isExpand:true,
	//是否带复选框
	isCheckbox:true,
	//是否显示图标
	showIcon:false,
	//默认选项
	chooseValues:'',
	//toolbar扩展
	tbarItems:[],
	checkCallback:function(node,checked){},
    
	initComponent:function(){
		var me = this;
		
		if(me.showIcon){
			me.extraParams.showIcon = me.showIcon;
		}
		if(me.isCheckbox){
			me.extraParams.canChecked = me.isCheckbox;
		}
		if(me.onlyLeafCheckable){
			me.extraParams.onlyLeafCheckable = me.onlyLeafCheckable;
		}
		if(me.rootVisible){
			me.root.rootVisible = me.rootVisible;
		}
		if(me.rootCheck){
			me.root.checked = false;
		}
		
		me.store = Ext.create('Ext.data.TreeStore', {
			fields : ['text', 'id', 'dbid','code','leaf','iconCls','cls','type','idSeq'],
    	    proxy: {
    	    	url: me.url,
    	    	extraParams: me.extraParams,
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        }
    	    },
    	    root: me.root
    	});
    	
		me.toolbar = Ext.create('Ext.toolbar.Toolbar',{
			border:false
		});
		
		Ext.applyIf(me,{
			tbar:[me.toolbar]
		});
		me.callParent(arguments);
		
		
		//级联或单独设置子节点的状态
		me.addListener('checkchange',function(node,checked,eopts){
			if(me.checkModel=='childCascade' || me.checkModel=='cascade'){
				me.expandCascade(node, checked, eopts);
			}else if(me.checkModel=='single'){
				var nodes = me.getChecked();
				if(checked){
					Ext.Array.each(nodes,function(selNode){
						if(node.data.id != selNode.data.id){
							selNode.set('checked', false);
						}
					});
				}else{
					node.set('checked', checked);
				}
				//回调
				me.checkCallback(node,checked);
			}else if(me.checkModel=='multiple'){
				//多选
				node.set('checked', checked);
				//回调
				me.checkCallback(node,checked);
			}else if(me.checkModel=='parentCascade'){
				//级联父结点
				if(me.onlyLeafCheckable){
					childNode.set("checked",checked); 
				}else{
					me.setParentCascade(node, checked);
				}
			}/*else if(me.checkModel=='cascade'){
				//级联
				me.setCascade(node, checked);
			}else if(me.checkModel=='childCascade'){
				//级联子结点
				if(me.onlyLeafCheckable){
					childNode.set("checked",checked); 
				}else{
					me.setChildCascade(node, checked);
				}
			}
			*/
    	});
    	
		//展开选中节点，设置子节点的选中状态
		me.addListener('afteritemexpand',function(node, index, item, eOpts){
			if(me.chooseValues != ''){
				var childNodes=node.childNodes;
				Ext.Array.each(childNodes,function(childNode){
					if(childNode.data.checked){
						//已选择不操作
					}else{
						//未选择
						if(Ext.Array.contains(me.chooseValues.split(","), childNode.data.id)){
							if(me.checkModel=='single'){
								var chooseValueArray = me.chooseValues.split(",");
								if(chooseValueArray.length>1){
									alert("单选模式默认选中值不能超过1个!");
								}else{
									childNode.set("checked",true);
									//回调
									me.checkCallback(node,true);
								}
							}else if(me.checkModel=='multiple'){
								//多选
								childNode.set("checked",true);
								//回调
								me.checkCallback(node,true);
							}else if(me.checkModel=='cascade'){
								//级联
								me.setCascade(childNode, true);
							}else if(me.checkModel=='parentCascade'){
								//级联父结点
								if(me.onlyLeafCheckable){
									childNode.set("checked",true); 
								}else{
									me.setParentCascade(childNode, true);
								}
							}else if(me.checkModel=='childCascade'){
								//级联子结点
								if(me.onlyLeafCheckable){
									childNode.set("checked",true); 
								}else{
									me.setChildCascade(childNode, true);
								}
							}
						}
						if(!childNode.data.checked && me.isCheckbox){
							childNode.set("checked",false);
						}
					}
				});
			}else{
				if(!childNode.data.checked && me.isCheckbox){
					childNode.set("checked",false);
				}
			}
    	});
    	
    	me.expandAndCollapseButton =Ext.create('Ext.Button',{
			//iconCls:'icon-collapse-all',
    		tooltip:'展开或收缩树结点',
			listeners:{
				beforerender:function(t){
					if(me.isExpand){
						t.setIconCls('icon-expand-all');
					}else{
						t.setIconCls('icon-collapse-all');
					}
				}
			},
			handler:function(){
				if(me.isExpand){
					me.isExpand=false;
					me.expandAndCollapseButton.setIconCls('icon-collapse-all');
					me.getRootNode().collapseChildren();
					//me.collapseAll();
				}else{
					me.isExpand=true;
					me.expandAndCollapseButton.setIconCls('icon-expand-all');
					me.expandAll();
				}
			}
		});
		
		me.refreshButton =Ext.create('Ext.Button',{
			iconCls:'icon-arrow-refresh-blue',
			tooltip:'刷新树结点',
			handler:function(){
				me.getStore().load({
					callback:function(){
						me.init();
					}
				});
			}
		});
		
		me.searchField = Ext.create('Ext.ux.form.SearchField', {
			width : 150,
			paramName:me.searchParamName,
			store:me.store,
			emptyText : FHD.locale.get('searchField.emptyText')
		});
		if(me.searchable){
			me.toolbar.add(me.searchField);
		}
		me.toolbar.add(me.expandAndCollapseButton);
		me.toolbar.add(me.refreshButton);
		
		me.selectAllButton =Ext.create('Ext.Button',{
    		tooltip:'全选',
    		iconCls:'icon-expand-all',
			handler:function(){
				me.selectedAll();
			}
		});
		me.notSelectAllButton =Ext.create('Ext.Button',{
    		tooltip:'全不选',
    		iconCls:'icon-collapse-all',
			handler:function(){
				me.notSelectedAll();
			}
		});
		me.toolbar.add(me.selectAllButton);
		me.toolbar.add(me.notSelectAllButton);
		
		if(me.tbarItems.length > 0){
			me.toolbar.add(me.tbarItems);
		}
		
		me.init();
	},
	//初始化选中结点
	init:function(values){
		var me=this;
		
		//设置树默认是否展开
		if(me.isExpand){
			me.isExpand=true;
			me.expandAndCollapseButton.setIconCls('icon-expand-all');
			//me.getRootNode().expand();
			me.expandAll();
		}else{
			me.isExpand=false;
			me.expandAndCollapseButton.setIconCls('icon-collapse-all');
			me.getRootNode().collapseChildren();
			//me.collapseAll();
		}
		if(me.isCheckbox){
			me.getRootNode().expand(false,function(){
				var childNodes=me.getRootNode().childNodes;
				Ext.Array.each(childNodes,function(childNode){
					childNode.set("checked",false);
				});
			});
		}
	},
	//级联展开：当checkModel='cascade'或'childCascade'时使用
	expandCascade:function(node,checked,eopts){
		var me=this;
		
		node.expand(true,function(){
			var childNodes=node.childNodes;
			Ext.Array.each(childNodes,function(childNode){
				if(childNode.data.leaf){
					if(me.checkModel=='childCascade'){
						me.setChildCascade(childNode, checked);
					}else if(me.checkModel=='cascade'){
						me.setCascade(childNode, checked);
					}
				}else{
					childNode.set('checked', checked);
					me.expandCascade(childNode, checked, eopts);
				}
			});
		});
	},
	//向上向下级联
	setCascade : function(node, checked) {
		var me=this;
		
		me.setParentCascade(node, checked);
		me.setChildCascade(node, checked);
	},
	//向上级联
	setParentCascade : function(node, checked) {
		var me = this;
		node.set('checked', checked);
		//回调
		me.checkCallback(node,checked);
		if (node.parentNode != null) {
			if(me.getRootNode().id != node.parentNode.id){
				me.setParentCascade(node.parentNode, checked);
			}else{
				if(me.rootCheck){
					me.setParentCascade(node.parentNode, checked);
				}
			}
		}
	},
	//向下级联
	setChildCascade:function(node, checked){
		var me=this;
		node.set('checked', checked);
		//回调
		me.checkCallback(node,checked);
		//expand回调递归
		node.expand(true,function(){
			var childNodes=node.childNodes;
			Ext.Array.each(childNodes,function(childNode){
				//childNode.expand(true,function(){
					me.setChildCascade(childNode, checked);
				//});
			});
		});
	},
	//全选
	selectedAll:function(){
		var me=this;
		
		if(me.getRootNode()){
			me.getRootNode().expand(true,function(){
				if(me.rootVisible && me.rootCheck){
					me.getRootNode().set('checked', rootCheck);
				}
				Ext.Array.each(me.getRootNode().childNodes,function(childNode){
					if (childNode != null) {
						me.setCascade(childNode, true);
					}
				});
			});
		}
	},
	//全不选
	notSelectedAll:function(){
		var me=this;
		
		if(me.getRootNode()){
			me.getRootNode().expand(true,function(){
				if(me.rootCheck){
					me.getRootNode().set('checked', false);
				}
				Ext.Array.each(me.getRootNode().childNodes,function(childNode){
					if (childNode != null) {
						me.setCascade(childNode, false);
					}
				});
			});
		}
	},
    reloadData:function(){
    	var me=this;
    	
    	me.getStore().load({
			callback:function(){
				me.init();
			}
		});
    }
});