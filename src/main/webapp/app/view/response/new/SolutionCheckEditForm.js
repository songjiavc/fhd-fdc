/**
 * 审批
 */
Ext.define('FHD.view.response.new.SolutionCheckEditForm',{
	extend:'Ext.form.Panel',
	alias: 'widget.solutioncheckeditform',
	requires: [
	           'FHD.ux.fileupload.FileUpload'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	autoScroll:true,
//	bodyPadding:'0 3 3 3',
	layout : {
		type : 'column'
	},
	defaults:{
		columnWidth:1/1
	},
	border:false,
	// 初始化方法
    initComponent: function() {
        var me = this;
        
        me.bbar = [
        	'->',{
	            text: FHD.locale.get("fhd.common.submit"),//提交按钮
	            iconCls: 'icon-operator-submit',
	            id: 'icm_standard_submit1',
	            handler: function () {
	            	
	            }
	        }
        ];
        
        //方案名称
        var solutionName = Ext.widget('textfield', {
            fieldLabel: '方案名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            name: 'solutionName',
            columnWidth: 1
        });
        //方案编号
        var solutionCode = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: '方案编号' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'solutionCode',
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
	                 	me.getForm().setValues({'solutionCode':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
         /*责任部门  */
		var orgId = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门' + '<font color=red>*</font>',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 10 30',
            columnWidth: .5
		});
		var orgOra = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '协助部门',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 10 30',
            columnWidth: .5
		});
        
        //方案描述
        var solutionDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '方案描述' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'solutionDesc',
            columnWidth: 1
        });
        //完成标志
        var indicator = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '完成标志' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            rows : 1,
            name: 'indicator',
            columnWidth: 1
        });
        //预计成本
        var cost = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计成本',
            margin: '7 10 10 30',
            name: 'cost',
            columnWidth: .5
        });
        //预计收效
        var income = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计收效',
            margin: '7 10 10 30',
            name: 'income',
            columnWidth: .5
        });
        //附件
        var attachment = Ext.widget('FileUpload', {
			labelAlign : 'left',
			labelText : '附件',
			labelWidth : 100,
			columnWidth: 1,
			name : 'fileId',
			height: 50,
			margin: '7 10 10 30',
			showModel : 'base'
		});   
		//预计日期
		var startDate = {
			xtype: 'datefield',
			margin: '7 10 10 30',
			fieldLabel : '预计开始时间',
		    name: 'beginDataStr',
		    format: 'Y-m-d',
		    columnWidth: .5
		};
        //预计日期
		var completeDate = {
			xtype: 'datefield',
			margin: '7 10 10 30',
			fieldLabel : '预计完成时间',
		    name: 'endDataStr',
		    format: 'Y-m-d',
		    columnWidth: .5
		};
   	 	//负责人
        var reponser = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '负责人' + '<font color=red>*</font>',
        	name : 'responsName',
        	id : 'responsNameId',
            type:'emp',
            multiSelect:false,
            margin: '7 10 10 30',
            columnWidth: .5
        });
        //是否纳入预案库
	    var ispreplan = Ext.widget('combo', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            margin: '7 10 10 30',
            columnWidth:.5,
            value:'N',
            name: 'statusId',
            fieldLabel:'是否纳入预案库', 
            labelAlign: 'left',
            store : [['Y', '是'],['N', '否']]
        });
		//基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '<input id="c1c"  type="checkbox" onclick="Ext.getCmp(\''+me.id+'\').select1(this.checked)"/>应对方案',
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
     	    items : [solutionName, solutionCode, autoButton,reponser,orgId,orgOra,cost,income, startDate,completeDate,solutionDesc,indicator,attachment,ispreplan]
        };
        
        Ext.apply(me, {
        	border:false,
            items : [basicInfoFieldset]
        });

       me.callParent(arguments);
    },
	reloadData:function(){
		var me=this;
	},
	loadData:function(businessId, executionId){
		var me=this;
		me.responsePlanGrid.reloadData();
	}
});