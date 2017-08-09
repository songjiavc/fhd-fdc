/**
 * 
 * 参数设置表单
 */

Ext.define('FHD.view.riskAnalysisDemo.AnalysisResultFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.analysisResultFormPanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.fieldSet1 = {
                xtype:'fieldset',
                title: '回归统计',
                height: 170,
                defaultType: 'textfield',
                margin: '5 5 5 5',
                columnWidth: .3,
         	    items : [{xtype:'displayfield', fieldLabel : 'Multiple R', name:'MultipleR', value:'0.58165731015547'},
         	             {xtype:'displayfield', fieldLabel : 'R Square', name:'RSquare', value:'0.338325226457296'},
         	             {xtype:'displayfield', fieldLabel : 'Adjusted R', name:'AdjustedR', value:'0.205990271748755'},
         	             {xtype:'displayfield', fieldLabel : '标准误差', name:'', value:'0.218554350953744'},
         	             {xtype:'displayfield', fieldLabel : '观测值', name:'', value:'19'}]
        };
        var filterPanel1 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .1,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', fieldLabel : '  ', name:'title'},
         	        {xtype:'displayfield', fieldLabel : '回归分析', name:'hgfx'},
         	        {xtype:'displayfield', fieldLabel : '残差', name:'cc'},
         	        {xtype:'displayfield', fieldLabel : '总计', name:'count'}]
		});
		var filterPanel2 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .1,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', value : 'df', name:''},
         	        {xtype:'displayfield', value : '1', name:''},
         	        {xtype:'displayfield', value : '17', name:''},
         	        {xtype:'displayfield', value : '18', name:''}]
		});
		var filterPanel3 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .2,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', value : 'SS', name:'title'},
         	        {xtype:'displayfield', value : '0.27721511781033', name:'hgfx'},
         	        {xtype:'displayfield', value : '0.805628136098222', name:'cc'},
         	        {xtype:'displayfield', value : '1.08284325390855', name:'count'}]
		});
		var filterPanel4 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .2,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', value : 'MS', name:''},
         	        {xtype:'displayfield', value : '0.27721511781033', name:''},
         	        {xtype:'displayfield', value : '0.0473898903587189', name:''},
         	        {xtype:'displayfield', value : '', name:''}]
		});
		var filterPanel5 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .2,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', value : 'F', name:'title'},
         	        {xtype:'displayfield', value : '5.84966784501807', name:''},
         	        {xtype:'displayfield', value : '', name:''},
         	        {xtype:'displayfield', value : '', name:''}]
		});
		var filterPanel6 = Ext.create('Ext.panel.Panel', {
         	border : false,
         	columnWidth: .2,
         	margin: '5 5 5 5',
		    items: [{xtype:'displayfield', value : 'Significance F', name:''},
         	        {xtype:'displayfield', value : '0.027086314586931', name:''},
         	        {xtype:'displayfield', value : '', name:''},
         	        {xtype:'displayfield', value : '', name:''}]
		});
        me.fieldSet2 = {
                xtype:'fieldset',
                title: '方差分析',
                height: 170,
                defaultType: 'textfield',
                margin: '5 5 5 5',
                layout: {
     	       		type: 'column'
     	    	},
                columnWidth: .7,
         	    items : [filterPanel1,filterPanel2,filterPanel3,filterPanel4,filterPanel5,filterPanel6]
       };
       
        Ext.apply(me, {
        	border:false,
        	margin: '5 5 10 5',
        	layout: {
     	        type: 'column'
     	    },
            items : [me.fieldSet1, me.fieldSet2]
        });

       me.callParent(arguments);
    }

});