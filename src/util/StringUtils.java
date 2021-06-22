package util;

import java.util.ArrayList;

import common.Settings;

public class StringUtils {
	//WIP:Started at 20.06
	public static String format(String s, Object... objects) {
		StringBuilder sb=new StringBuilder ();
		int l=s.length();
		int inclusionLevel=0;char c;
		for(int i=0;i<l;++i) {
			c=s.charAt(i);
			if(c=='\'')do {sb.append(c);i++;c=s.charAt(i);}while(c!='\''&&i<l-1);
			if(c=='\"')do {sb.append(c);i++;c=s.charAt(i);}while(c!='\"'&&i<l-1);
			if(c=='/'&&i<l-1&&s.charAt(i+1)=='/')do {sb.append(c);i++;c=s.charAt(i);}while(c!='\n'&&i<l-1);
			if(c=='/'&&i<l-1&&s.charAt(i+1)=='*')do {sb.append(c);i++;c=s.charAt(i);}while((c!='*'||s.charAt(i+1)!='/')&&i<l-2);
			
			if(c=='('||c=='['||c=='{')inclusionLevel++;
			if(c=='%')sb.append(c);
			if(c==')'||c==']'||c=='}')inclusionLevel--;
			if(inclusionLevel==-1)return null;
		}
		
		return sb.toString();
	}
	public static String power(String element, int count) {
		if(count<0)return null;
		if(count==0)return "";
		if(count==1)return element;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<count;++i)sb.append(element);
		return sb.toString();
	}
	public static String getNameFromFileName(String fileName) {
		int lastIndexOfBackSlash=fileName.lastIndexOf('\\');
		int lastIndexOfSlash=fileName.lastIndexOf('/');
		int lastIndex = Math.max(lastIndexOfSlash, lastIndexOfBackSlash);
		return fileName.substring(lastIndex+1);
	}
	public static String getFileExtension(String fileName) {
		String name = getNameFromFileName(fileName);
		int lastIndexOfDot=fileName.lastIndexOf('.');
		if(lastIndexOfDot==-1||lastIndexOfDot==fileName.length()-1)return "";
		return fileName.substring(lastIndexOfDot+1);
	}
}
