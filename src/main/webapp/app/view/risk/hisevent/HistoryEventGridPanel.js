/**
 *
 * 历史事件 
 * type : 历史类型
 * 默认为风险：risk，指标：kpi，记分卡：sc，流程：process
 */
Ext.define('FHD.view.risk.hisevent.HistoryEventGridPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.historyeventgridpanel',
    
    queryUrl: '/historyevent/findhistoryeventbytype.f',
    currentId: '',
    type: 'risk',//risk关联风险的历史事件，dept为部门历史事件，all为全部历史事件按状态查询
    navHeight: 22,
    archiveStatus : 'saved',
    navData : null,//导航条信息
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        var cols = [{
            header: "id",
            dataIndex: 'id',
            invisible: true
        }, {
            header: "历史事件",
            dataIndex: 'hisname',
            sortable: false,
            flex: 1.2,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['id'].split('_')[0];
                var name = value;
                if (name != null && name != undefined && name.length > 33) {
                    name = name.substring(0, 30) + "...";
                }
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showHistoryEventDetailContainer('" + id + "','" + name + "')\" >" + value + "</a>";
            }
        },{
                dataIndex: 'riskname',
                header: '风险名称',
                sortable: false,
                flex: .8,
                hidden : false,
                scope: me,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="' + value + '"';
                	return value;
	            }
            },{
            header: "发生日期",
            dataIndex: 'occurDate',
            sortable: false,
            flex: 0.3,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }, {
            header: "损失金额(万元)",
            dataIndex: 'lostAmount',
            sortable: false,
            flex: 0.4,
            align : 'right',
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return '<font color="green">'+value+'</font>';
            }
        }, {
            header: "状态",
            dataIndex: 'status',
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
        if(me.type == 'risk'){
	        for(var i = 0 ; i < cols.length; i++){
	        	if(cols[i].dataIndex == 'riskname'){
	        		cols[i].hidden = true;
	        	}
	        }
        }
        var btns = [{
        		authority:'ROLE_ALL_HISTORYEVENT_ADD',
                btype: 'add',
                name: 'addbutton',
                handler: function () {
                    me.editFun(true);
                }
            }, {
            	authority:'ROLE_ALL_HISTORYEVENT_EDIT',
                btype: 'edit',
                disabled: true,
                name: 'editbutton',
                handler: function () {
                    me.editFun(false);
                }
            }, {
            	authority:'ROLE_ALL_HISTORYEVENT_DELETE',
                btype: 'delete',
                disabled: true,
                name: 'deletebutton',
                handler: function () {
                    me.deleteFun(false);
                }
            }
        ];
        me.grid = Ext.create('FHD.ux.GridPanel',{
        	cols: cols,
            tbarItems: btns,
            border: false,
            checked: true,
            pagable: true,
            rowlines: true,
            columnLines: true
        });
        me.grid.on('selectionchange', function () {
            me.setstatus(me)
        });
        
        Ext.apply(me, {
        	items : me.grid
        });
        me.callParent(arguments);
    },
    reloadData: function (id) {
        var me = this;
        me.reRightLayout(me.grid);
        if(me.down("[name='editbutton']")){
        	me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='deletebutton']")){
        	me.down("[name='deletebutton']").setDisabled(true);
        }
        
        if (id != null && id != '') {
            me.currentId = id;
        }
        me.grid.store.proxy.url = __ctxPath + me.queryUrl;
        me.grid.store.proxy.extraParams.id = me.currentId;
        me.grid.store.proxy.extraParams.type = me.type;
		me.grid.store.proxy.extraParams.schm = me.typeId;
        me.grid.store.load();
    },
    initParams: function (type) {
        var me = this;
        if(type != null ){
	        me.type = type;
        }
    },
    //编辑方法
    editFun: function (isAdd) {
        var me = this;
        var selections = me.grid.getSelectionModel().getSelection();
        if (isAdd) {
            me.showHistoryEventAddContainer(null, '添加历史事件', me.currentId);
        } else {
	        var hisid = selections[0].get('id');
	        var name = selections[0].get('hisname');
            if (name != null && name != undefined && name.length > 33) {
                name = name.substring(0, 30) + "...";
            }
            me.showHistoryEventAddContainer(hisid.split('_')[0], name, hisid.split('_')[1]);
        }
    },
    // 设置按钮可用状态
    setstatus: function (me) {
    	me.down("[name='editbutton']").setDisabled(me.grid.getSelectionModel().getSelection().length != 1);
    	me.down("[name='deletebutton']").setDisabled(me.grid.getSelectionModel().getSelection().length == 0);
    },
    //风险事件编辑
    showHistoryEventAddContainer: function (id, name, relationId) {
        var me = this;
        if (!me.historyEventAddForm) {
            // 风险事件基本信息
            me.historyEventAddForm = Ext.create('FHD.view.risk.hisevent.HistoryEventAddForm', {
            	archiveStatus : me.archiveStatus,
            	type : me.type,
				typeId:me.typeId,//分库标志
                border: false,
                goback: function () {
                	me.go();
                }
            });
            me.add(me.historyEventAddForm);
        }
        me.reRightLayout(me.historyEventAddForm);
        if(me.navData){
	        var data = [];
			for(i=0;i<me.navData.length;i++) {
				data.push(
					me.navData[i]
				);
			};
            data.push({
	               type: 'historyedit',
	               id: 'historyedit',
	               name: name
	        });
	        me.reLayoutNavigationBar(data);
        }
        if (id) {
            me.historyEventAddForm.reloadData(id);
        } else {
            me.historyEventAddForm.resetData(me.currentId);
        }
    },
    //风险事件查看
    showHistoryEventDetailContainer: function (id, name) {
        var me = this;
            // 风险事件基本信息
        var historyEventDetailForm = Ext.create('FHD.view.risk.hisevent.HistoryEventDetailForm', {
            border: false,
            showbar: false
        });
        var detailwindow = Ext.create('FHD.ux.Window', {
            title: '历史事件详情',
            maximizable: true,
            modal: true,
            collapsible: true,
            autoScroll: true
        }).show();
        historyEventDetailForm.reloadData(id);
        detailwindow.add(historyEventDetailForm);
    },
    
    deleteFun: function () {
        var me = this;
        var selections = me.grid.getSelectionModel().getSelection();
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    var hisid = selections[0].get('id').split('_')[0];
                    FHD.ajax({ //ajax调用
                        url: __ctxPath + '/historyevent/deletehistoryevent.f',
                        params: {
			                hisid: hisid
			            },
                        callback: function (data) {
                            if (data) { //删除成功！
                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),
                                    				 FHD.locale.get('fhd.common.prompt'));
                                me.reloadData();
                            }
                        }
                    });
                }
            }
        });
    },
    
    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    },
	go : function(){
		var me = this;
		me.reloadData();
		if(me.navData){
			me.reLayoutNavigationBar(me.navData);
		}
	},
	// 刷新导航方法
	reLayoutNavigationBar: function(data){}
});