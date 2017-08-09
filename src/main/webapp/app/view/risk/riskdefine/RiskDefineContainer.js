/**
 * 风险库risk页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.riskdefine.RiskDefineContainer', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskdefinecontainer',

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
    
    //切换风险基本信息表单
    showRiskAddForm: function(id,isAdd){
    	var me = this;
    	me.riskFormViewDisable(false);
    	me.onClickFlag = false;
    	if(!me.riskAddForm){
    		me.riskAddFormContainer.onClick();
    	}
    	me.tabPanel.setActiveTab(me.riskAddFormContainer);
    	if(isAdd){
    		me.riskAddForm.resetData('risk',id);
    		me.basicInfo.navToFirst();		//导航到第1个tab
    		me.basicInfo.setAddState(true); //设置下一步不可点
    		/**
    		 * 宋佳修改 当添加部门风险分类时要将主责部门初始化，并设置为不可编辑状态
    		 * 2017-4-12  [{"deptid":"2a3de568-95d8-4319-b4e2-c4b43346b5cc","deptno":"1_025","deptname":"理化测试中心","empid":""}]
    		 */
    		if(me.typeId == 'dept'){
	    		me.riskAddForm.respDeptName.initValue([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
	    		me.riskAddForm.respDeptName.setValues([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
    			me.riskAddForm.respDeptName.setReadOnly(true);
    		}
    	}else{
    		me.basicInfo.setAddState(false); //设置下一步不可点
    		me.riskAddForm.reloadData(id);
    		me.riskKpiForm.reloadData(id);
    		if(me.typeId == 'dept'){
    			me.riskAddForm.respDeptName.setReadOnly(true);
    		}
    	}
    	me.onClickFlag = true;
    	me.chageNavigationBar(id,'');
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        //创建导航条
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');

        me.riskAddFormContainer =  Ext.create('Ext.panel.Panel',{
        	layout:'fit',
        	border:false,
        	title:'基本信息',
            listeners:{
            	resize:function(){
            		//刚开始显示时，显示加载提示
            		if(!me.riskAddForm){
                        me.riskAddFormContainer.body.mask("正在加载中...","x-mask-loading");//XABM
                        me.chageNavigationBar('','');
            		}
            	}
            }
        	
        });
        
        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
            items: [me.riskAddFormContainer],
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
        //me.riskAddFormContainer.onClick();
    },

    /**
     * 右侧添加基本信息表单。在整个页面加载后
     */
    addCenterPanel:function(){
    	var me = this;

		if(!me.riskAddForm){//stepPanel
			//2.表单，分步骤
	        me.riskAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
	        	navigatorTitle: '基本信息',
	        	showbar:false,
	        	typeId : me.typeId,
	        	type: 'rbs',
	        	border: false,
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
	        	hiddenUndo:true,
                items: [me.riskAddForm,me.riskKpiForm]
            });
	        me.riskAddFormContainer.add(me.basicInfo);
	        me.riskAddFormContainer.doLayout();
		}
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
        me.basicInfo.navToFirst();
        me.riskAddForm.reloadData(me.currentId);//刷新基本信息
        me.riskKpiForm.reloadData(me.currentId);//刷新指标信息
        me.basicInfo.setAddState(false); //设置下一步不可点
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
    	me.riskAddFormContainer.setDisabled(visible);
    },
    
    destroy:function(){
    	var me = this;
    	
    	//销毁组件
    	
    	//销毁对象
    	me.navigationBar = null;
    	
    	me.callParent(arguments);
    }

});