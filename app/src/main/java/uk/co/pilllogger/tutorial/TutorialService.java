package uk.co.pilllogger.tutorial;

import java.util.HashMap;

/**
 * Created by alex on 25/01/2014.
 */
public class TutorialService {
    HashMap<String, TutorialPage> _tutorialPages = new HashMap<String, TutorialPage>();

    public TutorialService(HashMap<String, TutorialPage> tutorialPages) {
        _tutorialPages = tutorialPages;
    }

    public TutorialPage getTutorialPage(String tag){
        return _tutorialPages.get(tag);
    }
}
