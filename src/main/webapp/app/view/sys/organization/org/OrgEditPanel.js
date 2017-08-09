/**
 * 机构基本信息FORM面板
 * 
 * @author 金鹏祥
 */
Ext.define('FHD.view.sys.organization.org.OrgEditPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.orgEditPanel',
    title:'机构信息',
    orgname:null,
    orgcode:null,
    getOrgcodeButton:null,
    parentOrg:null,
    orgLevel:null,
    /**
     * 加载form数据
     */
    formLoad : function(){
    	var me = this;
    	if(typeof(me.orgtreeId) != 'undefined') {
    		me.form.load({
    	        url:'sys/organization/findorgbyid.f',
    	        params:{id:me.orgtreeId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        	var formValue = form.getValues();
    	        	me.orgtreeId = '';
    	        	if(action.result.data.parentOrgStr){
						me.parentOrg.setValues(Ext.JSON.decode(action.result.data.parentOrgStr));
					}
    	        }
    	    });
    	}
    },
    /**
     * 添加下级/同级机构，给‘上级机构’组件赋值
     */
    parentOrgLoad : function(){
    	var me = this;
    	if(typeof(me.orgtreeId) != 'undefined') {
    		me.form.load({
    	        url:'sys/organization/findparentorgbynodeId.f',
    	        params:{
    	        	id:me.orgtreeId,
    	        	isSameLevel:me.isSameLevel
        		},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        	var formValue = form.getValues();
    	        	if(action.result.data.parentOrgStr){
						me.parentOrg.setValues(Ext.JSON.decode(action.result.data.parentOrgStr));
					}
    	        	if('Z'==action.result.data.orgtype){
		    			me.orgType.setValue('0orgtype_d');
		    		}else if('F'==action.result.data.orgtype){
		    			me.orgType.setValue('0orgtype_sd');
		    		}
    	        }
    	    });
    	}
    },
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        me.id = 'orgEditPanel';
        var newFlag;//标记是否为新增
        var isSameLevel;
        var orgEdit_saveUrl = 'sys/organization/saveOrgInfo.f';//保存
        var queryOrgTypeUrl = 'sys/organization/findOrgAll.f';//选择模版下拉菜单
        var queryOrgForumUrl = 'sys/organization/findOrgForumAll.f';//业务板块下拉菜单
        var queryRegionUrl = 'sys/organization/findOrgRegionAll.f';//区域下拉菜单
        var queryorgStateUrl = 'sys/organization/findorgstates.f';//机构状态下拉菜单
        
        me.typeStore = Ext.create('Ext.data.Store', {//机构类型store
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryOrgTypeUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});

    	var forumStore = Ext.create('Ext.data.Store', {//业务板块store
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryOrgForumUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	
    	var regionStore = Ext.create('Ext.data.Store', {//区域store
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryRegionUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	
    	var statusStore = Ext.create('Ext.data.Store', {//机构状态store
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryorgStateUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
    	//机构名称
    	me.orgname=Ext.create('Ext.form.TextField', {
    	    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgname')+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    margin: '7 30 3 30',
    	    name:'orgname',
    	    columnWidth:.5
    	});
    	//机构编号
    	me.orgcode=Ext.create('Ext.form.TextField', {
    	    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgcode')+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    margin: '7 30 3 30',
    	    name:'orgcode',
    	    columnWidth:.35
    	});
    	//自动生成机构编号按钮
    	me.getOrgcodeButton=Ext.create('Ext.button.Button', {
    	    text: '自动生成',
    	    margin: '7 30 3 30',
    	    columnWidth:.15,
    	    listeners:{
                click:function(){
                	var parentOrgId=eval(me.parentOrg.getValue())[0].id;
					jQuery.ajax({
						type: "POST",
						url: __ctxPath +"/sys/organization/getOrgcode.f",
						data: "parentOrgId="+parentOrgId,
						success: function(orgcode){
							me.orgcode.setValue(orgcode);
						},
						error: function(){
							FHD.alert("系统出错。");
						}
					});
                }
            }
    	});
    	//上级机构
		me.parentOrg = Ext.create('Ext.ux.form.OrgEmpSelect',{
			fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.parentorgname'),
			type : 'dept',
			multiSelect : false,
			rootVisible:true,
			subCompany:true,
			id:'parentOrgStrId',
			name:'parentOrgStr',
			margin: '7 30 3 30',
			columnWidth:.5,
			growMin: 75,
			growMax: 120,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true
		});
    	//机构层级
    	me.orgLevel=Ext.create('Ext.form.TextField', {
    	    fieldLabel:'机构层级',
    	    allowBlank:false,//不允许为空
    	    disabled:true,//禁止用户输入
    	    margin: '7 30 3 30',
    	    name:'orgLevel',
    	    columnWidth:.5
    	});
    	// 机构类型
    	 me.orgType = Ext.create('Ext.form.field.ComboBox', {
		    store: me.typeStore,
		    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgtype')+'<font color=red>*</font>',
		    emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
		    allowBlank:false,
		    editable:false,
//		    disabled: true,
		    margin: '7 30 3 30',
		    queryMode: 'local',
		    name:'orgType',
		    displayField: 'text',
		    valueField: 'id',
		    triggerAction :'all',
		    columnWidth:.5
		});
    	//业务板块
		var forum = Ext.create('Ext.form.field.ComboBox', {
		    store: forumStore,
		    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.forum'),
		    emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示  
		    editable:false,
		    margin: '7 30 3 30',
		    queryMode: 'local',
		    name:'forum',
		    displayField: 'text',
		    valueField: 'id',
		    triggerAction :'all',
		    columnWidth:.5
		});
    	//机构地址
    	var orgAddress=Ext.create('Ext.form.TextField', {
		    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgaddress'),
		    margin: '7 30 3 30',
		    name:'address',
		    id:'addressId',
		    columnWidth:.5
		});
    	//区域
    	var orgRegion = Ext.create('Ext.form.field.ComboBox', {
			 store: regionStore,
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.region'),
			 emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示  
			 editable:true,
			 margin: '7 30 3 30',
		     queryMode: 'local',
			 name:'region',
			 displayField: 'text',
			 valueField: 'id',
			 triggerAction :'all',
			 columnWidth:.5
		});
    	//电子邮件
    	var orgEmail=Ext.create('Ext.form.TextField', {
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.email'),
			 margin: '7 30 3 30',
			 name:'email',
			 id:'emailId',
			 columnWidth:.5
		});
    	//邮政编码
    	var orgZipcode=Ext.create('Ext.form.TextField', {
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgzipcode'),
			 margin: '7 30 3 30',
			 name:'zipcode',
			 id:'zipcodeId',
			 columnWidth:.5
		});
    	//联系人
    	var orgLinkMan=Ext.create('Ext.form.TextField', {
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.linkMan'),
			 margin: '7 30 3 30',
			 name:'linkMan',
			 id:'linkManId',
			 columnWidth:.5
		});
    	//联系电话
    	var orgLinkTel=Ext.create('Ext.form.TextField', {
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.linktel'),
			 margin: '7 30 3 30',
			 name:'linkTel',
			 id:'linkTelId',
			 columnWidth:.5
		});
    	//网站地址
    	var orgWeburl=Ext.create('Ext.form.TextField', {
			 fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.weburl'),
			 margin: '7 30 3 30',
			 name:'weburl',
			 id:'weburlId',
			 columnWidth:.5
		});
    	//机构状态
    	 var orgStatus = Ext.create('Ext.form.field.ComboBox', {
		    store: statusStore,
		    fieldLabel: FHD.locale.get('fhd.sys.orgstructure.org.orgstatus'),
		    emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
		    editable:false,
		    margin: '7 30 3 30',
		    value:'1',
		    queryMode: 'local',
		    name:'orgStatus',
		    displayField: 'text',
		    valueField: 'id',
		    triggerAction :'all',
		    columnWidth:.5
		});
    	//开始时间
    	var startTime=Ext.create('Ext.form.field.Date', {
    	    fieldLabel: FHD.locale.get('fhd.sys.planEdit.startTime'),
    	    margin: '7 30 3 30',
    	    name:'startDataStr',
    	    format:"Y-m-d",
    	    columnWidth:.25
    	});
    	//结束时间
    	var endTime=Ext.create('Ext.form.field.Date', {
    	    fieldLabel: FHD.locale.get('fhd.sys.planEdit.entTime'),
    	    margin: '7 30 3 30',
    	    name:'endDataStr',
    	    format:"Y-m-d",
    	    columnWidth:.25
    	});
    	//排列顺序
    	var orgSn=Ext.create('Ext.form.field.Number', {
			fieldLabel: '排列顺序',
			//allowBlank:false,//不允许为空
			margin: '7 30 3 30',
			name:'snStr',
			//id:'snId',
			columnWidth:.5
		});
    	//备注
    	var dutyRemark=Ext.create('Ext.form.field.TextArea', {
    	    fieldLabel: FHD.locale.get('fhd.sys.duty.dutyremark'),
    	    margin: '7 30 3 30',
    	    name:'remark',
    	    id:'remarkId',
    	    columnWidth:.5
    	});
    	
    	//隐藏域
    	me.idfiled = Ext.widget('hiddenfield',{
            name:"id",
            value:''
	    });
    	
    	var tbar =[//菜单项
    	           '->',
    	           {text : "保存",iconCls: 'icon-save',id:'orgsave1${param._dc}', handler:save, scope : this}];
    	var bbar =[//菜单项
    	           '->',
    	           {text : "保存",iconCls: 'icon-save',id:'orgsave2${param._dc}', handler:save, scope : this}];
        //保存
    	function save(){
    		var orgGridPanel = Ext.getCmp('tabpanelorgGridPanel');
    		var tabPanel = Ext.getCmp('tabPanel');
    		var treePanel = Ext.getCmp('treePanel');
    		var form = me.getForm();
    		
    		if(form.isValid()){
	    		if(orgGridPanel.isAdd){//新增保存
	    			FHD.submit({
	    				form:form,
	    				url:orgEdit_saveUrl,
	    				callback:function(objectMaps){
	    					//选中新添加的节点
	    					 var node = {id:objectMaps.data.id,text:objectMaps.data.text,type:objectMaps.data.type,leaf:true,keys:objectMaps.data.keys,expanded:objectMaps.data.expanded};
                             if(treePanel.currentNode.isLeaf()){
                            	 treePanel.currentNode.data.leaf = false;
                             }
                             treePanel.currentNode.appendChild(node);
                             treePanel.currentNode.expand();
                             treePanel.getSelectionModel().select(treePanel.currentNode.lastChild);
                             treePanel.itemclickTree(treePanel.currentNode.lastChild);
	    					orgGridPanel.store.load();
	    					tabPanel.setActiveTab(3);
	    				}
	    				});	
	    		}else{
	    			FHD.submit({
	    				form:form,
	    				url:orgEdit_saveUrl + '?id=' + me.idfiled.getValue(),
	    				callback:function(objectMaps){
	    					orgGridPanel.store.load();
	    					tabPanel.setActiveTab(0);
	    					treePanel.store.load();
	    					
	    				}
	    			});	
	    		}
    		}
    	}
    	
        Ext.applyIf(me, {
            autoScroll: true,
            border: false,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "5 5 5 5",
            tbar:tbar,
            items: [{
                xtype: 'fieldset',//基本信息fieldset
                autoHeight: true,
                autoWidth: true,
                collapsible: true,
                defaults: {
                    margin: '3 30 3 30',
                    labelWidth: 100
                },
                layout: {
                    type: 'column'
                },
                title: "基础信息",
                items:[
					me.orgname,me.orgcode, me.getOrgcodeButton, me.parentOrg, me.orgLevel, me.orgType, orgAddress, orgLinkMan,orgLinkTel, 
					orgWeburl, orgSn, orgEmail, forum, orgRegion, orgZipcode, orgStatus, startTime, endTime, dutyRemark,me.idfiled]
            }]
            
        });
        
        me.callParent(arguments);
        
    },
    load:function(){
    	var me = this;
    	if(typeof(me.orgtreeId) != 'undefined'){
        	//编辑时加载form数据
            me.formLoad();
    	}
    }
});