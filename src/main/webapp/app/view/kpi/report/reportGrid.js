Ext.define('FHD.view.kpi.report.reportGrid',{
    extend: 'Ext.panel.Panel',
    
    requires: [
               
              ],
              
    gridBorder:false,
    
    checked:false,
    
    queryUrl:'',
    
    pagable:false,
    
    searchable:false,
	
	
    initComponent: function () {
        var me = this;
        
        me.grid = Ext.create('FHD.ux.GridPanel',{
        	 title:'关联已有的报表',
        	 cols: [
        	          {
        	              header: FHD.locale.get('fhd.kpi.kpi.form.name'),
        	              dataIndex: 'name',
        	              sortable: false,
        	              flex: 1
        	            },
        	            {
        	          	  	header: '说明',
        	                dataIndex: 'descs',
        	                sortable: false,
        	                flex: 3
        	            },
        	            {
          	          	  	header: '操作',
          	                dataIndex: 'operation',
          	                sortable: false,
          	                flex: 0.3
          	            }
        	          
        	          ],
             url: me.queryUrl,
             border: me.gridBorder,
             checked: me.checked,
             pagable:me.pagable,
             searchable:me.searchable
             
        })
        
        Ext.apply(me, {
           hidden:me.hidden,
           items:[
                  me.grid
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