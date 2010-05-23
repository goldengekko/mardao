package net.sf.mardao.domain;


public class Field implements Comparable<Field> {
	private String name;
	private String type;
	private String mappedBy;

	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getType() {
		return type;
	}
	
	public String getSimpleType() {
		String returnValue = this.type;
		int beginIndex = type.lastIndexOf('.');
		if (-1 < beginIndex) {
			returnValue = type.substring(beginIndex+1);
		}
		return returnValue;
	}
	
	@Override
	public String toString() {
		return name;
	}


	@Override
	public int compareTo(Field other) {
		return toString().compareTo(other.toString());
	}


	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}


	public String getMappedBy() {
		return mappedBy;
	}
}
