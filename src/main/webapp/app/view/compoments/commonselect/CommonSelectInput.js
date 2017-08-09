/**
 * @author  jia.song@pcitc.com
 * @desc     通用选择组件入口类
 * @date     2017-08-03
 */
Ext.define('FHD.view.compoments.commonselect.CommonSelectInput', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.commonselectinput',
	requires: [
    	'FHD.view.compoments.commonselect.CommonSelectMainPanel'
    ],
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	multiSelect : false,
	valueArray : [],
	
	setValue : function(arr){
		var me = this;
		var names = '';
		Ext.Array.each(arr,function(item,index){
			me.valueArray = [];
			me.valueArray.push(item.id);
			if(names == ''){
				names = item.name;
			}else{
				names = names + "," + item.name;
			}
		});
		me.selText.setValue(names);
	},
	
	getValue : function(){
		var me = this;
		return me.valueArray;
	},
	
	border : false,
	initComponent : function(){
		var me = this;
		me.callParent(arguments);
		//两个控件  一个text 一个button
		if(!Ext.isEmpty(me.fieldLabel)){
			var label = Ext.widget('label',{
				width : me.labelWidth,
				html : me.fieldLabel
			});
			me.add(label);
		}
		me.selText = Ext.widget('textfield',{
			flex : .8,
			readOnly : true
		});
		me.add(me.selText);
		var selBtn = Ext.widget('button',{
			flex : .1,
			iconCls:'icon-magnifier',
			handler : function(){
				//创建公共选择组件
        		me.commonSel = Ext.widget('commonselectmainpanel',{
        			parentField : me,
        			schm : me.schm,
        			multiSelect : me.multiSelect,
        			type : me.type
        		});
				var window = Ext.create('FHD.ux.Window',{
					title:FHD.locale.get('fhd.common.execute'),
					iconCls: 'icon-edit',//标题前的图片
					maximizable: true,
					height : Ext.getBody().getHeight() * 0.6,
					width : Ext.getBody().getWidth() * 0.6,
					buttons: [
						{
							text: '确定',
							handler:function(){
								//对于选中的节点进行赋值给组件
						       	var selecteds = me.commonSel.gridPanel.getStore();
					    		var names = '';
					    		var temp = []; //清空数组
					    		selecteds.each(function(record){
					    			temp.push({
					    				id : record.get('id'),
					    				name : record.get('riskName')
					    			});
					    		});
					    		me.setValue(temp);
					    		this.up('window').close();
							}
						},
		    			{
		    				text: '关闭',
		    				handler:function(){
		    					var me = this;
		    					me.up('window').close();
		    				}
		    			}
		    		]
				});
				window.show();
				window.add(me.commonSel);
			}
		});
		me.add(selBtn);
	}
});