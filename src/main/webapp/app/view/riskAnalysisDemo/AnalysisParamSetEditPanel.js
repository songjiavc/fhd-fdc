/**
 * 
 * 参数设置表单
 */

Ext.define('FHD.view.riskAnalysisDemo.AnalysisParamSetEditPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.analysisParamSetEditPanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        //X值输入区域store
   	 	var xareaStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data : [
	        	{"id":"AL", "name":"$C$S:$C$23"}
	    	]
    	    /*proxy: {
    	         type: 'ajax',
    	         url: '',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true*/
        });
       	//Y值输入区域store
        var yareaStore = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    data : [
	        	{"type":"AL", "name":"$D$S:$E$23"}
	    	]
        });
        //X值输入区域
   	 	var X_area = Ext.create('Ext.form.ComboBox', {
            fieldLabel: 'X值输入区域',
            name : 'xarea',
            store: xareaStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            margin: '7 10 10 30'
        });
        //Y值输入区域
   	 	var Y_area = Ext.create('Ext.form.ComboBox', {
            fieldLabel: 'Y值输入区域',
            name : 'yarea',
            store: yareaStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            margin: '15 10 10 30'
        });
        
        var checkboxgroup = Ext.create('Ext.form.CheckboxGroup', {
        	fieldLabel: '',
        	margin: '15 10 0 30',
		    items: [
		            { boxLabel: '标志', name: 'rb', inputValue: '1' },
		            { boxLabel: '常数为零', name: 'rb', inputValue: '2'},
		            /*{ boxLabel: '置信度', name: 'rb', inputValue: '3' }*/
		        ]
		});
		
		 var checkbox2 = Ext.create('Ext.form.CheckboxGroup', {
        	fieldLabel: '',
        	margin: '15 10 0 30',
        	layout: {
     	        type: 'column'
     	    },
     	    columnWidth: .3,
		    items: [
		            { boxLabel: '置信度', name: 'rb', inputValue: '3' }
		        ]
		});
		
		var textValue = Ext.create('Ext.form.TextField', {
    	    disabled:true,//禁止用户输入
    	    margin: '15 10 0 30',
    	    layout: {
     	        type: 'column'
     	    },
     	    columnWidth: .6,
    	    name:''
    	});
    	
    	var bfh = Ext.create('Ext.form.Label', {
    	    margin: '15 10 0 0',
    	    layout: {
     	        type: 'column'
     	    },
     	    columnWidth: .1,
    	    text:'%'
    	});
    	
    	var panel = Ext.create('Ext.panel.Panel', {
         	border : false,
         	layout: {
     	        type: 'column'
     	    },
     	    //margin: '15 10 10 30',
		    items: [checkbox2, textValue, bfh]
		});

        
        var fieldSet = {
            xtype:'fieldset',
            title: '输入',
            //collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 5 5',
            layout: {
     	        type: 'fit'
     	    },
     	    items : [X_area, Y_area, checkboxgroup, panel]
        };
        Ext.apply(me, {
        	border:false,
            items : [fieldSet]
        });

       me.callParent(arguments);
    }

});