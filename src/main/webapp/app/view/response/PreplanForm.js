/**
 * 预案表单
 */
Ext.define('FHD.view.response.PreplanForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.preplanform',
    requires:['FHD.ux.fileupload.FileUpload'],
	collapsed : false,
	collapsable : false,
    
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
    select1 : function(tt){
	    var me = this;
	    var array = me.up('preplanforriskform').selectArray;//standardapply中存放standardcontroledit的数组
	   	if(tt){
	   		array.push(me.id);//me.id 形式  standardcontroledit0
	   	}else{
	   		Ext.Array.remove(array, me.id);
	   	}
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        //风险
        var riskName={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险分类',
		    value:'供应商管理风险',
		    columnWidth: .5
		};
        //风险code
        var riskCode={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险编号',
		    value:'201308098',
		    columnWidth: .5
		};
		
		var riskLevel={
            xtype: 'radiogroup',
            margin: '7 10 10 30',
            fieldLabel: '风险等级',
            colSpan : 4,
            width : 400,
            items: [
                {
                    xtype: 'radiofield',
                    boxLabel: '红'
                },
                {
                    xtype: 'radiofield',
                    boxLabel: '黄'
                },
                {
                    xtype: 'radiofield',
                    boxLabel: '绿'
                }
            ]
        };
        
        //预案名称
        var preplanName = Ext.widget('textfield', {
            fieldLabel: '预案名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 10 30',
            name: 'preplanName',
            columnWidth: .5
        });
        //预案编号
        var preplanCode = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: '预案编号' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'preplanCode',
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
	                 	me.getForm().setValues({'preplanCode':data.code});//给code表单赋值
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
			fieldLabel : '协助部门' + '<font color=red>*</font>',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false,
			margin: '7 10 10 30',
            columnWidth: .5
		});
        
        //预案描述
        var preplanDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '预案描述' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'preplanDesc',
            columnWidth: 1
        });
        //预计成本
        var cost = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计成本' + '<font color=red>*</font>',
            margin: '7 10 10 30',
            name: 'cost',
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
        //风险信息
        var riskInfoFieldset = {
            xtype:'fieldset',
            title : '风险信息',
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
     	    items : [riskName, riskCode, riskLevel]
        };
        
		//基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '应对预案',
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
     	    items : [preplanName, preplanCode, autoButton,orgId,orgOra,reponser,cost, preplanDesc,attachment]
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
            items : [riskInfoFieldset, basicInfoFieldset]
        });

       me.callParent(arguments);
    }
});