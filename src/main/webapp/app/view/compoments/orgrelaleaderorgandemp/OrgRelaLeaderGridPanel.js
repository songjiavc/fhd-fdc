/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderGridPanel', {
    extend: 'FHD.ux.GridPanel',
    searchable : false,
    checked : true,
    url: __ctxPath + '/check/findOrgRelaEmpInfoByBussIdOrOrgId.f',
    requires: [
    	'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderConfigPanel'
    ],
    alias: 'widget.orgrelaleadergridpanel',
	pageSize : 10,
	initParam : function(extraParams){
		var me = this;
		me.extraParams = extraParams;
	},
    
	reloadData : function(extraParams){
		var me=this;
		me.store.proxy.extraParams = extraParams;
    	me.store.load();
	},
	
	configRelaOrg : function(){
		var me = this.up('orgrelaleadergridpanel');
		//创建配置页
		var configPanel = Ext.widget('orgrelaleaderconfigpanel');
		//创建配置window
		var window =  Ext.create('FHD.ux.Window',{
			parent : me,
			bbar: [
					'->',
				  { 
				  		xtype: 'button',
				  		text: '确定',
				  		iconCls: 'icon-save',
	                   	handler: function() {
					       	var me = window,temp = [];
					       	//获取人员组件value
					        var bussId = me.down('combobox').getValue();
					        var empIds = me.down('selectuserbyroleordeptinput').getValue();
					       	var selecteds = me.down('orgrelaleaderselectedgridpanel').getStore();     //获取grid 选中的人员
				    		if(empIds.length == 0  || selecteds.data.items.length == 0 || bussId == null){
				    			FHD.notification('提示', '必填项不能为空!');
				    			return false;
				    		}
				    		selecteds.each(function(record){
				    			temp.push(record.data.id);
				    		});
				    		FHD.ajax({
								url : __ctxPath + '/check/saveOrgRelaEmpInfo.f',//判断是否发起工作流
								params : {
									bussinessId : bussId,
									empIds : empIds,
									orgIds : temp
								},
								callback: function (data) {
									if(data.success){
										FHD.notification('提示', '保存成功!');
										me.parent.up('orgrelaleadermainpanel').down('orgrelaleaderparampanel').queryFun(); 
										me.parent.up('orgrelaleadermainpanel').down('orgrelaleaderparampanel').managePeopleCombo.store.load(); //初始化人员列表查询项
				    					return false;
									}
						        }
							});
				    		me.close();
					    }
				  },
				  { 
				  		xtype: 'button', 
				  		text: '取消' ,
				  		iconCls : 'icon-cancel ',
				  		handler: function (btn) {
		                   window.close();
		                }
				  }
			]
		});
		window.add(configPanel);
		window.show();
	},
	
	deleteRelaOrg : function(btn){
		var me = btn.up('orgrelaleadergridpanel');
		var deleteCheckUrl = '/check/deleteRelaOrgByIds.f';
		var selections = me.getSelectionModel().getSelection();
		var ids = [];
		if(selections.length > 0){
			for(var i=0;i<selections.length;i++){
				ids.push(selections[i].data.id);
			}
			FHD.ajax({
                url : __ctxPath + deleteCheckUrl,
                params : {
                	ids : ids
                },
                callback : function(data) {
                    if(data.success) {//删除成功！
                    	Ext.MessageBox.show({
			                title:'操作成功',
			                msg:'已经删除!'
			            });
                    	me.reloadData(me.extraParams);
                    }
                }});
		 }else{
		 	 Ext.MessageBox.show({
                title:'操作错误',
                msg:'没有选中任何记录!'
            });
		 	return false;
		 }
	},
	initComponent : function(){
		var me = this;
		var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
				header : "业务单元",
				dataIndex : 'bussinessName',
				flex : 1
			},{
	            header: "管理人员",
	            dataIndex: 'empName',
	            flex:1
	        },{
	        	header: "管理部门",
	            dataIndex: 'manageName',
	            flex:1
	        },{
	        	header : "被管理部门",
	        	dataIndex : 'managedName',
	        	flex : 1
	        }
       ];
        
       var tbarItems = [{
               	text : '配置关系',
            	iconCls: 'icon-edit',
                handler: me.configRelaOrg
            },{
            	text : '删除',
            	text : FHD.locale.get('fhd.common.delete'),
            	iconCls: 'icon-del',
                handler: me.deleteRelaOrg
            }]
        
        Ext.apply(me,{
        	cols : cols,
        	tbarItems : tbarItems
        });
        me.callParent(arguments);
	}
});