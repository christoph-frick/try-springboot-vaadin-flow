package app

import com.vaadin.router.Route
import com.vaadin.ui.Composite
import com.vaadin.ui.button.Button
import com.vaadin.ui.common.HtmlImport
import com.vaadin.ui.html.Div
import com.vaadin.ui.html.H1
import com.vaadin.ui.textfield.TextField
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.support.SpringBootServletInitializer

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        // further config...
        app.run(args)
    }
}

@Route('')
@Slf4j
@HtmlImport('frontend:///styles.html')
class MainLayout extends Composite<Div> {
    MainLayout() {
        H1 label
        TextField input
        content.add(
                label = new H1("Hello world"),
                input = new TextField().with {
                    addValueChangeListener {
                        label.text = "Hello ${it.value ?: "World"}"
                    }
                    it
                },
                new Button("Greet!", {
                    log.info "Hello ${input.value ?: "World"} to the server log"
                })
        )
    }
}

