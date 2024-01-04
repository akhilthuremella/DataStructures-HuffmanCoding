package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */ 
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        double chars = 0.0;
        int frequency []= new int [128];
        while(StdIn.hasNextChar()){
            char next = StdIn.readChar();
            chars++;
            frequency[next]++;
        }
        sortedCharFreqList = new ArrayList<>();
        for (int i = 0; i< frequency.length; i++){
            if (frequency[i]>0){
                sortedCharFreqList.add(new CharFreq((char)(i),(double) (frequency[i]/chars)));
            }
        }
        if (sortedCharFreqList.size()== 1.0){
            CharFreq x = sortedCharFreqList.get(0);
            sortedCharFreqList.add(new CharFreq((char)(x.getCharacter()+1), 0.0));

        }
        Collections.sort(sortedCharFreqList);

        }
        
 
	
    

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
        Queue <TreeNode> sourceQ =new Queue<TreeNode>(); //treenode
        Queue <TreeNode> targetQ =new Queue<TreeNode>(); //treenode
 
         
        for(int i=0; i<sortedCharFreqList.size(); i++){
             TreeNode p =new TreeNode(sortedCharFreqList.get(i),null,null); 
             sourceQ.enqueue(p); 
        }
     
        //for the first case 
        TreeNode firstChild=null; 
        TreeNode secondChild=null; 
        while(!(targetQ.size()==1 && sourceQ.isEmpty())){
         if(!sourceQ.isEmpty() && (targetQ.isEmpty()||(sourceQ.peek().getData().getProbOcc()<=targetQ.peek().getData().getProbOcc()))){
             firstChild=sourceQ.dequeue();
 
         }
         else{
             firstChild=targetQ.dequeue();
         }
         if((!sourceQ.isEmpty()&!targetQ.isEmpty())&& sourceQ.peek().getData().getProbOcc()<=targetQ.peek().getData().getProbOcc()){
             secondChild=sourceQ.dequeue(); 
         }
         else if((!targetQ.isEmpty()&&!sourceQ.isEmpty())&&sourceQ.peek().getData().getProbOcc()>targetQ.peek().getData().getProbOcc()){
             secondChild=targetQ.dequeue();
         }
         else if(!targetQ.isEmpty() && sourceQ.isEmpty()){
             secondChild=targetQ.dequeue(); 
         }
         else if(targetQ.isEmpty()&&!sourceQ.isEmpty()){
             secondChild=sourceQ.dequeue(); 
         }
         CharFreq addToTree =new CharFreq(null,firstChild.getData().getProbOcc()+secondChild.getData().getProbOcc()); 
         TreeNode p =new TreeNode(addToTree,firstChild,secondChild); 
         targetQ.enqueue(p);
        }
        huffmanRoot=targetQ.dequeue(); 
 
      
     }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
       encodings = new String[128];

        for (CharFreq charFreq : sortedCharFreqList) {
            stringCreatorB(charFreq, huffmanRoot, ""); 
        }

    }

    private boolean stringCreatorB(CharFreq charFreq, TreeNode treeNode, String binary) {
        if (charFreq.getCharacter() != null && treeNode.getData().getCharacter() != null && treeNode.getData().getCharacter().equals(charFreq.getCharacter())) {
            int number = (int) charFreq.getCharacter();
            encodings[number] = binary;
            return true;
        }

        if (treeNode.getLeft() != null) {
            if(stringCreatorB(charFreq, treeNode.getLeft(), binary + "0")) 
            return true;
        }

        if (treeNode.getRight() != null) {
            if(stringCreatorB(charFreq, treeNode.getRight(), binary + "1")) 
            return true;
        }
        return false;
    }
    
    
    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String encodingfinal = "";

        while(StdIn.hasNextChar()) {
            char character = StdIn.readChar();
            int num = (int) character;
            encodingfinal += encodings[num];
        }

        writeBitString(encodedFile, encodingfinal);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String bitString = readBitString(encodedFile);
        char[] array = new char[bitString.length()];

        int x = 0;
        while(x < bitString.length()) {
            array[x] = bitString.charAt(x);
            x++;
        }

        TreeNode node = huffmanRoot;
        for(int i = 0; i < array.length; i++) {            
            char charValue = array[i];           
            if(charValue == '0') {
                node = node.getLeft();
            }
            else {
                node = node.getRight();
            }
            if(node.getLeft() == null && node.getRight() == null) {
                StdOut.print(node.getData().getCharacter());
                node = huffmanRoot;
            }           
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
