Ext.define('FHD.view.risk.assess.formulatePlan.AssessPlanFollowGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.assessPlanFollowGrid',
 	/*requires: [
 			'FHD.view.risk.assess.quaAssess.QuaAssessShowGrid'
	],*/
	
	reloadData :function(){//刷新grid
		var me = this;
	},
	//发送提醒邮件
	sendEmailToEmp: function(){
		var me = this;
		var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认发送邮件吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//跳转到角色设置页面
    				FHD.ajax({//ajax调用
					url : __ctxPath + '/access/formulateplan/sendassessplanemailtoemp.f',
					params : {
						businessId: me.id,
						empId: selection.get('empId'),
						executionId: selection.get('executionId')
					},
					callback : function(data){
						if(data.success){
							FHD.notification('操作成功',FHD.locale.get('fhd.common.prompt'));
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
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath +"/access/formulateplan/exportquestmoniall.f?businessId="+me.id
    								+"&exportFileName="+""+"&sheetName="+sheetName;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{header: "empId", dataIndex:'empId', invisible:true},
			{header: "planId", dataIndex:'planId', hidden:true},
			{header: "orgId", dataIndex:'orgId', invisible:true},
	        {header: "部门", dataIndex: 'deptName', sortable: true, width:40, flex:1},
	        {header: "人员", dataIndex: 'empName', sortable: true, width:40, flex:1},
	        {header: "风险数量", dataIndex: 'scoreObjCount', sortable: true, width:40, flex:1},
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
	    	{header:'操作',dataIndex:'operate',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
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
			             	if(record.data.executionId){//未开始的人员可以发提醒邮件,否则按钮置灰
			             		this.items[0].disabled = false;
			             	}else{
			             		this.items[0].disabled = true;
			             	}
			            }
			}
        ];
       
        Ext.apply(me,{
        	//storeAutoLoad: false,//不自动加载数据
        	cols:cols,
        	url: __ctxPath + '/access/formulateplan/findplanfollowbybusinessid.f?businessId='+me.id,
        	title: '评估进度跟踪',
        	margin: '1 1 1 1',
		    border: true,
		    checked : false,
		    searchable: true,
		    pagable : false,
		    tbarItems:[
		    	{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}
		    ]
        });
       
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [2]);
        });
    }
});