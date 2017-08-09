/**
 * 指标监控-战略目标基本信息页签
 */
Ext.define('FHD.view.kpi.cmp.sm.SmBasicInfoContainer', {
    extend: 'Ext.container.Container',
    
    layout:'fit',
    
    title: '基本信息',
    /**
     * 基本信息提交后,追加树节点
     */
    appendTreeNode:function(node){
    	var me = this;
    	
    },
    selectTreeNode:function(data){
    	
    },
    /**
     * 基本信息添加后,刷新导航
     */
    refreshRightNavigator:function(){
    	var me = this;
    },
    /**
     *基本信息面板返回操作 
     */
    undo:function(){
    	var me = this;
    },
    setActiveType:function(){
    	var me = this;
    },
    getCurrentTreeNode:function(){
    	var me = this;
    },
    updateTreeNode:function(){
    	var me = this;
    },
    navToFirst:function(){
    	var me = this;
    	me.smBasicPanel.navToFirst();
    },
    reloadData:function(paramObj){
    	var me = this;
    	me.showComponent();
    	me.initSmBasicInfo(paramObj);
    },
    initSmBasicInfo:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
        me.smBasicPanel.setAddState(!paramObj.editflag);
        //初始化基本信息表单参数
        me.smBasicForm.initParam(paramObj);
		//清空基本信息表单
        me.smBasicForm.clearFormData();
        //重新加载基本信息表单
        me.smBasicForm.reloadData();
        //初始化衡量指标页面
        me.smKpiSet.initParam(paramObj);
        //重新加载衡量指标页面
        me.smKpiSet.reloadData();
        //初始化目标告警设置页面
        me.smWarningSet.initParam(paramObj);
        //重新加载目标告警设置页面
        me.smWarningSet.reloadData();
    },
    showComponent: function () {
        var me = this;
        me.setActiveType();
    	if (!me.smBasicPanel) {
            if (!me.smBasicForm) {
                me.smBasicForm = Ext.create('FHD.view.kpi.cmp.sm.SmBasicForm', {
                    navigatorTitle: '基本信息' ,
                    submitCallBack:function(editflag,smid,smname){
                    	//提交后要树进行操作的回调函数,如果是添加则添加树节点;如果是修改则修改节点名称
                    	if (!editflag) {
                    		//添加节点
                            var node = {
                                iconCls: 'icon-status-disable',
                                id: smid,
                                text: smname,
                                dbid: smid,
                                leaf: true,
                                type: 'sm'
                            };
                            me.appendTreeNode(node);
                        
                    	} else {
                        	//编辑节点,需要替换节点名称
                    		var currentnode = me.getCurrentTreeNode();
                     		
                      		if(currentnode){
                    			var data = currentnode.data;
                            	data.text = smname;
                            	me.updateTreeNode(data);
                    		}
                      		me.selectTreeNode(smid);
                    	}
                    	//刷新导航
                        me.refreshRightNavigator(smid);
                    }
                });
            }
            if(!me.smKpiSet){
            	me.smKpiSet = Ext.create('FHD.view.kpi.cmp.sm.SmKpiSet',{
                    navigatorTitle: '衡量指标' 
            	});
            }
            if(!me.smWarningSet){
            	me.smWarningSet = Ext.create('FHD.view.kpi.cmp.sm.SmWarningSet',{
                    navigatorTitle: '告警设置' 
            	});
            }
			var hiddenSave = false;
            /*if(!$ifAnyGranted('ROLE_ALL_TARGET_EDIT')){
    			hiddenSave = true;
    		}*/
            me.smBasicPanel = Ext.create('FHD.ux.layout.StepNavigator', {
            	hiddenSave:hiddenSave,
                items: [me.smBasicForm,me.smKpiSet,me.smWarningSet],
                undo: function () {
                	me.undo();
                }
            });
            this.add(me.smBasicPanel);

            this.doLayout();
        }
    },
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        Ext.apply(me, {
        	listeners: {
                show:function(me, eOpts){
                	me.showComponent();
                }
            }
        });

        me.callParent(arguments);
    }
});