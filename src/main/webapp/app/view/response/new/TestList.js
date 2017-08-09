/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.new.TestList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.testlist',
    requires: [
    	'FHD.view.response.new.bpm.SolutionApproveFormForBpm',
    	'FHD.view.risk.assess.utils.GridCells',
    	'FHD.view.response.new.MeaSureEditFormItemForView',
    	'FHD.view.response.new.SolutionFormForView',
    	'FHD.view.response.new.SolutionHigherQueryForm'
    ],
    url : __ctxPath +'/app/view/response/responseplan/ResponsePlanData.json',
    collapsible : true,
    collapsed : false,
    columnLines: true,
    pagable:true,
    checked : false,
    tools:[ {
    			type: 'plus',
    			handler: function(event, toolEl, panel){
       				// refresh logic
    				//alert("333");
    			}
			}],
    searchable : false,
    layout: 'fit',
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    initComponent: function(){
    	var me = this;
    	me.solutionhigherqueryform = Ext.widget('solutionhigherqueryform');
    	me.win = Ext.create('FHD.ux.Window',{
				title:'高级查询主窗口',
				height : 300,
				collapsible:false,
				me : me,
				closeAction : 'hide',
				maximizable:true//（是否增加最大化，默认没有）
	    });
	    me.win.add(me.solutionhigherqueryform);
		//评价计划列表
        me.cols = [
        
 			{header : '措施id',dataIndex : 'measureId',sortable : false, hidden : true},
 			{header : '措施名称',dataIndex : 'measureName',sortable : false, flex : 2,height: '30px'
 			},
 			{header : '',dataIndex : 'orgId',sortable : false, hidden : true},
 			{header : '主责部门',dataIndex : 'orgName',sortable : false, flex : 1,height: '30px'
 			},
 			{header : '',dataIndex : 'empId',sortable : false, hidden : true},
 			{header : '责任人',dataIndex : 'empName',sortable : false, flex : 1},
 			{header : '措施类型',dataIndex : 'type',sortable : false, flex : 1,
 			renderer:function(value,metaData,record,colIndex,store,view) { 
		    			return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
							+ "background-position: center top;' data-qtitle='' "
							+ ">应对措施</div>";
			}},
			{header : '执行状态',dataIndex : 'status',sortable : false,
			 renderer:function(value,metaData,record,colIndex,store,view) { 
			    	
			    		return "已执行";
				}
			},{header: "操纵", flex : 2,
					dataIndex: 'createTime',
                    xtype: "actioncolumn",//Ext.grid.column.Action动作列

                    items: [{

                        icon: "images/icons/edit.gif",//指定编辑图标资料的URL 

                        handler: function(grid, rowIndex, colIndex) {

                           me.addPlan(grid, rowIndex, colIndex)

                        }

                    },{

                        icon: "images/icons/del.png",//指定编辑图标资料的URL 

                        handler: function(grid, rowIndex, colIndex) {

                            var rec = grid.getStore().getAt(rowIndex);

                            alert("删除 " + rec.get("name"));

                        }                
                    }]
                }
		];
        me.callParent(arguments);
        me.on('afterLayout',function(){ 
        	Ext.widget('gridCells').mergeCells(me, [4]);
        });
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
	    var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	//  更改导航
    	var containerAndTree = me.getTopContainerBySelf(); //导航container实例
    	if(containerAndTree){
	        var id ;
	        if(containerAndTree.type == 'sm'){
	        	id = containerAndTree.topContainer.treeNodeId;
	        }else{
	        	id = selection[0].get('riskParentId');
	        }
	        var name = selection[0].get('riskName');
	        if(name.length > 33){
		        name = name.substring(0,30)+"...";
		    }
			containerAndTree.topContainer.navigationBar.renderHtml(containerAndTree.containers.id + 'DIV', id, name , containerAndTree.type,containerAndTree.tree.id);
    	}
    	var solutioneditpanel = me.up('solutioneditpanel');
	    solutioneditpanel.solutionform.clearFormData();
	    solutioneditpanel.setActiveItem(solutioneditpanel.solutionform);
	    solutioneditpanel.solutionform.initParam({
    		'riskId' : selection[0].get('riskId'),
    		'solutionId' : ''
    	});
    	solutioneditpanel.solutionform.riskFieldSet.add(solutioneditpanel.solutionform.riskdetailform);
    	solutioneditpanel.solutionform.riskdetailform.reloadData(selection[0].get('riskId'));
    },
    //新增方案
    addMeasure:function(){
    	var me=this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	//  更改导航
    	var containerAndTree = me.getTopContainerBySelf(); //导航container实例
        var id ;
        if(containerAndTree){
	        if(containerAndTree.type == 'sm'){
	        	id = containerAndTree.topContainer.treeNodeId;
	        }else{
	        	id = selection[0].get('riskParentId');
	        }
	        var name = selection[0].get('riskName');
	        if(name.length > 33){
		        name = name.substring(0,30)+"...";
		    }
			containerAndTree.topContainer.navigationBar.renderHtml(containerAndTree.containers.id + 'DIV', id, name , containerAndTree.type,containerAndTree.tree.id);
        }
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	solutioneditpanel.measureeditformitem.clearFormData();
    	solutioneditpanel.setActiveItem(solutioneditpanel.measureeditformitem);
    	solutioneditpanel.measureeditformitem.initParam({
    		'riskId' : selection[0].get('riskId'),
    		'measureId' : ''
    	});
    	solutioneditpanel.measureeditformitem.riskFieldSet.add(solutioneditpanel.measureeditformitem.riskdetailform);
    	solutioneditpanel.measureeditformitem.riskdetailform.reloadData(selection[0].get('riskId'));
    },
    //修改措施
    editMeasure:function(){
    	var me=this;
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(selection[0].get('type') == 'M'){
	    	solutioneditpanel.setActiveItem(solutioneditpanel.measureeditformitem);
	    	solutioneditpanel.measureeditformitem.initParam({
	    		'riskId' : selection[0].get('riskId'),
	    		'measureId' : selection[0].get('measureId')
	    	});
	    	solutioneditpanel.measureeditformitem.clearFormData();
	    	solutioneditpanel.measureeditformitem.reloadData();
    	}else if(selection[0].get('type') == 'S'){
    		solutioneditpanel.setActiveItem(solutioneditpanel.solutionform);
	    	solutioneditpanel.solutionform.initParam({
	    		'riskId' : selection[0].get('riskId'),
	    		'solutionId' : selection[0].get('measureId')
	    	});
	    	solutioneditpanel.solutionform.clearFormData();
	    	solutioneditpanel.solutionform.reloadData();
    	}
    },
    //修改措施
    delMeasure:function(){
    	var me=this;
    	var delUrl = '';
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(0 == selection.length){
    		Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.msgDel'));
    	}else{
			if(selection[0].get('type') == 'M'){
	    		delUrl = '/response/removeresponsemeasurelistbyids.f';
    		}else if(selection[0].get('type') == 'S'){
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
					    		Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),'对不起,您不能删除已提交的数据!');
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
	                                Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateSuccess'));
		 							me.store.load();
	                            } else {
	                                Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateFailure'));
	                            }
	   						}
						});
					}
				}
			});
    	}
    },
    getHigherQuery : function(obj){
    	var jsonObject = "";
    	if(obj.solutionhigherqueryform){
    		jsonObject = Ext.encode(obj.solutionhigherqueryform.getForm().getValues(false,false,true));
    	}
    	return jsonObject;
    },
	reloadData:function(){
		var me=this;
		// 通用列表当不是工作计划的工作流的时候用这个 url:__ctxPath +'/app/view/response/responseplan/ResponsePlanData.json',
		me.store.proxy.url = __ctxPath +'/app/view/response/responseplan/ResponsePlanData.json';
        var status;
        if(me.paramObj.status){
        	status = me.paramObj.status;
        }else{
        	status = "N";
        }
        
        me.store.proxy.extraParams = {
        	//公用type  为了使多个模块通用一个查询
        	type : me.paramObj.type,
        	//status  状态为 未执行：执行中：已执行
        	status : status,
        	selectId : me.paramObj.selectId,
        	queryJson : me.getHigherQuery(me)
        };
		me.store.load();
	},
	showMeasure : function(id,riskId){
		var me=this;
		var measureeditformitemforview = Ext.widget('measureeditformitemforview');
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
	showHigherQuery : function(){
		var me=this;
	    me.win.show();
	},
	showSolution : function(id,riskId){
		var me=this;
		var solutionformforview = Ext.widget('solutionformforview');
		solutionformforview.basicInfoFieldset.expand();
		solutionformforview.riskFieldSet.expand();
    	solutionformforview.initParam({
    		'solutionId' : id,
    		'riskId' : riskId
    	});
	    solutionformforview.reloadData();
	    
	    var win = Ext.create('FHD.ux.Window',{
				title:'应对措施详细信息',
				collapsible:false,
				maximizable:true//（是否增加最大化，默认没有）
	    }).show();
		win.add(solutionformforview);
	},
	getTopContainerBySelf : function(){
		var me = this;
		var obj = {};
//		var topContainer;
		if(me.up('solutionManager')){
	    	obj.topContainer = me.up('solutionManager'); //导航container实例
	    	obj.containers = obj.topContainer.containers;
	    	obj.tree = me.up('solutionManager').riskTree;
	    	obj.type = 'risk';
    	}else if(me.up('myallfoldermain')){
    		obj.topContainer = me.up('myallfoldermain'); //导航container实例
    		obj.containers = obj.topContainer.containers;
    		obj.tree = me.up('myallfoldermain').departmentTree;
    		obj.type = 'risk';
    	}else if(me.up('kpimonitormain')){
    		obj.topContainer = me.up('kpimonitormain'); //导航container实例
    		obj.containers = obj.topContainer.strMainContainer;
    		obj.tree = me.up('kpimonitormain').strTree;
    		obj.type = 'sm';
    	}else {
    		Ext.Msg.alert('提示','未找到顶级panel（服务测试 使用 ）');
    		return null;
    	}
		return obj;
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
	}
});