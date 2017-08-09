/**
 * 
 * 领导审批
 */

Ext.define('FHD.view.risk.riskDataEditFlow.RiskEdit', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskEdit',
    
    requires : [ 'FHD.view.risk.assess.utils.GridCells',
                 'FHD.view.risk.riskDataEditFlow.RiskEditIdea'],
    
    riskDatas : null,
    
    show : function(riskId){
    	var me = this;
    	me.quaAssessEdit = Ext.widget('riskEditIdea',{isEdit : false});//riskEditIdeaApprove,riskEditIdea
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
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    	    ]
		});
		me.formwindow.show();
		me.quaAssessEdit.load(riskId);
    },
    
    getColsShow : function(){
    	var me = this;
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},{
    					dataIndex:'bId',
    					hidden:true
    				},{
    					dataIndex:'empId',
    					hidden:true
    				},
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').show('" + record.get('riskId') + "')\">" + record.get('riskName') + "</a>";
    	     			}
    		        },{
    					header: "所属部门",
    					dataIndex : 'orgName',
    					sortable: true,
    					flex:.4
    				},
    				{
    					header: "人员",
    					dataIndex : 'empName',
    					sortable: true,
    					flex:.3
    				},
    				{
    					header: "意见内容",
    					dataIndex : 'content',
    					sortable: true,
    					flex:.3,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'"';
    		            	return record.get('content');
    	     			}
    				}
    	        ];
    	
    	return cols;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        Ext.apply(me,{
        	region:'center',
        	margin : '0 0 0 0',
        	url : __ctxPath + '/findRiskEmpIdeaList.f',
        	cols:me.getColsShow(),
		    border: false,
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
        	Ext.widget('gridCells').mergeCells(me, [4]);
        });
        
//        me.on('resize',function(p){
//        	me.setHeight(Ext.getCmp(me.assessApproveSubmit.winId).getHeight() - 105);
//    	});
    }

});