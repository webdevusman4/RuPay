package com.simplex.views;

import javafx.scene.Node;
import com.simplex.controllers.NavigationController;
import com.simplex.services.DataService;

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
