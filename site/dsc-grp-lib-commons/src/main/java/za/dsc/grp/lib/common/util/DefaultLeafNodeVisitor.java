package za.dsc.grp.lib.common.util;

import java.beans.PropertyDescriptor;

public class DefaultLeafNodeVisitor implements LeafNodeVisitor
{

	public boolean isLeafNode(ClassGraphPath node) {
		PropertyDescriptor currentProperty = node.peek();
		boolean isLeafNode = false;
		isLeafNode |= currentProperty.getPropertyType().isAnnotationPresent(Type.class);
		isLeafNode |= currentProperty.getPropertyType().getPackage() == null ;
		isLeafNode |= currentProperty.getPropertyType().getPackage() != null && currentProperty.getPropertyType().getPackage().getName().startsWith("java");
		isLeafNode |= currentProperty.getReadMethod().isAnnotationPresent(LeafNode.class);
		isLeafNode |= Enum.class.isAssignableFrom(currentProperty.getPropertyType());
		return isLeafNode;
	}

}
