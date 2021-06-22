package common;

import java.util.ArrayList;
import java.util.HashMap;

import gui.JComboBoxExt;
import languageprocessing.LanguageProcessor;
import languageprocessing.LowLevelTextProcessor;
import languageprocessing.OpenCorporaTag;
import languageprocessing.SentimentData;
import unit.DataTable;
import unit.MessageBank;
import unit.Sequence;
import util.Parameters;

public class EntityTextManager extends EntityManager<MessageBank> {
	/* MessageBank manager */

	public ArrayList<SentimentData> textMonomialstoSentiment;
	public ArrayList<String> textMonomials;
	public ArrayList<String> textLemmes;
	public ArrayList<Integer> textMonomialsToLemmes;
	public ArrayList<OpenCorporaTag> textMonomialsToTags;
	//public ArrayList<Sequence> sequenceRoots;

	
	public EntityTextManager() {
		super(MessageBank.class);
		textMonomials = new ArrayList<String>();
		textLemmes = new ArrayList<String>();
		textMonomialsToTags = new ArrayList<OpenCorporaTag>();
		textMonomialsToLemmes = new ArrayList<Integer>();
		textMonomialstoSentiment = new ArrayList<SentimentData>();
		//sequenceRoots=new ArrayList<Sequence>();
		//messageBanks=new ArrayList<MessageBank>();	
	}
	
	public int addWord(String word) {
		int index = textMonomials.indexOf(word);
		if(index!=-1)return index;
		textMonomials.add(word);
		return textMonomials.size()-1;
	}
	private int addLemmaInner(String word) {
		int index = textLemmes.indexOf(word);
		if(index!=-1)return index;
		textLemmes.add(word);
		return textLemmes.size()-1;
	}
	public void addLemmaAndTag(String lemma, String tagStr) {
		int lemmaIndex=addLemmaInner(lemma);
		textMonomialsToLemmes.add(lemmaIndex);
		OpenCorporaTag tag = new OpenCorporaTag(tagStr,lemmaIndex);
		textMonomialsToTags.add(tag);
	}

	public void addSentimentInformation(String mean, String opinion, String sentiment) {
		textMonomialstoSentiment.add(new SentimentData(mean, opinion, sentiment));
	}
	
	@Override
	/**allows to create databank even from somePerson or someTextForum or RawText.
	 * makes messagesArray with word indexes, new words of which sends to externalLibraries to recognize*/
	public void addManagedElement(MessageBank element, String identifier) {
		//String newIdentifier=null;
		//DataTable table = CommonData.tableManager.getManagedElement(tableIdentifier);if(table==null)return null;
		//MessageBank element = languageprocessing.LowLevelTextProcessor.extractMessagesFromDataTable(table);if(messageBank==null)return null;
		//newIdentifier=common.Lang.InnerTable.Misc.dataTableToMessageBankAppendix+tableIdentifier;
		element.name=identifier;
		super.addManagedElement(element, identifier);
		/**updating TextManager mechanism*/
		appendKnowledgeBase();
		element.storageLevel=MessageBank.storageLevel_Lexemes;
		//unifyTagsByLemmes(); - was made automatically by textManager
		LanguageProcessor.upgradeAndSpliceAppendedMessages(element);//getGraphemesFromMessages();
		//return newIdentifier;
	}
	private void appendKnowledgeBase() {if(textMonomials.size()-textMonomialsToLemmes.size()>0)LowLevelTextProcessor.appendKnowledgeBase(this.textMonomials.subList(textMonomialsToLemmes.size(), textMonomials.size()),Settings.appPath+Settings.linkDelimiter+"saves"+Settings.linkDelimiter+"words.txt");}


}
