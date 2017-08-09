Ext.define('FHD.view.comm.analysis.ThemeChartPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.themechartpanel',

    // 初始化方法
    initComponent: function () {
        var me = this;

        me.panel = Ext.create('Ext.panel.Panel',{
        	title:'ThemeChartPanel'
        });
        Ext.applyIf(me, {
            items: [
                me.panel
            ]
        });
        me.callParent(arguments);
    },
    saveData:function(){
    	var me=this;
    	
    	alert("chart save data ......");
    	return true;
    },
    loadData:function(businessId,editflag){
    	var me=this;
    	
    	me.businessId = businessId;
    	me.editflag = editflag;
    	//alert(me.businessId+'\t'+me.editflag);
    	//me.setInitBtnState(editflag);
    },
   	reloadData:function(){
   		var me=this;
   		
   	}
});