Ext.define('FHD.view.SASACdemo.companyReport.HistoryReportForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.historyReportForm',
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
     // 名称
		var name = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '事件名称' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'name',
			maxLength : 255,
			labelWidth : 120,
			columnWidth : .5,
			allowBlank:false
		});
		
		// 编号
		var code = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '编号' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'code',
			maxLength : 255,
			labelWidth : 120,
			columnWidth : .4
		});
		//自动生成机构编号按钮
    	var autoButton = {
            xtype: 'button',
            margin: '7 10 10 10',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
            handler: function(){
            	
            },
            columnWidth: .1
        };
		
		//发生日期
		var occurDateStr = Ext.widget('datefield',{
			fieldLabel : '发生日期',
		    name: 'occurDateStr',
		    format: 'Y-m-d',
		    margin : '7 30 3 30',
		    labelWidth : 120,
		    columnWidth : .5
		});
		
		// 损失金额
		var lostAmount = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '损失金额（万元）',
			name : 'lostAmount',
			labelWidth : 120,
			margin : '7 30 3 30',
			columnWidth : .5
		});
		
		// 处理状态
		var dealStatusDict = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            multiSelect: false,
            name: 'dealStatusDict',
            dictTypeId: '0deal_status',
            fieldLabel: '处理状态', 
            columnWidth: .5,
            labelWidth : 120,
            margin : '7 30 3 30',
            labelAlign: 'left'
        });
		
		// 事件等级
		var eventLevelDict = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            multiSelect: false,
            name: 'eventLevelDict',
            dictTypeId: 'ic_processure_importance',
            fieldLabel: '事件等级', 
            columnWidth: .5,
            margin : '7 30 3 30',
            labelWidth : 120,
            labelAlign: 'left'
        });
        
        // 责任部门
        me.eventOccuredOrgStr = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门',
			labelAlign : 'left',
			type : 'dept',
			subCompany : true,
			multiSelect : false,
			margin : '7 30 3 30',
			name : 'eventOccuredOrgStr',
			labelWidth : 115,
			columnWidth : .5
		});
		
		// 责任认定
		var eventOccuredObject = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '责任认定',
			margin : '7 30 3 30',
			name : 'eventOccuredObject',
			labelWidth : 120,
			height : 120,
			columnWidth : .5
		});
        
		// 影响
		var effect = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '影响',
			margin : '7 30 3 30',
			name : 'effect',
			labelWidth : 120,
			height : 120,
			columnWidth : .5
		});
		
		// 发生地点
		var occurePlace = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '发生地点',
			margin : '7 30 3 30',
			name : 'occurePlace',
			labelWidth : 120,
			maxLength : 255,
			columnWidth : .5
		});
		
		//  备注
		var comment = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '备注',
			margin : '7 30 3 30',
			name : 'comment',
			labelWidth : 120,
			height : 120,
			columnWidth : .5
		});
		//描述
		var hisdesc = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '描述',
			margin : '7 30 3 30',
			name : 'hisdesc',
			labelWidth : 120,
			height : 120,
			columnWidth : .5
		});

		me.basicfieldSet = Ext.widget('fieldset', {
			title : FHD.locale.get('fhd.common.baseInfo'),
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			layout : {
				type : 'column'
			},
			items : [name,code,autoButton,
						eventLevelDict,dealStatusDict,
						occurDateStr,occurePlace,
						lostAmount,me.eventOccuredOrgStr,
						effect,hisdesc,eventOccuredObject,
						comment
					]
		});
      
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.basicfieldSet]
        });

        me.callParent(arguments);
    }

});