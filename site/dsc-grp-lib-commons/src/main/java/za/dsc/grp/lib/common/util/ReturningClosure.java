package za.dsc.grp.lib.common.util;

public interface ReturningClosure<K, T>
{
	
	
	K yield(T value);

}
