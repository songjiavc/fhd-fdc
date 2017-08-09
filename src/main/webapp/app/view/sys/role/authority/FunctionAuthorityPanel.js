Ext.define('FHD.view.sys.role.authority.FunctionAuthorityPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.functionauthoritypanel',
	
    title:'功能授权',
    //权限类型：B->按钮权限；F->字段权限；
	etype:'B',
	//获取checkbox的属性name
	name:'authorityCode',
    
    initComponent : function() {
    	var me = this;
    	
    	me.saveBtn = Ext.widget('button' ,{ 
    		text: FHD.locale.get('fhd.common.save'),
    		iconCls: 'icon-save',
    		handler: function () {
    			me.saveFunctionAuthority();
            }
    	});
    	
    	Ext.applyIf(me,{
    		bbar : [
    			'->',me.saveBtn,me.submitBtn
    		],
    		border:false,
    		overflowX:'hidden',
			overflowY:'auto'
    	});
    	
    	FHD.ajax({
            url: __ctxPath + '/sys/auth/role/findFunctionAuthorityByRoleIdAndEtype.f',
            params: {
            	roleId:me.roleId,
            	etype: me.etype
            },
            callback: function (response) {
                if (response) {
                	me.createFiledSet(response)
                }else{
                    me.saveBtn.setDisabled(true);
                }
            }
    	});
    	
        me.callParent(arguments);
    },
 	//创建fieldSet
 	createFiledSet: function (response){
    	var me = this;
    	
    	Ext.each(response.authorityMap, function (item, index){
    		for(var key in item){  
    			//js动态生成需要数据
        		me.checkboxGroup = Ext.create('Ext.form.CheckboxGroup',{
            		layout : 'column',
            	    autoScroll: false,
            		labelAlign : 'right',
                    fieldLabel: '功能权限',
            		hideLabel:true,
                    vertical: true
                });
               	
               	me.myFieldSet = Ext.create('Ext.form.FieldSet',{
               		title : key,
               		margin: '5 25 5 5',
               		layout: 'fit',
               		items : [me.checkboxGroup]
               	});
               	
               	me.add(me.myFieldSet);
               	
               	var items = new Array();
            	Ext.each(item[key], function (r, i){
            		items.push({
            			xtype:'checkbox',
            			columnWidth: 1/8,
            			boxLabel:r.authorityName,
            			inputValue:r.authorityCode,
            			name:me.name
            		});
                });
                if(items.length>0){
                	me.checkboxGroup.add(items);
                	
                	me.setDefaultValue(response.defaultAuthority);
                }
    		}
        });
 	},
 	//设置复选框默认选中
 	setDefaultValue:function(defaultAuthority){
 		var me=this;

		var defaultAuthorityArray = defaultAuthority.split(","); 
		
		var items = Ext.ComponentQuery.query('checkbox', me);
		Ext.each(items, function (item, index){
		    item.setValue(false);
		    for(i=0;i<defaultAuthorityArray.length;i++){
		    	if(item.inputValue==defaultAuthorityArray[i]){
		    		item.setValue(true);
		    	}
			}
		});
 	},
 	//保存角色与按钮权限|字段权限
 	saveFunctionAuthority: function (){
 		var me=this;
 		
 		var vobj = me.getForm().getValues();
 		
    	if(vobj.authorityCode == undefined || vobj.authorityCode =='' || vobj.authorityCode =='[]'){
    		FHD.notification('请选择赋予的功能权限!',FHD.locale.get('fhd.common.prompt'));
    		return false;
    	}
    	
    	var myMask = new Ext.LoadMask(Ext.getBody(), {
			msg:"数据保存中，请等待..."
		});
		myMask.show();
		
        FHD.ajax({
        	url: __ctxPath + '/sys/auth/role/saveRoleAuthority.f',
            params: {
            	roleId:me.roleId,
            	authorityIdsStr:vobj.authorityCode,
            	type:me.etype
            },
            callback: function (response) {
            	myMask.hide();
            	if(response){
            		FHD.notification('保存成功!','提示');
            	}
            }
        });
 	},
 	//设置角色id
 	setRoleId:function(roleId){
		var me=this;
		
		me.roleId=roleId;
	},
 	//重新加载数据
 	reloadData: function (){
    	var me = this;
    	
    	FHD.ajax({
            url: __ctxPath + '/sys/auth/role/findDefaulAuthorityByRoleIdAndEtype.f',
            params: {
            	roleId:me.roleId,
            	etype: me.etype
            },
            callback: function (response) {
                if (response) {
                	me.setDefaultValue(response.defaultAuthority);
                }else{
                    me.saveBtn.setDisabled(true);
                }
            }
    	});
    }
});