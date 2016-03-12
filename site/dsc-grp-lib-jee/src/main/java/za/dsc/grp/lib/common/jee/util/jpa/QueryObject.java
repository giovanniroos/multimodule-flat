package za.dsc.grp.lib.common.jee.util.jpa;

public class QueryObject {
	public enum QueryType {
		AND, OR
	}

	public enum Operator {
		LIKE("like"), EQUALS("="), GREATER(">"), LESS("<"), GE(">="), LE("<="), NOT_EQUAL("!=");
		private Operator(String operator) {
			this.operator = operator;
		}

		private String operator;

		/**
		 * @return the operator
		 */
		public String getOperator() {
			return operator;
		}

		/**
		 * @param operator
		 *            the operator to set
		 */
		public void setOperator(String operator) {
			this.operator = operator;
		}
	}

	private boolean ignore = false;
	private QueryType type = QueryType.AND;
	private Operator operator = Operator.EQUALS;
	private String leftPrefix = "";
	private String leftSuffix = "";
	private String rightPrefix = "";
	private String rightSuffix = "";

	public static final QueryObject DEFAULT = new QueryObject(
			false,
			QueryType.AND,
			Operator.EQUALS,
			"",
			"",
			"",
			"");

	public QueryObject() {
	}

	public QueryObject(boolean ignore) {
		this.ignore = ignore;
	}

	public QueryObject(boolean ignore, QueryType type) {
		this.ignore = ignore;
		this.type = type;
	}

	public QueryObject(boolean ignore, Operator operator) {
		this.ignore = ignore;
		this.operator = operator;
	}

	public QueryObject(Operator operator) {
		this.operator = operator;
	}
	
	public QueryObject(QueryType type) {
		this.type = type;
	}

	public QueryObject(QueryType type, Operator operator) {
		this.type = type;
		this.operator = operator;
	}

	public QueryObject(boolean ignore, QueryType type, Operator operator) {
		this.ignore = ignore;
		this.type = type;
		this.operator = operator;
	}

	public QueryObject(
			String leftPrefix, String leftSuffix,
			String rightPrefix,	String rightSuffix) {
		this.leftPrefix = leftPrefix;
		this.leftSuffix = leftSuffix;
		this.rightPrefix = rightPrefix;
		this.rightSuffix = rightSuffix;
	}

	public QueryObject(
			boolean isLeft,
			String prefix, 
			String suffix) {
		if (isLeft) {
			this.leftPrefix = prefix;
			this.leftSuffix = suffix;
		} else {
			this.rightPrefix = prefix;
			this.rightSuffix = suffix;
		}
	}

	public QueryObject(boolean ignore, QueryType type, Operator operator,
			String leftPrefix, String leftSuffix, String rightPrefix,
			String rightSuffix) {
		this.ignore = ignore;
		this.type = type;
		this.operator = operator;
		this.leftPrefix = leftPrefix;
		this.leftSuffix = leftSuffix;
		this.rightPrefix = rightPrefix;
		this.rightSuffix = rightSuffix;
	}

	/**
	 * @return the leftPrefix
	 */
	public String getLeftPrefix() {
		return leftPrefix;
	}

	/**
	 * @param leftPrefix
	 *            the leftPrefix to set
	 */
	public void setLeftPrefix(String leftPrefix) {
		this.leftPrefix = leftPrefix;
	}

	/**
	 * @return the leftSuffix
	 */
	public String getLeftSuffix() {
		return leftSuffix;
	}

	/**
	 * @param leftSuffix
	 *            the leftSuffix to set
	 */
	public void setLeftSuffix(String leftSuffix) {
		this.leftSuffix = leftSuffix;
	}

	/**
	 * @return the rightPrefix
	 */
	public String getRightPrefix() {
		return rightPrefix;
	}

	/**
	 * @param rightPrefix
	 *            the rightPrefix to set
	 */
	public void setRightPrefix(String rightPrefix) {
		this.rightPrefix = rightPrefix;
	}

	/**
	 * @return the rightSuffix
	 */
	public String getRightSuffix() {
		return rightSuffix;
	}

	/**
	 * @param rightSuffix
	 *            the rightSuffix to set
	 */
	public void setRightSuffix(String rightSuffix) {
		this.rightSuffix = rightSuffix;
	}

	/**
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * @param ignore
	 *            the ignore to set
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	/**
	 * @return the type
	 */
	public QueryType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(QueryType type) {
		this.type = type;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

}
