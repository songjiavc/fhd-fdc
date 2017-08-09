/**
 * 
 * 定性评估表格
 */

Ext.define('FHD.view.risk.assess.AssessApproveGridSecrecy', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.assessApproveGridSecrecy',
    
    requires : [ 'FHD.view.risk.assess.utils.DeptLeadIdeaSecrecy'],
    
    riskDatas : null,
    
    edit : function(scoreObjectId, riskId){
    	var me = this;
    	me.quaAssessEdit = Ext.widget('deptLeadIdeaSecrecy');
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
//			collapsible:true,
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
			{
					text: '对比',
					handler:function()
					{
					me.compareRisk(riskId);
					}
				},     
				{
					text: '修改',
					handler:function(){
						FHD.ajax({
				            url: __ctxPath + '/assess/quaassess/saveDeptEditIdea.f',
				            params: {
				            	editIdeaContent : me.quaAssessEdit.editIdea.getValue(),
				            	responseIdeaContent : me.quaAssessEdit.responseIdea.getValue(),
				            	deptLeadCircuseeId : me.quaAssessEdit.DeptIds.getValue()
				            },
				            callback: function (data) {
				            	me.store.load();
				            	Ext.MessageBox.alert('修改信息','修改成功');
				            	me.formwindow.close();
				            	//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
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
		me.quaAssessEdit.load(scoreObjectId, me.assessApproveSubmit.businessId, riskId);
		me.quaAssessEdit.on('resize',function(p){
    		me.quaAssessEdit.detailAllForm.setHeight(me.formwindow.getHeight()-62);
	   	});
    },
    
    
//对比差异    
compareRisk:function(riskId)
	{
		var me=this;
	me.riskRelateForm = Ext.create('FHD.view.risk.riskStorageSpecial.RiskCompareForm',{
            showbar:false,
            type: 're',
            border: false,
            schm : me.schm,
            autoDestroy: true
        });
	  	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
//			collapsible:true,
			width:800,
			height:500,
			title : '风险信息对比',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.riskRelateForm],
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
		me.riskRelateForm.reloadData(riskId);
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
				if('riskIcon'==item.dataIndex){
					value['text'] = '风险等级';
				}else if('isSummarizingRiskIcon'==item.dataIndex){
					value['text'] = '汇总等级';
				}else{
					value['text'] = item.text;
				}
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	//exportFileName = '评估任务审批数据';
    	window.location.href = __ctxPath + "/assess/quaassess/exportfindleaderdeptgrid.f?businessId="+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+
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
    				},
    				{
    					dataIndex:'hrId',
    					hidden:true
    				},
    				{
    					dataIndex:'objectScoreId',
    					hidden:true
    				},
    		        {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:.2
    		        },{
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:.5,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('objectScoreId') + "','" + record.get('riskId') + "')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        },
//    		        {
//    		            header: "操作",
//    		            dataIndex: 'parentRiskName',
//    		            sortable: true,
//    		            flex:.3,
//    		            renderer:function(value,metaData,record,colIndex,store,view) {
//    		            	return "<a href=\"javascript:void(0);\" " +
//    		    			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('objectScoreId') + "','" + record.get('riskId') + "')\">反馈意见</a>";
//    		 			}
//    		        },
    		        {
    		        	header: "评价汇总意见",
    					dataIndex : 'hEditIdeaContent',
    					sortable: true,
    					flex:.5,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    		        },
    		        {
    		        	header: "应对汇总意见",
    					dataIndex : 'hrResponseIdeaContent',
    					sortable: true,
    					flex:.5,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    		        },
    		        {
    					dataIndex : 'isSummarizingRiskIcon',
    					sortable: true,
    					width:40,
    					hidden:true,
    		        	cls: 'grid-icon-column-header grid-statushead-column-header',
    		        	menuDisabled:true,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    		            	return "<div id=" + record.get('riskId') + " style='width: 23px; height: 19px; background-repeat: " +
    		            			"no-repeat;background-position: center top;' class='" + record.get('isSummarizingRiskIcon') + "'/>";
    		 			}
    				},
    		        {
    					dataIndex : 'rangObjectDeptEmpId',
    					hidden:true
    				},
    				{
    					header: "评估人",
    					dataIndex : 'assessEmpId',
    					sortable: true,
    					flex:.2
    				},
    				{
    					header: "评价意见",
    					dataIndex : 'empEditIdea',
    					sortable: true,
    					flex:.5,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    				}
    				,
    				{
    					header: "应对意见",
    					dataIndex : 'empResponseIdea',
    					sortable: true,
    					flex:.5,
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
        me.riskDatas = [];
        
        var cols  = me.getColsShow();
        
        for(var i = 0 ; i < me.array.length; i++){
        	cols.push(me.array[i]);
        }
        
        cols.push({dataIndex:'riskIcon', sortable : true, width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header',
        	menuDisabled:true,
        	renderer:function(value,metaData,record,colIndex,store,view) {
            	var value = {};
            	value['rangObjectDeptEmpId'] = record.get('rangObjectDeptEmpId');
            	value['riskId'] = record.get('riskId');
            	me.riskDatas.push(value);
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + record.get('riskIcon') + "'/>";
 			}
        });
        
        Ext.apply(me,{
        	region:'center',
        	margin : '0 0 0 0',
        	url : me.url,
        	cols:cols,
		    border: false,
        	autoScroll:true,
        	storeAutoLoad:false,
		    scroll: 'vertical',//只显示垂直滚动条
		    checked: false,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : false,
		    tbarItems:[{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}]
        });
        
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3,4,5,6,7,8,9]);
        });
        
        me.on('resize',function(p){
        	if(Ext.getCmp(me.assessApproveSubmit.winId)){
        		me.setHeight(Ext.getCmp(me.assessApproveSubmit.winId).getHeight() - 105);
            }
    	});
    }

});