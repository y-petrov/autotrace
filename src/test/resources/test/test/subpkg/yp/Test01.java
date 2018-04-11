package test.test.subpkg.yp;

import java.util.Comparator;
import java.util.List;

public class Test01 {

	{
		System.out.println("hardcoded - instance init block");
	}

	public Test01(String pArg) {
		System.out.println("hardcoded - constructor with param");
	}

	public Test01() {
		System.out.println("hardcoded - constructor without param");
	}

	public static void main(String[] args) {
		Test01 t01 = new Test01();

		System.out.println("hardcoded - in main");

		/*
		 * Comparator<Integer> cmpInt = new Comparator<Integer>() {
		 * 
		 * @Override public int compare(Integer o1, Integer o2) { if (o1 != null && o2 != null) return o1.compareTo(o2); else return 0; } };
		 */

		t01.m1();

		while (true) {
			break;
		}

		if (args.length == 0)
			return;

		if (args.length > 2)
			System.out.println();
		else
			return;
		{
			System.out.println("hardcoded - main");
		}

		while (Math.random() > 0.1) {
			{
			}
			return;
		}

		for (;;) {
			// return;
			if (Math.random() > 0.2)
				break;
		}

		switch ((int) Math.random() * 10) {
		case 5:
			break;
		default:
			return;
		}

		return;
	}

	static {
		System.out.println("hardcoded - static init");
		{
			System.out.println("hardcoded - instance static init - nested block");
			System.out.println("hardcoded - instance static init - nested block");
		}
	}

	{
	}

	private String m1() {
		return "";
	}

	List<String> myList = null;

	static class TestNest01 {

		String m(int i) {
			Top2 t2 = new Top2();
			System.out.println(t2.dummyVar);
			return null;
		}

		class TestNestNest01 {

		}
	}

	{
		String str = "2nd instance init block";
	}

}

class Top2 {

	public Object dummyVar = null;

	{
		dummyVar = new String();
	}

}