package foodReview2013;

public class HelloWorld {
	
	public String advanceFunction(){
		return "add some function";
	}
	
	public String addMoreFunction(){
		return "more function";
	}
	
	public static void main(String[] args){
		System.out.println("Hello World!");
		HelloWorld hello = new HelloWorld();
		String a = hello.advanceFunction();
		String b = hello.addMoreFunction();
	}
}
