/**
 * 沈飞首页-新闻公告
 * 
 * @author 邓广义
 */
Ext.define('FHD.view.sf.index.SFNewsGrid',{
    extend: 'FHD.ux.GridPanel',
	checked: false,
	pagable : false,
	searchable : false,
	columnLines: false,
	hideHeaders:true,
    initComponent: function () {
    	var me = this;
    	me.queryUrl = __ctxPath + '/sys/report/findReportTemplateList.f';
    	me.cols = [
	             	{header: 'id' ,dataIndex: 'id',sortable: true,flex : 1,hidden : true,height:50},
	             	{header: '模板类型' ,dataIndex: 'templateType',sortable: true,flex : 1,hidden : true},
	             	{header: '模板描述' ,dataIndex: 'templateName',sortable: true,flex : 2,hidden : false,
	             	
	             	 	renderer:function(value,metaData,record,colIndex,store,view) {
	             	 		var id = record.data.id;
	            		return "<a href=\"javascript:void(0);\"onclick=\"Ext.getCmp('" + me.id + "').onShowDetails('" + id + "')\">" + value + "</a>";
	            		}
	            	
	            	},
	             	{header: '模板编号' ,dataIndex: 'templateCode',sortable: true,flex : 1,hidden : false,	            
		             	renderer:function(value,metaData,record,colIndex,store,view) {
		            		return "<div style=\"height:30px\">"+"<span style=\"float: left;margin: 8px 0;\">"+value+"</span>"+"</div>";
	     	    		//"<span style=\"float: left;margin: 10px 0;\">"+value+"</span>"
	     				} 
     				}
    	          ];
		
    	Ext.apply(me, {
		   listeners:{
				afterrender:function(self,eopts){
//					var elments = Ext.get("gridview-1079");
//					elments.setStyle("overflow", 'hidden');
				}
		   },
    	   border:false,
    	   pagable:false,
    	   searchable:false,
 	       multiSelect: false,
 	       rowLines:true,//显示横向表格线
 	       checked: false, //复选框
 	       autoScroll:true
         });
         me.callParent(arguments);
     	
    },
    reloadData:function(param){
    	var me = this;
    	me.store.proxy.extraParams.templateType = param;
    	me.store.proxy.extraParams.limit = 5;
    	me.store.proxy.url = me.queryUrl;
    	me.store.load();
    },
    onShowDetails:function(id){
    	var me = this;
    	me.preForm = Ext.create('FHD.view.sf.index.SFNewsPreForm',{
    	
    	});
    	me.preForm.reloadData(id);
   		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 400,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.preForm]
		}).show();
    }

});