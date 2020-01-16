package lse;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 *
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile)
		    throws FileNotFoundException {

		        HashMap<String, Occurrence> keywords = new HashMap<String, Occurrence>();
		        Scanner sc = new Scanner(new File(docFile));


		        while(sc.hasNext()) {
		            String word = sc.next();
		            word = getKeyword(word);

		            if(word == null) {
		                continue; //if null it is not a keyword, so ignore it, iterates loop again
		            }
		            if(keywords.containsKey(word)) {
		                keywords.replace(word, keywords.get(word), new Occurrence(docFile, keywords.get(word).frequency + 1));
		                }
		            else {
		                keywords.put(word, new Occurrence(docFile, 1));
		                }
		            }

		        sc.close();

		        return keywords;
		    }

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table.
	 * This is done by calling the insertLastOccurrence method.
	 *
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/

		Set<Map.Entry<String,Occurrence>> itemSet = kws.entrySet(); //itemset is set of all items in kws hash table, including string and occurrence
		Iterator<Map.Entry<String,Occurrence>> iterate = itemSet.iterator();

		while(iterate.hasNext()){

			Entry<String, Occurrence> item = iterate.next();

			if(keywordsIndex.containsKey(item.getKey())){

				for(Map.Entry<String,ArrayList<Occurrence>> occList : keywordsIndex.entrySet()){

					if(item.getKey().equals(occList.getKey())){

							occList.getValue().add(item.getValue());
							insertLastOccurrence(occList.getValue());


					}

				}

			}
			else{
				ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
				temp.add(item.getValue());
				keywordsIndex.put((String) item.getKey(), temp);
			}



		}

	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 *
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'

	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {

        word = word.toLowerCase();
        String punct = "";
        String newWord = "";

        ArrayList <Character> chars = new ArrayList<Character>();



        for(int i = 0; i < word.length(); i++) {
            if(!Character.isAlphabetic(word.charAt(i))) {
                if(!(word.charAt(i) == ',' || word.charAt(i) == '.' || word.charAt(i) == '?' || word.charAt(i) == ':' || word.charAt(i) == ';' || word.charAt(i) == '!')) {
                    return null;
                }
            }
        }

        for(int i = 0; i < word.length(); i++) {
            chars.add(word.charAt(i));
        }

        for(int i = 0; i < chars.size(); i++) {
            if(chars.get(i) == ',' || chars.get(i) == '.' || chars.get(i) == '?' || chars.get(i) == ':' || chars.get(i) == ';' || chars.get(i) == '!') {
                punct += chars.get(i);
            } else {
                newWord += chars.get(i);
            }
        }

        if(newWord.length() == 1){
        	return null;
        }

        try {
            Scanner sc = new Scanner(new File("noiseWords.txt"));

            while(sc.hasNextLine()) {
                if((newWord).equals(sc.nextLine())) {
                    return null;
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if((newWord + punct).equals(word)) {
            return newWord;
        } else {
            return null;
        }
    }

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 *
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/

		if(occs.size()==1) return null;

		ArrayList<Integer> freq = new ArrayList<Integer>();
		ArrayList<Integer> midpoints = new ArrayList<Integer>();


			String tempDocName = occs.get(occs.size()-1).document;
			int numToInsert = occs.remove(occs.size()-1).frequency;

			for(int i = 0;i<occs.size(); i++){
				freq.add(occs.get(i).frequency);
			}

			int targetIndex = 0;

			int l = 0;
			int r = freq.size()-1;
			int m = 0;

			while(r>=l){
				 m = (l+r)/2;
				midpoints.add(m);

				if(occs.get(m).frequency == numToInsert){
					break;
				}

				if(occs.get(m).frequency > numToInsert){
					l = m + 1;
				}else{
					r = m - 1;
				}

			}

			if(freq.get(m) < numToInsert){
				occs.add(m,new Occurrence(tempDocName,numToInsert));
			}
			else{
				occs.add(new Occurrence(tempDocName,numToInsert));
			}


			return midpoints;

	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 *
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile)
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies.
	 *
	 *
	 * Ties in frequency values are broken in favor of the first keyword.
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 *
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 *
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches,
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {

		ArrayList<String> docsList = new ArrayList<String>();

		ArrayList<Occurrence> occsList1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occsList2 = keywordsIndex.get(kw2);
		int index1 = 0; //index for first arraylist
		int index2 = 0; //index for second arraylist
		int counter = 0; //max number of items in docslist must be 5

		if (occsList1 == null && occsList2 == null)
		{
			return docsList;
		}

		else if (occsList2 == null)
		{
			while (index2 < occsList2.size() && counter < 5)
			{
				docsList.add(occsList2.get(index2).document);
				index2++;
				counter++;
			}

		}

		else
		{
			while ((index1 < occsList1.size() || index2 < occsList2.size()) && counter < 5)
			{
				if (occsList1.get(index1).frequency > occsList2.get(index2).frequency && (!docsList.contains(occsList1.get(index1).document)))
				{
					docsList.add(occsList1.get(index1).document);
					index1++;
					counter++;
				}

				else if (occsList1.get(index1).frequency < occsList2.get(index2).frequency && (!docsList.contains(occsList2.get(index2).document)))
				{
					docsList.add(occsList2.get(index2).document);
					index2++;
					counter++;
				}

				else
				{
					if (!docsList.contains(occsList1.get(index1).document))
					{
						docsList.add(occsList1.get(index1).document);
						counter++;
						index1++;
					}

					else
					{
						index1++;
					}

					if ((!docsList.contains(occsList2.get(index2).document)))
					{
						if (counter < 5)
						{
							docsList.add(occsList2.get(index2).document);
							index2++;
							counter++;
						}
					}

					else
					{
						index2++;
					}
				}
			}
		}

		return docsList;
	}
}
