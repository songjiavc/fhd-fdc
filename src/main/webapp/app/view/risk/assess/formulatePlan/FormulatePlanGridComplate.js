/**
 * 
 * 计划制定表格
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanGridComplate', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.formulatePlanGridComplate',
	
	newRiskTidyManClick : function(url, businessId, executionId, titles){
		
		var me = this;
		var centerPanel = Ext.getCmp('center-panel');
//		if(me.newRiskTidyMan){
//			centerPanel.remove(me.newRiskTidyMan);
//			me.newRiskTidyMan = centerPanel.add(Ext.create('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan',{
//				businessId: businessId,
//				executionId: executionId,
//				result : '',
//				closable:true,
//				title: titles
//	    	}));
//			centerPanel.setActiveTab(me.newRiskTidyMan);
//		}else{
//			me.newRiskTidyMan = centerPanel.add(Ext.create('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan',{
//				businessId: businessId,
//				executionId: executionId,
//				result : '1111',
//				closable:true,
//				title: titles
//	    	}));
//			centerPanel.setActiveTab(me.newRiskTidyMan);
//		}
		
		
		me.newRiskTidyMan = centerPanel.add(Ext.create('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan',{
			businessId: businessId,
			executionId: executionId,
			result : '1111',
			closable:true,
			title: titles
    	}));
		centerPanel.setActiveTab(me.newRiskTidyMan);
		
	},
	
	edit : function(isAdd){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	me.pid = selection[0].get('id');
    	me.previewWindow(me,me.pid);//预览窗口
    },
    //预览窗口
    previewWindow:function(me,pid){
    	me.formulateplanpreview = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanPreview',{
    		pid: pid,
    		businessId: me.pid
    	});
    	me.formulateplanpreview.form.load({
    	        url:__ctxPath + '/access/formulateplan/querypreviewpanelbyplanId.f',
    	        params:{pid:pid},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
		me.formulateplanpreview.formulatasubmitpreviewGridPanel.store.proxy.url = __ctxPath + '/access/formulateplan/queryriskcircuseegrid.f';
		me.formulateplanpreview.formulatasubmitpreviewGridPanel.store.proxy.extraParams.planId = pid;
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.formulateplanpreview]
		}).show();
	},
    
    //计划整理
    makeUpResult: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	var businessId = selection[0].get('id');
    	//查询是否到评估结果审批节点
    	FHD.ajax({//ajax调用
				url :  __ctxPath + '/access/formulateplan/findexcutionidbybusinessid.f',
				params : {
					businessId:businessId
				},
				callback : function(data){
					if(data){
						
						if('true'==data.data.resultNode){
							me.newRiskTidyManClick('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan',
									businessId, 
									data.data.executionId, 
									'评估结果-' + selection[0].get('planName'));
						}
					}
				}
			});
    	
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.businessId;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "计划名称",
	            dataIndex: 'planName',
	            sortable: true,
	            width:40,
	            flex:2,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').edit()\">"+value+"</a>";
     			}
	        },{
	            header: "负责人",
	            dataIndex: 'responsName',
	            sortable: false,
	            width:40,
	            flex:.5
	        },{
	            header: "联系人",
	            dataIndex: 'contactName',
	            sortable: false,
	            width:40,
	            flex:.5
	        },{
	            header: "开始时间",
	            dataIndex: 'beginDataStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "结束时间",
	            dataIndex: 'endDataStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "处理状态",
	            dataIndex: 'dealStatus',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(dataIndex) { 
    				  if(dataIndex == "N"){
    					  return '<span style="color:red;">未开始</span>';
    				  }else if(dataIndex == "H"){
    					  return '<span style="color:green;">处理中</span>';
    				  }else if(dataIndex == "F"){
    					  return "已完成";
    				  }else if(dataIndex == "A"){
    					  return "逾期";
    				  }else if(dataIndex == "E"){
    					  return "已评价";
    				  }else if(dataIndex == "R"){
    					  return "已复核";
    				  }
    			}
	        },
	        {header:'操作',dataIndex:'caozuo',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
	        renderer: function(value, metaData, record, colIndex, store, view) { 
						return '<a href="javascript:void(0);" class="icon-view" onclick="Ext.getCmp(\''+me.id+'\').makeUpResult()">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>';
					}
				      /* xtype:'actioncolumn',
				       items: [{
				    	   	iconCls: 'icon-view',
			                tooltip: '评估结果',
			                handler: function(grid, rowIndex, colIndex) {
			                    grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			grid.getSelectionModel().select(rows,true);
			                    me.makeUpResult(me);
			                }
			            }]*/
			}
        ];
       
        Ext.apply(me,{
        	region:'center',
        	url : __ctxPath + "/access/formulateplan/queryAccessPlansPage.f?status="+'F'+'&planType='+'riskAssess'+
			'&schm='+me.typeId,//查询列表url(处理中)
        	cols:cols,
			typeId: me.typeId,	//菜单配置-分库标识
		    border: false,
		    checked : true,
		    pagable : true
        });
       
        me.callParent(arguments);
    }

});