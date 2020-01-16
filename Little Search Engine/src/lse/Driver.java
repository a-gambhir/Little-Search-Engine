package lse;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class Driver {

    public static void main( String[] args ) throws FileNotFoundException, InputMismatchException {
        LittleSearchEngine lse = new LittleSearchEngine();
        Scanner sc = new Scanner(System.in);

        System.out.print( "mode?: " );
        int mode = sc.nextInt();

        if( mode == 0 ) {
            do {
                System.out.print("\nget keyword for: ");
                System.out.println(lse.getKeyword(sc.next()));
            } while (sc.hasNextLine());
        } else if( mode == 1 ) {
            System.out.print("\nwhat file?: ");
            String docFile = "";
            HashMap<String,Occurrence> hm = new HashMap<>();
            if( sc.hasNext() )
                docFile = sc.next();

            try { hm = lse.loadKeywordsFromDocument(docFile); } catch( FileNotFoundException fnfe ) { fnfe.printStackTrace(); }
            System.out.println( hm );
        } else if( mode == 2 ) {

        	ArrayList<Occurrence> ocal = new ArrayList<Occurrence>();
        	ArrayList<Integer> o = new ArrayList<Integer>();

        	ocal.add(new Occurrence("AliceCh1.txt",43));
        	ocal.add(new Occurrence("AliceCh1.txt",42));
        	ocal.add(new Occurrence("AliceCh1.txt",14));
        	ocal.add(new Occurrence("AliceCh1.txt",11));
        	ocal.add(new Occurrence("AliceCh1.txt",3));
        	ocal.add(new Occurrence("AliceCh1.txt",1));
        	ocal.add(new Occurrence("AliceCh1.txt",12));

        	o = lse.insertLastOccurrence(ocal);
        	

        } else if( mode == 3 ) {
            lse.makeIndex( "docs.txt", "noisewords.txt" );
            System.out.print( "HashMap: " + lse.keywordsIndex );
        } else if( mode ==  4 ) {
            lse.makeIndex( "docs.txt", "noisewords.txt" );
            System.out.print( "keywords: " );
            String kw1 = null; String kw2 = null;
            if( sc.hasNext() ) {
                kw1 = sc.next();
                kw2 = sc.next();
            }
            System.out.println( "\n" + lse.top5search( kw1, kw2 ) );
        }

        else {
            throw new InputMismatchException();
        }
    }
}
