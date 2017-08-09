/**
 * 历史事件上报表单
 */
Ext.define('FHD.view.risk.demo.popupnew.HistEventForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.histeventform',
    requires: [
    	'FHD.ux.icm.common.FlowTaskBar'
    ],
	collapsed : false,
	collapsable : false,
    autoScroll : true,
    loadData : function(id){
    	var me = this;
		me.load({
	        url:'',
	        params:{},
	        failure:function(form,action) {
	            alert("加载数据失败");
	        },
	        success:function(form,action){
	        	var formValue = form.getValues();
	        }
	    });
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.flowtaskbar=Ext.widget('panel',{
        	title: "历史事件上报",
            region:'north',
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items:[
	        	Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.历史事件上报',status:'current'},
		    		{index: 2, context:'2.历史事件审批',status:'undo'},
		    		{index: 3, context:'3.历史事件归档',status:'undo'}
		    	]
	    		})
        	]
        });
		//针对风险
		var risk = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			onlyLeaf: false,
			title : '请您选择风险',
			fieldLabel : '针对风险' + '<font color=red>*</font>',
			name : 'risk',
			margin: '7 10 0 30',
			multiSelect: false,
			columnWidth : .5
		});
		//历史事件名称
        var eventName = Ext.widget('textfield', {
            fieldLabel: '历史事件类型'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //方案编号
        var eventCode = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: '历史事件编号' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'eventCode',
            columnWidth: .4
        });
        //自动生成机构编号按钮
    	var autoButton = {
            xtype: 'button',
            margin: '7 10 10 10',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
            handler: function(){
       			FHD.ajax({
	            	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
	            	params: {
	                	nodeId: me.nodeId
                 	},
	                callback: function (data) {
	                 	me.getForm().setValues({'eventCode':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
        //历史事件类型
        var etype = Ext.widget('textfield', {
            fieldLabel: '历史事件类型'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //历史事件等级
        var eventLevel = Ext.widget('combo', {
	        editable: false,
	        multiSelect: false,
	        margin: '7 10 10 30',
	        columnWidth:.5,
	        name: 'eventLevel',
	        fieldLabel:'事件等级' + '<font color=red>*</font>', 
	        labelAlign: 'left',
	        store : [['T', '特大'],['Z', '重大'],['Y', '一般']]
	    });
        //发生地点
        var eventLevel = Ext.widget('textfield', {
            fieldLabel: '发生地点'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //动因
        var cause = Ext.widget('textfield', {
            fieldLabel: '动因'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //日期
		var occurDate = {
			xtype: 'datefield',
			margin: '7 10 10 30',
			fieldLabel : '发生时间' + '<font color=red>*</font>',
		    name: 'beginDataStr',
		    format: 'Y-m-d',
		    columnWidth: .5
		};
		//发生地点
        var occurePlace = Ext.widget('textfield', {
            fieldLabel: '发生地点'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
		//处理状态
        var dealStatus = Ext.widget('combo', {
	        editable: false,
	        multiSelect: false,
	        margin: '7 10 10 30',
	        columnWidth:.5,
	        name: 'statusId',
	        fieldLabel:'处理状态' + '<font color=red>*</font>', 
	        labelAlign: 'left',
	        store : [['U', '待更新'],['O', '已更新']]
	    });
        //状态
        var estatus = Ext.widget('combo', {
	        editable: false,
	        multiSelect: false,
	        margin: '7 10 10 30',
	        columnWidth:.5,
	        name: 'statusId',
	        fieldLabel:'状态' + '<font color=red>*</font>', 
	        labelAlign: 'left',
	        store : [['U', '处理中'],['O', '已完成']]
	    });
        //备注
        var ecomment = Ext.widget('textareafield', {
            fieldLabel: '事件发生过程' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'ecomment',
            columnWidth: .5
        });
        //人员伤亡数-死亡
        var empDeadNum = Ext.widget('textfield', {
            fieldLabel: '人员伤亡数-死亡'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //人员伤亡数-轻伤
        var empLightHurtNum = Ext.widget('textfield', {
            fieldLabel: '人员伤亡数-轻伤'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //人员伤亡数-重伤
        var empSeriouslyHurtNum = Ext.widget('textfield', {
            fieldLabel: '人员伤亡数-重伤'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //事件前状态
        var eventOccuredBeforeStatus = Ext.widget('textfield', {
            fieldLabel: '事件前状态'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //责任认定
        var eventOccuredObject = Ext.widget('textfield', {
            fieldLabel: '责任认定'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //事件发生原因分析
        var eventOccuredReason = Ext.widget('textareafield', {
            fieldLabel: '事件发生原因分析' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'ecomment',
            columnWidth: .5
        });
        //事件发生过程
        var eventOccuredStory = Ext.widget('textareafield', {
            fieldLabel: '事件发生过程' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'ecomment',
            columnWidth: .5
        });
        //财务损失金额
        var financeLostAmount = Ext.widget('textfield', {
            fieldLabel: '财务损失金额'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //财务损失说明
        var financeLostDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '财务损失说明' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'ecomment',
            columnWidth: .5
        });
        //运营影响
        var operationEffect = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '运营影响' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'operationEffect',
            columnWidth: .5
        });        
        //设备影响
        var equipmentEffect = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '设备影响' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'equipmentEffect',
            columnWidth: .5
        });        
        //声誉影响
        var reputationEffect = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '声誉影响' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'reputationEffect',
            columnWidth: .5
        });
         //上报部门
		var reportOrgId = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '上报部门' + '<font color=red>*</font>',
			name:'reportOrgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 10 30',
            columnWidth: .5
		});
		//责任主体
		var responseDetermin = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任主体' + '<font color=red>*</font>',
			name:'responseDetermin',
			type : 'dept',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 10 30',
            columnWidth: .5
		});
		//基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '基本信息',
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [
				eventName,eventCode,autoButton,risk,eventLevel
			]
        };
        //事件描述fieldset
        var descInfoFieldset = {
            xtype:'fieldset',
            title : '事件描述',
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [
				occurDate,occurePlace,
				reportOrgId,responseDetermin,eventOccuredStory,eventOccuredReason
			]
        };
        //事件影响fieldset
        var effectInfoFieldset = {
            xtype:'fieldset',
            title : '事件影响',
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [
				empDeadNum,empLightHurtNum,
				empSeriouslyHurtNum,financeLostAmount,financeLostDesc,operationEffect,equipmentEffect,
				reputationEffect,eventOccuredBeforeStatus,eventOccuredObject
			]
        };
        //事件处理fieldset
        var dealInfoFieldset = {
            xtype:'fieldset',
            title : '事件处理',
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [
				etype,cause,
				ecomment,dealStatus,estatus
			]
        };
		me.bbar = {
	        items: [
		        '->',
		       	{
	                name: 'icm_defect_undo_btn' ,
		            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
		            iconCls: 'icon-operator-home',
	            	handler: function () {
	           			return true;
	            	}
	            },
		        {
		            text: FHD.locale.get("fhd.common.submit"),//提交按钮
		            iconCls: 'icon-operator-submit',
		            id: 'icm_standard_submit',
		            handler: function () {
		            	
		            }
		        }
		    ]
	    };
        
        Ext.apply(me, {
        	border:false,
            items : [me.flowtaskbar, basicInfoFieldset, descInfoFieldset, effectInfoFieldset ,dealInfoFieldset]
        });

       me.callParent(arguments);
    }
});