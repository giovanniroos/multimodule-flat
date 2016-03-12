package za.dsc.grp.lib.common.util;

public interface ReturningBiClosure<T, K, P> {

	P yield(T aParm, K bParm);
	
}
