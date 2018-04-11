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
	}
	
	public Test02() {
		System.out.println("...constructor... ...no args...");
	}

	public Test02(String arg) {
		System.out.println("...constructor... ...args...");
	}

}

class Test02_a {
	public Test02_a(Object arg) {
		return;
	}
}
