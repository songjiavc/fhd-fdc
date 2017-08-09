Ext.define('FHD.demo.time.Time', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.Time',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;			
	    var panel = Ext.create('Ext.panel.Panel',{
					border:false,
		    		items: [{
		    			xtype: 'button', 
		    			text: '时间控件',
		    				handler:function(){
		    					Ext.create('FHD.ux.timestamp.TimestampWindow',{
									onSubmit:function(values){
										var valuesStr = values.split(',');
										var yearId = valuesStr[0];
										var quarterId = valuesStr[1];
										var monthId = valuesStr[2];
										var weekId = valuesStr[3];
										alert(yearId);
										alert(quarterId);
										alert(monthId);
										alert(weekId);
									}
								}).show();
		    				}
		    		}]
			});
		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					items : [panel]
				});
		me.callParent(arguments);
	}
})