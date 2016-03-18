package wesupply.project_androidpoker;

public class Deck {

    //1-13 hjärter, 14-26 spader, 27-39 klöver, 40-52 ruter
    int [] Cards =  {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,
            33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52};

    public Deck(){

    }

    public void ShuffleDeck(){

        for (int i = 0; i < 10000; ++i){
            int k = (int) (Math.random()*52);
            int l = (int) (Math.random()*52);
            int temp = Cards[k];
            Cards[k] = Cards[l];
            Cards[l] = temp;
        }

        //	System.out.println(Arrays.toString(Cards));
    }

    public void getArray(){

    }
}