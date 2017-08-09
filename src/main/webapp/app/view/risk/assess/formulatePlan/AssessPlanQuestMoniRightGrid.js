/**
 * 
 * 问卷监控右侧面板
 */

Ext.define('FHD.view.risk.assess.formulatePlan.AssessPlanQuestMoniRightGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.assessPlanQuestMoniRightGrid',
 	requires: [
 			'FHD.view.risk.assess.utils.GridCells'
	],
	
	reloadData :function(planId,deptId){//刷新grid
		var me = this;
		me.deptId = deptId;
		//查询部门明细 
		me.store.proxy.url =  __ctxPath + '/access/formulateplan/querydeptdetailedbydeptId.f';
		me.store.proxy.extraParams.businessId = planId;
		me.store.proxy.extraParams.orgId = deptId;
		me.store.load();
	},
	//发送提醒邮件
	sendEmailToEmp: function(){
		var me = this;
		var assessPlanQuestMonitorMain = me.up('assessPlanQuestMonitorMain');
		var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
		Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : '您确定要发送邮件吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认发送
    				me.body.mask("发送中...","x-mask-loading");
    				FHD.ajax({//ajax调用
						url : __ctxPath + '/access/formulateplan/sendassessplanemailtoemp.f',
						params : {
							businessId: assessPlanQuestMonitorMain.id,
							empId: selection.get('id'),
							executionId: selection.get('executionId')
						},
						callback : function(data){
							me.body.unmask();
							if(data.success){
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
							}
							
						}
					});
    			}
    		}
    	});
	},
	
	//导出grid列表
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	var assessPlanQuestMonitorMain = me.up('assessPlanQuestMonitorMain');
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath +"/access/formulateplan/exportquestmoniemp.f?businessId="+assessPlanQuestMonitorMain.id
    								+"&exportFileName="+""+"&sheetName="+sheetName+"&orgId="+me.deptId;
    },
	
    colClick:function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
		var empId = selection[0].data.id;
		
		var assessPlanQuestMonitorMain = me.up('assessPlanQuestMonitorMain');
		Ext.Ajax.request({
			    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
			    params : {
							assessPlanId: assessPlanQuestMonitorMain.id
						},
			    async:  false,
			    success: function(response){
			        var text = response.responseText;
			        array = new Array();
			        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
			        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.3});
			        });
			        
			        me.quaAssessShowGrid = Ext.create('FHD.view.risk.assess.quaAssess.QuaAssessShowGrid',
			        		{url:__ctxPath + '/access/formulateplan/findassessshowgridbyempid.f', 
			        		array : array, businessId:assessPlanQuestMonitorMain.id, empId: empId});
			        //加载列表数据		
			        me.quaAssessShowGrid.store.proxy.extraParams.businessId = assessPlanQuestMonitorMain.id;
				    me.quaAssessShowGrid.store.proxy.extraParams.empId = empId;
				    me.quaAssessShowGrid.store.load();
					
					me.formwindow = new Ext.Window({
						layout:'fit',
						iconCls: 'icon-show',//标题前的图片
						modal:true,//是否模态窗口
						collapsible:true,
						width:800,
						height:500,
						maximizable:true,//（是否增加最大化，默认没有）
						constrain:true,
						items : [me.quaAssessShowGrid],
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
					me.formwindow.maximize();
			    }
			});
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{header: "id", dataIndex:'id', hidden:true},
	        {header: "姓名", dataIndex: 'empName', sortable: true, width:40, flex:1, 
	        	renderer:function(value,metaData,record,colIndex,store,view) { 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').colClick()\">"+value+"</a>";
     			}
     		},
	        {header: "评价风险", dataIndex: 'scoreObjCount', sortable: true, width:40, flex:1,
	        	 renderer:function(value,metaData,record,colIndex,store,view) { 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').colClick()\">"+value+"</a>";
     			}
	        },
	        {header: "角色", dataIndex: 'emprole', sortable: true, width:40, flex:1,
		        renderer:function(value,metaData,record,colIndex,store,view) { 
	     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"'; 
	     	    		return value;
	     			}},
	        {header: "邮箱", dataIndex: 'email', sortable: true, width:40, flex:1},
	        /*{header:'取消',dataIndex:'',hidden:false,editor:false,align:'center',
			       xtype:'actioncolumn',
			       items: [{
		                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
		                //tooltip: FHD.locale.get('fhd.common.del'),
		                handler: function(grid, rowIndex, colIndex) {
		                	alert('努力实现中......');
		                }
		            }]
			},
		    {header:'退回',dataIndex:'',hidden:false,editor:false,align:'center',
			       xtype:'actioncolumn',
			       items: [{
		                icon: __ctxPath+'/images/icons/arrow_turn_left.png',  
		               // tooltip: FHD.locale.get('fhd.common.del'),
		                handler: function(grid, rowIndex, colIndex) {
		                	alert('努力实现中......');
		                }
		            }]
			},*/
	        {header: "任务状态", dataIndex: 'taskStatus', sortable: true, width:40, flex:1,
		        renderer:function(dataIndex) { 
	    				  if(dataIndex == "0"){
	    					  return '<span style="color:red;">未开始</span>';
	    				  }else if(dataIndex == "1"){
	    					  return '已处理';
	    				  }
	    			}},
	        {dataIndex: 'taskId',invisible:true},
	        {dataIndex: 'executionId',invisible:true},
	    	{header:'操作',dataIndex:'caozuo',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
				       xtype:'actioncolumn',
				       items: [{
			                icon: __ctxPath+'/images/icons/email_go.png',  // Use a URL in the icon config
			                tooltip: '发送email',
			                disabled: true,
			                handler: function(grid, rowIndex, colIndex) {
			                	grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			grid.getSelectionModel().select(rows,true);
			                    me.sendEmailToEmp();
			                }
			            }],
			            renderer:function(value,metaData,record,colIndex,store,view) {
			             	if(0 == record.data.taskStatus){//未开始的人员可以发提醒邮件,否则按钮置灰
			             		this.items[0].disabled = false;
			             	}else{
			             		this.items[0].disabled = true;
			             	}
			             	
			            }
			}
			
        ];
       
        Ext.apply(me,{
        	storeAutoLoad: false,//不自动加载数据
        	cols:cols,
        	title: '部门明细',
        	margin: '1 0 1 1',
		    border: true,
		    checked : false,
		    searchable: true,
		    pagable : false,
		    tbarItems:[
		    	{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}
		    ]
        });
       
        me.callParent(arguments);
        
       me.on('resize',function(p){
    		me.setHeight(FHD.getCenterPanelHeight());
    	});
    }

});