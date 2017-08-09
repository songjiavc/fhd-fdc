Ext.define('FHD.view.check.quarterlycheck.plan.QuarterlyCheckGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.quarterlyCheckGrid',
 	requires: [
	],
	
    edit : function(isAdd){
    	var me = this;
    	var cardPanel = me.up('quarterlyCheckCard');
    	var planEditOne = cardPanel.quarterlyCheckFormMain.yearCheckPlanFormOne;
    	if(isAdd){//添加按钮
    		planEditOne.reponser.clearValues();//清空组件缓存
    		planEditOne.contactor.clearValues();
    		planEditOne.resetData();
    		planEditOne.getForm().reset();
    		
    	}else{//修改
    		var selection = me.getSelectionModel().getSelection();
    		planEditOne.idField.setValue(selection[0].get('id'));//给id隐藏域赋值
    	   	planEditOne.loadData(selection[0].get('id'));
    	}
    	cardPanel.showQuarterlyCheckFormMain();
    },
    //预览窗口
    previewWindow:function(pid){
    	var me = this;
    
        me.planConformPreviewWindow = Ext.create('FHD.view.check.yearcheck.plan.CheckPlanFormPreviewWindow',{
        		businessId: pid
        	});

    	me.planConformPreviewWindow.loadData(pid);
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.planConformPreviewWindow]
		}).show();
	},
    //删除方法
    del : function(me){
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				var ids = [];
    				for(var i=0;i<selection.length;i++){
    					ids.push(selection[i].get('id'));
    				}
    				FHD.ajax({//ajax调用
    					url : __ctxPath + '/check/quarterlycheck/deleteQuarterlyPlan.d',
    					params : {
    						id:ids.join(',')
    					},
    					callback : function(data){
    						if(data.data){//删除成功！
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    //设置按钮可用状态
    setstatus : function(me){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(selection.length){
    		if(selection[0].data.dealStatus=='N'){//未提交
    			if (me.down("[name='planConformEditId']")) {
		    		if(me.getSelectionModel().getSelection().length === 1){
		    			me.down("[name='planConformEditId']").setDisabled(false);
		    		}else{
		    			me.down("[name='planConformEditId']").setDisabled(true);
		    		}
		    		if (me.down("[name='planConformDeleteId']")) {
			            me.down("[name='planConformDeleteId']").setDisabled(me.getSelectionModel().getSelection().length === 0);
			        }
		        }
    		}else{
    			me.down("[name='planConformEditId']").setDisabled(true);
    			me.down("[name='planConformDeleteId']").setDisabled(true);
    		}
    	}else{
    		me.down("[name='planConformEditId']").setDisabled(true);
    		me.down("[name='planConformDeleteId']").setDisabled(true);
    	}
    },
    
    // 初始化方法
    initComponent: function() {
    	//alert("i am grid")
        var me = this;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "计划名称",
	            dataIndex: 'name',
	            sortable: true,
	            width:40,
	            flex:2,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').previewWindow('"+record.data.id+ "')\">"+value+"</a>";
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
	            dataIndex: 'beginDateStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "结束时间",
	            dataIndex: 'endDateStr',
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
	        }
        ];
        Ext.apply(me,{
        	region:'center',
        	url : __ctxPath + "/check/quarterly/findAllQuarterlyCheckPlan.f",//查询列表url
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				me.edit(true);
        			}
    			},'-',{
        			btype:'edit',
        			disabled:true,
        			name : 'planConformEditId',
        			handler:function(){
        				me.edit(false);
        			}
    			},'-',{
        			btype:'delete',
        			disabled:true,
        			name : 'planConformDeleteId',
        			handler:function(){
        				me.del(me);
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : true
        });
       
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);

    }

});