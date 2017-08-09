Ext.define('FHD.view.risk.cmpdemo.RiskEventSelectorDemo', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskeventselectordemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.riskEvent = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
			title : '请您选择风险事件',
			fieldLabel : '风险事件',
			multiSelect: true,
			height:40,
			labelAlign : 'left',
			name : 'riskEvent',
			margin : '7 30 3 30',
			columnWidth : .5
		});
        me.riskEventButton = Ext.create('Ext.Button', {
            text: '风险事件弹出window',
            handler: function() {
            	var win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{
    				multiSelect:true,
    				modal: true,
    				onSubmit:function(win){
    					var values = new Array();
    					var store = win.selectedgrid.store;
    					store.each(function(r){
    			    		values.push(r.data.id);
    			    	});
    					alert(values.join(','));
    				}
    			}).show();
            }
        });

        Ext.applyIf(me, {
            autoScroll: true,
            border: false,
            bodyPadding: "5 5 5 5",
            items: [{
                xtype: 'fieldset',//基本信息fieldset
                collapsible: true,
                defaults: {
                	margin: '7 30 3 30',
                	columnWidth:.5
                },
                layout: {
                    type: 'column'
                },
                title: "风险事件选择",
                items:[me.riskEvent,me.riskEventButton]
            }]
            
        });
        
        me.callParent(arguments);
      
    }
});