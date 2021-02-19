package hr.example.treeapp.addTree;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagsLogic {
    public List<String> listOfHashtags;
    public String colorHastags (TextView treeDescription){
        String text = treeDescription.getText().toString();
        String regexPattern = "(#\\w+)";
        List<String> lstTag = new ArrayList<String>();
        Pattern p = Pattern.compile(regexPattern);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String hashtag = m.group(1);
            lstTag.add(hashtag);
        }
        String modifiedText=text;
        for (int i =0; i<lstTag.size(); i++){
            String replaceWith ="<span style='color:#2DB180'>"+lstTag.get(i)+"</span>";
            modifiedText=modifiedText.replaceAll(lstTag.get(i),replaceWith);
        }
        listOfHashtags=lstTag;
        return modifiedText;
    }
}
