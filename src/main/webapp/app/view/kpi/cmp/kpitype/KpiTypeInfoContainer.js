/**
 * 目标管理-指标类型面板
 */
Ext.define('FHD.view.kpi.cmp.kpitype.KpiTypeInfoContainer', {
    extend: 'Ext.container.Container',
    
    layout:'fit',
    
    title: '基本信息',
    /**
     * 基本信息提交后,追加树节点
     */
    appendTreeNode:function(node){
    	var me = this;
    	
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
    	me.kpiTypeBasicPanel.navToFirst();
    },
    initKpiTypeBasicInfo:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
        me.kpiTypeBasicPanel.setAddState(!paramObj.editflag);
        me.kpiTypeBasicForm.clearFormData();
        me.kpiTypeBasicForm.initParam(paramObj);
        me.kpiTypeBasicForm.reloadData();
        
        me.kpiTypeGatherForm.clearFormData();
        me.kpiTypeGatherForm.initParam(paramObj);
        me.kpiTypeGatherForm.reloadData();
        
        me.kpiTypeWarningSet.initParam(paramObj);
        me.kpiTypeWarningSet.reloadData();
    },
    showComponent:function(){
    	var me = this;
    	me.setActiveType();
    	if (!me.kpiTypeBasicPanel) {
            if (!me.kpiTypeBasicForm) {
                me.kpiTypeBasicForm = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeBasicForm', {
                    navigatorTitle: '基本信息' ,
                    submitCallBack:function(editflag,typeid,typename){
                    	//提交后要树进行操作的回调函数,如果是添加则添加树节点;如果是修改则修改节点名称
                    	if (!editflag) {
                    		//添加节点
                            var node = {
                                iconCls: 'icon-ibm-icon-metrictypes',
                                id: typeid,
                                text: typename,
                                dbid: typeid,
                                leaf: true,
                                type: 'kpi_type'
                            };
                            me.appendTreeNode(node);
                        
                    	} else {
                        	//编辑节点,需要替换节点名称
                    		var currentnode = me.getCurrentTreeNode();
                       		var data = currentnode.data;
                        	data.text = typename;
                        	me.updateTreeNode(data);
                    	}
                    	//刷新导航
                        me.refreshRightNavigator(typeid);
                    }
                });
            }
            if(!me.kpiTypeGatherForm){
            	me.kpiTypeGatherForm = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeGatherForm',{
            		navigatorTitle: '采集设置' 
            	});
            }
            if(!me.kpiTypeWarningSet){
            	me.kpiTypeWarningSet = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeWarningSet',{
            		navigatorTitle: '告警设置' 
            	});
            }

            me.kpiTypeBasicPanel = Ext.create('FHD.ux.layout.StepNavigator', {
                items: [me.kpiTypeBasicForm,me.kpiTypeGatherForm,me.kpiTypeWarningSet],
                undo: function () {
                	me.undo();
                }
            });
            this.add(me.kpiTypeBasicPanel);

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