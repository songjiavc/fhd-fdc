
Ext.define('FHD.view.sf.index.SFMemoGrid',{
    extend: 'FHD.ux.GridPanel',
    hideHeaders:true,
	checked: false,
	pagable : false,
	searchable : false,
	columnLines: true,
	height:80,
    
    initComponent : function() {
    	var me = this;
    	
        Ext.apply(me, {
			url:__ctxPath + '/sf/index/findmemolist.f',
    		cols : [
    		{
    			dataIndex:'memoId',
    			hidden:false,
    			width:0
    		},{
    			header: "计划名称",
    			dataIndex: 'memoName',
    			sortable: false,
    			//align: 'center',
    			flex: 2,
    			renderer:function(value,meta, record){
	    				var id = record.data.id;
	    				return "<a style=\"text-decoration: none;color:#2C4674;\" href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').showMemo('" + id + "')\">"+value+"</a>";
           		}
    		},{
    			header: "计划时间",
    			dataIndex:'memoTime',
    			flex: 1.5,
    			hidden:false
    		},{header:'操作',
    		   dataIndex:'',
    		   hidden:false,
    		   editor:false,
    		   align:'center',
    		   flex:.5,
		       xtype:'actioncolumn',
		       items: [{
	                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
	                tooltip: FHD.locale.get('fhd.common.delete'),
	                handler: function(grid, rowIndex, colIndex) {
	                	//点击编辑按钮时，自动选中行
	                	Ext.MessageBox.show({
	                		title : FHD.locale.get('fhd.common.delete'),
	                		width : 260,
	                		msg : FHD.locale.get('fhd.common.makeSureDelete'),
	                		buttons : Ext.MessageBox.YESNO,
	                		icon : Ext.MessageBox.QUESTION,
	                		fn : function(btn) {
	                			if (btn == 'yes') {
	                				var rec = grid.getStore().getAt(rowIndex).data.id;
	                				FHD.ajax({
	                					url:  __ctxPath+'/sf/index/deletememo.f',
	                					params: {
	                						id:rec
	                					},
	                					callback: function (data) {
	                						if (data) {
	                							parent.Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
	                							me.store.load();
	                						}
	                					}
	                				});
	                			}
	                		}
	                	});
	                }
	            }]
			}]
        });
        me.callParent(arguments);
    },
    showMemo : function(id){
    	var me = this;
 		var formPanel = Ext.create('FHD.view.sf.index.MemoForm',{
		});
    	formPanel.reloadData(id,me);
    }
});
