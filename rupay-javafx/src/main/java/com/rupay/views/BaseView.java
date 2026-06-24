package com.rupay.views;

import javafx.scene.Node;
import com.rupay.controllers.NavigationController;
import com.rupay.services.DataService;

public abstract class BaseView {
    protected NavigationController navController;
    protected DataService dataService;

    public BaseView(NavigationController navController) {
        this.navController = navController;
        this.dataService = DataService.getInstance();
    }

    public abstract Node getView();
    
    public void refresh(Object data) {
        // Override in subclasses if needed
    }
}
