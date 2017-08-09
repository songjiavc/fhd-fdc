/**
 * 
 * 学习园地链接页面
 * 使用border布局
 * 
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.sf.index.SFStudy.SFStudyMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.sfstudymain',

    requires: [
    	'FHD.view.sys.documentlib.DocumentLibTree',
    	'FHD.view.sys.documentlib.DocumentLibCard'
    ],
    
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        //知识库页面
        /*me.documentLibMainPanel = Ext.create('FHD.view.sys.documentlib.DocumentLibMainPanel',{
        	typeId: 'document_library_knowledge',
        	region: 'center',
        	flex: 2
        });*/
        
        //视频列表
        me.SFStudyVideoGrid = Ext.create('FHD.view.sf.index.SFStudy.SFStudyVideoGrid',{
        	region:'south',
        	flex: 1
        });
        
        //文档库右侧页面
        me.documentLibCard = Ext.widget('documentLibCard',{
        	border : true,
        	typeId: me.typeId,
            region: 'center',
            flex: 2
        });
        //右侧主面板，包括文档库列表和视频列表
        me.documentLibMainPanel = Ext.create('Ext.panel.Panel',{
        	layout: 'border',
        	border: false,
        	region: 'center',
        	items: [me.documentLibCard,me.SFStudyVideoGrid]
        });
        
        //文档库树
        me.documentLibTree = Ext.widget('documentLibTree',{
        	collapsible: true,
            collapsed:true,
        	typeId: 'document_library_knowledge',
        	nodeId:me.nodeId||me.SFnodeId,
        	region: 'west'
        });
        
        Ext.applyIf(me, {
        	typeId: 'document_library_knowledge',
        	region: 'center',
            items: [me.documentLibTree,me.documentLibMainPanel]
        });

        me.callParent(arguments);
    }

});