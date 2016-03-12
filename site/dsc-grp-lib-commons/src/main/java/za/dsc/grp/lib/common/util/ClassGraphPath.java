package za.dsc.grp.lib.common.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class ClassGraphPath
{
	
	List<PropertyDescriptor> path;
	@SuppressWarnings("unchecked")
	Class rootClass;

	private ClassGraphPath() {

		super();
		this.path = new ArrayList<PropertyDescriptor>();
	}
	
	public ClassGraphPath(PropertyDescriptor field, Class<?> rootClass) {
		this();
		assert(field != null);
		this.path.add(field);
		this.rootClass = rootClass;
	}
	
	ClassGraphPath(PropertyDescriptor field, ClassGraphPath existingPath, Class<?> rootClass) {
		this();
		this.path = new ArrayList<PropertyDescriptor>();
		for (PropertyDescriptor propertyDescriptor : existingPath.path) {
			this.path.add(propertyDescriptor);
		}
		this.path.add(field);
		this.rootClass = rootClass;
	}

	public void addChild(PropertyDescriptor property) {
		assert(property != null);
		path.add(property);
	}
	
	
	public String toPath() {
		StringBuilder b = new StringBuilder();
		for (PropertyDescriptor pathElement : path) {
			b.append(pathElement.getName()).append('.');
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	public int getDepth() {
		return path.size();
	}
	
	@Override
	public String toString() {
		return toPath();
	}
	
	public PropertyDescriptor peek() {
		return path.get(path.size()-1);
	}
	
	public ClassGraphPath fork(PropertyDescriptor child) {
		return new ClassGraphPath(child, this,rootClass );
	}

	public Class<?> getRootClass() {
	
		return rootClass;
	}
	
	
	
	
	

}
