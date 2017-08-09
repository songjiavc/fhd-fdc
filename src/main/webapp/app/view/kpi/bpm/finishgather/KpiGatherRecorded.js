Ext.define('FHD.view.kpi.bpm.finishgather.KpiGatherRecorded', {
    extend: 'Ext.panel.Panel',
    border: false,

    initComponent: function () {
        var me = this;
        var items = {};
        items.executionId = me.executionId;
        items.businessId = me.businessId;
        me.extraParams = {
            items: Ext.JSON.encode(items)
        }
        //创建流程导航
        if (!me.bpmtBar) {
            me.jsonArray = [{
                index: 1,
                context: '1.实际值采集',
                status: 'current'
            }, {
                index: 2,
                context: '2.采集审批',
                status: 'undo'
            }]
            me.bpmtBar = Ext.create('FHD.ux.icm.common.FlowTaskBar', {
                jsonArray: me.jsonArray
            });
        }
        me.bpmSet = Ext.widget('panel', {
            collapsible: true,
            collapsed: true,
            layout: {
                type: 'fit'
            },
            border: false,
            title: "实际值采集数据收集",
            items: [me.bpmtBar]
        })
        // 实际值採集頁面
        if (!me.resultInput) {
            me.resultInput = Ext.create('FHD.view.kpi.bpm.finishgather.KpiFinishResultInput', {
                layout: 'fit',
                extraParams: me.extraParams,
                executionId: me.executionId,
                pcontainer: me,
                isEdit: true
            });
        }
        me.bpmStart = Ext.create('Ext.button.Button', {
            text: '提交', //提交按钮
            iconCls: 'icon-control-stop-blue',
            handler: function () {

                me.submitBpm();
            }
        });
        me.finishBottom = Ext.create('Ext.button.Button', {
            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"), //保存按钮
            iconCls: 'icon-control-stop-blue',
            handler: function () {
            	if(me.resultInput.last(true)) {
            		 me.undo();
            	}                              
            }
        });
        me.kpiGatherPanel = Ext.create('Ext.panel.Panel', {
            flex: 1,
            items: [me.resultInput],
            undo: function () {
                me.undo();
            },
            layout: 'fit',
            bpmStart: false,
            bbar: ['->', me.finishBottom, '-', me.bpmStart]
        });
        /*me.selector = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: '审批人',
            name: 'approver',
            type: 'emp',
            multiSelect: false,
            margin: '40 10 30 10'

        });*/
        
        me.selector = Ext.create('Ext.ux.form.OrgEmpSelect',{
			type: 'emp',
			fieldLabel : '审批人',
			multiSelect: false,
            margin: '40 10 30 30',
            width:500,
			labelAlign: 'left',
			labelWidth: 80,
			growMin: 75,
			growMax: 120,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'approver',
			value:''
		});
		
        me.selectSet = Ext.create('Ext.form.FieldSet', {
            collapsible: true,
            collapsed: false,
            defaults: {
                margin: '0 0 0 0'
            },
            layout: {
                type: 'fit'
            },
            title: "审批人",
            items: [me.selector]
        })
        Ext.apply(me, {
            layout: {
                align: 'stretch',
                type: 'vbox'
            },
            items: [me.bpmSet, me.kpiGatherPanel]
        })

        me.callParent(arguments);
    },
    undo: function () {
        var me = this;
        if (me.winId) {
            Ext.getCmp(me.winId).close();
        }
    },
    reloadData: function () {
        var me = this;
    },
    submitBpm: function () {
        var me = this;
        me.approver = Ext.create('Ext.form.Panel',{
            autoScroll:false,
        	border:false,
            items : [me.selector]
        });
        me.subWin = Ext.create('FHD.ux.Window', {
            title: '选择审批人',
   		 	height: 200,
    		width: 600,
    		closeAction: 'hide',
    		layout: {
     	        type: 'fit'
     	    },
            buttonAlign: 'center',
            items: [me.approver],
            fbar: [{
                xtype: 'button',
                text: '确定',
                handler: function () {
                    Ext.MessageBox.show({
                        title: '提示',
                        width: 260,
                        msg: '确认提交吗？',
                        buttons: Ext.MessageBox.YESNO,
                        icon: Ext.MessageBox.QUESTION,
                        fn: function (btn) {
                            if (btn == 'yes') {
                                if (me.selector.getValue() == null || me.selector.getValue() == '') {
                                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择审批人！');
                                } else {
                                	if(me.winId){
                                		me.subWin.hide();
                                	}else{
                                		FHD.closeWindow();
                                	}
                                    me.kpiGatherPanel.bpmStart = true;
                                    me.resultInput.last(false);
                                    me.undo();
                                }
                            }
                        }
                    })

                }
            }, {
                xtype: 'button',
                text: '取消',
                handler: function () {
                    me.subWin.hide();
                    me.selector.clearValues();
                }
            }]
        }).show();
    }
});