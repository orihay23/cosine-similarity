package cosine;
 
import java.util.*;
public class TokenizerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


			StringTokenizer t = new StringTokenizer("");
			

			 StringTokenizer s =
		                new StringTokenizer("\\\\/love<>:".toString().replaceAll(
		                    "\"(?:\\|[^])*?",
		                    ""), ".;)(}{][:\"+-/*<>|||&&&!=@~!@#$%^&*  ");
		
		System.out.println(s.nextToken());
		
		
/*		
		"\"(?:\\\\\"|[^\"])*?\"",
        ""), ".;)(}{][:\"+-/*<>|||&&&!=@~!@#$%^&*  ");
*/
	}

}
