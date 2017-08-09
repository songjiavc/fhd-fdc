/**
 * 新风险树，带右键菜单
 *
 * @author
 */
Ext.define('FHD.view.risk.riskstorage.RiskStorageTreePanelNew', {
    extend: 'FHD.view.risk.cmp.RiskTreePanel',
    alias: 'widget.riskstoragetreepanelnew',



    /**
     * 树节点被单击处理函数
     */
    nodeClick: function (record) {
        var me = this;
        var id = record.data.id;
        //改变新增按钮状态
        //叶子节点才可以添加风险事件
        if(record.data.leaf){
            me.up('riskstoragemainpanelnew').riskCard.riskGrid.changeAddbuttonStatus(false);
        }else{
            me.up('riskstoragemainpanelnew').riskCard.riskGrid.changeAddbuttonStatus(true);
        }
        //保存当前节点
        me.nodeId = id;
        me.node = record;
        me.nodeType = 'risk';
        //刷新右侧容器数据
        me.up('riskstoragemainpanelnew').riskCard.setActiveItem(0);
        me.up('riskstoragemainpanelnew').riskCard.riskGrid.reloadData(id, 'risk');
    },
    
    
    /**
     * 获取当前选中节点
     */
    getCurrentNode : function(){
    	var me = this;
    	return me.getSelectionModel().getSelection()[0];
    },
    /**
     * 选中首节点//并激活树节点被单击处理函数
     */
    selectFirstNode:function(){
        //选择默认节点
        var me = this;
        var selectedNode = null;
        var firstNode = me.riskTree.getRootNode().firstChild;
        if (null != firstNode) {
            me.riskTree.getSelectionModel().select(firstNode);
            selectedNode = firstNode;
            me.nodeId = selectedNode.data.id;
            me.nodeType = 'risk';
            me.node = selectedNode;
            //标识首节点被选中
            firstNodeSelected = true;
        }

    },

    // 初始化方法
    initComponent: function() {
        var me = this;

        Ext.apply(me, {
            showLight : true,
            listeners: {
                itemclick: function (tablepanel, record, item, index, e, options) {
                    me.nodeClick(record);
                },
                /**
                 * 右键监听事件
                 */
                itemcontextmenu: function (view, rec, node, index, e) {
                    e.stopEvent();
                    var menu = me.contextItemMenuFun(view, rec, node, index, e);
                    if (menu) {
                        menu.showAt(e.getPoint());
                    }
                    return false;
                },
                afteritemexpand: function(node,index,item,eOpts){
                    if(!me.node){
                        me.selectFirstNode();
                    }
                }
            }
        });

        me.callParent(arguments);
    },

    /**
     * 添加右键菜单
     */
    contextItemMenuFun: function (view, rec, node, index, e) {
        var me = this;
        var id = rec.data.id;

        var menu = Ext.create('FHD.ux.Menu', {
            margin: '0 0 10 0',
            items: []
        });

        /*删除*/
        var del = {
            authority:'ROLE_ALL_RISK_DELETE',
            iconCls: 'icon-del',
            text: "删除",
            handler: function () {
                var deleteCheckUrl = '/risk/risk/findRiskCanBeRemoved.f';
                var delUrl = '/risk/risk/removeRiskById.f';
                var selection = me.getSelectionModel().getSelection()[0];

                //1.判断是否可以删除风险，如果有叶子节点和风险已经别打分，将不能进行删除
                var canBeRemoved = false;
                FHD.ajax({
                    async:false,
                    url : __ctxPath + deleteCheckUrl + "?id=" + selection.data.id,
                    callback : function(data) {
                        if(data.success) {//删除成功！
                            canBeRemoved = true;
                        }else{
                            if(data.type == 'hasChildren'){	//分类下有子风险
                                Ext.MessageBox.show({
                                    title:'操作错误',
                                    msg:'该风险下有下级风险，不允许删除!'
                                });
                            }else if(data.type == 'hasRef'){	//hasRef 在其他模块被引用了
                                Ext.MessageBox.show({
                                    title:'操作错误',
                                    msg:'该风险已经被使用，不允许删除!'
                                });
                            }else{//风险管理删除不判断是否是自己创建的
                                canBeRemoved = true;
                            }
                        }
                    }
                });

                //2.开始删除
                if(canBeRemoved){
                    Ext.MessageBox.show({
                        title : '删除',
                        width : 260,
                        buttons : Ext.MessageBox.YESNO,
                        icon : Ext.MessageBox.QUESTION,
                        msg : FHD.locale.get('fhd.common.makeSureDelete'),
                        fn : function(btn) {
                            if (btn == 'yes') {//确认删除
                                FHD.ajax({//ajax调用
                                    url : __ctxPath + delUrl + "?ids=" + selection.data.id,
                                    callback : function(data) {
                                        if (data) {//删除成功！
                                            FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),
                                                FHD.locale.get('fhd.common.prompt'));
                                            var parentnode = rec.parentNode;

                                            //更改左侧节点，删除左侧树节点
                                            parentnode.removeChild(rec);
                                            if (null != parentnode && !parentnode.hasChildNodes()) {
                                                var nodeData = parentnode.data;
                                                nodeData.leaf = true;
                                                parentnode.updateInfo(true, nodeData);
                                            }
                                            me.getSelectionModel().select(parentnode);
                                            //防止上级节点时根节点,编辑查询报错
                                            if(parentnode.data.id!='root'){
                                                me.riskDeleteCallback(parentnode);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    })
                }
            }
        };

        /*刷新*/
        var refresh = {
            iconCls: 'icon-arrow-refresh-small',
            text: "刷新",
            handler: function () {
                me.store.load();
            }
        };

        //根节点只有刷新操作
        if(id=="root"){
            menu.add(refresh);
        }else{
            menu.add(del);
            menu.add(refresh);
        }

        return menu;
    }
});