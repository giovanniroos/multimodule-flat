package za.dsc.grp.lib.common.patterns.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class ChainOfResponsibility<T>
{

	List<ChainLink<T>> chainLinks;
	ChainLink<T> executedLink;

	public ChainOfResponsibility() {

		super();
		chainLinks = new ArrayList<ChainLink<T>>();
	}

	public ChainOfResponsibility(List<ChainLink<T>> chainLinks) {

		super();
		setChainLinks(chainLinks);
	}

	public List<ChainLink<T>> getChainLinks() {

		return chainLinks;
	}

	public void add(ChainLink<T> chainLink) {

		if (chainLink != null) {
			chainLinks.add(chainLink);
		}
	}
	
	public boolean insert(ChainLink<T> chainLink, int pos) {
		synchronized (chainLinks) {
			ListIterator<ChainLink<T>> iterator = chainLinks.listIterator();
			int cnt = 0;
			while (iterator.hasNext()) {
				if (cnt++ == pos) {
					iterator.add(chainLink);
					return true;
				}
				iterator.next();
			}
		}
		return false;
	}

	public void setChainLinks(List<ChainLink<T>> chainLinks) {

		if (chainLinks == null) {
			throw new IllegalArgumentException("chainLinks must not be null");
		}
		this.chainLinks = chainLinks;
	}

	public boolean execute(T context) {

		executedLink = null;
		for (ChainLink<T> chainLink : chainLinks) {
			if (chainLink.isResponsible(context)) {
				chainLink.execute(context);
				executedLink = chainLink;
				return true;
			}
		}
		return false;
	}

	public ChainLink<T> getExecutedLink() {

		return executedLink;
	}

}
