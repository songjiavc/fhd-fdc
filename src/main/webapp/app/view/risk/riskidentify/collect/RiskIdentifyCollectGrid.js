Ext.define('FHD.view.risk.riskidentify.collect.RiskIdentifyCollectGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskIdentifyCollectGrid',
    requires : [],
    
    reloadData: function(businessId,executionId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/findIdentifyByDept.f';//查询列表
    	me.store.proxy.extraParams.businessId = businessId;
	    me.store.proxy.extraParams.executionId = executionId;
	    me.store.load();
    },
    
    edit : function(scoreObjectId, riskId){
    	var me = this;
    	me.quaAssessEdit = Ext.create('FHD.view.risk.assess.utils.DeptLeadIdea');
    	me.formwindow = new Ext.Window({
    		layout:'fit',
 			iconCls: 'icon-show',//标题前的图片
 			modal:true,//是否模态窗口
 			collapsible:true,
 			title:'风险事件',
 			width:1000,
 			height:500,
 			autoScroll : true,
 			layout: {
 				type: 'vbox',
 	        	align:'stretch'
 	        },
 			maximizable:true,//（是否增加最大化，默认没有）
 			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
				{
					text: '修改',
					handler:function(){
						FHD.ajax({
				            url: __ctxPath + '/assess/quaassess/saveDeptEditIdea.f',
				            params: {
				            	editIdeaContent : me.quaAssessEdit.editIdea.getValue(),
				            	deptLeadCircuseeId : me.quaAssessEdit.DeptIds.value,
				            	responseIdeaContent : me.quaAssessEdit.responseIdea.getValue()
				            },
				            callback: function (data) {
				            	me.store.load();
				            	Ext.MessageBox.alert('修改信息','修改成功');
				            	me.formwindow.close();
				            }
				        });
					}
				},     
    			{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    	    ]
		});
		me.formwindow.show();
		me.quaAssessEdit.load(scoreObjectId, me.businessId, riskId);
		me.quaAssessEdit.on('resize',function(p){
    		me.quaAssessEdit.detailAllForm.setHeight(me.formwindow.getHeight()-62);
	   	});
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
    	window.location.href = __ctxPath + "/access/riskidentify/exportleaderidentifygrid.f?businessId="+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+
    							"&executionId="+me.executionId+"&headerData="+Ext.encode(me.headerDatas);
    },
    
    getColsShow : function(){
    	var me = this;
    	var cols = [
    				{
    					dataIndex:'templateId',
    					hidden:true
    				},{
    					dataIndex:'riskId',
    					hidden:true
    				},{
    					dataIndex:'hId',
    					hidden:true
    				},{
    					dataIndex:'objectScoreId',
    					hidden:true
    				},{
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            //align: 'center',
    		            width : 100
    		        },{
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            width : 300,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('objectScoreId') + "','" + record.get('riskId') + "')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        },{
    		        	header: "汇总意见",
    					dataIndex : 'hEditIdeaContent',
    					sortable: true,
    					width : 400,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    		        },{
    		        	header: "汇总应对",
    					dataIndex : 'hResponse',
    					sortable: true,
    					width : 400,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    		        },{
    					dataIndex : 'rangObjectDeptEmpId',
    					hidden:true
    				},{
    					header: "辨识人",
    					dataIndex : 'assessEmpId',
    					sortable: true,
    					width : 70
    				},{
    					header: "辨识意见",
    					dataIndex : 'empEditIdea',
    					sortable: true,
    					width : 300,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    				},{
    					header: "应对措施",
    					dataIndex : 'response',
    					sortable: true,
    					width : 300,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
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
        	region:'center',
        	margin : '0 0 0 0',
        	url : me.url,
        	cols:cols,
		    border: false,
        	autoScroll:true,
		    scroll: 'both',//只显示垂直滚动条
		    checked: false,
		    pagable : false,
		    searchable : true,
		    storeAutoLoad:false,
		    columnLines: true,
		    isNotAutoload : false,
		    tbarItems:[{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}]
        });
        
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [5,6,7,8]);
        });
    }

});