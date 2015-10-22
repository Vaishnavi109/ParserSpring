
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import net.sourceforge.plantuml.SourceStringReader;

public class Parser {
	public static HashMap<String, String> dependencyMap = new HashMap<String, String>();
	public static String data ="@startuml\n";
	public static ArrayList multiplicity = new ArrayList();
	public static ArrayList OneToOneMultiplicity = new ArrayList();
	public static ArrayList Uses = new ArrayList();
	public static ArrayList Interfaces = new ArrayList();
	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		FileInputStream in = new FileInputStream("JavaInput.java");

		CompilationUnit cu=null;
		try {
			// parse the file
			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}
		new MethodVisitor().visit(cu, null);
		
		
		 Set setDependency = dependencyMap.entrySet();
		 Iterator iterator = setDependency.iterator();
		 while(iterator.hasNext()) {
	          Map.Entry mentry = (Map.Entry)iterator.next();
	          boolean state = data.contains("\nclass "+mentry.getKey().toString()+" -- "+"class  "+mentry.getValue().toString());
	          boolean state1 = data.contains("\nclass "+mentry.getValue().toString()+" -- "+"class  "+mentry.getKey().toString());
	          if(!data.contains("\nclass "+mentry.getKey().toString()+" -- "+"class  "+mentry.getValue().toString()) &&
	        		  !data.contains("\nclass "+mentry.getValue().toString()+" -- "+"class  "+mentry.getKey().toString()))
	          {
	        	  System.out.println(mentry.getKey().toString()+" + "+mentry.getValue().toString());
	        	  if(multiplicity.contains(mentry.getKey().toString()+"-"+mentry.getValue().toString()) &&
	        			  !data.contains(mentry.getKey().toString()+'"'+"1"+'"'+" -- "+'"'+ "0..*"+'"'+" "+mentry.getValue().toString()+"\n"))
	        		  data+=mentry.getKey().toString()+'"'+"1"+'"'+" -- "+'"'+ "0..*"+'"'+" "+mentry.getValue().toString()+"\n";
	        	  else if(multiplicity.contains(mentry.getValue().toString()+"-"+mentry.getKey().toString())&&
	        			  !data.contains(mentry.getValue().toString()+'"'+"1"+'"'+" -- "+'"'+ "0..*"+'"'+" "+mentry.getKey().toString()+"\n"))
	        		  data+=mentry.getValue().toString()+" "+'"'+"1"+'"'+" -- "+'"'+ "0..*"+'"'+" "+mentry.getKey().toString()+"\n";
	        	  else
	              data+="\nclass "+mentry.getKey().toString()+" -- "+"class  "+mentry.getValue().toString()+"\n";
	          }
	       
	      }
		 /*
		 for(int i=0;i<Uses.size();i++){
			 
			 String[] segments = Uses.get(i).toString().split("-", 2);
			 if(!segments[0].equals("int") && !segments[0].equals("float") && !segments[0].equals("String") && !segments[0].equals("double") && !segments[0].equals("byte") 
			&& !segments[0].equals("boolean") && !segments[0].equals("short") && !segments[0].equals("long") ){
				data+="\n"+segments[0] + " <.. " +segments[1]+"\n";
			 
		 }
		 for(int i=0;i<OneToOneMultiplicity.size();i++){
			 
			 String[] segments = OneToOneMultiplicity.get(i).toString().split("-", 2);
			 data+=segments[0]+'"'+"1"+'"'+" - "+'"'+ "1"+'"'+" "+segments[1]+"\n";
		 }
		*/
		 for(int i=0;i<Uses.size();i++){
			 String[] segments = Uses.get(i).toString().split("-", 2);
			 if(!segments[0].equals("int") && !segments[0].equals("float") && !segments[0].equals("String") && !segments[0].equals("double") && !segments[0].equals("byte") 
					 && !segments[0].equals("boolean") && !segments[0].equals("short") && !segments[0].equals("long") ){
				data+="\n"+segments[0] + " <.. " +segments[1]+"\n";
		 }
		}
		data+="\n@enduml\n";
		
		System.out.println(data);
		
