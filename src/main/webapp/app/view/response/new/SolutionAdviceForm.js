/**
 * 应对方案编辑表单
 */
Ext.define('FHD.view.response.new.SolutionAdviceForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutionadviceform',
    
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
    // 初始化方法
    initComponent: function() {
        var me = this;

	    me.bbar={
				items: [
					'->',{
						text: '保存',
	    				iconCls: 'icon-control-stop-blue',
	    				handler: function () {
	    				} 
					},{
	       				text: '提交',
	    				iconCls: 'icon-operator-submit',
	    				handler: function () {
	    				} 
	    			}
	    		]
			};
			
			
		Ext.applyIf(me,{
			items:[{
				xtype : 'fieldset',
				defaults:{
				},
				layout : {
					type : 'column'
				},
				collapsed : false,
				collapsible : true,
				title : '风险信息',
				items:[
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险名称', value:'供应商订货起点与生产需求相差较大'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险分类', value:'供应商管理风险'},
	//						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险编号',value:'201308098'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:'管理创新部'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'财务部'}						
				]
			},{
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1
				},//每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				collapsed : false,
				collapsible : true,
				title : '应对方案',
				items:[
					{xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '方案名称', value:'风险应对方案'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '方案编号', value:'2013080098'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:'管理创新部'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'财务部'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '负责人', value:'邢军'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计成本', value:'100万元'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计开始日期', value:'2013-08-09'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计结束日期', value:'2013-08-19'},
					{xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '方案描述',value:'物料需求变差率'},
					{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '完成标志',value:'《库存管理方案指定》'}
				]
			},{
				xtype : 'fieldset',
				defaults : {
					margin : '7 10 0 30'
				},//每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				margin : '7 30 0 13',
				collapsed : false,
				collapsible : false,
				title : '复合意见',
				items:[
					{xtype: 'textareafield', fieldLabel: '复合意见 <font color=red>*</font>', allowBlank:false, name : '',columnWidth : 1}
				]
			}]
		});

   		me.callParent(arguments);
    }
});