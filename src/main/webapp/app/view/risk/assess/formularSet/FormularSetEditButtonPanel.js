/**
 * 
 */
Ext.define('FHD.view.risk.assess.formularSet.FormularSetEditButtonPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formularSetEditButtonPanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        Ext.apply(me, {
        	border: false,
			autoScroll:true,
			flex:1,
			region : 'south',
			title : FHD.locale.get('fhd.formula.operator'),
			layout:'column',
			defaults : {
				margin : '10 10 10 10'
			},
			items:[
		     {xtype : 'button',text:'+',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"+");
					textArea.formularId = textArea.formularId + "<" + "+" + ">";
		    	 }
		     },
		     {xtype : 'button',text:'-',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"-");
					textArea.formularId = textArea.formularId + "<" + "-" + ">";
		    	 }
		     },
		     {xtype : 'button',text:'*',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"*");
					textArea.formularId = textArea.formularId + "<" + "*" + ">";
		    	 }
		     },
		     {xtype : 'button',text:'/',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"/");
					textArea.formularId = textArea.formularId + "<" + "/" + ">";
		    	 }
		     },
		     {xtype : 'button',text:'sqrt',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"sqrt");
					textArea.formularId = textArea.formularId + "<" + "sqrt" + ">";
		    	 }
		     },
		     {xtype : 'button',text:'(',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+"(");
					textArea.formularId = textArea.formularId + "<" + "(" + ">";
		    	 }
		     },
		     {xtype : 'button',text:')',columnWidth : .25,
		    	 handler:function(){
		    		var formularSetEditRightPanel = me.up('formularSetEditRightPanel');
		    		var textArea = formularSetEditRightPanel.formularSetEditFormPanel.formularTextArea;
		    		var exitValue = textArea.value;
					textArea.setValue(exitValue+")");
					textArea.formularId = textArea.formularId + "<" + ")" + ">";
		    	 }
		     }
			]
        });

       me.callParent(arguments);
       
    }

});