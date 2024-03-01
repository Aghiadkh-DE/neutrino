package com.paperlessdesktop.finder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paperlessdesktop.finder.automation.AutomationToken;
import com.paperlessdesktop.util.Utility;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * This class holds the algorithm to find patterns in a text 
 * using the {@code Automations tokens} that it has.
 * <p>
 * The class is also responsible for managing the tokens.
 * @author Aghiad Khertabeel
 * @version 1.0
 */
public class PatternFinder implements Comparator<AutomationToken>{
    /**
     * List of the automations tokens
     */
    private List<AutomationToken> tokenList;

    private static PatternFinder instance;

    private PatternFinder(){
        tokenList = new ArrayList<>();
    }

    public static PatternFinder getInstance(){
        return instance == null ? instance = new PatternFinder() : instance;
    }

    public void setTokenList(List<AutomationToken> tokenList) {
        this.tokenList = tokenList;
    }

    public List<AutomationToken> getTokenList() {
        return tokenList;
    }

    /**
     * Adds a token based on a pattern only.
     * @param pattern String pattern.
     * @param automationListView A {@code ListView} GUI component to be updated.
     * @return True if the addition was a success, otherwise false.
     */
    public boolean addToken(String pattern, ListView<String> automationListView){
        if(tokenExistsPattern(pattern))
            return false;

        tokenList.add(new AutomationToken(pattern));
        updateAutomationsListAndGUI(automationListView);
        return true;
    }

    /**
     * Adds a token based on a pattern, title and folder name.
     * @param pattern String pattern.
     * @param title Title of token.
     * @param folderName Folder name of token.
     * @param automationListView A {@code ListView} GUI component to be updated.
     * @return True if the addition was a success, otherwise false.
     */
    public boolean addToken(String pattern, String title, String folderName, ListView<String> automationListView){
        if(tokenExists(pattern, title, folderName))
            return false;

        tokenList.add(new AutomationToken(pattern, title, folderName));
        updateAutomationsListAndGUI(automationListView);
        return true;
    }

    /**
     * Adds a token based on a pattern and title only.
     * @param pattern String pattern.
     * @param title Title of token.
     * @param automationListView A {@code ListView} GUI component to be updated.
     * @return True if the addition was a success, otherwise false.
     */
    public boolean addToken(String pattern, String title, ListView<String> automationListView){
        if(tokenExists(pattern, title, pattern))
            return false;
        
        tokenList.add(new AutomationToken(pattern, title));
        updateAutomationsListAndGUI(automationListView);
        return true;
    }

    /**
     * Edit the data of a specific automation token.
     * @param newPattern String pattern.
     * @param newTitle Title of token.
     * @param newFolderName Folder name of token.
     * @param automationListView A {@code ListView} GUI component to be updated.
     * @param previousToken Token object to update.
     * @return True if the addition was a success, otherwise false.
     */
    public boolean editToken(String newPattern, String newTitle, String newFolderName, ListView<String> automationListView, AutomationToken previousToken){
        if(tokenExistsExcept(newPattern, newTitle, newFolderName, previousToken))
            return false;

        previousToken.setTitle(newTitle);
        previousToken.setFolderName(newFolderName);
        previousToken.setPattern(newPattern);
        updateAutomationsListAndGUI(automationListView);

        return true;
    }

    /**
     * Checks if token with least one of the attributes already exists.
     * @param pattern Pattern of token.
     * @param title Title of token.
     * @param folderName Folder name of token.
     * @return True if the token exists, otherwise false.
     */
    public boolean tokenExists(String pattern, String title, String folderName){
        for (AutomationToken automationToken : tokenList) {
            if(title.equalsIgnoreCase(automationToken.getTitle())
            || folderName.equalsIgnoreCase(automationToken.getFolderName()))
                return true;
        }
        return tokenExistsPattern(pattern);
    }

