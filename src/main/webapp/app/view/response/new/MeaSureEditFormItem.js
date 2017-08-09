/**
 * 控制措施基本信息
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.response.new.MeaSureEditFormItem', {
	extend: 'Ext.form.Panel',
	alias: 'widget.measureeditformitem',
	requires: [
		'FHD.view.icm.icsystem.component.AssessPointEditGrid',
		'FHD.view.risk.cmp.form.RiskRelateFormDetail'
	],
	frame: false,
	border : false,
	type : 'risk',
	autoDestroy : true,
	archiveStatus : 'saved',
	paramObj : {
		measureId : ""
	},
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	autoScroll : true,
	autoHeight : true,
	initParam:function(paramObj){
  		var me = this;
		me.paramObj = paramObj;
	},
    addComponent: function () {
		var me = this;
		//存放可编辑列表的数据 
		me.editGridJson = Ext.widget('textfield', {
		    name : 'editGridJson',
		    value: '',
		    hidden : true
		});
		me.riskId = Ext.widget('textfield', {
		    name : 'riskId',
		    value: '',
		    hidden : true
		});
		me.measureId = Ext.widget('textfield', {
		    name : 'id',
		    value: '',
		    hidden : true
		});
		me.archiveStatusStr = Ext.widget('textfield', {
		    name : 'archiveStatus',
		    value: '',
		    hidden : true
		});
	    //基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
		    collapsible: false,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		        columnWidth : 1 / 2,
		        margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '基本信息'
		});
    	//基本信息fieldset
        me.moreinfofieldset = Ext.widget('fieldset', {
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                columnWidth : 1 / 2,
                margin: '7 30 3 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: '更多信息',
            collapsed : true
        });
//	        me.add(me.basicinfofieldset);
       
        //控制措施内容
        me.desc = Ext.widget('textareafield', {
			height:60,
			rows : 3,
			fieldLabel : '控制措施内容'+ '<font color=red>*</font>',
			value : '',
			allowBlank: false,
			name : 'meadesc',
			columnWidth: .5
        });
        me.basicinfofieldset.add(me.desc);
         me.code = Ext.widget('textfield', {
            name : 'meacode',
            fieldLabel : '控制措施编号' + '<font color=red>*</font>',
            value: '',
            allowBlank: false,
            columnWidth: .4
        });
	     // 初始化方法
	    me.measureCreateCodeBtn = {
             xtype: 'button',
             margin: '7 30 3 3',
             text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
             handler: function(){
       			 FHD.ajax({
	                 url:__ctxPath+'/ProcessPoint/ProcessPoint/ProcessPointCode.f',
	                 params: {
	                 	processId : me.processId,
	                 	processRiskId : me.processRiskId
                 	 },
                 callback: function (data) {
                 	 me.code.setValue(data.data.code);//给code表单赋值
                 }
                 });
	             },
	             columnWidth: .1
         	};
        me.basicinfofieldset.add(me.code,me.measureCreateCodeBtn,me.editGridJson,me.riskId,me.measureId,me.archiveStatusStr);
        /*责任部门  */
        me.noteDepart = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept',
			fieldLabel : '责任部门' + '<font color=red>*</font>',
			labelAlign: 'left',
			labelWidth: 100,
			store: [],
			allowBlank : false,
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'meaSureorgId',
			value:''
		});
        
        me.basicinfofieldset.add(me.noteDepart);
       	/*员工单选 */
        me.noteradio = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'emp',
			fieldLabel : '责&nbsp;&nbsp;任&nbsp;&nbsp;人'+ '<font color=red>*</font>',
			labelAlign: 'left',
			labelWidth: 100,
			store: [],
			allowBlank : false,
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'meaSureempId',
			value:''
		});
        me.basicinfofieldset.add(me.noteradio);
        
		me.pointNote = Ext.create('FHD.ux.process.ProcessAndPointSelector',{
		    name:'processAndPoint',
		    type : 'processandpoint',
		    allowBlank : false,
		    parent : false,
		    value:'',
		    fieldLabel:'选择流程 /节点<font color=red>*</font>',
         	//hidden : false,
            multiSelect : false,
		    extraParam : {
		    	smIconType:'display',
		    	canChecked:true,
		    	leafCheck : true
		    },
		    onChange : me.onChange
        });
    	me.basicinfofieldset.add(me.pointNote);
        /*节点类型*/
		/*me.notestyle = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'pointType',
			dictTypeId : 'ca_point_type',
			multiSelect : false,
			fieldLabel : '节点类型'
		});
		me.basicinfofieldset.add(me.notestyle);*/
		/* 是否关键节点 */
		/* 是否关键节点 */
		me.isKeyPoint = Ext.create('FHD.ux.dict.DictRadio',
		{
			name:'isKeyControlPoint',
			dictTypeId:'0yn',
			allowBlank : false,
			labelAlign:'left',
			defaultValue : '0yn_y',
			fieldLabel : '是否关键控制点'+ '<font color=red>*</font>'
		});
		me.basicinfofieldset.add(me.isKeyPoint);
		me.controlTarget = Ext.widget('textfield', {
            name : 'controlTarget',
            fieldLabel : '控制目标',
            value: '',
            columnWidth: .5
        });
        me.moreinfofieldset.add(me.controlTarget);
		//实施证据
		me.measureControl = Ext.widget('textfield', {
            name : 'implementProof',
            fieldLabel : '实施证据',
            value: '',
            columnWidth: .5
        });
        me.moreinfofieldset.add(me.measureControl);
        //控制点 
		me.controlPoint = Ext.widget('textfield', {
            name : 'controlPoint',
            fieldLabel : '控制点' ,
            value: '',
            columnWidth: .5
        });
        me.moreinfofieldset.add(me.controlPoint);
        /*控制频率  dict */
		me.controlFrequency = Ext.create('FHD.ux.dict.DictSelect',
		{
			name:'controlFrequency',
			dictTypeId:'ic_control_frequency',
			labelAlign : 'left',
			fieldLabel : '控制频率',
			columnWidth: .5
		});
		me.moreinfofieldset.add(me.controlFrequency);
		/* 控制方式 */
		me.controlMeasure = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'controlMeasure',
			dictTypeId : 'ic_control_measure',
			multiSelect : false,
			labelAlign : 'left',
			fieldLabel : '控制方式',
			columnWidth: .5
		});
		me.moreinfofieldset.add(me.controlMeasure);
		//维护评价店
        me.assesspointfieldset = Ext.widget('fieldset', {
            autoHeight : true,
            collapsible : false,
            collapsed : false,
            autoWidth: true,
      
            title: '评价点列表'
        });
        me.assesspointeditgrid =  Ext.widget('assesspointeditgrid',{
			type : 'E'
	    });    //将展示父节点组件创建
        me.assesspointfieldset.add(me.assesspointeditgrid);
        
        // 风险事件
        if(me.type == 'risk'){
			me.riskSelector = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
				margin : '0 0 0 0',
	            border: false,
	            showbar: false
	        });
		}else if(me.type == 'dept' || me.type == 'all'){
			me.riskSelector = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
				fieldLabel : '风险',
				multiSelect: false,
				riskmyfoldertreevisable : true,
				labelAlign : 'left',
				height: 23,
				name : 'riskSelect',
				columnWidth : 1
			});
		}

		me.riskfieldSet = Ext.widget('fieldset', {
			title : '风险信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			layout : {
				type : 'fit'
			},
			margin: '5 5 0 5',
            defaults : {
            	margin: '7 10 10 30'
            },
			items : [me.riskSelector
					]
		});
		
		if(me.type == 'risk'){
			me.riskfieldSet.collapse();
		}
		
        me.add(me.riskfieldSet);
        me.add(me.basicinfofieldset);
        me.add(me.moreinfofieldset);
        me.add(me.assesspointfieldset);
    },
   // 初始化方法
   initComponent: function() {
       var me = this;
       Ext.applyIf(me,{ bbar : {
               items: ['->',	
	               {   
	            	   text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),
	                   iconCls: 'icon-operator-home',
	                   handler: me.callback
	               },
	               	{   
	            	   text: FHD.locale.get("fhd.common.save"),
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.save
	               }
               ]
           }});
       me.callParent(arguments);
       //向form表单中添加控件
	   me.addComponent();
    },
    save: function() {
	   	var me = this.up('measureeditformitem');
	   	var measureForm = me.getForm();
//    	me.noteDepart.renderBlankColor(me.noteDepart);
//    	me.noteradio.renderBlankColor(me.noteradio);
    	if(measureForm.isValid()) {
    		if(me.noteDepart.getValue() == null){
				FHD.notification("请选择责任部门!",FHD.locale.get('fhd.common.prompt'));
				return false;
			}
    		
    		if(me.noteradio.getValue() == null){
				FHD.notification("请选择责任人!",FHD.locale.get('fhd.common.prompt'));
				return false;
			}
    		
    		var riskId = '';
    		if(me.type == 'risk'){
    			riskId = me.riskId;
    		}else if(me.type == 'dept' || me.type == 'all'){
    			if(me.riskSelector.getValue() != ''){
	    			riskId = Ext.decode(me.riskSelector.getValue())[0].id;
    			}
    		}
		   	measureForm.setValues({//paramObj
               riskId: riskId,
               archiveStatus : me.archiveStatus,
               id : me.paramObj.measureId
		    }); //editGridJson
		    var assessrows = me.assesspointeditgrid.store.getModifiedRecords();
		    var jsonArray=[];
				Ext.each(assessrows,function(item){
					jsonArray.push(item.data);
				});
			measureForm.setValues({
                editGridJson : Ext.encode(jsonArray)
            }); //editGridJson
    		FHD.submit({
				form : measureForm,
				url : __ctxPath + '/processrisk/savemeasure.f',
				callback: function (data) {
					if(!data.success){
						if(data.info){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.info);
						}
					}else{
						me.callback();
					}
				}
			});
		}
	},
	reloadData: function() {
        var me = this;
        me.load({
            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/processrisk/loadmeasureedititemformdata.f',
            params: {
                measureId : me.paramObj.measureId
            },
            success: function (form, action) {
                var jb = Ext.decode(action.response.responseText);
                if(jb.data.processPoint != null){
               		me.pointNote.initValue(jb.data.processPoint[0]);
                }
                if(action.result.data.meaSureorgId){
            		var value = Ext.JSON.decode(action.result.data.meaSureorgId);
            		me.noteDepart.setValues(value);
            	}
                
                if(action.result.data.meaSureempId){
            		var value = Ext.JSON.decode(action.result.data.meaSureempId);
            		me.noteradio.setValues(value);
            	}
                
                if(me.type == 'risk'){
            		me.riskSelector.reloadData(Ext.decode(action.result.data.riskId)[0].id);
            	}else if(me.type == 'dept' || me.type == 'all'){
            		if(action.result.data.riskId != '' && action.result.data.riskId != null && action.result.data.riskId != undefined){
	            		me.riskSelector.initValue(action.result.data.riskId);
            		}else{
            			me.riskSelector.clearValues();
            		}
            	}
                return true;
            }
        });
        
        me.assesspointeditgrid.initParam({
	    	processId : '',
			measureId : me.paramObj.measureId
	    });
        me.assesspointeditgrid.reloadData();
	},
    callback : function(){},
    clearFormData:function(){
		var me = this; 
		me.getForm().reset();
		me.noteradio.clearValues();
		me.noteDepart.clearValues();
		me.pointNote.grid.store.removeAll();
		// 责任部门
		var valuedept = [];
    	var objdept = {};
    	objdept["id"] = __user.majorDeptId;
    	valuedept.push(objdept);
    	me.noteDepart.setHiddenValue(valuedept);
		me.noteDepart.initValue(Ext.encode(valuedept));
		if(me.type == 'risk'){
			// 风险
			me.riskSelector.reloadData(me.paramObj.riskId);
			me.riskId = me.paramObj.riskId;
		}else if(me.type == 'dept' || me.type == 'all'){
			me.riskSelector.clearValues();
		}
	},
	onChange : function(){
		var me = this.up('measureeditformitem');
		if(this.getValue() == ""){
			me.assesspointeditgrid.store.removeAll();
			me.assesspointeditgrid.down('[iconCls=icon-add]').setDisabled(true);
		}else{
			me.assesspointeditgrid.initParam(
			{
				riskId : me.paramObj.riskId,
				processPointId : Ext.decode(this.getValue())[0].pointId,
				processId : Ext.decode(this.getValue())[0].processId,
				measureId : ''
			}
			);
			me.assesspointeditgrid.down('[iconCls=icon-add]').setDisabled(false);
		}
	}
});