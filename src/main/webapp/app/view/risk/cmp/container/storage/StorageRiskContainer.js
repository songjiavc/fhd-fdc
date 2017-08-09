/**
 * 风险库risk页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.container.storage.StorageRiskContainer', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.storageriskcontainer',

    currentId: '',
    flex: 1,
    navHeight: 22,  //导航条高度，设置为0则为没有导航
    //是否切换的时候刷新面板
    onClickFlag : true,
    
    //导航条方法链接方法
    navFunId: '',
    //风险新增查看显示的表单，storage风险库，relate风险关联
    formType : 'storage',
    
    //风险事件查看后续事件
    showRiskDetailCallback: function(){},

    //新增风险分类后续处理方法
    addFormCallback: function (data,editflag) {},

    //修改导航条方法
    chageNavigationBar: function (nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(me.tabContainer.id + 'DIV', nodeId, name, 'risk', me.navFunId);
        }
    },
    //风险列表相关导航操作
    chageRiskNavigationBar: function (navId, nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(navId, nodeId, name, 'risk', me.navFunId);
        }
    },
    
    //改变新增按钮状态
    changeAddbuttonStatus: function(isDisable){
    	var me = this;
    	me.riskEventGrid.changeAddbuttonStatus(isDisable);
    },
    
    //切换风险基本信息表单
    showRiskAddForm: function(id,isAdd,schm){
    	var me = this;
    	me.riskFormViewDisable(false);
    	me.onClickFlag = false;
    	if(!me.riskAddForm){
    		me.riskAddFormContainer.onClick();
    	}
    	me.tabPanel.setActiveTab(me.riskAddFormContainer);
    	//将step切回到第一个
    	me.riskAddFormContainer.navToFirst();
    	if(isAdd){
    		me.riskAddForm.resetData('risk',id);
    		me.riskAddFormContainer.setAddState(true);
    	}else{
    		if(me.basicInfo.activeItem == 0){
                me.riskAddForm.reloadData(id);//me.currentId
            }else{
                me.riskKpiForm.reloadData(id);//me.currentId
            }
//    		me.riskAddForm.reloadData(id);
//    		me.riskKpiForm.reloadData(id);
    		me.riskAddFormContainer.setAddState(false);
    	}
    	me.onClickFlag = true;
    	me.chageNavigationBar(id,'');
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        //创建导航条
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');

        //风险事件列表页
        me.riskEventGridContainer = Ext.create('Ext.container.Container', {
            layout: 'fit',
            title: '风险列表',
            onClick: function () {
                if (!me.riskEventGrid) {
                    me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.RiskEventGrid', {
                        face: me,
                        border: false,
                        navHeight: me.navHeight,
                        formType: me.formType,
                        schm : me.schm, //添加风险分库标识
                        showRiskAdd: function (p, parentId, name) {
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', parentId, name);
                        },
                        showRiskDetail: function (p, parentId, name) {
                            me.showRiskDetailCallback();
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', parentId, name);
                        },
                        goback: function () {
                            me.reRightLayout(me.tabContainer);
                            me.chageNavigationBar(me.currentId,'');
                            //步骤导航点击保存按钮，一步提交保存，这块延迟一会刷新列表，目的是让修改后的数据先保存，在获取
                            setTimeout(function(){
                            	me.riskEventGrid.reloadData();
                            },500);
                            
                        }
                    });
                    this.add(me.riskEventGrid);
                    this.doLayout();
                }
                //根据左侧选中节点，初始化数据
                if (me.currentId != '') {
                    me.riskEventGrid.initParams('risk');
                    me.riskEventGrid.reloadData(me.currentId);
                }
            }
        });

        me.riskAddFormContainer =  Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'基本信息',
        	navToFirst:function(){	//切回到第一个
        		me.basicInfo.navToFirst();
        	},
        	setAddState:function(state){
        		me.basicInfo.setAddState(state);
        	},
        	onClick:function(){
        		if(!me.riskAddForm){//stepPanel
        			//2.表单，分步骤
        	        me.riskAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
        	        	navigatorTitle: '基本信息',
        	        	showbar:false,
        	        	type: 'rbs',
        	        	border: false,
                        schm : me.schm,
        	        	last:function(){
        	        		//验证不通过，返回false
			            	var result = me.riskAddForm.save(function(data,editflag){
			            		
			            		if(me.addFormCallback){
			            			me.addFormCallback(data,editflag);			            			
			            		}
			            		//传递新保存的riskId到下一个面板
		            			me.riskKpiForm.riskId = data.id;
		            			//添加是，下一个面板清空
		            			if(!me.riskAddForm.isEdit){
		            				me.riskKpiForm.resetData();
		            			}
			            	});
			            	return result;	
        	        	}
        			});
        	        me.riskKpiForm = Ext.create("FHD.view.risk.cmp.form.RiskStorageKpiForm",{
        	        	navigatorTitle: '风险指标',
        	        	back:function(){
        	        		//返回后，变成编辑状态
        	        		me.riskAddForm.isEdit = true;//返回上一步变成编辑状态，否则重复添加
        	        		me.riskAddForm.riskId=me.riskKpiForm.riskId;
        	        	}
        	        });
        	        me.basicInfo = Ext.create('FHD.ux.layout.StepNavigator', {
                        items: [me.riskAddForm,me.riskKpiForm],
                        undo: function () {
                        	//跳转到风险事件tab
                        	me.tabPanel.setActiveTab(me.riskEventGridContainer);
                        }
                    });
            		this.add(me.basicInfo);
            		this.doLayout();
        		}
				if(me.onClickFlag){
	    			//根据左侧选中节点，初始化数据
	        		if(me.currentId != ''){
	        			me.riskAddForm.reloadData(me.currentId);
	        			me.riskKpiForm.reloadData(me.currentId);
	        		}
				}
    		}
        	
        });
        
        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
            items: [me.riskEventGridContainer, me.riskAddFormContainer],//
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                	if(me.onClickFlag){
	                    if (newCard.onClick) {
	                        newCard.onClick();
	                    }
                	}
                }
            }
        });
        me.tabContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
            border: false,
            navHeight: me.navHeight,
            tabpanel: me.tabPanel,
            flex: 1
        });

        Ext.apply(me, {
            border: false,
            items: [me.tabContainer]
        });
        me.callParent(arguments);
 
        //初始加载页面
        me.riskEventGridContainer.onClick();
    },

    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        me.reRightLayout(me.tabContainer);
        var activeTab = me.tabPanel.getActiveTab();
        if(activeTab == null){
        	Ext.Msg.alert("提示","您没有任何tab权限，请设置一条tab权限！");
        	return;
        }
        
        if (me.riskEventGridContainer && activeTab.id == me.riskEventGridContainer.id) {
            me.riskEventGrid.initParams('risk');
            me.riskEventGrid.reloadData(me.currentId);
        }else if (me.riskAddFormContainer && activeTab.id == me.riskAddFormContainer.id) {
    		//将step页签变成可编辑状态
    		me.riskAddFormContainer.setAddState(false);
    		//if(me.basicInfo.activeItem == 0){
                me.riskAddForm.reloadData(me.currentId);
            //}else{
                me.riskKpiForm.reloadData(me.currentId);
            //}
//            me.riskAddForm.reloadData(me.currentId);
//            me.riskKpiForm.reloadData(me.currentId);
        }
    },

    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    },
    //基本信息页签在ROOT节点不可点击
    riskFormViewDisable: function (visible) {
        var me = this;
        if(visible){
	        var activeTab = me.tabPanel.getActiveTab();
	        if (activeTab.id == me.riskAddFormContainer.id) {
	            me.tabPanel.setActiveTab(me.riskEventGridContainer);
	            me.riskEventGridContainer.onClick();
	        }
        }
    	me.riskAddFormContainer.setDisabled(visible);
    }

});