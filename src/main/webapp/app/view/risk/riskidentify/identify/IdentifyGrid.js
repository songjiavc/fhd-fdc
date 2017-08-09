Ext.define('FHD.view.risk.riskidentify.identify.IdentifyGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.identifyGrid',
    storeAutoLoad : false,
    requires : [],
    
    reloadData: function(executionId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/riskidentify/findidentifygrid.f';//查询列表
	    me.store.proxy.extraParams.executionId = executionId;
	    me.store.load();
    },
    
    edit : function(riskId, rangObjectDeptEmpId,objectId){
    	var me = this;
    	me.quaAssessEdit = Ext.create('FHD.view.risk.assess.quaAssess.QuaAssessEdit',{isEditIdea : true,objectId:objectId});
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:600,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
    			{
    				text: '修改',
    				handler:function(){
    					
//    					alert("editIdea:="+me.quaAssessEdit.editIdea.getValue());
//    					alert("rangObjectDeptEmpId:="+rangObjectDeptEmpId)
    					FHD.ajax({
    			            url: __ctxPath + '/assess/quaassess/saveEditIdea.f',
    			            params: {
    			            	editIdeaContent : me.quaAssessEdit.editIdea.getValue(),
    			            	responseText : me.quaAssessEdit.responseText.getValue(),
    			            	objectDeptEmpId : rangObjectDeptEmpId,
    			            	objectId : objectId
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
		me.quaAssessEdit.load(riskId, rangObjectDeptEmpId);
		me.quaAssessEdit.on('resize',function(p){
    		me.quaAssessEdit.detailAllForm.setHeight(me.formwindow.getHeight()-62);
	   	});
    },
    //删除新增风险方法
    delGrid:function(riskId){
  		var me = this;
    	var selection = me.getSelectionModel().getSelection();
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					FHD.ajax({
						url : __ctxPath + '/assess/quaassess/deletetaskbysome.f',
						params: {
			              	assessPlanId : me.businessId,
			              	riskId : riskId
						},
						callback : function(data){
							if(data){
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.store.load();
							}
						}
					});
				}
			} 
		});
  	},
  	
  	editGrid : function(riskId){//修改新增风险方法
  		var me = this;
  		if(!riskId){
  			Ext.MessageBox.alert('修改信息','请选择一条风险事件进行修改!');
  			return;
  		}
  		//风险信息组件
		me.editForms = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
    		type:'re',	//如果是re,上级风险只能选择叶子节点
    		border:false,
    		setLoginDept: true,//只读属性 
    		callback:function(){
    			me.editFormWin.body.unmask();
    			me.editFormWin.hide();
    			me.store.load();
    		}
    	});
	    me.editForms.reloadData(riskId);//加载风险信息
		me.editFormWin = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:463,
			title : '风险信息修改',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.editForms],
			buttons: [
						{
							text: '保存',
							handler:function(){
								var isAdd = me.editForms.save(me.editForms.callback);
								if(!isAdd){
									me.editFormWin.body.mask("保存中...","x-mask-loading");
								}
							}
						},
		    			{
		    				text: '关闭',
		    				handler:function(){
		    					me.editFormWin.close();
		    				}
		    			}
		    		]
		});
		me.editFormWin.show();
	},
	
    getColsInit : function(){
    	var me = this;
    	
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    				{
    					dataIndex:'templateId',
    					hidden:true
    				},
    				{
    					dataIndex : 'rangObjectDeptEmpId',
    					hidden:true
    				},
    				{
    					dataIndex:'objectId',
    					hidden:true
    				},
    		        {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.3
    		        },
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            flex:1,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
    		            			+ record.get('rangObjectDeptEmpId') +  "','" 
    		            			+ record.get('objectId') +"')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        },{
    		        /**
    		         * add by songjia
    		         * desc    添加应对内容列展示
    		         */
    		        	header : '应对措施',
    		        	dataIndex : 'responseContent',
    		        	sortable : false,
    		        	flex : 2
    		        },{
    		            header: "修改意见",
    		            dataIndex: 'editIdeaContent',
    		            sortable: false,
    		            flex:2
    		        },
    		        {
    		            header: "操作",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	if(record.get('riskTitle').indexOf('new') != -1){
    		            		return "<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
		            			+ record.get('rangObjectDeptEmpId') +"','" 
		            			+ record.get('objectId') + "')\">意见反馈</a>" + 
    		            		
    		            		 "　<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').editGrid('" + record.get('riskId') + "')\">修改</a>"+ 
    		            		
    		            		 "　<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').delGrid('" + record.get('riskId') + "')\">删除</a>";
    		            	}else{
    		            		return "<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
		            			+ record.get('rangObjectDeptEmpId') +"','" 
		            			+ record.get('objectId') + "')\">意见反馈</a>";
    		            	}
    	     			}
    		        },
    		        {
    		            header: "状态",
    		            dataIndex: 'riskTitle',
    		            sortable: true,
    		            flex:.3,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' " +
    		            			"class='" + record.get('riskTitle') + "'/>";
    	     			}
    		        }
    	        ];
    	
    	return cols;
    },
    
    getSubmit : function(){
    	var me = this;
    	
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = null;
        cols = me.getColsInit();
        Ext.apply(me,{
        	cols:cols,
		    border: false,
		    checked: false,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true
        });
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3,4,5]);
        });
    }
});