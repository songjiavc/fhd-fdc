/**
 * 风险综合查询-查询条件表单
 *
 * @author
 */
Ext.define('FHD.view.risk.comprehensivequery.RiskCompreQueryForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskcomprequeryform',

    //清空表单缓存
    resetData: function () {	//id为树节点id
        var me = this;
        //清空组件值
        me.getForm().reset();
        me.riskVersion.setDisabled(true);
        me.deptSelect.setDisabled(true);
        me.parentRisk.clearValues();
        me.mainDept.clearValues();
        me.relaDept.clearValues();
        me.deptSelect.clearValues();
    },
    //风险库选择监听函数
    versionTypeLisener: function(combo,records,eOpts){
		var me = this;
		me.riskVersion.reset();//改变风险库，清空风险版本和部门的值
		me.deptSelect.clearValues();
		me.parentRisk.treeUrl = '/cmp/risk/getRiskTreeRecord?onlyLeaf='+me.parentRisk.onlyLeaf+'&schm='+records[0].data.id.split('risk_schm_')[1];
		if('risk_schm_dept'==records[0].data.id){//部门风险
			me.deptSelect.setDisabled(false);
		}else{
			me.riskVersion.setDisabled(false);
			me.deptSelect.setDisabled(true);
		}
	},
	//
	versionTypeLisenerDept: function(combo,records,eOpts){
		var me = this;
		if(me.deptSelect.value){
			me.riskVersion.setDisabled(false);
		}
	},
	//查询风险版本下拉框
	myLisener: function(combo,records,eOpts){
		var me = this;
		me.selectRiskVersion.proxy.extraParams.deptId = me.deptSelect.hideValue;
		me.selectRiskVersion.proxy.extraParams.schm = me.versionType.value;
		me.selectRiskVersion.load();
	},
	//加载十大风险列表数据
	loadGrid: function(){
		var me = this;
		var form = me.getForm();
		if(form.isValid()){
			me.up('riskcomprequerypanel').riskQuaryGrid.reloadData(form);
		}
	},
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        //风险编号
        me.riskCode = Ext.widget('textfield', {
		    fieldLabel:"风险编号",
		    allowBlank:true,
		    columnWidth: 1/3,
		    margin: '7 10 0 30',
		    name:'riskCode'
		});
        
        //风险名称
        me.riskName = Ext.widget('textfield', {
		    fieldLabel:"风险名称",
		    allowBlank:true,
		    columnWidth: 1/3,
		    margin: '7 10 0 30',
		    name:'riskName'
		});
        
        //风险描述
        me.riskDesc = Ext.widget('textfield', {
		    fieldLabel:"风险描述",
		    allowBlank:true,
		    columnWidth: 1/3,
		    margin: '7 10 0 30',
		    name:'riskDesc'
		});
        
        
       //上级风险
       me.parentRisk = Ext.create('FHD.view.risk.cmp.RiskSelector', {
            onlyLeaf: true,
            allowBlanks:true,
            title : '请您选择风险',
            fieldLabel : '上级风险',
            name : 'parentId',
            multiSelect: false,
            columnWidth: 1/3,
		    margin: '7 10 0 30',
		    treeUrl: '',
            schm: me.schm
        });
        
        //责任部门
        me.mainDept = new Ext.create('Ext.ux.form.OrgEmpSelect',{
    		name:'mainDeptId',
    		disabled: false,
        	fieldLabel : '责任部门',
        	type : 'dept',
            multiSelect : false,
            growMin: 20,
            growMax: 120,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true,
            margin: '7 10 0 30',
            columnWidth: 1/3
        });
      //相关部门
        me.relaDept = new Ext.create('Ext.ux.form.OrgEmpSelect',{
    		name:'relaDeptId',
    		disabled: false,
        	fieldLabel : '相关部门',
        	type : 'dept',
            multiSelect : false,
            growMin: 20,
            growMax: 120,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true,
            margin: '7 10 0 30',
            columnWidth: 1/3
        });

        //风险分库
        me.versionType = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'schm',
			dictTypeId : 'risk_schm',
			editable: false,
			labelAlign : 'left',
			fieldLabel : "风险库"+'<font color=red>*</font>',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 0 30',
			columnWidth: 1/3,
			listeners: {
	           select:function(combo,records,eOpts){//根据风险库类型加载风险版本和部门选择
	            	me.versionTypeLisener(combo,records,eOpts);
	           }
	        }
		});
        //风险版本store
        me.selectRiskVersion = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/risk/compre/findriskversionbytype.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:false
        });
        
        me.riskVersion = Ext.create('Ext.form.ComboBox', {
        	fieldLabel: '风险版本',
            name : 'riskVersion',
            editable: false,
            disabled: true,
            store: me.selectRiskVersion,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            margin: '7 10 0 30',
            columnWidth: 1/3,
            listeners: {
            	focus:function(combo,records,eOpts){//根据风险库类型加载风险版本和部门选择
 	            	me.myLisener(combo,records,eOpts);
 	           }
 	        }
		});
        
        me.deptSelect = new Ext.create('Ext.ux.form.OrgEmpSelect',{
    		name:'schmDept',
    		disabled: true,
        	fieldLabel : '部门',
        	type : 'dept',
            multiSelect : false,
            growMin: 20,
            growMax: 120,
            store: [],
            queryMode: 'local',
            forceSelection: false,
            createNewOnEnter: true,
            createNewOnBlur: true,
            filterPickList: true,
            margin: '7 10 0 30',
            columnWidth: 1/3,
            listeners: {
            	change:function(combo,records,eOpts){//根据风险库类型加载风险版本和部门选择
 	            	me.versionTypeLisenerDept(combo,records,eOpts);
 	           }
 	        }
        });
        
        
        
        Ext.applyIf(me, {
            border : false,
            autoDestroy: true,
            height: 130,
            items:[{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.versionType,me.riskName,me.riskDesc]
            },
            {
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.parentRisk, me.mainDept, me.relaDept]
            },
            {
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[me.riskCode, me.deptSelect, me.riskVersion]
            }
            ],
            dockedItems: [{
				xtype: 'toolbar',
	            dock: 'bottom',
	            ui: 'footer',
	            items: ['->', {
	                text: '查询',
	                iconCls: 'icon-zoom',
	                height : 20,
	                handler: function () {
	                    me.loadGrid();
	                }
	            },{
	                text: '清空',
	                iconCls: 'icon-del',
	                height : 20,
	                handler: function () {
	                    me.resetData();
	                }
	            }]
            }]
            
        });

        me.callParent(arguments);
    }

});