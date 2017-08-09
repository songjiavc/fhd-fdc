/**
 * 考评项目
 * Grid AUTHOR:Perry Guo 
 * DATE:2017-07-14
 */
Ext.define('FHD.view.check.rule.CheckProjectGrid', {
	extend : 'FHD.ux.EditorGridPanel',
	alias : 'widget.checkProjectGrid',

	initComponent : function() {
		var me = this;
		var cols = [{
					dataIndex : 'isUserd',
					sortable : false,
					hidden : true
				},{
					dataIndex : 'id',
					sortable : false,
					hidden : true
				}, {
					header : '考评项目',
					dataIndex : 'name',
					sortable : false,
					flex : 2,
					editor : new Ext.form.field.Text({
					allowBlank : false
					})
				}, {
					header : '总分',
					dataIndex : 'totalScore',
					sortable : false,
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}, {
					header : '排序',
					dataIndex : 'projectOrder',
					sortable : true,
					flex : 1,
					editor : new Ext.form.NumberField({
								allowBlank : false
							})
				}];

		Ext.apply(me, {
			region : 'center',
			cols : cols,
			url : __ctxPath + "/check/checkproject/findCheckProject.f",
			clicksToEdit : 2,
			triggerEvent : me.editText,
			tbarItems : [{
						btype : 'add',
						handler : function() {
							var initValue = {
								id : "",
								name : "",
								totalScore : "",
								projectOrder : "",
								isUserd : "0"
							};
							me.getStore().add(initValue);
						}

					}, {
						btype : 'delete',
						handler : function() {
							var sm = me.getSelectionModel();
							var array=[];
							if (sm.hasSelection()) {
								Ext.Msg.confirm("提示", "真的要删除选中的行吗？", function(
										btn) {
									if (btn == "yes") {
									var delData=sm.getSelection();
								    Ext.each(delData,function(d){
								     	if(d.data.id!=null)
									{
										array.push(d.data.id);
									}
								    })
								    if (array.length!=0) {
								    Ext.Ajax.request({
										url : __ctxPath+ "/check/checkproject/deleteCheckProject.d",
									    method : 'post',
										success : function() {
										me.store.load();
										},
										params : {
										data : array
												}
											})
										}
									me.getStore().remove(sm.getSelection());
									}
								});
							} else {
								Ext.Msg.alert("错误", "请先选择删除的行，谢谢");
							}
						}
					}, {
						btype : 'save',
						handler : function() {
							var store = me.getStore();
							var modified = store.getModifiedRecords();

							if (modified.length != 0) {
								Ext.Msg.confirm("提示", "是否需要保存数据",
										function(btn) {
											if (btn == "yes") {
												var t=me.validaData(modified)
												if(!t){
												return;
												}else{
												var jsonArray = [];
												Ext.each(modified, function(m) {
															jsonArray
																	.push(m.data);
														})
												var data = Ext.JSON
														.encode(jsonArray);
														debugger;
												Ext.Ajax.request({
													url : __ctxPath
															+ "/check/checkproject/savaCheckProject.s",
													method : 'post',
													success : function() {
														me.store.load();
													},
													params : {
														data : data
													}
												})

											}
											}
										});

							}

						}

					}

			]
		})
		me.callParent(arguments);

	},
	/*
	 * 新增数据的验证
	 * 考评项目名称    不能为空
	 * 考评总分              不能为空且只能为整数
	 * 排序                        不能为空且只能为整数
	 * */
	validaData:function (modified)
		{
		 var me=this;
		 var result = true;  
		 var r=false;
         Ext.each(modified,function(d){
         	if(d.data.name!=null&&d.data.totalScore!=''&&d.data.projectOrder!='')
         		{
         		r=true;
         		}
         })
                if(!r){  
                    Ext.MessageBox.alert("验证","对不起，您输入的数据非法");  
                    result = false;  
                    return result;  
                }  
        return result;  
 		
		}

})