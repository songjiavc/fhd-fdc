Ext.define('FHD.view.risk.orgstorage.OrgStorageCardNew',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.orgstoragecardnew',

    showRiskGrid : function(){
        var me = this;
        me.getLayout().setActiveItem(me.riskGrid);
    },

    showRiskRelateForm : function(){
        var me = this;
        var riskForm=me.riskRelateForm;
        var orgObj=me.up('orgstoragemainpanelnew').orgTree.getCurrentTreeNode();
        if(orgObj!=''){
        var deptid = orgObj.data.id;
    	var deptname =orgObj.data.text;
    	var value = [];
        var obj = {};
        obj["deptid"] = deptid;
        obj["deptno"] = deptid;
        obj["deptname"] = deptname;
        value.push(obj);
        if(deptid!='1'&&deptid!=null&&deptid!=''){
        riskForm.respDeptName.setHiddenValue(value);
		riskForm.respDeptName.setValues(value);}}
        me.getLayout().setActiveItem(riskForm);
    },

    initComponent: function () {
        var me = this;
        //风险列表
        me.riskGrid = Ext.create('FHD.view.risk.orgstorage.OrgEventGridNew',{
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight,
            schm : me.schm      //添加风险分库标识
        });
        //风险编辑表单
        me.riskRelateForm = Ext.create('FHD.view.risk.orgstorage.OrgRelateFormNew',{
            showbar:false,
            type: 're',
            border: false,
            schm : me.schm,
            autoDestroy: true,
            title: '基本信息'
        });

        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.riskGrid, me.riskRelateForm]
        });

        me.callParent(arguments);
    }

});