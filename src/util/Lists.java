package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Lists {
	public static Object[] getArray(List<Object> objects) {
		if(objects==null||objects.size()==0)return null;
		Object[] result = new Object[objects.size()];
		for(int i=0;i<result.length;++i)result[i]=objects.get(i);
		return result;
	}
	public static String extractMethod(ArrayList<String> tagsContainer) {
		if(tagsContainer.size()==0)return "";
		return tagsContainer.get(0);
	}
	public static List<String> extractParametersV0(ArrayList<String> tagsContainer){
		if(tagsContainer.size()<=1)return null;
		return tagsContainer.subList(1, tagsContainer.size());
	}
	public static Map<String, String> extractParametersV1(){
		System.out.println("WIF");
		return null;
	}
	public static Map<String, Object> extractParametersV2(){
		System.out.println("WIF");
		return null;
	}
	public static Object tryToGet(List<?> list, int index) {
		if(list==null)return null;
		if(list.size()<=index)return null;
		return list.get(index);
	}
	public static void getUniqueArrayList(ArrayList in,ArrayList out){
		int l=in.size(),l1=0;
		boolean match;
		for(int i=0;i<l;++i) {
			match=false;
			for(int j=0;j<l1;++j) {
				match=in.get(i).equals(out.get(j));
				if(match)break;}
			if(match)continue;
			out.add(in.get(i));
			l1+=1;}
		return;
	}
	public static boolean addUnique(ArrayList<Object> container, Object object) {
		if(container.contains(object))return false;
		container.add(object);
		return true;
	}
	public static boolean addUnique(ArrayList<Integer> container, int object) {
		if(container.contains(object))return false;
		container.add(object);
		return true;
	}
	public static int getIndex(Object[] array, Object element) {
		for(int i=0;i<array.length;++i)
			if(array[i].equals(element))return i;
		return -1;
	}
	public static int getIndex(int[] array, int element) {
		for(int i=0;i<array.length;++i)
			if(array[i]==element)return i;
		return -1;
	}
	public static String readNextTerm(String string, ArrayList<String> container){
		//StringBuilder lastString=new StringBuilder();
		int l = string.length();char c;StringBuilder sb=new StringBuilder();boolean isBraceChecked=false;
		int i=0;int inclusionsCount=0;
		for(i=0;i<l;++i) {
			c=string.charAt(i);
			if(c=='(')inclusionsCount++;
			if(c>='A'&&c<='Z'||c>='a'&&c<='z'||c>='0'&&c<='9'||inclusionsCount>1)
				sb.append(c);
			else {
				tryToAddTag(container, sb, true);
				if(c!=' '&&c!='	'&&c!='\n'&&c!='('&&c!=')') {
					sb.append(c);
					tryToAddTag(container, sb, true);
				}
			}
			if(c==','||c==';'&&inclusionsCount==0)return string.substring(i,l);
			if(c==')') {if(inclusionsCount>0)inclusionsCount--;else return string.substring(i,l);}
		}
		if(isBraceChecked) {
			for(;i<l;++i) {
				c=string.charAt(i);
			}
			container.addAll(splitInBraces(string));
		}
		
		return string.substring(i,l);
	}
	
	public static ArrayList<ArrayList<Object>> sortObjectsByClass(ArrayList<Object> in){
		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
		HashMap<String, Integer> registeredClasses = new HashMap<String, Integer>();
		String nowClassName;Object nowObject;//Class nowClass;
		int index;
		for(int i=0;i<in.size();++i) {
			nowObject = in.get(i);
			//nowClass=nowObject.getClass();
			nowClassName=nowObject.getClass().getSimpleName();
			index=registeredClasses.get(nowClassName);
			if(index==-1) {
				index=result.size();
				result.add(new ArrayList<Object>());
				registeredClasses.put(nowClassName, index);
			}
			result.get(index).add(nowObject);
		}
		registeredClasses.clear();//Object[] result1 = new Object[2];result1[0]=result;result1[1]=registeredClasses;return result1; 
		return result;
	}
	
	
	/**splits string in braces by ','*/
	public static ArrayList<String> splitInBraces (String string){
		return split(string, splitModeBraces);
	}
	/**Splits string by ','*/
	public static ArrayList<String> splitByTerms (String string){
		return split(string, splitModeTerms);
	}
	/**Splits string by space*/
	public static ArrayList<String> splitBySpace (String string){
		return split(string, splitModeSpace);
	}
	private static final int splitModeBraces = 0;
	private static final int splitModeTerms = 1;
	private static final int splitModeSpace = 2; 
	/**mode==0 - split in braces, 1 - split by terms, 2 - split by delimiters*/
	private static ArrayList<String> split (String string, int mode){
		ArrayList<String> strings = new ArrayList<String>();
		int l = string.length();char c;StringBuilder sb=new StringBuilder();
		int from = (mode==splitModeBraces)?string.indexOf('(')+1:0;
		int to=l;//int to = (mode==splitModeBraces)?string.indexOf(')'):l;
		char delimiterChar=(mode==splitModeSpace)?' ':',';
		boolean lengthCheck=(mode==splitModeSpace);
		int inclusionLevel=0;
		for(int i=from; i<to;++i) {
			c=string.charAt(i);
			if(c=='(')inclusionLevel++;
			if(c==delimiterChar&&inclusionLevel==0)
				tryToAddTag(strings, sb, lengthCheck);
			else
				sb.append(c);
			if(c==')')inclusionLevel--;
			if(inclusionLevel==-1)return strings;
		}
		return strings;
	}
	private static void tryToAddTag(ArrayList<String> container, StringBuilder sb, boolean lengthCheck) {
		if(lengthCheck&&sb.length()==0)return;
		container.add(sb.toString());
		sb.delete(0, sb.length());
	}
	private static ArrayList<String> tokenizeString(String string, String delimiters, String monoChars){
		ArrayList<String> strings = new ArrayList<String>();
		int l = string.length();char c;StringBuilder sb=new StringBuilder(); boolean latch=false;
		for(int i=0;i<l;++i) {
			c=string.charAt(i);
			if(monoChars.indexOf(c)!=0) {
				if(sb.length()>0) {strings.add(sb.toString());sb=new StringBuilder();}
				strings.add(c+"");
				continue;
			}
			if(delimiters.indexOf(c)==-1) {
				sb.append(c);
				latch=false;
			}else {
				if(!latch) {
					latch=true;
					strings.add(sb.toString());
					sb = new StringBuilder();
				}
			}
		}
		if(!latch) {
			latch=true;
			strings.add(sb.toString());
			sb = null;
		}else {
			if(sb!=null)sb=null;
		}
		return strings;
	}
	public static ArrayList getArrayList(Object[] elements) {
		ArrayList result = new ArrayList();
		for(int i=0;i<elements.length;++i)result.add(elements[i]);
		return result;
	}
}
