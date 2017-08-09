
package com.fhd.sys.interfaces;

import org.springframework.stereotype.Service;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.dic.DictEntryRelationType;

@Service
public interface IDictRelationBO {	
	public abstract void saveDictEntryRelation(DictEntryRelation dictEntryRelation);
	
	public abstract void removeDictEntryRelation(String id);
	
	/**
	 * 按业务关系类型删除
	 * @param typeId
	 */
	public void removeDictEntryRelationByBusinessId(String typeId,String businessId);
	
	public abstract DictEntryRelationType findDictEntryRelationTypeByTypeId(String typeId);
}
