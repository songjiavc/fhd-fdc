/**
 * 沈飞首页-风险文档
 * 
 * @author 郝静
 */
Ext.define('FHD.view.sf.index.SFRiskFiles', {
    extend: 'FHD.ux.GridPanel',
    border:false,
	checked: false,
	pagable : false,
	searchable : false,
	columnLines: true,
	hideHeaders:true,
	border:true,
	flex:1,
    limit:2,
    typeId:'',
    height:50,
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	var cellGrid = Ext.create('FHD.view.risk.assess.utils.GridCells');
    	var limit = me.limit;
    	var typeId = me.typeId;
    	
			
    	 me.cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},{
	            header: "所属分类",
	            dataIndex: 'dictEntryNameStr',
	            sortable: true,
	            flex: 1,	            
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            		return "<div style=\"height:30px\">"+"<span style=\"float: left;margin: 10px 0;\">"+value+"</span>"+"</div>";
     	    		
     			}
	        }, {
	            header: "文件编号",
	            dataIndex: 'documentCode',
	            sortable: true,
	            flex: 2
	        }, {
	            header: "文件名称",
	            dataIndex: 'documentName',
	            sortable: true,
	            flex: 2,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" style=\"text-decoration:none;color:#000\">" + value + "</a>";
     			},
     			listeners: {
     				click: function(){
     					me.previewWin();
     				}
     			}
	        }, {
				dataIndex:'fileMore',
	            sortable: true,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	if(""==value){
	            		var nodeId = me.typeId;
	            		return "<a href=\"javascript:void(0);\"onclick=\"Ext.getCmp('" + me.id + "').showMore('" + nodeId + "')\" style=\"text-decoration:none;color:#000\">" + '更多...' + "</a>";
	            	}
     	    		
     			}
			}
        ]

    	Ext.apply(me, {
    		url: __ctxPath + '/sys/document/finddocumentlabraryspage.f',
    		extraParams:{typeId:me.typeId,limit:me.limit},
    		border:false,
    		flex:1,
    		layout: {
               type: 'hbox'
            },
    		items:[me.txwjPanel]
        });
      
        me.callParent(arguments);
        me.on('afterlayout',function(){ 
        	cellGrid.mergeCells(me, [2]);
        	cellGrid.mergeCells(me, [5]);
        });
    },
    
	showMore:function(nodeId){
		var me = this;
		var url = 'FHD.view.sys.documentlib.DocumentLibMainPanel';
		var typeId = 'document_library_case';
		me.fn('FHD.view.sys.documentlib.DocumentLibMainPanel','文档库',typeId,nodeId);
	},
    //预览窗口
   	previewWin: function(){
   		var me = this;
   		var selection = me.getSelectionModel().getSelection();
   		var docId = selection[0].get('id');
   		me.documentLibPreviewPanel = Ext.create('FHD.view.sys.documentlib.DocumentLibPreviewPanel');
    	me.documentLibPreviewPanel.reloadData(docId);
   		
   		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 300,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.documentLibPreviewPanel]
		}).show();
   	}
});