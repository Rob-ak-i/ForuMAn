package common;

import java.awt.Rectangle;
import java.util.ArrayList;

import parts.PartBasic;
import parts.PartBinaryBasic;
import parts.PartNode;

public class EntityEditorLowLevel {
	protected int[][] matrix, matrixOfNodeIndexes,matrixOfPartIndexes;
	
	/**this is used only for placing parts via graphic editor*/
	public ArrayList<PartNode> nodesContainer;
	/**it is used excludingly for computations, and like alternative to nodelist*/
	public ArrayList<PartBinaryBasic> binaryPartsContainer;
	
	public ArrayList<PartBasic> elementsContainer;

	//---------------------------------lowlevel computational troubles---------------------------------------------------------------------------

	public int whatPartBinaryIndexIsHere(int x, int y) {
		return matrixOfPartIndexes[x][y];
	}
	public PartBinaryBasic whatPartBinaryIsHere(int x, int y) {
		PartBinaryBasic result=null;
		try {result=binaryPartsContainer.get(whatElementIndexIsHere(x,y));}catch(Exception e) {result=null;}
		return result;
	}
	public int whatNodeIndexIsHere(int x, int y) {
		return matrixOfNodeIndexes[x][y];
	}
	public PartNode whatNodeIsHere(int x, int y) {
		PartNode result=null;
		try {result=nodesContainer.get(whatElementIndexIsHere(x,y));}catch(Exception e) {result=null;}
		return result;
	}
	public int whatElementIndexIsHere(int x, int y) {
		return matrix[x][y];
	}
	public PartBasic whatElementIsHere(int x, int y) {
		PartBasic result=null;
		try {result=elementsContainer.get(whatElementIndexIsHere(x,y));}catch(Exception e) {result=null;}
		return result;
	}
	public PartBasic whatIsHere(int x, int y) {
		PartBasic result=null;
		int selectorMode = EntityEditorMouseListener.getSelectorMode();
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_ALL)return whatElementIsHere(x,y);
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_NODE)return whatNodeIsHere(x,y);
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_PARTBINARY)return whatPartBinaryIsHere(x,y);
		return result;
	}
	

	int[][] selectedMatrix(){
		int selectorMode = EntityEditorMouseListener.getSelectorMode();
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_ALL)return matrix;
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_NODE)return matrixOfNodeIndexes;
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_PARTBINARY)return matrixOfPartIndexes;
		return null;
	}
	ArrayList selectedContainer(){
		int selectorMode = EntityEditorMouseListener.getSelectorMode();
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_ALL)return elementsContainer;
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_NODE)return nodesContainer;
		if(selectorMode==EntityEditorMouseListener.SELECTORMODE_PARTBINARY)return binaryPartsContainer;
		return null;
	}
	public int whatElementIndexInZone(int x, int y, int scanRadius) {
		return MatrixHelper.searchNearestNeighbor(x, y, scanRadius, true, selectedMatrix(), -1);
	}
	public ArrayList<PartBasic> whatElementsInZone(int x0, int y0, int x1, int y1) {
		ArrayList<PartBasic> result = new ArrayList<PartBasic>();
		ArrayList<Integer> partsIndexes = new ArrayList<Integer>();
		int[][] selectedMatrix=selectedMatrix();
		int index=0;
		if(x1<x0) {index=x0;x0=x1;x1=index;}
		if(y1<y0) {index=y0;y0=y1;y1=index;}
		if(x0<0)x0=0;if(y0<0)y0=0;if(x1>=selectedMatrix.length)x1=selectedMatrix.length-1;if(y1>=selectedMatrix[0].length)y1=selectedMatrix[0].length-1;
		for(int x=x0;x<=x1;++x) {
			for(int y=y0;y<=y1;++y) {
				index=selectedMatrix[x][y];
				if(index==-1)continue;
				if(partsIndexes.contains(index))continue;
				partsIndexes.add(index);
			}
		}
		for(int i=0;i<partsIndexes.size();++i) {
			result.add((PartBasic) (selectedContainer().get(partsIndexes.get(i))));
		}
		partsIndexes.clear();
		return result;
	}
	public PartBasic whatElementInZone(int x, int y, int scanRadius) {
		int index=whatElementIndexInZone(x,y, scanRadius);
		if(index<0)return null;
		return (PartBasic)selectedContainer().get(index);
	}
	

	public boolean partsCollider(int xPad, int yPad, int objectRadius) {
		
		
		return false;
	}
}
