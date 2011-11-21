package com.parsingUI;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

public class VelocityToolsView extends VelocityToolboxView  {

    private ToolContext toolContext;

    @Override
    protected Context createVelocityContext(Map model, HttpServletRequest request, HttpServletResponse response) {
    	try {
			toolContext = initVelocityToolContext();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block			
		}
        VelocityContext context = new VelocityContext(toolContext);
        if(model != null) {
            for(Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>)model.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        return context;
    }

    private ToolContext initVelocityToolContext() throws IllegalStateException, IOException {
    	  if (toolContext == null) {
    	    XmlFactoryConfiguration factoryConfiguration = new XmlFactoryConfiguration("Default Tools");
    	    factoryConfiguration.read(getServletContext().getResourceAsStream("getToolboxConfigLocation"));
    	    ToolboxFactory factory = factoryConfiguration.createFactory();
    	    factory.configure(factoryConfiguration);
    	    toolContext = new ToolContext();
    	    for (String scope : Scope.values()) {
    	      toolContext.addToolbox(factory.createToolbox(scope));
    	    }
    	  }
    	  return toolContext;
    	}
}