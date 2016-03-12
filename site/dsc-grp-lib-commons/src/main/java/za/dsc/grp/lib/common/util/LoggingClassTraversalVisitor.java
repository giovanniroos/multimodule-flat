package za.dsc.grp.lib.common.util;

import java.beans.PropertyDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingClassTraversalVisitor implements ClassTraversalVisitor
{
	
	Log log;
	Class<?> currentType;
	

	public LoggingClassTraversalVisitor() {

		super();
		log = LogFactory.getLog(getClass());
		
	}

	public void visit(ClassGraphPath node) {
		PropertyDescriptor peek = node.peek();
		int depth = node.getDepth();
		StringBuilder b = new StringBuilder();
		for (int i=0;i<depth;i++) {
			b.append("      ");
		}
		b.append("|----").append(peek.getName()).append("::").append(currentType.getName());
		log(b.toString());
	}
	
	public boolean before(ClassGraphPath path) {
		if (currentType == null) {
			currentType = path.getRootClass();
		} else {
			currentType = path.peek().getPropertyType();
		}
		return false;
	}
	
	private void log(String entry) {
		log.debug(entry);
	}

}
