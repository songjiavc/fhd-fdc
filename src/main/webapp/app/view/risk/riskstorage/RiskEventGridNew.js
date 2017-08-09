Ext.define('FHD.view.risk.riskstorage.RiskEventGridNew', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskeventgridnew',
    requires: [
        'FHD.view.risk.assess.utils.GridCells'
    ],
	storeAutoLoad : false,
    url : __ctxPath + '/cmp/risk/findEventById',
    /**
     * private 内部属性
     */
    //queryUrl: '/cmp/risk/findEventById',

    //改变新增按钮状态
    changeAddbuttonStatus: function(isDisable){
        var me = this;
        if(me.down("[name='addbutton']")){
            me.down("[name='addbutton']").setDisabled(isDisable);
        }
    },
    //加载数据
    reloadData: function (id,type) {
        var me = this;
        //设置按钮状态
        if(me.down("[name='editbutton']")){
            me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='delbutton']")){
            me.down("[name='delbutton']").setDisabled(true);
        }
        if (id != null) {
            me.currentId = id;
        }
        /**
         * @author add by jia.song@pcitc.com
         * @desc     修改grid参数传入问题
         * @date     2017-6-20
         */
        me.store.proxy.extraParams = {
        	id : me.currentId,
        	type : type,
        	schm : me.schm
        };
        /*
        me.store.proxy.extraParams.id = me.currentId;
        me.store.proxy.extraParams.type = type;
        //添加风险分库标识
        me.store.proxy.extraParams.schm = me.schm;
        */
        me.store.load();
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
            header: '所属风险',
            dataIndex: 'parentName',
            sortable: false,
            width: 150,
            renderer: function (value, metaData, record, colIndex, store, view) {
                var detail = record.data['belongRisk'];
                return "<div data-qtitle='' data-qtip='" + detail + "'>" + value + "</div>";
            }
        }, {
            header: '名称',
            dataIndex: 'name',
            sortable: false,
            flex: 2,
            align: 'left',
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['id'];
                //查看信息时，设置导航
                var parentId = record.data['parentId'];
                var name = record.data['name'];
                if (name.length > 33) {
                    name = name.substring(0, 30) + "...";
                }
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').editFun()\" >" + value + "</a>";
            }
        },{
        	 header: '应对措施',
            dataIndex: 'responseText',
            sortable: false,
            flex: 2,
            align: 'left',
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
            header: '责任部门',
            dataIndex: 'respDeptName',
            sortable: false,
            width: 100,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
            header: '相关部门',
            dataIndex: 'relaDeptName',
            sortable: false,
            width: 100,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'assessementStatus',
            sortable: true,
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
        }, 
        /*   change by jia.song 
        {
            cls: 'grid-icon-column-header grid-trendhead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'etrend',
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
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'></div>";
            }
        }, */  {
            header: '状态',
            dataIndex: 'isNew',
            sortable: false,
            width: 60,
            renderer: function (value, metaData, record, colIndex, store, view) {
                var icon = 'icon-status-assess_mr';
                if (record.data['isNew']) {
                    icon = 'icon-status-assess_new';
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + icon + "'/>";
            }
        }];

        Ext.apply(me, {
            cols:cols,
            border: false,
            columnLines: true,
            tbarItems: [{
                btype: 'add',
                name: 'addbutton',
                disabled: true,
                authority:'ROLE_ALL_RISK_ADD',
                icon : '',
                handler: function () {
                    me.addFun();
                }
            }, {
                btype: 'edit',
                name: 'editbutton',
                authority:'ROLE_ALL_RISK_EDIT',
                disabled: true,
                handler: function () {
                    me.editFun();
                }
            }, {
                btype: 'delete',
                name: 'delbutton',
                authority:'ROLE_ALL_RISK_DELETE',
                disabled: true,
                handler: function () {
                    me.delFun();
                }
            },
                {
                    btype: 'op',
                    authority:'ROLE_ALL_RISK_EXPORT',
                    tooltip: '导出数据到excel',
                    text: '导出',
                    iconCls: 'icon-ibm-action-export-to-excel',
                    handler: function () {
                        me.exportChart();
                    }
                }
            ]
        });
        me.callParent(arguments);

        me.on('afterlayout', function () {
            Ext.widget('gridCells').mergeCells(me, [2]);
        });

        //记录发生改变时改变按钮可用状态
        me.on('selectionchange', function () {
            me.setstatus(me);
        });
    },

    // 设置按钮可用状态
    setstatus: function (me) {
        if(me.down("[name='delbutton']")){
            me.down("[name='delbutton']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if(me.down("[name='editbutton']")){
            if (me.getSelectionModel().getSelection().length == 1) {
                me.down("[name='editbutton']").setDisabled(false);
            } else {
                me.down("[name='editbutton']").setDisabled(true);
            }
        }
    },


    /**
     * 添加
     */
    addFun: function () {
        var me = this;
      	var currentNode = me.up('riskstoragemainpanelnew').riskTree.getCurrentNode();
        me.up('riskstoragecardnew').riskRelateForm.resetData(currentNode.data.id);
        me.up('riskstoragecardnew').showRiskRelateForm();
        /**
         * 部门风险事件添加时  主责部门为登录人所在部门  并且不允许修改
         * add by 宋佳
         */
        if(me.schm == 'dept'){
	        me.up('riskstoragecardnew').riskRelateForm.respDeptName.initValue([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
	    	me.up('riskstoragecardnew').riskRelateForm.respDeptName.setValues([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
    		me.up('riskstoragecardnew').riskRelateForm.respDeptName.setReadOnly(true);
        }
    },

    /**
     * 编辑
     */
    editFun: function () {
        var me = this;
        var selection = me.getSelectionModel().getSelection()[0];
        var id = selection.data.id;
        //编辑时，设置导航
        var parentId = selection.data.parentId;
        var name = selection.data.name;
        if (name.length > 33) {
            name = name.substring(0, 30) + "...";
        }
        //切换到风险事件添加card
        me.up('riskstoragecardnew').riskRelateForm.reloadData(id);
        me.up('riskstoragecardnew').showRiskRelateForm();
        /**
         * 部门风险事件添加时  主责部门为登录人所在部门  并且不允许修改
         * add by 宋佳
         */
        if(me.schm == 'dept'){
	        me.up('riskstoragecardnew').riskRelateForm.respDeptName.initValue([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
	    	me.up('riskstoragecardnew').riskRelateForm.respDeptName.setValues([{"deptid" : __user.majorDeptId,"deptno" : __user.majorDeptNo,"deptname" : __user.majorDeptName,"empid" : ""}]);
    		me.up('riskstoragecardnew').riskRelateForm.respDeptName.setReadOnly(true);
        }
    },

    /**
     * 删除
     */
    delFun: function () {
        var me = this;
        var deleteCheckUrl = '/risk/risk/findRiskCanBeRemoved.f';
        var delUrl = '/risk/risk/removeRiskById.f';
        var selections = me.getSelectionModel().getSelection();
        if (selections.length > 0) {
            //1.判断是否可以删除风险，如果有叶子节点和风险已经别打分，将不能进行删除
            var canBeRemoved = true;
            for(var i=0;i<selections.length;i++){
                var id = selections[i].data.id;
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
                            }else if(data.type == 'hasRef'){	//hasRef 在其他模块被引用了
                                Ext.MessageBox.show({
                                    title:'操作错误',
                                    msg:'该风险已经被使用，不允许删除!'
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
                                        me.reloadData(null,'risk');
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
    //导出
    exportChart: function () {
        var me = this;
        me.headerDatas = [];
        var items = me.columns;
        Ext.each(items, function (item) {
            if (!item.hidden && item.dataIndex != '') {
                var value = {};
                value['dataIndex'] = item.dataIndex;
                value['text'] = item.text;
                if(item.dataIndex=='assessementStatus'){
                    value['text'] = "状态";
                }else if(item.dataIndex=='etrend'){
                    value['text'] = "趋势";
                }
                me.headerDatas.push(value);
            }
        });
        window.location.href = __ctxPath + "/cmp/risk/exportriskgrid.f?id=" + me.currentId + "&type=" + me.type + "&exportFileName=" + "" +
            "&sheetName=" + "" + "&headerData=" + Ext.encode(me.headerDatas) + "&style=" + "event";
    }
    

});