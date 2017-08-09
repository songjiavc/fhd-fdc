Ext.define('FHD.view.SASACdemo.homepage.RiskDetailWindowForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskDetailWindowForm',
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet1 = {
                xtype:'fieldset',
                title: '基础信息',
                collapsible: true,
                margin: '5 5 0 5',
                defaults: {
                        columnWidth : 1 / 2,
                        margin: '7 30 3 30',
                        labelWidth: 120
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [{xtype:'displayfield', fieldLabel : '风险名称', name:''},
         	             {xtype:'displayfield', fieldLabel : '所属类型', name:''},
         	             {xtype:'displayfield', fieldLabel : '描述', name:''}
    					]
    						
            };
        me.fieldSet2 = {
                xtype:'fieldset',
                title: '影响分析',
                collapsible: true,
                margin: '5 5 0 5',
                defaults: {
                        columnWidth : 1,
                        margin: '7 30 3 30',
                        labelWidth: 120
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [{xtype:'displayfield', fieldLabel : '对收入的影响分析', name:''},
         	             {xtype:'displayfield', fieldLabel : '对利润的影响分析', name:''}
    					]
    						
            };
        me.fieldSet3 = {
                xtype:'fieldset',
                title: '应对与支持',
                collapsible: true,
                margin: '5 5 0 5',
                defaults: {
                        columnWidth : 1,
                        margin: '7 30 3 30',
                        labelWidth: 125
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [{xtype:'displayfield', fieldLabel : '应对计划', name:''},
         	             {xtype:'displayfield', fieldLabel : '需要国资委支持的工作', name:''}
    					]
    						
            };
      
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1, me.fieldSet2, me.fieldSet3]
        });

        me.callParent(arguments);
    }

});