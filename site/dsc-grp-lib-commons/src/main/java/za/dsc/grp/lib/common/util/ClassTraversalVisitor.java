package za.dsc.grp.lib.common.util;

public interface ClassTraversalVisitor
{
		void visit(ClassGraphPath node);

		boolean before(ClassGraphPath path);
	
	
	
}