    /**
     * Checks if token with least one of the attributes already exists.
     * <p>
     * The automation token as a parameter will be excluded.
     * @param pattern Pattern of token.
     * @param title Title of token.
     * @param folderName Folder name of token.
     * @param tokenToExclude Token to exclude.
     * @return True if the token exists, otherwise false.
     */
    public boolean tokenExistsExcept(String pattern, String title, String folderName, AutomationToken tokenToExclude){
        for (AutomationToken automationToken : tokenList) {
            if(title.equalsIgnoreCase(automationToken.getTitle()) && !tokenToExclude.getTitle().equalsIgnoreCase(automationToken.getTitle())
            || folderName.equalsIgnoreCase(automationToken.getFolderName()) && !tokenToExclude.getFolderName().equalsIgnoreCase(automationToken.getFolderName()))
                return true;
        }
        return patternExistsElseWhereExcept(pattern, tokenToExclude);
    }

    /**
     * Checks if a token with this title already exists.
     * @param title Title to search for.
     * @return True if title exists, otherwise false.
     */
    public boolean titleExists(String title){
        for (AutomationToken automationToken : tokenList) {
            if(automationToken.getTitle().equalsIgnoreCase(title))
                return true;
        }
        return false;
    }

    /**
     * Checks if a token with this title already exists.
     * <p>
     * The automation token parameter as exception is excluded during the checks.
     * @param title Title to search for.
     * @param exception Automation token to exclude
     * @return True if title exists, otherwise false.
     */
    public boolean titleExistsExcept(String title, AutomationToken exception){
        for (AutomationToken automationToken : tokenList) {
            if(!automationToken.getTitle().equalsIgnoreCase(exception.getTitle()))
                if(automationToken.getTitle().equalsIgnoreCase(title))
                    return true;
        }
        return false;
    }

    /**
     * Checks if a token with folder name exists.
     * @param folderName Folder name to search for.
     * @return True if a token with folder name exists, otherwise false.
     */
    public boolean folderNameExists(String folderName){
        for (AutomationToken automationToken : tokenList) {
            if(automationToken.getFolderName().equalsIgnoreCase(folderName))
                return true;
        }
        return false;
    }

    /**
     * Checks if a token with folder name exists. This method checks all automation
     * tokens except the one passed as a parameter
     * @param folderName Folder name to search for.
     * @param exception A Reference on automation token that represents the exception.
     * @return True if a token with folder name exists, otherwise false.
     */
    public boolean folderNameExistsExcept(String folderName, AutomationToken exception){
        for (AutomationToken automationToken : tokenList) {
            if(automationToken.getFolderName().equalsIgnoreCase(folderName) && 
            !automationToken.getFolderName().equalsIgnoreCase(exception.getFolderName()))
                return true;
        }
        return false;
    }

    /**
     * Calls {@code patternExistsElseWhere}
     * @param pattern Pattern to check.
     * @return True if token with this pattern exists, otherwise false.
     */
    public boolean tokenExistsPattern(String pattern){
        return patternExistsElseWhere(pattern);
    }

    /**
     * Checks if a text matches with the given pattern.
     * @param pattern A list with patterns to check.
     * @param text Input text to compare with.
     * @return True if matched, otherwise false.
     */
    public boolean keyWordFound(List<String> pattern, String text){
        //check for all pattern
        for(String string : pattern){
            //Single word or sentence regex
            String regex = "\\b" + string + "\\b";
            Pattern patternObj = Pattern.compile(regex);
            Matcher matcher = patternObj.matcher(text);
            if(!matcher.find())
                return false;

        }
        return true;
    }

    /**
     * This method calls {@code keyWordFound} to find the {@code AutomationToken}
     * that matches its patterns with the given text.
     * @param text Text to compare patterns with.
     * @return A reference on a {@code AutomationToken} Object, otherwise {@code null}.
     */
    public AutomationToken foundByThisToken(String text){
        for (AutomationToken automationToken : tokenList) {
            if(keyWordFound(automationToken.getPatterns(), text))
                return automationToken;
        }
        return null;
    }

