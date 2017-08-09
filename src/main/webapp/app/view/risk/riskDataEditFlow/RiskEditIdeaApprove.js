/**
 * 
 * 领导审批
 */

Ext.define('FHD.view.risk.riskDataEditFlow.RiskEditIdeaApprove', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskEditIdeaApprove',
    
    requires : ['FHD.view.risk.riskDataEditFlow.RiskEditIdea',
                'FHD.view.risk.assess.utils.GridCells'],
    
    empRelaRiskIdeaIdDatas : null,            
                
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
    					dataIndex:'empRelaRiskIdeaId',
    					hidden:true
    				},{
    					dataIndex:'empId',
    					hidden:true
    				},
    				{
    					dataIndex:'riskIdeaId',
    					hidden:true
    				},
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:1.5,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'"';
							var value = {};           	
							value['empRelaRiskIdeaId'] = record.get('empRelaRiskIdeaId');
							me.empRelaRiskIdeaIdDatas.push(value);
    		            	
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').show('" + record.get('riskId') + "')\">" + record.get('riskName') + "</a>";
    	     			}
    		        },{
    					header: "所属部门",
    					dataIndex : 'orgName',
    					sortable: true,
    					flex:.5
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
    					flex:2.5,
    					renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+200+'"';
    		            	return record.get('content');
    	     			}
    				}
    	        ];
    	
    	return cols;
    },
    
    del : function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
    	var empRelaRiskIdeaId = selection.get('empRelaRiskIdeaId');
    	var riskIdeaId = selection.get('riskIdeaId');
    	
    	FHD.ajax({
            url: __ctxPath + '/delIdea.f',
            params: {
            	empRelaRiskIdeaId : empRelaRiskIdeaId,
            	riskIdeaId : riskIdeaId
            },
            callback: function (data) {
            	if(data.success == true){
            		me.store.load();
            		FHD.notification('删除成功','操作');
	            }
            }
        });
    },
    
    edit : function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
    	var riskId = selection.get('riskId');
    	
    	me.addShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
    		//title:'风险事件',
    		type:'re',	//如果是re,上级风险只能选择叶子节点
    		border:false,
    		callback:function(){
    			me.formwindow.body.unmask();
    			//alert('风险事件添加成功');
    			me.store.load();
    			me.formwindow.close();
    		}
    	});
    	me.addShortForm.reloadData(riskId);
    	
    	me.addShortForm.add(me.assessGridSet);
    	me.addShortForm.relafieldSet.collapse();
    	
    	me.addShortForm.on('resize',function(p){
        	me.addShortForm.setHeight(me.formwindow.getHeight() - 62);
	   	});
    	
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
 			items : [me.addShortForm],
 			buttons: [
 				{
 				    text:'保存',
 				    handler:function(){
 				 	   me.formwindow.body.mask("保存中...","x-mask-loading");
 				 	   me.addShortForm.save(me.addShortForm.callback);
 				}
 				
 				},
     			{
     				text: '关闭',
     				handler:function(){
     					me.formwindow.close();
     				}
     		}]
 		});
    	
		me.formwindow.show();
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.empRelaRiskIdeaIdDatas = [];
        if(me.isDel){
        	Ext.apply(me,{
            	region:'center',
            	margin : '0 0 0 0',
            	url : __ctxPath + '/findRiskEmpIdeaList.f?orgId=' + me.approveMan.businessId,
            	cols:me.getColsShow(),
    		    border: false,
    		    scroll: 'vertical',//只显示垂直滚动条
    		    checked: true,
    		    pagable : false,
    		    searchable : true,
    		    columnLines: true,
    		    isNotAutoload : false,
    		    tbarItems:[{iconCls: 'icon-del', id : 'riskedit_del', text:'删除', handler:function(){me.del();}}]
            });
        }else{
        	Ext.apply(me,{
            	region:'center',
            	margin : '0 0 0 0',
            	url : __ctxPath + '/findRiskEmpIdeaList.f?orgId=' + me.dataMain.businessId,
            	cols:me.getColsShow(),
    		    border: false,
    		    scroll: 'vertical',//只显示垂直滚动条
    		    checked: true,
    		    pagable : false,
    		    searchable : true,
    		    columnLines: true,
    		    isNotAutoload : false,
    		    tbarItems:[{iconCls: 'icon-edit', id : 'riskedit_edit', text:'修改', handler:function(){me.edit();}}]
            });
        }
        
        me.callParent(arguments);
        
        if(me.isDel){
        	me.down('#riskedit_del').setDisabled(me.getSelectionModel().getSelection().length === 0);
        }else{
        	me.down('#riskedit_edit').setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [6,7]);
        });
        
        if(me.isDel){
	        me.on('selectionchange',function(){
	        	me.down('#riskedit_del').setDisabled(me.getSelectionModel().getSelection().length === 0);
	        });
        }else{
        	 me.on('selectionchange',function(){
 	        	me.down('#riskedit_edit').setDisabled(me.getSelectionModel().getSelection().length === 0);
 	        });
        }
    }
});