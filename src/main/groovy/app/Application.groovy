package app

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Synchronize
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

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
@Theme(Lumo)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Composite<Div> {
    MainLayout() {
        H1 label
        TextField input
        content.add(
                label = new H1(),
                input = new TextField().tap {
                    addValueChangeListener({
                        label.text = "Hello ${it.value ?: "World"}"
                    } as HasValue.ValueChangeListener)
                    valueChangeMode = ValueChangeMode.EAGER
                    value = "Flow"
                    focus()
                },
                new Button("Greet the server log", {
                    log.info "Hello ${input.value ?: "World"} to the server log"
                }),
        )
    }
}

@Tag('monaco-editor')
@HtmlImport('bower_components/monaco-editor/monaco-editor.html')
class MonacoEditor extends Component implements HasSize {

    MonacoEditor() {
        value = ''
    }

    @Synchronize('value-changed')
    String getValue() {
        element.getProperty('value')
    }

    void setValue(String value) {
        element.setProperty('value', value)
    }

}
