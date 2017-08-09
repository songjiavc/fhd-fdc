Ext.define('FHD.view.comm.analysis.ThemeAnalysisCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.themeanalysiscardpanel',
    
    activeItem: 0,
    border:false,
    autoScroll:true,
    requires: [
       'FHD.view.comm.analysis.ThemeLayoutPanel',
       'FHD.view.comm.analysis.ThemeDataSourcePanel',
       'FHD.view.comm.analysis.ThemeChartPanel'
    ],
    
    tbar: {
        name: 'theme_analysis_card_topbar',
        items: [
	        {
	            text: '选择布局',
	            iconCls: 'icon-001',
	            name: 'theme_analysis_card_layout_btn_top',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.setBtnState(0);
	            		themeanalysiscardpanel.navBtnHandler(this.up('panel'), 0);
	            	}
	            }
	        },
	        '<img src="'+__ctxPath+'/images/icons/show_right.gif">',
	        {
	            text: '选择数据源',
	            iconCls: 'icon-002',
	            name: 'theme_analysis_card_datasource_btn_top',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.setBtnState(1);
	            		themeanalysiscardpanel.navBtnHandler(this.up('panel'), 1);
	            	}
	            }
	        },
	        '<img src="'+__ctxPath+'/images/icons/show_right.gif">',
	        {
	            text: '选择图表',
	            iconCls: 'icon-003',
	            name: 'theme_analysis_card_chart_btn_top',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.setBtnState(2);
	            		themeanalysiscardpanel.navBtnHandler(this.up('panel'), 2);
	            	}
	            }
	        }
	    ]
    },
    bbar: {
        name: 'theme_analysis_card_bbar',
        items: [
	        '->', 
	        {
	            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
	            name: 'theme_analysis_card_undo_btn' ,
	            iconCls: 'icon-operator-home',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.undo();
	            	}
	            }
	        },
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.back"),//上一步按钮
	            name: 'theme_analysis_card_pre_btn' ,
	            iconCls: 'icon-operator-back',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.back();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.last"),//下一步按钮
	            name: 'theme_analysis_card_next_btn' ,
	            iconCls: 'icon-operator-next',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.last();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.common.save"),//保存按钮
	            name: 'theme_analysis_card_finish_btn' ,
	            iconCls: 'icon-control-stop-blue',
	            handler: function () {
	            	var themeanalysiscardpanel = this.up('themeanalysiscardpanel');
	            	if(themeanalysiscardpanel){
	            		themeanalysiscardpanel.finish();
	            	}
	            }
	        }
	    ]
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.themelayoutpanel = Ext.widget('themelayoutpanel',{
        	businessId:me.businessId,
        	editflag:me.editflag
        });
        me.themedatasourcepanel = Ext.widget('themedatasourcepanel',{
        	businessId:me.businessId,
        	editflag:me.editflag
        });
        me.themechartpanel = Ext.widget('themechartpanel',{
        	businessId:me.businessId,
        	editflag:me.editflag
        });
        
        Ext.applyIf(me, {
            items: [
                me.themelayoutpanel,
                me.themedatasourcepanel,
                me.themechartpanel
            ]
        });

        me.callParent(arguments);
    },
    /**
     * 返回按钮事件
     */
    undo:function(){
    	var me = this;
    	var themeanalysismainpanel = this.up('themeanalysismainpanel');
    	if(themeanalysismainpanel){
    		themeanalysismainpanel.navBtnHandler(0);
    		//刷新主题分析列表
    		//themeanalysismainpanel.themeanalysislist.themeAnalysisGrid.store.load();
    		themeanalysismainpanel.themeanalysislist.reloadData();
    	}
    },
    /**
     * 上一步按钮事件
     */
    back:function(){
    	var me = this;
    	
    	var activePanel = me.getActiveItem();
        var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel)-1;
        
        if(0 == index){
 	    	//选择布局保存刷新
        	me.setBtnState(0);
        	me.navBtnHandler(me,0);
			//me.setActiveItem(0);
			me.navBtnState();
        	me.themelayoutpanel.saveData();
 	    }else if(1 == index){
 	    	//选择数据源保存刷新
 	    	me.setBtnState(1);
 	    	me.down('[name=theme_analysis_card_datasource_btn_top]').setDisabled(false);
 	    	me.navBtnHandler(me,1);
			//me.setActiveItem(1);
			me.navBtnState();
            me.themedatasourcepanel.saveData();
 	    }else if(2 == index){
 	    	//选择图表保存刷新
 	    	me.setBtnState(2);
 	    	me.down('[name=theme_analysis_card_chart_btn_top]').setDisabled(false);
 	    	me.navBtnHandler(me,2);
			//me.setActiveItem(2);
			me.navBtnState();
 	    	me.themechartpanel.saveData();
 	    }
    },
    /**
     * 下一步按钮事件
     */
    last:function(){
    	var me = this;
    	
    	var activePanel = me.getActiveItem();
        var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        
        if(0 == index){
 	    	//选择布局保存
			if(me.themelayoutpanel.saveData()){
				me.down('[name=theme_analysis_card_datasource_btn_top]').setDisabled(false);
				me.setBtnState(1);
				me.navBtnHandler(me,1);
				me.navBtnState();
				//选择数据源刷新
				me.themedatasourcepanel.reloadData();
			}
 	    }else if(1 == index){
 	    	me.setBtnState(2);
 	    	me.down('[name=theme_analysis_card_chart_btn_top]').setDisabled(false);
 	    	me.navBtnHandler(me,2);
			me.navBtnState();
			//选择数据源保存
            me.themedatasourcepanel.saveData();
            //选择图表刷新
            me.themechartpanel.reloadData();
 	    }
    },
    /**
     * 完成按钮事件
     */
    finish:function(){
    	var me = this;
    	
        var activePanel = me.getActiveItem();
        var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        
        if(0 == index){
 	    	//选择布局保存刷新
        	me.themelayoutpanel.saveData();
 	    }else if(1 == index){
 	    	//选择数据源保存刷新
            me.themedatasourcepanel.saveData();
 	    }else if(2 == index){
 	    	//选择图表保存刷新
 	    	me.themechartpanel.saveData();
 	    }
    },
    /**
     * 提交按钮事件
     */
    submit:function(){
    	var me = this;
    	
    	//判断流程列表不能为空
    	var count = me.themeanalysisdatasourceform.themeanalysisGrid.store.getCount();
    	if(count == 0){
    		FHD.notification('评价计划未选择评价范围!',FHD.locale.get('fhd.common.prompt'));
    		return;
    	}
    	
    	var validateFlag = false;
		var count = me.themeanalysisdatasourceform.themeanalysisGrid.store.getCount();
		for(var i=0;i<count;i++){
			var item = me.themeanalysisdatasourceform.themeanalysisGrid.store.data.get(i);
			if(typeof(item.get('isPracticeTest')) != "string" && typeof(item.get('isSampleTest')) != "string" && !item.get('isPracticeTest') && !item.get('isSampleTest') && item.get('isPracticeTest') == item.get('isSampleTest')){
				validateFlag = true;
			}
		}
		if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '穿行测试和抽样测试不能全部为否!');
 			return false;
 		}
    	
    	//保存流程列表
    	var jsonArray=[];
		var rows = me.themeanalysisdatasourceform.themeanalysisGrid.store.getModifiedRecords();
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		FHD.ajax({
		    url : __ctxPath+ '/icm/assess/mergethemeanalysisRelaProcessBatch.f',
		    params : {
		    	themeanalysisId:me.businessId,
		    	modifiedRecord:Ext.encode(jsonArray)
			},
			callback : function(data) {
				if (data) {
					Ext.MessageBox.show({
			            title: '提示',
			            width: 260,
			            msg: '提交后将不能修改，您确定要提交么?',
			            buttons: Ext.MessageBox.YESNO,
			            icon: Ext.MessageBox.QUESTION,
			            fn: function (btn) {
			                if (btn == 'yes') {
            					//所有按钮不可用，提交成功后跳转到列表
            			    	me.down('[name=theme_analysis_card_pre_btn]').setDisabled(true);
            			        me.down('[name=theme_analysis_card_next_btn]').setDisabled(true);
            			        me.down('[name=theme_analysis_card_finish_btn]').setDisabled(true);
            			        me.down('[name=theme_analysis_card_submit_btn]').setDisabled(true);
            			        	
            					//提交工作流
            			    	FHD.ajax({
            				        url: __ctxPath + '/icm/assess/themeanalysisDraft.f',
            				        async:false,
            				        params: {
            				        	businessId: me.businessId,
            				        	executionId: me.executionId
            				        },
            				        callback: function (data) {
            				        	if(data){
            				        		var themeanalysismainpanel = me.up('themeanalysismainpanel');
            				            	if(themeanalysismainpanel){
            				            		themeanalysismainpanel.navBtnHandler(0);
            				            	}else{
            				            		var themeanalysisbpmone = me.up('themeanalysisbpmone');
            					            	if(themeanalysisbpmone.winId){
            					            		Ext.getCmp(themeanalysisbpmone.winId).close();
            					            	}
            				            	}
            				        	}
            				        }
            			    	});
            				}
            			}
            		});
                }
            }
        });
    },
    /**
     * 设置导航按钮的事件函数
     * @param {panel} cardPanel cardpanel面板
     * @param index 面板索引值
     */
    navBtnHandler: function (cardPanel, index) {
 	   	var me = this;
 	   	
 	    cardPanel.setActiveItem(index);
 	    me.navBtnState();
 	    if(0 == index){
 	    	//选择布局刷新
            me.themelayoutpanel.loadData(me.businessId,me.editflag);
 	    }else if(1 == index){
 	    	//选择数据源刷新
            me.themedatasourcepanel.reloadData(me.businessId,me.editflag);
 	    }else if(2 == index){
 	    	//选择图表刷新
            me.themechartpanel.loadData(me.businessId,me.editflag);
 	    }
    },
    /**
     * 设置上一步和下一步按钮的状态
     */
    navBtnState:function(){
    	var me = this;
    	var layout = me.getLayout();
    	me.down('[name=theme_analysis_card_pre_btn]' ).setDisabled(!layout.getPrev());
        me.down('[name=theme_analysis_card_next_btn]' ).setDisabled(!layout.getNext());
        //me.down('[name=theme_analysis_card_finish_btn]').setDisabled(!layout.getNext());
    },
    /**
     * 设置导航按钮的选中或不选中状态
     * @param index,要激活的面板索引
     */
    setBtnState: function (index) {
    	var me=this;
    	
        var k = 0;
        var topbar = me.down('[name=theme_analysis_card_topbar]');
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
    	if(first){
    		//修改
    		me.down('[name=theme_analysis_card_layout_btn_top]').setDisabled(v);
    		me.down('[name=theme_analysis_card_datasource_btn_top]').setDisabled(v);
    		me.down('[name=theme_analysis_card_chart_btn_top]').setDisabled(v);
    	}else{
    		//新增
    		me.down('[name=theme_analysis_card_layout_btn_top]').setDisabled(first);
    		me.down('[name=theme_analysis_card_datasource_btn_top]').setDisabled(v);
    		me.down('[name=theme_analysis_card_chart_btn_top]').setDisabled(v);
    	}
    	me.setBtnState(0);
    	me.navBtnHandler(me,0);
    },
    /**
     * 初始化tbar和bbar按钮状态
     */
    setInitBtnState:function(editflag){
    	var me = this;
    	
		if(editflag){
			//编辑主题分析
        	me.setNavBtnEnable(false,true);
        }else{
        	//添加主题分析
        	me.setNavBtnEnable(true,false);
        }
    },
    loadData:function(businessId,editflag){
    	var me=this;
    	
    	me.businessId = businessId;
    	me.editflag = editflag;
    	me.setInitBtnState(editflag);
    },
    reloadData:function(){
    	var me=this;
    	
    }
});