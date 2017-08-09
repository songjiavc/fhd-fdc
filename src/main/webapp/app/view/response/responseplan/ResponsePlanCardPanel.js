Ext.define('FHD.view.response.responseplan.ResponsePlanCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.responseplancardpanel',
    
    activeItem: 0,
    
    requires: [
    	'FHD.view.response.responseplan.form.ResponsePlanForm',
    	'FHD.view.response.responseplan.form.ResponsePlanRangeForm',
    	'FHD.view.response.responseplan.SelectorResponseEmpWindow'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    tbar: {
        items: [
	        {
	            text: FHD.locale.get('fhd.common.details'),//基本信息导航按钮
	            iconCls: 'icon-001',
	            id: 'icm_constructplan_card_details_btn_top',
	            handler: function () {
	            	var constructplancardpanel = this.up('constructplancardpanel');
	            	if(constructplancardpanel){
	            		constructplancardpanel.setBtnState(0);
	            		constructplancardpanel.navBtnHandler(0);
	            	}
	            }
	        },
	        '<img src="'+__ctxPath+'/images/icons/show_right.gif">',
	        {
	            text: '范围选择',//范围选择导航按钮
	            iconCls: 'icon-002',
	            handler: function () {
	            	var constructplancardpanel = this.up('constructplancardpanel');
	            	if(constructplancardpanel){
	            		constructplancardpanel.setBtnState(1);
	            		constructplancardpanel.navBtnHandler(1);
	            	}
	            }
	        }
	    ]
    },
    bbar: {
        items: [
	        '->', 
	        {	
	            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
	            iconCls: 'icon-operator-home',
	            handler: function () {
	            	var responseplancardpanel = this.up('responseplancardpanel');
	            	if(responseplancardpanel){
	            		responseplancardpanel.undo();
	            	}
	            }
	        },
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.back"),//上一步按钮
	            iconCls: 'icon-operator-back',
	            handler: function () {
	            	var responseplancardpanel = this.up('responseplancardpanel');
	            	if(responseplancardpanel){
	            		responseplancardpanel.back();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.last"),//下一步按钮
	            iconCls: 'icon-operator-next',
	            handler: function () {
	            	var responseplancardpanel = this.up('responseplancardpanel');
	            	if(responseplancardpanel){
	            		responseplancardpanel.last();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.common.save"),//保存按钮
	            iconCls: 'icon-control-stop-blue',
	            handler: function () {
	            	var constructplancardpanel = this.up('constructplancardpanel');
	            	if(constructplancardpanel){
	            		constructplancardpanel.finish();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.common.submit"),//提交按钮
	            iconCls: 'icon-operator-submit',
	            handler: function () {
	            	var constructplancardpanel = this.up('responseplancardpanel');
	            	if(constructplancardpanel){
	            		constructplancardpanel.submit();
	            	}
	            }
	        }
	    ]
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.responseplanform = Ext.widget('responseplanform',{
        	businessId:me.businessId,
        	executionId:me.executionId,
        	editflag:me.editflag,
        	border:false
        });
        me.responseplanrangeform = Ext.widget('responseplanrangeform',{
        	businessId:me.businessId,
        	editflag:me.editflag,
        	border:false
        });
        
        Ext.applyIf(me, {
            items: [
                me.responseplanform,
                me.responseplanrangeform
            ]
        });

        me.callParent(arguments);
        
        if(me.executionId){
        }else{
    	}
    },
    /**
     * 返回按钮事件
     */
    undo:function(){
    	var me = this;
    	var responseplaneditpanel = me.up('responseplaneditpanel');
		responseplaneditpanel.setActiveItem(responseplaneditpanel.responseplanlist);
    },
    /**
     * 上一步按钮事件
     */
    back:function(){
    	var me = this;
    	/*
    	if(me.constructplanrangeform.saveFunc()){
    		me.setBtnState(0);
    		me.navBtnHandler(me,0);
    	}
    	*/
		me.setActiveItem(0);
    },
    /**
     * 下一步按钮事件
     */
    last:function(){
    	var me = this;
    	me.navBtnHandler(1);
    },
    /**
     * 下一步按钮事件
     */
    submit:function(){
    	var me = this;
    	//2.按缺陷选择
    	var me=this;
		var constructPlanRelaDefect = Ext.widget('selectorresponseempwindow',{
		}).show();
    
    },
    /**
     * 完成按钮事件
     */


    /**
     * 设置导航按钮的事件函数
     * @param {panel} cardPanel cardpanel面板
     * @param index 面板索引值
     */
    navBtnHandler: function (index) {
 	   	var me = this;
 	    me.setActiveItem(index);
    },
    /**
     * 设置上一步和下一步按钮的状态
     */
    navBtnState:function(){
    	var me = this;
    },
    /**
     * 设置导航按钮的选中或不选中状态
     * @param index,要激活的面板索引
     */
    setBtnState: function (index) {
        var k = 0;
        var topbar = Ext.getCmp('icm_constructplan_card_topbar');
        var btns = topbar.items.items;
        for (var i = 0; i < btns.length; i++) {
            var item = btns[i];
            if (item.pressed != undefined) {
                if (k == index) {
                    item.toggle(true);
                } else {
                    item.toggle(false);
                }
                k++;
            }
        }
    },
    /**
     * 设置tbar导航按钮状态:false可用，true不可用
     */
    setNavBtnEnable:function(v,first){
    	var me=this;
    	me.setBtnState(0);
    	me.navBtnHandler(0);
    },
    /**
     * 初始化tbar和bbar按钮状态
     */
    setInitBtnState:function(){
    	var me = this;
    },
    reloadData:function(){
    	var me=this;
    }
});