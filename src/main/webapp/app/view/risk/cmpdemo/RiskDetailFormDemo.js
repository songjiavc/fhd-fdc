Ext.define('FHD.view.risk.cmpdemo.RiskDetailFormDemo', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.riskdetailformdemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.detailForm = Ext.create('FHD.view.risk.cmp.RiskShortDetailForm', {
        	title:'风险详细信息查看',
        	riskId:'CW01'
		});
        me.allDetailForm = Ext.create('FHD.view.risk.cmp.RiskDetailForm', {
        	title:'风险详细信息查看(全)',//
        	riskId:'9322cf77-a8f4-4d08-bb69-bcae3cb4fdf3'
		});
        

//        me.riskDetailButton = Ext.create('Ext.Button', {
//            text: '风险详细信息window',
//            handler: function() {
//            	var win = Ext.create('Ext.window.Window', {
//            		autoScroll:true,
//            		title:'风险详细信息',
//            		width:800,
//            		height:400,
//                	items:[me.allDetailForm]
//        		});
//            	win.show();
//            }
//        });
//        me.detailPanel = Ext.create('Ext.form.Panel',{
//        	title:'弹窗',
//        	autoScroll: true,
//            border: false,
//            bodyPadding: "5 5 5 5",
//            items: [{
//                xtype: 'fieldset',//基本信息fieldset
//                collapsible: true,
//                defaults: {
//                	margin: '7 30 3 30',
//                	columnWidth:.5
//                },
//                layout: {
//                    type: 'column'
//                },
//                title: "风险详细信息查看",
//                items:[me.riskDetailButton]
//            }]
//        });
        
        Ext.apply(me, {
            autoScroll: false,	//不显示滚动条
            border: false,
            bodyPadding: "5 5 5 5",
            layout:'fit',
            items: [me.detailForm,me.allDetailForm]
        });
        
        me.callParent(arguments);
      
    }
});