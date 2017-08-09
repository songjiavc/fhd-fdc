Ext.define('FHD.view.kpi.datasource.kpiDataSourceTreePanel', {
			extend : 'FHD.ux.TreePanel',
			alias : 'widget.treePanel',
			title : '数据源信息',
			root : {
				"id" : "dataSource_root",
				text : '外部数据源',
				"leaf" : false,
				"expanded" : true
			},
			currentNode : null,
			itemclickTree: function(re){
            	var me = this;
            	var editPanel = Ext.getCmp("DateSourceForm");
            	var rootId = this.getRootNode().data.id;
            	me.currentNode = re;
            	var editPanel = Ext.getCmp('DateSourceForm');
            	editPanel.dataSourceId = re.data.id;
            	if (rootId != re.data.id) {
            		editPanel.load();
            		editPanel.isAdd = false;
            	} else {
            		editPanel.isAdd = true;
            		editPanel.getForm().reset();
            	}            	            	
            },
            /**
			 * 右键菜单
			 */
    	   contextItemMenuFun: function (view, record, node, index, e) {
    		   var me = this;
    		   var editPanel = Ext.getCmp("DateSourceForm");
 	    	   me.currentNode = record;
 		       var menu = Ext.create('Ext.menu.Menu', {
 		            margin: '0 0 10 0',
 		            items: []
 		        });
 		       var addButton = {
 		    		  iconCls: 'icon-add',
		              text: "添加数据源",
	                  handler:function(){
	                	  editPanel.addDs();
	                  }		    		   
 		       };
 		       menu.add(addButton);
				if(record.data.id!=me.root.id){
	 		       menu.add('-');
					var delButton = {
						iconCls: 'icon-del',
						text: "删除数据源",
						handler:function(){
							editPanel.del();
						}
		 		    }
					menu.add(delButton);
				}
 		      return menu;
    		   
    	   },

			// 初始化方法
			initComponent : function() {
				var me = this;

				me.id = 'DataSourceTree';
				me.queryUrl = 'kpi/dataSource/findKpiDataSource.f';

				Ext.apply(me, {
							rootVisible : true,
							width : 260,
							split : true,
							collapsible : true,
							border : true,
							region : 'west',
							multiSelect : true,
							rowLines : false,
							singleExpand : false,
							checked : false,
							url : me.queryUrl,// 调用后台url
							root : me.root,
							listeners : {
				   				load: function (store, records) { //默认选择首节点
				   					var selectedNode;
				   			        selectedNode = me.getRootNode();
				   			        if(selectedNode.hasChildNodes()){
				   			        	selectedNode = selectedNode.firstChild;
				   			        } else {
				   			        	selectedNode = me.getRootNode();
				   			        }
				   			        me.getSelectionModel().select(selectedNode);//默认选择首节点
						            me.itemclickTree(selectedNode);
				                },
								itemclick : function(view, re) {
									me.itemclickTree(re);
								}
							},
							viewConfig: {
							listeners: {
								itemcontextmenu: function(view, record, node, index, e){
									 e.stopEvent();
									 var menu = me.contextItemMenuFun(view, record, node, index, e);
			                         if (menu) {
			                             menu.showAt(e.getPoint());
			                         }
			                         return false;
								 }
							   }
							}							
						});

				me.callParent(arguments);
			}
		});