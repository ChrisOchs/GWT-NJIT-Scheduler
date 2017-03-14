package ScheduleClient.client;

import ScheduleClient.client.GUIObjects.ScheduleGrid;
import ScheduleClient.client.DataAdapters.SectionAdapter;
import ScheduleClient.client.GUIObjects.CoursePopup;
import ScheduleClient.client.GUIObjects.SectionPopup;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

/**
 * Main entry point.
 */
public class MainEntryPoint implements EntryPoint {

    private ScheduleClientServiceAsync service;
	
    static int i = 0;

    public MainEntryPoint() {
	
    }

    enum TreeItemType {
        Subject, Course
    };

    class CourseTreeItem extends TreeItem implements MouseOverHandler, MouseOutHandler {
	
        boolean wasOpened;
        boolean dataLoading;
		
        private TreeItemType type;
        private String title;
        private String desc;

        private Label nameLabel;
        private CoursePopup popup;

        public CourseTreeItem(TreeItemType type) {
            this.wasOpened = false;
            this.type = type;
            this.dataLoading = false;
        }

        public CourseTreeItem(TreeItemType type, final String s, final String description) {
            super(new Label(s));
            this.type = type;
            this.title = s;
            this.desc = description;

            if(type == TreeItemType.Course) {
                HorizontalPanel panel = new HorizontalPanel();
                Label lab = new Label(s);
                lab.addMouseOverHandler(this);
                panel.add(lab);

                lab.addMouseOverHandler(this);
                lab.addMouseOutHandler(this);

                Button autoSchedButton = new Button("A.S.");
                autoSchedButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent e) {
                     searchTab.selectTab(1);
					 
                        if(!autoScheduleClasses.contains(s)) {
                            autoScheduleBox.addItem(s);
                            autoScheduleClasses.add(s);

                        }
                    }
                });

                panel.add(autoSchedButton);

