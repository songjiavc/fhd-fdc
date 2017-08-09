/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptInput', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.selectuserbyroleordeptinput',
	requires: [
    	'FHD.view.compoments.selectcompoments.SelectUserByRoleOrDept'
    ],
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	roleId : '',
	multiSelect : true,
	orgId : '',
    labelWidth : 100,
	border : false,
	useUserOrg : true,
	valueArray : [], //存放最终获取的id
	/**
	 * @author jia.song
	 * 获取组件值用于保存时使用
	 */
	setValue : function(arr){
		var me = this;
		var names = '';
		me.valueArray = [];
		Ext.Array.each(arr,function(item,index){
			me.valueArray.push(item.id);
			if(names == ''){
				names = item.name;
			}else{
				names = names + "," + item.name;
			}
		});
		me.selText.setValue(names);
	},
	
	/**
	 * 获取保存的id数组
	 * @return {}
	 */
	getValue : function(){
		var me = this;
		return me.valueArray;
	},
	
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
			text : '选择',
			handler : function(){
				//创建公共选择组件
        		var commonSel = Ext.widget('selectuserbyroleordept',{
        			parentField : me,
        			orgId : me.orgId,
        			roleId : me.roleId,
        			multiSelect : me.multiSelect,
        			useUserOrg : me.useUserOrg,
        			valueArray : me.valueArray
        		});
				var window = Ext.create('FHD.ux.Window',{
					title:FHD.locale.get('fhd.common.execute'),
					iconCls: 'icon-edit',//标题前的图片
					maximizable: true,
					height : Ext.getBody().getHeight() * 0.6,
					width : Ext.getBody().getWidth() * 0.6
				});
				window.show();
				window.add(commonSel);
			}
		});
		me.add(selBtn);
	}
});