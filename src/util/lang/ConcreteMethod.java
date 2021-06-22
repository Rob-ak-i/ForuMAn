package util.lang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class ConcreteMethod {
	@Deprecated public String getMethodName() {return method.getName();}
	public Method method=null;
	public Object owner=null;
	public ArrayList<Object> parameters;
	public ConcreteMethod(Object owner, String methodName, List<Object> parameters) {
		this.owner=owner;
		this.method=getMethod(owner,methodName,parameters);
		this.parameters=null;
		if(parameters!=null&&parameters.size()>0) {
			this.parameters=new ArrayList<Object>();
			for(int i=0;i<parameters.size();++i)
				this.parameters.add(parameters.get(i));
		}
	}
	
	public static Method getMethod(Object selectedObject, String selectedField, List<Object> parameters) {
		Method method=null,m=null;int parv=0;if(parameters!=null)parv=parameters.size();
		Class[] methodParameters=null;Class tempClass=null;
		Object castingObject=null, nowObject=null;
		Method [] methods=null;
			methods=selectedObject.getClass().getMethods();
		for(int i=0;i<methods.length;++i)
			if(methods[i].getName().equals(selectedField)) {
				m=methods[i];
				if(m.getParameterCount()!=parv)continue;
				if(parv==0) {method=m;break;}
				
				methodParameters=m.getParameterTypes();
				boolean isSimilarMethod=true;
				try {
					for(int j=0;j<parv;++j) {
						nowObject=parameters.get(j);
						tempClass=methodParameters[j];
						if(nowObject==null)continue;
						if(tempClass.isPrimitive()||nowObject.getClass().isPrimitive()) {
							Class nowObjectClass=nowObject.getClass();
							boolean advCheck=true;
							advCheck=tempClass.getSimpleName().equalsIgnoreCase(nowObjectClass.getSimpleName());
							if(!advCheck)isSimilarMethod=false;
						}else
							castingObject=tempClass.cast(nowObject);
					}
				}catch(Exception e) {
					isSimilarMethod=false;
					}
				if(!isSimilarMethod)continue;
				method=m;
				break;
			}
		return method;
			
	}

	public Object get() {Object result=null;
			try {
				if(parameters==null)
					return method.invoke(owner);
				result=method.invoke(owner, parameters);
			} catch (Exception  e) {
				System.out.println(e.getMessage());
			}
			if(parameters!=null)parameters.clear();parameters=null;
			return result;
	}
}
