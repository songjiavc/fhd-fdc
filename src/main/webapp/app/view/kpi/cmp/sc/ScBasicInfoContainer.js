/**
 * 考核指标设计-基本信息页签
 */
Ext.define('FHD.view.kpi.cmp.sc.ScBasicInfoContainer', {
    extend: 'Ext.container.Container',

    layout: 'fit',

    title: '基本信息',
    /**
     * 基本信息提交后,追加树节点
     */
    appendTreeNode: function (node) {
        var me = this;

    },
    /**
     * 基本信息添加后,刷新导航
     */
    refreshRightNavigator: function () {
        var me = this;
    },
    /**
     *基本信息面板返回操作
     */
    undo: function () {
        var me = this;
    },
    setActiveType: function () {
        var me = this;
    },
    getCurrentTreeNode: function () {
        var me = this;
    },
    updateTreeNode: function () {
        var me = this;
    },
    navToFirst: function () {
        var me = this;
        me.scBasicPanel.navToFirst();
    },
    reloadData:function(paramObj){
    	var me = this;
    	me.showComponent();
    	me.initScBasicInfo(paramObj);
    },
    initScBasicInfo: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
        me.scBasicPanel.setAddState(!paramObj.editflag);
        //清空基本信息表单
        me.scBasicForm.clearFormData();
        //初始化基本信息表单参数
        me.scBasicForm.initParam(paramObj);
        //重新加载基本信息表单
        me.scBasicForm.reloadData();
        //初始化衡量指标页面
        me.scKpiSet.initParam(paramObj);
        //重新加载告警设置页面
        me.scKpiSet.reloadData();
        //初始化告警设置页面
        me.scWarningSet.initParam(paramObj);
        //重新加载告警设置页面
        me.scWarningSet.reloadData();
        
        
    },
    showComponent: function () {
        var me = this;
        me.setActiveType();
        if (!me.scBasicPanel) {
            if (!me.scBasicForm) {
                me.scBasicForm = Ext.create('FHD.view.kpi.cmp.sc.ScBasicForm', {
                    navigatorTitle: '基本信息',
                    submitCallBack: function (editflag, scid, scname) {
                        //提交后要树进行操作的回调函数,如果是添加则添加树节点;如果是修改则修改节点名称
                        if (!editflag) {
                            //添加节点
                            var node = {
                                iconCls: 'icon-status-disable',
                                id: scid,
                                text: scname,
                                dbid: scid,
                                leaf: true,
                                type: 'kpi_category'
                            };
                            me.appendTreeNode(node);

                        } else {
                            //编辑节点,需要替换节点名称
                            var currentnode = me.getCurrentTreeNode();
                            if(currentnode){
                            	var data = currentnode.data;
                                data.text = scname;
                                me.updateTreeNode(data);
                            }
                        }
                        //刷新导航
                        me.refreshRightNavigator(scid);
                    }
                });
            }
            if(!me.scKpiSet) {
            	me.scKpiSet = Ext.create('FHD.view.kpi.cmp.sc.ScKpiSet',{
            		navigatorTitle: '衡量指标' 
            	});
            }

            if(!me.scWarningSet){
            	me.scWarningSet = Ext.create('FHD.view.kpi.cmp.sc.ScWarningSet',{
                    navigatorTitle: '告警设置' 
            	});
            }
            var hiddenSave = false;
            /*if(!$ifAnyGranted('ROLE_ALL_CATEGORY_EDIT')){
    			hiddenSave = true;
    		}*/


            me.scBasicPanel = Ext.create('FHD.ux.layout.StepNavigator', {
            	hiddenSave:hiddenSave,
                items: [me.scBasicForm ,me.scKpiSet,me.scWarningSet ],
                undo: function () {
                    me.undo();
                }
            });
            this.add(me.scBasicPanel);

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