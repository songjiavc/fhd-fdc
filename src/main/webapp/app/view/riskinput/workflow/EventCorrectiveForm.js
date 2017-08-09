Ext.define('FHD.view.riskinput.workflow.EventCorrectiveForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.eventcorrectiveform',
	requires: [
    	'FHD.view.risk.responseplan.ResponseMeasureList'
    ],
	border : false,
	autoScroll:true,
//    layout: {
//        align: 'stretch',
//        type: 'vbox'
//    },
//	layout:{
//		type : 'column'
//	},
    defaults : {
        columnWidth : 1 / 1
    },
	bodyPadding:'0 3 3 3',
	
	initComponent: function(){
		var me = this;
		
		me.bbar=[
			'->',
			{
        		iconCls : 'icon-control-play-blue',
        		text:'保存',
        		handler: function(){
//               	var riskeventeditcardpanel = me.up('riskeventeditcardpanel');
//		    		riskeventeditcardpanel.reloadData();
//    				riskeventeditcardpanel.navBtnHandler(0);
//    				me.close();
        			var riskeventeditcardpanel = me.up('riskeventeditcardpanel');
        			riskeventeditcardpanel.reloadData();
        			var schemeform = Ext.widget('schemeform');
        			riskeventeditcardpanel.setActiveItem(schemeform);
        			
                }
        	},
        	'-',
        	{
        		iconCls : 'icon-control-fastforward-blue',
        		text:'提交',
        		handler: me.onSubmit
       		}
       	];
       	
        me.riskCategory = {	
        	xtype: 'displayfield',
        	fieldLabel: '风险分类',
        	name: 'riskStyle',
        	value: '预算编制风险'
        };
		
		me.effectiveness = {	
			xtype: 'displayfield',
        	fieldLabel: '有效性标准说明',
        	name: 'home_score',
        	value: '编制严重超过了年初设定。'
        };
        
        me.startTime = 	{	
        	xtype: 'displayfield',
        	fieldLabel: '产生时间',
        	name: 'ddd',
        	value: '2013-7-1'
        };
        
        me.basicInfoFieldset = Ext.widget('fieldset',{
			collapsed : false,
            collapsible : true,
            defaults: {
                columnWidth: 1/2
            },
            layout: {
                type: 'column'
            },
            columnWidth: 1,
            title: '基本信息',
            items: [me.riskCategory, me.effectiveness, me.startTime]
    	});
        
    	me.eventGrid = Ext.create('FHD.ux.GridPanel',{
    		height:150,
    		url: __ctxPath + '',
    		extraParams:{
    			assessplanId:''
    		},
    		columnWidth: 1,
    		checked:false,
    		pagable:false,
    		searchable:false,
    		cols:[{
    			header:'事件名称',dataIndex:'eventName',flex:1
    		},{
    			header:'事件描述',dataIndex:'eventDesc',flex:1
    		},
    		{
    			header:'发生时间',dataIndex:'startTime',flex:1
    		},
    		{
    			header:'事件上报标准',dataIndex:'standard',flex:1
    		}]
    	});
    	
    	me.eventListFieldset = Ext.widget('fieldset',{
			collapsed : false,
            collapsible : true,
            defaults: {
                margin: '0 0 0 0',
            	labelAlign: 'left'
            },
            layout: {
                type: 'column'
            },
            columnWidth: .5,
            title: '已发生事件',
            items: [me.eventGrid]
    	});
		
    	me.planGrid = Ext.create('FHD.ux.GridPanel',{
    		height:150,
    		url: __ctxPath + '',
    		extraParams:{
    			assessplanId:''
    		},
    		checked:false,
    		pagable:false,
    		columnWidth: 1,
    		searchable:false,
    		cols:[{
    				header:'名称',dataIndex:'measureName',flex:3
    			},{
    				header:'措施描述',dataIndex:'measureDesc',flex:3
    			},{
    				header:'开始执行时间',dataIndex:'startTime',flex:3
    			},{
    				header:'结束时间',dataIndex:'finishTime',flex:3
    			},{
    				header:'责任人',dataIndex:'empName',flex:3
    			}
    		]
    	});
    	
    	me.planListFieldset = Ext.widget('fieldset',{
			collapsed : false,
            collapsible : true,
            defaults: {
                margin: '0 0 0 0',
            	labelAlign: 'left'
            },
            layout: {
                type: 'column'
            },
            columnWidth: .5,
            title: '现有管控措施',
            items: [me.planGrid]
    	});
		
    	me.responsemeasurelist = Ext.widget('responsemeasurelist');
    	
		me.updateMeasureFieldset = Ext.widget('fieldset',{
			collapsed : false,
            collapsible : true,
            defaults: {
                margin: '0 0 0 0',
            	labelAlign: 'left'
            },
            layout: {
                type: 'fit'
            },
            columnWidth: 1,
            title: '整改管控措施',
            items: [me.responsemeasurelist]
    	});
		
       	me.suggest = {
            xtype : 'textareafield',
            fieldLabel:'管理改进建议',
            labelAlign : 'left',
            margin: '20 0 0 0',
            row : 10,
            columnWidth:1,
            name : 'requirement',
            labelWidth : 100
        };
        
        me.suggestFieldset = Ext.widget('fieldset',{
			collapsed : false,
            collapsible : true,
            defaults: {
                margin: '0 0 0 0',
            	labelAlign: 'left'
            },
            layout: {
                type: 'fit'
            },
            columnWidth: 1,
            title: '管理改进建议',
            items: [me.suggest]
    	});
    	
        me.items= [{
			xtype : 'fieldset',
			defaults : {
				columnWidth : 1/2
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			collapsed : false,
//			margin: '0 3 3 3',
			collapsible : false,
			items:[me.basicInfoFieldset, me.eventListFieldset, me.planListFieldset,
				me.suggestFieldset, me.updateMeasureFieldset]
            }];
        
		Ext.applyIf(me,{
			items:me.items
		});
		
		me.callParent(arguments);
		
	}
});