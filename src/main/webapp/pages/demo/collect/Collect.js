Ext.define('FHD.demo.collect.Collect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Collect',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var panel = Ext.widget('collectionSelector', {
							name : 'collections',
							labelText : '采集频率',
							single : false,
							value : ''
						});
				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [panel]
						});
				me.callParent(arguments);
			}
		})