Ext.define('FHD.view.risk.hisevent.HistoryEventDetailForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.historyeventdetailform',

    /**
     * 常量
     */
    detailUrl: '/historyevent/findhistoryinfo.f',
    
    goback: function(){},
    showbar : true,
    
    initComponent: function () {
        var me = this;
        me.riskdetailform = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail');
        
    	me.addBasicComponent();
    	
        var returnBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
            iconCls: 'icon-operator-home',
            handler: function () {
    			me.goback();
            }
        });
        
        if(me.showbar){
	        me.tbar = ['->',returnBtn];
        }
    	
    	Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [me.riskdetailform,me.basicfieldSet]
		});
        me.callParent(arguments);
    },
    
    reloadData: function (id) {	//id是风险事件id
    	var me = this;
    	
    	me.form.load({
   			params: {
                id: id,
                type : 'detail'
            },
            url: __ctxPath + me.detailUrl,
            success: function (form,action) {
            	if(action.result.data.relation != ''){
            		me.riskdetailform.setVisible(true);
            		me.riskdetailform.reloadData(action.result.data.relation);
            	}else{
	            	me.riskdetailform.setVisible(false);
            	}
            	
            	
            }
        });
    },
    
    /**
     * 添加基本信息
     */
    addBasicComponent: function () {
        var me = this;

        //名称
        var hisname = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '名称',
            name: 'hisname',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //编号
        var hiscode = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '编号',
            name: 'hiscode',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //发生日期
        var occurDateStr = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '发生日期',
            name: 'occurDateStr',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //损失金额
        var lostAmount = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '损失金额（万元）',
            name: 'lostAmount',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //处理状态
        var dealStatusDict = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '处理状态',
            name: 'dealStatusDict',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //事件等级
        var eventLevelDict = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '事件等级',
            name: 'eventLevelDict',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //责任部门
        var eventOccuredOrgStr = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '责任部门',
            name: 'eventOccuredOrgStr',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //责任认定
        var eventOccuredObject = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '责任认定',
            name: 'eventOccuredObject',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //影响
        var effect = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '影响',
            name: 'effect',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //发生地点
        var occurePlace = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '发生地点',
            name: 'occurePlace',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //备注
        var comment = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '备注',
            name: 'comment',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        //描述
        var hisdesc = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '描述	',
            name: 'hisdesc',
            margin : '7 30 3 30',
            labelWidth : 120,
            columnWidth: .5
        });
        
        me.basicfieldSet = Ext.widget('fieldset', {
			title : '应急预案信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : [hisname,hiscode,
						eventLevelDict,dealStatusDict,
						occurDateStr,occurePlace,
						lostAmount,eventOccuredOrgStr,
						hisdesc,eventOccuredObject,
						effect,comment
					]
		});
    }
});