/**
 * 应对方案编辑表单
 */
Ext.define('FHD.view.response.new.SolutionHigherQueryForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutionhigherqueryform',
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
        // 查询开关
        var onOrOff = Ext.widget('textfield', {
            name: 'onOrOff',
            hidden : true
        });
        //方案名称
        var solutionName = Ext.widget('textfield', {
            fieldLabel: '方案名称',
            name: 'solutionName'
        });
        //方案编号
        var solutionCode = Ext.widget('textfield', {
            fieldLabel: '方案编号',
            margin: '7 10 10 30',
            name: 'solutionCode'
        });
        //方案描述
        var solutionDesc = Ext.widget('textfield', {
            fieldLabel: '方案描述',
            name: 'desc'
        });
        //是否纳入预案库
	    var ispreplan = Ext.create('FHD.ux.dict.DictSelect', {
            name : 'isAddPresolution',
            labelAlign : 'left',
			dictTypeId : '0yn',
			fieldLabel : '是否纳入预案库'
        });
       /**
        *  public static String SOLUTION_AUTO = "0";
    	/**
     	 * */
        me.typeStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'0', 'name':'自动应对'},
		        {'id':'1', 'name':'计划应对'}
			]
		});
        /*   处理状态 store 
        * 
        */
        me.statusStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'N', 'name':'未执行'},
		        {'id':'H', 'name':'执行中'},
		        {'id':'F', 'name':'执行完'}
			]
		});
		/*工作内容*/
		me.statusCombo = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '计划状态',
			store :me.statusStore,
			emptyText:'请选择',
			multiSelect : true,
			valueField : 'id',
			name:'status',
			displayField : 'name'
			});
			/*工作内容*/
		me.typeCombo = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '计划类型',
			store :me.typeStore,
			emptyText:'请选择',
			valueField : 'id',
			name:'type',
			displayField : 'name'
			});
		 /*   处理状态 store 
        * 
        */
        me.measureTypeStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'M', 'name':'控制措施'},
		        {'id':'S', 'name':'应对措施'}
			]
		});
		/*工作内容*/
		me.measureType = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '措施类型',
			store :me.measureTypeStore,
			emptyText:'请选择',
			multiSelect : true,
			valueField : 'id',
			name:'measureType',
			displayField : 'name',
			});
        //基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '查询信息',
            border : true,
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
     	    items : [onOrOff,solutionName, solutionCode,me.statusCombo,me.typeCombo,solutionDesc,ispreplan,me.measureType]
        };
        
        Ext.apply(me, { bbar : {
               items: ['->',	
	               {   
	            	   text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),
	                   iconCls: 'icon-operator-home',
	                   handler: me.cancel
	               },
	               	{   
	            	   text: "重置",
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.clearFormData
	               },
	               	{   
	            	   text: "确定",
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.submit
	               }
               ]
           },
        	border:false,
            items : [me.riskFieldSet,basicInfoFieldset]
        });

       me.callParent(arguments);
    },
    submit: function() {
	   	var window = this.up('window');
	   	window.me.reloadData();
	   	this.up('solutionhigherqueryform').cancel();
	},
	reloadData: function() {
        var me = this;
        me.riskdetailform.reloadData(me.paramObj.riskId);
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
		var me = this.up('window');
		me.hide();
    },
    clearFormData:function(){
		var me = this.up('solutionhigherqueryform'); 
		me.getForm().reset();
	}
});