                this.setWidget(panel);
            }
        }

        public void onMouseOver(MouseOverEvent moe) {
            if(popup == null) {
                popup = new CoursePopup(title, desc);
            }
            
            popup.setPopupPosition(this.getAbsoluteLeft() + 175, this.getAbsoluteTop() + 8);
            popup.show();
        }

        public void onMouseOut(MouseOutEvent moe) {
            popup.hide();
        }

        public void setDataLoading(boolean value) {
            dataLoading = value;
        }

        public boolean getDataLoading() { return dataLoading; }

        public void setWasOpened(boolean value) {
            wasOpened = value;
        }

        public boolean getWasOpened() {
            return wasOpened;
        }

        public TreeItemType getType() {
            return type;
        }
    }

    class SectionTreeItem extends TreeItem implements
            MouseOverHandler, MouseOutHandler {

        private SectionAdapter sectionAdapter;
        private Label infoLabel;
        private SectionPopup popup;

        public SectionTreeItem(SectionAdapter sectionAdapter) {
            super(new Label(sectionAdapter.getSectionNum()));
            this.sectionAdapter = sectionAdapter;

            infoLabel = (Label) getWidget();
            infoLabel.addMouseOverHandler(this);
            infoLabel.addMouseOutHandler(this);
        }

        public SectionAdapter getSectionAdapter() {
            return sectionAdapter;
        }

        public void onMouseOver(MouseOverEvent moe) {
            if (popup == null) {
                popup = new SectionPopup(sectionAdapter);
            }

            popup.setPopupPosition(this.getAbsoluteLeft() + 150, this.getAbsoluteTop() + 8);
            popup.show();
        }

        public void onMouseOut(MouseOutEvent moe) {
            popup.hide();
        }
    }

    private ListBox semesterBox;
    private Tree courseTree;
	
	private TabPanel searchTab;
    private ArrayList<SectionAdapter> searchResults;
	
	private RadioButton profButton;
    private RadioButton courseNameButton;

    private ScheduleGrid scheduleGrid;
	
    private ArrayList<SectionAdapter> autoScheduleSections;

    private ListBox historyBox;
    private ListBox autoScheduleBox;

    private ArrayList<String> autoScheduleClasses;
	
	private MultiWordSuggestOracle oracle;

    public void onModuleLoad() {

        // Create schedule builder page
        service = GWT.create(ScheduleClientService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) service;

        //endpoint.setServiceEntryPoint("http://web.njit.edu/~cro3/servlet/ScheduleClientService");
        endpoint.setServiceEntryPoint("/ScheduleClient/ScheduleClientService");

        RootPanel.get().add(createMainPage());
    }

    private Widget createMainPage() {

        DockPanel dockPanel = new DockPanel();
        dockPanel.setWidth("1024");

        dockPanel.add(createLogoBar(), DockPanel.NORTH);
        dockPanel.add(createNavigationBar(), DockPanel.WEST);

        HorizontalPanel schedulePanel = new HorizontalPanel();
        schedulePanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

        DecoratorPanel decoratorPanel = new DecoratorPanel();
        decoratorPanel.setWidth("20");

        schedulePanel.add(decoratorPanel);
        schedulePanel.add(getScheduleGridPanel());

        decoratorPanel = new DecoratorPanel();
        decoratorPanel.setWidth("20");
        schedulePanel.add(decoratorPanel);

        dockPanel.add(schedulePanel, DockPanel.CENTER);

        return dockPanel;
    }

    private Widget createLogoBar() {
	
        Image image = new Image("http://branding.njit.edu/branding/image/logos/njit_rgbkr_red_139x75.gif");
		
        VerticalPanel panel = new VerticalPanel();
        DockPanel logoPanel = new DockPanel();
		
        logoPanel.add(image, DockPanel.WEST);
		
        logoPanel.add(new Label("NJIT Schedule Builder 1.0 RC1"), DockPanel.NORTH);
        logoPanel.add(new Label("Developed By: Christopher Ochs, Christopher Hukushi, " +
                "Julian Raymar, and Georgios Zavolas"), DockPanel.SOUTH);
				
        logoPanel.setStyleName("logo-panel");
        logoPanel.setWidth("100%");
        panel.add(logoPanel);
        panel.setWidth("100%");

        panel.add(new Label("     "));

        return panel;
    }

    
    private Widget createNavigationBar() {
        
        VerticalPanel navPanel = new VerticalPanel();
        navPanel.add(getSemesterSelectPanel());

        DecoratorPanel spacePanel = new DecoratorPanel();
        spacePanel.setHeight("20");

        navPanel.add(spacePanel);


        VerticalSplitPanel navSplit = new VerticalSplitPanel();
        navSplit.setTopWidget(searchTabPanel());
        
        navPanel.add(spacePanel);
        navSplit.setBottomWidget(getCourseSelectPanel());
        navSplit.setHeight("1200");
        navSplit.setWidth("300");
        navSplit.setSplitPosition("340");
        navPanel.add(navSplit);

        return navPanel;
    }

    // Creates the "Select Semester" widget. Allows the user to select an
    // available semester.
    private Widget getSemesterSelectPanel() {
	
        VerticalPanel semesterSelectPanel = new VerticalPanel();
        semesterSelectPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        semesterSelectPanel.setStyleName("nav-panel");

        // Add Title
        semesterSelectPanel.add(new Label("Select Semester"));

        // Create semester drop down box
        semesterBox = new ListBox();
        semesterBox.setWidth("250");

        semesterBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent ce) {
			
                // Callback function for setting the current semester
                final AsyncCallback setSemesterCallback = new AsyncCallback() {

                    // On Success load the classes that are in the selected classes
                    public void onSuccess(Object o) {
                        courseTree.removeItems();
						
                        scheduleGrid.clearSchedule();
						
                        loadSemesterSubjects();

                        final AsyncCallback<String[]> loadOracleDataCallback =
                                new AsyncCallback<String[]>() {
                                    // On Success load the professors into the oracle

                                    public void onSuccess(String[] data) {
                                        loadIntoOracle(data);
                                    }

                                    // Display a warning on failure
                                    public void onFailure(Throwable caught) {
                                        Window.alert("Warning: Error loading professors.");
                                    }
                                };

                        if (profButton.getValue()) {
                            service.getProfessors(loadOracleDataCallback);
                        } else {
                            service.getCourseNames(loadOracleDataCallback);
                        }
                    }

                    // Display a warning on failure
                    public void onFailure(Throwable caught) {
                        Window.alert("Warning: Error setting selected semester.");
                    }
                };

                String selectedSemester = semesterBox.getItemText(semesterBox.getSelectedIndex());
                service.setSelectedSemester(selectedSemester, setSemesterCallback);
            }
        });

        // Load the semesters
        semesterSelectPanel.add(semesterBox);

        return semesterSelectPanel;
    }

    // Creates the "select course" tree and initializes the values in it
    private Widget getCourseSelectPanel() {
        VerticalPanel courseSelectPanel = new VerticalPanel();
        courseSelectPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        courseSelectPanel.setStyleName("nav-panel");

        courseSelectPanel.add(new Label("Select Course"));

        courseTree = new Tree();

        // Creates a handler to handle clicking the "plus" icon button next
        // to a subject or course
        courseTree.addOpenHandler(new OpenHandler<TreeItem>() {

            public void onOpen(OpenEvent event) {
                final CourseTreeItem selectedItem = (CourseTreeItem) event.getTarget();

                courseTree.setSelectedItem(null);

                if (selectedItem.getWasOpened()) {
                    return;
                } else {
                    selectedItem.setWasOpened(true);
                }

                // If its a course expanding
                if (selectedItem.getType() == TreeItemType.Course) {

                    // Creates a callback function that loads the sections
                    final AsyncCallback<SectionAdapter[]> setSectionsCallback =
                            new AsyncCallback<SectionAdapter[]>() {

                                // On Success load the sections that are in the selected course
                                public void onSuccess(SectionAdapter[] sections) {
                                    for (SectionAdapter section : sections) {
                                        selectedItem.addItem(new SectionTreeItem(section));
                                    }

                                    selectedItem.setDataLoading(false);
                                }

                                // Display a warning on failure
                                public void onFailure(Throwable caught) {
                                    Window.alert("Warning: Error loading sections for selected course.");
                                }
                            };

                    if( !selectedItem.getDataLoading() ) {
                        selectedItem.removeItems();
                        selectedItem.setDataLoading(true);
                        service.getAvailableSections(selectedItem.getText(), setSectionsCallback);
                    }
                } else if (selectedItem.getType() == TreeItemType.Subject) { // Is subject expanding

                    // Create a callback function to load courses in a selected subject
                    final AsyncCallback<String[]> setCoursesCallback = new AsyncCallback<String[]>() {

                        // On Success load the coureses that are in the selected subject
                        public void onSuccess(String[] courses) {
                            for (String course : courses) {

                                String [] courseString = course.split("\\|");

                                String name = courseString[0];
                                String description = "";

                                if(courseString.length > 1) {
                                     description = courseString[1];
                                }
                                
                                CourseTreeItem courseTreeItem =
                                        new CourseTreeItem(TreeItemType.Course, name, description);
                                courseTreeItem.addItem(new TreeItem(""));

                                selectedItem.addItem(courseTreeItem);
                            }
                        }

                        // Display a warning on failure
                        public void onFailure(Throwable caught) {
                            Window.alert("Warning: Error loading courses for selected semester.");
                        }
                    };

                    selectedItem.removeItems();
                    service.getAvailableCourses(selectedItem.getText(), setCoursesCallback);
                } else {
                    // What the hell did they open?
                }

            }
        });

        // Add handlers to handle course selections
        courseTree.addSelectionHandler(new SelectionHandler<TreeItem>() {

            public void onSelection(SelectionEvent event) {
                if (courseTree.getSelectedItem() instanceof SectionTreeItem) {
                    SectionTreeItem sti = (SectionTreeItem) courseTree.getSelectedItem();
                    scheduleGrid.displaySection(sti.getSectionAdapter());
                } else if (courseTree.getSelectedItem() instanceof CourseTreeItem) {
                    final CourseTreeItem cti = (CourseTreeItem) courseTree.getSelectedItem();

                    if(cti.getType() == TreeItemType.Course) {
                        if(cti.getWasOpened()) {
                            int children = cti.getChildCount();

                            ArrayList<SectionAdapter> sections = new ArrayList<SectionAdapter>();

                            for(int c = 0; c < children; c++) {
                                SectionTreeItem sti = (SectionTreeItem)cti.getChild(c);
                                sections.add(sti.getSectionAdapter());
                            }

                            scheduleGrid.displaySections(sections);
                        }
                        else {
                            if(cti.getDataLoading()) { return; }

                            // Creates a callback function that loads the sections then displays them
                            final AsyncCallback<SectionAdapter[]> setSectionsCallback =
                                    new AsyncCallback<SectionAdapter[]>() {

                                        // On Success load the sections that are in the selected course
                                        public void onSuccess(SectionAdapter[] sections) {
                                            ArrayList<SectionAdapter> sectionsList =
                                                    new ArrayList<SectionAdapter>();

                                            for (SectionAdapter section : sections) {
                                                cti.addItem(new SectionTreeItem(section));
                                                sectionsList.add(section);
                                            }

                                            scheduleGrid.displaySections(sectionsList);

                                            cti.setWasOpened(true);
                                            cti.setDataLoading(false);
                                        }

                                        // Display a warning on failure
                                        public void onFailure(Throwable caught) {
                                            Window.alert("Warning: Error loading sections for selected course.");
                                        }
                                    };

                            // Clear the subtree
                            cti.removeItems();
                            cti.setDataLoading(true);

                            // Load the sections
                            service.getAvailableSections(cti.getText(), setSectionsCallback);
                        }
                    }
                } else {
                    courseTree.setSelectedItem(null);
                }
            }
        });

        courseTree.setStyleName("course-tree");

        // Put the tree into a scroll panel so it can expand
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidth("250");
        scrollPanel.setHeight("600");
        scrollPanel.add(courseTree);

        DecoratorPanel treePanel = new DecoratorPanel();
        treePanel.add(scrollPanel);
        courseSelectPanel.add(treePanel);

        return courseSelectPanel;
    }

    private Widget getScheduleGridPanel() {

        ScrollPanel gridScroller = new ScrollPanel();

        scheduleGrid = new ScheduleGrid(service, ScheduleGrid.GridType.WeekdaysAndSat, 8, 22,
                historyBox);
        scheduleGrid.setStyleName("nav-panel");
        gridScroller.setStyleName("nav-panel");
        gridScroller.setWidth("900");
        gridScroller.setHeight("800");
        //gridScroller.add(scheduleGrid);

        return scheduleGrid;
    }
	
	
    private Widget searchTabPanel() {
        VerticalPanel historySearchPanel = new VerticalPanel();
        historySearchPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        historySearchPanel.setStyleName("nav-panel");
        historySearchPanel.setWidth("250");
        historySearchPanel.setHeight("250");

        searchTab = new TabPanel();
        searchTab.setStyleName("gwt-TabPanel");

        searchTab.add(getSearchPanel(), "Search");
        searchTab.add(getAutoSchedulePanel(), "Auto-Scheduler");
        searchTab.add(getHistoryPanel(), "History");
        searchTab.setWidth("250");
        searchTab.setAnimationEnabled(true);
        searchTab.selectTab(0);

        historySearchPanel.add(searchTab);

        return historySearchPanel;
    }

    private Widget getHistoryPanel() {
        VerticalPanel historyPanel = new VerticalPanel();
        historyPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

        historyPanel.add(new Label("History of Added Classes"));

        historyBox = new ListBox(true);

        ScrollPanel historyScroll = new ScrollPanel(historyBox);
        historyBox.setWidth("240");
        historyBox.setHeight("180");

        historyBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e) {
                String value = historyBox.getValue(historyBox.getSelectedIndex());
                scheduleGrid.navigateHistory(value);
            }
        });

        historyPanel.add(historyScroll);

        return historyPanel;
    }

    private void loadIntoOracle(String[] data) {
        oracle.clear();

        for (String s : data) {
            oracle.add(s);
        }
    }

    private Widget getSearchPanel() {

        searchResults = new ArrayList<SectionAdapter>();

        VerticalPanel searchPanel = new VerticalPanel();
        searchPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        searchPanel.setWidth("250");
        searchPanel.setHeight("180");

        searchPanel.add(new Label("Search for Professor or Course"));

        HorizontalPanel buttonPanel = new HorizontalPanel();
        oracle = new MultiWordSuggestOracle(); 

        final AsyncCallback<String[]> loadOracleDataCallback =
                new AsyncCallback<String[]>() {
                    // On Success load the professors into the oracle
                    public void onSuccess(String[] data) {
                        loadIntoOracle(data);
                    }

                    // Display a warning on failure
                    public void onFailure(Throwable caught) {
                        Window.alert("Warning: Error loading professors.");
                    }
                };

        // Load the sections
        service.getProfessors(loadOracleDataCallback);

        profButton = new RadioButton("search", "Professor");
        profButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e) {
                searchResults.clear();
                service.getProfessors(loadOracleDataCallback);
            }
        });
        profButton.setValue(true);

        courseNameButton = new RadioButton("search", "Course Name");
        courseNameButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e) {
                searchResults.clear();
                service.getCourseNames(loadOracleDataCallback);
            }
        });

        buttonPanel.add(profButton);
        buttonPanel.add(courseNameButton);

        searchPanel.add(buttonPanel);

        final SuggestBox searchBox = new SuggestBox(oracle);
        searchBox.setWidth("240");
        searchBox.setStyleName("gwt-SuggestBox");
        searchBox.setPopupStyleName("gwt-SuggestBoxPopup");
        searchPanel.add(searchBox);

        Button searchButton = new Button("Search");

        final ListBox searchList = new ListBox(true);
        ScrollPanel searchScroll = new ScrollPanel(searchList);
        searchList.setWidth("240");
        searchList.setHeight("180");

        searchList.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e) {
                scheduleGrid.displaySection(searchResults.get(searchList.getSelectedIndex()));
            }
        });

        final AsyncCallback<SectionAdapter[]> loadSearchDataCallback =
                new AsyncCallback<SectionAdapter[]>() {
                    // On Success load the professors into the oracle
                    public void onSuccess(SectionAdapter[] data) {
                        for(SectionAdapter sa : data) {
                            searchResults.add(sa);
                            searchList.addItem(sa.getSectionNum());
                        }

                        scheduleGrid.displaySections(searchResults);
                    }

                    // Display a warning on failure
                    public void onFailure(Throwable caught) {
                        Window.alert("Warning: Error loading sections.");
                    }
                };

        searchButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent e) {
                if (profButton.getValue()) {
                    service.getSectionsByProfessor(searchBox.getText(),
                            loadSearchDataCallback);
                } else {
                    service.getAvailableSections(searchBox.getText(),
                            loadSearchDataCallback);
                }

                searchList.clear();
                searchResults.clear();
            }
        });

        searchPanel.add(searchButton);

        searchPanel.add(searchScroll);

        return searchPanel;
    }

    private Widget getAutoSchedulePanel() {
        autoScheduleSections = new ArrayList<SectionAdapter>();
        
        VerticalPanel panel = new VerticalPanel();
        HorizontalPanel horpanel = new HorizontalPanel();

        autoScheduleClasses = new ArrayList<String>();

        panel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        panel.add(new Label("AutoSchedule Classes"));
        autoScheduleBox = new ListBox(true);

        ScrollPanel autoSchedScroll = new ScrollPanel(autoScheduleBox);
        autoScheduleBox.setWidth("240");
        autoScheduleBox.setHeight("180");

        autoScheduleBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent e) {
                scheduleGrid.displaySection(
                        autoScheduleSections.get(
                            autoScheduleBox.getSelectedIndex()));
            }
        });

        panel.add(autoSchedScroll);

        Button scheduleButton = new Button("AutoSchedule");
        Button clearButton = new Button("Remove");

        clearButton.addClickHandler(new ClickHandler()  {
           public void onClick(ClickEvent f) {
              autoScheduleClasses.remove( autoScheduleBox.getSelectedIndex());
              autoScheduleBox.removeItem(autoScheduleBox.getSelectedIndex() );
           }
         });

        scheduleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e) {
                final AsyncCallback<SectionAdapter[]> autoScheduleCallback =
                        new AsyncCallback<SectionAdapter[]>() {

                            // On Success load the sections that are in the selected course
                            public void onSuccess(SectionAdapter[] sections) {
                                autoScheduleSections.clear();

                                for(SectionAdapter sa : sections) {
                                    autoScheduleSections.add(sa);
                                    autoScheduleBox.addItem(sa.getSectionNum());
                                }

                                scheduleGrid.addSections(autoScheduleSections);
                            }

                            // Display a warning on failure
                            public void onFailure(Throwable caught) {
                                Window.alert("Warning: Error with autoschedule.");
                            }
                        };

                scheduleGrid.clearSchedule();
                autoScheduleBox.clear();

                // Load the sections
                service.autoScheduleClasses(autoScheduleClasses, autoScheduleCallback);
            }
        });

        horpanel.add(scheduleButton);
        horpanel.add(clearButton);
        panel.add(horpanel);
        
        return panel;
    }

    private void loadSemesters() {

        // Callback function for loading the available semesters into the
        // semester box
        final AsyncCallback<String[]> getSemesterCallback = new AsyncCallback<String[]>() {

            // On success load all of the semesters into the semester box
            public void onSuccess(String[] result) {
			
                for (String s : result) {
                    semesterBox.addItem(s);
                }

                loadSemesterSubjects();
            }

            // On failure display a warning message
            public void onFailure(Throwable caught) {
                Window.alert("Warning: Error loading semesters.\n" + caught.getMessage());
            }
        };

        service.getAvailableSemesters(getSemesterCallback);
    }

    private void loadSemesterSubjects() {
        // Callback function for loading subjects into the tree
        final AsyncCallback<String[]> setSubjectsCallback = new AsyncCallback<String[]>() {

            // On Success load the subjects that are in the selected semester
            public void onSuccess(String[] subjects) {
                for (String subject : subjects) {
                    CourseTreeItem subjectTreeItem = new CourseTreeItem(TreeItemType.Subject, subject, "");
					
                    subjectTreeItem.addItem(""); // Blank item for expand arrow
                    courseTree.addItem(subjectTreeItem);
                }
            }

            // Display a warning on failure
            public void onFailure(Throwable caught) {
                Window.alert("Warning: Error loading subjects for selected semester.\n" + caught.getMessage());
            }
        };

        // Load the subjects from the database
        service.getAvailableSubjects(setSubjectsCallback);
    }
}
