Ext.define('FHD.view.myallfolder.kpifolder.DeptRelaScGrid', {
    extend: 'FHD.ux.GridPanel',
    extraParams: {},
    layout: 'fit',
    border: false,
    paramObj: {},
    queryUrl: '',
    requires:['FHD.ux.TipColumn'],

    //构造函数
    initComponent: function () {
        var me = this;
        
        //var focuspanel = Ext.widget('container');
        
        //var statuspanel = Ext.widget('container');
        
        //创建记分卡基本信息对象
        me.createScBasicInfo();
        
        // 显示列
        me.cols = [{
                cls: 'grid-icon-column-header grid-statushead-column-header',
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'assessmentStatus',
                menuDisabled:true,
                sortable: true,
                width: 40,
                renderer: function (v) {
                    var display = "";
                    if (v == "icon-ibm-symbol-4-sm") {
                        display = FHD.locale.get("fhd.alarmplan.form.hight");
                    } else if (v == "icon-ibm-symbol-6-sm") {
                        display = FHD.locale.get("fhd.alarmplan.form.low");
                    } else if (v == "icon-ibm-symbol-5-sm") {
                        display = FHD.locale.get("fhd.alarmplan.form.min");
                    } else if (v == "icon-ibm-symbol-safe-sm") {
                        display = "安全";
                    } else {
                        v = "icon-ibm-underconstruction-small";
                        display = "无";
                    }
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle='' " +
                        "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
                }
            }, {
                cls: 'grid-icon-column-header grid-trendhead-column-header',
                menuDisabled:true,
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'directionstr',
                sortable: true,
                width: 40,
                renderer: function (v) {
                    var display = "";
                    if (v == "icon-ibm-icon-trend-rising-positive") {
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
                    } else if (v == "icon-ibm-icon-trend-neutral-null") {
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
                    } else if (v == "icon-ibm-icon-trend-falling-negative") {
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
                    }
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle='' " +
                        "class='" + v + "'  data-qtip='" + display + "'></div>";
                }
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.name'),
                dataIndex: 'name',
                sortable: true,
                flex: 2,
				renderer : function(value, meta, record) {
					var id = record.data.id;
					var name = value;
					meta.tdAttr = 'data-qtip="' + value + '"';
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"
							+ me.id + "').showBasicInfo('" + id
							+ "')\" >" + value + "</a>";
				}
            }, {
                header:  '所属人',
                dataIndex: 'owerName',
                sortable: true,
                flex: 2
            }, {
                header: '上级记分卡',
                dataIndex: 'parentName',
                sortable: true,
                flex: 2,
                renderer: function (value, meta, record) {
                    var id = record.data.parentKpiId;
                    var name = value;
                    if(null!=name){
                    	 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').detailAnalysis('" + id + "," + value + "')\" >" + value + "</a>";
                    }else{
                    	return "记分卡";
                    }
                }
            } ,{
                header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
                dataIndex: 'assessmentValue',
                sortable: true,
                flex: 1.1,
                align: 'right'
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
                dataIndex: 'dateRange',
                sortable: true,
                flex: 1,
                renderer: function (v) {
                    return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
                }
            } ,
             {
            header: FHD.locale.get('fhd.common.operate'),
            dataIndex: 'operate',
            sortable: false,
            align:'center',
            width: 65,
            renderer: function (value, metaData, record, colIndex, store, view) {
            	var id = record.data.id;
                var name = record.data.name;
	            return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').detailAnalysis('" + id + "," + name + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>";
            }
        }
            /*,{
                header: FHD.locale.get('fhd.sys.planMan.start'),
                xtype: 'tipcolumn',
                tips: {
                    items: [statuspanel],
                    renderer: function (cellIndex, rowIndex, tooltip) {
                        var data = me.items.items[0].store.data.items[rowIndex].data.kpistatus;
                        tooltip.setWidth(75);
                        tooltip.setHeight(30);
                        var htmlstr = "启用";
                        if (data == "0yn_y") {
                            htmlstr = "停用";
                        }
                        var p = statuspanel.items.items[0];
                        statuspanel.remove(p);
                        statuspanel.add({
                            border: false,
                            html: htmlstr
                        });
                    }
                },
                dataIndex: 'statusStr',
                sortable: false,
                flex: 0.5,
                renderer: function (v) {
                    var type = me.type;
                    if ("0yn_y" == v) {
                    	if($ifAnyGranted('ROLE_ALL_CATEGORY_ENABLE')) {
                    		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_n' + "')\" >" + "<image src='images/icons/state_ok.gif'/>" + "</a>";                    		
                    	}
                        else {
                        	return "<image src='images/icons/state_ok.gif'/>";
                        }
                    }
                    if ("0yn_n" == v || "" == v  || null == v) {
                    	if($ifAnyGranted('ROLE_ALL_CATEGORY_ENABLE')) {
                    		 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_y' + "')\" >" + "<image src='images/icons/state_error.gif'/>" + "</a>";
                    		
                    	}else {
                    		return "<image src='images/icons/state_error.gif'/>";
                    	}
                       
                    }
                }
            }, {
                header: '关注',
                xtype: 'tipcolumn',
                tips: {
                    items: [focuspanel],
                    renderer: function (cellIndex, rowIndex, tooltip) {
                        var data = me.items.items[0].store.data.items[rowIndex].data.kpifocus;
                        tooltip.setWidth(75);
                        tooltip.setHeight(30);
                        var htmlstr = "关注";
                        if (data == "0yn_y") {
                            htmlstr = "取消关注";
                        }
                        var p = focuspanel.items.items[0];
                        focuspanel.remove(p);
                        focuspanel.add({
                            border: false,
                            html: htmlstr
                        });
                    }
                },
                dataIndex: 'isFocus',
                sortable: false,
                flex: 0.5,
                renderer: function (v) {
                    var type = me.type;
                    if ("0yn_y" == v) {
                    	if($ifAnyGranted('ROLE_ALL_CATEGORY_ATTENTION')) {
                    		 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_n' + "')\" >" + "<image src='images/icons/kpi_heart.png' />" + "</a>";
                    		
                    	} else {
                    		 return "<image src='images/icons/kpi_heart.png' />";
                    	}
                       
                    }
                    if ("0yn_n" == v || "" == v || null == v) {
                    	if($ifAnyGranted('ROLE_ALL_CATEGORY_ATTENTION')){
                    		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_y' + "')\" >" + "<image src='images/icons/kpi_heart_add.png' />" + "</a>";
                    	} else {
                    		 return "<image src='images/icons/kpi_heart_add.png' />";
                    	}
                       
                    }
                }

            }*/,{
                dataIndex: 'id',
                invisible: true
            },
            {
            	dataIndex:'parentKpiId',
            	invisible:true
            },
            {
            	dataIndex:'timeperiod',
            	invisible:true
            },
            {
            	dataIndex:'kgrId',
            	invisible:true
            }

        ];

        Ext.apply(me, {
            url: me.queryUrl,
            border: false,
            checked: true,
            storeAutoLoad:false,
            cols: me.cols,
            extraParams: {
                id: me.paramObj.smid,
                year: FHD.data.yearId,
                month: FHD.data.monthId,
                quarter: FHD.data.quarterId,
                week: FHD.data.weekId,
                eType: FHD.data.eType,
                isNewValue: FHD.data.isNewValue,
                dataType: me.paramObj.dataType
            },
            tbarItems: [{
                    tooltip: "添加",
                    authority:'ROLE_ALL_CATEGORY_ADD',
                    iconCls: 'icon-add',
                    name: 'add_sc_same',
                    handler: function () {
                    	me.addFun('same');
                    },
                    text: "添加"
                },
                 {
                    tooltip: "添加下级",
                    authority:'ROLE_ALL_CATEGORY_ADD' +
                    		'',
                    disabled: true,
                    iconCls: 'icon-add',
                    name: 'add_sc_sub',
                    handler: function () {
                    	me.addFun('sub');
                    },
                    text: "添加下级"
                },
                 {
                    tooltip: "编辑",
                    authority:'ROLE_ALL_CATEGORY_EDIT',
                    name: 'edit_sc',
                    iconCls: 'icon-edit',
                    disabled: true,
                    handler: function () {
                        me.editFun();
                    },
                    text: "编辑"
                },
                 {
                    tooltip: "删除",
                    authority:'ROLE_ALL_CATEGORY_DELETE',
                    name: 'sc_del',
                    iconCls: 'icon-del',
                    disabled: true,
                    handler: function () {
                        me.delFun();
                    },
                    text: "删除"
                },
               
                		{
                            tooltip: FHD.locale.get('fhd.sys.planMan.start'),
                            btype:'op',
                            authority:'ROLE_ALL_CATEGORY_ENABLE',
                            iconCls: 'icon-plan-start',
                            name: 'sc_enable',
                            handler: function () {
                                me.enables("0yn_y");
                            },
                            disabled: true,
                            text: FHD.locale.get('fhd.sys.planMan.start')
                        }, {
                            tooltip: FHD.locale.get('fhd.sys.planMan.stop'),
                            btype:'op',
                            authority:'ROLE_ALL_CATEGORY_ENABLE',
                            iconCls: 'icon-plan-stop',
                            name: 'sc_disable',
                            handler: function () {
                                me.enables("0yn_n");
                            },
                            disabled: true,
                            text: FHD.locale.get('fhd.sys.planMan.stop')
                        }, {
                            tooltip: '关注',
                            btype:'op',
                            authority:'ROLE_ALL_CATEGORY_ATTENTION',
                            iconCls: 'icon-kpi-heart-add',
                            name: 'sc_focus',
                            handler: function () {
                                me.focus('0yn_y');
                            },
                            disabled: true,
                            text: '关注'
                        },

                        {
                            tooltip: '取消关注',
                            btype : 'op',
                            authority:'ROLE_ALL_CATEGORY_ATTENTION',
                            iconCls: 'icon-kpi-heart-delete',
                            name: 'sc_no_focus',
                            handler: function () {
                                me.focus('0yn_n');
                            },
                            disabled: true,
                            text: '取消关注'
                        },
                        {
			            	tooltip:   FHD.locale.get('fhd.formula.calculate'),
			            	btype:'op',
			            	authority:'ROLE_ALL_CATEGORY_CALCULATE',
			                iconCls: 'icon-calculator',
			                name: 'sc_calc',
			                handler: function() {
			                	me.recalc();
			                },
			                disabled: true,
			                text: FHD.locale.get('fhd.formula.calculate')
			                
			            }
            ]
        });

        me.callParent(arguments);
        me.addListerner();
    },
    
    //获得选择的记录
    getSelectionRecord:function(){
    	var me = this;
    	var selection = null;
    	var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if(length>0){
        	if (length >= 2) {
            	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "请选择一个记分卡");
                return;
            }else{
                selection = selections[0]; 
            }
        }
        return selection;
    },
    
    //添加同级或下级
    addFun:function(type){
    	var me = this;
        var length = me.getSelectionModel().getSelection().length;
        if(length==0){
        	if("same"==type){
        		me.reloadScBaicInfoData('undefined','',"category_root","记分卡",false);
        	}
        }else{
        	var selectedRecord = me.getSelectionRecord();
        	if(selectedRecord){
        		var scid = selectedRecord.get('id');
        		if("same"==type){//添加同级
            		//查询出父记分卡id和name
        			FHD.ajax({
        		        async: false,
        		        url: __ctxPath + '/kpi/category/findparentbyid.f',
        		        params: {
        		            id: scid
        		        },
        		        callback: function (data) {
        		        	var parentid = data.parentid;
        		        	var parentname = data.parentname;
        		        	if(!parentid){
        		        		parentid = "category_root";
        		        	}
        		        	if(!parentname){
        		        		parentname="记分卡";
        		        	}
        		        	me.reloadScBaicInfoData('undefined','',parentid,parentname,false);
        		        }
        		    });
        			
            	}else if("sub"==type){//添加下级
            		 var parentid = selectedRecord.get('id'); // 获得记分卡ID
                     var parentname = selectedRecord.get('name');//记分卡名称
                     me.reloadScBaicInfoData('undefined','',parentid,parentname,false);
            	}
        	}
        }
    	
    },
    
    //编辑
    editFun: function () {
        var me = this;
        var selectedRecord = me.getSelectionRecord();
        if(selectedRecord){
        	 var scid = selectedRecord.get('id'); // 获得记分卡ID
             var scname = selectedRecord.get('name');//记分卡名称
         	 me.reloadScBaicInfoData(scid,scname,'category_root','记分卡',true);
        }
    },
    
    //初始化记分卡基本信息数据
    reloadScBaicInfoData:function(scid,scname,parentid,parentname,editflag){
    	var me = this;
    	var paramObj = {
            	scid: scid, //记分卡ID
            	scname:scname,//记分卡名称
                parentid: parentid, //父记分卡ID
                parentname: parentname, //父记分卡名称
                editflag: editflag, //是否是编辑状态
            	navid:me.paramObj.navid,//树节点id
            	navtype:me.paramObj.navtype,//导航类型
            	treeid:me.paramObj.treeid,//树id
            	empType :  'dept_emp'
            };
        me.scBasicInfoComp.initParam(paramObj);
        me.reRightLayout(me.scBasicInfoComp);
        me.scBasicInfoComp.reloadData();
    },
    
    destroy:function(){
    	if(this.scBasicInfoComp){
    		if(this.scBasicInfoComp.navigationBar) {
    			this.scBasicInfoComp.navigationBar.destroy();
    		}    		
    		this.scBasicInfoComp.destroy();
    	}
    	this.callParent(arguments);
    },
    
    //创建记分卡基本信息
    createScBasicInfo:function(){
    	var me = this;
    	if(!me.scBasicInfoComp){
    		me.scBasicInfoComp = Ext.create('FHD.view.myallfolder.kpifolder.DeptScBasicInfo',{
    			undo:me.undo,
    			navData: me.navData
    		});
    	}
    },

    //初始化参数
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    //记分卡详细分析
    detailAnalysis: function (value) {
        var me = this;
        var para = value.split(",");
        var id = para[0];
        var name = para[1];
		var param = {
				scid : id,
				name:name,
				type:"departmentfolder"
		    }
		if(null!=me.detailAnaysisPanel){
			me.removePanel(me.detailAnaysisPanel);
		}
		var data = [];
		for(i=0;i<me.navData.length;i++) {
			data.push(
			me.navData[i]
			);
		};
		me.detailAnaysisPanel = Ext.create('FHD.view.kpi.homepage.ScDetailAnalysis',{
		 paramObj : param,
		reRightLayout:function(p){
			me.reRightLayout(p);
		},
		navData: data,
		go:function() {						
            me.detailAnaysisPanel.card.setActiveItem(me.tabpanel);
            me.detailAnaysisPanel.scKpiGrid.store.load();
            me.detailAnaysisPanel.newNav.renderHtml(me.detailAnaysisPanel.id + 'DIV',me.detailAnaysisPanel.navData);
		  }
	   });
		me.reRightLayout(me.detailAnaysisPanel);
    },

    //删除
    delFun: function () {
        var me = this;
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { // 确认删除
                	var selectionRecord = me.getSelectionRecord();
                    if(selectionRecord){
	                    FHD.ajax({
	                       	url: __ctxPath + '/kpi/category/removecategory.f',
	                        params: {
	                            id: selectionRecord.get('id')
	                        },
	                        callback: function (ret) {
	                            if (ret && ret.result) {
	                                if (ret.result == "cascade") {
	                                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '存在下级,不能删除!');
	                                } else if (ret.result == "success") {
	                                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
	                                    me.store.load();
	                                }
				                }
	                        }
	                    });
                    }
                }
            }
        });
    },

    //启用或禁用
    enables: function (enable) {
   		var me = this;
    	var paraobj = {};
        paraobj.enable = enable;
        paraobj.scids = [];
        var selections = me.getSelectionModel().getSelection();
        Ext.Array.each(selections,
        function(item) {
            paraobj.scids.push(item.get("id"));
        });
    	
    	if(me.body != undefined){
        	if('0yn_y'==enable){
	        	me.body.mask("启用中...","x-mask-loading");
        	}else{
	        	me.body.mask("停用中...","x-mask-loading");
        	}
        }
    	FHD.ajax({
            params: {
                scItems: Ext.JSON.encode(paraobj)
            },
            url: __ctxPath + '/kpi/category/mergecategoryenables.f',
            callback: function (data) {
            	if (data && data.success) {
                    me.store.load();
                    if(me.body != undefined){
                    	me.body.unmask();
                    }
                }
            }
        });
    },
    
    recalc: function () {
        var me = this;
        var objArr = [];
        var selections = me.getSelectionModel().getSelection();
        for (var i = 0; i < selections.length; i++) {
            var obj = {};
            obj.type='category';
            obj.id = selections[i].get('id');
            obj.timeperiod = selections[i].get('timeperiod')==null?'':selections[i].get('timeperiod');
            obj.gatherResultId = selections[i].get('kgrId')==null?'':selections[i].get('kgrId');
            objArr.push(obj);
        }
        if (me.body != undefined) {
            me.body.mask("计算中...", "x-mask-loading");
        }
        FHD.ajax({
            url: __ctxPath + '/formula/deptcategoryformulacalculate.f',
            params: {
                items: Ext.JSON.encode(objArr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.store.load();
                    if (me.body != undefined) {
                        me.body.unmask();
                    }
                }
            }
        });
    },

    //关注或取消关注
    focus: function (focus) {
		var me = this;
		var paraobj = {};
        paraobj.focus = focus;
        paraobj.scids = [];
        var selections = me.getSelectionModel().getSelection();
        Ext.Array.each(selections,
        function(item) {
            paraobj.scids.push(item.get("id"));
        });
		
        if(me.body != undefined){
         	if('0yn_y'==focus){
 	        	me.body.mask("关注中...","x-mask-loading");
         	}else{
 	        	me.body.mask("取消关注中...","x-mask-loading");
         	}
         }
         
    	FHD.ajax({
            params: {
                 scItems: Ext.JSON.encode(paraobj)
            },
            url: __ctxPath + '/kpi/category/mergecategoryfocuss.f',
            callback: function (data) {
            	if (data && data.success) {
                    me.store.load();
                    if(me.body != undefined){
                    	me.body.unmask();
                    }
                }
            }
        });
    },
    
    //重新布局
    reRightLayout:function(){
    	var me = this;
    },
    
    //监听按钮
    addListerner: function () {
        var me = this;
        // 选择记录发生改变时改变按钮可用状态
        me.store.on('load',function(){
    		  me.setBtnState();
    	});
        me.on('selectionchange', function () {
        	  me.setBtnState();
        });
    },
    //改变按钮状态
    setBtnState: function(){
    	var me = this;
    	if (me.down("[name='add_sc_sub']")) {
    		me.down("[name='add_sc_sub']").setDisabled(me.getSelectionModel().getSelection().length === 0);
    	}
        if (me.down("[name='edit_sc']")) {
            me.down("[name='edit_sc']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_del']")) {
            me.down("[name='sc_del']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_disable']")) {
            me.down("[name='sc_disable']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_enable']")) {
            me.down("[name='sc_enable']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_focus']")) {
            me.down("[name='sc_focus']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_no_focus']")) {
            me.down("[name='sc_no_focus']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='sc_calc']")) {
        	me.down("[name='sc_calc']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
            
    },
    
    //重新加载列表数据
    reloadData: function () {
        var me = this;
        me.queryUrl = __ctxPath + "/myfolder/finddeptrelasmsc.f";
        if (me.paramObj != undefined) {
            me.store.proxy.url = me.queryUrl;
            me.store.proxy.extraParams.dataType = me.paramObj.dataType;
            me.store.load();
        }
    },
	showBasicInfo: function(id) {
		var me = this;
	    me.scBasicInfoForm = Ext.create('FHD.view.kpi.cmp.sc.ScBasicInfoForm', {});
        var paramObj = {
            scid: id //目标ID
        };
        me.scBasicInfoForm.initParam(paramObj);
        me.scBasicInfoForm.reloadData();
        me.window = Ext.create('FHD.ux.Window', {
            title: '目标基本信息',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true
        });
        me.window.show(); 
        me.window.add(me.scBasicInfoForm);    
     
	}

})