Ext.define('FHD.demo.grid.GridEdit',{	
	extend : 'Ext.panel.Panel',
	alias : 'widget.gridPanel',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		me.id = 'GridEdit';
		var gridList = Ext.getCmp('GridList');
		var formButtons = [//表单按钮
		               	{text: FHD.locale.get('fhd.common.save'),handler:save},
		                   {text: FHD.locale.get('fhd.common.cancel'),handler:cancel}
		               ];
		               /***attribute end***/
		               /***function start***/
		               function save(){//保存方法
		                   var form = formPanel.getForm();
		                   if (form.isValid()) {
		               		if (me.isAdd) {//新增
		               			FHD.submit({form:form,url:addUrl,callback:function(data){grid.store.load()}});//参数依次为form，url，callback
		               		} else {//更新
		               			FHD.submit({form:form,url:updateUrl,callback:function(data){grid.store.load()}});//参数依次为form，url，callback
		               		}
		               		me.close();
		               		gridList.treeComboxStore.load();
		               	}
		                   
		               }
		               function cancel(){//取消方法
		            	   me.close();
		               }
		               
		               /***function end***/
		               /***form start***/ 
		               var formColums = [//form表单的列
		                  	{xtype : 'hidden',name : 'id', id:'id'}
		                  	,{xtype : 'treecombox',
		                  		name : 'parentId',
		                  		labelAlign: 'right',
		                  		fieldLabel : FHD.locale.get('fhd.pages.test.field.parentName'),
		                  		valueField : 'id',
		                  		displayField : 'text',
		                  		vtype:'treeNode',
		                  		maxPickerHeight : 200,
		                  		maxPickerWidth : 250,
		                  		margin: '3 3 3 3',
		                  		multiSelect:true,
		                  		store : gridList.treeComboxStore}
		                  	,{xtype : 'textfield',
		                  		labelAlign: 'right',
		                  		fieldLabel : FHD.locale.get('fhd.pages.test.field.title')+'<font color=red>*</font>',
		                  		name : 'title', vtype:'uniqueTitle',margin: '3 3 3 3',
		                  		allowBlank : false}
		                  	, {xtype : 'textfield',
		                  		labelAlign: 'right',
		                  		fieldLabel : FHD.locale.get('fhd.pages.test.field.name')+'<font color=red>*</font>',
		                  		name : 'name',margin: '3 3 3 3',
		                  		allowBlank : false}
		                  	,Ext.create('FHD.ux.org.CommonSelector',{
		                   	fieldLabel: '部门员工混合多选',
		                   	labelAlign: 'right',
		                   	name:'dept',
		                   	growMax:50,
		                       type:'dept_emp',
		                       multiSelect:true
		                   })
		                  	, {xtype : 'numberfield',
		                  		labelAlign: 'right',
		                  		fieldLabel : 'NUMBER',
		                  		name : 'num',  
		                  		maxValue: 92,  
		                  		minValue: 0,  
		                  		allowDecimals: true, // 允许小数点 
		                  		nanText:'请输入数字',  
		                  		step:0.5,  margin: '3 3 3 3',
		                  		allowBlank : true}
		                  	,{xtype : 'combobox',
		                  		labelAlign: 'right',
		                  		fieldLabel : FHD.locale.get('fhd.pages.test.field.myLocale')+'<font color=red>*</font>',
		                  		store : gridList.fieldstore,
		                  		displayField : 'name',
		                  		valueField : 'id',
		                  		name: 'myLocale',
		                  		allowBlank : false,margin: '3 3 3 3',
		                  		editable : false
		                  	}
		               ];
		               var formPanel = Ext.create('Ext.form.Panel',{
		               	layout:'fit',
		               	height:310,
		               	bodyPadding: 5,
		               	items: [
		               		{xtype: 'fieldset',
		               			defaults: {columnWidth: 1/1,margin: '5 5 5 5'},//每行显示一列，可设置多列
		               			layout: {type: 'column'},
		               			collapsed: false,
		               			collapsible: false,
		               			title: FHD.locale.get('fhd.common.baseInfo'),
		               			items: formColums}
		               	      ],
		               	buttons:formButtons
		               });
		               /***form end***/ 
		               /***CustomValidator start***/
		               Ext.apply(Ext.form.field.VTypes, {
		                   //验证title不重复
		                   uniqueTitle:  function(val,field) {
		                   	var id = field.up("form").down('#id');
		                       var flag=false;
		                       FHD.ajax({
		                       	url: 'test/checkTitle.f',
		                           async:false,//这一项必须加上，否则后台返回true,flag的值也为false
		                           params : {
		               					title:val,
		               					id:id.value
		               				},
		               			callback : function(data){
		               				if(data){
		               					flag = true;
		               				}
		               			}
		                       })
		                       return flag;
		                   },
		                   uniqueTitleText: FHD.locale.get('fhd.pages.test.uniqueMsg'),
		                   //验证parentId的合法性
		                   treeNode: function(val,field){
		                   	var parentId = field.up("form").down('[name=parentId]').value;
		                   	var id = field.up("form").down('#id').value;
		                   	var flag=false;
		                   	if(parentId==null || parentId == ''){
		                   		flag = true;
		                   	}else if(parentId==id){
		                   		flag = false;
		                   	}else{
		                   		FHD.ajax({
		               	        	url: 'test/checkParent.f',
		               	            async:false,//这一项必须加上，否则后台返回true,flag的值也为false
		               	            params : {
		               						parentId:parentId,
		               						id:id
		               					},
		               				callback : function(data){
		               					if(data){
		               						flag = true;
		               					}
		               				}
		               	        })
		                   	}
		                       return flag;
		                   },
		                   treeNodeText: FHD.locale.get('fhd.pages.test.treeNodeMsg')
		               });
		               /***CustomValidator end***/

		                me.on('resize',function(me){
		               	formPanel.setWidth(me.getWidth()-10);
		               	formPanel.setHeight(me.getHeight()-40);
		               })
		               if(typeof(gridList.paramId) != "undefined") {
		               	formPanel.form.load( {
		                       url : 'test/getTestMvc.f',
		                       params: {id: gridList.paramId},
		                       failure : function(form,action) {
		                           alert('页面初始化错误');
		                       }
		                   });
		               }
		                Ext.applyIf(me, {
		                    autoScroll: true,
		                    border: false,
		                    items: [formPanel]            
		                });
		             me.callParent(arguments);
	}
	
})