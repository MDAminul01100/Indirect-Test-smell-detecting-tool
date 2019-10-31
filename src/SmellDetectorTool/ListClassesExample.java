package SmellDetectorTool;

import com.github.javaparser.JavaParser;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.lang.String;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ListClassesExample {
	public int counter = 1;
	
    public void listClasses(File projectDir) throws ParseException {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
          	
        	System.out.println(counter + ". " +path);
        	counter++;
            System.out.println(Strings.repeat("=", path.length()));
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        //super.visit(n, arg);
                    	NodeList<BodyDeclaration<?>> bodymembers = n.getMembers();                   	
                        System.out.println(" * " + n.getName());
                        boolean temp = checkMethodsHavingSmell(bodymembers);
						if(!temp)
							 System.out.println("This class doesn't have indirect test smell");
                    }
                }.visit(JavaParser.parse(file), null);
                System.out.println(); // empty line
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
    }

    public boolean checkMethodsHavingSmell(NodeList<BodyDeclaration<?>> bodymembers)
    {
    	boolean hasSmellinThisClass = false;
    	for(BodyDeclaration<?> method : bodymembers)
    	{    		    		
    		if((method instanceof MethodDeclaration) && checkValidityTestMethod((MethodDeclaration) method)) {
    			MethodDeclaration tempMethod = (MethodDeclaration) method;
    			
    			if(hasSmellinThisMethod(tempMethod))
    			{
    				System.out.println("	" +tempMethod.getBegin() +  tempMethod.getNameAsString());
    				hasSmellinThisClass = true;
    			}
    				
    		} 			
    		
    	}
    	return hasSmellinThisClass;
    }
    public boolean hasSmellinThisMethod(MethodDeclaration method)
    {
    	List<String> lines = new ArrayList<String>();
    	String tempStr[] = method.toString().split("\n");
    	lines = Arrays.asList(tempStr);
    	
    	List<String> variables = new ArrayList<String>(); 
    	boolean hasSmell = false;
    	for(String line : lines) 
    	{
    		String str = line;
    		if((str.toCharArray()[0] == '/') || !str.contains(";"))
    			continue;
    		    		
    		if( line.contains("new"))
    		{
    			String tempStr1[] = str.split("new");
    			
    			if(tempStr1.length > 2)    			
    				hasSmell = true;
    			String str2 = line.trim().replaceAll(" +", " ");;
    			String[] temp2 = str2.split(" ");
    			variables.add(temp2[1]);
    			
    			line = line.replace(temp2[1] , "");
    			
    			for(String str1 : variables)
    			{
    				if(line.contains(str1))
    					hasSmell = true;
    			}
    			
    		}
    	}
    	
		return hasSmell;		
	}
    
    
    public boolean checkValidityTestMethod(MethodDeclaration method) {
        
        boolean isTest=false;
        boolean validTest = false;
        if (method.getAnnotationByName("Ignore").isPresent()) {
        	return false;
        }
        else{
            if (method.getAnnotationByName("Test").isPresent())
            	isTest=true;          
            if ( method.getNameAsString().toLowerCase().startsWith("test")) 
            	isTest=true;
            if ( method.getNameAsString().toLowerCase().lastIndexOf("test")!=-1)
            	isTest=true;
            
            if (isTest==true && method.getModifiers().contains(Modifier.PUBLIC)) 
                    validTest = true;
                        
        }
        if(method.toString().contains("assert"))
        	validTest = true;
        return validTest;
    }
}
