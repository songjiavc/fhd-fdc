Ext.define('FHD.view.myallfolder.riskfolder.MyRiskEventAddForm', {
    extend: 'FHD.view.risk.cmp.RiskAddForm',
    alias: 'widget.myriskeventaddform',
    autoScroll: true,
    border: false,
    /**
     * 返回的风险事件列表
     */
    riskEventGrid:null,
    
    initComponent: function () {
        var me = this;
        
        var saveBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
            iconCls: 'icon-control-stop-blue',
            handler: function () {
            	me.save();
            }
        });
        var returnBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
            iconCls: 'icon-arrow-undo',
            handler: function () {
            	me.face.cardpanel.setActiveItem(me.icontainer);
            	me.face.navigationBar.renderHtml(me.face.myfolderRiskContainer.id + 'DIV', 'deptrisk', '', 'departmentfolder', me.face.departmentTree.id);
            	//刷新数据
            	me.riskEventGrid.reloadData('org',__user.majorDeptId);
            }
        });
        Ext.apply(me, {
        	type:'re',
        	hiddenSaveBtn:true,
			border:false,
        	tbar:[{width:0,height:20}],
            bbar:['->',returnBtn,saveBtn]
        });
        me.callParent(arguments);
        
    }
});