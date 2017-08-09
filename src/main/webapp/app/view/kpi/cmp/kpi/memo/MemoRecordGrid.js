Ext.define('FHD.view.kpi.cmp.kpi.memo.MemoRecordGrid', {
    extend: 'FHD.ux.GridPanel',
	height : 180,
    border: false,
    autoScroll:true,
    requires: [
               
              ],
    /**
     * 删除
     */
    memoDelFun: function () {
        var me = this;
        var selection = me.getSelectionModel().getSelection();//得到选中的记录
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                	var ids = [];
    				for(var i=0;i<selection.length;i++){
    					ids.push(selection[i].get('id'));
    				}
                    FHD.ajax({
                        url: __ctxPath + "/kpi/kpimemo/removeMemobyId.f",
                        params: {
                            ids:ids.join(',')
                        },
                        callback: function (data) {
                            if (data) {
                            	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
                            }
                        }
                    });
                }
            }
        });
    },
 
    /**
     * 添加
     */
    memoAddFun:function(){
    	var me = this;
    	me.memomainpanel.memopanel.clearFormData();
        me.memomainpanel.memopanel.memoId=null;
    },
    /**
     * 编辑
     */
    memoEditFun:function(){
    	var me = this;
        var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if (length > 0) {
            if (length >= 2) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.kpi.kpi.prompt.editone'));
                return;
            } else {
                var selection = selections[0]; //得到选中的记录
                var id = selection.get('id'); //获得ID
                var theme = selection.get('theme');
            	//加载form数据
                me.memomainpanel.memopanel.formLoad(id);
            }
        } else {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择一条指标.');
            return;
        }
    	
    },
    
    onClick: function(id) {
    	var me = this;
    	var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if (length == 1) {
            var selection = selections[0]; //得到选中的记录
            var id = selection.get('id'); //获得ID
            
            me.memomainpanel.memopanel.memoId = id;
        	//加载form数据
            me.memomainpanel.memopanel.formLoad(id);
        } else {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择一条指标.');
            return;
        }
         
    },
    //列表监听事件
    addListerner: function () {
        var me = this;
        me.on('selectionchange', function () {
            if (me.down("[name='memo_del']")) {
                me.down("[name='memo_del']").setDisabled(me.getSelectionModel().getSelection().length === 0);
            }

        }); //选择记录发生改变时改变按钮可用状态

    },

    reloadData: function() {
    	var me = this;
    	me.store.load();
    },
    initComponent: function() {
        var me = this;
        me.kpiid = null;
        Ext.apply(me, {
        	checked: true,
        	storeAutoLoad:false,
        	searchable:false,
        	extraParams:{
	    			kpiid : ''
	    	},
            tbarItems: [{
            	text:'添加',
                iconCls: 'icon-add',
                name: 'memo_add',
                handler: function () {
                    me.memoAddFun();
                }
            },  '-', {
            	text:'删除',
                name: 'memo_del',
                iconCls: 'icon-del',
                disabled: true,
                handler: function () {
                  me.memoDelFun();
                }
            }

            ],
            cols:[
       		     {
	            	header: 'id' ,
	            	dataIndex: 'id',
	            	sortable: true,
	            	flex : 1,
	            	invisible : true
	            	},{  
	       		        header: '重要性', 
	       		        dataIndex: 'important', 
	       		        flex : 0.2,
	       		        renderer: function (v, rowIndex, cellIndex) {
		                	if("0alarm_startus_h"==v){
		                		return "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_high.gif' />";
		                	}
		                	if("0alarm_startus_l"==v){
		                		return "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_low.gif' />";		                		
		                	}
		                	if("0alarm_startus_n"==v){
		                		return "<image src='"+__ctxPath+"/images/icons/icon_note.gif' />";
		                	}
	               		 }
					},{  
	       		        header: '主题', 
	       		        dataIndex: 'theme', 
	       		        flex : 0.3
					},
	    			{
		    			header: '创建时间',
		    			dataIndex: 'operTime',
		    			sortable: true,
		    			flex : 0.5
	    			}
	    		
    			]
        });
        
        me.on('selectionchange', function ( model,  selected,  eOpts ) {
        	
            if(selected.length==1){
            	var id = model.getSelection()[0].get('id');
            	me.memomainpanel.memopanel.memoId = id;
	        	//加载form数据
	            me.memomainpanel.memopanel.formLoad(id);
            }
            if(selected.length==0||selected.length>1){
            	me.memomainpanel.memopanel.clearFormData();
            	me.memomainpanel.memopanel.memoId = null;
        	}
            
        });
        
        me.callParent(arguments);
         
        me.store.on('load',function(store,  records,  successful,  eOpts){
        	var  model = me.getSelectionModel();
   			model.select(0);
        });        
        me.addListerner();
    }

});