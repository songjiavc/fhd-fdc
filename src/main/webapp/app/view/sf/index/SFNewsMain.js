Ext.define('FHD.view.sf.index.SFNewsMain',{
	extend:'Ext.form.Panel',
	
	initComponent : function(){
		var me = this;
		
		me.newsPanel = Ext.create('FHD.view.sf.index.SFNewsGrid',{
			fn:me.fn,
			border:false,
	        style: {
	        	backgroundColor: '#EFEFEF'
	        },
		});
		me.newsPanel.reloadData('index_news_report');
		Ext.apply(me,{
        	border:true,
			layout: 'fit',
			items:[me.newsPanel]
		});
		me.callParent(arguments);
	}
    
})