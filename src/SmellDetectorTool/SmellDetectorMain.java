package SmellDetectorTool;

import java.io.File;

import com.github.javaparser.ParseException;

public class SmellDetectorMain {

	public static void main(String[] args) throws ParseException {
		ListClassesExample temp = new ListClassesExample();
		temp.listClasses(new File("C:\\Users\\Aminul\\eclipse-workspace\\Example temp"));
		
	}

}
