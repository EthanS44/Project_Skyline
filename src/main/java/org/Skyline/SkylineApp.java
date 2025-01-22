
package org.Skyline;

import javafx.application.Application;
import javafx.stage.Stage;


public class SkylineApp extends Application {
    private StateContext context;

    @Override
    public void start(Stage primaryStage) {

        // Initialize StateContext with primaryStage
        context = new StateContext(primaryStage);

        // Set the initial state (LoginState)
        context.setState(new LoginState(context));


        //for testing purposes set state to this
        /*
        PackageParser packageParser = new PackageParser();
        Model model = packageParser.parsePackage("com.example.models");
        context.setSelectedModel(model);
        context.setState(new ModelViewState(context));

         */
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/*

package org.Skyline;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.Skyline")
@EnableJpaRepositories("org.Skyline")
@EntityScan("org.Skyline")
public class SkylineApp extends Application {


    @Autowired
    private StateContext context;  // Inject the StateContext

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Initialize Spring ApplicationContext before JavaFX application starts
        springContext = new SpringApplicationBuilder(SkylineApp.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        // Manually pass the primaryStage to the StateContext
        context.setPrimaryStage(primaryStage);  // Set Stage to the context

        // Set the initial state (LoginState)
        context.setState(new LoginState(context));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        springContext.close();  // Close Spring context when JavaFX app stops
    }

    public static void main(String[] args) {
        launch(args);
    }
}

 */