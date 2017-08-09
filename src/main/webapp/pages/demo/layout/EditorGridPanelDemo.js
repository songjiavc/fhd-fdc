Ext.define('FHD.demo.layout.EditorGridPanelDemo', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editorgridpaneldemo',
	
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
		Ext.define('Test', {//定义model
			 extend: 'Ext.data.Model',
			 fields: [{name: 'id', type: 'string'},
			 {name: 'name', type:'string'},
			 {name: 'code', type:'string'}]
			 });
	    function add(){// 新增方法
			var r = Ext.create('Test');
			grid.store.insert(0, r);
			grid.editingPlugin.startEditByPosition({
						row : 0,
						column : 0
					});
		};
		function del() {// 删除方法
			var selection = grid.getSelectionModel().getSelection();
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if (btn == 'yes') {
						var ids = [];
						for (var i = 0; i < selection.length; i++) {
							ids.push(selection[i].get('id'));
						}
						var ids = ids.join(',');
						alert('刪除ids：'+ids);
					}
				}
			});
		};
		function save() {// 保存方法
			var rows = grid.store.getModifiedRecords();
			var jsonArray = [];
			Ext.each(rows, function(item) {
						jsonArray.push(item.data);
					});
			var jsonStr = Ext.encode(jsonArray);
			alert(jsonStr);
			grid.store.commitChanges();
		};
		
        var cols = [ {
			dataIndex : 'id',
			hidden : true
		}, {
			header : "编号",
			dataIndex : 'code',
			sortable : true,
			flex : 1,
			editor : {
				//vtype : 'uniqueTitle',
				allowBlank : false
			}
		}, {
			header : "名称",
			dataIndex : 'name',
			sortable : true,
			flex : 1,
			editor : {
				allowBlank : false
			}
		}];
		
        var grid = Ext.create("FHD.ux.GridPanel",{//FHD.ux.EditorGridPanel
        	region:'center',
        	type:'editgrid',
        	url : __ctxPath + "/pages/demo/layout/list.json",
            extraParams:{
            	
            },
        	cols:cols,
        	tbarItems:[{
        			btype:'custom',
        			text:'添加',
        			handler:function(){
        				add();
        			}
    		},'-',{
    			btype:'custom',
    			text:'刪除',
    			handler:function(){
    				del();
    			}
    		},'-',{
    			btype:'save',
    			handler:function(){
    				save();
    			}
    		}],
        	title:'列表',
		    border: true,
		    checked: true
        });
        
        Ext.apply(me, {
            autoScroll: true,
            border: false,
            bodyPadding: "5 5 5 5",
            layout:'border',
            items: [grid]
        });
        
        me.callParent(arguments);
      
    }
});