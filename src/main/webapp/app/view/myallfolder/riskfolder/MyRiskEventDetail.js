Ext.define('FHD.view.myallfolder.riskfolder.MyRiskEventDetail', {
    extend: 'FHD.view.risk.cmp.RiskDetailForm',
    alias: 'widget.myriskeventdetail',
    autoScroll: true,
    border: false,
    /**
     * 返回的风险事件列表
     */
    riskEventGrid:null,
    
    initComponent: function () {
        var me = this;
        
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
            tbar:['->',returnBtn]
        });
        me.callParent(arguments);
        
    }
});