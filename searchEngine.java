// Kevin Li
// Student #: 260565522
// Dec 6, 2015
import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings) 
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
    // Below is the directory that contains all the internet files
    htmlParsing.internetFilesLocation = "internetFiles";
    wordIndex = new HashMap<String, LinkedList<String> > ();        
    internet = new directedGraph();             
    } // end of constructor2015
    
    
    // Returns a String description of a searchEngine
    public String toString () {
    return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception{
            // Initialize the things we need.
            LinkedList<String> content = htmlParsing.getContent(url);
            LinkedList<String> links = htmlParsing.getLinks(url);
            Iterator<String> content_it = content.iterator();
            Iterator<String> links_it = links.iterator();

            // 1. Add the vertex to the internet graph.
            internet.addVertex(url);
            internet.setVisited(url, true);

            // 2. Iterate over all words to add them to wordIndex if they're not in wordIndex,
            // and LinkedList.addLast the current url to them
            while (content_it.hasNext()) {
                String s = content_it.next();
                if (!wordIndex.containsKey(s)) {
                    LinkedList<String> new_links = new LinkedList<String>();
                    new_links.addLast(url);
                    wordIndex.put(s, new_links);
                }
                else {
                    LinkedList<String> old_links = wordIndex.get(s);
                    old_links.addLast(url);
                    wordIndex.put(s, old_links);
                }
            }

            // 3. Iterate over all links to add edges and call traverseInternet on them if they're
            // not in internet.
            while (links_it.hasNext()) {
                String l = links_it.next();
                boolean vis = internet.getVisited(l);
                if (!vis) {
                    traverseInternet(l);
                }

                internet.addEdge(url, l);
            }
    } // end of traverseInternet
    
    // Helper page rank computation function.
    double prFunc(String v) {
        // Get LinkedList of internet.getEdgesInto(v) and (double)getPageRank on those vertices
        // divided by out degree(cast int as double). Sum all of them. Multiply that by 0.5. Add 0.5
        LinkedList<String> e = internet.getEdgesInto(v);
        Iterator<String> e_it = e.iterator();
        double thisPR = 0;

        while (e_it.hasNext()) {
            String s = e_it.next();
            double s_pr = internet.getPageRank(s);
            double s_outDegree = internet.getOutDegree(s);
            thisPR += (s_pr/s_outDegree);
        }
        
        thisPR = (thisPR*0.5) + 0.5;
        return thisPR;
    }

    void computePageRanks() {
        // 1. Get LinkedList of vertices, then set all PR of vertices to 1.
        // 2. Start 100 iterations for calculating PR of all vertices.
        // 3. Loop over each vertex and use the PR formula to calculate its PR.
        
        LinkedList<String> vertices = internet.getVertices();
        Iterator<String> v_it = vertices.iterator();
        while (v_it.hasNext()) {
            String s = v_it.next();
            internet.setPageRank(s, 1);
        }
        
        for (int i = 0; i < 100; i++) {
            LinkedList<String> vxs = internet.getVertices();
            Iterator<String> vs = vertices.iterator();

            while (vs.hasNext()) {
                String a = vs.next();
                double pr = prFunc(a);
                internet.setPageRank(a, pr);
            }
        }
    } // end of computePageRanks
    
    
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
        try {
            LinkedList<String> results = new LinkedList<String>();
            results = wordIndex.get(query);
            Iterator<String> r = results.iterator();
            double best_score = 0;
            String best = "";

            while (r.hasNext()) {
                String current_url = r.next();
                double score = internet.getPageRank(current_url);
                if (score > best_score) {
                    best_score = score;
                    best = current_url;
                }
            }
            best = best + ", p.r. = " + best_score;
            return best;
        } catch (Exception e) {
            return ("No best site for this query.");
        }

    } // end of getBestURL
    
    
    
    public static void main(String args[]) throws Exception{        
    searchEngine mySearchEngine = new searchEngine();
    // to debug your program, start with.
    //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
    
    // When your program is working on the small example, move on to
    mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
    
    mySearchEngine.computePageRanks();
    
    BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
    String query;
    do {
        System.out.print("Enter query: ");
        query = stndin.readLine();
        if ( query != null && query.length() > 0 ) {
        System.out.println("Best site = " + mySearchEngine.getBestURL(query));
        }
    } while (query!=null && query.length()>0);              
    } // end of main
}

