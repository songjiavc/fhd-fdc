Ext.define('FHD.view.SASACdemo.homepage.RiskCategoryDetail', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskCategoryDetail',

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
        
        me.riskCategoryGrid = Ext.create('FHD.view.SASACdemo.homepage.RiskCategoryGrid');//列表
        
        me.sasaccolumnarChart = Ext.create('FHD.view.SASACdemo.homepage.SASACcolumnarChart',{//收入图
        	height:300,
        	region: 'west',
        	border:false,
        	flex: 1
        });
        
        me.sasaccolumnarChartRight = Ext.create('FHD.view.SASACdemo.homepage.SASACcolumnarChart',{//利润图 
        	height:300,
        	region: 'center',
        	border:false,
        	flex: 1
        });
        
        me.panel1 = Ext.create('Ext.panel.Panel',{
        	border : false,
        	height: 300,
        	layout: 'fit',
        	region: 'center',
            items: [me.riskCategoryGrid]
        });
        
        me.panel2 = Ext.create('Ext.panel.Panel',{
        	border : false,
        	height: 300,
        	layout: 'border',
        	region: 'north',
        	items: [me.sasaccolumnarChart,me.sasaccolumnarChartRight]
        });
        
        Ext.applyIf(me, {
        	border : false,
        	title: '风险预警分析',
        	layout: 'border',
        	bbar: {items: [ '->',{text: '返回', //保存按钮
					            iconCls: 'icon-control-repeat-blue',
					            handler: function () {
					              me.backhomePage()
					            }
				            }
		  			]
	   		},
            items: [me.panel1, me.panel2]
        });

        me.callParent(arguments);
    }

});