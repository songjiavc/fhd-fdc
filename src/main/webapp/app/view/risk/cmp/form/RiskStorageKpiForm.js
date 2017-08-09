Ext.define('FHD.view.risk.cmp.form.RiskStorageKpiForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskstoragekpiform',

	/**
	 * 常量
	 */
	alarmPlanUrl: __ctxPath + "/kpi/alarm/findriskalarmplan.f", //告警方案url
	saveUrl: __ctxPath + "/cmp/risk/saveRiskStorageKpi.f",		  //风险指标保存url
	findUrl: __ctxPath + '/cmp/risk/findRiskEditInfoById',				      //风险编辑赋值请求的url
	/**
	 * 变量
	 */
	riskId:undefined,	//风险id
	
	last:function(){
		var me = this;

		//得到风险指标
		var kpiParam = me.kpiGrid.findKpiStr();

		//提交表单
		var form = me.getForm();
		if (form.isValid()) {
	        //me.body.mask("提交中...","x-mask-loading");
	        
	        // 公式定义
			var formulaDefine = me.formulaDefine.getValue();
	        //采集频率相关字符串
			var valueDictType = me.gatherfrequenceDict.valueDictType;//采集频率
	        var gatherValueCron = me.gatherfrequenceDict.valueCron;//时间采集公式
			var valueDictValue = me.gatherfrequenceDict.getValue();;//采集频率显示内容
	        var valueRadioType = me.gatherfrequenceDict.valueRadioType;//显示内容后缀
	        var gatherfrequenceDict = '';
	        if(valueDictValue != ''){
		        gatherfrequenceDict = valueDictType + '|' + gatherValueCron + '|' + valueDictValue + '|' +  valueRadioType;
	        }
			FHD.submit({
				form : form,
				url : me.saveUrl,
				params : {
					riskId:me.riskId,
					formulaDefine : me.formulaDefine.getValue(),
					gatherfrequenceDict : gatherfrequenceDict,
					kpiParam : kpiParam //风险指标
				},
				callback : function(data) {
	            	//me.body.unmask();
				}
			});
		}else{
			return false;
		}
	},
	
	initComponent : function() {
		var me = this;

		//1.风险指标
		me.kpiGrid  = Ext.create("FHD.view.risk.cmp.form.RiskStorageKpiFormSet",{
			
		});
		me.kpiSet = Ext.widget('fieldset', {
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
				type : 'fit'
			},
			title : '风险指标',
			items : [me.kpiGrid]
		});
		
		//2.计算
        // 计算公式
		me.formulaDefine = Ext.create('FHD.ux.kpi.FormulaTrigger', {
			fieldLabel : "计算公式",
			growMin:20,
			hideLabel : false,
			emptyText : '',
			labelAlign : 'left',
			flex : 1.5,
			labelWidth : 100,
			cols : 20,
			margin : '7 30 3 30',
			name : 'formulaDefine',
			type : 'kpi',
			showType : 'all',
			column : 'assessmentValueFormula',
			columnWidth : .5
		});
		
		// 是否计算
        me.calcStr = Ext.create('FHD.ux.dict.DictRadio', {
            xtype: 'dictradio',
            //height : 40,
            labelWidth: 100,
            margin : '7 30 3 30',
            name: 'calcStr',
            dictTypeId: '0yn',
            fieldLabel: '是否计算',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });
		
		//计算频率
        me.gatherfrequenceDict = Ext.create('FHD.ux.collection.CollectionSelector', {
            name: 'gatherfrequence',
            xtype: 'collectionSelector',
            label: '计算频率', //结果收集频率
            valueDictType: '',
            valueRadioType: '',
            single: false,
            value: '',
            labelWidth: 100,
            margin : '7 30 3 30',
            columnWidth: .5
        });
        
		// 延期天数
		me.resultCollectInterval = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '延期天数',
			name : 'resultCollectInterval',
			margin : '7 30 3 30',
			columnWidth : .5
		});
		
		// 告警方案
		var alarmPlanIdStore = Ext.create('Ext.data.Store', {
			fields : [ 'id', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : me.alarmPlanUrl,
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});
		alarmPlanIdStore.load();
		me.alarmPlanId = Ext.create('Ext.form.ComboBox', {
			name : 'alarmPlanId',
			store : alarmPlanIdStore,
			displayField : 'name',
			valueField : 'id',
			labelAlign : 'left',
			fieldLabel : '告警方案',
			hidden : true,
			multiSelect : false,
			triggerAction : 'all',
			columnWidth : .5
		});
		
		//小数点位数
		me.digit = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '小数点位数',
			name : 'digit',
			margin : '7 30 3 30',
			columnWidth : .5
		});
		
		me.calcSet = Ext.widget('fieldset',{
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
			title : '计算',
			items : [me.alarmPlanId,me.digit,me.gatherfrequenceDict,me.resultCollectInterval,me.formulaDefine,me.calcStr]
		});
		
		Ext.apply(me, {
			autoScroll : true,
			border : false,
			items : [me.kpiSet,me.calcSet]
		});

		me.callParent(arguments);

	},
	
	resetData:function(){
		var me = this;
		me.getForm().reset();
		// 采集频率
		me.gatherfrequenceDict.reset();
		me.gatherfrequenceDict.setValue('');
		
		//风险指标清空
		me.kpiGrid.initParam({riskId:null});
		me.kpiGrid.reloadData();
	},
	
	reloadData:function(riskId){
		var me = this;
		me.riskId = riskId;
		
		//表单清空
		me.resetData();
		
		//风险指标初始化
		me.kpiGrid.initParam({riskId:riskId});
		me.kpiGrid.reloadData();
		
		//表单初始化
		FHD.ajax({
   			async:false,
   			params: {
                riskId: riskId
            },
            url: me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
            		alarmPlanId:json.alarmPlanId,
            		digit:json.digit,
            		resultCollectInterval:json.resultCollectInterval,
        			formulaDefine : json.formulaDefine, //计算公式
        			calcStr:json.calcStr	//是否计算
        		});
        		
        		if (json.gatherfrequenceDictType) {
                    me.gatherfrequenceDict.valueDictType = json.gatherfrequenceDictType;
                }else{
                	me.gatherfrequenceDict.valueDictType = '';
                }
        		if (json.gatherfrequenceCron) {
                    me.gatherfrequenceDict.valueCron = json.gatherfrequenceCron;
                }else{
                	me.gatherfrequenceDict.valueCron = '';
                }
                if (json.gatherfrequence) {
                    me.gatherfrequenceDict.setValue(json.gatherfrequence);
                }else{
                	me.gatherfrequenceDict.setValue('');
                }
                if (json.gatherfrequenceRule) {
                    me.gatherfrequenceDict.valueRadioType = json.gatherfrequenceRule;
                }else{
                	me.gatherfrequenceDict.valueRadioType = '';
                }
            }
        });
	}
});