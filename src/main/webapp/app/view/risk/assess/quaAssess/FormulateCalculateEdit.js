/**
 * 公式计算
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.risk.assess.quaAssess.FormulateCalculateEdit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateCalculateEdit',
    
    border: false,
    
    //重置按钮
    reEdit: function(){
    	var me = this;
    	var formularTextArea = me.formularSetEditMain.rightPanel.formularSetEditFormPanel.formularTextArea;
    	formularTextArea.setValue("");
    	formularTextArea.formularId = "";
    },
    //保存
    save : function(){
    	var me = this;
    	var formularId;
    	var dimensionStr;
    	var form = me.getForm();
    	if(me.formularSetEditMain){
    		var formulartextarea = me.formularSetEditMain.rightPanel.formularSetEditFormPanel.formularTextArea;
    		formularId = formulartextarea.formularId;
    	}else{
    		formularId = me.newformularId;
    	}
    	if(form.isValid()){
    		FHD.submit({//ajax调用
    				form: form,
    				url : __ctxPath + '/access/quaAssess/saveformulaset.f',//查询默认值
    				params : {
    					id : me.formulaId,
    					formularId : formularId,
    					dimensionStr : dimensionStr
    				},
    				callback : function(data){
    					
    				}
    		});
    	}
    },
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        me.id = 'formulateCalculateEditId';
        me.newformularId;
        
        FHD.ajax({
    			url : __ctxPath + '/sys/assess/findformulasetbycompanyid.f',
    			callback : function(data){
    			}
    		});
    		
    
		var kpiSummarizingStore = Ext.create('Ext.data.Store', {//汇总store
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/summarizingStore.f?type=kpiSummarizing',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
        
        var orgSummarizingStore = Ext.create('Ext.data.Store', {//汇总store
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/summarizingStore.f?type=orgSummarizing',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
        
        var strategySummarizingStore = Ext.create('Ext.data.Store', {//汇总store
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/summarizingStore.f?type=strategySummarizing',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
        
        var processSummarizingStore = Ext.create('Ext.data.Store', {//汇总store
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/summarizingStore.f?type=processSummarizing',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    
    
    	var Store = Ext.create('Ext.data.Store', {//性别store
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/findformulatecalculatestore.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	
    	var maxWeightStore = Ext.create('Ext.data.Store', {//最大值，加权平均值
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/findformulatemaxweightstore.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	
 
    	var weightAvgStore = Ext.create('Ext.data.Store', {//最大值，加权平均值
    	    fields: ['id', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/access/quaAssess/findformulateweightavgstore.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	
    	 Store.load({
				callback : function(records, options, success) {
					FHD.ajax({//ajax调用
						url : __ctxPath + '/access/quaAssess/queryformulatwcalculatevalue.f',//查询默认值
						params : {
						},
						callback : function(data,ope){
							me.orgRiskGather.setValue(data[0].deptRiskFormula);
							me.aimRiskGather.setValue(data[0].strategyRiskFormula);
							me.targetRiskGather.setValue(data[0].kpiRiskFormula);
							me.processRiskGather.setValue(data[0].processRiskFormula);
							//me.riskKindPossi.setValue(data[0].riskTypeHappen);
							//me.riskKindAffect.setValue(data[0].riskTypeImpact);
							me.riskLevelCount.setValue(data[0].riskLevelFormula);
							me.newformularId = data[0].formularId;
							me.formulaId = data[0].id;
							me.orgSummarizing.setValue(data[0].orgSummarizing);
							me.kpiSummarizing.setValue(data[0].kpiSummarizing);
							me.strategySummarizing.setValue(data[0].strategySummarizing);
							me.processSummarizing.setValue(data[0].processSummarizing);
						}
					});
				} });
    	
    	//组织风险值汇总计算公式
    	me.orgRiskGather = Ext.create('Ext.form.field.ComboBox', {
    		    store: Store,
    		    fieldLabel: '组织风险值汇总计算公式',
    		    labelWidth : 200,
    		    editable:false,
    		    margin: '7 30 3 30',
    		    queryMode: 'local',
    		    name:'deptRiskFormula',
    		    displayField: 'name',
    		    valueField: 'id',
    		    triggerAction :'all',
    		    columnWidth:.5
    		});
    	
    	//目标风险值汇总计算公式
    	me.aimRiskGather = Ext.create('Ext.form.field.ComboBox', {
    	    store: weightAvgStore,
    	    fieldLabel: '目标风险值汇总计算公式',
    	    labelWidth : 200,
    	    editable:false,
    	    margin: '7 30 3 30',
    	    queryMode: 'local',
    	    name:'strategyRiskFormula',
    	    displayField: 'name',
    	    valueField: 'id',
    	    triggerAction :'all',
    	    columnWidth:.5
    	});
    	//指标风险值汇总计算公式
    	me.targetRiskGather = Ext.create('Ext.form.field.ComboBox', {
    	    store: Store,
    	    fieldLabel: '指标风险值汇总计算公式',
    	    labelWidth : 200,
    	    editable:false,
    	    margin: '7 30 3 30',
    	    queryMode: 'local',
    	    name:'kpiRiskFormula',
    	    displayField: 'name',
    	    valueField: 'id',
    	    triggerAction :'all',
    	    columnWidth:.5
    	});
    	//流程风险值汇总计算公式
    	me.processRiskGather = Ext.create('Ext.form.field.ComboBox', {
    	    store: Store,
    	    fieldLabel: '流程风险值汇总计算公式',
    	    labelWidth : 200,
    	    editable:false,
    	    margin: '7 30 3 30',
    	    queryMode: 'local',
    	    name:'processRiskFormula',
    	    displayField: 'name',
    	    valueField: 'id',
    	    triggerAction :'all',
    	    columnWidth:.5
    	});
    	
    	me.riskLevelCount = Ext.widget('textfield', {
            fieldLabel: '风险水平计算公式',
            labelWidth : 200,
            readOnly: true,		//文本只读
            margin: '7 0 3 30',
            name: 'riskLevelFormula',
            columnWidth: .45
        });
    	
        
      var bbar =[//菜单项
    	           '->',
    	           {text : "保存",iconCls: 'icon-save', handler:me.save, scope : this}];
    
//	    var bbar = null;
//	    
//	    if($ifAllGranted('ROLE_ALL_ASSESS_RISKSTANDARD_SAVEFORMULA')){
//	    	bbar =[//菜单项
//	    	           '->',
//	    	           {text : "保存",iconCls: 'icon-save', handler:me.save, scope : this}];
//	    }else{
//	    	bbar =[//菜单项
//	    	           '->'];
//	    }
    
    
    me.orgSummarizing = Ext.create('Ext.form.field.ComboBox', {
	    store: orgSummarizingStore,
	    fieldLabel: '组织汇总',
	    labelWidth : 200,
	    editable:false,
	    margin: '7 30 3 30',
	    queryMode: 'local',
	    name:'orgSummarizing',
	    displayField: 'name',
	    valueField: 'id',
	    triggerAction :'all',
	    columnWidth:.5
	});
    
    me.kpiSummarizing = Ext.create('Ext.form.field.ComboBox', {
	    store: kpiSummarizingStore,
	    fieldLabel: '指标汇总',
	    labelWidth : 200,
	    editable:false,
	    margin: '7 30 3 30',
	    queryMode: 'local',
	    name:'kpiSummarizing',
	    displayField: 'name',
	    valueField: 'id',
	    triggerAction :'all',
	    columnWidth:.5
	});
    
    me.strategySummarizing = Ext.create('Ext.form.field.ComboBox', {
	    store: strategySummarizingStore,
	    fieldLabel: '目标汇总',
	    labelWidth : 200,
	    editable:false,
	    margin: '7 30 3 30',
	    queryMode: 'local',
	    name:'strategySummarizing',
	    displayField: 'name',
	    valueField: 'id',
	    triggerAction :'all',
	    columnWidth:.5
	});
    
    me.processSummarizing = Ext.create('Ext.form.field.ComboBox', {
	    store: processSummarizingStore,
	    fieldLabel: '流程汇总',
	    labelWidth : 200,
	    editable:false,
	    margin: '7 30 3 30',
	    queryMode: 'local',
	    name:'processSummarizing',
	    displayField: 'name',
	    valueField: 'id',
	    triggerAction :'all',
	    columnWidth:.5
	});
    
    me.summarizingFieldSet = Ext.create('Ext.form.FieldSet',{
		collapsible: true,
		defaults: {
                    margin: '3 30 3 30',
                    labelWidth: 200
            },
            layout: {
                    type: 'column'
            },
			title: '分类汇总',
			items : [me.orgSummarizing, me.kpiSummarizing, me.strategySummarizing, me.processSummarizing]
	});
    
    Ext.applyIf(me, {
        autoScroll: true,
        border: false,
        layout: 'column',
        bbar:bbar,
        bodyPadding: "5 5 5 5",
        items: [
		{	
			 xtype: 'fieldset',
             collapsible: true,
             defaults: {
                    margin: '3 30 3 30',
                    labelWidth: 200
            },
            layout: {
                    type: 'column'
            },
			title: '公式计算',
			items:[me.orgRiskGather, me.aimRiskGather, me.targetRiskGather, me.processRiskGather]
		}, me.summarizingFieldSet]
        
    });
    
    me.callParent(arguments);
    me.on('resize',function(p){
    	me.setHeight(FHD.getCenterPanelHeight());
    });
        
    }
});