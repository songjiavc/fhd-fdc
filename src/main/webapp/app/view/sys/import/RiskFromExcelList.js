Ext.define('FHD.view.sys.import.RiskFromExcelList', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.riskfromexcellist',

	url :__ctxPath+ '/dataimport/risk/showRiskFromRiskList.f',
	extraParams: {
	},
	pagable : false,
	viewConfig:{
       getRowClass : function(record,rowIndex,rowParams,store){   
//       console(record);
       }
	},
	closeWin: function(){
    	var me = this;
		Ext.getCmp(me.winId).close();
    },
	confirm : function(){
		var me = this;
		Ext.MessageBox.show({
				title : '提示',
				width : 260,
				msg : '确定导入么？',
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if(btn == 'yes') {
						me.riskImportFun();
						me.closeWin();
					}
				}
			});
	},
	riskImportFun : function(){},
	validate : function(){
		var me = this;
		Ext.Msg.alert('提示','数据验证成功！');
		FHD.ajax({
			url : __ctxPath+ '/dataimport/risk/validateRiskFromExcelData.f',
			params : {
			},
			callback : function(data) {
				me.reloadData()
			}
		});
	},
	initComponent : function() {
		var me = this;
		
//		me.tbarItems = [
//			{
//				name : 'standard_list_add',
//				id : 'riskfromExcelListAddSaveId',
//				text : '导入数据',
//				tooltip: '导入数据',
//				iconCls : 'icon-save',
//				handler : me.confirm,
//				scope : this
//			}
//			,
//			{
//				name : 'standard_list_add',
//				text : '重新验证',
//				tooltip: '重新验证',
//				iconCls : 'icon-arrow-refresh-blue',
//				handler : me.validate,
//				scope : this
//			}
//		];
		
		me.cols = [
			{header : 'ID', dataIndex : 'id', sortable : true, hidden:true},
			{
				header : '', 
				dataIndex : 'isPass', 
				hidden:true,
				renderer: function (value, metaData, record, colIndex, store, view) {
					var icon = null;
					if('yes' == value){
						icon = "<image src='images/icons/state_ok.gif'/>";
					}else {
						icon = "<image src='images/icons/state_error.gif'/>";
						
					}
	                return icon;
	            }
			},
//			{header : '对应公司', dataIndex : 'companyId', sortable : true, flex : 1},
			{
	            header: '行号',
	            dataIndex: 'exRow',
	            sortable: true,
	            flex: 2
	        },
			{header : '父级编号', dataIndex : 'parentCode', sortable : true, flex : 2},
			{header : '父级名称', dataIndex : 'parentName', sortable : true, flex : 3},
			{header : '风险编号', dataIndex : 'riskCode', sortable : true, flex : 2},
			{header : '风险名称', dataIndex : 'riskName', sortable : true, flex : 4,
				renderer: function (value, metaData, record, colIndex, store, view) {
					metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
		        }
			},
			{header : '风险描述', dataIndex : 'edesc', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },
//			{header : 'ID序列', dataIndex : 'idSeq', sortable : true, flex : 1},
//			{header : '排序', dataIndex : 'esort', sortable : true, flex : 1},
//			{header : '是否叶子', dataIndex : 'isLeaf', sortable : true, flex : 1},
//			{header : '是否继承', dataIndex : 'isInherit', sortable : true, flex : 1},
			{header : '主责部门', dataIndex : 'respOrgs', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },
			{header : '相关部门', dataIndex : 'relaOrgs', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },
			{header : '影响指标', dataIndex : 'impactTarget', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
			},
			{header : '影响流程', dataIndex : 'impactProcess', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },
			{header : '评估模板', dataIndex : 'assessmentTemplate', sortable : true, flex : 2,
				renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
			},
			{header : '发生可能性', dataIndex : 'probability', sortable : true, flex : 2},
			{header : '影响程度', dataIndex : 'impact', sortable : true, flex : 2},
			{header : '管理紧迫性', dataIndex : 'urgency', sortable : true, flex : 2},
			{header : '风险水平', dataIndex : 'riskLevel', sortable : true, flex : 2},
			{header : '风险状态', dataIndex : 'riskStatus', sortable : true, flex : 2},
            {header : '分库标志', dataIndex : 'SCHM', sortable : true, flex : 2},
            {header : '所属部门', dataIndex : 'createOrg', sortable : true, flex : 2},
			{header : '验效结果', dataIndex : 'comment', sortable : true, flex : 3,
				renderer:function(value,metaData,record,colIndex,store,view) {
					metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
 				}
			}
//			,
//			{
//				header : '操作', 
//				dataIndex : 'id', 
//				sortable : true, 
//				flex : 1,
//				renderer: function (value, metaData, record, colIndex, store, view) {
//	                value = "修改";
//	                metaData.tdAttr = 'data-qtip="'+value+'"';
//	                var id = record.data['id'];
//	                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRiskEditForm('" + record.data.id + "')\" >" + "<image src='images/icons/tool_edit.gif'/>" +"</a>";
//	            }
//			}
		];
		
		//me.cols.push({xtype: 'rownumberer',resizable:true});
		
        Ext.apply(me,{ 
        	layout : {
        		type : 'fit'
        	},
//        	storeGroupField : '',
        	checked : false,
//        	columnLines: true,
        	border : false,
        	storeAutoLoad:false,
        	viewConfig: {
                //行背景色间隔显示
                getRowClass : function(record, rowIndex, rp, ds){
                    if(record.data.isPass == "no"){
                    	Ext.getCmp('riskfromExcelListAddSaveId').disable();
                    	return record.get("comment") ? "row-s" : "";
                    }
                }
        	}
		});
        
		me.callParent(arguments);
	},
	showRiskEditForm : function(id){
		var me=this;
		var winId = "win" + Math.random()+"$ewin";
    	me.riskFromExcelEditForm=Ext.create('FHD.view.sys.import.RiskFromExcelEditForm',{
    		winId:winId,
    		isWindow: true
    	});
		
		me.riskFromExcelEditForm.reloadData(id);
		
		var win = Ext.create('FHD.ux.Window',{
			id:winId,
			title:'风险数据详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			maximized :true,
			buttonAlign: 'center'
    	}).show();
    	
    	win.add(me.riskFromExcelEditForm);
	},
	setstatus: function(){
    	var me = this;
    	
        var length = me.getSelectionModel().getSelection().length;
        me.down('[name=standard_list_del]').setDisabled(length === 0);
        if(length != 1){
        	me.down('[name=standard_list_edit]').setDisabled(true);
        }else{
        	me.down('[name=standard_list_edit]').setDisabled(false);
        }
    },
	reloadData :function(){
		var me = this;
		me.store.load();
	}
});