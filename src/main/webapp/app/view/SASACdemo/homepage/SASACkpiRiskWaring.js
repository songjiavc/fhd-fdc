Ext.define('FHD.view.SASACdemo.homepage.SASACkpiRiskWaring', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.sasackpiRiskWaring',

    requires: [
               
    ],
    
    backhomePage: function(){
		var me = this;
		var card = me.up('sasaccardpanel');
		card.showHomePageMainPanel();
	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
		me.showRiskStatusTabPanel = Ext.create('FHD.view.SASACdemo.homepage.ShowRiskStatusTabPanel',{
			region: 'center'
		});
        
        me.SASACchartPointLine = Ext.create('FHD.view.SASACdemo.homepage.SASACchartPointLine',{//收入图
        	height:300,
        	region: 'west',
        	border: false,
        	flex: 1
        });
        
        me.SASACchartPointLineRight = Ext.create('FHD.view.SASACdemo.homepage.SASACchartPointLine',{//利润图 
        	height:300,
        	region: 'center',
        	border: false,
        	flex: 1
        });
        
        me.panel2 = Ext.create('Ext.panel.Panel',{
        	border : false,
        	margin: '1 1 0 1',
        	height: 250,
        	layout: 'border',
        	region: 'north',
        	items: [me.SASACchartPointLine,me.SASACchartPointLineRight]
        });
        
        Ext.applyIf(me, {
        	border : false,
        	layout: 'border',
        	bbar: {items: [ '->',{text: '返回', //返回按钮
					            iconCls: 'icon-control-repeat-blue',
					            handler: function () {
					              me.backhomePage()
					            }
				            }
		  			]
	   		},
            items: [me.panel2,me.showRiskStatusTabPanel]
        });

        me.callParent(arguments);
    }

});