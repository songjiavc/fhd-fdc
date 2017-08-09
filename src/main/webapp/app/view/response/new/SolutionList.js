/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.new.SolutionList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.solutionlist',
    url : '',
    columnLines: true,
    pagable:true,
    searchable : true,
    type : 'risk',//risk关联风险，dept部门应对，all应对库
    businessType: 'analysis',//solution应对措施，measure控制措施，analysis风险分析
    archiveStatus : 'saved',
    showbar : true,
    layout: 'fit',
    showType : 'form',//显示类型，是否用window弹出
    flex : 12,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.cols = [
 			{header : '措施id',dataIndex : 'measureId',sortable : false, invisible : true},
 			{header : '措施名称',dataIndex : 'measureName',sortable : false, flex : 2,height: '30px',
 			 renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                var id = record.get('measureId');
	                var btnUrl = "";
	                if(record.get('type') == 'measure'){
	                	btnUrl = "<div style='height:38px;white-space:normal;overflow:hidden;line-height:150%' >" + "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMeasure('" + id + "','" + record.get('riskId') + "')\" >" + value + "</a>" + "</div>";
	                }else if(record.get('type') == 'solution'){
	                	btnUrl = "<div style='height:38px;white-space:normal;overflow:hidden;line-height:150%'  >" + "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showSolution('" + id + "','" + record.get('riskId') + "')\" >" + value + "</a>" + "</div>";
	                }
	                return btnUrl;
	            }
 			},
 			{header : '风险分类Id',dataIndex : 'riskParentId',sortable : false, invisible : true},
        	{header : '风险Id',dataIndex : 'riskId',sortable : false, invisible : true},
    		{
    			header : '风险名称',dataIndex : 'riskName',sortable : false, flex : 1.5,height : '19px',
    	     	renderer : function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="'+value+'"';
	                return "<div style='height:38px;white-space:normal;overflow:hidden;line-height:150%'>" + value + "</div>";
	            }
    		},
 			{header : '',dataIndex : 'orgId',sortable : false, invisible : true},
 			{header : '主责部门',dataIndex : 'orgName',sortable : false, flex : .5,height: '30px'
 			},
 			{header : '',dataIndex : 'empId',sortable : false, invisible : true},
 			{header : '责任人',dataIndex : 'empName',sortable : false, flex : .4},
 			{header : '措施类型',dataIndex : 'type',sortable : false, flex : .4,
 				renderer:function(value,metaData,record,colIndex,store,view) { 
			    		if(value == 'measure'){
			    			return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center ;' data-qtitle='' "
								+ ">控制措施</div>";
			    		}else if(value == 'solution'){
			    			return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "' data-qtitle='' "
								+ ">应对措施</div>";
			    		}else{
			    			return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center top;' data-qtitle='' "
								+ "></div>";
			    		}
				}
			},
			{header : '执行状态',dataIndex : 'status',sortable : false, flex:.4,
			 renderer:function(value,metaData,record,colIndex,store,view) { 
			    	if(value == "N"){
    					  return '<span style="color:red;">未执行</span>';
    				}else if(value == "H"){
    					return '<span style="color:green;">执行中</span>';
    				}else if(value == "F"){
    					  return "已执行";
    				}else{
			    		return null;
			    	}
				}
			}, {
            header: "状态",
            dataIndex: 'archiveStatus',
            sortable: false,
            width : 55,
            renderer: function (v, metaData, record, colIndex,
                store, view) {
                var value='';
            	var show='';
                if (v == 'saved') {
                	value = "<font color=red>"+"待提交"+"</font>";
                	show = '待提交';
                } else if (v == 'examine') {
                	value = "<font color=green>"+"审批中"+"</font>";
                	show = '审批中';
                } else if (v == 'archived') {
                	value = "已归档";
                	show = '已归档';
                } else {
                	value = "" ;
                }
                metaData.tdAttr = 'data-qtip="' + show + '"';
                return '<div style="width: 32px; height: 19px;" >'+value+'</div>';
            }
        }];
		if(me.type != 'dept' && me.type != 'all'){
			for(var i=0;i<me.cols.length;i++){
				if(me.cols[i].dataIndex == 'riskName'){
					me.cols[i].invisible = true;
				}
			}
		}
		if(me.businessType != 'analysis' && me.type != 'all'){
			for(var i=0;i<me.cols.length;i++){
				if(me.cols[i].dataIndex == 'type'){
					me.cols[i].invisible = true;
				}
			}
		}
		if(me.businessType == 'measure'){
			for(var i=0;i<me.cols.length;i++){
				if(me.cols[i].dataIndex == 'status'){
					me.cols[i].invisible = true;
				}
			}
		}
		if(me.businessType == 'solution'){
			me.cols.push({
	            header: FHD.locale.get('fhd.common.operate'),
            	exportText : '操作',
	            dataIndex: 'operate',
	            sortable: false,
	            align:'center',
	            width: 65,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	var id = record.data['measureId'];
	            	var empId = record.data['empId'];
	            	var measureName = record.data['measureName'];
	            	var status = record.data['status'];
	                if(status == 'N' && record.data['archiveStatus'] == 'archived'){
		                var name = record.data['riskname'];
		                if (name != null && name != undefined && name.length > 23) {
		                    name = name.substring(0, 20) + "...";
		                }
		                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').executeSolution('" + id + "','" + empId + "','" + measureName + "')\" class='icon-view' data-qtip='执行'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;执行</a>";
	                }else{
	                	return '';
	                }
	            }
	        });
		}
		
		me.tbarItems = [];
		if(me.showbar){
	        if(me.businessType == 'solution'){
	        	me.tbarItems.push({iconCls : 'icon-add',text: '添加',
	        		authority:'ROLE_ALL_RESPONS_ADDRESPONS',
	        		tooltip: '添加',handler :me.addSolution,scope : this}
	        	);
	        }else if(me.businessType == 'measure'){
	        	me.tbarItems.push({iconCls : 'icon-add',text: '添加',
	        		authority:'ROLE_ALL_RESPONS_ADDCONTROL',
	        		tooltip: '添加',handler :me.addMeasure,scope : this}
	        	);
	        }else if(me.businessType == 'analysis'){
	        	me.tbarItems.push({iconCls : 'icon-add',text: '添加应对措施',
	        		authority:'ROLE_ALL_RESPONS_ADDRESPONS',
	        		tooltip: '添加应对措施',handler :me.addSolution,scope : this}
	        	);
	        	me.tbarItems.push({iconCls : 'icon-add',text: '添加控制措施',
	        		authority:'ROLE_ALL_RESPONS_ADDCONTROL',
	        		tooltip: '添加控制措施',handler :me.addMeasure,scope : this}
	        	);
	        }
	        me.tbarItems.push({iconCls : 'icon-edit',text: '修改',
					authority:'ROLE_ALL_RESPONS_EDIT',
					tooltip: '修改',handler :me.editMeasure,disabled: true,scope : this},
				{iconCls : 'icon-del',text: '删除',
					authority:'ROLE_ALL_RESPONS_DELETE',
					tooltip: '删除',handler :me.delMeasure,disabled: true,scope : this});
		}
        
        me.on('selectionchange',me.onchange);//选择记录发生改变时改变按钮可用状态
        me.callParent(arguments);
        me.onchange();
    },
    colInsert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },
    addSolution:function(){
    	var me=this;
    	if(me.showType == 'form'){
		    if(!me.solutionform){
			    //评价计划第一步container
		        me.solutionform = Ext.create('FHD.view.response.new.SolutionForm',{
		        	archiveStatus : me.archiveStatus,
		        	type : me.type,
		        	bodyPadding: "0 3 3 3",
					callback : function(){
						me.callback();
					}
	        	});
		    }
		    me.solutionform.initParam({
		    	'type' : me.paramObj.type,
	    		'riskId' : me.paramObj.selectId,
	    		'solutionId' : ''
	    	});
		    me.solutionform.clearFormData();
		    me.addFormContainer(me.solutionform,'添加应对措施');
	    }else{
	    	var window = Ext.create('FHD.ux.Window', {
	            title: '应对信息',
	            maximizable: true,
	            modal: true,
	            collapsible: true,
	            autoScroll: true
	        }).show();
	    	var solutionform = Ext.create('FHD.view.response.new.SolutionForm',{
	        	archiveStatus : me.archiveStatus,
	        	type : me.type,
	        	bodyPadding: "0 3 3 3",
				callback : function(){
					window.close();
					me.reloadData();
				}
        	});
	        solutionform.initParam({
		    	'type' : me.paramObj.type,
	    		'riskId' : me.paramObj.selectId,
	    		'solutionId' : ''
	    	});
		    solutionform.clearFormData();
		    window.add(solutionform);
	    }
    },
    //面板切换方法
    addFormContainer:function(p,name){},
    //新增方案
    addMeasure:function(){
    	var me=this;
    	if(me.showType == 'form'){
	    	if(!me.measureeditformitem){
	    		me.measureeditformitem = Ext.create('FHD.view.response.new.MeaSureEditFormItem',{
	    			archiveStatus : me.archiveStatus,
		        	type : me.type,
		        	bodyPadding: "0 3 3 3",
					callback : function(){
						me.callback();
					}
		        });
	    	}
	    	me.measureeditformitem.initParam({
	    		'type' : me.paramObj.type,
	    		'riskId' : me.paramObj.selectId,
	    		'measureId' : ''
	    	});
	    	me.measureeditformitem.clearFormData();
	    	me.addFormContainer(me.measureeditformitem,'添加控制措施');
    	}else{
    		var window = Ext.create('FHD.ux.Window', {
	            title: '应对信息',
	            maximizable: true,
	            modal: true,
	            collapsible: true,
	            autoScroll: true
	        }).show();
    		var measureeditformitem = Ext.create('FHD.view.response.new.MeaSureEditFormItem',{
    			archiveStatus : me.archiveStatus,
	        	type : me.type,
	        	bodyPadding: "0 3 3 3",
				callback : function(){
					window.close();
					me.reloadData();
				}
	        });
	        measureeditformitem.initParam({
	    		'type' : me.paramObj.type,
	    		'riskId' : me.paramObj.selectId,
	    		'measureId' : ''
	    	});
	    	measureeditformitem.clearFormData();
    		window.add(measureeditformitem);
    	}
    },
    //修改措施
    editMeasure:function(){
    	var me=this;
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(selection[0].get('type') == 'measure'){
    		if(me.showType == 'form'){
	    		if(!me.measureeditformitem){
		    		me.measureeditformitem = Ext.create('FHD.view.response.new.MeaSureEditFormItem',{
			        	type : me.type,
			        	bodyPadding: "0 3 3 3",
						callback : function(){
							me.callback();
						}
			        });
		    	}
		    	me.measureeditformitem.initParam({
		    		'riskId' : selection[0].get('riskId'),
		    		'measureId' : selection[0].get('measureId')
		    	});
		    	me.measureeditformitem.clearFormData();
		    	me.measureeditformitem.reloadData();
		    	me.addFormContainer(me.measureeditformitem,'修改控制措施');
	    	}else{
	    		var window = Ext.create('FHD.ux.Window', {
		            title: '应对信息',
		            maximizable: true,
		            modal: true,
		            collapsible: true,
		            autoScroll: true
		        }).show();
	    		var measureeditformitem = Ext.create('FHD.view.response.new.MeaSureEditFormItem',{
		        	type : me.type,
		        	bodyPadding: "0 3 3 3",
					callback : function(){
						window.close();
						me.reloadData();
					}
		        });
			    measureeditformitem.initParam({
		    		'riskId' : selection[0].get('riskId'),
		    		'measureId' : selection[0].get('measureId')
		    	});
		    	measureeditformitem.clearFormData();
		    	measureeditformitem.reloadData();
		    	window.add(measureeditformitem);
	    		
	    	}
    	}else if(selection[0].get('type') == 'solution'){
    		if(me.showType == 'form'){
	    		if(!me.solutionform){
				    //评价计划第一步container
			        me.solutionform = Ext.create('FHD.view.response.new.SolutionForm',{
			        	type : me.type,
			        	bodyPadding: "0 3 3 3",
						callback : function(){
							me.callback();
						}
		        	});
			    }
		    	me.solutionform.initParam({
		    		'riskId' : selection[0].get('riskId'),
		    		'solutionId' : selection[0].get('measureId')
		    	});
		    	me.solutionform.clearFormData();
		    	me.solutionform.reloadData();
			    me.addFormContainer(me.solutionform,'添加应对措施');
		    }else{
		    	var window = Ext.create('FHD.ux.Window', {
		            title: '应对信息',
		            maximizable: true,
		            modal: true,
		            collapsible: true,
		            autoScroll: true
		        }).show();
		    	var solutionform = Ext.create('FHD.view.response.new.SolutionForm',{
		        	type : me.type,
		        	bodyPadding: "0 3 3 3",
					callback : function(){
						window.close();
						me.reloadData();
					}
	        	});
		    	solutionform.initParam({
		    		'riskId' : selection[0].get('riskId'),
		    		'solutionId' : selection[0].get('measureId')
		    	});
		    	solutionform.clearFormData();
		    	solutionform.reloadData();
			    window.add(solutionform);
		    }
    	}
    },
    //修改措施
    delMeasure:function(){
    	var me=this;
    	var delUrl = '';
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(0 == selection.length){
    		FHD.notification(FHD.locale.get('fhd.common.msgDel'),FHD.locale.get('fhd.common.prompt'));
    	}else{
			if(selection[0].get('type') == 'measure'){
	    		delUrl = '/response/removeresponsemeasurelistbyids.f';
    		}else if(selection[0].get('type') == 'solution'){
	    		delUrl = '/response/removeresponselistbyids.f';
    		}
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if(btn == 'yes') {
	 					var ids = [];
						for(var i = 0; i < selection.length; i++) {
							var isSubmit=selection[i].get('status');
					    	if(isSubmit=='P'){
					    		FHD.notification('对不起,您不能删除已提交的数据!',FHD.locale.get('fhd.common.prompt'));
					    		return false;
					    	}else{
					    		ids.push(selection[i].get('measureId'));
					    	}
	 					}
	 					FHD.ajax({
	 						url : __ctxPath+ delUrl,
	 						params : {
	 							/*measureIds*/
	 							ids : ids
	 						},
	 						callback : function(data) {
	 							if (data.success) { 
		 							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		 							me.store.load();
	                            } else {
	                            	FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
	                            }
	   						}
						});
					}
				}
			});
    	}
    },
	reloadData:function(){
		var me=this;
		// 通用列表当不是工作计划的工作流的时候用这个 
		me.store.proxy.url = __ctxPath + '/response/findresponselistbypage.f';
        var status;
        if(me.paramObj.status){
        	status = me.paramObj.status;
        }else{
        	status = "N";
        }
        me.store.proxy.extraParams = {
        	//公用type  为了使多个模块通用一个查询
        	type : me.type,
        	//status  状态为 未执行：执行中：已执行
        	status : status,
        	selectId : me.paramObj.selectId,
        	businessType : me.businessType
        };
		me.store.load();
	},
	onchange :function(){//设置你按钮可用状态
		var me = this;   // iconCls : 'icon-del',
		var selection = me.getSelectionModel().getSelection();
		if(selection.length == 1 && selection[0].get('measureId')!= null){
			if((selection[0].get('type') == 'solution' && selection[0].get('status') == 'H') || selection[0].get('archiveStatus') == 'examine'){
				if(me.down('[iconCls=icon-edit]')){
					me.down('[iconCls=icon-edit]').setDisabled(true);
				}
				if(me.down('[iconCls=icon-del]')){
					me.down('[iconCls=icon-del]').setDisabled(true);
				}
			}else{
				if(me.down('[iconCls=icon-edit]')){
					me.down('[iconCls=icon-edit]').setDisabled(false);
				}
				if(me.down('[iconCls=icon-del]')){
					me.down('[iconCls=icon-del]').setDisabled(false);
				}
			}
		}else{
			if(me.down('[iconCls=icon-edit]')){
				me.down('[iconCls=icon-edit]').setDisabled(true);
			}
			if(me.down('[iconCls=icon-del]')){
				me.down('[iconCls=icon-del]').setDisabled(true);
			}
		}
	},
	showMeasure : function(id,riskId){
		var me=this;
		var measureeditformitemforview = Ext.create('FHD.view.response.new.MeaSureEditFormItemForView');
		measureeditformitemforview.riskFieldSet.expand();
    	measureeditformitemforview.initParam({
    		'measureId' : id,
    		'riskId' : riskId
    	});
	    measureeditformitemforview.reloadData();
	    var win = Ext.create('FHD.ux.Window',{
				title:'控制措施详细信息',
				collapsible:false,
				maximizable:true//（是否增加最大化，默认没有）
	    }).show();
		win.add(measureeditformitemforview);
	},
	showSolution : function(id,riskId){
		var me=this;
		var solutionformforview = Ext.create('FHD.view.response.new.SolutionFormForView');
		solutionformforview.basicInfoFieldset.expand();
		//solutionformforview.riskFieldSet.expand();
		solutionformforview.riskdetailform.expand();
    	solutionformforview.initParam({
    		'solutionId' : id,
    		'riskId' : riskId,
    		'type' : me.type
    	});
	    solutionformforview.reloadData();
	    var win = Ext.create('FHD.ux.Window',{
				title:'应对措施详细信息',
				collapsible:false,
				maximizable:true//（是否增加最大化，默认没有）
	    }).show();
		win.add(solutionformforview);
	},
	startProcess : function(){
		var me = this;
		FHD.ajax({
			url : __ctxPath+ '/response/workflow/startprocess.f',
		 	callback : function(data) {
				if(me.winId){
					Ext.getCmp(me.winId).close();
				}
			 }
		});
	},
	executeSolution: function(id,empId,name){
		var me = this;
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : '您确定要执行吗?',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if(btn == 'yes') {
					me.savingMask = new Ext.LoadMask(Ext.getBody(), {
						msg:"处理中..."
					});
					me.savingMask.show();
 					FHD.ajax({
						url : __ctxPath+ '/responseplan/workflow/startexcuteresponse.f',
						params : {
							businessId : id,
							empId : empId,
							name : name
						},
						callback : function(data) {
							me.savingMask.hide();
							me.reloadData();
						}
					});
				}
			}
		});
	},
	callback : function(){}
});