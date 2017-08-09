Ext.define('FHD.view.sys.documentlib.DocumentLibPreviewPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.documentLibPreviewPanel',
    requires: [
    ],
    
    reloadData: function(docId){
    	var me = this;
    	if(!docId){
    		docId = me.docId;
    	}
    	me.form.load({
    	        url:__ctxPath + '/access/formulateplan/findpreviewpanelbydocid.f',
    	        params:{
    	        	docId:docId
    	        },
    	        failure:function(form,action) {
    	            FHD.alert("加载出错了");
    	        },
    	        success:function(form,action){
    	        }
    	    });
    },
    //下载文件
    downLoadFile: function(){
    	var me = this;
    	var fileId = me.down("[name='fileIds']").value.split(',')[0];
    	if(fileId != ''){
        	window.location.href=__ctxPath+"/sys/file/download.do?id=" + fileId;
        }
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
       
        me.fieldSet1 = {
                xtype:'fieldset',
                title: '基础信息',
                collapsible: true,
                margin: '10 10 10 10',
                defaults: {
                        columnWidth : 1 / 2,
                        margin: '10 30 5 30',
                        labelWidth: 95
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [ 	{xtype:'displayfield', fieldLabel : '文件名称', name:'documentName'},
         	              	{xtype:'displayfield', fieldLabel : '文件编号', name:'documentCode'},
         	              	{xtype:'displayfield', fieldLabel : '所属分类', name:'dictEntryName'},
    						{xtype:'displayfield', fieldLabel : '责任部门', name : 'orgName'},
    						{xtype:'displayfield', fieldLabel : '排序', name : 'sort'},
    						{xtype:'displayfield', fieldLabel : '描述', name : 'desc'},
    						{xtype:'displayfield', fieldLabel : '附件名称', name : 'fileName',
	    						renderer:function(value,metaData,record,colIndex,store,view) {
				     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
				     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').downLoadFile()\">" + value + "</a>";
				     			}},
				     		{xtype:'hiddenfield', name:'fileIds'}]
            };
      
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1]
        });

        me.callParent(arguments);
    	me.on('resize',function(p){
			me.setHeight(FHD.getCenterPanelHeight()-30);
		});
    }

});