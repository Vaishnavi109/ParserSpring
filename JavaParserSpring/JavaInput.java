 

import java.util.Collection;

public class A extends B implements E,F{
 
	private int x;
	 
	private int[] y;
	 
	private B b;
	 
	private C c;
	 
	private D d;
	private Collection<D> d;

	 
	public void test(int a,int b){}
	private int abc(){}
	protected int xyz(){}
	abstract int xyz(){}
	static int xyz(){}
}
 
 

public class B {
 
	private A a;
	 
}
 
  

public class C {
 
	private A a;
	 
}
 
 




public class D {
 
	private A a;
	 
}
 
