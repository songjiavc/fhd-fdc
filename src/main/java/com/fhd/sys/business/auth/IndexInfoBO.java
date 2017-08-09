package com.fhd.sys.business.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.auth.IndexInfoDAO;
import com.fhd.entity.sys.auth.IndexInfo;

/**
 * 
 * ClassName:IndexInfoBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-8-23		下午2:03:18
 *
 * @see
 */
@Service
public class IndexInfoBO {
	@Autowired
	private IndexInfoDAO o_indexInfoDAO;
	/**
	 * merge:保存更改方法
	 * 
	 * @author 杨鹏
	 * @param indexInfo
	 * @since  fhd　Ver 1.1
	 */
	public void merge(IndexInfo indexInfo){
		o_indexInfoDAO.merge(indexInfo);
	}
	/**
	 * 
	 * remove:删除方法
	 * 
	 * @author 杨鹏
	 * @param indexInfo
	 * @since  fhd　Ver 1.1
	 */
	public void remove(IndexInfo indexInfo){
		o_indexInfoDAO.delete(indexInfo);
	}
}
