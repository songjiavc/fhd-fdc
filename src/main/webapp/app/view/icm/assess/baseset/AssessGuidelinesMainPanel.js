Ext.define('FHD.view.icm.assess.baseset.AssessGuidelinesMainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.assessguidelinesmainpanel',

    // 初始化方法
    initComponent: function() {
        var me = this;

        me.assessguidelineseditgrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesEditGrid');
        
        //me.assessguidelinespropertyeditgrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesPropertyEditGrid');
        
        Ext.apply(me, {
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
            items: [
            	me.assessguidelineseditgrid
                //me.assessguidelinespropertyeditgrid
            ]
        });
        
        me.callParent(arguments);
    },
    reloadData:function(){
    	var me=this;
    	
    }
});