package za.dsc.grp.lib.common.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ClassGraphTraversal
{

	Log log;
	Class<?> type;
	List<ClassTraversalVisitor> visitors;
	LeafNodeVisitor leafNodeVisitor;
	Object target;

	public ClassGraphTraversal(Class<?> target) {

		super();
		this.type = target;
		visitors = new ArrayList<ClassTraversalVisitor>(2);
		visitors.add(new LoggingClassTraversalVisitor());
		leafNodeVisitor = new DefaultLeafNodeVisitor();
		log = LogFactory.getLog(getClass());
	}

	public void traverse() {

		PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(type);
		for (PropertyDescriptor field : properties) {		
			if (log.isDebugEnabled())
				log.debug("visiting field: " + field.getName() + " :: " + field.getPropertyType().getName() + ";");
			visit(new ClassGraphPath(field, type));
		}
	}

	public void visit(ClassGraphPath path) {

		PropertyDescriptor field = path.peek();
		if (field.getName().equals("class")) {
			return;
		}
		if (leafNodeVisitor.isLeafNode(path)) {
			if (log.isDebugEnabled())
				log.debug("preparing to call visitors on the field: " + field.getName() + "::" + field.getPropertyType().getName() + ";");

			for (ClassTraversalVisitor visitor : visitors) {

				visitor.before(path);
			}
			for (ClassTraversalVisitor visitor : visitors) {
				visitor.visit(path);
			}
		}
		else {
			if (log.isDebugEnabled())
				log.debug("preparing to drill down into the field: " + field.getName() + "::" + field.getPropertyType().getName() + ";");
			PropertyDescriptor[] fields = PropertyUtils.getPropertyDescriptors(field.getPropertyType());
			for (PropertyDescriptor childField : fields) {
				visit(path.fork(childField));
			}
		}

	}

	public void addVisitor(ClassTraversalVisitor visitor) {

		assert (visitor != null);
		visitors.add(visitor);
	}

	public void removeVisitor(ClassTraversalVisitor visitor) {

		assert (visitor != null);
		visitors.remove(visitor);
	}

	public LeafNodeVisitor getLeafNodeVisitor() {

		return leafNodeVisitor;
	}

	public void setLeafNodeVisitor(LeafNodeVisitor leafNodeVisitor) {

		this.leafNodeVisitor = leafNodeVisitor;
	}

}
