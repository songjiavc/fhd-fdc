Ext.define('FHD.view.risk.assess.DepartmentGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.departmentGrid',
      
      // 初始化方法
      initComponent: function() {
    	  var me = this;
          var cols = [
				{
					dataIndex:'id',
					hidden:true
				},
				{
				    header: "责任部门",
				    dataIndex: 'ZRDept',
				    sortable: true,
				    align: 'center',
				    flex:1,
				    editor:{
						xtype:'textfield',
						editable:true
					}
				},{
				    header: "相关部门",
				    dataIndex: 'XGDept',
				    sortable: true,
				    align: 'center',
				    flex:1,
				    editor:{
						xtype:'textfield',
						editable:true
					}
				},
				{
				    header: "参与部门",
				    dataIndex: 'FZDept',
				    sortable: true,
				    align: 'center',
				    flex:1,
				    editor:{
						xtype:'textfield',
						editable:true
					}
				}/*,
				{
				    header: "参与部门",
				    dataIndex: 'CYDept',
				    sortable: true,
				    align: 'center',
				    flex:1,
				    editor:{
						xtype:'textfield',
						editable:true
					}
				}*/
				
          ];
          
          Ext.apply(me, {
          	  url : __ctxPath + "/sys/assess/queryweightsetgrid.f",
          	  cols:cols,
          	  type: 'editgrid',
  		      border:  true,
  		      checked: true,
  		      pagable : false,
  		      searchable : false,
  		      tbarItems:[{
    			btype:'save',
    			handler:function(){
					var items = me.store.data.items;
					var rows = me.store.getModifiedRecords();//取出列表每一行数据
					if(!items.length){
						FHD.notification('权重不能为空！',FHD.locale.get('fhd.common.prompt'));
						return ;
					}
					var jsonArray=[];
					jsonArray.push(rows[0].data);
					
					FHD.ajax({//ajax调用
    					url : __ctxPath + '/sys/assess/saveweightsetdept.f',//保存部门权重
    					params : {
    					modifyRecords:Ext.encode(jsonArray),
    					id: rows[0].data.id
    				},
    				callback : function(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    				}
    			});
    			}
        	  }]
          });
          me.callParent(arguments);
      }

});