Ext.define('FHD.view.sys.template.TemplateManageGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.templateManageGrid',
 	requires: [
	],
	
	// 添加/修改方法
    edit: function(isAdd){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var cardPanel = me.up('templateManageCard');
    	var editPanel = cardPanel.templateManageEdit;
    	if(isAdd){//新增
    		editPanel.clearFormData();
    		var nodeId = cardPanel.up('templateManageMain').templateManageTree.currentNodeId;
    		editPanel.dictEntryId.setValue(nodeId);
    	}else{//修改
    	   var length = selection.length;
    	   if (length >= 2) {//判断是否多选
				FHD.notification(FHD.locale.get('fhd.common.updateTip'),FHD.locale.get('fhd.common.prompt'));
				return;
    	   }
    	   editPanel.reLoadData(selection[0].get('id'));
    	}
    	cardPanel.showTemplateManageEdit();
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
    					url :  __ctxPath + '/sys/templatemanage/removetemplatemanagesbyids.f',
    					params : {
    						ids:ids.join(',')
    					},
    					callback : function(data){
							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
							me.store.load();
    					}
    				});
    			}
    		}
    	});
    	
    },
    
    //设为默认
    setDefault: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var length = selection.length;
	   	if (length >= 2) {//判断是否多选
			FHD.notification('只能设置一个默认模板',FHD.locale.get('fhd.common.prompt'));
			return;
	   	}
	   	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : '确认设置为默认模板吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				FHD.ajax({//ajax调用
    					url :  __ctxPath + '/sys/templatemanage/setdefaulttemplatebytemplateid.f',
    					params : {
    						id: selection[0].get('id')
    					},
    					callback : function(data){
							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
							me.store.load();
    					}
    				});
    			}
    		}
    	});
    	
    },
    
    //重新加载数据方法
    reloadData: function(nodeId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/sys/templatemanage/findtemplatemanagesbyentryid.f';
 		me.store.proxy.extraParams.entryId = nodeId;
 		me.store.load();
    },
    
    setstatus : function(me){//设置按钮可用状态
    	if (me.down("[name='template_edit']")) {
            me.down("[name='template_edit']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='template_del']")) {
            me.down("[name='template_del']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='template_default']")) {
            me.down("[name='template_default']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				invisible:true
			},
	        {
	            header: "模板名称",
	            dataIndex: 'name',
	            sortable: true,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').edit(false)\">" + value + "</a>";
     			}
	        },
	        {
	            header: "模板类型",
	            dataIndex: 'dictName',
	            sortable: true,
	            flex: 1
	        },
	        {
	            header: "是否默认",
	            dataIndex: 'isDefault',
	            sortable: true,
	            width: 100,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				if('1'==value){
     					return '是';
     				}else{
     					return '否';
     				}
     			}
	        }
        ];
       
        Ext.apply(me,{
        	region:'center',
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				me.edit(true);
        			}
    			},{
        			btype:'edit',
        			disabled:true,
        			name : 'template_edit',
        			handler:function(){
        				me.edit(false);
        			}
    			},{
        			btype:'delete',
        			disabled:true,
        			name : 'template_del',
        			handler:function(){
        				me.del(me);
        			}
    			},{
        			text:'设为默认',
        			iconCls:'icon-accept',
        			disabled:true,
        			name : 'template_default',
        			handler:function(){
        				me.setDefault();
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