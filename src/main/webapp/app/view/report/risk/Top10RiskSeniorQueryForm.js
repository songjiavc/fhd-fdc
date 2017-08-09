/**
 * 应对方案编辑表单
 */
Ext.define('FHD.view.report.risk.Top10RiskSeniorQueryForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.top10riskseniorqueryform',
    
    /**
     * 常量区
     */
    companyUrl:__ctxPath + "/risk/findHierarchyCompany.f",	//集团公司树
    listUrl:'/riskhistoryversion/findAllVersion.f',   		//版本url
    
    /**
     * 变量区
     */
    owner:null,	//窗口的拥有者对象
    
    autoScroll: true,
	collapsed : false,
	collapsable : false,
    border : false,

    // 初始化方法
    initComponent: function() {
        var me = this;

       /**
        *  风险版本列表
    	*/
        me.versionStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy:{
					type : 'ajax',
					url:__ctxPath + me.listUrl,
					extraParams:{
						companyId:__user.companyId,
						schm:me.typeId//分库标志
					},
	    			reader: {
			            type : 'json',
			            root : 'datas',
			            totalProperty :'totalCount'
			        }
				 } 
		});	
		me.versionCombo = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '风险版本',
			store :me.versionStore,
			emptyText:'请选择',
			valueField : 'id',
			name:'version',
			displayField : 'name'
		});
		//当前版本，id=0
		me.versionCombo.setValue(0);
		me.versionCombo.setRawValue('当前版本');
		
        /** 
         *  风险级别列表
         */
        me.levelStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
			    {'id':'0', 'name':'全部'},
				{'id':'2', 'name':'二级风险'},
		        {'id':'3', 'name':'三级风险'},
		        {'id':'-1', 'name':'风险事件'}
			]
		});
		me.levelCombo = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '风险级别',
			store :me.levelStore,
			valueField : 'id',
			name:'level',
			displayField : 'name',
			value:'2'
		});

		/**
		 * 部门
		 */
		/*me.respDeptName = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '组织',// + '<font color=red>*</font>',
			labelAlign : 'left',
			type : 'dept',
			subCompany : false,
			multiSelect : true,
			name : 'respDeptName'
		});*/
		me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept',
			fieldLabel: '组织'+ '<font color=red>*</font>', 
			labelAlign: 'left',
			labelWidth: 100,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'respDeptName',
			value:''
		});
		
		/**
		 * 风险名称
		 */
		me.searchField = Ext.widget("textfield",{
			fieldLabel : '名称',
			labelAlign : 'left',
			name:'riskName'
		});
        //查询条件
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '查询条件',
            border : true,
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '7 10 10 30',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.versionCombo,me.levelCombo,me.searchField,me.respDeptName]
        };
        
        Ext.apply(me, { bbar : {
               items: ['->',	
	               {   
	            	   text: '返回',
	                   iconCls: 'icon-operator-home',
	                   handler: me.cancel,
	                   scope:me
	               },{   
	            	   text: "重置",
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.clearFormData,
	                   scope:me
	               },{   
	            	   text: "确定",
	                   iconCls: 'icon-control-stop-blue',
	                   handler: me.submit,
	                   scope:me
	               }
               ]
           },
        	border:false,
            items : [basicInfoFieldset]
        });

       me.callParent(arguments);
    },
    submit: function() {
	   	//刷新列表
    	var version = this.versionCombo.getValue();
    	var level = this.levelCombo.getValue();
    	var orgIds = "";
    	var orgIdJsons = this.respDeptName.getValue();	//json格式[{id:'111'},{id:'222'}]
    	if(orgIdJsons&&orgIdJsons!=""){
    		var orgIdJson = Ext.JSON.decode(orgIdJsons);
	    	for(var i=0;i<orgIdJson.length;i++){
	    		orgIds += orgIdJson[i].id + ",";
	    	}
	    	if(orgIds != ""){
	    		orgIds = orgIds.substring(0,orgIds.length-1);
	    	}
    	}
    	var query = this.searchField.getValue();
	   	this.owner.reloadData(version,level,orgIds,query);
		//修改版本名称
	   	if(this.versionCombo.getValue()){
	   		this.owner.setVersionLabel(this.versionCombo.getRawValue());
	   	}

	    this.cancel();
	},
	clearFormData:function(){
		var me = this;
		me.getForm().reset();
        //版本
        me.versionCombo.setValue(0);
		me.versionCombo.setRawValue('当前版本');
	},
    cancel : function(){
		var win = this.up('window');
		win.close();
    }
});