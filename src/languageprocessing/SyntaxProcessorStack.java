package languageprocessing;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class SyntaxProcessorStack extends ArrayList<OpenCorporaTag>{
	public void push(OpenCorporaTag tag) {
		add(tag);
	}
	public OpenCorporaTag pop() {
		return pop(size()-1);
	}
	public OpenCorporaTag pop(int index) {
		OpenCorporaTag tag=null;
		if(index<0||index>=size())return null;
		tag=get(index);
		this.remove(index);
		return tag;
	}

}
