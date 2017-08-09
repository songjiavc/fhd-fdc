Ext.define('FHD.view.risk.assess.RoleGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.roleGrid',
    autoLoad:false,
      
      // 初始化方法
      initComponent: function() {
    	  var me = this;
          var cols = [
				{
					dataIndex:'wSetId',
					hidden:true
				},
				{
				    header: "职务",
				    dataIndex: 'duty',
				    sortable: true,
				    align: 'center',
				    flex:1
				},{
				    header: "权重",
				    dataIndex: 'weighting',
				    sortable: true,
				    align: 'center',
				    flex:1,
				    editor:{
						xtype:'textfield',
						editable:true
					}
				},
				{
					dataIndex:'roleCode',
					hidden:true
				}
          ];
          
          Ext.apply(me, {
        	  region:'center',
          	  url : __ctxPath + "/sys/assess/queryweightsetgridbyduty.f",
              /*extraParams:{
              	riskId:1
              },*/
          	  cols:cols,
          	  type: 'editgrid',
  		      border: true,
  		      checked: false,
  		      pagable : false,
  		      searchable : false,
  		      tbarItems:[{
    			btype:'add',
    			handler:function(){
    				var departmentGrid = me.up('weightingSetMain').departmentGrid;
					me.empRoleGridPanel = Ext.create('FHD.view.sys.organization.emp.EmpRoleGridPanel',{
						save:function(){
							var selections = me.empRoleGridPanel.getSelectionModel().getSelection();//得到选中的记录
					    	var ids = [];
							for(var i=0;i<selections.length;i++){
								ids.push(selections[i].get('id'));
							}
							//验证用户是否存在多个角色中
							FHD.ajax({//ajax调用
		    					url : __ctxPath + '/sys/assess/finduserrolessamebyroleids.f',
		    					params : {
		    						roleIds: ids
		    					},
			    				callback : function(data){
			    					if(data.length>0){
			    						var alertStr = '';
			    						for(var i=0;i<data.length;i++){
			    							alertStr = alertStr+data[i].userNames+'存在'+data[i].roleNames+'角色中'+'<br>';
			    						}
			    						FHD.alert(alertStr+'<br>'+'角色下员工不允许重复，请重新配置！');
			    					}else{
			    						FHD.ajax({//ajax调用
					    					url : __ctxPath + '/sys/assess/saveroleweightsetbyroleid.f',//保存职务
					    					params : {
					    						roleIds: ids,
					    						id: departmentGrid.store.data.items[0].data.id
					    					},
						    				callback : function(data){
						    					me.rolewin.hide();
						    					FHD.notification('操作成功！',FHD.locale.get('fhd.common.prompt'));
						    					me.store.load();
						    				}
						    			});
			    					}
			    				}
			    			});
						}
					});
					me.rolewin = new Ext.Window({
			    	    title: '选择角色',
			    	    height: 500,
			    	    width: 600,
			    	    layout: 'fit',
			    	    items: [me.empRoleGridPanel]
			    	}).show();
    			}
        	  },{
    			btype:'delete',
    			handler:function(){
					var items = me.getSelectionModel().getSelection();
					var jsonArray=[];
						Ext.each(items,function(item){
							jsonArray.push(item.data);
						});
					FHD.ajax({//ajax调用
    					url : __ctxPath + '/sys/assess/deleteroleweight.f',//删除职务权重
    					params : {
    						modifyRecords:Ext.encode(jsonArray)
    					},
    				callback : function(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    					me.store.load();
    				}
    			});
    			}
        	  },{
    			btype:'save',
    			handler:function(){
					var items = me.store.data.items;
					var jsonArray=[];
						Ext.each(items,function(item){
							jsonArray.push(item.data);
						});
					var departmentGrid = me.up('weightingSetMain').departmentGrid;
					FHD.ajax({//ajax调用
    					url : __ctxPath + '/sys/assess/saveweightsetduty.f',//保存职务权重
    					params : {
    						modifyRecords:Ext.encode(jsonArray),
    						id: departmentGrid.store.data.items[0].data.id
    					},
    				callback : function(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    					me.store.load();
    				}
    			});
    			}
        	  }]
          });

          me.callParent(arguments);
      }

});