Ext.define('FHD.view.risk.planconform.PlanConformEditNext', {
    extend: 'Ext.form.Panel',
    alias: 'widget.planConformEditNext',
    requires: [],
    loadData: function (id, url) {
        var me = this;
        me.planId = id;
        me.fieldSet3.removeAll();    
        me.downPanel = Ext.create(url, {
        	schm:me.schm,
        	executionId: me.executionId,
			businessId: me.businessId,
			winId: me.winId,
            flex: 1,
            margin: 2,
            columnWidth: 1
        });

        // 如果最下面的面板存在自定义标题 则修改标题
        if (me.downPanel.hasUniqueTitle) {
            me.fieldSet3.setTitle(me.downPanel.hasUniqueTitle);
        } else {
            me.fieldSet3.setTitle('风险事件选择');
        }
        me.fieldSet3.add(me.downPanel);
        me.downPanel.loadData(id);
        if (me.rendered) {
            me.downPanel.setHeight(me.getHeight() - 100);
        } else { //没渲染，得不到me.height
            me.downPanel.setHeight(100);
        }

        // 如果存在考核时间 则显示考核范围选择FieldSet 反之隐藏
        if (me.downPanel.hasDateRange) {
            me.assessKpiTimeStart.setValue(me.downPanel.startTime);
            if (me.downPanel.checkType && '0checktype_new' != me.downPanel.checkType) {
                me.checkFre.setValue(me.downPanel.checkType);
                me.assessKpiTimeStart.setVisible(true);
                me.displayTime.setVisible(true);
                me.displayDate(me.downPanel.startTime,me.checkFre.getValue());
            } else {
                me.checkFre.setValue('0checktype_new');
                me.assessKpiTimeStart.setVisible(false);
                me.assessKpiTimeStart.setValue('');
                me.displayTime.setVisible(false);
                me.displayTime.setValue('');
            }

            me.fieldSet2.setVisible(true);
        } else {
            me.fieldSet2.setVisible(false);
        }
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        me.fieldSet = {
            xtype: 'fieldset',
            title: '基础信息',
            collapsible: true,
            collapsed: true, //初始化收缩
            margin: '5 5 5 5',
            defaults: {
                columnWidth: 1 / 2,
                margin: '7 30 7 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            listeners: {
                expand: function () {
                    if (me.downPanel) {
                        me.downPanel.setHeight(me.getHeight() - 180);
                    }
                },
                collapse: function () {
                    if (me.downPanel) {
                        me.downPanel.setHeight(me.getHeight() - 100);
                    }
                }
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: '计划名称',
                name: 'pc_planName'
            }, {
                xtype: 'displayfield',
                fieldLabel: '起止日期',
                name: 'pc_date'
            }, {
                xtype: 'displayfield',
                fieldLabel: '联系人',
                name: 'pc_contactor'
            }, {
                xtype: 'displayfield',
                fieldLabel: '负责人',
                name: 'pc_responser'
            }]
        };
        //风险事件选择列表
        /*me.downPanel = Ext.create('FHD.view.risk.planconform.PlanConformEditNextGrid',{
        					flex: 1,
        					margin: 2,
        					columnWidth: 1});*/
        // 考核类型comboBox
        var typeStore = Ext.create('Ext.data.Store', { // 机构类型store
            fields: ['id', 'text'],
            data: [{
                    id: '0checktype_new',
                    text: '当期采集'
                }, {
                    id: '0checktype_y',
                    text: '年度采集'
                },
                {
                    id: '0checktype_q',
                    text: '季度采集'
                },
                 {
                    id: '0checktype_m',
                    text: '月度采集'
                }, 
                {
                    id: '0checktype_w',
                    text: '周采集'
                }
            ],
            autoLoad: true
        });
        me.checkFre = Ext.create('Ext.form.field.ComboBox', {
            store: typeStore,
            fieldLabel: '采集周期',
            emptyText: FHD.locale.get('fhd.common.pleaseSelect'),
            editable: false,
            queryMode: 'local',
            name: 'checkFre',
            displayField: 'text',
            valueField: 'id',
            triggerAction: 'all',
            value: '0checktype_new',
            allowBlank: false,
            columnWidth: .33,
            listeners: {
                select: function (c, r, o) {
                    if (r[0].data.id == '0checktype_new') {
                        me.assessKpiTimeStart.setVisible(false);
                        me.displayTime.setVisible(false);                     
                    } else {
                        me.assessKpiTimeStart.setVisible(true);
                        me.displayTime.setVisible(true);
                    }
                    me.displayTime.setValue('');
                    me.assessKpiTimeStart.setValue('');
                }
            }
        });
        //开始时间
        me.assessKpiTimeStart = Ext.widget('datefield', {
            name: 'checkBeginData',
            columnWidth: .33,
            format: "Y-m-d",
            fieldLabel: '开始时间',
            labelAlign: 'left',
            emptyText: ''
        });
        me.assessKpiTimeStart.on('select',function(field,value,eopts) {
        	me.displayDate(value,me.checkFre.getValue());
        }
        );
        me.displayTime = Ext.widget('displayfield', {
            xtype: 'displayfield',
            name: 'timeDisPlay',
            fieldLabel: '采集区间', //名称
            value: '',
            columnWidth: .33,
            allowBlank: false
        });
        me.fieldSet2 = Ext.create('Ext.form.FieldSet', {
            title: '采集时间选择',
            collapsible: true,
            margin: '5 5 5 5',
            defaults: {
                columnWidth: 1 / 2,
                margin: '7 30 7 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            hidden: true,
            items: [me.checkFre, me.assessKpiTimeStart,me.displayTime]
        });
        me.fieldSet3 = Ext.create('Ext.form.FieldSet', {
            layout: {
                type: 'column'
            },
            title: '风险事件选择',
            collapsible: true,
            margin: '5 5 5 5',
            items: []
        });

        Ext.apply(me, {
            autoScroll: false,
            border: false,
            items: [me.fieldSet, me.fieldSet2, me.fieldSet3]
        });

        me.callParent(arguments);
        me.on('resize', function (p) {
            if (me.downPanel) {
                me.downPanel.setHeight(me.getHeight() - 100);
            }
        });
    },
    last: function () {
        var me = this;
        var startTime = me.assessKpiTimeStart.getValue();
        // 如果考核时间fieldSet展示 
        if (!me.fieldSet2.isHidden()) {

            if (me.validateDate()) {
                if (startTime) {
                    startTime = Ext.Date.format(new Date(startTime), 'Y-m-d');
                }

                FHD.ajax({
                    url: __ctxPath + '/access/planconform/saveplanbycheckTime.f',
                    async: false,
                    params: {
                        startTime: startTime,
                        checkType: me.checkFre.getValue(),
                        planId: me.planId
                    },
                    callback: function (data) {}
                })
            } else {
                return false;
            }
        }
    },
    validateDate: function () {
        var me = this;
        var startTime = me.assessKpiTimeStart.getValue();
        if ('0checktype_new' != me.checkFre.getValue() && !startTime) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '采集日期不能为空！');
            return false;
        }
        return true;
    },
    displayDate: function(date,type) {
    	var me = this;
    	if(date){
    		var mI = new Date(date).getMonth();
    		var yI = new Date(date).getFullYear();
    		if('0checktype_w' == type) {
    			var wI = me.getYearWeek(date);
    		   	me.displayTime.setValue(yI + '年' + wI +'周');
    		}else if('0checktype_y' == type){
    			me.displayTime.setValue(yI + '年');
    		}else if('0checktype_m' == type){
    			mI++;
    			me.displayTime.setValue(yI + '年' + mI + '月');
    		}else if('0checktype_q' == type) {
    	      var qI;
		      if (mI < 3) {
	                qI = 1;
	            } else if (mI < 6) {
	               qI = 2;
	            } else if (mI < 9) {
	                qI = 3;
	            } else if (mI < 12) {
	                qI = 4;
	            }
    			me.displayTime.setValue(yI + '年' + '第' + qI + '季度');
    		}
    	}else {
    	   me.displayTime.setValue('');
    	}    	
    },
    getYearWeek:function(date){  
    	if(date){
    		date = new Date(date);
    	    var date2=new Date(date.getFullYear(), 0, 1);  
		    var day1=date.getDay();  
		    if(day1==0) day1=7;  
             var day2=date2.getDay();  
		    if(day2==0) day2=7;  
		    d = Math.round((date.getTime() - date2.getTime()+(day2-day1)*(24*60*60*1000)) / 86400000);    
		    return Math.ceil(d /7)+1;  
    	}
 
    }     
});