Ext.define('FHD.view.comm.analysis.ThemeAnalysisList',{
	extend: 'Ext.container.Container',
    alias: 'widget.themeanalysislist',
    
    pagable:true,
    layout: 'fit',
    
    initComponent: function(){
    	var me = this;
    	
		//主题分析列表
		me.themeAnalysisGrid = Ext.create('FHD.ux.GridPanel', {
	        border: false,
	        url: __ctxPath + '/themeAnalysis/findThemeAnalysisList.f',
	        extraParams:{
	        	companyId: __user.companyId
	        },
	        cols: [
	            {dataIndex: 'id',hidden:true},
	            {dataIndex: 'companyId',hidden:true},
	    		{header : '名称',dataIndex : 'name',sortable : true, flex : 2}, 
	 			{header:'描述', sortable: false,dataIndex: 'desc',flex:3,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showThemeAnalysisDetail('" + record.data.id + "')\" >" + value + "</a>"; 
					}
				},
	 			{header : '布局类型',dataIndex : 'layoutType',sortable : true, flex:1},
	 			{header : '布局属性',dataIndex : 'attribute',sortable : true, flex:1},
	 			{header : '所属公司',dataIndex : 'companyName',sortable : true, flex:3}
			],
            tbarItems: [
				{iconCls : 'icon-add',id:'theme_themeanalysis_add',text:'添加',tooltip: '添加主题分析',handler :me.addThemeAnalysis,scope : this},
				{iconCls : 'icon-edit',id:'theme_themeanalysis_edit',text:'修改',tooltip: '修改主题分析',handler :me.editThemeAnalysis,disabled: true,scope : this},
				{iconCls : 'icon-del',id:'theme_themeanalysis_del',text:'删除',tooltip: '删除主题分析',handler :me.delThemeAnalysis,disabled: true,scope : this}
			]
		});
		me.themeAnalysisGrid.store.on('load', function () {
			me.setstatus()
		});
        me.themeAnalysisGrid.on('selectionchange', function () {
            me.setstatus()
        });
        me.callParent(arguments);
        //主题分析列表
        me.add(me.themeAnalysisGrid);
    },
    setstatus: function(){
    	var me = this;
    	
        var length = me.themeAnalysisGrid.getSelectionModel().getSelection().length;
        me.themeAnalysisGrid.down('#theme_themeanalysis_del').setDisabled(length === 0);
        if(length != 1){
        	me.themeAnalysisGrid.down('#theme_themeanalysis_edit').setDisabled(true);
        }else{
        	me.themeAnalysisGrid.down('#theme_themeanalysis_edit').setDisabled(false);
        }
    },
    //新增主题分析
    addThemeAnalysis:function(){
    	var me=this;
    	
    	var themeanalysismainpanel = me.up('themeanalysismainpanel');
    	if(themeanalysismainpanel){
    		themeanalysismainpanel.paramObj.editflag=false;
    		themeanalysismainpanel.paramObj.businessId='';
    		//激活新增面板
    		themeanalysismainpanel.navBtnHandler(1);
    	}
    },
    //编辑主题分析
    editThemeAnalysis: function(button){
    	var me=this;
    	
    	var selection = me.themeAnalysisGrid.getSelectionModel().getSelection();//得到选中的记录
    	
    	var themeanalysismainpanel = me.up('themeanalysismainpanel');
    	if(themeanalysismainpanel){
    		themeanalysismainpanel.paramObj.editflag=true;
    		themeanalysismainpanel.paramObj.businessId=selection[0].get('id');
    		
    		//激活编辑面板
    		themeanalysismainpanel.navBtnHandler(1);
    	}
    },
    //删除主题分析
    delThemeAnalysis: function(){
		var me=this;
		
		var selection = me.themeAnalysisGrid.getSelectionModel().getSelection();//得到选中的记录
		
		if(0 == selection.length){
    		FHD.notification(FHD.locale.get('fhd.common.msgDel'),FHD.locale.get('fhd.common.prompt'));
    	}else{
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if(btn == 'yes') {
	 					var ids = [];
						for(var i = 0; i < selection.length; i++) {
					    	ids.push(selection[i].get('id'));
	 					}
	 					FHD.ajax({
	 						url : __ctxPath+ '/themeAnalysis/removeThemeAnalysisByIds.f',
	 						params : {
	 							themeAnalysisIds : ids
	 						},
	 						callback : function(data) {
	 							if (data) { 
	                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		 							me.themeAnalysisGrid.store.load();
	                            } else {
	                                FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
	                            }
	   						}
						});
					}
				}
			});
    	}
    },
    showThemeAnalysisDetail:function(id){
    	var me=this;
    	
    	me.themeAnalysisDetailPanel=Ext.create('FHD.view.comm.analysis.ThemeAnalysisPreview',{
    		themeAnalysisId:id
		});
		
		me.showWindow = Ext.create('FHD.ux.Window',{
			title:'主题分析详细信息',
			collapsible:false,
			maximizable:true
    	}).show();
		
		me.showWindow.add(me.themeAnalysisDetailPanel);
    },
    reloadData:function(){
		var me=this;
		
		me.themeAnalysisGrid.store.load();
	}
});