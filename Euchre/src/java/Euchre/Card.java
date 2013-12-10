package Euchre;

/**
 *
 * @author JOHNSONEJ1
 */
public class Card {
    
    public enum Suit { CLUBS, DIAMONDS, SPADES, HEARTS };
    
    // Constructor - zero-argument per JavaBean specifications
    public Card()
    {
        // Default card is a 2 of clubs
        sortValue = 0;
        pointValue = 2;
        suit = Suit.CLUBS;
        faceValue = 2;
        imgFileName = "2c";
    }
    public Card(Suit s, int number)
    {
        suit = s;
        faceValue = number;
        
        switch(faceValue)
        {
            case 1:
                imgFileName = "a";
                break;
            case 11:
                imgFileName = "j";
                break;
            case 12:
                imgFileName = "q";
                break;
            case 13:
                imgFileName = "k";
                break;
            default:
                imgFileName = Integer.toString(faceValue);
        }
        
        sortValue = faceValue; // clubs' sort value is face value, others have offset added below
        
        switch(suit)
        {
            case CLUBS:
                imgFileName += "c";
                break;
            case DIAMONDS:
                imgFileName += "d";
                sortValue += 13;
                break;
            case SPADES:
                imgFileName += "s";
                sortValue += 26;
                break;
            case HEARTS:
                imgFileName += "h";
                sortValue += 39;
                break;
        }
        
        if(faceValue > 10)
            pointValue = 10;
        else if(faceValue == 1)
            pointValue = 11; // aces are usually 11 but can be 1 in some cases - this is handled in the game logic
        else
            pointValue = faceValue;
    }
    
    private int sortValue;
    private int pointValue;
    private Suit suit;
    private int faceValue;
    private String imgFileName;
    
    // JavaBean accessors
    public int getSortValue() { return sortValue; }
    public int getPointValue() { return pointValue; }
    public Suit getSuit() { return suit; }
    public int getFaceValue() { return faceValue; }
    public String getImgFileName() { return imgFileName; }
}
