/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.commonselect.CommonSelectMainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.commonselectmainpanel',
	requires: [
    	'FHD.view.compoments.commonselect.CommonSelectGridPanel'
    ],
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	initComponent : function(){
		var me = this;
		var treePanel = Ext.widget('fhdtree',{
			flex : .3,
			url : __ctxPath + '/cmp/risk/getRiskTreeRecord',
			extraParams : {
				schm : me.schm
			},
			listeners : {
	        	itemclick : function( obj, record, item, index, e, eOpts ){
	        		if(me.type == 're'){
	        			if(record.get('leaf')){
	        				me.gridPanel.insertRecord({
	        					id : record.get('id'),
	        					riskCode : record.get('code'),
	        					riskName : record.get('text')
	        				});
	        			}
	        		}else{
	        			me.gridPanel.insertRecord({
        					id : record.get('id'),
        					riskCode : record.get('code'),
        					riskName : record.get('text')
	        			});
	        		}
	        	}
	        }
		});
		
		me.gridPanel = Ext.widget('commonselectgridpanel',{
			flex : .7,
			storeAutoLoad : false,
			checked : false,
			multiSelect : me.multiSelect,
			columns : [
				{
					dataIndex:'id',
					hidden:true
				},{
		            header: "风险编码",
		            dataIndex: 'riskCode',
		            flex:1
		        },{
		        	header: "风险名称",
		            dataIndex: 'riskName',
		            flex:1
		        }
			],
			listeners : {
				itemdblclick : function(c,r,o){
					//双击删除选中的记录
					var me = this;
					me.getStore().remove(r);
				}
	        }
		});
		
		Ext.apply(me,{
			items : [treePanel,me.gridPanel]
		});
		me.callParent(arguments);
		//如果值数组中存在值则直接设置树选中
		if(me.parentField.valueArray.length > 0){
			//首先找到是store中的第几个元素
		//	var selectNode = me.down('fhdtree').getStore().getNodeById(me.parentField.valueArray[0]);
		//	me.down('fhdtree').getSelectionModel().select(selectNode);
			//初始化右侧grid
			me.gridPanel.reloadData({
				ids : me.parentField.valueArray
			});
		}
	}
});