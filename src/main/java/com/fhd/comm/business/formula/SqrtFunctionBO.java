package com.fhd.comm.business.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fhd.comm.interfaces.ISystemFunctionBO;

@SuppressWarnings({"rawtypes","unchecked"})
public class SqrtFunctionBO extends FunctionCalculateBO implements
		ISystemFunctionBO {
	/**
	 * sqrt函数计算.
	 * @param cs
	 * @param objs
	 * @return String
	 * @throws NoSuchMethodException 
	 * @throws IllegalAccessException 
	 * @throws InvocationTargetException 
	 * @throws ClassNotFoundException 
	 */
	@Override
	public String calculateRule(Class[] cs, Object[] objs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class cls = Class.forName("java.lang.Math");
		Method m = cls.getMethod("sqrt", cs);
		return String.valueOf(m.invoke(cls, objs));
	}
}
