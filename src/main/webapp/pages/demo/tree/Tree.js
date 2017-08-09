Ext.define('FHD.demo.tree.Tree', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.TreePanel',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var indexMainPanel = Ext.getCmp('indexMainPanel');;
				var queryUrl = 'test/loadTestMvcTree.f'; // 查询url
				treetest = Ext.create('FHD.ux.TreePanel', {// 实例化一个grid列表
					useArrows : true,
					multiSelect : true,
					rowLines : false,
					singleExpand : false,
					checked : false,
					url : queryUrl,// 调用后台url
					height : FHD.getCenterPanelHeight() - 5// 高度为：获取center-panel的高度
				});
				indexMainPanel.tree.on('collapse', function(p) {
							treetest.setWidth(indexMainPanel.getWidth() - 26 - 5);
						});
				indexMainPanel.tree.on('expand', function(p) {
							treetest.setWidth(indexMainPanel.getWidth() - p.getWidth()
									- 5);
						});
				indexMainPanel.on('resize', function(p) {
							treetest.setHeight(p.getHeight() - 5);
							if (indexMainPanel.tree.collapsed) {
								treetest.setWidth(p.getWidth() - 26 - 5);
							} else {
								treetest.setWidth(p.getWidth()
										- indexMainPanel.tree.getWidth() - 5);
							}
						});
				indexMainPanel.tree.on('resize', function(p) {
							if (p.collapsed) {
								treetest.setWidth(indexMainPanel.getWidth() - 26 - 5);
							} else {
								treetest.setWidth(indexMainPanel.getWidth()
										- p.getWidth() - 5);
							}
						});
				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [treetest]
						});
				me.callParent(arguments);
			}

		})