Ext.define('FHD.view.risk.riskidentify.identifyapprove.IdentifyCollectGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.identifyCollectGrid',
    requires: [
        'FHD.view.risk.assess.utils.GridCells'
    ],
    reloadData: function(businessId,executionId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/riskidentify/findidentifylistbybusinessid.f',//'/findLeaderDept.f';//查询列表
    	me.store.proxy.extraParams.businessId = businessId;
	    me.store.proxy.extraParams.executionId = executionId;
	    me.store.load();
    },
    
    edit : function(scoreObjectId,hIdeaId,hRespopnseId){
    	var me = this;
    	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
			height : 500,
			reloadData: function (scoreObjectId,hIdeaId,hResponseId) {
		        var me = this;
				var detailUrl = "/cmp/risk/findObjectAndMeasureDetailByObjectId.f";
				FHD.ajax({
					params: {
		                objectId : scoreObjectId,
		                hIdeaId : hIdeaId,
		                hResponseId : hResponseId
		            },
		            url: __ctxPath + detailUrl,
					callback : function(data){
				        me.form.setValues({
				            parentName: data.parentName,
				            code:data.code,
				            name:data.name,
				            desc:data.desc,
				            respDeptName : data.respDeptName,
				            relaDeptName : data.relaDeptName,
				            hEditIdea : data.hEditIdea,
				            hResponseIdea : data.hResponseIdea
				        });
					}
				});
    		}
		});
		
		var hEditIdea = Ext.widget('textareafield', {
			rows : 4,
			readOnly : true,
			name : 'hEditIdea',
			margin : '5 0 3 20',
			columnWidth : 1
		});
		
		var editIdeaFieldSet = {
				xtype : 'fieldset',
				collapsible : true,
				title : '反馈意见汇总',
				margin : '10 10 10 10',
				layout: {
					 type: 'column'
				},
				items : [hEditIdea]
		};
		
		me.detailAllForm.add(editIdeaFieldSet);
		
		var responseIdea = Ext.widget('textareafield', {
    			rows : 4,
    			readOnly : true,
    			name : 'hResponseIdea',
    			margin : '5 0 3 20',
    			columnWidth : 1
    		});
    		
		var responseIdeaFieldSet = {
				xtype : 'fieldset',
				collapsible : true,
				title : '应对措施汇总',
				flex : 1,
				margin : '10 10 10 10',
				layout: {
					 type: 'column'
				},
				items : [responseIdea]
		};
		
		me.detailAllForm.add(responseIdeaFieldSet);
		
		me.detailAllForm.reloadData(scoreObjectId,hIdeaId,hRespopnseId);
		/**
		 * add by 宋佳
		 */
		
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
    	window.location.href = __ctxPath + "/access/riskidentify/exportleaderidentifygrid.f?businessId="+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+
    							"&executionId="+me.executionId+"&headerData="+Ext.encode(me.headerDatas);
    },
    
    getColsShow : function(){
    	var me = this;
    	var cols = [
    				{
    					dataIndex:'templateId',
    					invisible:true
    				},{
    					dataIndex:'riskId',
    					invisible:true
    				},{
    					dataIndex:'hId',
    					invisible:true
    				},{
    					dataIndex : 'hIdeaId',
    					hidden : true
    				},{
    					dataIndex : 'hResponseId',
    					hidden : true
    				}, {
    					dataIndex:'objectScoreId',
    					invisible:true
    				},  {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            width : 200
    		        },{
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            width : 400,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('objectScoreId') + "','"+  record.get('hIdeaId') + "','" + record.get('hResponseId') + "') \">" + value + "</a>";
    	     			}
    		        },
    		        {
    					dataIndex : 'rangObjectDeptEmpId',
    					invisible:true
    				},
    				{
    					header: "辨识人",
    					dataIndex : 'assessEmpId',
    					sortable: true,
    					width : 100
    				},
    				{
    					header: "意见",
    					dataIndex : 'empEditIdea',
    					sortable: true,
    					width : 300,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    				},{
    					header : '应对措施',
    					dataIndex : 'response',
    					width : 300,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    				},{
    					header: "汇总辨识意见",
    					dataIndex : 'leadContent',
    					sortable: true,
    					width : 300,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return value;
    		 			}
    				},{
    					header: "汇总应对措施",
    					dataIndex : 'hResponse',
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
        	margin : '0 0 0 0',
        	url : me.url,
        	cols:cols,
        	autoScroll:true,
		    scroll: 'both',//只显示垂直滚动条
		    checked: false,
		    pagable : false,
		    searchable : false,
		    storeAutoLoad:false,
		    columnLines: true,
		    isNotAutoload : false,
		    tbarItems:[{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}]
        });
        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3,4,8,9]);
        });
    }

});