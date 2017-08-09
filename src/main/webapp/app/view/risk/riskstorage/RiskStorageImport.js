/*
 * 按照公司查询所有风险库
 * zhengjunxiang
 * */

Ext.define('FHD.view.risk.riskstorage.RiskStorageImport', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.riskstorageimport',
	
	/**
	 * 常量
	 */
	treeUrl:__ctxPath + "/risk/findRiskStorage.f",
	companyUrl:__ctxPath + "/risk/findHierarchyCompany.f",
	importUrl:__ctxPath + "/risk/importRiskStorage.f",
	
	initComponent: function () {//__user.companyName = '中航工业集团';__user.companyId = '1';
        var me = this;
        var cols=[
        		{dataIndex : 'id',hidden:true},
    			{text: '名称',dataIndex: 'name',flex: 2,hideable:false,sortable: false,xtype: 'treecolumn',
    			    renderer:function(value,metaData,record,colIndex,store,view) { 
    					metaData.tdAttr = 'data-qtip="'+value+'"';  
    				    return value;
    				}
    			},
    			{text: '编号',dataIndex: 'code',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    				    return value;
	    				}
    			},
    			{text: '责任部门',dataIndex: 'dutyDepartment',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    				    return value;
	    				}
    			},
    			{text: '相关部门',dataIndex: 'relativeDepartment',flex: 1,hideable:false,sortable: false,
	    			    renderer:function(value,metaData,record,colIndex,store,view) { 
	    					metaData.tdAttr = 'data-qtip="'+value+'"';  
	    				    return value;
	    				}
    			}
		];

        /** 
         *  公司列表下拉框
         */
//        me.companyStore = Ext.create('Ext.data.Store',{
//			fields : ['id', 'name'],
//			proxy : {
//						type : 'ajax',
//						url : me.companyUrl,
//						reader : {
//							type : 'json',
//							root : 'datas',
//							totalProperty : 'totalCount'
//						}
//					} 
//		});
//        me.companyCombo = Ext.create("Ext.form.field.ComboBox",{
//        	width:260,
//        	fieldLabel: '公司',
//        	labelWidth:40,
//			store :me.companyStore,
//			valueField : 'id',
//			name:'company',
//			displayField : 'name',
//			value:__user.companyName,
//			triggerAction: 'all',
//			listeners:{
//				change:function(c, newValue, oldValue, eOpts){
//					//设置导入按钮的状态
//					if(newValue==__user.companyId){
//						me.importBtn.setDisabled(true);
//					}else{
//						me.importBtn.setDisabled(false);
//					}
//					me.reloadData();
//				}
//			}
//        });
		var companyStoreData = {};
		FHD.ajax({
			async:false,
			url : me.companyUrl,
			callback : function(data) {
				companyStoreData = data;
			}
		});
        me.companyStore = Ext.create('Ext.data.TreeStore',{
        	root:companyStoreData
//			proxy : {
//				type : 'ajax',
//				url : me.companyUrl
//			},
//			root : {
//				id:'1',
//				text:'集团',
//				expanded:true,
//				children:[{
//					id:'XA',
//					text:'西安公司',
//					leaf:true
//				},{
//					id:'XD',
//					text:'信达公司',
//					leaf:true
//				}]
//			}
		});
        me.companyCombo = Ext.create("Ext.ux.TreePicker",{
        	width:260,
        	autoScroll:true,
        	fieldLabel: '公司',
        	labelWidth:40,
        	displayField : 'text',
        	forceSelection : true,// 只能选择下拉框里面的内容    
	        editable : false,// 不能编辑    
	    	store : me.companyStore,
	    	listeners:{
	    		select:function(picker, record, eOpts){
	    			var id = record.data.id;
	    			//设置导入按钮的状态
					if(id ==__user.companyId){
						me.importBtn.setDisabled(true);
					}else{
						me.importBtn.setDisabled(false);
					}
					me.reloadData();
	    		}
			}
        });
        me.companyCombo.setRawValue(__user.companyName);
        /**
         * 导入按钮
         */
        me.importBtn = Ext.create("Ext.button.Button",{
			text : '导入风险及应对',
			margin : '0 10 0 10',
			tooltip: '导入风险库到本公司',
			iconCls : 'icon-ibm-action-export-to-excel',
			disabled:true,
			handler:function(){
            	me.body.mask("导入中...","x-mask-loading");
            	FHD.ajax({
    				url : me.importUrl,
    				params : {
    					companyId:me.companyCombo.getValue()	//从哪个公司导入
    				},
    				callback : function(data) {
    					me.body.unmask();
    					if(data.success){
    						//设置到当前公司
    						me.companyCombo.setValue(__user.companyId);
    						me.companyCombo.setRawValue(__user.companyName);
    						me.reloadData();
    						FHD.notification("风险库导入成功","提示");
    					}else{
    						alert("不允许将当前公司风险数据导入到当前该公司！");
    					}
    				}
    			});
			}
		});
		
		var root = {
			id : 'root',
			name : __user.companyName,
			leaf : false,
			expanded : true
		};
        me.treegrid = Ext.create('FHD.ux.TreeGridPanel',{
        	useArrows: true,
	        rootVisible: true,
	        multiSelect: false,
	        border:true,
	        rowLines:true,
	        checked: false,
	        autoScroll:true,
		    searchable : false,
		    border : false,
		    cols: cols,
            root:root,
            url : me.treeUrl,
            extraParams : {
				companyId : __user.companyId	//默认登录用户的companyid
            },
            tbarItems:[me.companyCombo,me.importBtn]
        	
        });
		Ext.applyIf(me, {
			layout : 'fit',
			border : false,
			items : me.treegrid
        });
    		
    	me.callParent(arguments);
	},
	
	reloadData : function(){
		var me = this;
		var companyId = me.companyCombo.getValue();
		var companyName = me.companyCombo.getRawValue();

		me.treegrid.store.proxy.extraParams.companyId = companyId;
		var root = {
			id:'root',
			name:companyName,
			leaf:false,
			expanded:true
		};
		me.treegrid.setRootNode(root);
	}
		
});