    /**
     * Checks if the given pattern exists in another token.
     * @param pattern pattern to check.
     * @return True if the pattern matches with the pattern of another token, otherwise false.
     */
    public boolean patternExistsElseWhere(String pattern){
        String[] array = pattern.split(",");
        List<String> list = new ArrayList<>();
        for (String string : array) 
            list.add(string.trim());

        for (AutomationToken token : tokenList) {
            if(new HashSet<>(token.getPatterns()).containsAll(list))
                return true;   
        }
        return false;
    }

    /**
     * Checks if the given pattern exists in another token.
     * <p>
     * The exception automation token is not considered during the checking.
     * @param pattern pattern to check.
     * @param exception Token to exclude
     * @return True if the pattern matches with the pattern of another token, otherwise false.
     */
    public boolean patternExistsElseWhereExcept(String pattern, AutomationToken exception){
        String[] array = pattern.split(",");
        List<String> list = new ArrayList<>();
        for (String string : array) 
            list.add(string.trim());

        for (AutomationToken token : tokenList) {
            if(!exception.equals(token) && new HashSet<>(token.getPatterns()).containsAll(list))
                return true;
        }
        return false;
    }

    /**
     * This method updates the GUI components (List) 
     * @param automationListView ListView to update.
     */
    public void updateAutomationsListAndGUI(ListView<String> automationListView){
        //clear the list
        automationListView.getItems().clear();
        //sort the token list too
        tokenList.sort(this);
        //loop through the tokens
        for (AutomationToken automationToken : tokenList) {
            StringBuilder stringbuilder = new StringBuilder("[");
            for (int i = 0; i < automationToken.getPatterns().size(); i++) {
                stringbuilder.append(automationToken.getPatterns().get(i)).append(i != automationToken.getPatterns().size() - 1 ? ", " : "");
            }
            stringbuilder.append("]");
            automationListView.getItems().add(automationToken.getTitle() + ", " + stringbuilder + ", " + automationToken.getFolderName());
        }
        //sort the tokens base on title
//        Platform.runLater(() -> automationListView.getItems().sort(String::compareToIgnoreCase));

    }

    /**
     * Remove an automation based on selection from the GUI ListView.
     * <p>
     * @deprecated the method is deprecated because it only removes one single automation
     * at a time, which is considered a bad practice. Use {@link #removeAutomation(ListView, ObservableList)} instead.
     * <p/>
     * @param index The index of the item to be removed.
     * @param automationListView the listView to update.
     */
    @Deprecated
    public void removeAutomation(ListView<String> automationListView, int index){
        tokenList.remove(index);
        updateAutomationsListAndGUI(automationListView);
    }

    /**
     * Removes all selected indices of an {@link ObservableList} from a {@link ListView}
     * @param automationListView Automation list view to update accordingly
     * @param indices indexes selected by user that should be removed
     */
    public void removeAutomation(ListView<String> automationListView, ObservableList<Integer> indices){
        AutomationToken[] temp = new AutomationToken[tokenList.size()];
        tokenList.toArray(temp);
        tokenList.clear();

        indices.forEach(index -> temp[index] = null);

        tokenList.addAll(Utility.buildListFromArray(temp, true));
        updateAutomationsListAndGUI(automationListView);
    }

    /**
     * Gets automation token at a specific index.
     * @param index index to check
     * @return A reference on the automation token at the given index.
     * @throws RuntimeException if the index was invalid or out of bounds of token list.
     */
    public AutomationToken getAutomationToken(int index){
        if(index >= 0 && index < tokenList.size()){
            return tokenList.get(index);
        }

        throw new RuntimeException("Selected index cannot be accessed from the list!");
    }


    @Override
    public int compare(AutomationToken o1, AutomationToken o2) {
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }
}
