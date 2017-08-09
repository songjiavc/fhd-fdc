Ext.define('FHD.view.risk.riskidentify.approvetidy.ApproveTidyGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.approveTidyGrid',
    
    reloadData: function(businessId,executionId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/riskidentify/findapprovetidygrid.f';//查询列表
    	me.store.proxy.extraParams.businessId = businessId;
	    me.store.load();
    },
    
    edit : function(scoreObjectId, riskId){
    	var me = this;
    	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
			height : 500,
			riskId : riskId,
			objectId:scoreObjectId
		});
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.detailAllForm]
		});
		me.formwindow.show();
    },
    
  //导出grid列表
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	me.headerDatas = [];
    	var items = me.columns;
			Ext.each(items,function(item){
				if(!item.hidden){
				var value = {};
				value['dataIndex'] = item.dataIndex;
            	value['text'] = item.text;
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath + "/?businessId="+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+
    							"&executionId="+me.executionId+"&headerData="+Ext.encode(me.headerDatas);
    },
    
    getColsShow : function(){
    	var me = this;
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    				{
    					dataIndex:'scoreObjectId',
    					hidden:true
    				},
    		        {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.5
    		        },{
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            flex:2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('scoreObjectId') + "','" + record.get('riskId') + "')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        },
    		        {
    					header: "辨识人",
    					dataIndex : 'assessEmpId',
    					sortable: true,
    					flex:.3
    				},
    				{
    					header: "部门",
    					dataIndex : 'deptName',
    					sortable: true,
    					flex:.3
    				},
    				{
    					header: "状态",
    					dataIndex : 'deleteStatus',
    					sortable: true,
    					flex:.3,
    					 renderer:function(value,metaData,record,colIndex,store,view) {
    						 var label = "";
    							if(value == 100){
    								label = "<font color='red'>(已删除)</font> ";
    							}else{
    								label = "<font color='green'>(正常)</font> ";
    							}
    		            		return label;
     	     			}
    				}
    	        ];
    	
    	return cols;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols  = me.getColsShow();
        
        Ext.apply(me,{
        	margin : '0 0 0 0',
        	url : me.url,
        	cols:cols,
		    border: true,
        	//autoScroll:true,
		    scroll: 'vertical',//只显示垂直滚动条
		    checked: false,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    storeAutoLoad : false,
		    tbarItems:[{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}]
        });
        
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [2,3]);
        });
    }

});