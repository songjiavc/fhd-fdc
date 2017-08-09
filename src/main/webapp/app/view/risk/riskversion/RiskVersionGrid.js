Ext.define('FHD.view.risk.riskversion.RiskVersionGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskversiongrid',
 	requires: [
	],
	
	// 添加/修改方法
    edit: function(isAdd){
    	var me = this;
    	var versionCard = me.up('riskversioncardmain');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(isAdd){//新增
    		versionCard.versionForm.clearFormData();
    		versionCard.showVersionForm();
    	}else{
    		var length = selection.length;
    		if (length >= 2) {//判断是否多选
    	        FHD.notification(FHD.locale.get('fhd.common.updateTip'),FHD.locale.get('fhd.common.prompt'));
    	        return;
    		}else{
    			versionCard.versionForm.verId = selection[0].get('id');
    			versionCard.versionForm.reLoadData(selection[0].get('id'));
    			versionCard.showVersionForm();
    		}
    	}
    } ,
    
    //重新加载数据方法
    reloadData: function(){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/risk/riskhistory/queryriskversionspage.f';
 		me.store.proxy.extraParams.schm = me.typeId;//版本分库标识
    },
    
    setstatus : function(me){//设置按钮可用状态
    	if (me.down("[name='version_edit']")) {
            me.down("[name='version_edit']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    
    //查看版本风险明细
    viewDetails: function(rowIndex){
    	var me = this;
    	me.getSelectionModel().select(rowIndex);
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var versionCard = me.up('riskversioncardmain');
    	versionCard.detailtGrid.reloadData(selection[0].get('id'));//版本id
    	versionCard.showDetailtGrid();
    	versionCard.detailtGrid.verId = selection[0].get('id');
    },
    
    //展示新增风险列表
    showAddDelRisks: function(isAdd){
    	var me = this;
    	var versionCard = me.up('riskversioncardmain');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	versionCard.addDelRisksGrid.reloadData(selection[0].get('id'),isAdd);
    	versionCard.showAddDelGrid();
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
	            header: "版本名称",
	            dataIndex: 'versionName',
	            sortable: false,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\">" + value + "</a>";
     			},
     			listeners: {
     				click: function(){
     					me.edit(false);
     				}
     			}
	        },
	        {
	            header: "风险总数",
	            dataIndex: 'riskCount',
	            sortable: false,
	            flex: 1
	        },{
	            header: "新增风险数",
	            dataIndex: 'addRisk',
	            sortable: false,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\">" + value + "</a>";
     			},
     			listeners: {
     				click: function(){
     					me.showAddDelRisks(true);
     				}
     			}
	        },{
	            header: "删除风险数",
	            dataIndex: 'deleteRisk',
	            sortable: false,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\">" + value + "</a>";
     			},
     			listeners: {
     				click: function(){
     					me.showAddDelRisks(false);
     				}
     			}
	        },{
	            header: "创建人",
	            dataIndex: 'createBy',
	            sortable: false,
	            width:40,
	            flex:1
	        },
			{header:'操作',dataIndex:'cz',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
			       xtype:'actioncolumn',
			       items: [{
		                icon: __ctxPath+'/images/icons/application_form_magnify.png',  // Use a URL in the icon config
		                tooltip: '查看明细',
		                handler: function(grid, rowIndex, colIndex) {
		                    me.viewDetails(rowIndex);
		                }
		            }]
			  }
        ];
       
        Ext.apply(me,{
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				me.edit(true);
        			}
    			},'-',{
        			btype:'edit',
        			disabled:true,
        			name : 'version_edit',
        			handler:function(){
        				me.edit(false);
        			}
    			}],
		    border: false,
		    checked : true,
		    autoDestroy: true,
		    pagable : true
        });
       
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);

    }

});