Ext.define('FHD.view.response.new.SolutionEditPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.solutioneditpanel',
    border : false,
    activeItem: 0,
    type : 'risk',//关联风险的应对，dept为部门应对
    archiveStatus : 'saved',
    businessType : 'analysis',
    showType : 'form',
    navData: null,
    paramObj:{
    	editflag:false,
    	businessId:''
    },
    
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //评价计划列表
        me.solutionlist = Ext.create('FHD.view.response.new.SolutionList',{
        	businessType : me.businessType,
        	archiveStatus : me.archiveStatus,
        	showType : me.showType,
        	type : me.type,
        	addFormContainer:function(p,name){
        		me.reRightLayout(p);
        		if(me.navData){
	        		var data = [];
	        		Ext.Array.push(data,me.navData)
					data.push({
			               type: 'solution',
			               id: 'solution',
			               name: name
			        });
	        		me.reLayoutNavigationBar(data);
        		}
        	},
        	callback : function(){
        		if(me.showType != 'form'){
        			me.solutionlist.window.window.close();
        		}
        		me.go();
        	}
        });
        
        Ext.apply(me, {
            items: [
                me.solutionlist
            ]
        });

        me.callParent(arguments);
    },
    cancel : function(){
		var me = this;
		me.up('solutioneditpanel').setActiveItem(me.solutionlist);
	},
    reloadData:function(){
    	var me=this;
    	me.setActiveItem(me.solutionlist);
    	me.solutionlist.initParam(me.paramObj);
    	me.solutionlist.reloadData();
    },
    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.add(c);
        me.setActiveItem(c);
        me.doLayout();
    },
    //刷新导航方法
    reLayoutNavigationBar: function(data){},
    
    go : function(data){
    	var me = this;
    	me.reloadData();
    	if(me.navData){
	    	me.reLayoutNavigationBar(me.navData);
    	}
    }
    
});