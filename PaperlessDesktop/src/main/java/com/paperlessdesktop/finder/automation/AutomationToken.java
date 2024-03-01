package com.paperlessdesktop.finder.automation;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.paperlessdesktop.util.Settings;

/**
 * This class represent an Automation's token object and does implement
 * the {@code Serializable} interface.
 * @author Aghiad Khertabeel
 * @version 1.0
 */
public class AutomationToken implements Serializable, Comparable<AutomationToken>{
    /**
     * Serial version for the serialization
     */
    @Serial
    private static final long serialVersionUID = -7203223317780311857L;
    /**
     * Title of the token
     */
    private String title;
    /**
     * A String of patterns that this token holds
     */
    private List<String> patterns = new ArrayList<>();
    /**
     * Folder name that this token later create
     */
    private String folderName;

    /**
     * Initializing automation token with a pattern only.
     * <p>
     * The title will be "Untitled Automation-Token (#)"
     * <p>
     * the folder name will follow the pattern.
     * @param pattern String pattern
     */
    public AutomationToken(String pattern){
        readPatterns(pattern);
        this.folderName = pattern;
        Settings settings = Settings.getInstance();
        settings.tokensCount++;
        this.title = "Untitled Automation-Token (" + settings.tokensCount + ")";
    }

    /**
     * Initializing automation token with a pattern and title.
     * <p>
     * the folder name will follow the pattern.
     * @param pattern Pattern String
     * @param title Title of the token
     */
    public AutomationToken(String pattern, String title){
        this(pattern);
        this.title = title;
    }

    /**
     * Initializing automation token with a pattern, title and folder name.
     * @param pattern String pattern
     * @param title Title of the token
     * @param folderName Folder name of the token
     */
    public AutomationToken(String pattern, String title, String folderName){
        this(pattern, title);
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public String getTitle() {
        return title;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method parse patterns from a string to
     * the {@code ArrayList} and add them.
     * <p>
     * Pattern:
     * <p>
     * sentence1, word2, sentence3
     * @param patternStr The String to parse
     */
    private void readPatterns(String patternStr){
        String[] array = patternStr.split(",");
        for (String string : array) {
            patterns.add(string.trim());
        }
    }

    /**
     * Set the pattern of this automation token.
     * @param patternStr String containing the pattern to change.
     */
    public void setPattern(String patternStr){
        patterns.clear();
        readPatterns(patternStr);
    }

    /**
     * Get formatted string of the patterns of this token.
     * @return The formatted String.
     */
    public String getPatternStr(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < patterns.size(); i++) {
            sb.append(patterns.get(i));
            if(i + 1 < patterns.size())
                sb.append(", ");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AutomationToken otherToken)) return false;
        for (String string : patterns) {
            if(!otherToken.patterns.contains(string)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(AutomationToken o) {
        return this.title.compareToIgnoreCase(o.getFolderName());
    }
}
