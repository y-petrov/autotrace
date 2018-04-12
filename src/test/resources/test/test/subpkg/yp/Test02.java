package test.test.subpkg.yp;

public class Test02 {
	
	static Object varStat = null;
	
	static {
		varStat = "ABC";
	}
	
	Object varInst = null;
	{
		varInst = "123";
	}
	
	public void m1(String[] arg) {
		if (arg == null)
			return;
		System.out.println("hi there...");
		Test02Nested t02n = new Test02Nested();
		t02n.m();
	}
	
	public static void main(String[] args) {
		Test02 t02 = new Test02();
		t02.m1(args);
	}
	
	public Test02() {
		System.out.println("...constructor... ...no args...");
	}

	public Test02(String arg) {
		System.out.println("...constructor... ...args...");
	}
	
	public class Test02Nested implements {
		public Test02Nested() {
			System.out.println("constr");
		}
		public String m() {
			Test02NestedNested t02nn = new Test02NestedNested();
			t02nn.mnn();
			if (Math.random() < 0.2)
				return "2";
			System.out.println("1");
			return "1";
		}

		public class Test02NestedNested {
			public Test02NestedNested() {
				System.out.println("constr");
			}
			public String mnn() {
				if (Math.random() < 0.2)
					return "2";
				System.out.println("1");
				return "1";
			}
		}
	
	}

}