		SourceStringReader reader=new SourceStringReader(data);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String desc =reader.generateImage(output);
		byte [] data = output.toByteArray();
		InputStream inputImageStream = new ByteArrayInputStream(data);
		BufferedImage umlImage = ImageIO.read(inputImageStream);
		ImageIO.write(umlImage, "png", new java.io.File("D:\\image.png"));

	}

	private static class MethodVisitor extends VoidVisitorAdapter {
		

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			n.getAllContainedComments();
		
			String ClassName = n.getName();
			if(n.isInterface()){
				data+="interface\t"+ClassName+"{"+"\n";
				Interfaces.add(ClassName);
				List<BodyDeclaration> members = n.getMembers();
				if(members!=null){
					for (BodyDeclaration member : members) {
						 if(member instanceof MethodDeclaration)
						{
							MethodDeclaration method = (MethodDeclaration)member;	
							List<Parameter> params = method.getParameters();
							if(params==null)
							data+=method.getName().toString()+" : "+method.getType().toString()+"\n";
							else{
							int modifier=method.getModifiers();
							String modifierSymbol= GetModifier(modifier);
							data+=modifierSymbol +" "+ method.getName().toString()+"(";
							for(Parameter param : params){
								data+=param.getId()+" : "+param.getType()+" ";
								GetUsesRelation(param.getType().toString(),ClassName);
								}
							data+=")"+" : "+method.getType().toString()+"\n";
							}
						}
					}
					
				}
				data+="\n"+"}"+"";
			}
			else{
				
			List<ClassOrInterfaceType> extClasses = n.getExtends();
			if(extClasses!=null)
				for(ClassOrInterfaceType ext : extClasses){
				data+= "Class\t" +ext.getName() +"<|--"+"Class\t"+ClassName+"\n";
				
				}
			
			List<ClassOrInterfaceType> interfaces = n.getImplements();
			if(interfaces!=null){
				for(ClassOrInterfaceType infc : interfaces){
					data+=infc.getName() +"<|.."+ClassName+"\n";
				}
			}
			data+="class "+ClassName+"{\n";
			List<BodyDeclaration> members = n.getMembers();
			if(members!=null)
			{
				for (BodyDeclaration member : members) {
					if(member instanceof ConstructorDeclaration){
						ConstructorDeclaration construct = (ConstructorDeclaration)member;
						String modifierSymbol= GetModifier(construct.getModifiers());
						data+=modifierSymbol+" "+construct.getName()+"(";
						List<Parameter> ConstructParams = construct.getParameters();
						if(ConstructParams!=null){
							for(Parameter para : ConstructParams){
								data+=para.getId()+" : "+para.getType()+" ";
								GetUsesRelation(para.getType().toString(),ClassName);
							}
							
						}
						data+=")\n";
						
					}
					if(member instanceof FieldDeclaration)
					{

						FieldDeclaration vars = (FieldDeclaration)member;
						int abc = vars.getModifiers();
						String type1 = vars.getType().toString();
			            //System.out.println(type1);
						List<VariableDeclarator> variables = vars.getVariables();
						
						if(variables!=null){
							for (VariableDeclarator var : variables) {
								int modifier=vars.getModifiers();
								String modifierSymbol= GetModifier(modifier);
								data+=modifierSymbol+" "+var.getId().getName();
								GetVariableType(vars,ClassName);
								
							}
						}
			
						
					}
					else if(member instanceof MethodDeclaration)
					{
						MethodDeclaration method = (MethodDeclaration)member;	
						List<Parameter> params = method.getParameters();
						if(params==null)
						data+=method.getName().toString()+" : "+method.getType().toString()+"\n";
						else{
						int modifier=method.getModifiers();
						String modifierSymbol= GetModifier(modifier);
						data+=modifierSymbol +" "+ method.getName().toString()+"(";
						for(Parameter param : params){
							data+=param.getId()+" : "+param.getType()+" ";
							GetUsesRelation(param.getType().toString(),ClassName);
							}
						data+=")"+" : "+method.getType().toString()+"\n";
						}
					}
				}
				data+="\n}\n";
			}
		}
		}
		private void GetUsesRelation(String Type, String className) {
			
				Uses.add(Type+"-"+className);
			
			
		}
		private void GetVariableType(FieldDeclaration vars, String ClassName) {
			if(vars.getType().toString().equals("int")||vars.getType().toString().equals("String")||
					vars.getType().toString().equals("float")||vars.getType().toString().equals("double")||
					vars.getType().toString().equals("boolean")||vars.getType().toString().equals("byte")||
					vars.getType().toString().equals("short")||vars.getType().toString().equals("long") )
			{
				data+= " : "+vars.getType().toString()+"\n";
	
			}
			else if(vars.getType().toString().equals("int[]")||vars.getType().toString().equals("String[]")||
					vars.getType().toString().equals("float[]")||vars.getType().toString().equals("double[]")||
					vars.getType().toString().equals("boolean[]")||vars.getType().toString().equals("byte[]")||
					vars.getType().toString().equals("short[]")||vars.getType().toString().equals("long[]") )
			{
				data+= " : "+vars.getType().toString()+"\n";
	
			}
			else{
					if(vars.getType().toString().contains("<"))
					{
						int beginIndex=vars.getType().toString().indexOf("<");
						int endIndex = vars.getType().toString().indexOf(">");
						String CollectionClass = vars.getType().toString().substring(beginIndex+1, endIndex);
						
						dependencyMap.put(CollectionClass,ClassName);
						data+=" : "+CollectionClass+"[*]"+"\n";
						//String multi  = ClassName+" 1"+" - "+ " 0..* "+CollectionClass;
						multiplicity.add(ClassName+"-"+CollectionClass);
					}
					else{
					data+= " : "+vars.getType().toString()+"\n";
					dependencyMap.put(vars.getType().toString(),ClassName);
					String multi  = ClassName+" 1"+" - "+ " 1 "+vars.getType().toString();
					OneToOneMultiplicity.add(ClassName+"-"+vars.getType().toString());
					}
				}
	
		}

		private String GetModifier(int modifier) {
			if(modifier ==1)
				return "+";
			else if(modifier ==2)
				return "-";
			else if(modifier ==4)
				return "#";
			else if(modifier ==1024)
				return "{abstract}";
			else if(modifier ==8)
				return "{static}";
			
			
			return "";
		}
		
	}
}





