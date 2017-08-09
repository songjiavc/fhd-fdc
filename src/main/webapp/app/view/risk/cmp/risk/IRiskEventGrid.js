/**
 * @author zhengjunxiang
 * 风险事件列表
 * 给部门风险，指标风险提供接口，添加风险，修改风险需要走流程审批
 */
Ext.define('FHD.view.risk.cmp.risk.IRiskEventGrid', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.iriskeventgrid',

    currentId: '',
    navHeight: 22,
    addButtonStatus : true,
    navData : null,
    //按不同的类型查询风险事件 risk，org,sm,process
    type: 'risk', 
    //操作按钮是否显示
    operateType: true,
    //风险新增查看显示的表单，storage风险库，relate风险关联,define风险定义
    formType : 'storage',//define,relate
    /**
     * private 内部属性
     */
    queryUrl: '/cmp/risk/findAllRiskEventByOrgId',
    
    //新增,修改接口
    showRiskAdd: function(p,parentId,name){},
    //查看接口
    showRiskDetail: function(p,parentId,name){},
    //新增修改返回接口
    goback: function(){},
    //改变新增按钮状态
    changeAddbuttonStatus: function(isDisable){
    	var me = this;
    	if(me.down("[name='addbutton']")){
	    	me.down("[name='addbutton']").setDisabled(isDisable);
    	}
    },

    edit : function(riskId){
    	var me = this;
    	//me.quaAssessEdit = Ext.widget('quaAssessEdit',{isEditIdea : false});
    	var selection = me.grid.getSelectionModel().getSelection()[0];
        var riskId = selection.data.id;
    	me.quaAssessEdit = Ext.create('FHD.view.risk.riskDataEditFlow.RiskEditIdea');//riskEditIdeaApprove,riskEditIdea
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
				{
					text: '修改',
					handler:function(){
						FHD.ajax({
				            url: __ctxPath + '/saveRiskEditIdea.f',
				            params: {
				            	editIdeaContent : me.quaAssessEdit.editIdea.getValue(),
				            	empRelaRiskIdeaId : me.quaAssessEdit.objectDeptEmpIdHide.value,
				            	riskId : riskId
				            },
				            callback: function (data) {
				            	//me.grid.store.load();
				            	FHD.notification('修改成功','操作');
				            	me.formwindow.close();
				            	//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
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
		me.quaAssessEdit.load(riskId, true);
    },
    
    initComponent: function () {
        var me = this;

        //1.列表
        var cols = [{
            dataIndex: 'id',
            invisible: true
        }, {
            dataIndex: 'belongRisk',
            invisible: true
        }, {
            dataIndex: 'parentId',
            invisible: true
        }, {
            dataIndex: 'parentId',
            invisible: true
        }, {
            cls: 'grid-icon-column-header grid-statushead-column-header',
			header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'assessementStatus',
            exportText : '风险水平',
            sortable: true,
            menuDisabled:true,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-symbol-4-sm") {
                    color = "symbol_4_sm";
                    display = FHD.locale
                        .get("fhd.alarmplan.form.hight");
                } else if (v == "icon-ibm-symbol-6-sm") {
                    color = "symbol_6_sm";
                    display = FHD.locale
                        .get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
                    color = "symbol_5_sm";
                    display = FHD.locale
                        .get("fhd.alarmplan.form.min");
                } else {
                    v = "icon-ibm-underconstruction-small";
                    display = "无";
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
            }
        }, {
            cls: 'grid-icon-column-header grid-trendhead-column-header',
			header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>", 
            dataIndex: 'etrend',
            exportText : '趋势',
            menuDisabled:true,
            sortable: true,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-icon-trend-rising-positive") {
                    color = "icon_trend_rising_positive";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.positiv");
                } else if (v == "icon-ibm-icon-trend-neutral-null") {
                    color = "icon_trend_neutral_null";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.flat");
                } else if (v == "icon-ibm-icon-trend-falling-negative") {
                    color = "icon_trend_falling_negative";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.negative");
                }
//                else {
//                    v = "icon-ibm-underconstruction-small";
//                    display = "无";
//                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'></div>";
            }
        }, {
            header: '名称',
            dataIndex: 'name',
            exportText : '名称',
            sortable: false,
            flex: 2,
            align: 'left',
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['id'];
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + id + "')\" >" + value + "</a>";
            }
        }, {
            header: '所属风险',
            exportText : '所属风险',
            dataIndex: 'parentName',
            sortable: false,
            width: 150,
            renderer: function (value, metaData, record, colIndex, store, view) {
                var detail = record.data['belongRisk'];
                return "<div data-qtitle='' data-qtip='" + detail + "'>" + value + "</div>";
            }
        }, {
            header: "状态",
            exportText : '状态',
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
        
        if(me.operateType){
        	cols.push({
	            header: FHD.locale.get('fhd.common.operate'),
            	exportText : '操作',
	            dataIndex: 'operate',
	            sortable: false,
	            notExport: true,
	            align:'center',
	            width: 65,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	var id = record.data['id'];
	                //查看信息时，设置导航
	                var parentId = record.data['parentId'];
	                var name = record.data['name'];
	                if (name.length > 23) {
	                    name = name.substring(0, 20) + "...";
	                }
	                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailContainer('" + id + "','" + parentId + "','" + name + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>";
	            }
	        });
        }
        
        me.grid = Ext.create('FHD.ux.GridPanel', {
            border: false,
            cols: cols,
            tbarItems: [
//            	{
//            		authority:'ROLE_ALL_RISK_ADDCHECK',
//                    btype: 'add',
//                    name: 'addbutton',
//                    handler: function () {
//                        me.addFun();
//                    }
//                }, '-', {
//                	authority:'ROLE_ALL_RISK_EDITSUGGESTION',
//                    btype: 'edit',
//                    name: 'editbutton',
//                    text:'修改意见',
//                    disabled: true,
//                    handler: function () {
//                        //me.editFun();
//                    	me.edit();
//                    }
//                }, 
                {
                    btype: 'add',
                    name: 'addbutton',
                    authority:'ROLE_ALL_RISK_ADD',
                    handler: function () {
                        me.addFun();
                    }
                },{
                    btype: 'edit',
                    name: 'editbutton',
                    authority:'ROLE_ALL_RISK_EDIT',
                    disabled: true,
                    handler: function () {
                        me.editFun();
                    }
                },{
                    btype: 'delete',
                    name: 'delbutton',
                    authority:'ALL_ASSESS_IDENTIFY_LIBRARY_DEL',
                    disabled: true,
                    handler: function () {
                        me.delFun();
                    }
                },{
                    btype: 'op',
                    authority:'ALL_ASSESS_IDENTIFY_LIBRARY_EXPORT',
                    tooltip: '导出数据到excel',
                    text: '导出',
                    iconCls: 'icon-ibm-action-export-to-excel',
                    handler: function () {
                        me.exportChart();
                    }
                }
            ]
        });

        Ext.apply(me, {
            items: [me.grid]
        });
        me.callParent(arguments);

        //记录发生改变时改变按钮可用状态
        me.grid.on('selectionchange', function () {
            me.setstatus(me.grid);
        });
    },

    // 设置按钮可用状态
    setstatus: function (me) {
    	if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(me.getSelectionModel().getSelection().length === 0);
    	}
        if(me.down("[name='editbutton']")){
	        if (me.getSelectionModel().getSelection().length == 1 && me.getSelectionModel().getSelection()[0].data.archiveStatus != 'examine') {
	            me.down("[name='editbutton']").setDisabled(false);
	        } else {
	            me.down("[name='editbutton']").setDisabled(true);
	        }
    	}
    },
    
    reloadData: function (id) {
        var me = this;
        if(me.down("[name='editbutton']")){
	        me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(true);
        }
        if (id != null) {
            me.currentId = id;
        }
        me.grid.store.proxy.url = __ctxPath + me.queryUrl;
        me.grid.store.proxy.extraParams.id = me.currentId;
        me.grid.store.proxy.extraParams.type = me.type;
        me.grid.store.load();
    },

    initParams: function (type) {
        var me = this;
        me.type = type;
    },

    /**
     * 添加
     */
    addFun: function () {
        var me = this;
        me.showRiskEventAddContainer(null, me.currentId, "风险事件添加");
    },

    /**
     * 编辑
     */
    editFun: function () {
        var me = this;
        var selection = me.grid.getSelectionModel().getSelection()[0];
        var id = selection.data.id;
        
        //只有归档状态,已删除状态，保存状态的风险选中，才可以操作
        var status = selection.data.archiveStatus;
        if(status=='archived' || status=='saved' || status=='deleted'){
        	//编辑时，设置导航
	        var parentId = selection.data.parentId;
	        var name = selection.data.name;
	        if (name.length > 33) {
	            name = name.substring(0, 30) + "...";
	        }
	        //切换到风险事件添加card
	        me.showRiskEventAddContainer(id, parentId, name);
        }else{
        	FHD.notification("正在审批的风险不允许修改","提示");
        	return;
        }
    },

    showRiskEventAddContainer: function (id, parentId, name) {
        var me = this;

        if (!me.riskEventAddContainer) {
            //风险事件基本信息
        	if(me.formType == 'storage'){
	            me.riskAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
    	        	navigatorTitle: '基本信息',
    	        	showbar:false,
    	        	archiveStatus:'saved',
    	        	type: 're',
    	        	border: false,
    	        	last:function(){
    	        		//验证不通过，返回false
		            	var result = me.riskAddForm.save(function(data,editflag){
		            		//传递新保存的riskId到下一个面板
		            		me.solutioneditpanel.initParam({
					            type: '0',//自动任务
					            selectId: data.id
					        });
					        me.solutioneditpanel.reloadData();
		            	});
		            	return result;	
    	        	}
    			});
    			me.solutioneditpanel = Ext.create("FHD.view.response.new.SolutionEditPanel",{
    	        	navigatorTitle: '风险应对',
    	        	showType : 'window'
    	        });
    	        me.riskEventAddForm = Ext.create('FHD.ux.layout.StepNavigator', {
    	        	title:'基本信息',
                    items: [me.riskAddForm,me.solutioneditpanel],
                    undo: function () {
                    	//跳转到风险事件tab
                    	me.goback();
                    }
                });
    			
        	}else if(me.formType == 'relate'){
        		me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
        			type: 're',
        			//showSubmitBtn:true,
        			archiveStatus:'saved',
        			border: false,
	                title: '基本信息',
	                showbar: true,
	                goback: function(){
	                	me.goback();
	                }
	            });
        	}else if(me.formType == 'define'){
        		me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskShortForm', {
        			type: 're',
        			//showSubmitBtn:true,
        			archiveStatus:'saved',
        			border: false,
	                title: '基本信息',
	                showbar: true,
	                goback: function(){
	                	me.goback();
	                }
	            });
        	}
        	
            /**
             * 宋佳添加风险应对
             */
            me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.riskEventAddForm]
            });
            me.riskEventAddContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }

        if (id) {
        	if(me.formType == 'storage'){//me.formType == 'storage'
        		//切换到第一个step
        		me.riskEventAddForm.navToFirst();
        		//将step页签变成可编辑状态
	    		me.riskEventAddForm.setAddState(false);
	            me.riskAddForm.reloadData(id);
	            me.solutioneditpanel.initParam({
		            type: '0',
		            selectId: id
		        });
	            me.solutioneditpanel.reloadData();
        	}else{
            	me.riskEventAddForm.reloadData(id);
        	}
        } else {
        	if(me.formType ==  'define'){
        		me.riskEventAddForm.resetData();
        		if('myfolder' == me.type){
        			me.riskEventAddForm.setDutyDepartmentValue(__user.majorDeptId,__user.empId);
        		}else{
	        		me.riskEventAddForm.setDutyDepartmentValue(me.currentId,null);
        		}
        	}else if(me.formType == 'storage'){
				//切换到第一个step
    			me.riskEventAddForm.navToFirst();
    			//将step页签变成可添加状态
    			me.riskEventAddForm.setAddState(true);
    			if('myfolder' == me.type){
        			me.riskAddForm.resetData('org', __user.majorDeptId,__user.empId);
        		}else{
		            me.riskAddForm.resetData('org', me.currentId);
        		}
    		}else{
        		if('myfolder' == me.type){
        			me.riskEventAddForm.resetData(me.type, __user.majorDeptId,__user.empId);
        		}else{
		            me.riskEventAddForm.resetData('org', me.currentId);
        		}
        	}
        }
        me.showRiskAdd(me.riskEventAddContainer, parentId, name);
        if(me.navData){
    		var data = [];
    		Ext.Array.push(data,me.navData)
			data.push({
	               type: 'iriskevent',
	               id: 'iriskevent',
	               containerId: me.id,
	               name: name
	        });
    		me.reLayoutNavigationBar(data);
		}
    },
	showRiskEventDetail: function(id){
		var me = this;
            // 风险事件基本信息
        var riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
            border: false,
            showbar: false
        });
        riskEventDetailForm.reloadData(id);
        var window = Ext.create('FHD.ux.Window', {
                title: '风险基本信息',
                maximizable: true,
                modal: true,
                width: 800,
                height: 500,
                collapsible: true,
                autoScroll: true,
                items: riskEventDetailForm
            }).show();
	},

    /**
     * 创建风险事件查看容器
     */
    showRiskEventDetailContainer: function (id, parentId, name) {
        var me = this;
        if(me.navData){
    		var data = [];
    		Ext.Array.push(data,me.navData)
			data.push({
	               type: 'iriskevent',
	               id: 'iriskevent',
	               containerId: me.id,
	               name: name
	        });
    		me.reLayoutNavigationBar(data);
		}
        if (!me.riskEventDetailContainer) {
        	
            me.historyEventGrid = Ext.create('FHD.view.risk.hisevent.HistoryEventGridPanel', {
        		title: '历史事件',
                border: false,
                reLayoutNavigationBar : function(date){
        			me.reLayoutNavigationBar(date);
        		}
            });
        	
            me.solutioneditpanel = Ext.create('FHD.view.response.new.SolutionEditPanel', {
            	title: '风险应对',
            	businessType: 'analysis',
                type: 'risk',
                reLayoutNavigationBar : function(date){
        			me.reLayoutNavigationBar(date);
        		}
            });
            
            me.riskEventHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
            	title: '历史数据',
                type: 'riskevent',
                border: false
            });
	        
	        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
	        	title: '图形分析',
	        	layout:'fit',
	        	reloadData:function(riskId){//alert(riskId);
	        		if(!me.riskGraph){
	        			//2.表单
	        	        me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
	        			});
	            		this.add(me.riskGraph);
	            		this.doLayout();
	        		}
	        		
	    			//根据左侧选中节点，初始化数据
	        		me.riskGraph.initParam({
		                 riskId:riskId
		        	});
	        		me.riskGraph.reloadData();
	        	}	        	
	        });
            
            me.riskEventTabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
                items: [me.solutioneditpanel,me.historyEventGrid,me.riskGraphContainer,me.riskEventHistoryGrid],
                navData: data,
                listeners : {
                	tabchange : function (tabPanel, newCard, oldCard, eOpts) {
                		if(me.navData){
	                		me.reLayoutNavigationBar(me.riskEventTabPanel.navData);
                		}
                	}
                }
            });
            me.riskEventDetailContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }
        me.historyEventGrid.navData = data;
        me.solutioneditpanel.navData = data;
        me.riskEventHistoryGrid.navData = data;
        me.riskGraphContainer.navData = data;
        me.riskEventTabPanel.navData = data;
        
        me.historyEventGrid.reloadData(id);
        me.solutioneditpanel.initParam({
            type: '0',
            selectId: id
        });
        me.solutioneditpanel.reloadData();
        me.riskEventHistoryGrid.reloadData(id);
        me.riskGraphContainer.reloadData(id);
        me.showRiskDetail(me.riskEventDetailContainer, parentId, name);
    },

    /**
     * 删除
     */
    delFun: function () {
        var me = this;
        var deleteCheckUrl = '/risk/risk/findRiskCanBeRemoved.f';
        var delUrl = '/risk/risk/removeRiskById.f';
        var selections = me.grid.getSelectionModel().getSelection();
        if (selections.length > 0) {
        	//1.判断是否可以删除风险，如果有叶子节点和风险已经别打分，将不能进行删除
        	var canBeRemoved = true;
        	for(var i=0;i<selections.length;i++){
        		var id = selections[i].data.id;
        		//只有归档状态,已删除状态，保存状态的风险选中，才可以操作
		        var status = selections[0].data.archiveStatus;
		        if(status!='archived' && status!='saved' && status!='deleted'){
					FHD.notification('正在审批的风险不允许删除','提示');		        	
		        	return;
		        }
            	FHD.ajax({
            		async:false,
    				url : __ctxPath + deleteCheckUrl + "?id=" + id,
    				callback : function(data) {
    					if(data.success) {//删除成功！
    						
    					}else{
    						if(data.type == 'hasChildren'){	//分类下有子风险
    							Ext.MessageBox.show({
    		            			title:'操作错误',
    		            			msg:'该风险下有下级风险，不允许删除!'
    		            		});
    							canBeRemoved = false;
    						}else if(data.type == 'hasRef'){// 在其他模块被引用了
    							Ext.MessageBox.show({
    		            			title:'操作错误',
    		            			msg:'该风险已经被使用，不允许删除!'
    		            		});
    							canBeRemoved = false;
    						}else if(data.type == 'notSelfCreate'){// 不是自己添加的风险事件，不允许删除
    							Ext.MessageBox.show({
    		            			title:'操作错误',
    		            			msg:'该风险由其他人添加，不允许删除!'
    		            		});
    							canBeRemoved = false;
    						}else{
    							
    						}
    					}
    				}
    			});
        	}

        	//2.开始删除
        	if(canBeRemoved){
                Ext.MessageBox.show({
                    title: FHD.locale.get('fhd.common.delete'),
                    width: 260,
                    msg: FHD.locale.get('fhd.common.makeSureDelete'),
                    buttons: Ext.MessageBox.YESNO,
                    icon: Ext.MessageBox.QUESTION,
                    fn: function (btn) {
                        if (btn == 'yes') { //确认删除
                            var ids = [];
                            Ext.Array.each(selections,
                                function (item) {
                                    ids.push(item.get("id"));
                                });
                            FHD.ajax({ //ajax调用
                                url: __ctxPath + delUrl + "?ids=" + ids.join(','),
                                callback: function (data) {
                                    if (data) { //删除成功！
                                        FHD.notification(FHD.locale
                                            .get('fhd.common.operateSuccess'),
                                            FHD.locale.get('fhd.common.prompt'));
                                        me.reloadData();
                                    }
                                }
                            });
                        }
                    }
                });
        	}
        } else {
            FHD.notification('请选择一条指标.',FHD.locale.get('fhd.common.prompt'));
            return;
        }
    },
    /**
     * 启动和关闭
     */
    enablesFn: function (enable) {
        var me = this;

        var selections = me.getSelectionModel().getSelection();
        if (selections.length > 0) {
            var ids = [];
            Ext.Array.each(selections,
                function (item) {
                    ids.push(item.get("id"));
                });
            FHD.ajax({
                url: __ctxPath + '/risk/enableRisk',
                params: {
                    ids: ids.join(','),
                    isUsed: enable
                },
                callback: function (data) {
                    if (data) {
                        me.store.load();
                        if (enable == '0yn_y') {
                            Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '启用成功!');
                        } else {
                            Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '停用成功!');
                        }

                    }
                }
            });
        } else {
            FHD.notification('请选择一条指标.',FHD.locale.get('fhd.common.prompt'));
            return;
        }

    },
    exportChart: function () {
        var me = this;
        me.headerDatas = [];
        var items = me.grid.columns;
        Ext.each(items, function (item) {
            if ((!item.hidden && item.dataIndex != '' && !item.notExport)) {
                var value = {};
                value['dataIndex'] = item.dataIndex;
                value['text'] = item.exportText;
                me.headerDatas.push(value);
            }
        });
        window.location.href = "cmp/risk/exportriskgrid.f?id=" + me.currentId + "&type=" + me.type + "&exportFileName=" + "" +
            "&sheetName=" + "" + "&headerData=" + Ext.encode(me.headerDatas) + "&style=" + "event";
    },
    reLayoutNavigationBar : function(data){},
    go : function(){
    	var me = this;
    	me.riskEventTabPanel.getLayout().getActiveItem().go();
    }

});