import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


public class Word
{
   public Letter L1;
   public Letter L2;
   public Letter L3;
   public Letter L4;
   public Letter L5;
   public LinkedList<Letter> word;
   public String ActualWord;
   public int numLetters;

   Word(String n) {
    word = new LinkedList<>();
    ActualWord = n;
    L1 = new Letter((n.length() > 0) ? n.substring(0, 1) : "");
    L2 = new Letter((n.length() > 1) ? n.substring(1, 2) : "");
    L3 = new Letter((n.length() > 2) ? n.substring(2, 3) : "");
    L4 = new Letter((n.length() > 3) ? n.substring(3, 4) : "");
    L5 = new Letter((n.length() > 4) ? n.substring(4, 5) : "");

    word.add(L1);
    word.add(L2);
    word.add(L3);
    word.add(L4);
    word.add(L5);

    numLetters = n.length();
    }

  
    public void setValue(String n) {
        ActualWord = n;
        word.clear(); // Clear the stack
        
        L1 = new Letter((n.length() > 0) ? n.substring(0, 1) : "");
        L2 = new Letter((n.length() > 1) ? n.substring(1, 2) : "");
        L3 = new Letter((n.length() > 2) ? n.substring(2, 3) : "");
        L4 = new Letter((n.length() > 3) ? n.substring(3, 4) : "");
        L5 = new Letter((n.length() > 4) ? n.substring(4, 5) : "");
        
        word.add(L1);
        word.add(L2);
        word.add(L3);
        word.add(L4);
        word.add(L5);

        numLetters = n.length();
    }
    

   public String getActualWord()
   {
      return ActualWord;
   }

   public LinkedList<Letter> getword()
   {
      return word;
   }

   public Letter getL1()
   {
      return L1;
   }

   public Letter getL2()
   {
      return L2;
   }

   public Letter getL3()
   {
      return L3;
   }

   public Letter getL4()
   {
      return L4;
   }

   public Letter getL5()
   {
      return L5;
   }

   public Word popLetter(Word w)
   {
    Word newWord = new Word(w.ActualWord);
        
    // Remove the top letter from the stack
    w.word.pop();
    numLetters--;
    
    // If the stack size is less than 5, add default letters to reach length 5
    while (w.word.size() < 5) {
        w.word.add(new Letter(" "));
    }
    
    // Set the word stack of the new Word object to the modified stack
    newWord.word = w.word;
    
    return newWord;

   }


   public Word addLetter(String s)
   {
      if(this.getword().size() < 6)
      {
         String t = this.getActualWord() + s;
         numLetters++;
         Word temp = new Word(t);
         return temp;
      }
      else 
      {
         Word temp = new Word("");
         //system.out.println("adding to full word.");
         return temp;
      
      }
   }

   public boolean compareTo(Word answer){
        
        Map<Character, Integer>  answerMap = getLetterCounts(answer);
        Map<Character, Boolean> matchedLetters = new HashMap<>();
       

        for (int i = 0; i < 5; i++){
            
            char userLetter = Character.toUpperCase(((String)this.getword().get(i).getValue()).charAt(0));
            char answerLetter = Character.toUpperCase(((String)answer.getword().get(i).getValue()).charAt(0));

            if (userLetter == answerLetter){
                //system.out.println(userLetter + " is in the right place ");
                this.getword().get(i).setColor("#008000");
                //if we found direct match but it was already changed
                if(answerMap.get(userLetter) == 0){
                    //fix previous change
                    
                    for (int j = 0; j < 5; j++){
                        char userL = Character.toUpperCase(((String)this.getword().get(j).getValue()).charAt(0));
                        if(userL == userLetter){
                            this.getword().get(j).setColor("#808080");
                            break;
                        }
                    }

                }


                //add a match
                matchedLetters.put(answerLetter, true);
                //decrease the counts
                Integer count = answerMap.get(answerLetter);
                if (count != null) {
                    count--;
                    answerMap.put(answerLetter, count);
                }


            }
            else{
                matchedLetters.put(answerLetter, false);
                if(answerMap.containsKey(userLetter)){
                    
                    if(answerMap.get(userLetter) > 0){
                        //system.out.println(userLetter + " is in the word but in the wrong place");
                        this.getword().get(i).setColor("#E4D00A");
                        
                        Integer count = answerMap.get(userLetter);
                        if (count != null) {
                            count--;
                            answerMap.put(userLetter, count);
                        }

                
                    }
                    else{
                        //system.out.println(userLetter + " is not in the word 1 ");
                        this.getword().get(i).setColor("#808080");
                    }
                }
                else{
                    //system.out.println(userLetter + " is not in the word 2");
                    this.getword().get(i).setColor("#808080");
                   
                }
            }
        }

        boolean allValuesTrue = true;
        //system.out.println(matchedLetters);
        for (boolean value : matchedLetters.values()) {
            if (!value) {
                allValuesTrue = false;
                break;
            }
        }
        //system.out.println(allValuesTrue);
        return allValuesTrue;

   }

private Map<Character, Integer> getLetterCounts(Word word) {
    Map<Character, Integer> letterCounts = new HashMap<>();
    for (int i = 0; i < 5; i++) {
        char letter = Character.toUpperCase(word.getword().get(i).getValue().charAt(0));
        letterCounts.put(letter, letterCounts.getOrDefault(letter, 0) + 1);
    }
    return letterCounts;
}

   
   
}


	