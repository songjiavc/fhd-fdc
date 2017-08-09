Ext.define('FHD.view.risk.cmpdemo.RiskSelectorDemo', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskselectordemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.risk = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			title : '请您选择风险',
			fieldLabel : '风险分类单选',
			multiSelect: false,
			height:40,
			labelAlign : 'left',
			name : 'risk',
			margin : '7 30 3 30',
			columnWidth : .5
		});
        
        me.riskMulti = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			title : '请您选择风险',
			fieldLabel : '风险分类多选',
			multiSelect: true,
			height:40,
			labelAlign : 'left',
			name : 'riskMulti',
			margin : '7 30 3 30',
			columnWidth : .5
		});
        
        me.riskOnlyLeaf = Ext.create('FHD.view.risk.cmp.RiskSelector', {
        	onlyLeaf:true,
			title : '请您选择风险',
			fieldLabel : '风险分类(只叶子节点可选)',
			multiSelect: true,
			height:40,
			labelAlign : 'left',
			name : 'riskOnlyLeaf',
			margin : '7 30 3 30',
			columnWidth : .5
		});

        me.riskButton = Ext.create('Ext.Button', {
            text: '风险分类弹出window',
            handler: function() {
            	var win = Ext.create('FHD.view.risk.cmp.RiskSelectorWindow',{
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
                title: "风险分类选择",
                items:[me.risk,me.riskMulti,me.riskOnlyLeaf]	//,me.riskButton
            }]
            
        });
        
        me.callParent(arguments);
      
    }
});