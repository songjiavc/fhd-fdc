/**
 * 公式编辑树
 */
Ext.define('FHD.view.risk.assess.formularSet.FormularSetEditTreePanel', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.formularSetEditTreePanel',
    
    //单击树节点事件
    itemclickTree: function(re){
    	var me = this;
    	var formularSetEditMain = me.up('formularSetEditMain');
    	var textArea = formularSetEditMain.rightPanel.formularSetEditFormPanel.formularTextArea;
    	var exitValue = textArea.value;
    	textArea.setValue(exitValue+re.data.text);
    	textArea.formularId = textArea.formularId + re.data.id;
    },
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	Ext.apply(me, {
    		searchable: false,
    		width: 200,
    		split: true,
           	collapsible : false,
           	border: false,
           	region: 'west',
           	multiSelect: true,
           	rowLines: false,
           	checked: false,
   			url: __ctxPath + '/sys/assess/formularsetedittreeloader.f',
   			listeners : {
   				itemclick :function(view,re){
    			 me.itemclickTree(re);
    		 }
   			}
        });
    	
        me.callParent(arguments);
    }
});