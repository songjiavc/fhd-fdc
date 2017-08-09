/**
 * 应对方案发起表单  接受以后退回的应对措施
 * add by 宋佳   2013-9-27
 */
Ext.define('FHD.view.response.bpm.SolutionFormForBpm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutionformforbpm',
    autoScroll: true,
	collapsed : false,
	collapsable : false,
    border : false,
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //方案名称
        var solutionName = Ext.widget('textfield', {
            fieldLabel: '方案名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            name: 'name'
        });
        var riskId = Ext.widget('textfield', {
		    name : 'riskId',
		    value: '',
		    hidden : true
		});
        var solutionId = Ext.widget('textfield', {
		    name : 'id',
		    value: '',
		    hidden : true
		});
        //方案编号
        var solutionCode = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: '方案编号' + '<font color=red>*</font>',
            allowBlank : false,
            margin: '7 10 10 30',
            name: 'code',
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
	                 	me.getForm().setValues({'code':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
         /*责任部门  */
		me.orgId = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门' + '<font color=red>*</font>',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false
		});
        //方案描述
        var solutionDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '方案描述' + '<font color=red>*</font>',
            allowBlank : false,
            name: 'desc'
        });
        //完成标志
        var indicator = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '完成标志' + '<font color=red>*</font>',
            name: 'completeIndicator',
            allowBlank : false,
            columnWidth: 1
        });
        //预计成本
        var cost = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计成本',
            name: 'cost'
        });
        //预计收效
        var income = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计收效',
            name: 'income'
        });
        //附件
        var attachment = Ext.widget('FileUpload', {
			labelAlign : 'left',
			labelText : '附件',
			labelWidth : 100,
			columnWidth: 1,
			name : 'fileId',
			height: 50,
			showModel : 'base'
		});   
		//预计日期
		var startDate = Ext.widget('datefield',{
			fieldLabel : '预计开始时间<font color=red>*</font>',
			allowBlank : false,
		    name: 'expectStartTime',
		    format: 'Y-m-d'
		});
        //预计日期
		var completeDate = Ext.widget('datefield',{
			fieldLabel : '预计完成时间<font color=red>*</font>',
		    name: 'expectEndTime',
		    allowBlank : false,
		    format: 'Y-m-d'
		});
   	 	//负责人
        me.reponser = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '负责人' + '<font color=red>*</font>',
        	name : 'empId',
            type:'emp',
            multiSelect:false
        });
         //应对策略
        var strategy = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'stategy',
			labelAlign : 'left',
			dictTypeId : 'rm_response_strategy',
			multiSelect : false,
			fieldLabel : '应对策略'
		});
        //是否纳入预案库
	    var ispreplan = Ext.create('FHD.ux.dict.DictSelect', {
            name : 'isAddPresolution',
            labelAlign : 'left',
			dictTypeId : '0yn',
			multiSelect : false,
			fieldLabel : '是否纳入预案库'
        });
		//基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            border : false,
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '7 10 10 30',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [riskId,solutionId,solutionName, solutionCode, autoButton,me.orgId,me.reponser,cost,income, startDate,completeDate,solutionDesc,strategy,ispreplan,indicator,attachment]
        };
        
        Ext.apply(me, { bbar : {
               items: ['->',	
	               {   
	            	   text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),
	                   iconCls: 'icon-operator-home',
	                   handler: me.cancel
	               },
	               	{   
	            	   text: FHD.locale.get("fhd.common.save"),
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.save
	               }
               ]
           },
        	border:false,
            items : [basicInfoFieldset]
        });

       me.callParent(arguments);
    },
    save: function() {
	   	var me = this.up('solutionform');
	   	var solutionForm = me.getForm();
    	if(solutionForm.isValid()) {
		   	solutionForm.setValues({//paramObj
               riskId: me.paramObj.riskId,
               id : me.paramObj.solutionId
		    }); 
		    
    		FHD.submit({
				form : solutionForm,
				url : __ctxPath + '/response/saveresponsesolution.f',
				callback: function (data) {
					if(!data.success){
						if(data.info){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.info);
						}
					}else{
						me.cancel();
						me.up('solutioneditpanel').solutionlist.reloadData();
					}
				}
			});
		}
	},
	reloadData: function() {
        var me = this;
        me.load({
            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/response/loadsolutionformbysolutionid.f',
            params: {
                solutionId : me.paramObj.solutionId
            },
            success: function (form, action) {
                return true;
            },
            failure: function (form, action) {
                return true;
            }
        });
	},
    cancel : function(){
		var me = this;
		me.up('solutioneditpanel').setActiveItem(me.up('solutioneditpanel').solutionlist);
    },
    clearFormData:function(){
		var me = this; 
		me.getForm().reset();
		me.orgId.clearValues();
		me.reponser.clearValues();
	}
});