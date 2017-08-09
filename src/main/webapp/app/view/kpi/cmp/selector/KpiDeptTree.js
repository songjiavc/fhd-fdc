Ext.define('FHD.view.kpi.cmp.selector.KpiDeptTree', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.depttreepanel',
   
    //title: $locale('depttreepanel.title'), //zjx修改，去掉title
    
    subCompany:false, // 是否显示子公司
    companyOnly:false,
    checkable:false,	// 是否有复选框
    chooseId:'',
    checked: false,
    myexpand:true,	//设置树是否展开  郑军祥使用
    rootVisible:false,
    
    initComponent: function() {
        var me = this;
        if(__user.companyId == 'null') {
        	__user.companyId = 'root';
        	__user.companyName = $locale('depttreepanel.__user.companyName');
        	me.subCompany = true;
        }	
        Ext.apply(me, {
        	autoScroll:true,
        	listeners : {
				itemclick : function(node, record, item) {
				    me.onItemClick(record);
				},
				load:function(store,records){
                	me.treeload(store,records);
                }
			},
        	rootVisible:me.rootVisible,
        	url: __ctxPath + '/kpi/plan/depttreeloader.f',
    	    extraParams: {
	        	checkable: me.checkable,
	        	subCompany: me.subCompany,
	        	companyOnly: me.companyOnly,
	        	chooseId:me.chooseId,
	        	deptId: me.deptId
	        },
	        iconCls:me.iconCls,
    	    root: {
    	        text: __user.companyName,
    	        id: __user.companyId,
    	        expanded: me.myexpand
    	    }
        });
        
        me.callParent(arguments);
    },
    /**
     * 点击树节点执行函数
     */
    onItemClick: function (node) {
    }
});