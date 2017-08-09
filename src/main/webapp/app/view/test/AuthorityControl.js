Ext.define('FHD.view.test.AuthorityControl', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.authoritycontrol',

    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        /***********************tab1**********************/
        //tab1放一个带tbar或者bbar的grid
        me.tab1 = Ext.create('Ext.panel.Panel',{
        	title:'tab1',
        	html:'this is a static show.'
        });
        /***********************tab2**********************/
        //tab2放一个带fields和button的form
        me.tab2 = Ext.create('Ext.form.Panel',{
        	title:'tab2'
        });
        //form的code和name字段
        me.code = Ext.create('Ext.form.field.Text',{
        	name:'code',
        	fieldLabel: 'code'
        });
        me.name = Ext.create('Ext.form.field.Text',{
        	name:'name',
        	fieldLabel: 'name'
        });
        //添加了fields的权限
        if($ifAllGranted('ROLE_USER')){
        	me.tab2.add(me.code);
        	me.tab2.add(me.name);
        }
        //form的reset和submit按钮
        me.resetButton = Ext.create('Ext.button.Button',{
            text: 'Reset',
            handler: function() {
                me.tab2.getForm().reset();
            }
        });
        me.submitButton = Ext.create('Ext.button.Button',{
            text: 'Submit',
            handler: function() {
                var form = me.tab2.getForm();
                var vobj = form.getValues();
                Ext.Msg.alert('提示', vobj.code +':'+ vobj.name);
            }
        });
        me.buttons = new Array();
        //添加了button的权限--某个固定权限ROLE_(权限编号)authorityCode，多个权限之间用","分隔
        if($ifAllGranted('ROLE_USER')){
        	me.buttons.push(me.resetButton);
        	me.buttons.push(me.submitButton);
        }
        me.tab2.add(me.buttons);
        
        Ext.applyIf(me, {
        	flex:1,
        	border : false,
        	tabBar:{
        		//控制tab样式，右侧显示
        		style : 'border-left: 1px  #99bce8 solid;'
        	}
        });
        
        me.callParent(arguments);
        
        // 添加了TAB签的权限--某个固定权限ROLE_(权限编号)authorityCode，多个权限之间用","分隔
        me.add(me.tab1);
        if($ifAllGranted('ROLE_USER')){
            me.add(me.tab2);
        }
        
        me.getTabBar().insert(0,{xtype:'tbfill'});
    },
    reloadData:function(){
    	var me=this;
    	
    }
});