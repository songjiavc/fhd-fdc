Ext.define('FHD.view.risk.riskidentify.approve.RiskIdentifyPreviewRiskGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskIdentifyPreviewRiskGrid',
    requires: [
    			
    ],
	//加载列表数据   
    loadData: function(planId,deptId){
    	var me = this;
    	me.deptId = deptId;
    	me.store.proxy.url = __ctxPath + '/access/planconform/findrisksgridbyplanidordeptid.f';
	 	me.store.proxy.extraParams.planId = planId;
	 	me.store.proxy.extraParams.deptId = deptId;//根据部门查看，为空时，默认查询计划下全部
	 	me.store.load();
    },
    //弹出风险详细信息组件
    showRiskWindow: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
		var riskId = selection[0].get('riskId');
		var objectId = selection[0].get('scoreObjId');
		if("" != riskId){
			me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
				height : 500,
				riskId : riskId,
				objectId:objectId
			});
	    	me.formwindow = new Ext.Window({
				layout:'fit',
				iconCls: 'icon-show',//标题前的图片
				modal:true,//是否模态窗口
				collapsible:true,
				width:800,
				height:500,
				title : '风险详细信息查看',
				maximizable:true,//（是否增加最大化，默认没有）
				constrain:true,
				items : [me.detailAllForm],
				buttons: [
	    			{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    			}
	    	    ]
			});
			me.formwindow.show();
		}
    },
    //导出grid列表
    exportChart:function(sheetName, exportFileName){
    	var me=this;
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath + "/access/formulateplan/exportriskscoreobjspage.f?planId="
    		+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+"&deptId="+me.deptId;
    },
     
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.cols = [ {dataIndex : 'id', hidden : true}, 
        			{header : "部门", dataIndex : 'deptName', sortable : false, flex : 1},
         			{header : "上级风险", dataIndex : 'parentRiskName', sortable : false, flex : 1,
         				renderer:function(dataIndex) { 
		    				  return dataIndex.split(' ')[0];
    				}},
                    {header : "风险名称", dataIndex : 'riskName', sortable : true, flex : 3,
		                renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
							metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
							return "<a href=\"javascript:void(0);\">"+value+"</a>";
						},
						listeners:{
			        		click:{
			        			fn:function(g,d,i){
			        				me.showRiskWindow();
		        				}
        					}
        				}},
					{header : "责任类型", dataIndex : 'deptType', sortable : false, flex : .5,
						renderer:function(dataIndex) { 
		    				  if(dataIndex == "M"){
		    					  return "责任部门";
		    				  }else if(dataIndex == "A"){
		    					  return "相关部门";
		    				  }else if(dataIndex == "C"){
		    					  return "参与部门";
		    				  }
    				}},
				    {dataIndex : 'riskId', hidden : true},
				    {dataIndex : 'scoreDeptId', hidden : true},
				    {dataIndex : 'scoreObjId', hidden : true}
                  ];
        me.tbar = [
        		   {iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}
                   ];
        
        Ext.apply(me, {
        	region:'center',
            cols:me.cols,
            tbarItems:me.tbar,
		    border: false,
		    columnLines: true,
		    checked: true,
		    pagable : false,
		    searchable : true,
		    storeAutoLoad: false
        });

        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3,4]);
        });
    }

});


