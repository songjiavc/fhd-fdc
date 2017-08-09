Ext.define('FHD.view.sys.import.RiskFromExcelEditForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskfromexceleditform',
	requires: [
	],
	isWindow: false,
    
	/**
	 * private 属性
	 */
	
	findTemplateUrl: '/cmp/risk/findTemplateList',
	saveUrl: '/dataimport/risk/updateRiskFromExcel.f',
	findUrl: '/dataimport/risk/findRiskFromExcelById.f',
	
	/**
	 * public 保存
	 */
	submit : function() {
		var me = this.up('riskfromexceleditform');
		var form = me.getForm();
		if (form.isValid()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				callback : function(data) {
					Ext.Msg.alert('提示','保存成功！');
					me.closeWin()
				}
			});
		}else{
			Ext.Msg.alert('提示','必填项不能为空！');
		}
	},
	closeWin: function(){
    	var me = this;
		Ext.getCmp(me.winId).close();
//		var riskfromexcellist = Ext.create('riskfromexcellist');
//		riskfromexcellist.reloadData();
    },
	addBasicComponent : function() {
		var me = this;
		
		var itemArr = [];	//显示的数据项
		
		// id
		var id = Ext.widget('textfield', {
			xtype : 'textfield',
			name : 'id',
			hidden: true
		});
		itemArr.push(id);
		
		// esort
		var esort = Ext.widget('textfield', {
			xtype : 'textfield',
			name : 'esort',
			hidden: true
		});
		itemArr.push(esort);
		
		// parent编码
		var parentCode = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '上级风险编号',
			margin : '7 30 3 30',
			name : 'parentCode',
			maxLength : 255,
			columnWidth : .5,
			onChange :function(nValue,oValue,field){
				var code = nValue;
    			FHD.ajax({
			        url: __ctxPath + '/dataimport/risk/returnRiskNameByCode.f',
			        async:false,
			        params: {
			        	code: code
			        },
			        callback: function (data) {
			        	if(data){
			        		me.getForm().setValues({parentName:data.parentName});//给parentName表单赋值
			        	}
			        }
		    	});
        	}
		});
		itemArr.push(parentCode);
		
		// parent风险名称
		var parentName = Ext.widget('textareafield', {
			rows : 2,
			fieldLabel : '上级风险名称',
			margin : '7 30 3 30',
			name : 'parentName',
			height : 40,
			disabled:true,
			columnWidth : .5
		});
		itemArr.push(parentName);
		
		// 编码
		var code = Ext.widget('textfield', {
			fieldLabel : '风险编号' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'code',
			maxLength : 255,
			columnWidth : .5,
			allowBlank:false
		});
		itemArr.push(code);
		
		// 风险名称
		var name = Ext.widget('textareafield', {
			rows : 2,
			fieldLabel : '风险名称' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'name',
			allowBlank : false,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(name);

		// 风险描述
		var desc = Ext.widget('textareafield', {
			rows : 2,
			fieldLabel : '风险描述',
			margin : '7 30 3 30',
			name : 'desc',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(desc);

		// 主责部门
		me.respDeptName = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '主责部门',
			labelAlign : 'left',
			type : 'dept',
			subCompany : false,
			multiSelect : true,
			margin : '7 30 3 30',
			name : 'respOrgs',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(me.respDeptName);

		// 相关部门
		me.relaDeptName = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '相关部门',// + '<font color=red>*</font>',
			labelAlign : 'left',
			type : 'dept',
			subCompany : true,
			multiSelect : true,
			margin : '7 30 3 30',
			name : 'relaOrgs',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(me.relaDeptName);
		
		me.basicfieldSet = Ext.widget('fieldset', {
			title : FHD.locale.get('fhd.common.baseInfo'),
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
			items : itemArr
		});
	},
	addRelaComponent : function() {
		var me = this;

		// 影响指标
		me.influKpiName = Ext.create('FHD.ux.kpi.opt.KpiSelector', {
			labelWidth : 100,
			gridHeight : 40,
			btnHeight : 25,
			btnWidth : 22,
			multiSelect : true,
			labelAlign : 'left',
			labelText : '影响指标',// + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'impactTarget',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});

		// 影响流程
		me.influProcessureName = Ext.create('FHD.ux.process.ProcessSelector', {
			labelWidth : 95,
			gridHeight : 25,
			btnHeight : 25,
			btnWidth : 22,
			single : false,
			fieldLabel : '影响流程',// + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'impactProcess',
			allowBlank : true,
			multiSelect : true,
			height : 40,
			columnWidth : .5
		});
		
		me.relafieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '相关信息',
			items : [me.influKpiName, me.influProcessureName]
		});
	},
	addExtendComponent : function() {
		var me = this;

		// 是否继承上级模板
		var isInherit = Ext.create('FHD.ux.dict.DictRadio', {
			name : 'isInherit',
			dictTypeId : '0yn',
			defaultValue : '0yn_y',
			labelAlign : 'left',
			fieldLabel : '是否继承',// 上级模板
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 评估模板
		var templateNameStore = Ext.create('Ext.data.Store', {

			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + me.findTemplateUrl,
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});
		templateNameStore.load();

		me.templateName = Ext.create('Ext.form.ComboBox', {
			name : 'assessmentTemplate',
			store : templateNameStore,
			displayField : 'name',
			valueField : 'id',
			labelAlign : 'left',
			fieldLabel : '评估模板',
			multiSelect : false,
			margin : '7 30 5 30', // emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
			triggerAction : 'all',
			columnWidth : .5
		});


		me.extendfieldSet = Ext.widget('fieldset', {
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '扩展信息',
			items : [ isInherit, me.templateName]
		});
	},
	addAssessmentComponent : function() {
		var me = this;

		// 发生可能性
		var probability = Ext.widget('numberfield', {
			fieldLabel : '发生可能性',
			margin : '7 30 3 30',
			name : 'probability',
			maxLength : 255,
			columnWidth : .25,
			maxValue: 5,
        	minValue: 0
		});
		// 影响程度
		var impact = Ext.widget('numberfield', {
			fieldLabel : '影响程度',
			margin : '7 30 3 30',
			name : 'impact',
			maxLength : 255,
			columnWidth : .25,
			maxValue: 5,
        	minValue: 0
		});
		
		// 管理紧迫性
		var urgency = Ext.widget('numberfield', {
			fieldLabel : '管理紧迫性',
			margin : '7 30 3 30',
			name : 'urgency',
			maxLength : 255,
			columnWidth : .25,
			maxValue: 5,
        	minValue: 0
		});
		
		// 风险水平
		var riskLevel = Ext.widget('numberfield', {
			fieldLabel : '风险水平',
			margin : '7 30 3 30',
			name : 'riskLevel',
			maxLength : 255,
			columnWidth : .25,
			maxValue: 25,
        	minValue: 0
		});
		
		//风险状态（红黄绿）
		var riskStatusStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'01', 'name':'红'},
				{'id':'02', 'name':'黄'},
				{'id':'03', 'name':'绿'}
			]
		});
		
		//风险状态（红黄绿）
		var riskStatus = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '风险状态',
			store : riskStatusStore,
			columnWidth : .25,
			emptyText:'请选择',
			name:'riskStatus',
			displayField : 'name',
			editable : false
		});
		
		me.assessmentfieldSet = Ext.widget('fieldset', {
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '评估信息',
			items : [ probability, impact, urgency, riskLevel, riskStatus]
		});
	},
	initComponent : function() {
		var me = this;

		// 基本信息
		me.addBasicComponent();
		//关联信息
		me.addRelaComponent();
		// 扩展信息
		me.addExtendComponent();
		// 评估信息
		me.addAssessmentComponent();
		
		me.bbar={
           style: 'background-image:url() !important;background-color:rgb(250,250,250);',
           items: ['->',
           	{
      		 	id : 'icm_standard_submit',
	            text: FHD.locale.get("fhd.common.submit"),//提交按钮
	            name: 'icm_standard_submit_btn' ,
	            iconCls: 'icon-operator-submit',
           		handler: me.submit
      		 }]
        };
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [me.basicfieldSet,me.relafieldSet,me.riskAssessOpe,me.extendfieldSet, me.assessmentfieldSet]
		});

		me.callParent(arguments);
	},
    reloadData: function (id) {	//id是风险事件id
    	var me = this;

    	me.load({
           waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
           url: __ctxPath + me.findUrl,
           params: {
               riskFromExcelId: id
           },
           success: function (form, action){
           	me.influKpiName.initGridStore(action.result.data.impactTarget);
           }
        });
        
    }
	
});