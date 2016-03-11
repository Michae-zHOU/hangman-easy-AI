/*
* Hangman AI in java (Messy Code Version just for fun)
* Author: Ziyao Zhou
* Use an array to store the times of correct guess characters 
* minus times of incorrect guess characters.
* Simply use a hashmap to store this array due to different length of 
* the words.
* Added some random Java GUI simply allow user to look at the
* Result
*/
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class AI{
    
    AI(){}
    
    public boolean checkDone(char tc,ArrayList<Character> done){
        for(int i = 0; i<done.size(); i++){
            if(tc == done.get(i))
                return true;
        }
        
        return false;
    }
    
    public char guess(String gamestate, int[] character, ArrayList<Character> done){
        
        PriorityQueue<node> pq = new PriorityQueue<node>(26, new Comparator<node>() {
            public int compare(node n1, node n2) {
                if(n1.quantity > n2.quantity)
                    return -1;
                if(n1.quantity < n2.quantity)
                    return 1;
                return 0;
            }
        });
        
        for(int i = 0; i<26 ; i++){
            node newNode = new node((char)('a'+i),character[i]);
            pq.add(newNode);
        }
        
        node topNode = pq.remove();
        char topChar = topNode.character;
        
        while(checkDone(topChar,done)){
            //System.out.print(" Done: -----"+topChar+ " -----");
            topNode = pq.remove();
            topChar = topNode.character;
        }
        
        return topChar;
    }
}

class node{
    public int quantity;
    public char character;
    
    node(char c, int q){
        quantity = q;
        character = c;
    }
}
    
class Windows{
    JLabel label1;
    JFrame f;
    JButton b;
    
    Windows(JLabel label2){
        f=new JFrame();
        
        label1 = new JLabel("-------------------HANGMAN AI----------------------");
        
        f.add(label2, JLabel.CENTER);
        f.add(label1, JLabel.CENTER);
        
        f.setSize(400,400);
        
        f.setLayout(new GridLayout(3, 3));
        f.setVisible(true);
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void addButton(JButton b){
        f.add(b);
        
        f.setLayout(new GridLayout(3, 3));
        f.setVisible(true);
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
    
public class hangman{
    public static int win;
    public static int totalgame;
    //public static int[] character = new int[26];
    public static JLabel label;
    public static Windows wp;
    public static JButton button;
    public static boolean state;
    public static Map<Integer,int[]> map;
    
    public static void main(String[] args) throws Exception{
        String filename = "word.txt";
        
        String line = null;
        
        ArrayList<String> ar = new ArrayList<String>();
        map = new HashMap<Integer,int[]>();
        
        label = new JLabel("...");
        
        wp = new Windows(label);
        button = new JButton("AI Play");
        wp.addButton(button);
        
        try{
            FileReader fr = new FileReader(filename);
            
            BufferedReader br = new BufferedReader(fr);
            
            while((line = br.readLine()) != null) {
                ar.add(line);
            }
            
            game(ar);
        }catch(FileNotFoundException ex){
            System.err.println("File can't be open");
        };
    }
    
    public static void game(ArrayList<String> ar){
        Random rand = new Random();
        
        Scanner in = new Scanner(System.in);
        
        AI robot = new AI();
        
        System.out.println("Start Game? (y/n)");
        
        String command = "";
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OnGame(ar,robot,rand);
            }
        });
        
        command = in.nextLine();
        
        while(command.equals("y")){
            
            OnGame(ar,robot,rand);
            
            command = in.nextLine();
            
            state = false;
        }
    }
    public static void OnGame(ArrayList<String> ar, AI robot, Random rand){
        for(int k = 0; k<10000 ; k++){
            int index = rand.nextInt(ar.size());
            
            System.out.println("Answer Word: "+ar.get(index));
            
            win += gamestart(ar.get(index), robot);
            
            printarray();
            
            totalgame++;
            
            String outLn = "Win: "+win+" Game: "+totalgame+
            " Win Rate: "+((double)win/(double)totalgame)*100.0f
            +"%";
            
            System.out.println(outLn);
            
            label.setText(outLn);
            
            System.out.println("Restart? (y/n)");
            
            index = rand.nextInt(ar.size());
        }
    }
    
    public static void printarray(){
        System.out.println("Char array:");
        Set set = map.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print(me.getKey() + ": \n");
            printar(map.get(me.getKey()));
        }
    }
    public static void printar(int[] ar){
        for(int i=0; i<ar.length; i++)
            System.out.print((char)('a'+i)+" "+ar[i]+" ");
        
        System.out.println("");
    }
    
    public static int gamestart(String word, AI robot){
        int wordLength = word.length();
        int[] character;
        
        if(map.get(wordLength)==null)
            character = new int[26];
        else
            character = map.get(wordLength);
        
        String curr = word.replaceAll("[a-z]","_");
        int error = 0;
        char guessChar;
        Scanner s = new Scanner(System.in);
        ArrayList<Character> done = new ArrayList<Character>();
        while(error < 3){
            int find = 0;
            
            System.out.println("Word: "+curr);
            System.out.print("Guess: ");
            
            guessChar = robot.guess(curr,character,done);
            System.out.println(guessChar);
            
            int alphabet = (int)(guessChar-'a');
            
            done.add(guessChar);
            
            for(int i = 0; i < word.length() ; i++){
                if(curr.charAt(i) == guessChar || curr.toLowerCase().charAt(i) == guessChar){
                    break;
                }
                else if(word.charAt(i) == guessChar || word.toLowerCase().charAt(i) == guessChar){
                    curr = replaceCharAt(curr, i, word.charAt(i));
                    character[alphabet]++;
                    find++;
                }
            }
            if(find == 0){
                character[alphabet]--;
                error++;
            }
            if(curr.equals(word)){
                break;
            }
        }
        
        if(error < 3){
            System.out.println("Success");
            map.put(wordLength,character);
            return 1;
        }
        else{
            System.out.println("Fail");
            map.put(wordLength,character);
            return 0;
        }
    }
    
    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }
}


