package pms.util.db.constants;

public enum Comparator {
		LARGER(">",0),SMALLER("<",1),EQUALS("=",2),NOTEQUALS("!=",3);
		private String value;
		private int index;
		
		private Comparator(String value,int index) {
			this.value=value;
			this.index=index;
		}
		public String getValue() {
			return value;
		}

		public int getIndex() {
			return index;
		}
		
		public String toString() {
			return this.getValue();
		}
}
