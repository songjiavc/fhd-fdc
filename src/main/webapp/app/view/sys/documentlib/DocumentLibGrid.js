Ext.define('FHD.view.sys.documentlib.DocumentLibGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.documentLibGrid',
 	requires: [
	],
	
	// 添加/修改方法
    edit: function(isAdd){
    	var me = this;
    	var documentLibCard = me.up('documentLibCard');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var documentTree = documentLibCard.up('documentLibMainPanel').documentLibTree;
    	if(isAdd){//新增
    		documentLibCard.documentLibEditPanel.getForm().reset();
    		if(documentTree.nodeId && !documentTree.nodeType){
    			documentLibCard.documentLibEditPanel.parentDocType.setValue(documentTree.nodeId);
    		}
    		documentLibCard.documentLibEditPanel.docId = "";
    		documentLibCard.showDocumentLibEditPanel();
    	}else{
    		var length = selection.length;
    		if (length >= 2) {//判断是否多选
    	        FHD.notification(FHD.locale.get('fhd.common.updateTip'),FHD.locale.get('fhd.common.prompt'));
    	        return;
    		}else{
    			documentLibCard.documentLibEditPanel.docId = selection[0].get('id');
    			documentLibCard.documentLibEditPanel.reLoadData(selection[0].get('id'));
    			documentLibCard.showDocumentLibEditPanel();
    		}
    	}
    } ,
    
    //删除方法
    del : function(me){
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				var ids = [];
    				for(var i=0;i<selection.length;i++){
    					ids.push(selection[i].get('id'));
    				}
    				FHD.ajax({//ajax调用
    					url : __ctxPath + '/sys/document/deletedocumentlibrarybyids.f',
    					params : {
    						docIds:ids.join(',')
    					},
    					callback : function(data){
    						if(data){//删除成功！
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    
    //下载附件
    downloadFile: function(rowIndex){
		var me=this;
		me.getSelectionModel().deselectAll();
		me.getSelectionModel().select(rowIndex);
		var selection=me.getSelectionModel().getSelection();
        var fileId = selection[0].get('fileIds');
        
        if(fileId != ''){
        	window.location.href=__ctxPath+"/sys/file/download.do?id="+fileId;
        }else{
        	FHD.notification('该样本没有上传附件!',FHD.locale.get('fhd.common.prompt'));
        }
	},
    
    //重新加载数据方法
    reloadData: function(nodeId,type){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/sys/document/finddocumentlabraryspage.f';
 		me.store.proxy.extraParams.typeId = nodeId;
 		me.store.proxy.extraParams.type = type;//是否为根节点
 		me.store.load();
    },
    
    setstatus : function(me){//设置按钮可用状态
    	if (me.down("[name='doc_edit']")) {
            me.down("[name='doc_edit']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='doc_del']")) {
            me.down("[name='doc_del']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    //预览窗口
   	previewWin: function(){
   		var me = this;
   		var selection = me.getSelectionModel().getSelection();
   		var docId = selection[0].get('id');
   		var fileId = selection[0].get('fileIds');
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
   	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "文件名称",
	            dataIndex: 'documentName',
	            sortable: true,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\">" + value + "</a>";
     			},
     			listeners: {
     				click: function(){
     					me.previewWin();
     				}
     			}
	        },
	        {
	            header: "所属分类",
	            dataIndex: 'dictEntryNameStr',
	            sortable: true,
	            flex: 1
	        },{
	            header: "创建时间",
	            dataIndex: 'createTimeStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "更新时间",
	            dataIndex: 'lastModifyTimeStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },
			{header:'操作',dataIndex:'cz',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
			       xtype:'actioncolumn',
			       items: [{
		                icon: __ctxPath+'/images/icons/download_min.png',  // Use a URL in the icon config
		                tooltip: '下载',
		                handler: function(grid, rowIndex, colIndex) {
		                    me.downloadFile(rowIndex);
		                }
		            }]
			    },
			{
				header: "fileIds",
				dataIndex:'fileIds',
				hidden:true
			}
        ];
       
        Ext.apply(me,{
        	region:'center',
        	//url : __ctxPath + "",//查询列表url
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				me.edit(true);
        			}
    			},'-',{
        			btype:'edit',
        			disabled:true,
        			name : 'doc_edit',
        			handler:function(){
        				me.edit(false);
        			}
    			},'-',{
        			btype:'delete',
        			disabled:true,
        			name : 'doc_del',
        			handler:function(){
        				me.del(me);
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : true
        });
       
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);

    }

});