package ScheduleClient.client.GUIObjects;

import ScheduleClient.client.DataAdapters.SectionAdapter;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SectionPopup extends PopupPanel {

    private SectionAdapter sa;

    public SectionPopup(SectionAdapter sa) {
        this.sa = sa;
        initializeWidget();
    }

    private void initializeWidget() {
        this.setStyleName("add-section-popup");

        VerticalPanel panel = new VerticalPanel();
        panel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

        DecoratorPanel header = new DecoratorPanel();
        header.setStyleName("popup-header");

        String headerText = sa.getSectionNum() + "    " + sa.getCredits() + " Credits";
        Label label = new Label(headerText);
        label.setWidth("100%");
        header.setWidth("100%");

        header.add(label);
        panel.add(header);

        DecoratorPanel bodyPanel = new DecoratorPanel();

        String bodyText = sa.getCourseName();

        bodyText += "<br>Students: " + sa.getCurrentStudents() + "/" + sa.getMaxStudents();
 
        if(sa.getCurrentStudents() >= sa.getMaxStudents()) {
            bodyText += " (<b>FULL</b>)";
        } else {
            bodyText += " (<b>OPEN</b>)";
        }

        bodyText += "<br>Professor: " + sa.getProfessor();
        bodyText += "<br>Call Num: " + sa.getCallNum() + "<br>";
        
        if( sa.getOnline() ) {
            bodyText += "ONLINE SECTION<br>";
        }

        bodyText += "Comments: " + sa.getComment();

        bodyPanel.add(new HTML(bodyText));

        panel.add(bodyPanel);

        this.add(new ScrollPanel(panel));
    }
}
