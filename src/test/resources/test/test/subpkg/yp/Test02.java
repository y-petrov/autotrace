package test.test.subpkg.yp;

import java.util.Comparator;

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
		
		t02n.myC.compare(new Test02(), new Test02());
		
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
	
	public class Test02Nested {
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
		
		private Comparator<Test02> myC = new Comparator<Test02>() {
			
			@Override
			public int compare(Test02 o1, Test02 o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		};


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
