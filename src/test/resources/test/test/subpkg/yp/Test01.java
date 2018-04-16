package test.test.subpkg.yp;

import java.util.Comparator;
import java.util.List;

public class Test01 {

	{
		System.out.println("hardcoded - instance init block 1");
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
		System.out.println("hardcoded - static init 1");
		{
			System.out.println("hardcoded - instance static init 1 - nested block");
		}
	}

	{
	}

	private String m1() {
		if (Math.random() < 0.5)
			return "a";
		return "";
	}

	List<String> myList = null;

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