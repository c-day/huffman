//credit to rosettacode.org for code sample
	
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.lang.*;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;
import org.jfree.ui.*;



public class Huffman {
	
	public static final int BLUEGRASS = 0;
	public static final int BLUES = 1;
	public static final int POP = 2;
	public static final int ROCK = 3;
	public static final int HAIR = 4;
	public static final int SPEED = 5;
	public static final int SYMPHONIC = 6;
	public static final int POWER = 7;
	
	//instance variables
	String fileList;
	int start;
	int end;
	Hashtable<String, Integer> dictionary;
	Hashtable<String, String>  encodedHash;
	
	public enum genre {
		METAL, COUNTRY
	};
	
	public enum subgenre {
		BLUEGRASS, BLUES, HAIR, POP, POWER, ROCK, SPEED, SYMPHONIC
	};
	
	
	//Constructor
	Huffman(String fileList, int start, int end) {
    	dictionary = new Hashtable<String, Integer>();
    	encodedHash = new Hashtable<String, String>();
    	
    	this.fileList = fileList;
    	this.start = start;
    	this.end = end;
	}
    	
	
	//Methods
	public void makeGraph() throws Exception {
		//init graph
		ApplicationFrame demo = new ApplicationFrame("Huffman Code");
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(new XYSeries(0));
		
		//init variables
	    File file = new File(fileList);
	    Scanner sc = new Scanner(file);
	    int count = 0;
	    String fileName = "lyricdata\\" + sc.next();
	    
	    //iterate through the list of files to make and combine dictionaries 
	    System.out.println("\nCreating a dictionary that contains all of the words used and their frequencies...\n");
	    
	    //loop until we get to the start point
	    while (count < start) {
	    	count++;
	    }
	    
	    //loop until the end point or no more file names
	    while (count < end && fileName != null) {
	    	//use the file given to update global hash
	    	updateHash(fileName);
	    	System.out.println("Added file: " + fileName);
	    	if(sc.hasNext()) {
	    		fileName = "lyricdata\\" + sc.next();
	    	} else {
	    		end = count;
	    		break;
	    	}
	    	count++;
	    }
	    sc.close();
	    
	    //smooth out the global dictionary
	    System.out.println("\n\nSmoothing out the dictioanry...");
		smooth();
		
		//build tree
		System.out.println("\n\nBuilding Huffman Code Tree...");
		HuffmanTree tree = buildTree();
		
		//build a hash to quickly look up a given words code
		System.out.println("\n\nBuilding encoded dictionary...");
		encodedHash = encodeWords(tree, new StringBuffer());
		
		/*use the dictionary created to compress all of the files in the fileList
		we compare the compression of Huffman code to that of block compression to get a compression ratio
		we use these ratios and dates to make a graph*/
		int genre = 0;
		int huff = 0;
		int bloc = blocCount();
		float ratio = 0;
		sc = new Scanner(file);
		
		System.out.println("\nMaking graph...");
		while (sc.hasNext()) {
			fileName = sc.next();
			//compute number of bits in huffman code
			huff = huffCount(fileName);
			System.out.println("\nBloc count: " + bloc + " Huffman count: " + huff);
			ratio = (float) huff/bloc;
			
			//remove all but the first 4 chars of the file name (the year)
			//fileName = fileName.substring(0, 4);
			//year = Integer.parseInt(fileName);
			if(fileName.toLowerCase().contains("pop")) {
				genre = POP;
			} else if(fileName.toLowerCase().contains("bluegrass")) {
				genre = BLUEGRASS;
			} else if(fileName.toLowerCase().contains("blues")) {
				genre = BLUES;
			} else if(fileName.toLowerCase().contains("hair")) {
				genre = HAIR;
			} else if(fileName.toLowerCase().contains("power")) {
				genre = POWER;
			} else if(fileName.toLowerCase().contains("rock")) {
				genre = ROCK;
			} else if(fileName.toLowerCase().contains("speed")) {
				genre = SPEED;
			} else if(fileName.toLowerCase().contains("symphonic")) {
				genre = SYMPHONIC;
			}
			
			//add data to graph
			data.getSeries(0).add(genre, ratio);
			System.out.println("Added data series: KKGenre?:" + genre + " Ratio:" + ratio);
		}
		sc.close();

		//create scatter plot
		String chartName = new String("Huffman Ratio using lyrics " + start + " to " + end);
		JFreeChart chart = ChartFactory.createScatterPlot(
		chartName, 					// chart title
		"Genre", 					// domain axis label
		"Compression Ratio (Huffman/Bloc)", 		// range axis label
		data, // data
		PlotOrientation.VERTICAL,	// orientation
		false, 						// include legend
		true, 						// tooltips
		false 						// urls
		); 
		 
		JPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		demo.setContentPane(chartPanel);  
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	
	public void updateHash(String fileName) throws Exception{
	    //try to open and setup to read file  
	    File file = new File(fileName);
	    Scanner sc = new Scanner(file);
	    String word = new String();
	    
	    //read all the words in the file
	    while(sc.hasNext()) {
	    	word = sc.next();
	    	
	    	//convert to lower case and remove punctuation
	    	word = word.toLowerCase();
	    	word = word.replaceAll("([a-z]+)[?:!.,;]*", "$1");
	    	
	    	//insert words into dictionary
	    	if ( dictionary.get(word) == null ) {
	    		//if it doesn't exist we add it
	    		dictionary.put(word, 1);
	    	} else {
	    		//if the word does exist we increment by 1
	    		int value = dictionary.get(word) + 1;
	    		dictionary.remove(word);
	    		dictionary.put(word, value);
	    	}
	    }
	    sc.close();
	}
	
	public void smooth() throws Exception {
		//try to open and setup to read file  
	    File files = new File(fileList);
	    Scanner sc = new Scanner(files);
	    String fileName = "lyricdata\\" + sc.next();
	    String word = "";
	    
	    //read all of the files
	    while ( sc.hasNext() ) {
	    	fileName = "lyricdata\\" + sc.next();
	    	
	    	//get current file
	    	File file = new File(fileName);
		    Scanner sc1 = new Scanner(file);
	    	
		    //read all of the words in the file
		    while( sc1.hasNext() ) {
		    	word = sc1.next();
				//convert to lower case and remove punctuation
				word = word.toLowerCase();
				word = word.replaceAll("([a-z]+)[?:!.,;]*", "$1");
				
				//insert words into dictionary
				if ( dictionary.get(word) == null ) {
					//if it doesn't exist we add it
					dictionary.put(word, 1);
				} else {
					//if the word does exist we do NOTHING
				}
		    }
		    sc1.close();
	    }
	    sc.close();
	}
	
	public HuffmanTree buildTree() {
		PriorityQueue<HuffmanTree> tree = new PriorityQueue<HuffmanTree>();
	    
		for (Entry<String, Integer> entry : dictionary.entrySet()) {
	    // initially, we have a forest of leaves
	    // one for each non-empty character
			tree.offer( new HuffmanLeaf(entry.getValue(), entry.getKey()) );
		}
	
	    assert tree.size() > 0;
	    // loop until there is only one tree left
	    while (tree.size() > 1) {
	        // two trees with least frequency
	        HuffmanTree a = tree.poll();
	        HuffmanTree b = tree.poll();
	
	        // put into new node and re-insert into queue
	        tree.offer(new HuffmanNode(a, b));
	    }
	    return tree.poll();
	}
	
	public Hashtable<String, String> encodeWords(HuffmanTree tree, StringBuffer prefix) {
		Hashtable<String, String> encodedHash = new Hashtable<String, String>();
	    assert tree != null;
	    if (tree instanceof HuffmanLeaf) {
	        HuffmanLeaf leaf = (HuffmanLeaf)tree;
	
	        //create a new word with the same name but different value (instead of frequency we use its code)
	        encodedHash.put(leaf.word, prefix.toString());
	
	    } else if (tree instanceof HuffmanNode) {
	        HuffmanNode node = (HuffmanNode)tree;
	
	        // traverse left
	        prefix.append('0');
	        encodedHash.putAll(encodeWords(node.left, prefix));
	        prefix.deleteCharAt(prefix.length()-1);
	
	        // traverse right
	        prefix.append('1');
	        encodedHash.putAll(encodeWords(node.right, prefix));
	        prefix.deleteCharAt(prefix.length()-1);
	    }
		return encodedHash;
	}
	
	public int blocCount() throws Exception {
		//to get the number of bits for block encoding we count the number of words in the dictionary and multiply by the log base 2 of that number
		int count = 0;
		for (Entry<String, String> entry : encodedHash.entrySet()) {
		    	count++;
			}

	    System.out.println("\n\nCount: " + count);
	    count *= Math.log(count)/Math.log(2);
	    return count;
	}
	
	
	public int huffCount(String fileName) throws Exception {
		//to get bits from Huffman we can read the file and use the dictionary to look up the length of each word encoded
		File file = new File("lyricdata\\" + fileName);
		System.out.println("file:::: : :: " + fileName);
		Scanner sc = new Scanner(file);
		String word = "";
		int count = 0;
		
		while (sc.hasNext()) {
			word = sc.next();
			try {
				count += encodedHash.get(word).length();
			} catch(Exception e) {
				//System.out.println("word " + word + " caused an error with hash of " + encodedHash.get(word));
			}
		}
		sc.close();
		
		return count;
	}
	
	/*public void printCodes(HuffmanTree tree, StringBuffer prefix) {
	    assert tree != null;
	    if (tree instanceof HuffmanLeaf) {
	        HuffmanLeaf leaf = (HuffmanLeaf)tree;
	
	        // print out character, frequency, and code for this leaf (which is just the prefix)
	        System.out.println(leaf.word + "\t\t" + leaf.frequency + "\t\t" + prefix);

	    } else if (tree instanceof HuffmanNode) {
	        HuffmanNode node = (HuffmanNode)tree;
	
	        // traverse left
	        prefix.append('0');
	        printCodes(node.left, prefix);
	        prefix.deleteCharAt(prefix.length()-1);
	
	        // traverse right
	        prefix.append('1');
	        printCodes(node.right, prefix);
	        prefix.deleteCharAt(prefix.length()-1);
	    }
	}
	
    public void printEncoded(String fileName, Hashtable<String, String> dictionary)throws IOException {
	    //try to open and read file  
	    File file = new File(fileName);
	    Scanner sc = new Scanner(file);
	    String word = new String("");
	    int count = 0;
	    
	    //setup file to write to
	    File fileOut = new File("G:\\Programming\\Eclipse\\huffman\\fileOut.txt");
	    
	    // if file doesn't exist, then create it
	    if (!fileOut.exists()) {
	    	fileOut.createNewFile();
	    }
	    FileWriter fw = new FileWriter(fileOut.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    
	    //start reading the file.
	    while( sc.hasNext() ) {
	    	word = sc.next();
	    	//convert to lower case and remove punctuation
	    	word = word.toLowerCase();
	    	word = word.replaceAll("([a-z]+)[?:!.,;]*", "$1");
	    	
	    	//if it's in the dictionary print it, otherwise just print out the actual word
		    if ( dictionary.containsKey(word) ) {
		    	System.out.print(dictionary.get(word));
		    	bw.write(dictionary.get(word));
		    } else {
		    	System.out.print(word);
		    	bw.write(word);
		    	count++;
		    }
    	}
	    System.out.println("\nNumber of NID errors: " + count);
	    //bw.write("\nNumber of NID errors: " + count);
	    sc.close();
	    bw.close();
	}
    

	public static void main(String argv[]) throws IOException {
		
	}*/
}
