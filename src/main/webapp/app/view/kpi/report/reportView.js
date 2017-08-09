Ext.define('FHD.view.kpi.report.reportView',{
    extend: 'Ext.panel.Panel',
    
    
    requires: [
              ],
              
    border:false,
    
    relaReport:function(){
    	var me = this;
    	var reportGrid = me.reportMainContainer.reportGrid;
    	if(reportGrid.isHidden()){
    		reportGrid.show();
    	}else{
    		reportGrid.hide();
    	}
    },
	
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
        	
        	rbar: [

                {
                	tooltip:'新建报表',
                    iconCls: 'icon-ibm-action-new-report',
                    handler: function () {
                    }
                },
                {
                	tooltip: '关联已有的报表',
                    iconCls: 'icon-ibm-icon-add-report',
                    handler: function () {
                    	
                    	me.relaReport();
                    	
                    }
                },
                {
                	tooltip: '刷新',
                    iconCls: 'icon-arrow-refresh-blue',
                    handler: function () {
                    }
                },
                {
                	tooltip: '在新窗口中打开',
                    iconCls: 'icon-application-cascade',
                    handler: function () {
                    }
                }
                
                ],
            
            
        	
    		items:[
    		      ]
        });
        me.callParent(arguments);
    },
    /**
     * 重新加载数据
     */
    reloadData : function(record) {
    	var me = this;
    }

});