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
    
    public Card(String imgFileName) throws Exception
    {
        this.imgFileName = imgFileName;
        if(imgFileName.length() == 3)
        {
            faceValue = Integer.parseInt(imgFileName.substring(0, 2));
            switch(imgFileName.charAt(2))
            {
                case 'c':
                    suit = Suit.CLUBS;
                    break;
                case 'd':
                    suit = Suit.DIAMONDS;
                    break;
                case 's':
                    suit = Suit.SPADES;
                    break;
                case 'h':
                    suit = Suit.HEARTS;
                    break;
                default:
                    throw new Exception();
            }
        }
        else if(imgFileName.length() == 2)
        {
            if(Character.isLetter(imgFileName.charAt(0)))
            {
                switch(imgFileName.charAt(0))
                {
                    case 'a':
                        faceValue = 1;
                        break;
                    case 'j':
                        faceValue = 11;
                        break;
                    case 'q':
                        faceValue = 12;
                        break;
                    case 'k':
                        faceValue = 13;
                        break;
                    default:
                        throw new Exception();
                }
                switch(imgFileName.charAt(1))
                {
                    case 'c':
                        suit = Suit.CLUBS;
                        break;
                    case 'd':
                        suit = Suit.DIAMONDS;
                        break;
                    case 's':
                        suit = Suit.SPADES;
                        break;
                    case 'h':
                        suit = Suit.HEARTS;
                        break;
                    default:
                        throw new Exception();
                }
            }
            else
            {
                faceValue = Integer.parseInt(imgFileName.substring(0,1));
                switch(imgFileName.charAt(1))
                {
                    case 'c':
                        suit = Suit.CLUBS;
                        break;
                    case 'd':
                        suit = Suit.DIAMONDS;
                        break;
                    case 's':
                        suit = Suit.SPADES;
                        break;
                    case 'h':
                        suit = Suit.HEARTS;
                        break;
                    default:
                        throw new Exception();
                }
            }
        }
        else
            throw new Exception();
        
        sortValue = faceValue; // clubs' sort value is face value, others have offset added below
        switch(suit)
        {
            case DIAMONDS:
                sortValue += 13;
                break;
            case SPADES:
                sortValue += 26;
                break;
            case HEARTS:
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
    
    public boolean equals(Card other)
    {
        return (faceValue == other.faceValue) && (suit == other.suit);
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
