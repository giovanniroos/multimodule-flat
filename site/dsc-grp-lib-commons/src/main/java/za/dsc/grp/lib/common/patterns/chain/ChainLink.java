package za.dsc.grp.lib.common.patterns.chain;

public interface ChainLink<T>
{

	public boolean isResponsible(T context);

	public void execute(T context);

}
