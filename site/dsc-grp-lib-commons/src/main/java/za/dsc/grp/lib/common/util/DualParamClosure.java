package za.dsc.grp.lib.common.util;

public interface DualParamClosure<K,T>
{

	void yield(K val1, T val2);
	
}
