package app

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.richtexteditor.RichTextEditor
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.timepicker.TimePicker
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import groovy.util.logging.Slf4j
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        new SpringApplication(Application).tap {
            // further config...
            bannerMode = Banner.Mode.OFF
            run(args)
        }
    }
}

@RestController
class HelloWorldController {
    @GetMapping("/hello")
    String index() {
        return "Hello World"
    }
}

@Route('')
@Slf4j
@Theme(Material)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Composite<VerticalLayout> {
    MainLayout() {
        content.add(
                new H1("V13 components"),
                *[new RichTextEditor(),
                  new LoginForm(),
                  new Accordion().tap {
                      (1..3).each {
                          add("Item $it", new H3("Some Item $it"))
                      }
                  },
                  new Details("Show details", new Div(new Span("This is the details"))),
                  new EmailField(),
                  new NumberField(),
                  new TimePicker(),
                ].collect {
                    new Div(
                            new H2(it.getClass().simpleName),
                            it
                    )
                }
        )
    }
}

